package testgen;

public class Modification
{
    String fppath, pppath;
    String fploc, pploc;

    public Modification(String fppath, String fploc, String pppath, String pploc) {
	this.fppath = fppath;
	this.fploc = fploc;
	this.pppath = pppath;
	this.pploc = pploc;
    }

    public String getFPPath() {
	return fppath;
    }

    public String getFPLoc() {
	return fploc;
    }
    
    public String getPPPath() {
	return pppath;
    }

    public String getPPLoc() {
	return pploc;
    }
}
