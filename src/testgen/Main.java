package testgen;

import util.*;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;


public class Main
{
    private static Options options;

    static {
	options = new Options();
	options.addOption("bugid", true, "Bug ID");
	options.addOption("repairtool", true, "Repair Tool");
	options.addOption("difftgendpath", true, "DiffTGen directory");
	options.addOption("scriptrootdpath", true, "Script root directory");
	options.addOption("d4jprojdpath", true, "Defects4J project directory");
	options.addOption("evosuitejpath", true, "EvoSuite jar file path");
	options.addOption("dependjpath", true, "Dependency jar file path");
	options.addOption("outputdpath", true, "Output Directory");
	options.addOption("inputfpath", true, "Input File Containing Syntactic Deltas");
	options.addOption("oracleinputfpath", true, "Oracle Input File Containing Oracle Methods to be Instrumented");
	options.addOption("evosuitetrials", true, "# of EvoSuite Trials");
	options.addOption("evosuitetimeout", true, "EvoSuite Running Timeout in Seconds");
	options.addOption("forcecompile", false, "Force to compile all files");
	options.addOption("runparallel", false, "Run in parallel");
	options.addOption("simpletarget", false, "Use simple target");
	options.addOption("stopifoverfittingfound", false, "Stop running when an overfitting-indicative test case is found");
	options.addOption("runevosuite", false, "Run EvoSuite to generate test inputs");
    }

