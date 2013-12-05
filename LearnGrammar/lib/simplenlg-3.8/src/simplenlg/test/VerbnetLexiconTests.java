package simplenlg.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import simplenlg.lexicon.DBLexicon;
import simplenlg.lexicon.db.SQLAccessor;

/**
 * Tests for loading of verbnet class data from the lexicon database.
 * 
 * @author agatt
 * 
 */
public class VerbnetLexiconTests {

	SQLAccessor accessor;
	DBLexicon lexicon;

	@Before
	public void setUpBeforeClass() throws Exception {
		this.accessor = new SQLAccessor(NIHFeatureTests.DRIVER,
				NIHFeatureTests.DBURL, NIHFeatureTests.USER,
				NIHFeatureTests.PASS);
		this.lexicon = new DBLexicon();
		this.lexicon.setAccessor(this.accessor);
		this.lexicon.loadVerbnetData();
	}

	/**
	 * Tests for a few lexical classes and their subclasses
	 */
	@Test
	public void testHasLexicalClass() {
		Assert.assertTrue(this.lexicon.hasLexicalClass("carve-21.2-2"));
		Assert.assertTrue(this.lexicon.hasLexicalClass("admire-31.2-1"));
		Assert.assertTrue(this.lexicon.getLexicalClass("admire-31.2-1")
				.hasSuperclass());
		Assert.assertTrue(this.lexicon.getLexicalClass("admire-31.2-1")
				.getSuperclass().equals(
						this.lexicon.getLexicalClass("admire-31.2")));
	}

	/*
	 * @Test public void testGetLexicalClass() { fail("Not yet implemented"); }
	 */

}
