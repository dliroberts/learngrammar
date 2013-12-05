/* ==================================================
 * SimpleNLG: An API for Natural Language Generation
 * ==================================================
 *
 * Copyright (c) 2007, the University of Aberdeen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted FOR RESEARCH PURPOSES ONLY, provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 * 		this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 * 3. Neither the name of the University of Aberdeen nor the names of its contributors 
 * 	  may be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 *    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 *    THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 *    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 *    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 *    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *     LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 *     ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 *     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *     EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *     
 *  Redistribution and use for purposes other than research requires special permission by the
 *  copyright holders and contributors. Please contact Ehud Reiter (ereiter@csd.abdn.ac.uk) for
 *  more information.
 *     
 *	   =================    
 *     Acknowledgements:
 *     =================
 *     This library contains a re-implementation of some rules derived from the MorphG package
 *     by Guido Minnen, John Carroll and Darren Pearce. You can find more information about MorphG
 *     in the following reference:
 *     	Minnen, G., Carroll, J., and Pearce, D. (2001). Applied Morphological Processing of English.
 *     		Natural Language Engineering 7(3): 207--223.
 *     Thanks to John Carroll (University of Sussex) for permission to re-use the MorphG rules. 
 */
package simplenlg.test;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import simplenlg.features.AdjectivePosition;
import simplenlg.features.AdverbPosition;
import simplenlg.features.AdverbType;
import simplenlg.features.Agreement;
import simplenlg.features.Case;
import simplenlg.features.Deixis;
import simplenlg.features.Polarity;
import simplenlg.features.Quantification;
import simplenlg.features.Reflexivity;
import simplenlg.features.VerbType;
import simplenlg.lexicon.DBLexicon;
import simplenlg.lexicon.db.SQLAccessor;
import simplenlg.lexicon.lexicalitems.Adjective;
import simplenlg.lexicon.lexicalitems.Adverb;
import simplenlg.lexicon.lexicalitems.Conjunction;
import simplenlg.lexicon.lexicalitems.LexicalItem;
import simplenlg.lexicon.lexicalitems.Noun;
import simplenlg.lexicon.lexicalitems.Pronoun;
import simplenlg.lexicon.lexicalitems.Verb;

/**
 * Some simple tests to check that features specified in the lexicon DB are
 * correctly interpreted and set.
 * 
 * <P>
 * Requires jvm argument <code>-Xmx1g</code>. This test is rather slow, as it
 * loads all the lexicon into memory.
 * 
 * @author agatt
 */
public class NIHFeatureTests extends TestCase {

	// accessor object -- the default SQLAccessor class
	SQLAccessor accessor;

	// lexicon object -- an instance of Lexicon
	DBLexicon lexicon;

	// lex items used for tests -- loaded by setUp()
	LexicalItem lex1, lex2, lex3, lex4, lex5, lex6, lex7, lex8, lex9, lex10;

	// nouns
	Noun n1, n2, n3, n4, n5;

	// verbs
	Verb v1, v2, v3, v4, v5;

	// adverbs
	Adverb a1, a2, a3;

	// adjectives
	Adjective a4, a5, a6, a7;

	// conjunctions
	Conjunction c1, c2;

	// pronouns
	Pronoun p1, p2, p3, p4, p5;

	// db params
	static String DRIVER = "com.mysql.jdbc.Driver";

	static String DBURL = "jdbc:mysql://shallot.csd.abdn.ac.uk:3306/lexicon";

	static String USER = "simplenlg";

	static String PASS = "simplenlg";

