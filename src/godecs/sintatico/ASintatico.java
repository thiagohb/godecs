package godecs.sintatico;

import godecs.lexico.*;
import godecs.exceptions.*;

public class ASintatico {
	
	private ALexico alexico;
	private Token lookahead;
	
	public ASintatico() {
		alexico = new ALexico();
	}
	
	public void setFile(String file) {
		alexico.setFile(file);
	}
	
	public boolean analisa() throws ALexicoError, ASintaticoError {
		lookahead = alexico.nextToken();
		program();
		return true;				
	}
	
	private void reconhecer(String token){
		if(lookahead.getToken().equals(token)){
			lookahead = alexico.nextToken();
		}
		else throw new ASintaticoError("Esperando '" + token + "' e foi encontrado '" + lookahead.getLexema() + "' em " + lookahead.getLinCol() + ".");
	}
	
	private void program() throws ASintaticoError {
		boolean r = false;
		reconhecer("programa");
		reconhecer("id");
		reconhecer("abre_(");
		identifier_list();
		reconhecer("fecha_)");
		reconhecer("ponto_e_virgula");
		var_declarations();
		compound_statement();
		reconhecer("ponto");
		reconhecer("$EOF");
	}
	
	private void identifier_list() throws ASintaticoError {
		reconhecer("id");
		identifier_list_tail();
	}
	
	private void identifier_list_tail() throws ASintaticoError {
		if (lookahead.getToken().equals("virgula")){
			reconhecer("virgula");
			reconhecer("id");
			identifier_list_tail();
		}
	}
	private void var_declarations() throws ASintaticoError {
		if (lookahead.getToken().equals("var")){
			reconhecer("var");
			type();
			reconhecer("dois_pontos");
			identifier_list();
			reconhecer("ponto_e_virgula");
			declarations_tail();			
		}
	}
	
	private void declarations_tail() throws ASintaticoError {
		if (lookahead.getToken().equals("inteiro") || lookahead.getToken().equals("real") || lookahead.getToken().equals("vetor")) {
			type();
			reconhecer("dois_pontos");
			identifier_list();
			reconhecer("ponto_e_virgula");
			declarations_tail();					    	
		}	
	}
	
	private void type() throws ASintaticoError {
		if (lookahead.getToken().equals("inteiro") || lookahead.getToken().equals("real")) {
			standart_type();
		}	
		else {
			reconhecer("vetor");
			standart_type();
			reconhecer("abre_[");
			reconhecer("num");
			reconhecer("ponto_ponto");
			reconhecer("num");
			reconhecer("fecha_]");
		}
	}
	
	private void standart_type() throws ASintaticoError {
		if (lookahead.getToken().equals("inteiro")) reconhecer("inteiro");
		else reconhecer("real");
	}
				
	private void compound_statement() throws ASintaticoError {
		reconhecer("inicio");
		optional_statements();
		reconhecer("fim");			
	}
	
	private void optional_statements() throws ASintaticoError {
		if (lookahead.getToken().equals("id") || 
		    lookahead.getToken().equals("inicio") ||
		    lookahead.getToken().equals("se") ||
		    lookahead.getToken().equals("enquanto")) statement_list();			
	}
	
	private void statement_list() throws ASintaticoError {
		statement(); 
		optional_statements();		
	}
			
	private void statement() throws ASintaticoError {
		if (lookahead.getToken().equals("id")) {
			variable();
			reconhecer("atribuicao");
			expression();
			reconhecer("ponto_e_virgula");
			return; 
		}
		if (lookahead.getToken().equals("inicio")) {
			compound_statement();
			return; 
		}
		if (lookahead.getToken().equals("se")) {
			reconhecer("se");
			expression();
			reconhecer("entao");
			statement();
			reconhecer("senao");
			statement();
			return; 
		}
		if (lookahead.getToken().equals("enquanto")) {
			reconhecer("enquanto");
			expression();
			reconhecer("faça");
			statement();
			return;
		}
		throw new ASintaticoError("Esperando 'id' ou 'inicio' ou 'se' ou 'entao' e foi encontrado '" + lookahead.getLexema() + "' em " + lookahead.getLinCol() + ".");
	}
	
	private void variable() throws ASintaticoError {
		reconhecer("id");
		array_opt();
	}
	
	private void array_opt() throws ASintaticoError {
		if (lookahead.getToken().equals("abre_[")) {
			reconhecer("abre_[");
			expression();
			reconhecer("fecha_]");
		}
	}
	
	private void expression() throws ASintaticoError {
		simple_expression();
		relational_opt();		
	}
	
	private void relational_opt() throws ASintaticoError {
		boolean relop = false;
		if (!relop && lookahead.getToken().equals("op_igual")) {
			reconhecer("op_igual");
			relop = true;
		} 
		if (!relop && lookahead.getToken().equals("op_diferente")) {
			reconhecer("op_diferente");
			relop = true;
		}
		if (!relop && lookahead.getToken().equals("op_menor")) {
			reconhecer("op_menor");
			relop = true;
		}
		if (!relop && lookahead.getToken().equals("op_menor_igual")) {
			reconhecer("op_menor_igual");
			relop = true;
		}
		if (!relop && lookahead.getToken().equals("op_maior_igual")) {
			reconhecer("op_maior_igual");
			relop = true;
		}
		if (!relop && lookahead.getToken().equals("op_maior")) {
			reconhecer("op_maior");
			relop = true;
		}
		if (relop) simple_expression();
	}
	
	private void simple_expression() throws ASintaticoError {
		term();
		simple_expression_cont();	
	}
	
	private void simple_expression_cont() throws ASintaticoError {
		boolean addop = false;
		if (!addop && lookahead.getToken().equals("op_adicao")) {
			reconhecer("op_adicao");
			addop = true;
		} 
		if (!addop && lookahead.getToken().equals("op_subtracao")) {
			reconhecer("op_subtracao");
			addop = true;
		}
		if (!addop && lookahead.getToken().equals("op_ou")) {
			reconhecer("op_ou");
			addop = true;
		}
		if (addop) {
			term();
			simple_expression_cont();
	    }							
	}
		
	private void term() throws ASintaticoError {
		factor();
		term_cont();
	}
	
	private void term_cont() throws ASintaticoError {
		boolean mulop = false;
		if (!mulop && lookahead.getToken().equals("op_multiplicacao")) {
			reconhecer("op_multiplicacao");
			mulop = true;
		} 
		if (!mulop && lookahead.getToken().equals("op_divisao")) {
			reconhecer("op_divisao");
			mulop = true;
		}
		if (!mulop && lookahead.getToken().equals("op_divisao_inteiro")) {
			reconhecer("op_divisao_inteiro");
			mulop = true;
		} 
		if (!mulop && lookahead.getToken().equals("op_resto")) {
			reconhecer("op_resto");
			mulop = true;
		}		
		if (!mulop && lookahead.getToken().equals("op_e")) {
			reconhecer("op_e");
			mulop = true;
		}
		if (mulop) {
			factor();
			term_cont();
		}	
	}
	
	private void factor() throws ASintaticoError {
		if (lookahead.getToken().equals("op_soma")) reconhecer("op_soma"); 
		else if (lookahead.getToken().equals("op_subtracao")) reconhecer("op_subtracao");   
		oper();
	}
	
	private void oper() throws ASintaticoError {
		if (lookahead.getToken().equals("id")) reconhecer("id");
		else if (lookahead.getToken().equals("num")) reconhecer("num");
		     else {
		     	reconhecer("op_nao");
		     	oper();
		     }
	}
}
