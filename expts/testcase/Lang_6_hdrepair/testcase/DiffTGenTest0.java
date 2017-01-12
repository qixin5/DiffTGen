/*
 * This file was automatically generated by EvoSuite
 * Tue Jan 10 01:09:12 GMT 2017
 */

package org.apache.commons.lang3.text.translate;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.runtime.EvoAssertions.*;
import java.io.OutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityEscaper;
import org.apache.commons.lang3.text.translate.OctalUnescaper;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.evosuite.runtime.mock.java.io.MockFileOutputStream;
import org.evosuite.runtime.mock.java.io.MockPrintWriter;
import org.junit.runner.RunWith;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.lang.reflect.Field;
import org.apache.commons.lang3.reflect.FieldUtils;
import static org.junit.Assert.*;
import myprinter.FieldPrinter;

public class DiffTGenTest0 {

  //Test case number: 0
  /*
   * 9 covered goals:
   * Goal 1. translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V_java.lang.IllegalArgumentException_EXPLICIT
   * Goal 2. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V: Line 76
   * Goal 3. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V: Line 77
   * Goal 4. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V
   * Goal 5. Weak Mutation 10: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:76 - ReplaceComparisonOperator = null -> != null
   * Goal 6. Weak Mutation 12: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:76 - ReplaceComparisonOperator != null -> = null
   * Goal 7. Weak Mutation 13: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:76 - ReplaceComparisonOperator != null -> = null
   * Goal 8. Weak Mutation 14: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:77 - ReplaceConstant - The Writer must not be null -> 
   * Goal 9. Weak Mutation 15: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:77 - ReplaceComparisonOperator == -> !=
   */

  @Test public void test0()  throws Throwable  {
	Object target_obj_7au3e = null;
	boolean not_thrown = false;
	try {
		CharSequenceTranslator.clearORefMap();
		UnicodeEscaper unicodeEscaper0 = new UnicodeEscaper();
		try {
			unicodeEscaper0.translate((CharSequence) null, (Writer) null);
		} catch (IllegalArgumentException e) {
			assertThrownBy(
					"org.apache.commons.lang3.text.translate.CharSequenceTranslator",
					e);
		}
		not_thrown = true;
		fail();
	} catch (Throwable t) {
		if (not_thrown) {
			fail("Throwable Expected!");
		} else {
			List obj_list_7au3e = (List) CharSequenceTranslator.oref_map
					.get("translate(CharSequence$Writer)0");
			target_obj_7au3e = obj_list_7au3e.get(0);
			assertEquals(
					"(E)0,(C)org.apache.commons.lang3.text.translate.CharSequenceTranslator,(MSIG)translate(CharSequence$Writer)0,(I)0",
					"java.lang.IllegalArgumentException: The Writer must not be null",
					((Throwable) target_obj_7au3e).toString());
		}
	}
}

  //Test case number: 1
  /*
   * 2 covered goals:
   * Goal 1. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;ILjava/io/Writer;)I
   * Goal 2. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;ILjava/io/Writer;)I
   */

  

  //Test case number: 2
  /*
   * 19 covered goals:
   * Goal 1. org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;: Line 108
   * Goal 2. org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;: Line 109
   * Goal 3. org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;: Line 110
   * Goal 4. org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;: Line 111
   * Goal 5. org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;
   * Goal 6. org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;
   * Goal 7. Weak Mutation 90: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:108 - ReplaceConstant - 1 -> 0
   * Goal 8. Weak Mutation 91: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:108 - ReplaceArithmeticOperator + -> %
   * Goal 9. Weak Mutation 92: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:108 - ReplaceArithmeticOperator + -> -
   * Goal 10. Weak Mutation 93: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:108 - ReplaceArithmeticOperator + -> *
   * Goal 11. Weak Mutation 94: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:108 - ReplaceArithmeticOperator + -> /
   * Goal 12. Weak Mutation 95: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:109 - ReplaceVariable newArray -> translators
   * Goal 13. Weak Mutation 96: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:109 - ReplaceConstant - 0 -> 1
   * Goal 14. Weak Mutation 97: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:110 - ReplaceVariable translators -> newArray
   * Goal 15. Weak Mutation 98: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:110 - ReplaceConstant - 0 -> 1
   * Goal 16. Weak Mutation 99: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:110 - ReplaceVariable newArray -> translators
   * Goal 17. Weak Mutation 100: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:110 - ReplaceConstant - 1 -> 0
   * Goal 18. Weak Mutation 101: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:110 - ReplaceVariable translators -> newArray
   * Goal 19. Weak Mutation 102: org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;:111 - ReplaceVariable newArray -> translators
   */

  

  //Test case number: 3
  /*
   * 5 covered goals:
   * Goal 1. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;: Line 54
   * Goal 2. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;: Line 55
   * Goal 3. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;
   * Goal 4. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;
   * Goal 5. Weak Mutation 0: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;:54 - ReplaceComparisonOperator != null -> = null
   */

  