	/**
	 * Sets up the test case -- init the lexicon and some lexical items.
	 * 
	 * @throws java.lang.Exception
	 *             *
	 * @throws Exception
	 *             the exception
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		this.accessor = new SQLAccessor(NIHFeatureTests.DRIVER,
				NIHFeatureTests.DBURL, NIHFeatureTests.USER,
				NIHFeatureTests.PASS);
		this.lexicon = new DBLexicon();
		this.lexicon.setAccessor(this.accessor);
		this.lex1 = this.lexicon.getItemByID("E0544846"); // 'cyclohexanecarboxylate'
		this.lex2 = this.lexicon.getItemByID("E0216084"); // 'high fiber diet
		this.lex3 = this.lexicon.getItemByID("E0064931"); // vox
		this.lex4 = this.lexicon.getItemByID("E0037847"); // livedoid dermatitis
		this.lex5 = this.lexicon.getItemByID("E0023033"); // dip
		this.lex6 = this.lexicon.getItemByID("E0415904"); // electroblot
		this.lex7 = this.lexicon.getItemByID("E0052498"); // regardless of
		this.lex8 = this.lexicon.getItemByID("E0566408"); // pantoic
		this.lex9 = this.lexicon.getItemByID("E0229545"); // white-footed mouse
		this.lex10 = this.lexicon.getItemByID("E0053384"); // rewrite
		this.n1 = (Noun) this.lexicon.getItemByID("E0025687"); // ephemeral
		// fever
		this.n2 = (Noun) this.lexicon.getItemByID("E0060502"); // their
		this.n3 = (Noun) this.lexicon.getItemByID("E0011159"); // audience
		this.n4 = (Noun) this.lexicon.getItemByID("E0009846"); // anybody
		this.n5 = (Noun) this.lexicon.getItemByID("E0553347"); // hoistman
		this.v1 = (Verb) this.lexicon.getItemByID("E0059806"); // tag
		this.v2 = (Verb) this.lexicon.getItemByID("E0057049"); // sphacelate
		this.v3 = (Verb) this.lexicon.getItemByID("E0012152"); // be
		this.v4 = (Verb) this.lexicon.getItemByID("E0044483"); // ought
		this.a1 = (Adverb) this.lexicon.getItemByID("E0045835");// particularly
		this.a2 = (Adverb) this.lexicon.getItemByID("E0551601");// invisibly
		this.a3 = (Adverb) this.lexicon.getItemByID("E0573565");// quo ad vitam
		this.a4 = (Adjective) this.lexicon.getItemByID("E0571412"); // red-colored
		this.a5 = (Adjective) this.lexicon.getItemByID("E0331100"); // exocranial
		this.a6 = (Adjective) this.lexicon.getItemByID("E0558554"); // boilable
		this.a7 = (Adjective) this.lexicon.getItemByID("E0011464"); // awful
		this.c1 = (Conjunction) this.lexicon.getItemByID("E0007709"); // after
		this.c2 = (Conjunction) this.lexicon.getItemByID("E0008914"); // and/or
		this.p1 = (Pronoun) this.lexicon.getItemByID("E0008090");// all
		this.p2 = (Pronoun) this.lexicon.getItemByID("E0056729");// some
		this.p3 = (Pronoun) this.lexicon.getItemByID("E0031461");// herself
		this.p4 = (Pronoun) this.lexicon.getItemByID("E0042156");// neither
		this.p5 = (Pronoun) this.lexicon.getItemByID("E0060481");// that

	}

	/**
	 * Test for the InflectionType of a few lexical items.
	 */
	@Test
	public void testInflectionTypes() {
		// REGULAR CASES
		Assert.assertTrue(this.lex1.isRegular());
		Assert.assertTrue(this.lex2.isRegular());
		Assert.assertFalse(this.lex1.isDLRegular());
		Assert.assertFalse(this.lex2.isGLRegular());

		// Greco-latin inflection
		Assert.assertTrue(this.lex3.isGLRegular());
		Assert.assertTrue(this.lex4.isGLRegular());
		Assert.assertFalse(this.lex4.isRegular());
		Assert.assertFalse(this.lex3.isRegular());

		// regular with cons doubling
		Assert.assertTrue(this.lex5.isDLRegular());
		Assert.assertTrue(this.lex6.isDLRegular());

		// invariant
		Assert.assertTrue(this.lex7.isInvariant());
		Assert.assertTrue(this.lex8.isInvariant());

		// irregular
		Assert.assertFalse(this.lex9.isRegular());
		Assert.assertFalse(this.lex10.isRegular());

		// periphrastic adverb -- particularly
		Assert.assertTrue(this.a1.isPeriphrastic());
	}

	/**
	 * Test for the agreement forms of some lexical items.
	 */
	@Test
	public void testAgreement() {
		// mass noun ("ephemeral fever")
		Assert.assertEquals(this.n1.getAgreement(), Agreement.MASS);

		// fixed plural pronoun ("their"
		Assert.assertEquals(this.n2.getAgreement(), Agreement.FIXED_PLUR);

		// group noun "audience"
		Assert.assertEquals(this.n3.getAgreement(), Agreement.GROUP);

		// fixed sing "anybody"
		Assert.assertEquals(this.n4.getAgreement(), Agreement.FIXED_SING);

		// count noun: "hoistman"
		Assert.assertEquals(this.n5.getAgreement(), Agreement.COUNT);
	}

	/**
	 * Check some verb types.
	 */
	@Test
	public void testVerbType() {
		Assert.assertEquals(this.v1.getVerbType(), VerbType.MAIN);
		Assert.assertEquals(this.v2.getVerbType(), VerbType.MAIN);
		Assert.assertEquals(this.v3.getVerbType(), VerbType.AUX);
		Assert.assertEquals(this.v4.getVerbType(), VerbType.MODAL);
	}

	/**
	 * Check transitivity features of verbs, and whether they allows passive and
	 * dative movement.
	 */
	@Test
	public void testTransitivity() {
		// tag has 11 possible complement frames
		Assert.assertEquals(this.v1.getNumComplements(), 11);

		// verb "tag" has a complex trans reading
		Assert.assertTrue(this.v1.isComplextransitive());

		// verb "tag" can have linked comp
		Assert.assertTrue(this.v1.isLinkingVerb());

		// verb "tag" can be monotrans
		Assert.assertTrue(this.v1.isMonotransitive());

		// intrans verb "sphacelate"
		Assert.assertTrue(this.v2.isIntransitive());

		// "tag" allows passivisation
		Assert.assertTrue(this.v1.allowsPassiveRaising());

		// "tag" does not allow dative shift
		Assert.assertFalse(this.v1.allowsDativeShift());
	}

