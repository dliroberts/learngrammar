package simplenlg.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import simplenlg.features.Category;
import simplenlg.lexicon.DBLexicon;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.lexicalitems.Noun;
import simplenlg.realiser.NPPhraseSpec;
import simplenlg.realiser.Realiser;
import simplenlg.realiser.SPhraseSpec;

/**
 * Simple tests of NIH lexicon
 * 
 * @author ereiter
 * 
 */
public class NIHBasicTest extends TestCase {

	Lexicon lexicon;

	public NIHBasicTest(String name) {
		super(name);
	}

	/**
	 * Set up the variables we'll need for this simplenlg.test to run (Called
	 * automatically by JUnit)
	 */
	@Override
	@Before
	protected void setUp() {
		this.lexicon = new DBLexicon("com.mysql.jdbc.Driver",
				"jdbc:mysql://shallot.csd.abdn.ac.uk:3306/lexicon", "simplenlg", "simplenlg");
	}

	/* simple test of DB access */
	@Test
	public void testMouse() {

		Noun mouse = (Noun) this.lexicon.getItem(Category.NOUN, "mouse");
		Assert.assertEquals(mouse.getPlural(), "mice");
		Assert.assertTrue(mouse.isCountNoun());
	}

	/* make sure we can use NIH DB in realiser */
	@Test
	public void testSimpleRealiser() {
		NPPhraseSpec np = new NPPhraseSpec("the", "dog");
		np.setPlural(true);
		SPhraseSpec sent = new SPhraseSpec("John", "chase", np);
		Realiser r = new Realiser(this.lexicon);
		String text = r.realise(sent);
		Assert.assertEquals(text, "John chases the dogs.");
	}

}
