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

public class ClassUnderTestInstrumentor
{
    private final static String NAME_SUFFIX = "7au3e";
    private final static String EID_NAME = "eid_"+ NAME_SUFFIX;

    private boolean isCUTInstrumented(CompilationUnit cu) {

	AbstractTypeDeclaration atd = (AbstractTypeDeclaration) cu.types().get(0);
	List bd_objs = atd.bodyDeclarations();
	for (Object bd_obj : bd_objs) {
	    if (bd_obj instanceof FieldDeclaration) {
		FieldDeclaration fd = (FieldDeclaration) bd_obj;
		List fd_fragments = fd.fragments();
		for (Object fd_fragment : fd_fragments) {
		    String fd_name = ((VariableDeclarationFragment) fd_fragment).getName().getIdentifier();
		    if ("oref_map".equals(fd_name) || (EID_NAME.equals(fd_name))) {
			return true;
		    }
		}
	    }
	    else if (bd_obj instanceof MethodDeclaration) {
		MethodDeclaration md = (MethodDeclaration) bd_obj;
		String md_name = md.getName().getIdentifier();
		if ("addToORefMap".equals(md_name) || ("clearORefMap".equals(md_name))) {
		    return true;
		}
	    }
	}
	return false;
    }

    private boolean isMUTInstrumented(MethodDeclaration md) {
	
	String md_name = md.getName().getIdentifier();
	if (md_name.endsWith(NAME_SUFFIX)) {
	    return true;
	}
	else {
	    return false;
	}
    }
    
    public InstrumentedClass getInstrumentedClassContent(String fpath, String mloc) {

	File f = new File(fpath);
	String fctnt = null;
	try { fctnt = FileUtils.readFileToString(f, (String) null); }
	catch (Throwable t) {
	    t.printStackTrace();
	    System.err.println(t);
	}
	if (fctnt == null) { return null; }

	CompilationUnit cu = (CompilationUnit) ASTNodeLoader.getASTNode(fctnt);
	boolean is_CUT_instrumented = isCUTInstrumented(cu); //class_under_test
	AST ast = cu.getAST();
	String package_name = cu.getPackage().getName().toString();
	AbstractTypeDeclaration atd = (AbstractTypeDeclaration) cu.types().get(0);
	
	ASTNode mnode = ASTNodeFinder.find(cu, mloc).get(0);
	if (!(mnode instanceof MethodDeclaration)) {
	    System.err.println("The node found is NOT a method:");
	    System.err.println(fpath);
	    System.err.println(mloc);
	    return null;
	}
	boolean is_MUT_instrumented = isMUTInstrumented((MethodDeclaration) mnode); //method_under_test
	ASTRewrite rw = ASTRewrite.create(ast);

	if (!is_CUT_instrumented) {
	    //add imports
	    boolean contains_list_import = false;
	    boolean contains_arraylist_import = false;
	    boolean contains_map_import = false;
	    boolean contains_hashmap_import = false;
	    List import_list = cu.imports();
	    for (Object import_obj : import_list) {
		ImportDeclaration import_decl = (ImportDeclaration) import_obj;
		String import_decl_str = import_decl.toString().trim();
		if ("import java.util.*;".equals(import_decl_str)) {
		    contains_list_import = true;
		    contains_arraylist_import = true;
		    contains_map_import = true;
		    contains_hashmap_import = true;
		}
		if ("import java.util.List;".equals(import_decl_str)) {
		    contains_list_import = true;
		}
		if ("import java.util.ArrayList;".equals(import_decl_str)) {
		    contains_arraylist_import = true;
		}
		if ("import java.util.Map;".equals(import_decl_str)) {
		    contains_map_import = true;
		}
		if ("import java.util.HashMap;".equals(import_decl_str)) {
		    contains_hashmap_import = true;
		}
	    }
	    ListRewrite lrw0 = rw.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
	    if (!contains_list_import) {
		ImportDeclaration import_decl0 = ast.newImportDeclaration();
		import_decl0.setName(ast.newName(new String[] {"java", "util", "List"}));
		lrw0.insertLast(import_decl0, null);
	    }
	    if (!contains_arraylist_import) {
		ImportDeclaration import_decl0 = ast.newImportDeclaration();
		import_decl0.setName(ast.newName(new String[] {"java", "util", "ArrayList"}));
		lrw0.insertLast(import_decl0, null);
	    }
	    if (!contains_map_import) {
		ImportDeclaration import_decl0 = ast.newImportDeclaration();
		import_decl0.setName(ast.newName(new String[] {"java", "util", "Map"}));
		lrw0.insertLast(import_decl0, null);
	    }
	    if (!contains_hashmap_import) {
		ImportDeclaration import_decl0 = ast.newImportDeclaration();
		import_decl0.setName(ast.newName(new String[] {"java", "util", "HashMap"}));
		lrw0.insertLast(import_decl0, null);
	    }
	    
	    ImportDeclaration import_decl0 = ast.newImportDeclaration();
	    import_decl0.setName(ast.newName(new String[] {"myprinter", "FieldPrinter"}));
	    lrw0.insertLast(import_decl0, null);
	    
	    //Create object-saving static fields
	    VariableDeclarationFragment ref_map_fgmt = ast.newVariableDeclarationFragment();
	    ClassInstanceCreation ref_map_init = ast.newClassInstanceCreation();
	    ref_map_init.setType(ast.newSimpleType(ast.newSimpleName("HashMap")));
	    ref_map_fgmt.setInitializer(ref_map_init);
	    ref_map_fgmt.setName(ast.newSimpleName("oref_map"));
	    FieldDeclaration ref_map_fd = ast.newFieldDeclaration(ref_map_fgmt);
	    ref_map_fd.setType(ast.newSimpleType(ast.newSimpleName("Map")));
	    ref_map_fd.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
	    ref_map_fd.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));	
	    
