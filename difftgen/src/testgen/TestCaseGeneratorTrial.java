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


public class TestCaseGeneratorTrial implements Callable<List<TestCase>>
{
    String bugid, trialid, proj_dpath, script_dpath, instru_class_name;
    TestTarget target;
    int timeout;    
    TestClassGenerator tcgen;
    boolean overfitting_break;


    public TestCaseGeneratorTrial(String bugid, String trialid, TestTarget target, String proj_dpath, String script_dpath, String instru_class_name, int timeout, TestClassGenerator tcgen) {
	this.bugid = bugid;
	this.trialid = trialid;
	this.proj_dpath = proj_dpath;
	this.script_dpath = script_dpath;
	this.instru_class_name = instru_class_name;
	this.target = target;
	this.timeout = timeout;
	this.tcgen = tcgen;
	overfitting_break = true;
    }

    public TestCaseGeneratorTrial(String bugid, String trialid, TestTarget target, String proj_dpath, String script_dpath, String instru_class_name, int timeout, boolean overfitting_break, TestClassGenerator tcgen) {
	this.bugid = bugid;
	this.trialid = trialid;
	this.proj_dpath = proj_dpath;
	this.script_dpath = script_dpath;
	this.instru_class_name = instru_class_name;
	this.target = target;
	this.timeout = timeout;
	this.tcgen = tcgen;
	this.overfitting_break = overfitting_break;
    }
    
    

