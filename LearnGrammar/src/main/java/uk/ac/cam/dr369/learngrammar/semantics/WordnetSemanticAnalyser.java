package uk.ac.cam.dr369.learngrammar.semantics;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import simplenlg.lexicon.Lexicon;
import uk.ac.cam.dr369.learngrammar.model.CandcPtbPos;
import uk.ac.cam.dr369.learngrammar.model.GenericPos;
import uk.ac.cam.dr369.learngrammar.model.Pos;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.util.Utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Lists;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IVerbFrame;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;
import edu.mit.jwi.morph.IStemmer;
import edu.mit.jwi.morph.WordnetStemmer;

/**
 * Façade into WordNet.
 * 
 * @author duncan.roberts
 */
public final class WordnetSemanticAnalyser {
	private static final Logger LOGGER = LoggerFactory.getLogger(WordnetSemanticAnalyser.class);

	private static final ImmutableMap<String,String> STEMMER = new ImmutableMap.Builder<String,String>()
			.put("'ll", "will") // I'll
			.put("n't", "not") // couldn't
			.put("'re", "be") // we're
			.put("'ve", "have") // I've
			.put("'s", "be") // it's
			.put("'m", "be") // I'm
			.put("an", "a")
			.build();

	private static final ImmutableTable<String,Pos,String> POS_STEMMER = new ImmutableTable.Builder<String,Pos,String>()
			.put("'d", CandcPtbPos.VERB_PAST_TENSE, "had")
			.put("'d", CandcPtbPos.MODAL, "would")
			.build();

	// TODO work out means of disassociating this class from C&C/any other specific tagset. Maybe somehow reference the generic 'superclass' tags?
	static final ImmutableSet<? extends Pos> POS_NON_ING_INF = ImmutableSet.of(
			CandcPtbPos.VERB_PAST_TENSE,
			CandcPtbPos.VERB_PAST_PARTICIPLE,
			CandcPtbPos.VERB_NON_3SG_PRESENT,
			CandcPtbPos.VERB_3SG_PRESENT);
	
	static final ImmutableSet<? extends Pos> POS_ING = ImmutableSet.of(CandcPtbPos.VERB_GERUND_OR_PRESENT_PARTICIPLE);
	
	static final ImmutableSet<? extends Pos> POS_INF = ImmutableSet.of(CandcPtbPos.VERB_BASE_FORM);
	
	static final ImmutableSet<? extends Pos> POS_NP = ImmutableSet.of(GenericPos.ARGUMENT_GENERAL);

	private static WordnetSemanticAnalyser INSTANCE = null;
	
	// MIT Java WordNet Interface objects
	private IDictionary dictionary;

	private IStemmer stemmer;
	// Aberdeen SimpleNLG objects
	private Lexicon lexicon;
	
	private WordnetSemanticAnalyser(URL dict) {
		dictionary = new Dictionary(dict);
		try {
			dictionary.open();
			stemmer = new WordnetStemmer(dictionary);
			lexicon = new Lexicon();
		}
		catch (IOException ioe) {
			throw new RuntimeException("Cannot open WordNet dictionary!", ioe); // TODO work out why not reported to user. Separate thread?
		}
	}
	
	public static WordnetSemanticAnalyser getInstance(URL dictionary) {
		if (INSTANCE == null) {
			INSTANCE = new WordnetSemanticAnalyser(dictionary);
		}
		return INSTANCE;
	}

	public IDictionary getDictionary() {
		return dictionary;
	}
	
	/**
	 * Find base form of a word - e.g. 'thought' becomes 'think', 'mice' becomes 'mouse', 'cows' becomes 'cow'.
	 */
	public List<String> lemmatise(Token token) {
		if (token.getLemma() != null)
			return Arrays.asList(new String[] {token.getLemma()});
		
		String word = token.getWord();
		if (!token.pos().descendentOf(GenericPos.NOUN_PROPER_GENERAL)) { // if not a proper noun, let's throw away case information for the lemma
			word = word.toLowerCase();
		}
		
		POS wordnetPosTag = toWordnetPosTag(token);
		return lemmatise(word, wordnetPosTag);
	}
	