	/**
	 * Test that adjectival positions are being read correctly.
	 */
	@Test
	public void testAdjectivePosition() {
		// red-coloured is attributive in pos 1 or 2, or predicative
		Assert.assertTrue(this.a4.hasPosition(AdjectivePosition.ATTRIB_1));
		Assert.assertTrue(this.a4.hasPosition(AdjectivePosition.ATTRIB_2));
		Assert.assertTrue(this.a4.hasPosition(AdjectivePosition.PREDICATIVE));

		// exocranial is attrib in pos 3
		Assert.assertTrue(this.a5.hasPosition(AdjectivePosition.ATTRIB_3));

		// boilable is predicative or attrib in pos 3
		Assert.assertTrue(this.a6.hasPosition(AdjectivePosition.PREDICATIVE));
		Assert.assertTrue(this.a6.hasPosition(AdjectivePosition.ATTRIB_3));
	}

	/**
	 * Test that stative/non-stative feature is being read correctly.
	 */
	public void testAdjectiveType() {
		Assert.assertTrue(this.a4.isStative());
		Assert.assertTrue(this.a5.isStative());
		Assert.assertTrue(this.a6.isStative());
		Assert.assertFalse(this.a7.isStative());
	}

	/**
	 * Test that adverb types are read correctly.
	 */
	public void testAdverbType() {
		// particularly is an intensifier
		Assert.assertTrue(this.a1.hasType(AdverbType.INTENSIFIER));

		// invisaibly is a manner modifier
		Assert.assertTrue(this.a2.hasType(AdverbType.MANNER));

		// quo ad vitam is a temporal adv
		Assert.assertTrue(this.a3.hasType(AdverbType.TEMPORAL));
	}

	/**
	 * test that adverb positions are read correctly.
	 */
	public void testAdverbPosition() {
		// particularly has no position specified
		Assert.assertFalse(this.a1.hasPosition(AdverbPosition.SENTENTIAL));
		Assert.assertFalse(this.a1.hasPosition(AdverbPosition.VERB_PARTICLE));
		Assert.assertTrue(this.a1.hasPosition(AdverbPosition.VERBAL));

		// invisibly is a verb modifier
		Assert.assertTrue(this.a2.hasPosition(AdverbPosition.VERBAL));

		// quo ad vitam is a verb modifier
		Assert.assertTrue(this.a3.hasPosition(AdverbPosition.VERBAL));
	}

	/**
	 * Test that conjunction type features are set.
	 */
	public void testConjunctionType() {
		// after is subordinating
		Assert.assertTrue(this.c1.isSubordinating());

		// and/or is coordinating
		Assert.assertTrue(this.c2.isCoordinating());

	}

	/**
	 * Test that deixis features for pronouns ar read correctly.
	 */
	public void testDeixis() {
		// all is non-demonstrative
		Assert.assertFalse(this.p1.getDeixis().equals(Deixis.DEMONSTRATIVE));

		// that is demonstrative
		Assert.assertEquals(this.p5.getDeixis(), Deixis.DEMONSTRATIVE);
	}

	/**
	 * Test that case features for pronouns ar read correctly.
	 */
	public void testCase() {
		// all is either subj or obj
		Assert.assertEquals(this.p1.getCaseValue(), Case.NOM_ACC);

		// some is either subj or objt
		Assert.assertEquals(this.p2.getCaseValue(), Case.NOM_ACC);

		// herself is always accusative
		Assert.assertEquals(this.p3.getCaseValue(), Case.ACCUSATIVE);

	}

	/**
	 * Test that possession features for pronouns ar read correctly.
	 */
	public void testPossession() {

	}

	/**
	 * Test that polarity features for pronouns ar read correctly.
	 */
	public void testPolarity() {
		// neither has negative polarity
		Assert.assertEquals(this.p4.getPolarity(), Polarity.NEGATIVE);
	}

	/**
	 * Test that reflexivity features for pronouns ar read correctly.
	 */
	public void testReflexivity() {
		// herself is reflexive
		Assert.assertEquals(this.p3.getReflexivity(), Reflexivity.REFLEXIVE);
	}

	/**
	 * Test that quantification features for pronouns ar read correctly.
	 */
	public void testQuantification() {
		// all is a universal quantifier
		Assert.assertEquals(this.p1.getQuantification(),
				Quantification.UNIVERSAL);

		// some is indefinite, assertive
		Assert.assertEquals(this.p2.getQuantification(),
				Quantification.INDEF_ASSERT);
	}

}
