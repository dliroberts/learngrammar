package uk.ac.cam.dr369.learngrammar.parsing;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Determines the grammatical structure of a given input sentence.
 * @author duncan.roberts
 */
public interface SyntacticParser {
	public List<DependencyStructure> toDependencyStructures(String sentences) throws IOException;
	
	public DependencyStructure toDependencyStructure(String sentence) throws IOException;
	
	public boolean useCorpus();
	
	public Collection<DependencyStructure> getCorpus() throws IOException;
}