	/**
	 * Find base form of a word - e.g. 'thought' becomes 'think', 'mice' becomes 'mouse', 'cows' becomes 'cow'.
	 */
	private List<String> lemmatise(String word, POS pos) {
		// The stemmer doesn't stem common apostrophe-abbreviated words, like 'll -> will. So manual hackery:
		if (POS_STEMMER.contains(word, pos))
			return ImmutableList.of(POS_STEMMER.get(word, pos));
		if (STEMMER.containsKey(word))
			return ImmutableList.of(STEMMER.get(word));
		if (pos == null)
			return ImmutableList.of(word);
		
		List<String> words = null;
		try {
			words = stemmer.findStems(word, pos);
		} catch (IllegalArgumentException e) {
			return ImmutableList.of(word);
		}
		if (words.isEmpty()) // no lemma found, so fall back to unlemmatised word
			return ImmutableList.of(word);
		return words;
	}
		
	public List<IWord> getWords(Token token) {
		POS pos = toWordnetPosTag(token);
		IIndexWord idxWord = dictionary.getIndexWord(lemmatise(token.getWord(), pos).get(0), pos);
		List<IWord> words = new ArrayList<IWord>();
		if (idxWord == null)
			return Lists.newArrayList();
		for (IWordID wordId : idxWord.getWordIDs()) {
			words.add(dictionary.getWord(wordId));
		}
		return words;
	}
	
	private static POS toWordnetPosTag(Token token) {
		if (token.isVerb())
			return POS.VERB;
		if (token.isAdverb())
			return POS.ADVERB;
		if (token.isNoun())
			return POS.NOUN;
		if (token.isAdjective())
			return POS.ADJECTIVE;
		return null;
	}
	
	private IWord findSense(List<IWord> candidates, Token original, String sentence) {
		if (!original.isVerb())
			return candidates.get(0); // no filtering if not a verb. TODO use Simplified Lesk?
		
		Set<VerbFrame> originalVerbFrames = WordnetVerbFrame.getAcceptingFrames(original);
		if (originalVerbFrames.size() == 0) {
			return null;
		}
		List<IWord> appropriateSenses = new ArrayList<IWord>();
		int x = 0;
		for (IWord candidateSense : candidates) {
			LOGGER.debug("Considering whether sense {} has an appropriate verb subcat frame...", x);
			List<IVerbFrame> synonymVerbFrames = candidateSense.getVerbFrames();
			Set<String> overlap = getOverlappingVerbFrames(originalVerbFrames, synonymVerbFrames,
					original.getLemma(), "sense "+x);
			if (overlap.size() > 0) {
				appropriateSenses.add(candidateSense);
			}
			x++;
		}
		if (appropriateSenses.size() == 0)
			return null;
		return appropriateSenses.get(0); // TODO use Simplified Lesk?
	}
	
	public IWord findSense(Token token, String sentence) {
		List<IWord> candidates = getWords(token);
		return findSense(candidates, token, sentence);
	}
	
	public List<IWord> getSynonyms(Token token, IWord word) {
		List<IWord> synonyms = new ArrayList<IWord>(word.getSynset().getWords());
		for (ListIterator<IWord> li = synonyms.listIterator(); li.hasNext();) {
			IWord syn = li.next();
			if (syn == word || !isAppropriateSynonym(token, syn))
				li.remove();
		}
		return synonyms;
	}
	
	public Map<ISynset, List<IWord>> getHypernyms(IWord word) {
		return getSubSuper(word, true);
	}
	
	public Map<ISynset, List<IWord>> getHyponyms(IWord word) {
		return getSubSuper(word, false);
	}
	
	private Map<ISynset, List<IWord>> getSubSuper(IWord word, boolean sup) {
		ISynset synset = word.getSynset();
		List<ISynsetID> hypernyms = synset.getRelatedSynsets(sup ? Pointer.HYPERNYM : Pointer.HYPONYM);
		Map<ISynset, List<IWord>> m = new HashMap<ISynset, List<IWord>>();
		for(ISynsetID sid : hypernyms) {
			ISynset ss = dictionary.getSynset(sid);
			List<IWord> words = ss.getWords();
			m.put(ss, words);
		}
		return m;
	}
	
