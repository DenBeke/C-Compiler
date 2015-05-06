package Compiler;

import java.util.Vector;

public class BuiltinCode {
	public static Vector<String> generateReadstr() {
		Vector<String> instructions = new Vector<String>();
		
		String loopStart = CodeGenVisitor.getUniqueLabel();
		String loopEnd = CodeGenVisitor.getUniqueLabel();
				
		instructions.add("readstr:");
		instructions.add("ssp 7");
		
		instructions.add(loopStart + ":");	
		instructions.add("lod i 0 6");
		instructions.add("conv i b");
		instructions.add("fjp " + loopEnd);
		instructions.add("lod a 0 5");
		instructions.add("in c");
		instructions.add("sto c");
		instructions.add("lod a 0 5");
		instructions.add("inc a 1");
		instructions.add("str a 0 5");
		instructions.add("lod i 0 6");
		instructions.add("dec i 1");
		instructions.add("str i 0 6");
		instructions.add("ujp " + loopStart);

		instructions.add(loopEnd + ":");
		// null char
		instructions.add("lod a 0 5");
		instructions.add("ldc c 0");
		instructions.add("sto c");
		instructions.add("retp");

		
		return instructions;
	}
	
	public static Vector<String> generateIsdigit() {
		Vector<String> instructions = new Vector<String>();
		
		String nonDigit = CodeGenVisitor.getUniqueLabel();

		instructions.add("isdigit:");
		instructions.add("ssp 6");
		instructions.add("lod c 0 5");
		instructions.add("ldc c '0'");
		instructions.add("geq c");
		instructions.add("lod c 0 5");
		instructions.add("ldc c '9'");
		instructions.add("leq c");
		instructions.add("and");
		instructions.add("fjp " + nonDigit);
		instructions.add("ldc i 1");
		instructions.add("str i 0 0");
		instructions.add("retf");
		
		instructions.add(nonDigit + ":");
		instructions.add("ldc i 0");
		instructions.add("str i 0 0");
		instructions.add("retf");

		return instructions;
	}
		
	public static Vector<String> generateStrcmp() {
		Vector<String> instructions = new Vector<String>();
		
		String loopStart = CodeGenVisitor.getUniqueLabel();
		String loopEnd = CodeGenVisitor.getUniqueLabel();
		
		String nonNullChar = CodeGenVisitor.getUniqueLabel();
		
		instructions.add("strcmp:");
		instructions.add("ssp 7");
		
		instructions.add(loopStart + ":");
		instructions.add("lod a 0 5");
		instructions.add("ind c");		
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		
		instructions.add("equ c");
		instructions.add("fjp " + loopEnd);
		
		instructions.add("lod a 0 5");
		instructions.add("ind c");		
		instructions.add("ldc c 0");
		instructions.add("equ c");
		instructions.add("fjp " + nonNullChar);
		instructions.add("ldc i 1");
		instructions.add("str i 0 0");
		instructions.add("retf");
		
		instructions.add(nonNullChar + ":");
		instructions.add("lod a 0 5");
		instructions.add("inc a 1");
		instructions.add("str a 0 5");
		instructions.add("lod a 0 6");
		instructions.add("inc a 1");
		instructions.add("str a 0 6");
		instructions.add("ujp " + loopStart);

		
		instructions.add(loopEnd + ":");
		instructions.add("ldc i 0");
		instructions.add("str i 0 0");
		instructions.add("retf");

		
		return instructions;
	}

	public static Vector<String> generatePrint() {
		Vector<String> instructions = new Vector<String>();
		
		String loopStart = CodeGenVisitor.getUniqueLabel();
		String loopEnd = CodeGenVisitor.getUniqueLabel();
				
		instructions.add("print:");
		instructions.add("ssp 6");
		
		instructions.add(loopStart + ":");	
		instructions.add("lod a 0 5");
		instructions.add("ind c");		
		instructions.add("ldc c 0");
		instructions.add("neq c");
		instructions.add("fjp " + loopEnd);
		instructions.add("lod a 0 5");
		instructions.add("ind c");	
		instructions.add("out c");
		instructions.add("lod a 0 5");
		instructions.add("inc a 1");
		instructions.add("str a 0 5");
		instructions.add("ujp " + loopStart);

		
		instructions.add(loopEnd + ":");
		instructions.add("retp");

		
		return instructions;
	}
	