    public static void main(String[] args) {

	CommandLineParser clparser = new DefaultParser();
	CommandLine cmd_line = null;
	try { cmd_line = clparser.parse(options, args); }
	catch (ParseException exp) {
	    System.err.println("CommandLine Parsing Failed: " + exp);
	}
	if (cmd_line == null) { return; }

	String bugid = null;
	String repairtool = null;
	String dependjpath = null;
	String inputfpath = null;
	String outputdpath = null;
	int trials = -1, timeout = -1;
	String oracleinputfpath = null;
	
	if (cmd_line.hasOption("bugid")) {
	    String value = cmd_line.getOptionValue("bugid");
	    System.out.println("Bug ID: " + value);
	    Global.bugid = value;
	    bugid = value;
	}
	else {
	    System.out.println("Bug ID: " + Global.bugid);
	}

	if (cmd_line.hasOption("repairtool")) {
	    String value = cmd_line.getOptionValue("repairtool");
	    System.out.println("Repair Tool: " + value);
	    Global.repairtool = value;
	    repairtool = value;
	}
	else {
	    System.out.println("Repair Tool: " + Global.repairtool);
	}

	if (cmd_line.hasOption("difftgendpath")) {
	    String value = cmd_line.getOptionValue("difftgendpath");
	    System.out.println("DiffTGen Dir: " + value);
	    Global.difftgendpath = value;
	}
	else {
	    System.out.println("DiffTGen Dir: " + Global.difftgendpath);
	}
	
	if (cmd_line.hasOption("evosuitejpath")) {
	    String value = cmd_line.getOptionValue("evosuitejpath");
	    System.out.println("EvoSuite Jar File: " + value);
	    Global.evosuitejpath = value;
	}
	else {
	    System.out.println("EvoSuite Jar File: " + Global.evosuitejpath);
	}

	if (cmd_line.hasOption("dependjpath")) {
	    String value = cmd_line.getOptionValue("dependjpath");
	    System.out.println("Dependency Jar File: " + value);
	    dependjpath = value;
	    Global.dependjpath = value;
	}
	else {
	    System.out.println("Dependency Jar File: " + Global.dependjpath);
	}
	
	if (cmd_line.hasOption("outputdpath")) {
	    String value = cmd_line.getOptionValue("outputdpath");
	    System.out.println("Output Directory: " + value);
	    Global.outputdpath = value;
	    outputdpath = value;
	}
	else {
	    System.out.println("Output Directory: " + Global.outputdpath);
	}

	if (cmd_line.hasOption("inputfpath")) {
	    String value = cmd_line.getOptionValue("inputfpath");
	    System.out.println("Input File: " + value);
	    Global.inputfpath = value;
	    inputfpath = value;
	}
	else {
	    System.out.println("Input File: " + Global.inputfpath);
	}

	if (cmd_line.hasOption("oracleinputfpath")) {
	    String value = cmd_line.getOptionValue("oracleinputfpath");
	    System.out.println("Oracle Input File: " + value);
	    Global.oracleinputfpath = value;
	    oracleinputfpath = value;
	}
	else {
	    System.out.println("Oracle Input File: " + Global.oracleinputfpath);
	}
	
	if (cmd_line.hasOption("evosuitetrials")) {
	    String value = cmd_line.getOptionValue("evosuitetrials");
	    System.out.println("EvoSuite Trials: " + value);
	    trials = Integer.parseInt(value);
	    Global.evosuitetrials = trials;
	}
	else {
	    trials = Global.evosuitetrials;
	    System.out.println("EvoSuite Trials: " + trials);
	}

	if (cmd_line.hasOption("evosuitetimeout")) {
	    String value = cmd_line.getOptionValue("evosuitetimeout");
	    System.out.println("Evosuite Timeout (in sec.): " + value);
	    timeout = Integer.parseInt(value);
	    Global.evosuitetimeout = timeout;
	}
	else {
	    timeout = Global.evosuitetimeout;
	    System.out.println("Evosuite Timeout (in sec.): " + timeout);
	}
	
	if (cmd_line.hasOption("forcecompile")) {
	    System.out.println("Force to Compile Files: " + true);
	    Global.forcecompile = true;
	}
	else {
	    System.out.println("Force to Compile Files: " + Global.forcecompile);
	}

	if (cmd_line.hasOption("runparallel")) {
	    System.out.println("Run Parallel: " + true);
	    Global.runparallel = true;
	}
	else {
	    System.out.println("Run Parallel: " + Global.runparallel);
	}

	if (cmd_line.hasOption("simpletarget")) {
	    System.out.println("Simple Target: " + true);
	    Global.simpletarget = true;
	}
	else {
	    System.out.println("Use Simple Target: " + Global.simpletarget);
	}

	if (cmd_line.hasOption("stopifoverfittingfound")) {
	    System.out.println("Stop if Overfitting Patches Found: " + true);
	    Global.stopifoverfittingfound = true;
	}
	else {
	    System.out.println("Stop if Overfitting Patches Found: " + Global.stopifoverfittingfound);
	}

	if (cmd_line.hasOption("runevosuite")) {
	    System.out.println("Run EvoSuite: " + true);
	    Global.runevosuite = true;
	}
	else {
	    System.out.println("Run EvoSuite: " + Global.runevosuite);
	}

	if (bugid == null) {
	    System.err.println("Bug ID is Null.");
	    return;
	}
	if (repairtool == null) {
	    System.err.println("Repair Tool is Null.");
	    return;
	}
	if (dependjpath == null) {
	    System.err.println("Dependency Jar File Path is Null.");
	    return;
	}
	if (inputfpath == null) {
	    System.err.println("Input File Path is Null.");
	    return;
	}
	if (outputdpath == null) {
	    System.err.println("Output Directory Path is Null.");
	    return;
	}
	if (oracleinputfpath == null) {
	    System.err.println("Oracle Input File Path is Null.");
	    return;
	}

	
	List<String> input_lines0 = null;
	List<String> input_lines1 = null;
	try { input_lines0 = FileUtils.readLines(new File(inputfpath), (String)null); }
	catch (Throwable t) {
	    System.err.println(t);
	    t.printStackTrace();
	}
	if (input_lines0 == null) { return; }
	try { input_lines1 = FileUtils.readLines(new File(oracleinputfpath), (String)null); }
	catch (Throwable t) {
	    System.err.println(t);
	    t.printStackTrace();
	}
	if (input_lines1 == null) { return; }


	List<Modification> mod_list = SynDeltaParser.parse(input_lines0);
	List<MethodToBeInstrumented> oracle_med_instru_list = OracleParser.parse(input_lines1);
	if (mod_list != null && !mod_list.isEmpty() &&
	    oracle_med_instru_list != null && !oracle_med_instru_list.isEmpty()) {
	    Main m = new Main();
	    m.testgen(bugid, repairtool, mod_list, oracle_med_instru_list, trials, timeout, outputdpath);
	}
    }

