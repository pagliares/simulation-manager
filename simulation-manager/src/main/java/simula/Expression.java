// Arquivo Expression.java
// Implementação das Classes do Grupo Utilitário da Biblioteca de Simulação JAVA
// 26.Mar.1999	Wladimir

package simula;

import java.util.*;
import java.io.*;

public class Expression
{

	private String rpn;						// expressão na forma rpn
	private Stack stack;					// pilha para execução da expressão na forma rpn
	private String errm;					// mensagem de erro de parsing

	private static PushbackReader infix;	// stream que permite lookahead
	private static short token;				// último token
	private static String att;				// atributo do último token retornado
	private static String last;				// último valor (num ou id) obtido pelo lex
	private static boolean err;				// flag erro de parsing
	private static StringBuffer errmsg;		// ref ao buffer suprido pelo objeto

	private static boolean unaryminusok;	// flag para diferenciar op - de -num

	private static final short	ERR	= 0,	// tokens
								ID	= 1,	// (alfa)+(digit)*
								NUM = 2,	// float
								SUM = 3,	// +
								MIN = 4,	// - (op)
								MUL = 5,	// *
								DIV	= 6,	// /
								EQ	= 7,	// =
								NEQ	= 8,	// !=
								LE	= 9,	// <=
								GE	= 10,	// >=
								L	= 11,	// <
								G	= 12,	// >
								AND = 13,	// &
								OR	= 14,	// |
								NOT	= 15,	// !
								LP	= 16,	// (
								RP	= 17,	// )
								EOF	= 18;	// fim da linha

	/**
	 * tabela de variáveis globais da simulação.
	 */
	public static Variables globals;	

	private static void Debug()
	{
		System.out.println("token=" + token + " " + att);
	}
	private static void GetToken()
	// analisador léxico
	// armazena em 'token' tipo do próximo token da expressão em infix; atributo fica em att.
	{
		try			// read throws IOException
		{
		
		char c = (char)infix.read();
			
		while(Character.isWhitespace(c))							// come os espaços
			c = (char)infix.read();

		if(c == (char)-1)
		{ 
			att = "EOF";
			System.out.println("Lex: EOF");
			token = EOF;
			return;
		}

		if((Character.isLetter(c) && c <= 'z') || c == '_')	// somente o ASCII 7bits
		{
			unaryminusok = false;
			StringBuffer buf = new StringBuffer(10);
			do
			{
				buf.append(c);
				c = (char)infix.read();
			} while((Character.isLetterOrDigit(c) && c <= 'z') || c == '_');
			
			last = new String(att = buf.toString());
			if(c != (char)-1)
				infix.unread((int)c);
			System.out.println("Lex: ID " + last);
			token = ID;
			return;
		}

		if(Character.isDigit(c) || c == '.' || (c == '-' && unaryminusok))	// tenta extrair um número
		{
			unaryminusok = false; 
			boolean pread = false;
			boolean eread = false;
			boolean mallow = true;

			StringBuffer buf = new StringBuffer(10);
			
			do
			{
				mallow = false;
				if(c == '.') pread = true;		// mudanças de estado
				if((c == 'e') || (c == 'E'))
				{
					eread = true;
					pread = false;
					mallow = true;
				}

				buf.append(c);
				c = (char)infix.read();

			} while(Character.isDigit(c) || (c == '.' && !pread) 
				|| (c == '-' && mallow) || ((c == 'e' || c == 'E') && !eread));

			if((Character.isLetter(c) || c == '.' || c > '~') && c != (char)-1) // erro léxico
			{
				buf.append(c);
				att = buf.toString();
				token = ERR;
				return;
			}			
			else
			{
				last = new String(att = buf.toString());
				if(c != (char)-1)
					infix.unread((int)c);
				System.out.println("Lex: NUM " + last);
				token = NUM;
				return;
			}
		}

		att = "" + c;
		System.out.println("Lex: OP " + att);
		switch(c)
		{
			case '+': unaryminusok = false; token = SUM; return;
			case '-': token = MIN; return;
			case '*': unaryminusok = false; token = MUL; return;
			case '/': unaryminusok = false; token = DIV; return;
			case '=': unaryminusok = true; token = EQ; return;
			case '&': unaryminusok = true; token = AND; return;
			case '|': unaryminusok = true; token = OR; return;
			case '(': unaryminusok = true; token = LP; return;
			case ')': unaryminusok = false; token = RP; return;
			case '!':	unaryminusok = true;
						c = (char)infix.read(); 
						if(c == '=') 
							token = NEQ;
						else
						{
							infix.unread((int)c); 
							token = NOT;
						}
						return;
			case '<':	unaryminusok = true;
						c = (char)infix.read(); 
						if(c == '=') 
							token = LE;
						else
						{
							infix.unread((int)c); 
							token = L;
						}
						return;
			case '>':	unaryminusok = true;
						c = (char)infix.read(); 
						if(c == '=') 
							token = GE;
						else
						{
							infix.unread((int)c); 
							token = G;
						}
						return;
			default:  token = ERR; return;
		}

		}	// try
		catch(IOException e)
		{
			att = "EOF";
			token = EOF;
			return;
		}
	}