	    //Create eid static fields
	    //(e.g., "eid=7" means 7th execution of the target method)
	    VariableDeclarationFragment eid_fgmt = ast.newVariableDeclarationFragment();
	    eid_fgmt.setInitializer(ast.newNumberLiteral("0"));
	    eid_fgmt.setName(ast.newSimpleName(EID_NAME));
	    FieldDeclaration eid_fd = ast.newFieldDeclaration(eid_fgmt);
	    eid_fd.setType(ast.newPrimitiveType(PrimitiveType.INT));
	    eid_fd.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
	    eid_fd.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
	    
	    
	    //Create object-saving static methods
	    MethodDeclaration ref_map_md0 = createAddORefMethodDeclaration(ast);
	    MethodDeclaration ref_map_md1 = createClearORefMapMethodDeclaration(ast);
	    
	    String instru_class_name = package_name + "." + atd.getName().getIdentifier();
	    ListRewrite lrw1 = null;
	    if (atd instanceof AnnotationTypeDeclaration) {
		AnnotationTypeDeclaration aatd = (AnnotationTypeDeclaration) atd;
		lrw1 = rw.getListRewrite(aatd, AnnotationTypeDeclaration.BODY_DECLARATIONS_PROPERTY);
	    }
	    else if (atd instanceof EnumDeclaration) {
		EnumDeclaration ed = (EnumDeclaration) atd;
		lrw1 = rw.getListRewrite(ed, EnumDeclaration.BODY_DECLARATIONS_PROPERTY);
		
	    }
	    else if (atd instanceof TypeDeclaration) {
		TypeDeclaration td = (TypeDeclaration) atd;
		lrw1 = rw.getListRewrite(td, TypeDeclaration.BODY_DECLARATIONS_PROPERTY);
	    }
	    