    private boolean init(String bugid, String repair_tool, List<Modification> mod_list, List<MethodToBeInstrumented> oracle_med_instru_list, int trials, int timeout, String output_root_dpath) {

	String testid = bugid + "_" + repair_tool.toLowerCase();
	
	//Set up the output directory
	File output_root_dir = new File(output_root_dpath);
	if (!output_root_dir.exists()) { output_root_dir.mkdir(); }
	String output_dpath = output_root_dpath + "/" + testid;
	File output_dir = new File(output_dpath);
	if (!output_dir.exists()) { output_dir.mkdir(); }
	File output_bug_dir = new File(output_dpath+"/bug");
	File output_patch_dir = new File(output_dpath+"/patch");
	File output_fix_dir = new File(output_dpath+"/fix");
	File output_target_dir = new File(output_dpath+"/target");
	File output_test_dir = new File(output_dpath+"/test");
	File output_testcase_dir = new File(output_dpath+"/testcase");
	if (!output_bug_dir.exists()) { output_bug_dir.mkdir(); }
	if (!output_patch_dir.exists()) { output_patch_dir.mkdir(); }
	if (!output_fix_dir.exists()) { output_fix_dir.mkdir(); }
	if (!output_target_dir.exists()) { output_target_dir.mkdir(); }
	if (!output_test_dir.exists()) { output_test_dir.mkdir(); }
	if (!output_testcase_dir.exists()) { output_testcase_dir.mkdir(); }

	//Copy change-related files to bug, patch & fix directories
	Set<String> copied_set = new HashSet<String>();
	for (Modification mod : mod_list) {
	    String fppath = mod.getFPPath();
	    if (fppath == null) { fppath = mod.getInsertDummyPath(); }
	    if (fppath == null) { continue; }
	    if (!copied_set.contains(fppath)) { //Don't copy twice
		try {
		    FileUtils.copyFileToDirectory(new File(fppath), output_bug_dir);
		    copied_set.add(fppath);
		} catch (Throwable t) {
		    System.err.println("Failed copying the file: " + fppath);
		    t.printStackTrace();
		    System.err.println(t);
		    return false;
		}
	    }

	    String pppath = mod.getPPPath();
	    if (pppath == null) { pppath = mod.getDelDummyPath(); }
	    if (pppath == null) { continue; }
	    if (!copied_set.contains(pppath)) { //Don't copy twice
		try {
		    FileUtils.copyFileToDirectory(new File(pppath), output_patch_dir);
		    copied_set.add(pppath);
		} catch (Throwable t) {
		    System.err.println("Failed copying the file: " + pppath);
		    t.printStackTrace();
		    System.err.println(t);
		    return false;
		}
	    }
	}

	for (MethodToBeInstrumented oracle_med_instru : oracle_med_instru_list) {
	    String cppath = oracle_med_instru.getFilePath();
	    if (cppath == null) { continue; }
	    if (!copied_set.contains(cppath)) {
		try {
		    FileUtils.copyFileToDirectory(new File(cppath), output_fix_dir);
		    copied_set.add(cppath);
		}
		catch (Throwable t) {
		    System.err.println("Failed copying the file: " + cppath);
		    t.printStackTrace();
		    System.err.println(t);
		    return false;
		}
	    }
	}

	return true;
    }