    @Override public List<TestCase> call() {

	System.err.println("*** Running Trial " + trialid + " ***");
	System.err.println();

	List<TestCase> tc_list = new ArrayList<TestCase>();
	
	String rslt_tc_ctnt = null;
	String target_dpath = proj_dpath + "/target";
	boolean overfitting_found = false;
	boolean repair_found = false;
	boolean incorrect_found = false;
	
	//Generate a test class covering the goals
	TestClass tclass = tcgen.generateTestClass(bugid, trialid, target, target_dpath, "TestClass", instru_class_name, timeout);
	if (tclass == null) {
	    System.err.println("Trial "+trialid+": Failed Generating a Test Class.");
	    System.err.println("*** Trial " + trialid + " finished ***");
	    return tc_list;
	}
	if (tclass.isChangeExercised()) {
	    System.err.println("Trial "+trialid+": Change Exercised!");
	}
	else {
	    System.err.println("Trial "+trialid+": No Change Exercised!");
	    System.err.println("*** Trial " + trialid + " finished ***");
	    return tc_list;
	}

	String test_class_package_name = tclass.getPackageName();
	String test_class_name = tclass.getClassName();
	String test_class_full_name = test_class_package_name+"."+test_class_name;
	String test_class_ctnt = tclass.getClassContent();

	//Write the test class to file
	String test_class_dpath = proj_dpath + "/test/" + trialid;
	File test_class_dir = new File(test_class_dpath);
	if (!test_class_dir.exists()) { test_class_dir.mkdirs(); }
	String test_class_fpath = test_class_dpath+"/"+test_class_name+".java";
	File test_class_f = new File(test_class_fpath);
	try {
	    FileUtils.writeStringToFile(test_class_f, test_class_ctnt, (String) null);
	} catch (Throwable t) {
	    System.err.println(t);
	    t.printStackTrace();
	}

	//Compile the test class
	String[] compile_cmds = new String[] {
	    "ant", "-f", script_dpath+"/build.xml",
	    "-Dbug_id="+bugid, "-Dtest_proj_dir="+proj_dpath,
	    "-Dtestclass_dir="+test_class_dpath,
	    "compile-testclass"
	};
	int compile_exit_val = CommandExecutor.execute(compile_cmds, new File(test_class_dpath));
	if (compile_exit_val != 0) {
	    System.err.println("Trial "+trialid+": Test Class Compilation Failure.");
	    for (String compile_cmd : compile_cmds) {
		System.err.print(compile_cmd + " ");
	    }
	    System.err.println();
	    System.err.println("*** Trial " + trialid + " finished ***");
	    return tc_list;
	}

	System.err.println("Trial "+trialid+ " finish generating & compiling test class.");
	
	//Test fp, pp & cp against the test class
	String fp_rslt_path = proj_dpath + "/bug/test/"+trialid+"/rslt";
	String fp_rslt_dpath = proj_dpath + "/bug/test/"+trialid;
	String pp_rslt_path = proj_dpath + "/patch/test/"+trialid+"/rslt";
	String pp_rslt_dpath = proj_dpath + "/patch/test/"+trialid;
	String cp_rslt_path = proj_dpath + "/fix/test/"+trialid+"/rslt";
	String cp_rslt_dpath = proj_dpath + "/fix/test/"+trialid;

	File fp_rslt_dir = new File(fp_rslt_dpath);
	File pp_rslt_dir = new File(pp_rslt_dpath);
	File cp_rslt_dir = new File(cp_rslt_dpath);
	if (!fp_rslt_dir.exists()) { fp_rslt_dir.mkdir(); }
	if (!pp_rslt_dir.exists()) { pp_rslt_dir.mkdir(); }
	if (!cp_rslt_dir.exists()) { cp_rslt_dir.mkdir(); }
	
	String[] test_fp_cmds = new String[] {
	    "ant", "-f", script_dpath+"/build.xml",
	    "-Dbug_id="+bugid,
	    "-Dtest_proj_dir="+proj_dpath,
	    "-Dtestclass_dir="+test_class_dpath,
	    "-Dtest_class_full_name="+test_class_full_name,
	    "run-bug-test"
	};

	int test_fp_exit_val = CommandExecutor.execute(test_fp_cmds, fp_rslt_dir, new File(fp_rslt_path));

	String[] test_pp_cmds = new String[] {
	    "ant", "-f", script_dpath+"/build.xml",
	    "-Dbug_id="+bugid,
	    "-Dtest_proj_dir="+proj_dpath,
	    "-Dtestclass_dir="+test_class_dpath,
	    "-Dtest_class_full_name="+test_class_full_name,
	    "run-patch-test"
	};
	
	int test_pp_exit_val = CommandExecutor.execute(test_pp_cmds, pp_rslt_dir, new File(pp_rslt_path));
	
	String[] test_cp_cmds = new String[] {
	    "ant", "-f", script_dpath+"/build.xml",
	    "-Dbug_id="+bugid,
	    "-Dtest_proj_dir="+proj_dpath,
	    "-Dtestclass_dir="+test_class_dpath,
	    "-Dtest_class_full_name="+test_class_full_name,
	    "run-fix-test"
	};

	System.err.println("Trial "+trialid+" finish running bug & patch against the test method.");
	
	//=====================
	/*
	System.err.println("\n<TEST CP CMDS>");
	for (String test_cp_cmd : test_cp_cmds) {
	    System.err.print(test_cp_cmd + " ");
	}
	System.err.println("\n");
	*/
	//=====================
	
	int test_cp_exit_val = OracleRunner.writeResultWithDeprecatedValues(cp_rslt_dpath, test_cp_cmds);
	if (test_cp_exit_val != 0) {
	    System.err.println("Oracle Result is NOT available.");
	    System.err.println("*** Trial " + trialid + " finished ***");
	    return tc_list;
	}

	System.err.println("Trial "+trialid+" finish writing oracle results.");
	
	File fp_rslt_f = new File(fp_rslt_path);
	File pp_rslt_f = new File(pp_rslt_path);
	File cp_rslt_f = new File(cp_rslt_path);
	List<TestResult> fp_tr_list = readResultFile(fp_rslt_f);
	List<TestResult> pp_tr_list = readResultFile(pp_rslt_f);
	List<TestResult> cp_tr_list = readResultFile(cp_rslt_f);
	int fp_tr_list_size = fp_tr_list.size();
	int pp_tr_list_size = pp_tr_list.size();
	int cp_tr_list_size = cp_tr_list.size();

	int tr_size = (fp_tr_list_size <= pp_tr_list_size) ? fp_tr_list_size : pp_tr_list_size;
	boolean diff_semantics_found = false;
	ExpectedItem overfit_ei = null;
	ExpectedItem repair_ei = null;
	ExpectedItem incorrect_ei = null;
	
	for (int j=0; j<tr_size; j++) {
	    TestResult fp_tr = fp_tr_list.get(j);
	    TestResult pp_tr = pp_tr_list.get(j);
	    String fp_tr_mname = fp_tr.getMethodName();
	    String pp_tr_mname = pp_tr.getMethodName();
	    if (fp_tr_mname.equals(pp_tr_mname)) {
		String fp_tr_ctnt = fp_tr.getResultContent().trim();
		String pp_tr_ctnt = pp_tr.getResultContent().trim();
		System.err.println("Trial "+trialid+" Checking Running Results from Test Method: "+fp_tr_mname);

		if (!fp_tr_ctnt.equals(pp_tr_ctnt)) {
		    diff_semantics_found = true;
		    System.err.println("Trial "+trialid+": Semantic Difference Found!");
		    TestResult cp_tr = cp_tr_list.get(j);
		    String cp_tr_mname = cp_tr.getMethodName();
		    if (fp_tr_mname.equals(cp_tr_mname)) {
			String cp_tr_ctnt = cp_tr.getResultContent().trim();
			ExpectedItem ei0 = getExpectedItem(fp_tr_mname, fp_tr_ctnt, pp_tr_ctnt, cp_tr_ctnt);
			if (ei0 == null) { //Give up this test method
			    continue;
			} 

			int ei0_prop = ei0.getProperty();
			if (ei0_prop == 0) {
			    overfitting_found = true;
			    if (overfit_ei == null) { overfit_ei = ei0; }
			    System.err.println("Trial "+trialid+": Overfitting Test Found!");
			    if (overfitting_break) { break; }
			}
			else if (ei0_prop == 1) {
			    repair_found = true;
			    if (repair_ei == null) { repair_ei = ei0; }
			    System.err.println("Trial "+trialid+": Repair-revealing Test Found!");
			}
			else if (ei0_prop == 2) {
			    incorrect_found = true;
			    if (incorrect_ei == null) { incorrect_ei = ei0; }
			    System.err.println("Trial "+trialid+": Both-incorrect Test Found!");
			}
		    }
		    else {
			System.err.println("Trial "+trialid+": Inconsistent Test Methods between FP & CP");
		    }
		}
		else {
		    System.err.println("Trial "+trialid+": Identical Running Results.");
		}
	    }
	    else {
		System.err.println("Trial "+trialid+": Inconsistent Test Methods between FP & PP!");
	    }

	    if (overfit_ei!=null && repair_ei!=null && incorrect_ei!=null) {
		break;
	    }
	}

	System.err.println("Trial "+trialid+" finish Checking Running Results.");
	
	if (!diff_semantics_found) {
	    System.err.println("Trial "+trialid+": No Different Semantics Found.");
	    System.err.println("*** Trial " + trialid + " finished ***");
	    return tc_list;
	}
	else {
	    TestCaseGenerator0 tcgen0 = new TestCaseGenerator0();
	    if (overfit_ei != null) {
		rslt_tc_ctnt = tcgen0.createTestCaseClass(tclass, overfit_ei);
		String tc_full_name = test_class_package_name+".DiffTGen"+overfit_ei.getProperty()+"Test";
		tc_list.add(new TestCase(overfit_ei.getProperty(), tc_full_name, rslt_tc_ctnt));
	    }
	    if (repair_ei != null) {
		rslt_tc_ctnt = tcgen0.createTestCaseClass(tclass, repair_ei);
		String tc_full_name = test_class_package_name+".DiffTGen"+repair_ei.getProperty()+"Test";
		tc_list.add(new TestCase(repair_ei.getProperty(), tc_full_name, rslt_tc_ctnt));		
	    }
	    if (incorrect_ei != null) {
		rslt_tc_ctnt = tcgen0.createTestCaseClass(tclass, incorrect_ei);
		String tc_full_name = test_class_package_name+".DiffTGen"+incorrect_ei.getProperty()+"Test";
		tc_list.add(new TestCase(incorrect_ei.getProperty(), tc_full_name, rslt_tc_ctnt));
	    }
	    
	    if (overfit_ei==null && repair_ei==null && incorrect_ei==null) {
		System.err.println("Trial "+trialid+" Failed to produce a test case.");
	    }

	    System.err.println("*** Trial " + trialid + " finished ***");
	    return tc_list;
	}	
    }