	    lrw1.insertFirst(ref_map_md1, null);
	    lrw1.insertFirst(ref_map_md0, null);
	    lrw1.insertFirst(eid_fd, null);
	    lrw1.insertFirst(ref_map_fd, null);
	}
	
	MethodDeclaration tmd = (MethodDeclaration) mnode;
	String tmd_old_name = tmd.getName().getIdentifier();

        if (!is_MUT_instrumented) {
	CaughtExceptionPrintIntrumenter cepi = new CaughtExceptionPrintIntrumenter(rw);
	tmd.accept(cepi); //Add print statement for each thrown exception
	rw = cepi.getASTRewrite();

	String tmd_new_name = tmd_old_name + "_" + NAME_SUFFIX;
	rw.set(tmd, MethodDeclaration.NAME_PROPERTY, ast.newSimpleName(tmd_new_name), null);
	List tmd_params = tmd.parameters();
	Type tmd_ret = tmd.getReturnType2();
	List tmd_modifiers = tmd.modifiers();
	boolean is_no_return = ((tmd_ret==null) || ("void".equals(tmd_ret.toString()))) ? true : false; //tmd_ret is null => tmd is a constructor
	boolean is_static = false;
	for (Object tmd_modifier_obj : tmd_modifiers) {
	    if (tmd_modifier_obj instanceof Modifier) {
		Modifier tmd_modifier = (Modifier) tmd_modifier_obj;
		if (tmd_modifier.isStatic()) {
		    is_static = true;
		    break;
		}
	    }
	}

	//Clean all modifiers
	List tmd_mods = (List) tmd.getStructuralProperty(MethodDeclaration.MODIFIERS2_PROPERTY);
	ListRewrite tmd_mod_lrw = rw.getListRewrite(tmd, MethodDeclaration.MODIFIERS2_PROPERTY);
	//Remove annotations (e.g., @Override), but do not remove other modifiers (e.g., static)
	for (Object tmd_mod : tmd_mods) {
	    if (tmd_mod instanceof Annotation) {
		tmd_mod_lrw.remove((ASTNode) tmd_mod, null);
	    }
	}
	if (tmd_ret == null) { //add the void return type
	    rw.set(tmd, MethodDeclaration.CONSTRUCTOR_PROPERTY, (new Boolean(false)), null);
	    rw.set(tmd, MethodDeclaration.RETURN_TYPE2_PROPERTY, ast.newPrimitiveType(PrimitiveType.VOID), null);
	}
	
		
	MethodDeclaration tmd0 = (MethodDeclaration) ASTNode.copySubtree(ast, tmd);
	Block tmd0_block = ast.newBlock();
	tmd0.setBody(tmd0_block);
	List tmd0_block_stmts = tmd0_block.statements();

	//=====================
	//System.err.println("\nCHECK THIS!!!\n");
	//System.err.println(tmd0);
	//=====================	

	//Create "Object o_7au3e = null;"
	String o_name = "o_" + NAME_SUFFIX;
	VariableDeclarationFragment vdf = ast.newVariableDeclarationFragment();
	vdf.setName(ast.newSimpleName(o_name));
	vdf.setInitializer(ast.newNullLiteral());
	VariableDeclarationStatement vds = ast.newVariableDeclarationStatement(vdf);
	vds.setType(ast.newSimpleType(ast.newSimpleName("Object")));
	tmd0_block_stmts.add(vds);

	//Create, e.g., "String c_7au3e = \"org.jfree.chart.JFreeChart\";"
	String c_name = "c_" + NAME_SUFFIX;
	VariableDeclarationFragment vdf1 = ast.newVariableDeclarationFragment();
	vdf1.setName(ast.newSimpleName(c_name));
	StringLiteral instru_class_name_sl = ast.newStringLiteral();
	String instru_class_name = package_name + "." + atd.getName().getIdentifier();
	instru_class_name_sl.setLiteralValue(instru_class_name);
	vdf1.setInitializer(instru_class_name_sl);
	VariableDeclarationStatement vds1 = ast.newVariableDeclarationStatement(vdf1);
	vds1.setType(ast.newSimpleType(ast.newSimpleName("String")));
	tmd0_block_stmts.add(vds1);
	
	//Create, e.g., "String msig_7au3e = \"draw(Graphics2D$Rectangle2D$Point2D$ChartRenderingInfo)0\" + eid_7au3e;"
	String msig_name = "msig_" + NAME_SUFFIX;
	VariableDeclarationFragment vdf2 = ast.newVariableDeclarationFragment();
	vdf2.setName(ast.newSimpleName(msig_name));
	InfixExpression ie2 = ast.newInfixExpression();
	ie2.setOperator(InfixExpression.Operator.PLUS);
	StringLiteral tmd_sig_sl = ast.newStringLiteral();
	tmd_sig_sl.setLiteralValue(getMethodSignature(tmd));
	ie2.setLeftOperand(tmd_sig_sl);
	ie2.setRightOperand(ast.newSimpleName(EID_NAME));
	vdf2.setInitializer(ie2);
	VariableDeclarationStatement vds2 = ast.newVariableDeclarationStatement(vdf2);
	vds2.setType(ast.newSimpleType(ast.newSimpleName("String")));
	tmd0_block_stmts.add(vds2);
	
	//Create a try-catch
	TryStatement ts = ast.newTryStatement();
	tmd0_block_stmts.add(ts);

	Block ts_block = ts.getBody();
	List ts_block_stmts = ts_block.statements();
	List ts_cc_list = ts.catchClauses();
		
	//Create the method call
	Expression call_exp = null;
	MethodInvocation mi = ast.newMethodInvocation();
	mi.setName(ast.newSimpleName(tmd_new_name));
	List mi_args = mi.arguments();
	for (Object tmd_param_obj : tmd_params) {
	    SingleVariableDeclaration svd = (SingleVariableDeclaration) tmd_param_obj;
	    mi_args.add(ast.newSimpleName(svd.getName().getIdentifier()));
	}
	if (is_no_return) {
	    call_exp = mi;
	}
	else {
	    Assignment asgn = ast.newAssignment();
	    asgn.setLeftHandSide(ast.newSimpleName(o_name));
	    asgn.setOperator(Assignment.Operator.ASSIGN);
	    asgn.setRightHandSide(mi);
	    call_exp = asgn;
	}

	ts_block_stmts.add(ast.newExpressionStatement(call_exp)); //add method call
	int obj_index = 0;
	if (!is_no_return) { //add return variable print
	    ts_block_stmts.add(generateFieldPrintStatement(ast, ast.newSimpleName(o_name), obj_index));
	    ts_block_stmts.add(generateAddORefMethodInvocation(ast, o_name));
	}
	else {
	    ts_block_stmts.add(generateAddORefMethodInvocation(ast, null));
	}
	obj_index += 1;
	
	if (!is_static) {
	    ts_block_stmts.add(generateFieldPrintStatement(ast, ast.newThisExpression(), obj_index)); //add this print
	    ts_block_stmts.add(generateAddORefMethodInvocation(ast, "this"));
	}
	else {
	    ts_block_stmts.add(generateAddORefMethodInvocation(ast, null));
	}
	obj_index += 1;
	
	for (Object tmd_param_obj : tmd_params) {
	    SingleVariableDeclaration svd = (SingleVariableDeclaration) tmd_param_obj;
	    boolean is_primitive = false;
	    Type svd_type = svd.getType();
	    if (svd_type.isPrimitiveType()) {
		is_primitive = true;
	    }
	    else if (svd_type.isSimpleType()) {
		SimpleType simple_svd_type = (SimpleType) svd_type;
		if ("String".equals(simple_svd_type.toString())) {
		    is_primitive = true;
		}
	    }
	    
	    boolean is_static_or_final = false;
	    List svd_modifiers = svd.modifiers();
	    for (Object svd_modifier_obj : svd_modifiers) {
		if (svd_modifier_obj instanceof Modifier) {
		    Modifier svd_modifier = (Modifier) svd_modifier_obj;
		    if (svd_modifier.isStatic() || svd_modifier.isFinal()) {
			is_static_or_final = true;
			break;
		    }
		}
	    }

	    if (is_primitive || is_static_or_final) {
		ts_block_stmts.add(generateAddORefMethodInvocation(ast, null));		
	    }
	    else {
		String svd_name = svd.getName().getIdentifier();
		ts_block_stmts.add(generateFieldPrintStatement(ast, ast.newSimpleName(svd_name), obj_index)); //add parameter print
		ts_block_stmts.add(generateAddORefMethodInvocation(ast, svd_name));
	    }

	    obj_index += 1;
	}

	CatchClause cc = ast.newCatchClause();
	SingleVariableDeclaration cc_svd = ast.newSingleVariableDeclaration();
	cc_svd.setType(ast.newSimpleType(ast.newSimpleName("Throwable")));
	cc_svd.setName(ast.newSimpleName("t"+NAME_SUFFIX));
	cc.setException(cc_svd);
	Block cc_block = ast.newBlock();
	List cc_block_stmts = cc_block.statements();
	//cc_block_stmts.add(generateFieldPrintStatement(ast, ast.newSimpleName("t"+NAME_SUFFIX), obj_index));
	//obj_index += 1;
	cc_block_stmts.add(generateFieldPrintStatement(ast, ast.newSimpleName("t"+NAME_SUFFIX), 0));
	cc_block_stmts.add(generateAddORefMethodInvocation(ast, "t"+NAME_SUFFIX));
	ThrowStatement cc_throw_stmt = ast.newThrowStatement();
	cc_throw_stmt.setExpression(ast.newSimpleName("t"+NAME_SUFFIX));
	cc_block_stmts.add(cc_throw_stmt);
	cc.setBody(cc_block);
	ts.catchClauses().add(cc);

	Block final_block = ast.newBlock();
	PostfixExpression pe3 = ast.newPostfixExpression();
	pe3.setOperator(PostfixExpression.Operator.INCREMENT);
	pe3.setOperand(ast.newSimpleName(EID_NAME));
	final_block.statements().add(ast.newExpressionStatement(pe3));
	ts.setFinally(final_block);

	if (!is_no_return) { //add return statement
	    ReturnStatement return_stmt = ast.newReturnStatement();
	    tmd0_block_stmts.add(return_stmt);
	    CastExpression cast_e = ast.newCastExpression();
	    cast_e.setExpression(ast.newSimpleName(o_name));
	    cast_e.setType((Type) ASTNode.copySubtree(ast, tmd_ret));
	    return_stmt.setExpression(cast_e);
	}

	//add tmd0 to the class
	StructuralPropertyDescriptor spd = tmd.getLocationInParent();
	if (!(spd instanceof ChildListPropertyDescriptor)) {
	    System.err.println("The structural property descriptor for the following method is not of a list type");
	    return null;
	}
	ChildListPropertyDescriptor clpd = (ChildListPropertyDescriptor) spd;
	ListRewrite lrw = rw.getListRewrite(tmd.getParent(), clpd);
	lrw.insertLast(tmd0, null);
	}
	
	//Get the new content
	Document doc = new Document(fctnt);
	TextEdit tedit = rw.rewriteAST(doc, null);
	try { tedit.apply(doc); }
	catch (Exception e) {
	    System.err.println("Text Edit Apply Error: "+e);
	}

	return new InstrumentedClass(atd.getName().getIdentifier(), tmd_old_name, doc.get());
    }

    private MethodDeclaration createAddORefMethodDeclaration(AST ast) {

	MethodDeclaration md = ast.newMethodDeclaration();
	md.setName(ast.newSimpleName("addToORefMap"));
	List md_params = md.parameters();
	SingleVariableDeclaration param0 = ast.newSingleVariableDeclaration();
	param0.setName(ast.newSimpleName("msig"));
	param0.setType(ast.newSimpleType(ast.newSimpleName("String")));
	SingleVariableDeclaration param1 = ast.newSingleVariableDeclaration();
	param1.setName(ast.newSimpleName("obj"));
	param1.setType(ast.newSimpleType(ast.newSimpleName("Object")));
	md_params.add(param0);
	md_params.add(param1);
	Block md_block = ast.newBlock();
	md.setBody(md_block);
	List md_block_stmts = md_block.statements();

	//Build "List l = (List) oref_map.get(msig);"
	VariableDeclarationFragment vdf0 = ast.newVariableDeclarationFragment();
	vdf0.setName(ast.newSimpleName("l"));
	MethodInvocation mi0 = ast.newMethodInvocation();
	mi0.setName(ast.newSimpleName("get"));
	mi0.setExpression(ast.newSimpleName("oref_map"));
	mi0.arguments().add(ast.newSimpleName("msig"));
	CastExpression ce0 = ast.newCastExpression();
	ce0.setExpression(mi0);
	ce0.setType(ast.newSimpleType(ast.newSimpleName("List")));
	vdf0.setInitializer(ce0);
	VariableDeclarationStatement vds0 = ast.newVariableDeclarationStatement(vdf0);
	vds0.setType(ast.newSimpleType(ast.newSimpleName("List")));
	md_block_stmts.add(vds0);

	//Build "if (l==null) {...}"
	IfStatement if_stmt1 = ast.newIfStatement();
	InfixExpression ie1 = ast.newInfixExpression();
	ie1.setOperator(InfixExpression.Operator.EQUALS);
	ie1.setLeftOperand(ast.newSimpleName("l"));
	ie1.setRightOperand(ast.newNullLiteral());
	if_stmt1.setExpression(ie1);
	Block then_block1 = ast.newBlock();
	if_stmt1.setThenStatement(then_block1);
	//Build "l = new ArrayList();"
	Assignment a1 = ast.newAssignment();
	a1.setOperator(Assignment.Operator.ASSIGN);
	a1.setLeftHandSide(ast.newSimpleName("l"));
	ClassInstanceCreation cic1 = ast.newClassInstanceCreation();
	cic1.setType(ast.newSimpleType(ast.newSimpleName("ArrayList")));
	a1.setRightHandSide(cic1);
	then_block1.statements().add(ast.newExpressionStatement(a1));
	//Build "oref_map.put(msig, l);"
	MethodInvocation mi1 = ast.newMethodInvocation();
	mi1.setName(ast.newSimpleName("put"));
	mi1.setExpression(ast.newSimpleName("oref_map"));
	mi1.arguments().add(ast.newSimpleName("msig"));
	mi1.arguments().add(ast.newSimpleName("l"));
	then_block1.statements().add(ast.newExpressionStatement(mi1));
	md_block_stmts.add(if_stmt1);

	//Build "l.add(obj);"
	MethodInvocation mi2 = ast.newMethodInvocation();
	mi2.setName(ast.newSimpleName("add"));
	mi2.setExpression(ast.newSimpleName("l"));
	mi2.arguments().add(ast.newSimpleName("obj"));
	md_block_stmts.add(ast.newExpressionStatement(mi2));

	md.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
	md.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
	
	return md;
    }

    private MethodDeclaration createClearORefMapMethodDeclaration(AST ast) {

	MethodDeclaration md = ast.newMethodDeclaration();
	md.setName(ast.newSimpleName("clearORefMap"));
	Block md_block = ast.newBlock();
	md.setBody(md_block);
	List md_block_stmts = md_block.statements();

	MethodInvocation mi = ast.newMethodInvocation();
	mi.setName(ast.newSimpleName("clear"));
	mi.setExpression(ast.newSimpleName("oref_map"));
	md_block_stmts.add(ast.newExpressionStatement(mi));

	Assignment asgn = ast.newAssignment();
	asgn.setOperator(Assignment.Operator.ASSIGN);
	asgn.setLeftHandSide(ast.newSimpleName(EID_NAME));
	asgn.setRightHandSide(ast.newNumberLiteral("0"));
	md_block_stmts.add(ast.newExpressionStatement(asgn));

	md.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD));
	md.modifiers().add(ast.newModifier(Modifier.ModifierKeyword.STATIC_KEYWORD));
	
	return md;
    }
    
    private Statement generateFieldPrintStatement(AST ast, Expression arg, int arg_index) {

	MethodInvocation mi = ast.newMethodInvocation();
	mi.setName(ast.newSimpleName("print"));
	mi.setExpression(ast.newSimpleName("FieldPrinter"));
	List mi_args = mi.arguments();
	mi_args.add(arg);
	mi_args.add(ast.newSimpleName(EID_NAME));
	mi_args.add(ast.newSimpleName("c_" + NAME_SUFFIX));
	mi_args.add(ast.newSimpleName("msig_" + NAME_SUFFIX));
	mi_args.add(ast.newNumberLiteral(arg_index+"")); //arg's index in the list saved in ref_map 
	mi_args.add(ast.newNumberLiteral("5")); //max_depth
	return ast.newExpressionStatement(mi);
    }

    private Statement generateSystemPrintStatement(AST ast, Expression arg) {

	MethodInvocation mi = ast.newMethodInvocation();
	mi.setName(ast.newSimpleName("println"));
	Expression e = ast.newQualifiedName(ast.newSimpleName("System"), ast.newSimpleName("out"));
	mi.setExpression(e);
	mi.arguments().add(arg);
	return ast.newExpressionStatement(mi);
    }

    private Statement generateAddORefMethodInvocation(AST ast, String oname) {
	MethodInvocation mi = ast.newMethodInvocation();
	mi.setName(ast.newSimpleName("addToORefMap"));
	mi.arguments().add(ast.newSimpleName("msig_" + NAME_SUFFIX));
	if (oname == null) {
	    mi.arguments().add(ast.newNullLiteral());
	}
	else if (oname.equals("this")) {
	    mi.arguments().add(ast.newThisExpression());
	}
	else {
	    mi.arguments().add(ast.newSimpleName(oname));
	}
	return ast.newExpressionStatement(mi);
    }

    private class CaughtExceptionPrintIntrumenter extends ASTVisitor {

	ASTRewrite rw;	

	public CaughtExceptionPrintIntrumenter(ASTRewrite rw) {
	    this.rw = rw;
	}

	public ASTRewrite getASTRewrite() { return rw; }
	
	@Override public boolean visit(CatchClause cc) {

	    SingleVariableDeclaration svd = cc.getException();
	    AST ast = rw.getAST();
	    Expression printed_e = ast.newSimpleName(svd.getName().getIdentifier());
	    Statement printed_s = generateSystemPrintStatement(ast, printed_e);
	    Block cc_block = cc.getBody();
	    ListRewrite lrw = rw.getListRewrite(cc_block, Block.STATEMENTS_PROPERTY);
	    lrw.insertFirst(printed_s, null);
	    return false;
	}
    }


    private String getMethodSignature(MethodDeclaration md) {

	String mname = md.getName().getIdentifier();
	String marg = null;
	List param_list = md.parameters();
	for (Object param_obj : param_list) {
	    SingleVariableDeclaration param_svd = (SingleVariableDeclaration) param_obj;
	    if (marg == null) { marg = param_svd.getType().toString(); }
	    else { marg += "$" + param_svd.getType().toString(); }
	}
	if (marg == null) { marg = ""; }
	return mname + "(" + marg + ")";
    }
}
