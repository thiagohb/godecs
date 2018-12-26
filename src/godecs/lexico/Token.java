package godecs.lexico;

public class Token {
  private String lexema;
  private String token;
  private int linha, coluna;

  public Token(String lexema, String token,int linha,int coluna) {
	this.lexema = lexema;
	this.token = token;
    this.linha = linha;
    this.coluna = coluna;
  }

  public void setToken(String token) {
	this.token = token;
  }
  
  public String getToken() {
    return token;
  }
  
  public String getLexema() {
    return lexema;
  }
  
  public String getLinCol(){
  	return "(" + linha + "," + coluna + ")";
  }

  public String toString() {
	return "(" + linha + "," + coluna + ") " + lexema + " " + token;
  }

}