	/**
	 * analisa expressão exp e retorna-a na forma rpn se estiver correta, senão null.
	 */
	protected static synchronized String Parse(String exp, StringBuffer errm)
	{
		if(exp == null)
			return null;
		errmsg = errm;	// liga ao buffer do objeto 
		// cria analisador léxico e ajusta parâmetros
		infix = new PushbackReader(new StringReader(exp));
		unaryminusok = true;			// "permite" números negativos

		// começa análise sintática
		GetToken();						// lê o primeiro token
		String t = C();
		// limpa temporários
		infix = null;
		att = null;
		unaryminusok = false;
		errmsg = null;					// desconecta
		if(!err)
			return t;
		else 
			return null;
	}

	/** 
	 * analisador sintático LL(1) usando a técnica de predictive parsing
	 * Exp lógica
	 */ 
	private static String  C()
	{
		System.out.println("C->");
		Debug();

		switch(token)
		{
			case ID: case LP: case NUM: return Cprime(CT(), null);
			case NOT: GetToken(); return C() + " !";
			case EOF: return "";
			case ERR: errmsg.append("Lex err: ").append(att); err = true; return "";
			default: errmsg.append("C-> err: expected ID, (, NUM but found ").append(att);
				err = true; return "";
		}
	}
	private static String Cprime(String a, String op)
	{
		System.out.println("Cprime->");
		Debug();
		
		switch(token)
		{
			case OR: GetToken(); return a + ((op != null) ? " " + op : "") + Cprime(CT(), "|");			
			case RP: case EOF: return a + ((op != null) ? " " + op : "");
			case ERR: errmsg.append("Lex err: ").append(att); err = true; return "";
			default: errmsg.append("Cprime-> err: expected |, ) or EOF but found ");
				errmsg.append(att); err = true; return "";
		}
	}
	private static String CT()
	{
		System.out.println("CT->");
		Debug();
		
		switch(token)
		{
			case ID: case LP: case NUM: return CTprime(R(), null);	// pode dar pau!!!!!!!!!!
			case ERR: errmsg.append("Lex err: ").append(att); err = true; return "";
			default: errmsg.append("CT-> err: expected ID, (, NUM but found ").append(att);
				err = true; return "";
		}
	}
	private static String CTprime(String a, String op)
	{
		System.out.println("CTprime->");
		Debug();

		switch(token)
		{
			case AND: GetToken(); return " " + a + ((op != null) ? " " + op : "") + CTprime(R(), "&");
			case OR: case RP: case EOF: return " " + a + ((op != null) ? " " + op : "");
			case ERR: errmsg.append("Lex err: ").append(att); err = true; return "";
			default: errmsg.append("CTprime-> err: expected &, |, ) or EOF but found ");
				errmsg.append(att); err = true; return "";
		}
	}

