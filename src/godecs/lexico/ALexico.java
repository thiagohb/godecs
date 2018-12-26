package godecs.lexico;

import java.util.*;
import java.io.*;
import godecs.exceptions.ALexicoError;

public class ALexico {
  private char[] separadores;
  private char[] skip;
  private char next;
  private boolean eof = false;
  private int pos = -1;
  private String line = null;
  private int numLinha = 0, numColuna = 0;
  private Hashtable reservadas;
  private Vector coletor;
  private BufferedReader br;

  public ALexico() {
	separadores = new char[19];
	separadores[0] = '<';
	separadores[1] = '>';
	separadores[2] = '=';
	separadores[3] = '+';
	separadores[4] = '-';
	separadores[5] = '*';
	separadores[6] = '/';
	separadores[7] = '&';
	separadores[8] = '|';
	separadores[9] = '+';
	separadores[10] = '!';
	separadores[11] = '.';
	separadores[12] = ';';
	separadores[13] = ':';
	separadores[14] = ',';
	separadores[15] = '(';
	separadores[16] = ')';
	separadores[17] = '[';
	separadores[18] = ']';

	skip = new char[5];
	skip[0] = ' ';
	skip[1] = '\n';
	skip[2] = '\t';
	skip[3] = '\r';
	skip[4] = '\f';

	reservadas = new Hashtable();
	reservadas.put("programa","programa");
	reservadas.put("var","var");
	reservadas.put("vetor","vetor");
	reservadas.put("inteiro","inteiro");
	reservadas.put("real","real");
	reservadas.put("inicio","inicio");
	reservadas.put("fim","fim");
	reservadas.put("se","se");
	reservadas.put("entao","entao");
	reservadas.put("senao","senao");
	reservadas.put("enquanto","enquanto");
	reservadas.put("faça","faça");
	reservadas.put("faça","faça");
	reservadas.put("div","divisao_inteiro");
	reservadas.put("res","resto");
	reservadas.put("<-","atribuicao");
	reservadas.put("<","op_menor");
	reservadas.put("<=","op_menor_igual");
	reservadas.put("!=","op_diferente");
	reservadas.put(">","op_maior");
	reservadas.put(">=","op_maior_igual");
	reservadas.put("=","op_igual");
	reservadas.put("+","op_adicao");
	reservadas.put("-","op_subtracao");
	reservadas.put("*","op_multiplicacao");
	reservadas.put("/","op_divisao");
	reservadas.put("&","op_e");
	reservadas.put("|","op_ou");
	reservadas.put("!","op_nao");
	reservadas.put("..","ponto_ponto");
	reservadas.put(".","ponto");
	reservadas.put(";","ponto_e_virgula");
	reservadas.put(":","dois_pontos");
	reservadas.put(",","virgula");
	reservadas.put("(","abre_(");
	reservadas.put(")","fecha_)");
	reservadas.put("[","abre_[");
	reservadas.put("]","fecha_]");
	reservadas.put("$EOF","$EOF");
	coletor = new Vector();
  }

  private boolean separador(char c) {
	for (int i = 0;i < separadores.length; i++) if (c == separadores[i]) return true;
	return false;
  }

  private boolean skip(char c) {
  	for (int i = 0;i < skip.length; i++) if (c == skip[i]) return true;
  	return false;
  }

  public void setFile(String file) {
	try {
	  br = new BufferedReader(new FileReader(file));
	}
	catch (IOException ioe) {
	  System.err.println("IOException: setFile()");
	}
	coletor = new Vector();
	proximo();
  }

  private void proximo() {
    try {
      if (pos == -1) {
		line = br.readLine();
		numLinha++;
        if (line == null) {
		  eof = true;
		  next = ' ';
		  return;
	    }
	    else line += "\n";
      }
	  next = line.charAt(++pos);
	  //System.out.println("next:@" + next + "@");
	  if (pos == line.length() - 1) pos = -1;
    }
    catch (IOException ioe) {
	  System.err.println("IOException: proximo()");
	}
  }

  public Token nextToken() throws ALexicoError {
	while (coletor.size() == 0) {
		if ( !eof ) {
	  	numColuna = pos + 1;
	  	if (skip(next)) proximo();
	  	else if (separador(next)) consomeSeparador();
	    	   else if (Character.isLetter(next)) consomePalavra();
	        	    else if (Character.isDigit(next)) consomeNumero();
	            	     else throw new ALexicoError("Símbolo inválido: " + next + " na linha " + numLinha + ".");
    	}
    	else adiciona("$EOF");
	}
	//System.out.println("nextToken" + ((Token) coletor.get(0)));
    return (Token) coletor.remove(0);
  }

  private void consomeSeparador() {
	char c = next;
	proximo();
	switch (c) {
	  case '<': switch (next) {
	              case '-': adiciona("<-"); proximo(); break;
	              case '=': adiciona("<="); proximo(); break;
	              default : adiciona("<");
	            }; break;
	  case '!': if (next == '=') {
		          adiciona("!=");
		          proximo();
			    }
		        else adiciona("!"); break;
	  case '>': if (next == '=') {
		          adiciona(">=");
		          proximo();
			    }
	            else adiciona(">"); break;
	  case '=': adiciona("="); break;
	  case '+': adiciona("+"); break;
	  case '-': adiciona("-"); break;
	  case '*': adiciona("*"); break;
	  case '/': adiciona("/"); break;
	  case '&': adiciona("&"); break;
	  case '|': adiciona("|"); break;
	  case '.': if (next == '.') {
	  		      adiciona("..");
	  		      proximo();
	  		    }
	            else adiciona("."); break;
	  case ';': adiciona(";"); break;
	  case ':': adiciona(":"); break;
	  case ',': adiciona(","); break;
	  case '(': adiciona("("); break;
	  case ')': adiciona(")"); break;
	  case '[': adiciona("["); break;
	  case ']': adiciona("]"); break;
	}
  }

  private void consomePalavra() {
	String tok = "";
	while(Character.isLetter(next) || Character.isDigit(next)) {
	  tok += String.valueOf(next);
	  proximo();
	}
	adiciona(tok);
  }

  private void adiciona(String lex) {
	String tok = null;
	if (reservadas.containsKey(lex)) tok = (String) reservadas.get(lex);
	else if (Character.isDigit(lex.charAt(0))) tok = "num";
	     else tok = "id";
	coletor.add(new Token(lex, tok, numLinha, numColuna));
  }

  private void consomeNumero() throws ALexicoError {
	String tok = "";
	while(Character.isDigit(next)) {
	  tok += String.valueOf(next);
	  proximo();
    }
    if (next == '.') {
	  proximo();
	  if (next == '.') {
		adiciona(tok);
		numColuna = pos;
		adiciona("..");
		proximo();
	  }
	  else {
		tok += ".";
		while(Character.isDigit(next)) {
		  tok += String.valueOf(next);
		  proximo();
        }
        if (next == 'E' || next == 'e') {
		  tok += "E";
		  proximo();
		  if (next == '+' || next == '-') {
			tok += String.valueOf(next);
			proximo();
	      }
	      if (Character.isDigit(next)) {
		    while(Character.isDigit(next)) {
		      tok += String.valueOf(next);
		      proximo();
            }
	      }
	      else throw new ALexicoError("Esperado número no formato ponto flutuante na linha " + numLinha + ".");
		}
		adiciona(tok);
	  }
	}
	else adiciona(tok);
  }

}