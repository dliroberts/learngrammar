package simplenlg.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test suite containing all database-backed lexicon tests.
 * 
 * @author agatt
 * 
 */
public class LexiconTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for simplenlg DB Lexicon");
		// $JUnit-BEGIN$
		suite.addTestSuite(NIHFeatureTests.class);
		suite.addTestSuite(NIHBasicTest.class);
		suite.addTestSuite(NIHLexiconDBTest.class);
		suite.addTestSuite(NIHLexRelationTest.class);
		// $JUnit-END$
		return suite;
	}

}