    public boolean createInstrumentedFiles(String bugid, String repair_tool, List<Modification> mod_list, List<MethodToBeInstrumented> oracle_med_instru_list, int trials, int timeout, String output_root_dpath) {

	String testid = bugid + "_" + repair_tool.toLowerCase();
	String proj_dpath = output_root_dpath + "/" + testid;

	String fp_instru0_dpath = proj_dpath + "/bug/instru0";
	String pp_instru0_dpath = proj_dpath + "/patch/instru0";
	String cp_instru0_dpath = proj_dpath + "/fix/instru0";
	File fp_instru0_dir = new File(fp_instru0_dpath);
	File pp_instru0_dir = new File(pp_instru0_dpath);
	File cp_instru0_dir = new File(cp_instru0_dpath);
	if (!fp_instru0_dir.exists()) { fp_instru0_dir.mkdir(); }
	if (!pp_instru0_dir.exists()) { pp_instru0_dir.mkdir(); }
	if (!cp_instru0_dir.exists()) { cp_instru0_dir.mkdir(); }
	String fp_instru1_dpath = proj_dpath + "/bug/instru1";
	String pp_instru1_dpath = proj_dpath + "/patch/instru1";
	String cp_instru1_dpath = proj_dpath + "/fix/instru1";
	File fp_instru1_dir = new File(fp_instru1_dpath);
	File pp_instru1_dir = new File(pp_instru1_dpath);
	File cp_instru1_dir = new File(cp_instru1_dpath);
	if (!fp_instru1_dir.exists()) { fp_instru1_dir.mkdir(); }
	if (!pp_instru1_dir.exists()) { pp_instru1_dir.mkdir(); }
	if (!cp_instru1_dir.exists()) { cp_instru1_dir.mkdir(); }

	//Group the modifications by files
	Map<String, List<String>> mod_map_fp = new HashMap<String, List<String>>();
	Map<String, List<String>> mod_map_pp = new HashMap<String, List<String>>();
	Map<String, List<String>> mod_map_cp = new HashMap<String, List<String>>();
	int mod_list_size = mod_list.size();
	int oracle_med_instru_list_size = oracle_med_instru_list.size();
	for (int i=0; i<mod_list_size; i++) {
	    Modification mod = mod_list.get(i);
	    String fppath = mod.getFPPath();
	    String fploc = mod.getFPLoc();
	    if (fppath == null) { fppath = mod.getInsertDummyPath(); }
	    if (fploc == null) { fploc = mod.getInsertDummyCtxtLoc(); }
	    String fpmloc = ASTHelper.getMethodLoc(fppath, fploc);
	    //===============
	    //System.err.println("fpmloc in Main: " + fpmloc);
	    //===============
	    List<String> fpmlocs = mod_map_fp.get(fppath);
	    if (fpmlocs == null) {
		fpmlocs = new ArrayList<String>();
		mod_map_fp.put(fppath, fpmlocs);
	    }
	    if (!fpmlocs.contains(fpmloc)) { fpmlocs.add(fpmloc); }

	    String pppath = mod.getPPPath();
	    String pploc = mod.getPPLoc();
	    if (pppath == null) { pppath = mod.getDelDummyPath(); }
	    if (pploc == null) { pploc = mod.getDelDummyCtxtLoc(); }
	    String ppmloc = ASTHelper.getMethodLoc(pppath, pploc);
	    //===============
	    //System.err.println("ppmloc in Main: " + ppmloc);
	    //===============
	    List<String> ppmlocs = mod_map_pp.get(pppath);
	    if (ppmlocs == null) {
		ppmlocs = new ArrayList<String>();
		mod_map_pp.put(pppath, ppmlocs);
	    }
	    if (!ppmlocs.contains(ppmloc)) { ppmlocs.add(ppmloc); }
	}

	for (int i=0; i<oracle_med_instru_list_size; i++) {
	    MethodToBeInstrumented med_instru = oracle_med_instru_list.get(i);
	    String cpmloc = med_instru.getMethodLoc();
	    //if (cpmloc == null) { continue; } //This is not for instrumentation.
	    //===============
	    //System.err.println("cpmloc in Main: " + cpmloc);
	    //===============
	    String cppath = med_instru.getFilePath();
	    List<String> cpmlocs = mod_map_cp.get(cppath);
	    if (cpmlocs == null) {
		cpmlocs = new ArrayList<String>();
		mod_map_cp.put(cppath, cpmlocs);
	    }
	    if (cpmloc!=null && !cpmlocs.contains(cpmloc)) { cpmlocs.add(cpmloc); }
	}

	ClassUnderTestInstrumentor cftg = new ClassUnderTestInstrumentor();
	Iterator mod_map_fp_it = mod_map_fp.entrySet().iterator();
	while (mod_map_fp_it.hasNext()) {
	    Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) mod_map_fp_it.next();
	    String fppath = entry.getKey();
	    List<String> fpmlocs = entry.getValue();
	    InstrumentedClass fp_oic = cftg.getOutputInstrumentedClass(fppath, fpmlocs);
	    InstrumentedClass fp_tcic = cftg.getTestCaseInstrumentedClass(fppath, fpmlocs);
	    String fp_oic_fpath = fp_instru0_dpath+"/"+fp_oic.getClassName()+".java";
	    String fp_oic_fctnt = fp_oic.getInstrumentedClassContent();
	    if (fp_oic_fctnt != null) {
		try { FileUtils.writeStringToFile(new File(fp_oic_fpath), fp_oic_fctnt, (String)null); } catch (Throwable t) {
		    System.err.println(t);
		    t.printStackTrace();
		}
	    }

	    String fp_tcic_fpath = fp_instru1_dpath+"/"+fp_tcic.getClassName()+".java";
	    String fp_tcic_fctnt = fp_tcic.getInstrumentedClassContent();
	    if (fp_tcic_fctnt != null) {
		try { FileUtils.writeStringToFile(new File(fp_tcic_fpath), fp_tcic_fctnt, (String)null); } catch (Throwable t) {
		    System.err.println(t);
		    t.printStackTrace();
		}
	    }
	}

	Iterator mod_map_pp_it = mod_map_pp.entrySet().iterator();
	while (mod_map_pp_it.hasNext()) {
	    Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) mod_map_pp_it.next();
	    String pppath = entry.getKey();
	    List<String> ppmlocs = entry.getValue();
	    InstrumentedClass pp_oic = cftg.getOutputInstrumentedClass(pppath, ppmlocs);
	    InstrumentedClass pp_tcic = cftg.getTestCaseInstrumentedClass(pppath, ppmlocs);
	    String pp_oic_fpath = pp_instru0_dpath+"/"+pp_oic.getClassName()+".java";
	    String pp_oic_fctnt = pp_oic.getInstrumentedClassContent();
	    if (pp_oic_fctnt != null) {
		try { FileUtils.writeStringToFile(new File(pp_oic_fpath), pp_oic_fctnt, (String)null); } catch (Throwable t) {
		    System.err.println(t);
		    t.printStackTrace();
		}
	    }

