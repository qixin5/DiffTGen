package testgen;

public class Global
{
    public static Timer timer;
    public static String bugid;
    public static String repairtool;
    public static String difftgendpath;
    public static String scriptrootdpath;
    public static String d4jprojdpath;
    public static String evosuitejpath;
    public static String dependjpath;
    public static String outputdpath;
    public static String inputfpath;
    public static String oracleinputfpath;    
    public static int evosuitetrials;
    public static int evosuitetimeout;
    public static boolean forcecompile;
    public static boolean runparallel;
    public static boolean simpletarget;
    public static boolean stopifoverfittingfound;
    public static boolean runevosuite;
    
    
    static {
	timer = new Timer();
	bugid = "bugukn";
	repairtool = "repairtoolukn";
	difftgendpath = "/home/qx5/testgen-evosuite";
	scriptrootdpath = "/data/people/qx5/testgen/scripts";
	d4jprojdpath = "/data/people/qx5/defects4j-bugs/train_samples/projs";
	evosuitejpath = "/data/people/qx5/evosuite-1.0.2.jar";
	dependjpath = null;
	outputdpath = null;
	inputfpath = null;
	oracleinputfpath = null;
	evosuitetrials = 30;
	evosuitetimeout = 60;
	forcecompile = true;
	runparallel = false;
	simpletarget = false;
	stopifoverfittingfound = false;
	runevosuite = true;
    }
}