	public static Vector<String> generateScanf() {
		Vector<String> instructions = new Vector<String>();
		instructions.addAll(generateParseint());
		
		
		String loopStart = CodeGenVisitor.getUniqueLabel();
		String loopEnd = CodeGenVisitor.getUniqueLabel();
		String nonPercent = CodeGenVisitor.getUniqueLabel();
		String increment = CodeGenVisitor.getUniqueLabel();
		String notD = CodeGenVisitor.getUniqueLabel();
		String notC = CodeGenVisitor.getUniqueLabel();
		String notS = CodeGenVisitor.getUniqueLabel();
		String checkForPercent = CodeGenVisitor.getUniqueLabel();
		String notNumber = CodeGenVisitor.getUniqueLabel();



		// Nr of variadic arguments
		/*instructions.add("lod i 0 5");
		instructions.add("ldc i 1");
		instructions.add("sub i");
		instructions.add("str i 0 5");*/

		/*// counter to loop over string
		instructions.add("ldc i 0");*/
		
		instructions.add("scanf:");
		
		// Use first argument(nr of arguments) as pointer to variadic argument
		instructions.add("lda 0 7");
		instructions.add("str a 0 5");
		
		instructions.add("ldc b f");
		instructions.add("str b 0 0");
		
		// Loop over string
		instructions.add(loopStart + ":");

		// Check for null terminator
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("ldc c 0");
		instructions.add("neq c");
		instructions.add("fjp " + loopEnd);
		
		// Prev char was a %
		instructions.add("lod b 0 0");
		instructions.add("fjp " + checkForPercent);
		// Check for 'd'
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("ldc c 'd'");
		instructions.add("equ c");
		instructions.add("fjp " + notD);
		instructions.add("lod a 0 5");
		instructions.add("ind a");
		instructions.add("in i");
		instructions.add("sto i");
		instructions.add("lod a 0 5");
		instructions.add("inc a 1");
		instructions.add("str a 0 5");
		instructions.add("ujp " + increment);
		instructions.add(notD + ":");
		
		// Check for 'c'
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("ldc c 'c'");
		instructions.add("equ c");
		instructions.add("fjp " + notC);
		instructions.add("lod a 0 5");
		instructions.add("ind a");
		instructions.add("in c");
		instructions.add("sto c");
		instructions.add("lod a 0 5");
		instructions.add("inc a 1");
		instructions.add("str a 0 5");
		instructions.add("ujp " + increment);
		instructions.add(notC + ":");
		
		// Check for 'digit'
		instructions.add("mst 0");
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("cup 1 isdigit");
		instructions.add("conv i b");
		instructions.add("fjp " + notS);
		instructions.add("mst 0");
		instructions.add("lod a 0 5");
		instructions.add("ind a");
		instructions.add("mst 0");
		instructions.add("lda 0 6");
		instructions.add("cup 1 parseint");
		instructions.add("cup 2 readstr");

		instructions.add("lod a 0 5");
		instructions.add("inc a 1");
		instructions.add("str a 0 5");
		instructions.add("ujp " + increment);
		instructions.add(notS + ":");
		
		instructions.add("ujp " + nonPercent);

		
		// Check for %
		instructions.add(checkForPercent + ":");
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("ldc c '%'");
		instructions.add("equ c");
		instructions.add("fjp " + nonPercent);
		instructions.add("ldc b t");
		instructions.add("str b 0 0");
		instructions.add("ujp " + increment);
		
		instructions.add(nonPercent + ":");

		
		instructions.add(increment + ":");
		instructions.add("lod a 0 6");
		instructions.add("inc a 1");
		instructions.add("str a 0 6");
		instructions.add("ujp " + loopStart);
		instructions.add(loopEnd + ":");

		
		instructions.add("retp");



		// Loop over format string
		// -------------
	/*	instructions.add(loopStart + ":");
		instructions.add("lod a 0 6");
		instructions.add("ind c");

		instructions.add("ldc c 0");
		instructions.add("neq c");
		instructions.add("fjp " + loopEnd);

		// Body
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("out c");
		
		
		
		instructions.add("lod a 0 6");
		instructions.add("inc a 1");
		instructions.add("str a 0 6");
		instructions.add("ujp " + loopStart);

		
		instructions.add(loopEnd + ":");


		
		instructions.add("retp");*/

		
		return instructions;
	}
	
