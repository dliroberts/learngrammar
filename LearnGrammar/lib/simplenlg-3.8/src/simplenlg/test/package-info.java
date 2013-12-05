/**
 * This package contains classes that extend the basic <code>JUnit</code> <code>TestCase<code> class.
 * All <code>simplenlg</code> tests extend a basic abstract class <code>SimplenlgTest</code>, 
 * which is where a number of <code>JUnit</code> <i>fixtures</i> are set up for use by all
 * tests. Fixtures are objects which are manipulated by many different unit tests.
 * 
 * <P>
 * Running these tests requires the inclusion of the <code>JUnit</code> package in the classpath.
 * See <code>http://www.junit.org</code> for documentation and downloading instructions.
 * 
 * <P>
 * Classes extending <code>SimplenlgTest</code> group together several unit tests for a particular kind of
 * phrase.
 * Two test suite classes are also provided, called
 * {@link simplenlg.test.SyntaxTests} which runs all the <code>JUnit</code> 
 * tests related to syntax and inflection, and {@link simplenlg.test.LexiconTests}, which contains tests for the database-backed lexicon. 
 * 
 * <P>
 * It is recomended that new unit-testing classes in this package maintain the following conventions:
 * 
 *  <UL>
 *  	<LI>As far as possible, confine tests for a particular `family' of phrase types (e.g. noun phrases)
 *  		to a single test class.</LI>
 *  	<LI>Name each test method in a test class with the <i>test</i> prefix;</LI>
 *  	<LI>Decorate test methods with the <code>\@test</code> tag provided in the package <code>org.junit</code></LI>
 *  	<LI>Add the new test class to the relevant test suite method.</LI>
 *  </UL>
 *   
 * @author Albert Gatt 
 */
package simplenlg.test;

