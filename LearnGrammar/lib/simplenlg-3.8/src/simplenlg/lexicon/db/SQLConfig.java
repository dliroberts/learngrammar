package simplenlg.lexicon.db;

public class SQLConfig {

	/**
	 * The Id field in the database for lexical items.
	 */
	static final String ENTRY_ID = "euid";

	// query for complements table
	static final String complementQuery = "select c.transitivity, "
			+ "c.comp1, c.comp1Restr, c.comp2, c.comp2Restr, "
			+ "c.gRestr1, c.gRestr2 from complements c where c.euid=?";

	// query for adj features
	static final String adjFeatureQuery = "select a.positions, a.stative from adjectives a where a.euid=?";

	// query for adverb features
	static final String advFeatureQuery = "select a.positions, a.polarity from adverbs a where a.euid=?";

	// determiner features
	static final String detFeatureQuery = "select d.detType from determiners d where d.euid=?";

	// conjunction features
	static final String conjFeatureQuery = "select c.conjType, c.argType from conjunctions c where c.euid=?";

	// pronoun features
	static final String proFeatureQuery = "select p.gender, p.case1, p.possession, p.quantification, "
			+ "p.pronType from pronouns p where p.euid=?";

	// query for all entries
	static final String allEntries = "select e.euid, e.baseform, e.category, e.hasComp, "
			+ "e.infl, e.agr from entries e";

	// query for entries by category (incomplete): where clause set in the
	// appropriate method
	static final String entriesByCategory = "select e.euid, e.baseform, e.category, e.hasComp, "
			+ "e.infl, e.agr from entries e ";

	// retrieve a single entry by id
	static final String entryByID = "select e.baseform, e.category, e.hasComp, e.infl, e.agr "
			+ "from entries e where e.euid=?";

	// retrieve entries by baseform
	static final String entryByBaseform = "select e.euid, e.category, e.hasComp, e.infl, e.agr "
			+ "from entries e where e.baseform=?";

	// retrieve entries by category and baseform
	static final String entryByCatBaseform = "select e.euid, e.hasComp, e.infl, e.agr "
			+ "from entries e where e.category=? and e.baseform=?";

	// retrieve spelling variants of a word
	static final String wordVariants = "select v.form from variants v where v.euid=?";

	// derivations
	static final String derivations = "select neuid, relation from derivations d where d.euid=?";

	// retrieve verbframes
	static final String verbFrames = "select r.euid, c.class, c.superclass, "
			+ "d.frameid, d.description1, d.description2, d.syntax "
			+ "from vnframeref r left join vnclass c on (c.classid = r.classid) "
			+ "left join vnframedef d on(r.frameid = d.frameid) "
			+ "where r.quality = 1 order by c.classid";

	// path through the syntax node of a verb frame
	static final String SYNTAX_PATH = "SYNTAX/*";

	// Syntactic restrictions node in verbnet xml
	static final String SYNRESTR = "SYNRESTR";

	// Selectional restrictiosn node in verbnet xml
	static final String SELRESTR = "SELRESTR";

	// XMl attribute with the value for a syntax element in a verb frame
	static final String SLOT_ELEMENT_VALUE_ATT = "value";
}
