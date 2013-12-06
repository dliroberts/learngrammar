/**
 * 
 */
package uk.ac.cam.dr369.learngrammar.commonality;

import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.Token;

/**
 * Holds either a GR or token reference - i.e. any node or edge in the graph.
 * 
 * @author duncan.roberts
 *
 */
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