	    String pp_tcic_fpath = pp_instru1_dpath+"/"+pp_tcic.getClassName()+".java";
	    String pp_tcic_fctnt = pp_tcic.getInstrumentedClassContent();
	    if (pp_tcic_fctnt != null) {
		try { FileUtils.writeStringToFile(new File(pp_tcic_fpath), pp_tcic_fctnt, (String)null); } catch (Throwable t) {
		    System.err.println(t);
		    t.printStackTrace();
		}
	    }
	}

	Iterator mod_map_cp_it = mod_map_cp.entrySet().iterator();
	while (mod_map_cp_it.hasNext()) {
	    Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) mod_map_cp_it.next();
	    String cppath = entry.getKey();
	    List<String> cpmlocs = entry.getValue();
	    if (cpmlocs.isEmpty()) {
		//Note that we still copy the uninstrumented file to its instrumentation directory. Later we compile all these files in the directory for running.
		try { FileUtils.copyFileToDirectory(new File(cppath), new File(cp_instru0_dpath)); } catch (Throwable t) {
		    System.err.println(t);
		    t.printStackTrace();
		}
	    }
	    else {
		InstrumentedClass cp_oic = cftg.getOutputInstrumentedClass(cppath, cpmlocs);
		String cp_oic_fpath = cp_instru0_dpath+"/"+cp_oic.getClassName()+".java";
		String cp_oic_fctnt = cp_oic.getInstrumentedClassContent();
		if (cp_oic_fctnt != null) {
		    try { FileUtils.writeStringToFile(new File(cp_oic_fpath), cp_oic_fctnt, (String)null); } catch (Throwable t) {
			System.err.println(t);
			t.printStackTrace();
		    }
		}
	    }
	}

	return true;
    }

    private boolean compileInstrumentedFiles(String bugid, String repair_tool, List<Modification> mod_list, List<MethodToBeInstrumented> oracle_med_instru_list, int trials, int timeout, String output_root_dpath) {

	String testid = bugid + "_" + repair_tool.toLowerCase();
	String proj_dpath = output_root_dpath + "/" + testid;
	String dependjpath = Global.dependjpath;
	String difftgendpath = Global.difftgendpath;
	File proj_dir = new File(proj_dpath);	

	String fp_instru0_build_dpath = proj_dpath+"/bug/instru0/build";
	String pp_instru0_build_dpath = proj_dpath+"/patch/instru0/build";
	String cp_instru0_build_dpath = proj_dpath+"/fix/instru0/build";
	String fp_instru1_build_dpath = proj_dpath+"/bug/instru1/build";
	String pp_instru1_build_dpath = proj_dpath+"/patch/instru1/build";
	File fp_instru0_build_dir = new File(fp_instru0_build_dpath);
	File pp_instru0_build_dir = new File(pp_instru0_build_dpath);
	File cp_instru0_build_dir = new File(cp_instru0_build_dpath);
	File fp_instru1_build_dir = new File(fp_instru1_build_dpath);
	File pp_instru1_build_dir = new File(pp_instru1_build_dpath);
	if (!fp_instru0_build_dir.exists()) {
	    fp_instru0_build_dir.mkdir();
	    (new File(fp_instru0_build_dpath+"/classes")).mkdir();
	}
	if (!pp_instru0_build_dir.exists()) {
	    pp_instru0_build_dir.mkdir();
	    (new File(pp_instru0_build_dpath+"/classes")).mkdir();
	}
	if (!cp_instru0_build_dir.exists()) {
	    cp_instru0_build_dir.mkdir();
	    (new File(cp_instru0_build_dpath+"/classes")).mkdir();
	}
	if (!fp_instru1_build_dir.exists()) {
	    fp_instru1_build_dir.mkdir();
	    (new File(fp_instru1_build_dpath+"/classes")).mkdir();
	}
	if (!pp_instru1_build_dir.exists()) {
	    pp_instru1_build_dir.mkdir();
	    (new File(pp_instru1_build_dpath+"/classes")).mkdir();
	}

	String libdpath = difftgendpath + "/lib";
	String compilepath = ":"+dependjpath+":"
	    +libdpath+"/myprinter.jar:"
	    +libdpath+"/commons-lang3-3.5.jar:"
	    +libdpath+"/junit-4.11.jar:"
	    +libdpath+"/evosuite-1.0.2.jar:"
	    +libdpath+"/servlet.jar";


	String srcdpath0 = proj_dpath+"/bug/instru0";
	String desdpath0 = fp_instru0_build_dpath+"/classes";
	CompileResult comp_rslt0 = CompileExecutor.compile(proj_dir, compilepath, srcdpath0, desdpath0);
	if (comp_rslt0.getExitValue() != 0) {
	    System.err.println("Failed Compiling Faulty Program's Output Instrumented Files.");
	    String[] compile_cmds0 = comp_rslt0.getCompileCommands();
	    for (String compile_cmd0 : compile_cmds0) {
		System.err.print(compile_cmd0 + " ");
	    }
	    System.err.println();
	    return false;
	}
	
	String srcdpath1 = proj_dpath+"/patch/instru0";
	String desdpath1 = pp_instru0_build_dpath+"/classes";
	CompileResult comp_rslt1 = CompileExecutor.compile(proj_dir, compilepath, srcdpath1, desdpath1);
	if (comp_rslt1.getExitValue() != 0) {
	    System.err.println("Failed Compiling Patched Program's Output Instrumented Files.");
	    String[] compile_cmds1 = comp_rslt1.getCompileCommands();
	    for (String compile_cmd1 : compile_cmds1) {
		System.err.print(compile_cmd1 + " ");
	    }
	    System.err.println();
	    return false;
	}

	String srcdpath2 = proj_dpath+"/fix/instru0";
	String desdpath2 = cp_instru0_build_dpath+"/classes";
	CompileResult comp_rslt2 = CompileExecutor.compile(proj_dir, compilepath, srcdpath2, desdpath2);
	if (comp_rslt2.getExitValue() != 0) {
	    System.err.println("Failed Compiling Oracle Program's Output Instrumented & Non-Instrumented Files.");
	    String[] compile_cmds2 = comp_rslt2.getCompileCommands();
	    for (String compile_cmd2 : compile_cmds2) {
		System.err.print(compile_cmd2 + " ");
	    }
	    System.err.println();
	    return false;
	}

	String srcdpath3 = proj_dpath+"/bug/instru1";
	String desdpath3 = fp_instru1_build_dpath+"/classes";
	CompileResult comp_rslt3 = CompileExecutor.compile(proj_dir, compilepath, srcdpath3, desdpath3);
	if (comp_rslt3.getExitValue() != 0) {
	    System.err.println("Failed Compiling Faulty Program's TestCase Instrumented Files.");
	    String[] compile_cmds3 = comp_rslt3.getCompileCommands();
	    for (String compile_cmd3 : compile_cmds3) {
		System.err.print(compile_cmd3 + " ");
	    }
	    System.err.println();
	    return false;
	}

	String srcdpath4 = proj_dpath+"/patch/instru1";
	String desdpath4 = pp_instru1_build_dpath+"/classes";
	CompileResult comp_rslt4 = CompileExecutor.compile(proj_dir, compilepath, srcdpath4, desdpath4);
	if (comp_rslt4.getExitValue() != 0) {
	    System.err.println("Failed Compiling Patched Program's TestCase Instrumented Files.");
	    String[] compile_cmds4 = comp_rslt4.getCompileCommands();
	    for (String compile_cmd4 : compile_cmds4) {
		System.err.print(compile_cmd4 + " ");
	    }
	    System.err.println();
	    return false;
	}

	return true;
    }

    private boolean compileTestTargets(String bugid, String repair_tool, List<Modification> mod_list, List<MethodToBeInstrumented> oracle_med_instru_list, int trials, int timeout, String output_root_dpath) {

	String testid = bugid + "_" + repair_tool.toLowerCase();
	String proj_dpath = output_root_dpath + "/" + testid;
	File proj_dir = new File(proj_dpath);
	String dependjpath = Global.dependjpath;
	String difftgendpath = Global.difftgendpath;

	String target_dpath = proj_dpath+"/target";
	String target_build_dpath = target_dpath+"/build";
	String target_build_classes_dpath = target_build_dpath+"/classes";
	File target_build_dir = new File(target_build_dpath);
	if (!target_build_dir.exists()) {
	    target_build_dir.mkdir();
	    new File(target_build_classes_dpath).mkdir();
	}

	String libdpath = difftgendpath + "/lib";
	String compilepath = ":"+dependjpath+":"
	    +libdpath+"/myprinter.jar:"
	    +libdpath+"/commons-lang3-3.5.jar:"
	    +libdpath+"/junit-4.11.jar:"
	    +libdpath+"/evosuite-1.0.2.jar:"
	    +libdpath+"/servlet.jar";

	CompileResult comp_rslt = CompileExecutor.compile(proj_dir, compilepath, target_dpath, target_build_classes_dpath);
	if (comp_rslt.getExitValue() != 0) {
	    System.err.println("Failed Compiling Target Program Files.");
	    String[] compile_cmds = comp_rslt.getCompileCommands();
	    for (String compile_cmd : compile_cmds) {
		System.err.print(compile_cmd + " ");
	    }
	    System.err.println();
	    return false;
	}

	//Create a dependency jar file with target files updated (this is later used by the test generator)
	String all0_fpath = target_build_classes_dpath+"/all0.jar";
	File all0_f = new File(all0_fpath);
	String[] cp_cmds0 = new String[] { "cp", dependjpath, all0_fpath };
	int cp_exit_val = CommandExecutor.execute(cp_cmds0, new File(proj_dpath), null);
	if (cp_exit_val != 0) {
	    System.err.println("Failed Copying the Dependency Jar File.");
	    for (String cp_cmd0 : cp_cmds0) {
		System.err.print(cp_cmd0 + " ");
	    }
	    System.err.println();
	    return false;
	}

	//Update the copied dependency jar file
	List<String> jar_upt_cmd_list = new ArrayList<String>();
	jar_upt_cmd_list.add("jar");
	jar_upt_cmd_list.add("uf");
	jar_upt_cmd_list.add("all0.jar");
	File target_build_classes_dir = new File(target_build_classes_dpath);
	File[] files_to_be_updated = target_build_classes_dir.listFiles();
	for (File file_to_be_updated : files_to_be_updated) {
	    String file_to_be_updated_name = file_to_be_updated.getName();
	    if (file_to_be_updated.isDirectory() || file_to_be_updated_name.endsWith(".class")) {
		jar_upt_cmd_list.add(file_to_be_updated_name);
	    }
	}
	String[] jar_upt_cmds = jar_upt_cmd_list.toArray(new String[0]);
	int jar_upt_exit_val = CommandExecutor.execute(jar_upt_cmds, target_build_classes_dir, null);
	if (jar_upt_exit_val != 0) {
	    System.err.println("Failed Updating the Copied Dependency Jar File.");
	    for (String jar_upt_cmd : jar_upt_cmds) {
		System.err.print(jar_upt_cmd + " ");
	    }
	    System.err.println();
	    return false;
	}
	
	return true;
    }

    private boolean writeTestCaseToFile(TestCase tc, String proj_dpath) {
	String tc_full_name = tc.getTestCaseFullName();
	String rslt_tc_ctnt = tc.getTestCaseContent();
	if (rslt_tc_ctnt == null) { return false; }
	//Write to file
	String tc_name = null;
	int last_dot_index = tc_full_name.lastIndexOf(".");
	if (last_dot_index == -1) { tc_name = tc_full_name; }
	else { tc_name = tc_full_name.substring(last_dot_index+1); }
	String rslt_tc_fpath = proj_dpath + "/testcase/" + tc_name + ".java";
	File rslt_tc_f = new File(rslt_tc_fpath);
	try { FileUtils.writeStringToFile(rslt_tc_f, rslt_tc_ctnt, (String) null); }
	catch (Throwable t) {
	    t.printStackTrace(); System.err.println(t);
	    return false;
	}
	return true;
    }

    private boolean compileTestCases(String proj_dpath) {

	String difftgendpath = Global.difftgendpath;
	String dependjpath = Global.dependjpath;
	String libdpath = difftgendpath + "/lib";
	String compilepath =
	    ":"+proj_dpath+"/bug/instru1/build/classes:" //Instrumented Files First
	    +dependjpath+":"
	    +libdpath+"/myprinter.jar:"
	    +libdpath+"/commons-lang3-3.5.jar:"
	    +libdpath+"/junit-4.11.jar:"
	    +libdpath+"/evosuite-1.0.2.jar:"
	    +libdpath+"/servlet.jar";

	String tc_dpath = proj_dpath+"/testcase";
	String tc_build_dpath = tc_dpath+"/build";
	String tc_build_classes_dpath = tc_build_dpath+"/classes";
	File tc_dir = new File(tc_dpath);
	File tc_build_dir = new File(tc_build_dpath);
	File tc_build_classes_dir = new File(tc_build_classes_dpath);
	if (!tc_build_dir.exists()) { tc_build_dir.mkdir(); }
	if (!tc_build_classes_dir.exists()) { tc_build_classes_dir.mkdir(); }

	CompileResult comp_rslt = CompileExecutor.compile(tc_dir, compilepath, tc_dpath, tc_build_classes_dpath);
	if (comp_rslt.getExitValue() != 0) {
	    System.err.println("Failed Compiling the Test Cases.");
	    String[] comp_cmds = comp_rslt.getCompileCommands();
	    for (String comp_cmd : comp_cmds) {
		System.err.print(comp_cmd + " ");
	    }
	    System.err.println();
	    return false;
	}

	return true;
    }
    
    public void testgen(String bugid, String repair_tool, List<Modification> mod_list,
			List<MethodToBeInstrumented> oracle_med_instru_list,
			int trials, int timeout, String output_root_dpath) {

	Timer timer = Global.timer;
	timer.start();

	System.out.println("Initializing...");
	boolean status0 = init(bugid, repair_tool, mod_list, oracle_med_instru_list, trials, timeout, output_root_dpath);
	if (!status0) {
	    System.err.println("Initialization Failure.");
	    return;
	}
	System.out.println("Initializing Done.");


	System.out.println("Creating Instrumented Files...");
	boolean status1 = createInstrumentedFiles(bugid, repair_tool, mod_list, oracle_med_instru_list, trials, timeout, output_root_dpath);
	if (!status1) {
	    System.err.println("Create Instrumentation Files Failure.");
	    return;
	}
	System.out.println("Creating Instrumented Files Done.");


	System.out.println("Compiling Instrumented Files...");	
	boolean status2 = compileInstrumentedFiles(bugid, repair_tool, mod_list, oracle_med_instru_list, trials, timeout, output_root_dpath);
	if (!status2) {
	    System.err.println("Compiling Instrumented Files Failure.");
	    return;
	}
	System.out.println("Compiling Instrumented Files Done.");


	System.out.println("Creating Test Target(s)...");
	String testid = bugid + "_" + repair_tool.toLowerCase();
	String proj_dpath = output_root_dpath + "/" + testid;
	String target_dpath = proj_dpath + "/target";
	TestTargetGenerator ttgen = new TestTargetGenerator();
	List<TestTarget> tt_list = ttgen.getTestTargets(mod_list);
	int tt_list_size = tt_list.size();
	for (int i=0; i<tt_list_size; i++) {
	    TestTarget tt = tt_list.get(i);
	    System.out.println("Test Target No."+i+":");
	    System.out.println(tt);
	    File ttf = new File(target_dpath+"/"+tt.getFileName());
	    try { FileUtils.writeStringToFile(ttf, tt.getFileContent(), (String)null); }
	    catch (Throwable t) {
		System.err.println(t);
		t.printStackTrace();
	    }
	}
	System.out.println("Creating Test Target(s) Done.");


	System.out.println("Compiling Test Target(s)...");
	boolean status3 = compileTestTargets(bugid, repair_tool, mod_list, oracle_med_instru_list, trials, timeout, output_root_dpath);
	if (!status3) {
	    System.err.println("Compiling Target Programs Failure.");
	    return;
	}
	System.out.println("Compiling Test Target(s) Done.");


	System.out.println("Generating Test Case(s)...");
	boolean overfitting_break = Global.stopifoverfittingfound;
	TestCaseGenerator tcgen = new TestCaseGenerator();
	TestCase regression_tc = null, repair_tc = null, defective_tc = null;
	for (int i=0; i<tt_list_size; i++) {
	    TestTarget tt = tt_list.get(i);
	    System.out.println("Working on Test Target No."+i+" for Test Case Generation.");
	    List<TestCase> tc_list = tcgen.generateTestCases(i+"", tt);
	    for (TestCase tc : tc_list) {
		if (regression_tc == null && tc.isRegressionIndicative()) {
		    regression_tc = tc;
		}
		if (repair_tc == null && tc.isRepairIndicative()) {
		    repair_tc = tc;
		}
		if (defective_tc == null && tc.isDefectiveIndicative()) {
		    defective_tc = tc;
		}
	    }
	    if ((overfitting_break && (regression_tc!=null || defective_tc!=null)) ||
	        (regression_tc!=null && repair_tc!=null && defective_tc!=null)) {
		if (!timer.isReset()) {
		    timer.end();
		    System.out.println("Total execution time: " + timer.getDurationInMillis());
		    timer.reset();
		}
		break;
	    }
	}

	if (!timer.isReset()) {
	    timer.end();
	    System.out.println("Total execution time: " + timer.getDurationInMillis());
	    timer.reset();
	}

	if (regression_tc==null && repair_tc==null && defective_tc==null) {
	    System.out.println("Found Nothing.");
	    return;
	}

	if (regression_tc != null) {
	    boolean write_tc = writeTestCaseToFile(regression_tc, proj_dpath);
	    if (!write_tc) { System.out.println("Write Regression Test Case Failure."); }
	}
	if (repair_tc != null) {
	    boolean write_tc = writeTestCaseToFile(repair_tc, proj_dpath);
	    if (!write_tc) { System.out.println("Write Repair Test Case Failure."); }
	}
	if (defective_tc != null) {
	    boolean write_tc = writeTestCaseToFile(defective_tc, proj_dpath);
	    if (!write_tc) { System.out.println("Write Defective Test Case Failure."); }
	}
	System.out.println("Generating Test Case(s) Done");

	
	System.out.println("Compiling Test Case(s)...");
	boolean status4 = compileTestCases(proj_dpath);
	if (!status4) {
	    System.err.println("Compiling Test Cases Failure.");
	    return;
	}
	System.out.println("Compiling Test Case(s) Done.");
    }
}
