package simplenlg.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import simplenlg.features.Category;
import simplenlg.lexicon.DBLexicon;
import simplenlg.lexicon.db.SQLAccessor;
import simplenlg.lexicon.lexicalitems.DerivationalRelation;
import simplenlg.lexicon.lexicalitems.Verb;

/**
 * Tests of the loading of derivational relations in the lexicon (from table
 * "derivations").
 * 
 * @author agatt
 * 
 */
public class NIHLexRelationTest extends TestCase {

	// accessor object -- the default SQLAccessor class
	SQLAccessor accessor;

	// lexicon object -- an instance of Lexicon
	DBLexicon lexicon;

	// db params
	static String DRIVER = "com.mysql.jdbc.Driver";

	// dburl
	static String DBURL = "jdbc:mysql://shallot.csd.abdn.ac.uk:3306/lexicon";

	// db username
	static String USER = "simplenlg";

	// db password
	static String PASS = "simplenlg";

	@Override
	@Before
	/*
	 * * Sets up the accessor and runs it -- takes ca. 26 sec
	 */
	public void setUp() {
		this.accessor = new SQLAccessor(NIHLexiconDBTest.DRIVER,
				NIHLexiconDBTest.DBURL, NIHLexiconDBTest.USER,
				NIHLexiconDBTest.PASS);
		this.lexicon = new DBLexicon();
		this.lexicon.setAccessor(this.accessor);
	}

	@Test
	public void testNominalisation() {
		// Noun n = (Noun) lexicon.getItem(Category.NOUN, "abbreviation");
		Verb v = (Verb) this.lexicon.getItem(Category.VERB, "abbreviate");
		Assert.assertTrue(v.hasDerivationalRelation(
				DerivationalRelation.NOMINALISATION, this.lexicon.getItem(
						Category.NOUN, "abbreviation")));

		Assert.assertEquals(v.getDerivations(
				DerivationalRelation.NOMINALISATION).size(), 1);
	}

}
