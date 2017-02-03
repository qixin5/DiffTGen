package testgen;

import java.io.File;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import util.*;


public class TestTargetGenerator
{
    public static final String DELTA_NAME = "delta_syn_3nz5e";
    
    public TestTarget getTestTarget(Modification mod) {

	if (mod == null) { return null; }

	List<String> goal_locs = new ArrayList<String>();
	File fp = new File(mod.getFPPath());
	File pp = new File(mod.getPPPath());
	String fp_ctnt = null, pp_ctnt = null;
	try {
	    fp_ctnt = FileUtils.readFileToString(fp, (String) null);
	    pp_ctnt = FileUtils.readFileToString(pp, (String) null);
	}
	catch (Throwable t) {
	    System.err.println(t);
	    t.printStackTrace();
	}
	if (fp_ctnt == null || pp_ctnt == null) {
	    return null;
	}

	CompilationUnit fp_cu = (CompilationUnit) ASTNodeLoader.getASTNode(fp);
	CompilationUnit pp_cu = (CompilationUnit) ASTNodeLoader.getASTNode(pp);
	AST fp_ast = fp_cu.getAST();
	AST pp_ast = pp_cu.getAST();
	PackageDeclaration fp_pd = fp_cu.getPackage();
	String fp_pname = fp_pd.getName().getFullyQualifiedName(); //fp package name
	PackageDeclaration pp_pd = pp_cu.getPackage();
	String pp_pname = pp_pd.getName().getFullyQualifiedName(); //pp package name
	String fp_name = fp.getName();
	String pp_name = pp.getName();
	String fp_loc = mod.getFPLoc();
	String pp_loc = mod.getPPLoc();
	if (fp_loc == null && pp_loc == null) { return null; }

	//Deletion
	else if (fp_loc != null && pp_loc == null) {
	    ASTNode fp_node = ASTNodeFinder.find(fp_cu, fp_loc).get(0);
	    if (fp_node instanceof IfStatement) {
		IfStatement if_fp_node = (IfStatement) fp_node;
		if (if_fp_node.getElseStatement() == null) { //Partial
		    Statement then_stmt = if_fp_node.getThenStatement();
		    ASTNode target_node = getBranchTargetNode(then_stmt);
		    goal_locs.add(getLoc(fp_cu, target_node));
		    List<String> goal_methods = getGoalMethods(fp_cu, goal_locs);
		    return new TestTarget(fp_pname, fp_name, fp_ctnt, goal_locs, goal_methods);
		}
	    }

	    TestTarget tt = getGeneralChangeTestTarget(fp_ast, fp_cu, fp_node, fp_ctnt, fp_pname, fp_name);
	    return tt; //tt must not be null
	}

	//Insertion
	else if (fp_loc == null && pp_loc != null) {
	    ASTNode pp_node = ASTNodeFinder.find(pp_cu, pp_loc).get(0);
	    if (pp_node instanceof IfStatement) {
		IfStatement if_pp_node = (IfStatement) pp_node;
		if (if_pp_node.getElseStatement() == null) { //Partial
		    Statement then_stmt = if_pp_node.getThenStatement();
		    ASTNode target_node = getBranchTargetNode(then_stmt);
		    goal_locs.add(getLoc(pp_cu, target_node));
		    List<String> goal_methods = getGoalMethods(pp_cu, goal_locs);
		    return new TestTarget(pp_pname, pp_name, pp_ctnt, goal_locs, goal_methods);
		}
	    }
	    TestTarget tt = getGeneralChangeTestTarget(pp_ast, pp_cu, pp_node, pp_ctnt, pp_pname, pp_name);
	    return tt; //tt must not be null
	}

	else {
	    ASTNode fp_node = ASTNodeFinder.find(fp_cu, fp_loc).get(0);
	    ASTNode pp_node = ASTNodeFinder.find(pp_cu, pp_loc).get(0);
	    boolean is_fp_if = (fp_node instanceof IfStatement);
	    boolean is_pp_if = (pp_node instanceof IfStatement);

	    if (is_fp_if && is_pp_if) {
		IfStatement fp_if_stmt  = (IfStatement) fp_node;
		IfStatement pp_if_stmt  = (IfStatement) pp_node;
		Expression e1 = fp_if_stmt.getExpression();
		Expression e2 = pp_if_stmt.getExpression();
		Statement fp_then_stmt = fp_if_stmt.getThenStatement();
		Statement fp_else_stmt = fp_if_stmt.getElseStatement();
		Statement pp_then_stmt = pp_if_stmt.getThenStatement();
		Statement pp_else_stmt = pp_if_stmt.getElseStatement();
		boolean then_then = isEquivalent(fp_then_stmt, pp_then_stmt);
		boolean else_else = isEquivalent(fp_else_stmt, pp_else_stmt);
		boolean then_else = isEquivalent(fp_then_stmt, pp_else_stmt);
		boolean else_then = isEquivalent(fp_else_stmt, pp_then_stmt);

		if (isIdenticalToThenBranch(fp_if_stmt, pp_node)) {
		    //If-guard Deletion
		    TestTarget tt = getIfGuardModificationTestTarget(fp_ast, fp_if_stmt, fp_ctnt, fp_pname, fp_name);
		    if (tt != null) { return tt; }
		}

		if (isIdenticalToThenBranch(pp_if_stmt, fp_node)) {
		    //If-guard Insertion
		    TestTarget tt = getIfGuardModificationTestTarget(pp_ast, pp_if_stmt, pp_ctnt, pp_pname, pp_name);
		    if (tt != null) { return tt; }
		}
		
		//Simply pp_node as the goal
		if (!then_then && !else_else && !then_else && !else_then) {
		    TestTarget tt = getGeneralChangeTestTarget(pp_ast, pp_cu, pp_node, pp_ctnt, pp_pname, pp_name);
		    return tt; //tt must not be null!
		}

		//Create (!e1 && e2 || e1 && !e2)
		else if (then_then && else_else) {
		    TestTarget tt = getIfChangeTestTarget(0, pp_ast, fp_if_stmt, pp_if_stmt, pp_ctnt, pp_pname, pp_name);
		    if (tt != null) { return tt; }
		}

		//!(e1 && e2)
		else if (then_then) {
		    TestTarget tt = getIfChangeTestTarget(1, pp_ast, fp_if_stmt, pp_if_stmt, pp_ctnt, pp_pname, pp_name);
		    if (tt != null) { return tt; }
		}

		//!(!e1 && !e2) => e1 || e2
		else if (else_else) {
		    TestTarget tt = getIfChangeTestTarget(2, pp_ast, fp_if_stmt, pp_if_stmt, pp_ctnt, pp_pname, pp_name);
		    if (tt != null) { return tt; }		    
		}

		else if (then_else) {
		    //Do we do this?
		}

		else if (else_then) {
		    //Do we do this?
		}
	    }

	    else if (is_fp_if && !is_pp_if) {
		IfStatement fp_if_stmt  = (IfStatement) fp_node;
		Expression e1 = fp_if_stmt.getExpression();
		Statement fp_then_stmt = fp_if_stmt.getThenStatement();
		if (isIdenticalToThenBranch(fp_if_stmt, pp_node)) {
		    //If-guard Deletion
		    TestTarget tt = getIfGuardModificationTestTarget(fp_ast, fp_if_stmt, fp_ctnt, fp_pname, fp_name);
		    if (tt != null) { return tt; }
		}
	    }

	    else if (!is_fp_if && is_pp_if) {
		IfStatement pp_if_stmt  = (IfStatement) pp_node;
		Expression e2 = pp_if_stmt.getExpression();
		Statement pp_then_stmt = pp_if_stmt.getThenStatement();
		if (isIdenticalToThenBranch(pp_if_stmt, fp_node)) {
		    //If-guard Insertion
		    TestTarget tt = getIfGuardModificationTestTarget(pp_ast, pp_if_stmt, pp_ctnt, pp_pname, pp_name);
		    if (tt != null) { return tt; }
		}
	    }

	    //For all other cases, pp_node would be the target.
	    TestTarget tt = getGeneralChangeTestTarget(pp_ast, pp_cu, pp_node, pp_ctnt, pp_pname, pp_name);
	    return tt; //tt must not be null!
	}
    }

