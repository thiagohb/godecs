package godecs;

import godecs.exceptions.*;
import godecs.sintatico.ASintatico;
import javax.swing.JOptionPane;

public class  GodecsCompiler {

  public static void main(String args[]) {
	String fileName = "";
	if (args.length != 1) {
	  fileName = JOptionPane.showInputDialog(null, "Digite o nome do arquivo", "GodecsCompiler", JOptionPane.PLAIN_MESSAGE);
	}
	else fileName = args[0];
	System.out.println("### GodecsCompiler ###");
	System.out.println("Analisador Sintatico...");
	ASintatico as = new ASintatico();
	as.setFile(fileName);
	try {
		if(as.analisa()) System.out.println("Programa sintaticamente correto!");
	}
	catch (ALexicoError ale) {
		System.err.println("Foram encontrados erros durante a analise lexixa do programa '" + fileName + "'.");
		System.err.println(ale.getMessage());
	}
	catch (ASintaticoError ase) {
		System.err.println("Foram encontrados erros durante a analise sintatica do programa '" + fileName + "'.");
		System.err.println(ase.getMessage());
	}
	System.exit(0);
  }

}