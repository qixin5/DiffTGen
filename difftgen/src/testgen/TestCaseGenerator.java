package testgen;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import util.*;


public class TestCaseGenerator
{
    private int trials;
    private int timeout;
    private boolean overfitting_break;
    private String evosuite_jar_path;
    private boolean run_evosuite;

    public TestCaseGenerator(int trials, int timeout, String evosuite_jar_path, boolean run_evosuite) {
	this.trials = trials;
	this.timeout = timeout;
	this.overfitting_break = false;
	this.evosuite_jar_path = evosuite_jar_path;
	this.run_evosuite = run_evosuite;
    }

    public TestCaseGenerator(int trials, int timeout, boolean overfitting_break, String evosuite_jar_path, boolean run_evosuite) {
	this.trials = trials;
	this.timeout = timeout;
	this.overfitting_break = overfitting_break;
	this.evosuite_jar_path = evosuite_jar_path;
	this.run_evosuite = run_evosuite;
    }
    
    public List<TestCase> generateTestCase(String bugid, TestTarget target, String proj_dpath, String script_dpath, String instru_class_name, boolean parallel) {
	if (parallel) {
	    return generateTestCaseInParallel(bugid, target, proj_dpath, script_dpath, instru_class_name);
	}
	else {
	    return generateTestCaseInSequence(bugid, target, proj_dpath, script_dpath, instru_class_name);
	}
    }

    private List<TestCase> generateTestCaseInSequence(String bugid, TestTarget target, String proj_dpath, String script_dpath, String instru_class_name) {

	TestCase overfit_tc = null; 
	TestCase repair_tc = null; 
	TestCase bothincorrect_tc = null; 
	int overfit_trialid = -1;
	int repair_trialid = -1;
	int bothincorrect_trialid = -1;	
	
	Timer timer = Global.timer;
	
	for (int i=0; i<trials; i++) {
	    String trialid = i + "";
	    TestClassGenerator tcgen = new TestClassGenerator(evosuite_jar_path, run_evosuite);
	    TestCaseGeneratorTrial tcgt = new TestCaseGeneratorTrial(bugid, trialid, target, proj_dpath, script_dpath, instru_class_name, timeout, overfitting_break, tcgen);
	    List<TestCase> tc_list = tcgt.call();

	    for (TestCase tc : tc_list) {
		if (tc == null) { continue; }
		if (tc.isOverfittingIndicative()) {
		    if (overfit_tc == null) {
			overfit_tc = tc;
			overfit_trialid = i;
		    }
		}
		else if (tc.isFixedIndicative()) {
		    if (repair_tc == null) {
			repair_tc = tc;
			repair_trialid = i;
		    }
		}
		else if (tc.isBothIncorrectIndicative()) {
		    if (bothincorrect_tc == null) {
			bothincorrect_tc = tc;
			bothincorrect_trialid = i;
		    }
		}
	    }

	    if (overfit_tc != null || bothincorrect_tc != null) {
		if (!timer.isReset()) { //timer is still running
		    timer.end();
		    System.out.println("Total execution time: " + timer.getDurationInMillis());
		    timer.reset();
		}
	    }
	    
	    if (overfitting_break && overfit_tc != null) {
		break;
	    }
	    if (overfit_tc != null && repair_tc != null && bothincorrect_tc != null) {
		break;
	    }
	}

	if (!timer.isReset()) {
	    timer.end();
	    System.out.println("Total execution time: " + timer.getDurationInMillis());
	    timer.reset();
	}
	
	if (overfit_tc != null) {
	    System.out.println("Overfitting-indicative Test Case Found!");
	    System.out.println("Contributed by Trial " + overfit_trialid);
	}
	if (repair_tc != null) {
	    System.out.println("Repair-indicative Test Case Found!");
	    System.out.println("Contributed by Trial " + repair_trialid);
	}
	if (bothincorrect_tc != null) {
	    System.out.println("Incorrect-indicative Test Case Found!");
	    System.out.println("Contributed by Trial " + bothincorrect_trialid);
	}

	if (overfit_tc==null && repair_tc==null && bothincorrect_tc==null) {
	    System.out.println("Found Nothing.");
	}

	List<TestCase> rslt_tc_list = new ArrayList<TestCase>();
	if (overfit_tc != null) { rslt_tc_list.add(overfit_tc); }
	if (repair_tc != null) { rslt_tc_list.add(repair_tc); }
	if (bothincorrect_tc != null) { rslt_tc_list.add(bothincorrect_tc); }
	return rslt_tc_list;
    }