    private List<TestResult> readResultFile(File rslt_f) {

	List<TestResult> tr_list = new ArrayList<TestResult>();
	
	List<String> rslt_lines = null;
	try { rslt_lines = FileUtils.readLines(rslt_f, (String) null); }
	catch (Throwable t) {
	    System.err.println(t);
	    t.printStackTrace();
	}
	if (rslt_lines == null) { return tr_list; }

	int start=-1, end=-1;
	int size = rslt_lines.size();
	for (int i=0; i<size; i++) {
	    String rslt_line = rslt_lines.get(i).trim();
	    if ("run-bug-test:".equals(rslt_line) ||
		"run-patch-test:".equals(rslt_line) ||
		"run-fix-test:".equals(rslt_line)) {
		start = i+1;
		break;
	    }
	}
	for (int j=size-1; j>=0; j--) {
	    String rslt_line = rslt_lines.get(j).trim();
	    if ("BUILD SUCCESSFUL".equals(rslt_line) ||
		"BUILD FAILED".equals(rslt_line)) {
		end = j-1;
		break;
	    }
	}
	if (start==-1 || end==-1) { return tr_list; }


	String curr_tm_name = null;
	StringBuilder curr_output_sb = new StringBuilder();
	for (int i=start; i<=end; i++) {
	    //=============
	    //System.err.println("^^^ rslt_lines.get("+i+") ^^^");
	    //System.err.println(rslt_lines.get(i).trim());
	    //=============	    
	    String rslt_line = rslt_lines.get(i).trim();
	    if (rslt_line.startsWith("[java] ")) {
		rslt_line = rslt_line.substring(7); //rm [java]
	    } 
	    else {
		rslt_line = "";
	    }
	    
	    if (rslt_line.startsWith("Test Method:")) {
		curr_tm_name = rslt_line.substring(13);
	    }
	    else if (rslt_line.equals("<ssssss>")) {
		TestResult tr = new TestResult(curr_tm_name, curr_output_sb.toString().trim());
		tr_list.add(tr);
		curr_tm_name = null;
		curr_output_sb = new StringBuilder();
	    }
	    else {
		curr_output_sb.append(rslt_line);
		curr_output_sb.append("\n");
	    }
	}
	if (curr_tm_name != null) {
	    TestResult tr = new TestResult(curr_tm_name, curr_output_sb.toString().trim());
	    tr_list.add(tr);
	}
	
	return tr_list;
    }


