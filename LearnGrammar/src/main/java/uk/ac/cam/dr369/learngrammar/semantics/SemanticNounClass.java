package uk.ac.cam.dr369.learngrammar.semantics;

import static uk.ac.cam.dr369.learngrammar.model.NamedEntityClass.PERSON;

import java.util.List;
import java.util.Queue;

import uk.ac.cam.dr369.learngrammar.model.GenericPos;
import uk.ac.cam.dr369.learngrammar.model.NamedEntityClass;
import uk.ac.cam.dr369.learngrammar.model.Token;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.Pointer;

/**
 * Attempts to divide nouns into broad syntactic classes: things/objects and people/animals.
 * @author duncan.roberts
 */
public enum SemanticNounClass {
	SOMEBODY("animal", "person"), //"animate being", "beast", "brute", "creature", "fauna"),
	SOMETHING;//("artefact", "plant", "flora", "plant life");
	
	private final List<String> wordNetHypernyms;
	
	private SemanticNounClass(String... wordNetHypernyms) {
		this.wordNetHypernyms = ImmutableList.copyOf(wordNetHypernyms);
	}

	public static SemanticNounClass getSemanticNounClass(Token token) {
		if (token.isProperNoun()) {
			// Use named entity recogniser output to identify 'people'
			NamedEntityClass nec = token.getNamedEntityClass();
			return PERSON.equals(nec) ? SOMEBODY : SOMETHING;
		}
		else if (token.isNoun()) { // if WordNet-able
			return getCommonNounClass(token);
		}
		else if (token.pos().descendentOf(GenericPos.PRONOUN_GENERAL) && !token.getLemma().equals("it")) {
			return SOMEBODY;
		}
		return null;
	}

	private static SemanticNounClass getCommonNounClass(Token token) {
		WordnetSemanticAnalyser wnsa = WordnetSemanticAnalyser.getInstance();
		List<IWord> words = wnsa.getWords(token);
		if (words.isEmpty())
			return null;
		IWord word = words.get(0); // first word sense is supposed to be the most common
		
		Queue<ISynsetID> synsetIds = Lists.newLinkedList();
		ISynset synset = word.getSynset();
		
		synsetIds.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM));
		
		while (!synsetIds.isEmpty()) {
			synset = wnsa.getDictionary().getSynset(synsetIds.poll());
			words = synset.getWords();
			for (IWord w : words) {
				if (SOMEBODY.wordNetHypernyms.contains(w.getLemma()))
					return SOMEBODY;
			}
			synsetIds.addAll(synset.getRelatedSynsets(Pointer.HYPERNYM));
		}
		
		return SOMETHING;
	}
}