import java.util.Scanner;
import java.io.*;

public class Analyzer {
	static final int INT_LIT = 10;
	static final int IDENT = 11;
	static final int ASSIGN_OP = 20;
	static final int ADD_OP = 21;
	static final int SUB_OP = 22;
	static final int MULT_OP = 23;
	static final int DIV_OP = 24;
	static final int LEFT_PAREN = 25;
	static final int RIGHT_PAREN = 26;
	static final int LESS_OP = 27;
	static final int GREATER_OP = 28;
	static final int COLON = 29;
	
	static final int LETTER = 0;
	static final int DIGIT = 1;
	static final int UNKNOWN = 99;
	
	static String lexeme;
	static int len;
	static String s;
	static char nextChar;
	static int charClass;
	static int nextToken;
	static Scanner input = null;
	static File file = null; 
	static boolean bool = false;
	
	public static void main(String[] args) {
		file = new File("Test.txt");
		
		try {
			input = new Scanner(file);
		}catch(Exception e) {
			System.out.println("Error reading the file.");
		}
		
		getNextLex();
		program();
		
	}
	
	public static void getNextLex() {
		if(input.hasNext()) {
			if(bool) {
				if(len != s.length()) {
					s= s.substring(len);
					lex();
				}else {
					s = input.next();
					lex();
				}
			}else {
				s = input.next();
				lex();
			}
		}
	}
	
	public static void lex() {
		bool = true;
		
		len = 0;
		
		lexeme = "";
			
		getChar();
		getNonBlank();
			
		switch (charClass) {
		case LETTER:
			getChar();
			addChar();
			while(valid()&&(charClass == LETTER || charClass == DIGIT)) {
				getChar();
				addChar();
			}
			nextToken = IDENT;
			break;
		case DIGIT:
			getChar();
			addChar();
			while(valid()&&(charClass == DIGIT)) {
				getChar();
				addChar();
			}
			nextToken = INT_LIT;
			break;
		case UNKNOWN:
			getChar();
			lookup(nextChar);
			break;
		}
			
		System.out.printf("Next token is: %d, Next lexeme is %s\n", nextToken, lexeme);
			
	}
	
	public static void addChar() {
		lexeme += s.charAt(len);
		len++;
		if(len<s.length()) {
			nextChar = s.charAt(len);
		
			if(Character.isLetter(nextChar)) {
				charClass = LETTER;
			}else if(Character.isDigit(nextChar)) {
				charClass = DIGIT;
			}else {
				charClass = UNKNOWN;
			}
		}
	}
	
	public static void getChar(){
		if(len<s.length()) {
			nextChar = s.charAt(len);
		
			if(Character.isLetter(nextChar)) {
				charClass = LETTER;
			}else if(Character.isDigit(nextChar)) {
				charClass = DIGIT;
			}else {
				charClass = UNKNOWN;
			}
		}
	}
	
	public static void getNonBlank() {
		while((s.charAt(len)+"").isBlank()) {
			len++;
		}
	}
	
	public static int lookup(char c) {
		switch(c) {
		case '(':
			addChar(); nextToken = LEFT_PAREN; break;
		case ')':
			addChar(); nextToken = RIGHT_PAREN; break;
		case '+':
			addChar(); nextToken = ADD_OP; break;
		case '-':
			addChar(); nextToken = SUB_OP; break;
		case '*':
			addChar(); nextToken = MULT_OP; break;
		case '/':
			addChar(); nextToken = DIV_OP; break;
		case '=':
			addChar(); nextToken = ASSIGN_OP; break;
		case '<':
			addChar(); nextToken = LESS_OP; break;
		case '>':
			addChar(); nextToken = GREATER_OP; break;
		case ';':
			addChar(); nextToken = COLON; break;
		default:
			addChar(); nextToken = -1; break;
		}
		
		return nextToken;
	}
	
	public static boolean valid() {
		return len<s.length();
	}
	