	public static Vector<String> generateParseint() {
	Vector<String> instructions = new Vector<String>();
	
	String l1 = CodeGenVisitor.getUniqueLabel();
	String l2 = CodeGenVisitor.getUniqueLabel();
	String l3 = CodeGenVisitor.getUniqueLabel();
	String l4 = CodeGenVisitor.getUniqueLabel();

	instructions.add("parseint:");
	instructions.add("ssp 11");
	instructions.add("ldc i 0");
	instructions.add("str i 0 6");
	instructions.add("ldc i 0");
	instructions.add("str i 0 7");
	instructions.add("lod a 0 5");
	instructions.add("ind a");
	instructions.add("str a 0 8");
	instructions.add(l1 + ":");
	instructions.add("mst 0");
	instructions.add("lod a 0 5");
	instructions.add("ind a");
	instructions.add("ind c");
	instructions.add("cup 1 isdigit");
	instructions.add("conv i b");
	instructions.add("fjp " + l2);
	instructions.add("lda 0 7");
	instructions.add("lod i 0 7");
	instructions.add("conv i i");
	instructions.add("ldc i 1");
	instructions.add("conv i i");
	instructions.add("add i");
	instructions.add("sto i");
	instructions.add("lda 0 7");
	instructions.add("ind i");
	instructions.add("lod a 0 5");
	instructions.add("lod a 0 5");
	instructions.add("ind a");
	instructions.add("conv a i");
	instructions.add("conv i i");
	instructions.add("ldc i 1");
	instructions.add("conv i i");
	instructions.add("add i");
	instructions.add("conv i a");
	instructions.add("sto a");
	instructions.add("lod a 0 5");
	instructions.add("ind a");
	instructions.add("ujp " + l1);
	instructions.add(l2 + ":");
	instructions.add("lod a 0 5");
	instructions.add("lod a 0 8");
	instructions.add("sto a");
	instructions.add("lod a 0 5");
	instructions.add("ind a");
	instructions.add("lod i 0 7");
	instructions.add("str i 0 9");
	instructions.add(l3 + ":");
	instructions.add("lod i 0 9");
	instructions.add("ldc i 0");
	instructions.add("grt i");
	instructions.add("conv b i");
	instructions.add("conv i b");
	instructions.add("fjp " + l4);
	instructions.add("lda 0 6");
	instructions.add("lod i 0 6");
	instructions.add("conv i i");
	instructions.add("mst 0");
	instructions.add("lod a 0 5");
	instructions.add("ind a");
	instructions.add("ind c");
	instructions.add("cup 1 chartoint");
	instructions.add("conv i i");
	instructions.add("mst 0");
	instructions.add("ldc i 10");
	instructions.add("lod i 0 9");
	instructions.add("conv i i");
	instructions.add("ldc i 1");
	instructions.add("conv i i");
	instructions.add("sub i");
	instructions.add("cup 2 pow");
	instructions.add("conv i i");
	instructions.add("mul i");
	instructions.add("conv i i");
	instructions.add("add i");
	instructions.add("sto i");
	instructions.add("lda 0 6");
	instructions.add("ind i");
	instructions.add("lod a 0 5");
	instructions.add("lod a 0 5");
	instructions.add("ind a");
	instructions.add("conv a i");
	instructions.add("conv i i");
	instructions.add("ldc i 1");
	instructions.add("conv i i");
	instructions.add("add i");
	instructions.add("conv i a");
	instructions.add("sto a");
	instructions.add("lod a 0 5");
	instructions.add("ind a");
	instructions.add("lda 0 9");
	instructions.add("lod i 0 9");
	instructions.add("conv i i");
	instructions.add("ldc i 1");
	instructions.add("conv i i");
	instructions.add("sub i");
	instructions.add("sto i");
	instructions.add("lda 0 9");
	instructions.add("ind i");
	instructions.add("ujp " + l3);
	instructions.add(l4 + ":");
	instructions.add("lod i 0 6");
	instructions.add("str i 0 0");
	instructions.add("retf");
		
		return instructions;
	}
	
	public static Vector<String> generateChartoint() {
		Vector<String> instructions = new Vector<String>();
		
		instructions.add("chartoint:");
		instructions.add("ssp 7");
		instructions.add("lod c 0 5");
		instructions.add("conv c i");
		instructions.add("ldc c 48");
		instructions.add("conv c i");
		instructions.add("sub i");
		instructions.add("str i 0 0");
		instructions.add("retf");
		
		return instructions;
		
	}
	
