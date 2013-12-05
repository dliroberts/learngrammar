package uk.ac.cam.dr369.learngrammar.semantics;

import java.io.Serializable;

import uk.ac.cam.dr369.learngrammar.model.Token;

public interface VerbFrame extends Serializable {
	public boolean accept(Token verb);
	public String getDescription();
}
