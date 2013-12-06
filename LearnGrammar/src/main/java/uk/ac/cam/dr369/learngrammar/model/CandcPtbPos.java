package uk.ac.cam.dr369.learngrammar.model;

import static uk.ac.cam.dr369.learngrammar.model.GenericPos.ADJECTIVE_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.ADVERB_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.COMPARATIVE_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.CONJUNCTION_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.DETERMINER_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.NOUN_COMMON_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.NOUN_PLURAL_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.NOUN_PROPER_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.NOUN_SINGULAR_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.NUMERIC_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.POSSESSIVE_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.PREPOSITION_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.PRONOUN_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.PUNCTUATION_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.SUPERLATIVE_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.SYMBOL_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.VERB_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.VERB_PAST_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.VERB_PRESENT_GENERAL;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * C&C's extended version of the Penn Treebank POS tagset.
 * 
 * @author duncan.roberts
 *
 */
public enum CandcPtbPos implements Pos {
	ADJECTIVE(									"JJ",  "adjective",									ADJECTIVE_GENERAL),
	ADJECTIVE_COMPARATIVE(						"JJR", "comparative adjective",						ADJECTIVE_GENERAL, COMPARATIVE_GENERAL),
	ADJECTIVE_SUPERLATIVE(						"JJS", "superlative adjective",						ADJECTIVE_GENERAL, SUPERLATIVE_GENERAL),
	WH_ADVERB(									"WRB", "wh-adverb", 								ADVERB_GENERAL),
	ADVERB(										"RB",  "adverb",									ADVERB_GENERAL),
	ADVERB_COMPARATIVE(							"RBR", "comparative adverb",						ADVERB_GENERAL, COMPARATIVE_GENERAL),
	ADVERB_SUPERLATIVE(							"RBS", "superlative adverb",						ADVERB_GENERAL, SUPERLATIVE_GENERAL),
	COORDINATING_CONJUNCTION(					"CC",  "coordinating conjunction",					CONJUNCTION_GENERAL),
	PREPOSITION_OR_SUBORDINATING_CONJUNCTION(	"IN",  "preposition, or subordinating conjunction",	CONJUNCTION_GENERAL, PREPOSITION_GENERAL),
	DETERMINER(									"DT",  "determiner",								DETERMINER_GENERAL),
	PREDETERMINER(								"PDT", "predeterminer",								DETERMINER_GENERAL),
	WH_DETERMINER(								"WDT", "wh-determiner",								DETERMINER_GENERAL),
	NOUN_SINGULAR_OR_MASS(						"NN",  "singular or mass noun",						NOUN_SINGULAR_GENERAL, NOUN_COMMON_GENERAL),
	NOUN_SINGULAR_PROPER(						"NNP", "singular proper noun",						NOUN_SINGULAR_GENERAL, NOUN_PROPER_GENERAL),
	NOUN_PLURAL_PROPER(							"NNPS","plural proper noun",						NOUN_PLURAL_GENERAL, NOUN_PROPER_GENERAL),
	NOUN_PLURAL_COMMON(							"NNS", "plural common noun",						NOUN_PLURAL_GENERAL, NOUN_COMMON_GENERAL),
//	NP(											"NP",  "NP"), // Custom C&C addition to PTB POS tag set. one,all,some,many,both,neither. CandcSyntacticParser simply replaces with DT.
	CARDINAL_NUMBER(							"CD",  "cardinal number",							NUMERIC_GENERAL),
	POSSESSIVE_ENDING(							"POS", "possessive ending",							POSSESSIVE_GENERAL),
	AS(											"AS",  "'as'",										PREPOSITION_OR_SUBORDINATING_CONJUNCTION, ADVERB), // Custom C&C addition to PTB POS tag set. Non-specific instances of 'as' (should be RB or IN).
	PRONOUN_PERSONAL(							"PRP", "personal pronoun",							PRONOUN_GENERAL),
	PRONOUN_POSSESSIVE(							"PRP$","possessive pronoun",						PRONOUN_GENERAL, POSSESSIVE_GENERAL),
	WH_PRONOUN(									"WP",  "wh-pronoun", 								PRONOUN_GENERAL),
	POSSESSIVE_WH_PRONOUN(						"WP$", "possessive wh-pronoun",						PRONOUN_GENERAL, POSSESSIVE_GENERAL),
	EXISTENTIAL_THERE(							"EX",  "existential 'there'",						PRONOUN_GENERAL),
	LCB(										"LCB", "left curved bracket",						PUNCTUATION_GENERAL), // Custom C&C addition to PTB POS tag set.
	LRB(										"LRB", "left round bracket",						PUNCTUATION_GENERAL), // Custom C&C addition to PTB POS tag set.
	LIST_ITEM_MARKER(							"LS",  "list item marker",							PUNCTUATION_GENERAL, NUMERIC_GENERAL),
	RIGHT_BRACKET(								"RRB", "right bracket",								PUNCTUATION_GENERAL), // Custom C&C addition to PTB POS tag set.
	COMMA(										",",   "comma", 									PUNCTUATION_GENERAL), // Custom C&C addition to PTB POS tag set. All tokens but "," reassigned by CandcSyntacticParser to more appropriate tag. Tokens: 2 underwriters Wa an section ,
	SEMI_COLON(									";",   "semi-colon",								PUNCTUATION_GENERAL), // Custom C&C addition to PTB POS tag set. Tokens: ;
	ASSORTED_PUNCTUATION(						":",   "assorted non-sentence-terminal punctuation",PUNCTUATION_GENERAL), // Custom C&C addition to PTB POS tag set. Tokens: -- ' ... : -
	SENTENCE_TERMINAL(							".",   "sentence-terminal punctuation",				PUNCTUATION_GENERAL), // Custom C&C addition to PTB POS tag set. Tokens: . ? !
	LEFT_QUOTE(									"LQU", "left quote",								PUNCTUATION_GENERAL), // Custom C&C addition to PTB POS tag set. Tokens: "
	RIGHT_QUOTE(								"RQU", "right quote",								PUNCTUATION_GENERAL), // Custom C&C addition to PTB POS tag set. Tokens: "
	FOREIGN_WORD(								"FW",  "foreign word"/*,							TOKEN*/),
	PARTICLE(									"RP",  "particle"/*,								TOKEN*/),
//	SBAR(										"SBAR","SBAR",										TOKEN), // Custom C&C addition to PTB POS tag set. just one instance: -LCB- . CandcSyntacticParser replaces with LRB.
//	SO(											"SO",  "the words 'so' and 'too'",					TOKEN), // Custom C&C addition to PTB POS tag set. CandcSyntacticParser replaces with RB.
	TO(											"TO",  "'to'"/*, 									TOKEN*/),
	INTERJECTION(								"UH",  "interjection"/*, 							TOKEN*/),
	SYMBOL(										"SYM", "symbol",									SYMBOL_GENERAL),
	CURRENCY(									"$",   "currency",									SYMBOL_GENERAL), // Custom C&C addition to PTB POS tag set. Tokens: M$ A$ C$ US$ HK$ S$ $ NZ$
	HASH(										"#",   "'#' symbol",								SYMBOL_GENERAL), // Custom C&C addition to PTB POS tag set. Tokens: #
	MODAL(										"MD",  "modal verb",								VERB_GENERAL),
	VERB_BASE_FORM(								"VB",  "base form verb",							VERB_GENERAL),
	VERB_PAST_TENSE(							"VBD", "past tense verb",							VERB_PAST_GENERAL),
	VERB_GERUND_OR_PRESENT_PARTICIPLE(			"VBG", "gerund verb",								VERB_GENERAL),
	VERB_PAST_PARTICIPLE(						"VBN", "past participle verb",						VERB_PAST_GENERAL),
	VERB_3SG_PRESENT(							"VBZ", "3rd person singular present verb",			VERB_PRESENT_GENERAL),
	VERB_NON_3SG_PRESENT(						"VBP", "non-3rd person singular present verb",		VERB_PRESENT_GENERAL);
	
	private final String tagText;
	private final String description;
	private final Set<Pos> parents;
	
	private CandcPtbPos(String tagText, String description, Pos... parents) {
		this.tagText = tagText;
		this.description = description;
		this.parents = Collections.unmodifiableSet(new HashSet<Pos>(Arrays.asList(parents)));
	}
	@Override
	public String description() {
		return description;
	}
	@Override
	public String getLabel() {
		return tagText;
	}
	@Override
	public Set<Pos> parents() {
		return parents;
	}
	@Override
	public String toString() {
		return tagText;
	}
	public static Pos valueOfByLabel(String label) {
		for (Pos grt : values()) {
			if (grt.getLabel().equals(label))
				return grt;
		}
		throw new IllegalArgumentException("No enum const class "+CandcPtbPos.class+" instance with label="+label);
	}
	@Override
	public boolean descendentOf(Pos parent) {
		return GenericPos.ancestorOf(this, parent);
	}
	@Override
	public boolean ancestorOf(Pos child) {
		return GenericPos.ancestorOf(child, this);
	}
	@Override
	public Set<Pos> ancestors() {
		return GenericPos.ancestors(this);
	}
	@Override
	public double weight() {
		return 1d;
	}
}