    private TestTarget getGeneralChangeTestTarget(AST pp_ast, CompilationUnit pp_cu, ASTNode pp_node, String pp_ctnt, String pp_pname, String pp_name) {

	List<String> goal_locs = new ArrayList<String>();
	goal_locs.add(getLoc(pp_cu, pp_node));
	List<String> goal_methods = getGoalMethods(pp_cu, goal_locs);
	return new TestTarget(pp_pname, pp_name, pp_ctnt, goal_locs, goal_methods);
    }
    
    private TestTarget getIfGuardModificationTestTarget(AST fp_ast, IfStatement fp_if_stmt, String fp_ctnt, String fp_pname, String fp_name) {

	String delta_syn_name = DELTA_NAME + "_0";
	ASTRewrite fp_rw = ASTRewrite.create(fp_ast);
	Expression e1 = fp_if_stmt.getExpression();
	ASTNode new_cond = createNotE(fp_ast, e1);
	LabeledStatement pholder_lstmt = getPHolderLStatement(fp_ast, (Expression) new_cond, delta_syn_name);
	
	ASTNode fp_if_stmt_par = fp_if_stmt.getParent();
	StructuralPropertyDescriptor spd = fp_if_stmt.getLocationInParent();
	if (spd.isChildListProperty()) {
	    ChildListPropertyDescriptor lspd = (ChildListPropertyDescriptor) spd;
	    ListRewrite lrw = fp_rw.getListRewrite(fp_if_stmt_par, lspd);
	    lrw.insertBefore(pholder_lstmt, fp_if_stmt, null);
	}
	else if (spd.isChildProperty()) {
	    Block block0 = fp_ast.newBlock();
	    block0.statements().add(pholder_lstmt);
	    block0.statements().add(fp_if_stmt);
	    fp_rw.set(fp_if_stmt_par, spd, block0, null);
	}
	else {
	    System.err.println("Structural Property Unrecognized: " + spd);
	    return null;
	}

	//Load the modified Compilation Unit
	Document doc = new Document(fp_ctnt);
	TextEdit tedit = fp_rw.rewriteAST(doc, null);
	try { tedit.apply(doc); }
	catch (Exception e) {
	    System.err.println("Text Edit Apply Error: "+e);
	}
	String tt_ctnt = doc.get();
	CompilationUnit tt_cu = (CompilationUnit) ASTNodeLoader.getASTNode(tt_ctnt);

	//Get the labeled placeholder statement
	LabeledStatementFinder lsfinder = new LabeledStatementFinder(delta_syn_name);
	tt_cu.accept(lsfinder);
	pholder_lstmt = lsfinder.getLabeledStatement();
	if (pholder_lstmt == null) {
	    System.err.println("The placeholder statement labeled " + delta_syn_name + " is not found!");
	    return null;
	}

	ASTNode tt_node = pholder_lstmt.getBody();
	if (tt_node instanceof IfStatement) {
	    //Shouldn't happen otherwise.
	    IfStatement tt_if_stmt = (IfStatement) tt_node;
	    Statement tt_then_stmt = tt_if_stmt.getThenStatement();
	    ASTNode target_then_node = getBranchTargetNode(tt_then_stmt);
	    List<String> goal_locs = new ArrayList<String>();
	    if (target_then_node != null)
		goal_locs.add(getLoc(tt_cu, target_then_node));
	    List<String> goal_methods = getGoalMethods(tt_cu, goal_locs);
	    return new TestTarget(fp_pname, fp_name, tt_ctnt, goal_locs, goal_methods);
	}

	return null;
    }

