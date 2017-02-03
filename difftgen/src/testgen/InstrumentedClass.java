package testgen;

public class InstrumentedClass
{
    String class_name;
    String target_method_name;
    String instru_class_ctnt;

    public InstrumentedClass(String cn, String tmn, String icc) {
	class_name = cn;
	target_method_name = tmn;
	instru_class_ctnt = icc;
    }

    public String getClassName() { return class_name; }

    public String getTargetMethodName() { return target_method_name; }

    public String getInstrumentedClassContent() { return instru_class_ctnt; }
}