	public static void program() {
		System.out.println("Enter <program>");
		
		if(lexeme.equals("program")) {
			getNextLex();
			if(lexeme.equals("begin")) {
				getNextLex();
				statementList();
				
				getNextLex();
				if(lexeme.equals("end")) {
					System.out.println("Program has no errors.");
				}else error();
			}else error();
		}else error();
		
		System.out.println("Exit <program>");
	}
	
	public static void statementList() {
		System.out.println("Enter <statement_list>");
		
		statement();
		
		while(nextToken == COLON) {
			getNextLex();
			statement();
		}
		
		System.out.println("Exit <statement_list>");
	}
	
	public static void statement() {
		System.out.println("Enter <statement>");
		
		if(nextToken != IDENT) {
			error();
		}else {
			if(lexeme.equals("if")) {
				ifStatement();
			}else if(lexeme.equals("loop")) {
				loopStatement();
			}else {
				assignmentStatement();
			}
		}
		
		System.out.println("Exit <statement>");
	}
	
	public static void assignmentStatement() {
		System.out.println("Enter <assignment_statement>");
		
		variable();
		if(nextToken == ASSIGN_OP) {
			getNextLex();
			expression();
		}else error();
		
		System.out.println("Exit <assignment_statement>");
	}
	
	public static void variable() {
		System.out.println("Enter <variable>");
		
		if(nextToken == IDENT) {
			getNextLex();
		}else error();
		
		System.out.println("Exit <variable>");
	}
	
	public static void expression() {
		System.out.println("Enter <expression>");
		term();
		
		while(nextToken == ADD_OP || nextToken == SUB_OP) {
			getNextLex();
			term();
		}
		
		System.out.println("Exit <expression>");
	}
	
	public static void term() {
		System.out.println("Enter <term>");
		factor();
		
		while(nextToken == MULT_OP || nextToken == DIV_OP) {
			getNextLex();
			factor();
		}
		
		System.out.println("Exit <term>");
	}
	
	public static void factor() {
		System.out.println("Enter <factor>");
		
		if(nextToken == IDENT || nextToken == INT_LIT) {
			getNextLex();
		}else {
			if(nextToken == LEFT_PAREN) {
				getNextLex();
				expression();
				if(nextToken == RIGHT_PAREN) {
					getNextLex();
				}else error();
			}else error();
		}
		
		System.out.println("Exit <factor>");
	}
	
	public static void ifStatement() {
		System.out.println("Enter <if_statement>");
		
		if(!lexeme.equals("if")) {
			error();
		}else {
			getNextLex();
			if(nextToken != LEFT_PAREN) {
				error();
			}else {
				getNextLex();
				logicExpression();
				if(nextToken != RIGHT_PAREN) {
					error();
				}else {
					getNextLex();
					if(!lexeme.equals("then")) {
						error();
					}else {
						getNextLex();
						if(lexeme.equals("if")) {
							ifStatement();
						}else {
							statement();
						}
					}
				}
			}
		}
		
		System.out.println("Exit <if_statement>");
	}
	
	public static void logicExpression() {
		System.out.println("Enter <logic_expression>");
		
		variable();
		System.out.println(nextToken);
		if(!(nextToken == LESS_OP || nextToken == GREATER_OP)) {
			error();
		}else {
			getNextLex();
			variable();
		}
		
		System.out.println("Exit <logic_expression>");
	}
	
	public static void loopStatement() {
		System.out.println("Enter <loop_statement>");
		
		if(!lexeme.equals("loop")) {
			error();
		}else {
			getNextLex();
			if(nextToken != LEFT_PAREN) {
				error();
			}else {
				getNextLex();
				logicExpression();
				
				if(nextToken != RIGHT_PAREN) {
					error();
				}else {
					getNextLex();
					statement();
				}
			}
		}
		
		System.out.println("Exit <loop_statement>");
	}
	
	
	public static void error() {
		System.out.println("THERE IS AN ERROR IN THE CODE!");
		System.exit(0);
	}
}