    private TestTarget getIfChangeTestTarget(int change_type, AST pp_ast, IfStatement fp_if_stmt, IfStatement pp_if_stmt, String pp_ctnt, String pp_pname, String pp_name) {

	String delta_syn_name = DELTA_NAME + "_0";
	ASTRewrite pp_rw = ASTRewrite.create(pp_ast);
	Expression e1 = fp_if_stmt.getExpression();
	Expression e2 = pp_if_stmt.getExpression();
	ASTNode new_cond = null;
	if (change_type == 0) { new_cond = createNotE1E2ORE1NotE2(pp_ast, e1, e2); }
	else if (change_type == 1) { new_cond = createNotE1E2(pp_ast, e1, e2); }
	else if (change_type == 2) { new_cond = createE1ORE2(pp_ast, e1, e2); }
	else {
	    System.err.println("I don't recognize the change type " + change_type + " for creating a forking if-statement.");
	    return null;
	}
	
	LabeledStatement pholder_lstmt = getPHolderLStatement(pp_ast, (Expression) new_cond, delta_syn_name);
	ASTNode pp_if_stmt_par = pp_if_stmt.getParent();
	StructuralPropertyDescriptor spd = pp_if_stmt.getLocationInParent();
	if (spd.isChildListProperty()) {
	    ChildListPropertyDescriptor lspd = (ChildListPropertyDescriptor) spd;
	    ListRewrite lrw = pp_rw.getListRewrite(pp_if_stmt_par, lspd);
	    lrw.insertBefore(pholder_lstmt, pp_if_stmt, null);
	}
	else if (spd.isChildProperty()) {
	    Block block0 = pp_ast.newBlock();
	    block0.statements().add(pholder_lstmt);
	    block0.statements().add((Statement) ASTNode.copySubtree(pp_ast, pp_if_stmt));
	    pp_rw.set(pp_if_stmt_par, spd, block0, null);
	}
	else {
	    System.err.println("Structural Property Unrecognized: " + spd);
	    return null;
	}

	Document doc = new Document(pp_ctnt);
	TextEdit tedit = pp_rw.rewriteAST(doc, null);
	try { tedit.apply(doc); }
	catch (Exception e) {
	    System.err.println("Text Edit Apply Error: " + e);
	}
	String tt_ctnt = doc.get();
	CompilationUnit tt_cu = (CompilationUnit) ASTNodeLoader.getASTNode(tt_ctnt);
	//Get the labeled placeholder statement
	LabeledStatementFinder lsfinder = new LabeledStatementFinder(delta_syn_name);
	tt_cu.accept(lsfinder);
	pholder_lstmt = lsfinder.getLabeledStatement();
	if (pholder_lstmt == null) {
	    System.err.println("The placeholder statement labeled " + delta_syn_name + " is not found!");
	    return null;
	}

	ASTNode tt_node = pholder_lstmt.getBody();
	if (tt_node instanceof IfStatement) {
	    //Shouldn't happen otherwise.
	    IfStatement tt_if_stmt = (IfStatement) tt_node;
	    Statement tt_then_stmt = tt_if_stmt.getThenStatement();
	    ASTNode target_then_node = getBranchTargetNode(tt_then_stmt);
	    List<String> goal_locs = new ArrayList<String>();
	    if (target_then_node != null)
		goal_locs.add(getLoc(tt_cu, target_then_node));
	    List<String> goal_methods = getGoalMethods(tt_cu, goal_locs);
	    return new TestTarget(pp_pname, pp_name, tt_ctnt, goal_locs, goal_methods);
	}

	return null;
    }

