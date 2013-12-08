package uk.ac.cam.dr369.learngrammar.parsing;

import java.util.List;

/**
 * Determines the grammatical structure of a given input sentence.
 * @author duncan.roberts
 *
 */
public abstract class SyntacticParser {
	public abstract List<DependencyStructure> toDependencyStructures(String sentences) throws Exception;
	
	public abstract DependencyStructure toDependencyStructure(String sentence) throws Exception;
}