	// Exp rel
	private static String  R()
	{
		System.out.println("R->");
		Debug();

		switch(token)
		{
			case ID: case LP: case NUM: return Rprime(E(), null);
			case EOF: return "";
			case ERR: errmsg.append("Lex err: ").append(att); err = true; return "";
			default: errmsg.append("E-> err: expected ID, (, NUM but found ").append(att);
				err = true; return "";
		}
	}
	private static String Rprime(String a, String op)
	{
		System.out.println("Rprime->");
		Debug();
		
		switch(token)
		{
			case EQ: GetToken(); return a + ((op != null) ? " " + op : "") + Rprime(E(), "=");
			case NEQ: GetToken(); return a + ((op != null) ? " " + op : "") + Rprime(E(), "#");
			case L: GetToken(); return a + ((op != null) ? " " + op : "") + Rprime(E(), "<");
			case LE: GetToken(); return a + ((op != null) ? " " + op : "") + Rprime(E(), "[");
			case GE: GetToken(); return a + ((op != null) ? " " + op : "") + Rprime(E(), "]");
			case G: GetToken(); return a + ((op != null) ? " " + op : "") + Rprime(E(), ">");
			case AND: case OR:
			case RP: case EOF: return a + ((op != null) ? " " + op : "");
			case ERR: errmsg.append("Lex err: ").append(att); err = true; return "";
			default: errmsg.append("Eprime-> err: expected OPREL, ) or EOF but found ");
				errmsg.append(att); err = true; return "";
		}
	}

	// Exp arit
	private static String  E()
	{
		System.out.println("E->");
		Debug();

		switch(token)
		{
			case ID: case LP: case NUM: return Eprime(T(), null);
			case EOF: return "";
			case ERR: errmsg.append("Lex err: ").append(att); err = true; return "";
			default: errmsg.append("E-> err: expected ID, (, NUM but found ").append(att);
				err = true; return "";
		}
	}
	private static String Eprime(String a, String op)
	{
		System.out.println("Eprime->");
		Debug();
		
		switch(token)
		{
			case SUM: GetToken(); return a + ((op != null) ? " " + op : "") + Eprime(T(), "+");
			case MIN: GetToken(); return a + ((op != null) ? " " + op : "") + Eprime(T(), "_");
			case AND: case OR:
			case EQ: case NEQ: case L: case LE: case G: case GE:
			case RP: case EOF: return a + ((op != null) ? " " + op : "");
			case ERR: errmsg.append("Lex err: ").append(att); err = true; return "";
			default: errmsg.append("Eprime-> err: expected OPREL, +, -, ) or EOF but found ");
				errmsg.append(att); err = true; return "";
		}
	}
	private static String T()
	{
		System.out.println("T->");
		Debug();
		
		switch(token)
		{
			case ID: case LP: case NUM: return Tprime(F(), null);
			case ERR: errmsg.append("Lex err: ").append(att); err = true; return "";
			default: errmsg.append("T-> err: expected ID, (, NUM but found ").append(att);
				err = true; return "";
		}
	}
	private static String Tprime(String a, String op)
	{
		System.out.println("Tprime->");
		Debug();

		switch(token)
		{
			case MUL: GetToken(); return " " + a + ((op != null) ? " " + op : "") + Tprime(F(), "*");
			case DIV: GetToken(); return " " + a + ((op != null) ? " " + op : "") + Tprime(F(), "/");
			case AND: case OR:
			case EQ: case NEQ: case L: case LE: case G: case GE:
			case SUM: case MIN: case RP: case EOF: return " " + a + ((op != null) ? " " + op : "");
			case ERR: errmsg.append("Lex err: ").append(att); err = true; return "";
			default: errmsg.append("Tprime-> err: expected *, /, +, -, OPREL, ) or EOF but found ");
				errmsg.append(att); err = true; return "";
		}
	}
	private static String F()
	{
		System.out.println("F->");
		Debug();
		String e;
		
		switch(token)
		{
			case ID: case NUM: GetToken(); return last;
			case LP: GetToken(); e = C();
				if(!err) 
				{
					if(token == RP) 
					{
						GetToken();
						return e;
					} 
					else 
					{
						errmsg.append("F-> err: expected ) but found ").append(att);
						err = true;
					}
				}
				return "";
			case ERR: errmsg.append("Lex err: ").append(att); err = true; return "";
			default: errmsg.append("F-> err: expected ID, (, NUM but found ").append(att);
				err = true; return "";
		}
	}