    /* A very simple version for comparison. */
    public TestTarget getTestTarget0(Modification mod) {

	if (mod == null) { return null; }
	
	File fp = new File(mod.getFPPath());
	File pp = new File(mod.getPPPath());

	String ttname = null;
	String ttctnt = null;
	String ttloc = null;

	if (mod.getPPLoc() != null) {
	    ttname = pp.getName();
	    try { ttctnt = FileUtils.readFileToString(pp, (String) null); }
	    catch (Throwable t) {
		System.err.println(t);
		t.printStackTrace();
	    }
	    ttloc = mod.getPPLoc();
	}
	else {
	    ttname = fp.getName();
	    try { ttctnt = FileUtils.readFileToString(fp, (String) null); }
	    catch (Throwable t) {
		System.err.println(t);
		t.printStackTrace();
	    }
	    ttloc = mod.getFPLoc();
	}

	if (ttctnt == null) {
	    return null;
	}
	
	CompilationUnit ttcu = (CompilationUnit) ASTNodeLoader.getASTNode(ttctnt);
	PackageDeclaration ttpd = ttcu.getPackage();
	String ttpname = ttpd.getName().getFullyQualifiedName(); //package name
	
	List<String> goal_locs = new ArrayList<String>();
	goal_locs.add(ttloc);

	//Generate the goal methods corresponding to the goal locs.
	List<String> goal_methods = new ArrayList<String>();
	for (String goal_loc : goal_locs) {
	    String goal_method = null;
	    List<ASTNode> goal_nodes = ASTNodeFinder.find(ttcu, goal_loc);
	    for (ASTNode goal_node : goal_nodes) {
		ASTNode par = goal_node;
		while (par != null) {
		    if (par instanceof MethodDeclaration) {
			MethodDeclaration md = (MethodDeclaration) par;
			goal_method = md.getName().getIdentifier();
			break;
		    }
		    par = par.getParent();
		}
		if (goal_method != null) {
		    break;
		}
	    }
	    goal_methods.add(goal_method); //goal_method could be NULL.
	}
	
	return new TestTarget(ttpname, ttname, ttctnt, goal_locs, goal_methods);
    }