    private List<TestCase> generateTestCaseInParallel(String bugid, TestTarget target, String proj_dpath, String script_dpath, String instru_class_name) {

	int pe = 8;
	TestCase overfit_tc = null;
	TestCase repair_tc = null;
	TestCase bothincorrect_tc = null;
	int overfit_trialid = -1;
	int repair_trialid = -1;
	int bothincorrect_trialid = -1;	
	
	Timer timer = Global.timer;

	for (int i=0; i<trials; i+=pe) {
	    ExecutorService exe_service = Executors.newFixedThreadPool(pe);
	    List<Callable<List<TestCase>>> call_list = new ArrayList<Callable<List<TestCase>>>();
	    int j_upper_bound = (i+pe < trials) ? (i+pe) : trials;
	    for (int j=i; j<j_upper_bound; j++) {
		String trialid = j + "";
		TestClassGenerator tcgen = new TestClassGenerator(evosuite_jar_path, run_evosuite);
		TestCaseGeneratorTrial tcgt = new TestCaseGeneratorTrial(bugid, trialid, target, proj_dpath, script_dpath, instru_class_name, timeout, overfitting_break, tcgen);
		call_list.add(tcgt);
	    }
	    List<Future<List<TestCase>>> tc_future_list = null;
	    try { tc_future_list = exe_service.invokeAll(call_list); }
	    catch (Throwable t) {
		System.err.println("Test Case Generator Trial Error.");
		System.err.println(t);
		t.printStackTrace();
		exe_service.shutdownNow();
	    }
	    if (!exe_service.isShutdown()) {
		exe_service.shutdown();
	    }
	    
	    if (tc_future_list == null) { continue; }
	    int tc_future_list_size = tc_future_list.size();
	    for (int k=0; k<tc_future_list_size; k++) {
		Future<List<TestCase>> tc_future = tc_future_list.get(k);
		int trialid = k+i;
		if (tc_future == null) { continue; }
		List<TestCase> tc_list = null;
		try { tc_list = tc_future.get(); }
		catch (Throwable t) {
		    System.err.println("Failed getting a test case from a future.");
		    System.err.println(t);
		    t.printStackTrace();
		}
		if (tc_list == null) { continue; }
		
		for (TestCase tc : tc_list) {
		    if (tc == null) { continue; }
		    if (tc.isOverfittingIndicative()) {
			if (overfit_tc == null) {
			    overfit_tc = tc;
			    overfit_trialid = trialid;
			}
		    }
		    else if (tc.isFixedIndicative()) {
			if (repair_tc == null) {
			    repair_tc = tc;
			    repair_trialid = trialid;
			}
		    }
		    else if (tc.isBothIncorrectIndicative()) {
			if (bothincorrect_tc == null) {
			    bothincorrect_tc = tc;
			    bothincorrect_trialid = trialid;
			}
		    }
		}
	    }

	    if (overfit_tc != null || bothincorrect_tc != null) {
		if (!timer.isReset()) {
		    timer.end();
		    System.out.println("Total execution time: " + timer.getDurationInMillis());
		    timer.reset();
		}
	    }
	    if (overfitting_break && overfit_tc != null) {
		break;
	    }
	    if (overfit_tc != null && repair_tc != null && bothincorrect_tc != null) {
		break;
	    }
	}

	if (overfit_tc != null) {
	    System.out.println("Overfitting-indicative Test Case Found!");
	    System.out.println("Contributed by Trial " + overfit_trialid);
	}
	if (repair_tc != null) {
	    System.out.println("Repair-indicative Test Case Found!");
	    System.out.println("Contributed by Trial " + repair_trialid);
	}
	if (bothincorrect_tc != null) {
	    System.out.println("Incorrect-indicative Test Case Found!");
	    System.out.println("Contributed by Trial " + bothincorrect_trialid);
	}

	if (overfit_tc==null && repair_tc==null && bothincorrect_tc==null) {
	    System.out.println("Found Nothing.");
	}

	if (!timer.isReset()) {
	    timer.end();
	    System.out.println("Total execution time: " + timer.getDurationInMillis());
	    timer.reset();
	}
	
	List<TestCase> rslt_tc_list = new ArrayList<TestCase>();
	if (overfit_tc != null) { rslt_tc_list.add(overfit_tc); }
	if (repair_tc != null) { rslt_tc_list.add(repair_tc); }
	if (bothincorrect_tc != null) { rslt_tc_list.add(bothincorrect_tc); }
	return rslt_tc_list;
    }
}