	public static Vector<String> generatePow() {
		Vector<String> instructions = new Vector<String>();
		
		String l1 = CodeGenVisitor.getUniqueLabel();
		String l2 = CodeGenVisitor.getUniqueLabel();
		String l3 = CodeGenVisitor.getUniqueLabel();
		
		instructions.add("pow:");
		instructions.add("ssp 11");
		instructions.add("lod i 0 6");
		instructions.add("ldc i 0");
		instructions.add("equ i");
		instructions.add("conv b i");
		instructions.add("conv i b");
		instructions.add("fjp " + l1);
		instructions.add("ldc i 1");
		instructions.add("str i 0 0");
		instructions.add("retf");
		instructions.add("ujp " + l1);
		instructions.add(l1 + ":");
		instructions.add("lod i 0 5");
		instructions.add("str i 0 7");
		instructions.add("ldc i 1");
		instructions.add("str i 0 8");
		instructions.add(l2 + ":");
		instructions.add("lod i 0 8");
		instructions.add("lod i 0 6");
		instructions.add("les i");
		instructions.add("conv b i");
		instructions.add("conv i b");
		instructions.add("fjp " + l3);
		instructions.add("lda 0 7");
		instructions.add("lod i 0 7");
		instructions.add("lod i 0 5");
		instructions.add("mul i");
		instructions.add("sto i");
		instructions.add("lda 0 7");
		instructions.add("ind i");
		instructions.add("lda 0 8");
		instructions.add("lod i 0 8");
		instructions.add("ldc i 1");
		instructions.add("add i");
		instructions.add("sto i");
		instructions.add("lda 0 8");
		instructions.add("ind i");
		instructions.add("ujp " + l2);
		instructions.add(l3 + ":");
		instructions.add("lod i 0 7");
		instructions.add("str i 0 0");
		instructions.add("retf");
	
		return instructions;
	}
		
		
	public static Vector<String> generatePrintf() {
		Vector<String> instructions = new Vector<String>();
		
		String loopStart = CodeGenVisitor.getUniqueLabel();
		String loopEnd = CodeGenVisitor.getUniqueLabel();
		String nonPercent = CodeGenVisitor.getUniqueLabel();
		String increment = CodeGenVisitor.getUniqueLabel();
		String notD = CodeGenVisitor.getUniqueLabel();
		String notC = CodeGenVisitor.getUniqueLabel();
		String notS = CodeGenVisitor.getUniqueLabel();
		String checkForPercent = CodeGenVisitor.getUniqueLabel();



		// Nr of variadic arguments
		/*instructions.add("lod i 0 5");
		instructions.add("ldc i 1");
		instructions.add("sub i");
		instructions.add("str i 0 5");*/

		/*// counter to loop over string
		instructions.add("ldc i 0");*/
		
		instructions.add("printf:");
		
		// Use first argument(nr of arguments) as pointer to variadic argument
		instructions.add("lda 0 7");
		instructions.add("str a 0 5");
		
		instructions.add("ldc b f");
		instructions.add("str b 0 0");
		
		// Loop over string
		instructions.add(loopStart + ":");

		// Check for null terminator
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("ldc c 0");
		instructions.add("neq c");
		instructions.add("fjp " + loopEnd);
		
		// Prev char was a %
		instructions.add("lod b 0 0");
		instructions.add("fjp " + checkForPercent);
		// Check for 'd'
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("ldc c 'd'");
		instructions.add("equ c");
		instructions.add("fjp " + notD);
		instructions.add("lod a 0 5");
		instructions.add("ind i");
		instructions.add("out i");
		instructions.add("lod a 0 5");
		instructions.add("inc a 1");
		instructions.add("str a 0 5");
		instructions.add("ujp " + increment);
		instructions.add(notD + ":");
		
		// Check for 'c'
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("ldc c 'c'");
		instructions.add("equ c");
		instructions.add("fjp " + notC);
		instructions.add("lod a 0 5");
		instructions.add("ind c");
		instructions.add("out c");
		instructions.add("lod a 0 5");
		instructions.add("inc a 1");
		instructions.add("str a 0 5");
		instructions.add("ujp " + increment);
		instructions.add(notC + ":");
		
		// Check for 's'
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("ldc c 's'");
		instructions.add("equ c");
		instructions.add("fjp " + notS);
		instructions.add("mst 0");
		instructions.add("lod a 0 5");
		instructions.add("ind a");
		instructions.add("cup 1 print");
		instructions.add("lod a 0 5");
		instructions.add("inc a 1");
		instructions.add("str a 0 5");
		instructions.add("ujp " + increment);
		instructions.add(notS + ":");
		
		instructions.add("ujp " + nonPercent);

		
		// Check for %
		instructions.add(checkForPercent + ":");
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("ldc c '%'");
		instructions.add("equ c");
		instructions.add("fjp " + nonPercent);
		instructions.add("ldc b t");
		instructions.add("str b 0 0");
		instructions.add("ujp " + increment);
		
		instructions.add(nonPercent + ":");
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("out c");
		instructions.add("ldc b f");
		instructions.add("str b 0 0");
		
		instructions.add(increment + ":");
		instructions.add("lod a 0 6");
		instructions.add("inc a 1");
		instructions.add("str a 0 6");
		instructions.add("ujp " + loopStart);
		instructions.add(loopEnd + ":");

		
		instructions.add("retp");



		// Loop over format string
		// -------------
	/*	instructions.add(loopStart + ":");
		instructions.add("lod a 0 6");
		instructions.add("ind c");

		instructions.add("ldc c 0");
		instructions.add("neq c");
		instructions.add("fjp " + loopEnd);

		// Body
		instructions.add("lod a 0 6");
		instructions.add("ind c");
		instructions.add("out c");
		
		
		
		instructions.add("lod a 0 6");
		instructions.add("inc a 1");
		instructions.add("str a 0 6");
		instructions.add("ujp " + loopStart);

		
		instructions.add(loopEnd + ":");


		
		instructions.add("retp");*/

		
		return instructions;
	}
	
}