    private boolean isPartialIfStmt(ASTNode node) {
	if (node == null) { return false; }
	if (node instanceof IfStatement) {
	    IfStatement if_stmt = (IfStatement) node;
	    if (if_stmt.getElseStatement() == null) {
		return true;
	    }
	}
	return false;
    }

    private String getLoc(CompilationUnit cu, ASTNode node) {
	int start_pos = node.getStartPosition();
	return "slc:" + cu.getLineNumber(start_pos) + "," + cu.getColumnNumber(start_pos);
    }

    private ASTNode getBranchTargetNode(ASTNode branch_node) {
	ASTNode target_node = null;
	if (branch_node instanceof Block) {
	    Block block = (Block) branch_node;
	    List stmts = block.statements();
	    if (stmts.isEmpty()) { target_node = block; }
	    else { target_node = (ASTNode) stmts.get(0); }
	}
	else {
	    target_node = branch_node;
	}
	return target_node;
    }

    private LabeledStatement getPHolderLStatement(AST ast, Expression e, String delta_syn_name) {
	//"delta_syn_name" is used to create (1) the label (2) the dummy declaration stmt
	IfStatement pholder_ifstmt = ast.newIfStatement();
	pholder_ifstmt.setExpression(e);
	Block pholder_ifstmt_thenblock = ast.newBlock();
	pholder_ifstmt.setThenStatement(pholder_ifstmt_thenblock);
	//Statement dummystmt = getDummyDeclarationStatement(ast, delta_syn_name);
	VariableDeclarationFragment vdf0 = ast.newVariableDeclarationFragment();
	vdf0.setName(ast.newSimpleName(delta_syn_name));
	vdf0.setInitializer(ast.newNumberLiteral("-1"));
	VariableDeclarationStatement dummystmt = ast.newVariableDeclarationStatement(vdf0);
	pholder_ifstmt_thenblock.statements().add(dummystmt);
	LabeledStatement pholder_lstmt = ast.newLabeledStatement();
	pholder_lstmt.setBody(pholder_ifstmt);
	pholder_lstmt.setLabel(ast.newSimpleName(delta_syn_name)); //Wrap as a label statement
	return pholder_lstmt;
    }
    
    private boolean isEquivalent(Statement s1, Statement s2) {
	if (s1 == null && s2 == null) { return true; }
	else if (s1 == null && s2 != null) { return false; }
	else if (s1 != null && s2 == null) { return false; }
	else { return s1.toString().equals(s2.toString()); }
    }

