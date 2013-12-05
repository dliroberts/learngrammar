package simplenlg.test;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Suite containing all tests for syntax and inflection
 * 
 * @author agatt
 * 
 */
public class SyntaxTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for simplenlg.test");
		// $JUnit-BEGIN$
		suite.addTestSuite(PPTest.class);
		suite.addTestSuite(AdjPTest.class);
		suite.addTestSuite(InterrogativeTest.class);
		suite.addTestSuite(TextSpecTest.class);
		suite.addTestSuite(ExternalTest.class);
		suite.addTestSuite(STest.class);
		suite.addTestSuite(VPTest.class);
		suite.addTestSuite(NPTest.class);
		suite.addTestSuite(InflectionTest.class);
		// $JUnit-END$
		return suite;
	}

}
