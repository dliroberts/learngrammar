package uk.ac.cam.dr369.learngrammar.semantics;

import java.io.Serializable;

import uk.ac.cam.dr369.learngrammar.model.Token;

/**
 * Verb 'signature' - how many complements? Direct or indirect? Animate or inanimate subject?
 * @author duncan.roberts
 */
public interface VerbFrame extends Serializable {
	public boolean accept(Token verb);
	public String getDescription();
}