    private ASTNode createNotE1E2ORE1NotE2(AST ast, Expression e1, Expression e2) {
	InfixExpression new_cond = ast.newInfixExpression();
	InfixExpression not_e1_e2 = ast.newInfixExpression();
	InfixExpression e1_not_e2 = ast.newInfixExpression();
	not_e1_e2.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
	PrefixExpression not_e1 = (PrefixExpression) createNotE(ast, e1);
	PrefixExpression not_e2 = (PrefixExpression) createNotE(ast, e2);
	not_e1_e2.setLeftOperand(not_e1);
	not_e1_e2.setRightOperand((Expression) ASTNode.copySubtree(ast, e2));
	e1_not_e2.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
	e1_not_e2.setLeftOperand((Expression) ASTNode.copySubtree(ast, e1));
	e1_not_e2.setRightOperand(not_e2);
	new_cond.setOperator(InfixExpression.Operator.CONDITIONAL_OR);
	new_cond.setLeftOperand(not_e1_e2);
	new_cond.setRightOperand(e1_not_e2);
	return new_cond;
    }

    private ASTNode createE1ORE2(AST ast, Expression e1, Expression e2) {

	InfixExpression e1ore2 = ast.newInfixExpression();
	e1ore2.setOperator(InfixExpression.Operator.CONDITIONAL_OR);
	e1ore2.setLeftOperand((Expression) ASTNode.copySubtree(ast, e1));
	e1ore2.setRightOperand((Expression) ASTNode.copySubtree(ast, e2));
	return e1ore2;
    }
    
    private ASTNode createNotE1E2(AST ast, Expression e1, Expression e2) {

	InfixExpression e1e2 = ast.newInfixExpression();
	e1e2.setOperator(InfixExpression.Operator.CONDITIONAL_AND);
	e1e2.setLeftOperand((Expression) ASTNode.copySubtree(ast, e1));
	e1e2.setRightOperand((Expression) ASTNode.copySubtree(ast, e2));
	return createNotE(ast, e1e2);
    }

    private ASTNode createNotE(AST ast, Expression e) {

	PrefixExpression note = ast.newPrefixExpression();
	note.setOperator(PrefixExpression.Operator.NOT);
	ParenthesizedExpression pe = ast.newParenthesizedExpression();
	note.setOperand(pe);
	pe.setExpression((Expression) ASTNode.copySubtree(ast, e));
	return note;
    }

    private List<String> getGoalMethods(CompilationUnit cu, List<String> goal_locs) {
	List<String> goal_methods = new ArrayList<String>();
	for (String goal_loc : goal_locs) {
	    String goal_method = null;
	    List<ASTNode> goal_nodes = ASTNodeFinder.find(cu, goal_loc);
	    for (ASTNode goal_node : goal_nodes) {
		ASTNode par = goal_node;
		while (par != null) {
		    if (par instanceof MethodDeclaration) {
			MethodDeclaration md = (MethodDeclaration) par;
			goal_method = md.getName().getIdentifier();
			break;
		    }
		    par = par.getParent();
		}
		if (goal_method != null) {
		    break;
		}
	    }
	    goal_methods.add(goal_method); //goal_method could be NULL.
	}
	return goal_methods;
    }

    private boolean isIdenticalToThenBranch(IfStatement if_stmt, ASTNode node) {
	Statement then_stmt = if_stmt.getThenStatement();
	if (then_stmt instanceof Block) {
	    Block then_block = (Block) then_stmt;
	    List then_stmts = then_block.statements();
	    if (then_stmts.isEmpty() || then_stmts.size() > 1) {
		return false;
	    }
	    else {
		Statement first_stmt = (Statement) then_stmts.get(0);
		return first_stmt.toString().equals(node.toString());
	    }
	}
	else {
	    return then_stmt.toString().equals(node.toString());
	}
    }

    private class LabeledStatementFinder extends ASTVisitor
    {
	String target_label;
	LabeledStatement target_ls;

	public LabeledStatementFinder(String tl) {
	    target_label = tl;
	    target_ls = null;
	}

	public LabeledStatement getLabeledStatement() { return target_ls; }

	@Override public boolean visit(LabeledStatement ls) {

	    if (ls.getLabel().getIdentifier().equals(target_label)) {
		target_ls = ls;
	    }
	    return false;
	}
    }
}