    private ExpectedItem getExpectedItem(String tm_name, String fp_ctnt, String pp_ctnt, String cp_ctnt) {

	String[] fp_lines = fp_ctnt.split("\n");
	String[] pp_lines = pp_ctnt.split("\n");
	String[] cp_lines = cp_ctnt.split("\n");
	int fp_lines_size = fp_lines.length;
	int pp_lines_size = pp_lines.length;
	int cp_lines_size = cp_lines.length;

	boolean determined_value_changed = false;
	for (int i=0; i<cp_lines_size; i++) {
	    String cp_line = cp_lines[i];
	    if (cp_line.startsWith("VALUE:")) {
		boolean comparable0 = false;
		if (i<fp_lines_size && i<pp_lines_size) {
		    String fp_line = fp_lines[i];
		    String pp_line = pp_lines[i];
		    if (fp_line.startsWith("VALUE:") && pp_line.startsWith("VALUE:")) {
			comparable0 = true;
		    }
		    else {
			System.err.println("Either FP Line or PP Line is NOT a VALUE Line.");
			System.err.println("CP Line (#"+i+"): " + cp_line);
			System.err.println("FP Line (#"+i+"): " + fp_line);
			System.err.println("PP Line (#"+i+"): " + pp_line);
		    }
		}
		else {
		    System.err.println("Either FP Line (#"+i+") or PP Line (#"+i+") is NOT available.");
		}
		if (!comparable0) { //Give up producing an expected item
		    return null;
		} 

		boolean comparable1 = false;
		String cp_prim_loc = cp_lines[i-2];
		String fp_prim_loc =
		    ((0<=i-2) && (i-2<fp_lines_size)) ? fp_lines[i-2] : "";
		String pp_prim_loc =
		    ((0<=i-2) && (i-2<pp_lines_size)) ? pp_lines[i-2] : "";
		if (cp_prim_loc.equals(fp_prim_loc) &&
		    cp_prim_loc.equals(pp_prim_loc)) {
		    comparable1 = true;
		}
		if (!comparable1) { //Give up producing an expected item
		    System.err.println("Non-comparable VALUE Lines.");
		    System.err.println("CP Line (#"+i+"): " + cp_line);
		    System.err.println("CP PRIM_LOC Line (#"+(i-2)+"): " + cp_prim_loc);
		    System.err.println("FP PRIM_LOC Line (#"+(i-2)+"): " + fp_prim_loc);
		    System.err.println("PP PRIM_LOC Line (#"+(i-2)+"): " + pp_prim_loc);
		    return null;
		} 
		
		String fp_line = fp_lines[i];
		String pp_line = pp_lines[i];
		boolean cp_equals_fp = cp_line.equals(fp_line);
		boolean cp_equals_pp = cp_line.equals(pp_line);
		if (cp_equals_fp && cp_equals_pp) {
		    continue; //Keep looking for other comparable values
		}

		determined_value_changed = true;
		String asserted_prim_loc =
		    cp_prim_loc.substring(new String("PRIM_LOC:").length());
		String expected_type =
		    cp_lines[i-1].substring(new String("TYPE:").length());
		String expected_value =
		    cp_lines[i].substring(new String("VALUE:").length());

		ExpectedItem ei = new ExpectedItem(tm_name, asserted_prim_loc, expected_type, expected_value, "Throwable".equals(expected_type));

		if (cp_equals_fp && !cp_equals_pp) {
		    ei.setProperty(0); //Overfitting
		}
		else if (!cp_equals_fp && cp_equals_pp) {
		    ei.setProperty(1); //Repair
		}
		else if (!cp_equals_fp && !cp_equals_pp) {
		    ei.setProperty(2); //Both-incorrect
		}

		return ei;
	    }
	}

	if (!determined_value_changed) {
	    System.err.println("No Determined Value Changed For " + tm_name + "!");
	}

	return null;
    }
    
}
