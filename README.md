# DiffTGen

DiffTGen is a testing technique which identifies test-suite-overfitted patches through test case generation.

## Running Requirements

+ Linux environment
+ JDK 1.8
+ Apache Ant

## How to Build DiffTGen

Run `ant compile` to compile all the source files.

## How to Run DiffTGen

+ In the script file `run`, change the value of `proj_dir` to the *absolute* path of your DiffTGen directory (if you haven't done so).

+ Run the script file `run` with arguments as follows:
  * `-bugid`: the program id
  * `-repairtool`: the repair tool id
  * `-difftgendpath`: the path of your DiffTGen directory
  * `-evosuitejpath`: the path of the EvoSuite jar (you may use the jar we provided in the `lib` directory)
  * `-dependjpath`: the dependency jar path of the faulty program
  * `-outputdpath`: the path of the output directory
  * `-inputfpath`: the path of the input file indicating the syntactic deltas between the faulty program and the patched program
  * `-oracleinputfpath`: the path of the oracle input file containing oracle methods
  * `-evosuitetrials`: the number of the trials that EvoSuite runs
  * `-evosuitetimeout`: EvoSuite's running timeout in seconds for a trial
  * `-runparallel`: do you want to run the trials in parallel?
  * `-stopifoverfittingfound`: do you want to stop running if an overfitting-indicative is found?

### How to Create an Input File

The input file encodes a list of syntactic deltas between the faulty program and the patched program. For each delta, you write two lines in the file with the first line representing the modified statement in the faulty program and second representing the modified statement in the patched program. So in the file, the first two lines encode the first delta, the second two lines encode the second delta, and so on.

For a non-null modified statement, the form of a line in the input file is `filepath:ln,cn` where `filepath` is the file path of the program (either the faulty program or the patched program), `ln` is the starting line number of the statement, and `cn` is the starting column number of the statement. (Note that the column number is equal to the number of space characters from the start of a line to the first non-space character of the statement in the line. For example, if the first character of a statement is two tabs after the start of its line, the column number is 2.)

For a null modified statement as a removed statement (for an insertion, the modified statement in the faulty program is null, and for a deletion, the modified statement in the patched program is null), the form of a line in the input file is `null(filepath:ln,cn;ctxtloc)`. The keyword `null` is used to indicate it is a null modified statement. Within the parentheses, you need to specify a context statement using `filepath:ln,cn` and its location relative to the removed statement using `ctxtloc`. `ctxtloc` could be `before` or `after` to indicate the context statement is *before* or *after* the removed statement. It could also be something as `else-branch` to indicate the removed statement is the only statement in the else-branch of the context statement which is an *if*-statement. All the options for `ctxtloc` are `before`, `after`, `then-branch`, `else-branch`, `block-body`, `do-body`, `enhancedfor-body`, `for-body`, `switch-body`, `synchronized-body`, `try-body`, `catch-clause-body[x]` where x is the catch clause index, `finally-body`, and `while-body`. It is recommended that you always try using *before* or *after* as `ctxtloc` if you can.

### How to Create an Oracle File

Currently, DiffTGen uses a fixed version of the faulty program as oracle. DiffTGen creates an instrumented version of the oracle program and runs it against any EvoSuite-generated test methods (as test inputs) to obtain the expected outputs. In an oracle file, you need to specify *the methods in the fixed version that correspond to those that are patched in the patched program*. If they do not include all the methods where the correct fixes are made, you need to additionally specify *the files where the correct fixes are made*. For example, for the bug *Chart_26*, the repair tool jKali modifies a method in *CategoryPlot.java*, but the bug's correct version modifies something in *Axis.java*. In this case, you need to create an oracle file specifying (1) the same method of the fixed version of *CategoryPlot.java* and (2) the fixed version (as a file) of *Axis.java*.

When you need to specify a method of the fixed program, put a line in the form of `filepath:ln,cn` where `filepath` is the path of the method's class file, `ln` is the starting line number of the method, and `cn` is the starting column of the method. (Note that if a method starts with a javadoc, you need to specify the line and column numbers of its opening tag /\*\*.)

When you need to specify a fixed file, put a line in the form of `null(filepath)` where `null` is the keyword and `filepath` is the path of the fixed file.

Examples of the input and oracle files can be found under the directory `examples`.


## Output

The generated test cases, if any, can be found under `outputdir/testcase` where `outputdir` is the output directory you specified. The file named `DiffTGen0Test.java` is an overfitting-indicative test case. The file named `DiffTGen1Test.java` is a repair-indicative test case. The file named `DiffTGen2Test.java` is a defective-indicative test case. (A test-case-instrumented version of the faulty program can be found under `outputdir/bug/instru1`.)