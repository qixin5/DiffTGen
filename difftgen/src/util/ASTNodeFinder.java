package util;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ASTVisitor;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class ASTNodeFinder
{
    public static List<ASTNode> find(CompilationUnit cu, String loc) {
	List<ASTNode> rslt_list = new ArrayList<ASTNode>();
	if (cu == null) { return rslt_list; }
	String[] sublocs = loc.split(";");
	for (String subloc : sublocs) {
	    String[] subloc_items = subloc.split(":");
	    String subloc_title = "", subloc_ctnt = "";
	    try {
		subloc_title = subloc_items[0];
		subloc_ctnt = subloc_items[1];
	    } catch (Exception e) {
		System.err.println("Unknown loc: " + subloc);
		return new ArrayList<ASTNode>();
	    }
	    ASTNode found_node = findNode(cu, subloc_ctnt);
	    rslt_list.add(found_node);
	}
	return rslt_list;
    }

    private static ASTNode findNode(CompilationUnit cu, String loc_ctnt) {
	String[] rc = loc_ctnt.split(",");
	int sln = -1, scn = -1;
	try {
	    sln = Integer.parseInt(rc[0]); //start line number
	    scn = Integer.parseInt(rc[1]); //start column number
	} catch (Exception e) {
	    System.err.println("Parsing Error: " + loc_ctnt);
	}
	if (sln == -1 || scn == -1) { return null; }
	int charseq = cu.getPosition(sln, scn);
	NodeFindVisitor nfv = new NodeFindVisitor(charseq);
	cu.accept(nfv);
	return nfv.getFoundNode();
    }

    private static class NodeFindVisitor extends ASTVisitor
    {
	private int charseq;
	private ASTNode found_node;

	public NodeFindVisitor(int charseq) { this.charseq = charseq; }

	public ASTNode getFoundNode() { return found_node; }

	@Override public boolean preVisit2(ASTNode node) {
	    int node_charseq = node.getStartPosition();
	    //========
	    /*
	    System.err.println("--- Node ---");
	    CompilationUnit cu = (CompilationUnit) node.getRoot();
	    System.err.println("QUERY CHARSEQ: " + charseq);
	    System.err.println("CURRENT CHARSEQ: " + node_charseq);
	    System.err.print("CURRENT LC: ");
	    System.err.println(cu.getLineNumber(node_charseq)+","+cu.getColumnNumber(node_charseq));
	    System.err.println(node);
	    System.err.println();
	    */
	    //========
	    if (node_charseq == charseq) {
		found_node = node;
		return false;
	    }
	    else if (node_charseq > charseq) {
		return false; //unlikely to find the target
	    }
	    else {
		return true; //Still need to visit the children
	    }
	}
    }
}