	/**
	 * constrói expressão constante (para ser usado pelas subclasses)
	 */
	protected Expression(float value)
	{
		stack = null;
		errmsg = null;
		rpn = Float.toString(value);
	}
	
	/**
	 * constrói expressão a partir de uma string contendo-a na forma infixa.
	 */
	public Expression(String infix)
	{
		StringBuffer err = new StringBuffer(30);
		rpn = Parse(infix, err);
		if(rpn == null)
		{
			errm = err.toString();
		}
		else
			stack = new Stack();
	}
	
	/**
	 * determina valor da expressão utilizando parâmetros da entidade e e das variáveis globais.
	 */
	public float Evaluate(Entity e)
	{
		StreamTokenizer is = new StreamTokenizer(new StringReader(rpn));
		is.resetSyntax();
		is.whitespaceChars(0, ' ');
		is.wordChars('0', '9');
		is.wordChars('A', 'Z');
		is.wordChars('a', 'z');
		is.wordChars('-', '.');

		try
		{
		while(is.nextToken() != StreamTokenizer.TT_EOF)
		{
			if(is.ttype == StreamTokenizer.TT_WORD)
			{
				if(Character.isLetter(is.sval.charAt(0)))			// é um identificador
				{
					// faz procura nas propriedades e variáveis globais
					float x = e.GetAttribute(is.sval);
					if(Float.isNaN(x))
					{
						if(globals != null)
							x = globals.Value(is.sval);
					}

					if(Float.isNaN(x))
						return Float.NaN;		
					stack.push(new Float(x));	
				}
				else													// é núemero
				{
					try
					{
						stack.push(new Float(is.sval));
					}
					catch(NumberFormatException n){}
				}
			}
			else
			{
				float x, y;
				y = ((Float)stack.peek()).floatValue();
				stack.pop();
				if((char)is.ttype == '!')	// NOT
				{
					if(y == 0)
						stack.push(new Float(1));
					else
						stack.push(new Float(0));
				}
				else
				{
					x = ((Float)stack.peek()).floatValue();
					stack.pop();
					switch((char)is.ttype)
					{
						case '+': stack.push(new Float(x + y)); break;
						case '_': stack.push(new Float(x - y)); break;
						case '*': stack.push(new Float(x * y)); break;
						case '/': stack.push(new Float(x / y)); break;
						case '=': stack.push(new Float(x == y ? 1 : 0)); break;
						case '<': stack.push(new Float(x < y ? 1 : 0)); break;
						case '>': stack.push(new Float(x > y ? 1 : 0)); break;
						case '[': stack.push(new Float(x <= y ? 1 : 0)); break;
						case ']': stack.push(new Float(x >= y ? 1 : 0)); break;
						case '#': stack.push(new Float(x != y ? 1 : 0)); break;
						case '&': stack.push(new Float((x !=0 && y != 0) ? 1 : 0)); break;
						case '|': stack.push(new Float((x !=0 || y != 0) ? 1 : 0)); break;
					}
				}
			}
		}
		}
		catch(IOException ex){return Float.NaN;}

		float x = ((Float)stack.peek()).floatValue();
		stack.pop();	// limpa stack
		return x;
	}

	/**
	 * para debug
	 */
	public final String GetRPN()
	{
		if(rpn != null)
			return rpn;
		else
			return errm;		
	}
}