	public Token reinflect(Token original, IWord synonym) {
		int idx = original.getIndex();
		Pos posTag = original.pos();
		String supertag = original.getSupertag();
		String word = inflect(original.pos(), synonym.getLemma());
		return new Token(synonym.getLemma(), null, idx, posTag, supertag, word);
	}
	
	private String inflect(Pos pos, String lemma) {
		// Verbs
		if (pos.equals(CandcPtbPos.VERB_BASE_FORM))
			return lemma;
		if (pos.equals(CandcPtbPos.VERB_PAST_TENSE))
			return lexicon.getPast(lemma);
		if (pos.equals(CandcPtbPos.VERB_PAST_PARTICIPLE))
			return lexicon.getPastParticiple(lemma);
		if (pos.equals(CandcPtbPos.VERB_GERUND_OR_PRESENT_PARTICIPLE))
			return lexicon.getPresentParticiple(lemma);
		if (pos.equals(CandcPtbPos.VERB_3SG_PRESENT))
			return lexicon.getPresent3SG(lemma);
		if (pos.equals(CandcPtbPos.VERB_NON_3SG_PRESENT))
			return lemma;
		// Nouns
		if (pos.descendentOf(GenericPos.NOUN_SINGULAR_GENERAL))
			return lemma;
		if (pos.descendentOf(GenericPos.NOUN_PLURAL_GENERAL))
			return lexicon.getPlural(lemma);
		// Adjectives and adverbs
		if (pos.descendentOf(GenericPos.ADJECTIVE_GENERAL) || pos.descendentOf(GenericPos.ADVERB_GENERAL)) {
			if (pos.descendentOf(GenericPos.COMPARATIVE_GENERAL))
				return lexicon.getComparative(lemma);
			if (pos.descendentOf(GenericPos.SUPERLATIVE_GENERAL))
				return lexicon.getSuperlative(lemma);
			return lemma; // basic form of adverb/adjective
		}
		// TODO maybe do CLAWS2 tags if needed...
		throw new IllegalArgumentException("Unable to infect: "+lemma);
	}
	
	public boolean isAppropriateSynonym(Token original, IWord synonym) {
		// Coarse-grained POS tag matching
		if (!toWordnetPosTag(original).equals(synonym.getPOS()))
			return false;
		
		// Verb subcategorisation frame checking
		if (original.isVerb()) {
			Set<VerbFrame> originalVerbFrames = WordnetVerbFrame.getAcceptingFrames(original);
			List<IVerbFrame> synonymVerbFrames = synonym.getVerbFrames();
			Set<String> overlappingVfStrs = getOverlappingVerbFrames(originalVerbFrames, synonymVerbFrames,
					original.getLemma(), synonym.getLemma());
			if (overlappingVfStrs.size() == 0)
				return false;
		}
		return true;
	}
	
	private Set<String> getOverlappingVerbFrames(Set<VerbFrame> originalVerbFrames, List<IVerbFrame> synonymVerbFrames,
			String originalWord, String replacementWord) {
		List<String> originalVfStrs = Lists.newArrayList();
		List<String> synonymVfStrs = Lists.newArrayList();
		
		for (VerbFrame ovf : originalVerbFrames)
			originalVfStrs.add(ovf.getDescription());
		LOGGER.debug("Verb frames matching the usage of {} in the above sentence: {}", originalWord, originalVfStrs);
		for (IVerbFrame svf : synonymVerbFrames)
			synonymVfStrs.add(svf.getTemplate());
		LOGGER.debug("Verb frames for {}: {}", replacementWord, synonymVfStrs);
		Set<String> overlappingVfStrs = Utils.intersection(originalVfStrs, synonymVfStrs);
		LOGGER.debug("Overlap: {}", overlappingVfStrs);
		return overlappingVfStrs;
	}
}