package uk.ac.cam.dr369.learngrammar.model;

import static uk.ac.cam.dr369.learngrammar.model.GenericPos.ADJECTIVE_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.ADVERB_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.COMPARATIVE_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.CONJUNCTION_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.DETERMINER_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.NOUN_COMMON_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.NOUN_GENERAL;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * CLAWS2 POS tagset used by the RASP parser.
 * 
 * @author duncan.roberts
 */
public enum Claws2Pos implements Pos {
	EXLAMATION(								"!",    "punctuation tag - exclamation mark", PUNCTUATION_GENERAL),
	QUOTE(									"\"",   "punctuation tag - quotation marks", PUNCTUATION_GENERAL),
	GENITIVE(								"$",    "germanic genitive marker - (' or 's)", POSSESSIVE_GENERAL),
	FORMULA(								"&FO",  "formula", SYMBOL_GENERAL),
	FOREIGN_WORD(							"&FW",  "foreign word"),
	LEFT_BRACKET(							"(",    "punctuation tag - left bracket", PUNCTUATION_GENERAL),
	RIGHT_BRACKET(							")",    "punctuation tag - right bracket", PUNCTUATION_GENERAL),
	COMMA(									",",    "punctuation tag - comma", PUNCTUATION_GENERAL),
	DASH(									"-",    "punctuation tag - dash", PUNCTUATION_GENERAL),
	NEW_SENTENCE(							"-----","new sentence marker"),
	FULL_STOP(								".",    "punctuation tag - full-stop", PUNCTUATION_GENERAL),
	ELLIPSIS(								"...",  "punctuation tag - ellipsis", PUNCTUATION_GENERAL),
	COLON(									":",    "punctuation tag - colon", PUNCTUATION_GENERAL),
	SEMI_COLON(								";",    "punctuation tag - semi-colon", PUNCTUATION_GENERAL),
	QUESTION_MARK(							"?",    "punctuation tag - question-mark", PUNCTUATION_GENERAL),
	PRONOUN_POSSESSIVE_PRENOMINAL(			"APP$", "possessive pronoun, pre-nominal (my, your, our etc.)", POSSESSIVE_GENERAL),
	ARTICLE(								"AT",   "article (the, no)", DETERMINER_GENERAL),
	ARTICLE_SINGULAR(						"AT1",  "singular article (a, an, every)", DETERMINER_GENERAL),
	BEFORE_CONJUNCTION(						"BCS",  "before-conjunction (in order (that), even (if etc.))"),
	BEFORE_INFINITIVE_MARKER(				"BTO",  "before-infinitive marker (in order, so as (to))"),
	COORDINATING_CONJUNCTION_AND_OR(		"CC",   "coordinating conjunction (and, or)", CONJUNCTION_GENERAL),
	COORDINATING_CONJUNCTION_BUT(			"CCB",  "coordinating conjunction (but)", CONJUNCTION_GENERAL),
	SEMI_COORDINATING_CONJUNCTION(			"CF",   "semi-coordinating conjunction (so, then, yet)", CONJUNCTION_GENERAL),
	SUBORDINATING_CONJUNCTION(				"CS",   "subordinating conjunction (if, because, unless)", CONJUNCTION_GENERAL),
	CONJUNCTION_AS(							"CSA",  "'as' as a conjunction", CONJUNCTION_GENERAL),
	CONJUNCTION_THAN(						"CSN",  "'than' as a conjunction", CONJUNCTION_GENERAL),
	CONJUNCTION_THAT(						"CST",  "'that' as a conjunction", CONJUNCTION_GENERAL),
	CONJUNCTION_WHETHER(					"CSW",  "'whether' as a conjunction", CONJUNCTION_GENERAL),
	AFTER_DETERMINER(						"DA",   "after-determiner (capable of pronominal function) (such, former, same)"),
	AFTER_DETERMINER_SINGULAR(				"DA1",  "singular after-determiner (little, much)"),
	AFTER_DETERMINER_PLURAL(				"DA2",  "plural after-determiner (few, several, many)"),
	AFTER_DETERMINER_PLURAL_COMPARATIVE(	"DA2R", "comparative plural after-determiner (fewer)"),
	AFTER_DETERMINER_COMPARATIVE(			"DAR",  "comparative after-determiner (more, less)"),
	AFTER_DETERMINER_SUPERLATIVE(			"DAT",  "superlative after-determiner (most, least)"),
	BEFORE_DETERMINER(						"DB",   "before-determiner (capable of pronominal function) (all, half)"),
	BEFORE_DETERMINER_PLURAL(				"DB2",  "plural before-determiner (capable of pronominal function) (eg. both)"),
	DETERMINER(								"DD",   "determiner  (capable of pronominal function) (any, some)", DETERMINER_GENERAL),
	DETERMINER_SINGULAR(					"DD1",  "singular determiner (this, that, another)", DETERMINER_GENERAL),
	DETERMINER_PLURAL(						"DD2",  "plural determiner (these, those)", DETERMINER_GENERAL),
	WH_DETERMINER(							"DDQ",  "wh-determiner (which, what)", DETERMINER_GENERAL),
	WH_DETERMINER_GENITIVE(					"DDQ$", "wh-determiner, genitive (whose)", POSSESSIVE_GENERAL, DETERMINER_GENERAL),
	WH_EVER_DETERMINER(						"DDQV", "wh-ever determiner (whichever, whatever)", DETERMINER_GENERAL),
	EXISTENTIAL_THERE(						"EX",   "existential 'there'", PRONOUN_GENERAL),
	PREPOSITION_CONJUNCTION(				"ICS",  "preposition-conjunction (after, before, since, until)", PREPOSITION_GENERAL),
	PREPOSITION_FOR(						"IF",   "'for' as a preposition", PREPOSITION_GENERAL),
	PREPOSITION(							"II",   "preposition", PREPOSITION_GENERAL),
	PREPOSITION_OF(							"IO",   "'of' as a preposition", PREPOSITION_GENERAL),
	PREPOSITION_WITH_WITHOUT(				"IW",   "'with' or 'without' as a preposition", PREPOSITION_GENERAL),
	ADJECTIVE_PREDICATIVE(					"JA",   "predicative adjective (tantamount, afraid, asleep)", ADJECTIVE_GENERAL),
	ADJECTIVE_ATTRIBUTATIVE(				"JB",   "attributive adjective (main, chief, utter)", ADJECTIVE_GENERAL),
	ADJECTIVE_ATTRIBUTATIVE_COMPARATIVE(	"JBR",  "attributive comparative adjective (upper, outer)", ADJECTIVE_GENERAL),
	ADJECTIVE_ATTRIBUTATIVE_SUPERLATIVE(	"JBT",  "attributive superlative adjective (utmost, uttermost)", ADJECTIVE_GENERAL),
	ADJECTIVE(								"JJ",   "general adjective", ADJECTIVE_GENERAL),
	ADJECTIVE_COMPARATIVE(					"JJR",  "general comparative adjective (older, better, bigger)", ADJECTIVE_GENERAL, COMPARATIVE_GENERAL),
	ADJECTIVE_SUPERLATIVE(					"JJT",  "general superlative adjective (oldest, best, biggest)", ADJECTIVE_GENERAL, SUPERLATIVE_GENERAL),
	ADJECTIVE_CATENATIVE(					"JK",   "adjective catenative ('able' in 'be able to'; 'willing' in 'be willing to')", ADJECTIVE_GENERAL),
	LEADING_COORDINATOR(					"LE",   "leading co-ordinator ('both' in 'both...and...'; 'either' in 'either... or...')"),
	CARDINAL_NUMBER(						"MC",   "cardinal number neutral for number (two, three...)", NUMERIC_GENERAL),
	CARDINAL_NUMBER_GENITIVE(				"MC$",  "genitive cardinal number, neutral for number (10's)", POSSESSIVE_GENERAL, NUMERIC_GENERAL),
	HYPHENATED_NUMBER(						"MC-MC","hyphenated number 40-50, 1770-1827)", NUMERIC_GENERAL),
	CARDINAL_NUMBER_SINGULAR(				"MC1",  "singular cardinal number (one)", NUMERIC_GENERAL),
	CARDINAL_NUMBER_PLURAL(					"MC2",  "plural cardinal number (tens, twenties)", NUMERIC_GENERAL),
	ORDINAL_NUMBER(							"MD",   "ordinal number (first, 2nd, next, last)", NUMERIC_GENERAL),
	FRACTION(								"MF",   "fraction, neutral for number (quarters, two-thirds)", NUMERIC_GENERAL),
	NOUN_CITED_WORD_PLURAL(					"NC2",  "plural cited word ('ifs' in 'two ifs and a but')", NOUN_PLURAL_GENERAL),
	NOUN_DIRECTIONAL_SINGULAR(				"ND1",  "singular noun of direction (north, southeast)", NOUN_SINGULAR_GENERAL),
	NOUN_COMMON(							"NN",   "common noun, neutral for number (sheep, cod)", NOUN_COMMON_GENERAL),
	NOUN_COMMON_SINGULAR(					"NN1",  "singular common noun (book, girl)", NOUN_COMMON_GENERAL, NOUN_SINGULAR_GENERAL),
	NOUN_COMMON_SINGULAR_GENITIVE(			"NN1$", "genitive singular common noun (domini)", POSSESSIVE_GENERAL, NOUN_COMMON_GENERAL, NOUN_SINGULAR_GENERAL),
	NOUN_COMMON_PLURAL(						"NN2",  "plural common noun (books, girls)", NOUN_COMMON_GENERAL, NOUN_PLURAL_GENERAL),
	NOUN_ORGANISATION(						"NNJ",  "organization noun, neutral for number (department, council, committee)", NOUN_GENERAL),
	NOUN_ORGANISATION_SINGULAR(				"NNJ1", "singular organization noun (Assembly, commonwealth)", NOUN_SINGULAR_GENERAL),
	NOUN_ORGANISATION_PLURAL(				"NNJ2", "plural organization noun (governments, committees)", NOUN_PLURAL_GENERAL),
	NOUN_LOCATIVE(							"NNL",  "locative noun, neutral for number (Is.)", NOUN_GENERAL),
	NOUN_LOCATIVE_SINGULAR(					"NNL1", "singular locative noun (street, Bay)", NOUN_SINGULAR_GENERAL),
	NOUN_LOCATIVE_PLURAL(					"NNL2", "plural locative noun (islands, roads)", NOUN_PLURAL_GENERAL),
	NOUN_NUMERAL(							"NNO",  "numeral noun, neutral for number (dozen, thousand)", NOUN_GENERAL, NUMERIC_GENERAL),
	NOUN_NUMERAL_SINGULAR(					"NNO1", "singular numeral noun (no known examples)", NOUN_SINGULAR_GENERAL, NUMERIC_GENERAL),
	NOUN_NUMERAL_PLURAL(					"NNO2", "plural numeral noun (hundreds, thousands)", NOUN_PLURAL_GENERAL, NUMERIC_GENERAL),
	NOUN_OF_STYLE(							"NNS",  "noun of style, neutral for number (no known examples)", NOUN_GENERAL),
	NOUN_OF_STYLE_SINGULAR(					"NNS1", "singular noun of style (president, rabbi)", NOUN_SINGULAR_GENERAL),
	NOUN_OF_STYLE_PLURAL(					"NNS2", "plural noun of style (presidents, viscounts)", NOUN_PLURAL_GENERAL),
	AFTER_TITLE_ABBREVIATION(				"NNSA1","following noun of style or title, abbreviatory (M.A.)"),
	AFTER_PLURAL_TITLE_ABBREVIATION(		"NNSA2","following plural noun of style or title, abbreviatory"),
	BEFORE_TITLE_ABBREVIATION(				"NNSB", "preceding noun of style or title, abbr. (Rt. Hon.)"),
	BEFORE_SINGULAR_TITLE_ABBREVIATION(		"NNSB1","preceding sing. noun of style or title, abbr. (Prof.)"),
	BEFORE_PLURAL_TITLE_ABBREVIATION(		"NNSB2","preceding plur. noun of style or title, abbr. (Messrs.)"),
	NOUN_TEMPORAL(							"NNT",  "temporal noun, neutral for number (no known examples)", NOUN_GENERAL),
	NOUN_TEMPORAL_SINGULAR(					"NNT1", "singular temporal noun (day, week, year)", NOUN_SINGULAR_GENERAL),
	NOUN_TEMPORAL_PLURAL(					"NNT2", "plural temporal noun (days, weeks, years)", NOUN_PLURAL_GENERAL),
	MEASUREMENT(							"NNU",  "unit of measurement, neutral for number (in., cc.)", SYMBOL_GENERAL),
	MEASUREMENT_SINGULAR(					"NNU1", "singular unit of measurement (inch, centimetre)", SYMBOL_GENERAL),
	MEASUREMENT_PLURAL(						"NNU2", "plural unit of measurement (inches, centimetres)", SYMBOL_GENERAL),
	NOUN_PROPER(							"NP",   "proper noun, neutral for number (Indies, Andes)", NOUN_PROPER_GENERAL),
	NOUN_PROPER_SINGULAR(					"NP1",  "singular proper noun (London, Jane, Frederick)", NOUN_PROPER_GENERAL, NOUN_SINGULAR_GENERAL),
	NOUN_PROPER_PLURAL(						"NP2",  "plural proper noun (Browns, Reagans, Koreas)", NOUN_PROPER_GENERAL, NOUN_PLURAL_GENERAL),
	NOUN_WEEKDAY_SINGULAR(					"NPD1", "singular weekday noun (Sunday)", NOUN_SINGULAR_GENERAL),
	NOUN_WEEKDAY_PLURAL(					"NPD2", "plural weekday noun (Sundays)", NOUN_PLURAL_GENERAL),
	NOUN_MONTH_SINGULAR(					"NPM1", "singular month noun (October)", NOUN_SINGULAR_GENERAL),
	NOUN_MONTH_PLURAL(						"NPM2", "plural month noun (Octobers)", NOUN_PLURAL_GENERAL),
	PRONOUN_INDEFINITE(						"PN",   "indefinite pronoun, neutral for number (\"none\")", PRONOUN_GENERAL),
	PRONOUN_INDEFINITE_SINGULAR(			"PN1",  "singular indefinite pronoun (one, everything, nobody)", PRONOUN_GENERAL),
	PRONOUN_WHOM(							"PNQO", "'whom'", PRONOUN_GENERAL),
	PRONOUN_WHO(							"PNQS", "'who'", PRONOUN_GENERAL),
	PRONOUN_WHOSEVER(						"PNQV$","'whosever'", PRONOUN_GENERAL),
	PRONOUN_WHOMSOEVER(						"PNQVO","'whomever' or 'whomsoever'", PRONOUN_GENERAL),
	PRONOUN_WHOSOEVER(						"PNQVS","'whoever' or 'whosoever'", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_INDEFINITE_REFLEXIVE(	"PNX1", "reflexive indefinite pronoun (oneself)", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_NOMINAL_POSSESSIVE(	"PP$",  "nominal possessive personal pronoun (mine, yours)", POSSESSIVE_GENERAL, PRONOUN_GENERAL),
	PRONOUN_PERSONAL_IT(					"PPH1", "'it'", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_HIM_HER(				"PPHO1","'him' or 'her'", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_THEM(					"PPHO2","'them'", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_HE_SHE(				"PPHS1","'he' or 'she'", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_THEY(					"PPHS2","'they'", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_ME(					"PPIO1","'me'", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_US(					"PPIO2","'us'", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_I(						"PPIS1","'I'", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_WE(					"PPIS2","'we'", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_REFLEXIVE_SINGULAR(	"PPX1", "singular reflexive personal pronoun (yourself, itself)", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_REFLEXIVE_PLURAL(		"PPX2", "plural reflexive personal pronoun (yourselves, ourselves)", PRONOUN_GENERAL),
	PRONOUN_PERSONAL_YOU(					"PPY",  "'you'", PRONOUN_GENERAL),
	ADVERB_AFTER_NOMINAL(					"RA",   "adverb, after nominal head (else, galore)", PRONOUN_GENERAL),
	ADVERB_INTRODUCING_APPOSITIONAL(		"REX",  "adverb introducing appositional constructions (namely, viz, eg.)", PRONOUN_GENERAL),
	ADVERB_DEGREE(							"RG",   "degree adverb (very, so, too)", PRONOUN_GENERAL),
	ADVERB_DEGREE_INDEED_ENOUGH(			"RGA",  "post-nominal/adverbial/adjectival degree adverb (indeed, enough)", PRONOUN_GENERAL),
	WH_ADVERB_HOW(							"RGQ",  "wh- degree adverb (how)", PRONOUN_GENERAL),
	WH_EVER_ADVERB_DEGREE(					"RGQV", "wh-ever degree adverb (however)", PRONOUN_GENERAL),
	ADVERB_DEGREE_COMPARATIVE(				"RGR",  "comparative degree adverb (more, less)", ADVERB_GENERAL, COMPARATIVE_GENERAL),
	ADVERB_DEGREE_SUPERLATIVE(				"RGT",  "superlative degree adverb (most, least)", ADVERB_GENERAL, SUPERLATIVE_GENERAL),
	ADVERB_LOCATIVE(						"RL",   "locative adverb (alongside, forward)", ADVERB_GENERAL),
	ADVERB_PREPOSITIONAL_PARTICLE(			"RP",   "prep. adverb; particle (in, up, about)", ADVERB_GENERAL),
	ADVERB_PREPOSITIONAL_CATENATIVE(		"RPK",  "prep. adv., catenative ('about' in 'be about to')", ADVERB_GENERAL),
	ADVERB(									"RR",   "general adverb", ADVERB_GENERAL),
	WH_ADVERB(								"RRQ",  "wh- general adverb (where, when, why, how)", ADVERB_GENERAL),
	WH_EVER_ADVERB(							"RRQV", "wh-ever general adverb (wherever, whenever)", ADVERB_GENERAL),
	ADVERB_COMPARATIVE(						"RRR",  "comparative general adverb (better, longer)", ADVERB_GENERAL, COMPARATIVE_GENERAL),
	ADVERB_SUPERLATIVE(						"RRT",  "superlative general adverb (best, longest)", ADVERB_GENERAL, SUPERLATIVE_GENERAL),
	ADVERB_NOMINAL_TIME(					"RT",   "nominal adverb of time (now, tommorow)", ADVERB_GENERAL),
	INFINITIVE_MARKER(						"TO",   "infinitive marker (to)"),
	INTERJECTION(							"UH",   "interjection (oh, yes, um)"),
	VERB_BE(								"VB0",  "'be'", VERB_GENERAL),
	VERB_WERE(								"VBDR", "'were'", VERB_GENERAL),
	VERB_WAS(								"VBDZ", "'was'", VERB_GENERAL),
	VERB_BEING(								"VBG",  "'being'", VERB_GENERAL),
	VERB_AM(								"VBM",  "'am'", VERB_GENERAL),
	VERB_BEEN(								"VBN",  "'been'", VERB_GENERAL),
	VERB_ARE(								"VBR",  "'are'", VERB_GENERAL),
	VERB_IS(								"VBZ",  "'is'", VERB_GENERAL),
	VERB_DO(								"VD0",  "'do'", VERB_GENERAL),
	VERB_DID(								"VDD",  "'did'", VERB_GENERAL),
	VERB_DOING(								"VDG",  "'doing'", VERB_GENERAL),
	VERB_DONE(								"VDN",  "'done'", VERB_GENERAL),
	VERB_DOES(								"VDZ",  "'does'", VERB_GENERAL),
	VERB_HAVE(								"VH0",  "'have'", VERB_GENERAL),
	VERB_HAD_PAST_TENSE(					"VHD",  "'had' (past tense)", VERB_GENERAL),
	VERB_HAVING(							"VHG",  "'having'", VERB_GENERAL),
	VERB_HAD_PAST_PARTICIPLE(				"VHN",  "'had' (past participle)", VERB_GENERAL),
	VERB_HAS(								"VHZ",  "'has'", VERB_GENERAL),
	VERB_MODAL(								"VM",   "modal auxiliary (can, will, would etc.)", VERB_GENERAL),
	VERB_USED(								"VMK",  "modal catenative (ought, used)", VERB_GENERAL),
	VERB_BASE_FORM(							"VV0",  "base form of lexical verb (give, work etc.)", VERB_GENERAL),
	VERB_PAST_TENSE(						"VVD",  "past tense form of lexical verb (gave, worked etc.)", VERB_GENERAL),
	VERB_GERUND(							"VVG",  "-ing form of lexical verb (giving, working etc.)", VERB_GENERAL),
	VERB_PAST_PARTICIPLE(					"VVN",  "past participle form of lexical verb (given, worked etc.)", VERB_GENERAL),
	VERB_3RD_PERSON_SINGULAR_PRESENT(		"VVZ",  "-s form of lexical verb (gives, works etc.)", VERB_GENERAL),
	VERB_ING_CATENATIVE(					"VVGK", "-ing form in a catenative verb ('going' in 'be going to')", VERB_GENERAL),
	VERB_PAST_PARTICIPLE_CATENATIVE(		"VVNK", "past part. in a catenative verb ('bound' in 'be bound to')", VERB_GENERAL),
	NOT(									"XX",   "'not' or 'n't'"),
	ALPHABET_SINGULAR(						"ZZ1",  "singular letter of the alphabet:'A', 'a', 'B', etc."),
	ALPHABET_PLURAL(						"ZZ2",  "plural letter of the alphabet: 'As', b's, etc. ");

	private final String tagText;
	private final String description;
	private final Set<Pos> parents;
	
	private Claws2Pos(String tagText, String description, Pos... parents) {
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
		throw new IllegalArgumentException("No enum const class "+Claws2Pos.class+" instance with label="+label);
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