package testgen;

public class TestCase
{
    public int prop;
    public String tc_full_name;
    public String tc_ctnt;

    public TestCase(int p, String s0, String s1) {
	prop = p;
	tc_full_name = s0;
	tc_ctnt = s1;
    }

    public boolean isOverfittingIndicative() {
	return (prop == 0);
    }

    public boolean isFixedIndicative() {
	return (prop == 1);
    }

    public boolean isBothIncorrectIndicative() {
	return (prop == 2);
    }

    public String getTestCaseFullName() {
	return tc_full_name;
    }
    
    public String getTestCaseContent() {
	return tc_ctnt;
    }
}