  //Test case number: 4
  /*
   * 6 covered goals:
   * Goal 1. org.apache.commons.lang3.text.translate.CharSequenceTranslator.hex(I)Ljava/lang/String;: Line 122
   * Goal 2. org.apache.commons.lang3.text.translate.CharSequenceTranslator.hex(I)Ljava/lang/String;
   * Goal 3. org.apache.commons.lang3.text.translate.CharSequenceTranslator.hex(I)Ljava/lang/String;
   * Goal 4. Weak Mutation 103: org.apache.commons.lang3.text.translate.CharSequenceTranslator.hex(I)Ljava/lang/String;:122 - InsertUnaryOp Negation of codepoint
   * Goal 5. Weak Mutation 104: org.apache.commons.lang3.text.translate.CharSequenceTranslator.hex(I)Ljava/lang/String;:122 - InsertUnaryOp IINC 1 codepoint
   * Goal 6. Weak Mutation 105: org.apache.commons.lang3.text.translate.CharSequenceTranslator.hex(I)Ljava/lang/String;:122 - InsertUnaryOp IINC -1 codepoint
   */

  

  //Test case number: 5
  /*
   * 3 covered goals:
   * Goal 1. with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;_java.lang.NullPointerException_IMPLICIT
   * Goal 2. org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;: Line 108
   * Goal 3. org.apache.commons.lang3.text.translate.CharSequenceTranslator.with([Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;)Lorg/apache/commons/lang3/text/translate/CharSequenceTranslator;
   */

  

  //Test case number: 6
  /*
   * 8 covered goals:
   * Goal 1. translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V_java.lang.IllegalArgumentException_EXPLICIT
   * Goal 2. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V: Line 76
   * Goal 3. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V: Line 77
   * Goal 4. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V
   * Goal 5. Weak Mutation 10: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:76 - ReplaceComparisonOperator = null -> != null
   * Goal 6. Weak Mutation 11: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:76 - ReplaceComparisonOperator != null -> = null
   * Goal 7. Weak Mutation 14: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:77 - ReplaceConstant - The Writer must not be null -> 
   * Goal 8. Weak Mutation 15: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:77 - ReplaceComparisonOperator == -> !=
   */

  //Test case number: 7
  /*
   * 2 covered goals:
   * Goal 1. translate(Ljava/lang/CharSequence;ILjava/io/Writer;)I_java.lang.StringIndexOutOfBoundsException_IMPLICIT
   * Goal 2. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;ILjava/io/Writer;)I
   */

  

  //Test case number: 8
  /*
   * 2 covered goals:
   * Goal 1. translate(Ljava/lang/CharSequence;ILjava/io/Writer;)I_java.lang.NullPointerException_IMPLICIT
   * Goal 2. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;ILjava/io/Writer;)I
   */

  

  //Test case number: 9
  /*
   * 20 covered goals:
   * Goal 1. translate(Ljava/lang/CharSequence;)Ljava/lang/String;_java.lang.IllegalArgumentException_EXPLICIT
   * Goal 2. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;: Line 54
   * Goal 3. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;: Line 58
   * Goal 4. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;: Line 59
   * Goal 5. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V: Line 76
   * Goal 6. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V: Line 77
   * Goal 7. org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;
   * Goal 8. Weak Mutation 0: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;:54 - ReplaceComparisonOperator != null -> = null
   * Goal 9. Weak Mutation 1: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;:58 - ReplaceConstant - 2 -> 0
   * Goal 10. Weak Mutation 2: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;:58 - ReplaceConstant - 2 -> 1
   * Goal 11. Weak Mutation 3: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;:58 - ReplaceConstant - 2 -> -1
   * Goal 12. Weak Mutation 4: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;:58 - ReplaceConstant - 2 -> 3
   * Goal 13. Weak Mutation 5: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;:58 - ReplaceArithmeticOperator * -> +
   * Goal 14. Weak Mutation 6: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;:58 - ReplaceArithmeticOperator * -> %
   * Goal 15. Weak Mutation 7: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;:58 - ReplaceArithmeticOperator * -> -
   * Goal 16. Weak Mutation 8: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;)Ljava/lang/String;:58 - ReplaceArithmeticOperator * -> /
   * Goal 17. Weak Mutation 10: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:76 - ReplaceComparisonOperator = null -> != null
   * Goal 18. Weak Mutation 11: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:76 - ReplaceComparisonOperator != null -> = null
   * Goal 19. Weak Mutation 14: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:77 - ReplaceConstant - The Writer must not be null -> 
   * Goal 20. Weak Mutation 15: org.apache.commons.lang3.text.translate.CharSequenceTranslator.translate(Ljava/lang/CharSequence;Ljava/io/Writer;)V:77 - ReplaceComparisonOperator == -> !=
   */
}