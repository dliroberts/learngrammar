/**
 * 
 */
package uk.ac.cam.dr369.learngrammar.commonality;

import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.Token;

public class PathItem {
	private Token token;
	private GrammaticalRelation gr;
	
	PathItem(GrammaticalRelation gr) {
		this.gr = gr;
	}
	PathItem(Token token) {
		this.token = token;
	}
	public Token token() {
		if (token == null)
			throw new NullPointerException();
		return token;
	}
	public GrammaticalRelation gr() {
		if (gr == null)
			throw new NullPointerException();
		return gr;
	}
}