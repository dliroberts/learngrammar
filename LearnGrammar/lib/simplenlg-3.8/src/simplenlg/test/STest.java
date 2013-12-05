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

import org.junit.Before;
import org.junit.Test;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Form;
import simplenlg.features.SModifierPosition;
import simplenlg.features.Tense;
import simplenlg.lexicon.lexicalitems.Verb;
import simplenlg.realiser.NPPhraseSpec;
import simplenlg.realiser.Phrase;
import simplenlg.realiser.SPhraseSpec;

// TODO: Auto-generated Javadoc
/**
 * The Class STest.
 */
public class STest extends SimplenlgTest {

	// set up a few more fixtures
	/** The s4. */
	SPhraseSpec s1, s2, s3, s4;

	/**
	 * Instantiates a new s test.
	 * 
	 * @param name
	 *            the name
	 */
	public STest(String name) {
		super(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.test.SimplenlgTest#setUp()
	 */
	@Override
	@Before
	protected void setUp() {
		super.setUp();

		// the woman kissed the man behind the curtain
		this.s1 = new SPhraseSpec();
		this.s1.setSubject(this.woman);
		this.s1.setVerbPhrase(this.kiss);
		this.s1.setComplement(this.man);

		// there is the dog on the rock
		this.s2 = new SPhraseSpec();
		this.s2.setSubject("there");
		this.s2.setHead("be");
		this.s2.setComplement(this.dog);
		this.s2.setModifier(SModifierPosition.POST_VERB, this.onTheRock);

		// the man gives the woman John's flower
		this.s3 = new SPhraseSpec();
		this.s3.setSubject(this.man);
		this.s3.setVerbPhrase(this.give);
		NPPhraseSpec flower = new NPPhraseSpec("flower");
		NPPhraseSpec john = new NPPhraseSpec("John");
		john.setPossessive(true);
		flower.setSpecifier(john);
		flower.setDiscourseFunction(DiscourseFunction.OBJECT);
		this.s3.addComplement(flower);
		this.s3.addComplement(DiscourseFunction.INDIRECT_OBJECT, this.woman);

		this.s4 = new SPhraseSpec();
		this.s4.setCuePhrase("however,");
		this.s4.addFrontModifier("tomorrow");
		NPPhraseSpec subject = new NPPhraseSpec("Jane")
				.coordinate(new NPPhraseSpec("Andrew"));
		this.s4.addSubject(subject);
		this.s4.setHead("pick");
		this.s4.setVerbParticle("up");
		this.s4.addComplement("the balls");
		this.s4.addModifier("in the shop");
		this.s4.setTense(Tense.FUTURE);
	}

	/**
	 * Initial test for basic sentences.
	 */
	@Test
	public void testBasic() {
		Assert.assertEquals("The woman kisses the man.", this.realiser
				.realise(this.s1));
		Assert.assertEquals("There is the dog on the rock.", this.realiser
				.realise(this.s2));
		Assert.assertEquals("The man gives the woman John's flower.",
				this.realiser.realise(this.s3));
		Assert
				.assertEquals(
						"However, tomorrow Jane and Andrew will pick up the balls in the shop.",
						this.realiser.realise(this.s4));
	}

	/**
	 * Test that pronominal args are being correctly cast as NPs.
	 */
	public void testPronounArguments() {
		// the subject of s2 should have been cast into a pronominal NP
		Phrase subj = this.s2.getSubjects().get(0);
		Assert.assertTrue(subj instanceof NPPhraseSpec);
		Assert.assertTrue(((NPPhraseSpec) subj).isPronominal());
	}

	/**
	 * Tests for setting tense, aspect and passive from the sentence interface.
	 */
	@Test
	public void testTenses() {
		// simple past
		this.s3.setTense(Tense.PAST);
		Assert.assertEquals("The man gave the woman John's flower.",
				this.realiser.realise(this.s3));

		// perfect
		this.s3.setPerfect(true);
		Assert.assertEquals("The man had given the woman John's flower.",
				this.realiser.realise(this.s3));

		// negation
		this.s3.setNegated(true);
		Assert.assertEquals("The man had not given the woman John's flower.",
				this.realiser.realise(this.s3));

		this.s3.setProgressive(true);
		Assert.assertEquals(
				"The man had not been giving the woman John's flower.",
				this.realiser.realise(this.s3));

		// passivisation with direct and indirect object
		this.s3.setPassive(true);
		Assert.assertEquals(
				"John's flower had not been being given the woman by the man.",
				this.realiser.realise(this.s3));
	}

	/**
	 * Test what happens when a sentence is subordinated as complement of a
	 * verb.
	 */
	@Test
	public void testSubordination() {

		// subordinate sentence by setting it as complement of a verb
		this.say.setComplement(this.s3);

		// check the getter
		Assert.assertTrue(this.s3.isSubordinateClause());

		// check realisation
		Assert.assertEquals("says that the man gives the woman John's flower",
				this.realiser.realise(this.say));
	}

	/**
	 * Test the various forms of a sentence, including subordinates.
	 */
	@Test
	public void testForm() {

		// check the getter method
		Assert.assertEquals(this.s1.getForm(), Form.NORMAL);

		// infinitive
		this.s1.setForm(Form.INFINITIVE);
		Assert.assertEquals("To kiss the man.", this.realiser.realise(this.s1));

		// gerund with "there"
		this.s2.setForm(Form.GERUND);
		Assert.assertEquals("There being the dog on the rock.", this.realiser
				.realise(this.s2));

		// gerund with possessive
		this.s3.setForm(Form.GERUND);
		Assert.assertEquals("The man's giving the woman John's flower.",
				this.realiser.realise(this.s3));

		// imperative
		this.s3.setForm(Form.IMPERATIVE);
		Assert.assertEquals("Give the woman John's flower.", this.realiser
				.realise(this.s3));

		// subordinating the imperative to a verb should turn it to infinitive
		this.say.setComplement(this.s3);
		Assert.assertEquals("says to give the woman John's flower",
				this.realiser.realise(this.say));

		// imperative -- case II
		this.s4.setForm(Form.IMPERATIVE);
		Assert.assertEquals("However, tomorrow pick up the balls in the shop.",
				this.realiser.realise(this.s4));

		// infinitive -- case II
		this.s4.setForm(Form.INFINITIVE);
		Assert.assertEquals(
				"However, to pick up the balls in the shop tomorrow.",
				this.realiser.realise(this.s4));
	}

	/**
	 * Slightly more complex tests for forms.
	 */
	public void testForm2() {
		// set s4 as subject of a new sentence
		SPhraseSpec temp = new SPhraseSpec(this.s4, "is", "recommended");

		// s4 should now be in gerund form
		Assert.assertEquals(Form.GERUND, this.s4.getForm());
		Assert
				.assertEquals(
						"However, tomorrow "
								+ "Jane and Andrew's picking up the balls in the shop is recommended.",
						this.realiser.realise(temp));

		// compose this with a new sentence
		// ER - switched direct and indirect object in sentence
		SPhraseSpec temp2 = new SPhraseSpec("I", "tell", temp);
		temp2.setTense(Tense.FUTURE);
		temp2.addIndirectObject("John");
		Assert
				.assertEquals(
						"I will tell John that however, tomorrow Jane and Andrew's picking up the balls "
								+ "in the shop is recommended.", this.realiser
								.realise(temp2));

		// turn s4 to imperative and put it in indirect object position
		this.s4.setForm(Form.IMPERATIVE);
		temp2 = new SPhraseSpec("I", "tell", this.s4);
		temp2.addIndirectObject("John");
		temp2.setTense(Tense.FUTURE);
		Assert.assertEquals("I will tell John however, to pick up the balls "
				+ "in the shop tomorrow.", this.realiser.realise(temp2));
	}

	/**
	 * Tests for gerund forms and genitive subjects.
	 */
	@Test
	public void testGerundsubject() {
		// the man's giving the woman John's flower upset Peter
		SPhraseSpec s4 = new SPhraseSpec();
		s4.setHead(new Verb("upset"));
		s4.setTense(Tense.PAST);
		s4.setComplement(new NPPhraseSpec("Peter"));
		this.s3.setPerfect(true);

		// set the sentence as subject of another: makes it a gerund
		s4.setSubject(this.s3);

		// suppress the genitive realisation of the NP subject in gerund
		// sentences
		this.s3.suppressGenitiveInGerund(true);

		// s3 should now be a subordinate clause in gerund form
		Assert.assertTrue(this.s3.isSubordinateClause());
		Assert.assertTrue(this.s3.getForm().equals(Form.GERUND));

		// check the realisation: subject should not be genitive
		Assert.assertEquals(
				"The man having given the woman John's flower upset Peter.",
				this.realiser.realise(s4));

	}

	/**
	 * Some tests for multiple embedded sentences.
	 */
	@Test
	public void testComplexSentence1() {

		// the man's giving the woman John's flower upset Peter
		SPhraseSpec complexS = new SPhraseSpec();
		complexS.setHead(new Verb("upset"));
		complexS.setTense(Tense.PAST);
		complexS.setComplement(new NPPhraseSpec("Peter"));
		this.s3.setPerfect(true);
		complexS.setSubject(this.s3);

		// s3 should now be a subordinate clause in gerund form
		Assert.assertTrue(this.s3.isSubordinateClause());
		Assert.assertTrue(this.s3.getForm().equals(Form.GERUND));

		// check the realisation: subject should be genitive
		Assert.assertEquals(
				"The man's having given the woman John's flower upset Peter.",
				this.realiser.realise(complexS));

		// coordinate sentences in subject position
		SPhraseSpec s5 = new SPhraseSpec();
		s5.setSubject(new NPPhraseSpec("some", "person"));
		s5.setHead("stroke");
		s5.setComplement(new NPPhraseSpec("the", "cat"));
		SPhraseSpec coord = this.s3.coordinate(s5);
		complexS.setSubject(coord);
		Assert.assertEquals("The man's having given the woman John's flower "
				+ "and some person's stroking the cat upset Peter.",
				this.realiser.realise(complexS));

		// now subordinate the complex sentence
		// coord.setClauseStatus(SPhraseSpec.ClauseType.MAIN);
		SPhraseSpec s6 = new SPhraseSpec();
		s6.setHead("tell");
		s6.setTense(Tense.PAST);
		s6.setSubject(new NPPhraseSpec("the", "boy"));
		// ER - switched indirect and direct object
		s6.addComplement(DiscourseFunction.INDIRECT_OBJECT, new NPPhraseSpec(
				"every", "girl"));
		s6.addComplement(DiscourseFunction.OBJECT, complexS);
		Assert.assertEquals(
				"The boy told every girl that the man's having given the woman John's flower"
						+ " and some person's stroking the cat upset Peter.",
				this.realiser.realise(s6));

		// the object phrase phrase should be subordinate automaticalluy
		Assert.assertEquals(true, complexS.isSubordinateClause());
	}

	/**
	 * More coordination tests.
	 */
	@Test
	public void testComplexSentence3() {
		// the coordinate sentence allows us to raise and lower complementiser
		SPhraseSpec coord2 = this.s1.coordinate(this.s3);
		coord2.setTense(Tense.PAST);
		Assert
				.assertEquals(
						"The woman kissed the man and the man gave the woman John's flower.",
						this.realiser.realise(coord2));
	}

	/**
	 * Tests recogition of strings in API.
	 */
	@Test
	public void testStringRecognition() {
		// test recognition of forms of "be"
		SPhraseSpec s1 = new SPhraseSpec("my cat", "is", "sad");
		Assert.assertEquals("My cat is sad.", this.realiser.realise(s1));

		// test recognition of pronoun for afreement
		SPhraseSpec s2 = new SPhraseSpec("I", "want", "Mary");
		Assert.assertEquals("I want Mary.", this.realiser.realise(s2));

		// test recognition of pronoun for correct form
		NPPhraseSpec subject = new NPPhraseSpec("dog");
		subject.setSpecifier("a");
		subject.addPostmodifier("from next door");
		NPPhraseSpec object = new NPPhraseSpec("I");
		SPhraseSpec s = new SPhraseSpec(subject, "chase", object);
		s.setProgressive(true);
		Assert.assertEquals("A dog from next door is chasing me.",
				this.realiser.realise(s));
	}

	/**
	 * Tests complex agreement.
	 */
	@Test
	public void testAgreement() {
		// basic agreement
		NPPhraseSpec np = new NPPhraseSpec("dog");
		np.setSpecifier("the");
		np.addPremodifier("angry");
		SPhraseSpec s1 = new SPhraseSpec(np, "chase", "John");
		Assert.assertEquals("The angry dog chases John.", this.realiser
				.realise(s1));

		// plural
		np.setPlural(true);
		Assert.assertEquals("The angry dogs chase John.", this.realiser
				.realise(s1));

		// test agreement with "there is"
		np.setPlural(false);
		np.setSpecifier("a");
		SPhraseSpec s2 = new SPhraseSpec("there", "be", np);
		Assert
				.assertEquals("There is an angry dog.", this.realiser
						.realise(s2));

		// plural with "there"
		np.setPlural(true);
		Assert.assertEquals("There are some angry dogs.", this.realiser
				.realise(s2));
	}

	/**
	 * Tests passive.
	 */
	@Test
	public void testPassive() {

		// passive with just complement
		SPhraseSpec s1 = new SPhraseSpec();
		s1.setHead("intubate");
		s1.addComplement("the baby");
		s1.setPassive(true);
		Assert
				.assertEquals("The baby is intubated.", this.realiser
						.realise(s1));

		// passive with subject and complement
		s1.addSubject("the nurse");
		Assert.assertEquals("The baby is intubated by the nurse.",
				this.realiser.realise(s1));

		// passive with subject and indirect object
		SPhraseSpec s2 = new SPhraseSpec();
		s2.setHead("give");
		s2.addComplement("the baby");
		s2.addIndirectObject("50ug of morphine");
		s2.setPassive(true);
		Assert.assertEquals("The baby is given 50ug of morphine.",
				this.realiser.realise(s2));

		// passive with subject, complement and indirect object
		s2.addSubject("the nurse");
		Assert.assertEquals("The baby is given 50ug of morphine by the nurse.",
				this.realiser.realise(s2));

		// test agreement in passive
		SPhraseSpec s3 = new SPhraseSpec();
		s3.addSubject("my dog");
		s3.addSubject("your cat");
		s3.setHead("chase");
		s3.addComplement("George");
		s3.setTense(Tense.PAST);
		s3.addFrontModifier("yesterday");
		Assert.assertEquals("Yesterday, my dog and your cat chased George.",
				this.realiser.realise(s3));

		s3.setPassive(true);
		Assert.assertEquals(
				"Yesterday, George was chased by my dog and your cat.",
				this.realiser.realise(s3));

		// test correct pronoun forms
		SPhraseSpec s4 = new SPhraseSpec("he", "chase", "me");
		Assert.assertEquals("He chases me.", this.realiser.realise(s4));
		s4.setPassive(true);
		Assert.assertEquals("I am chased by him.", this.realiser.realise(s4));

		// same thing, but giving the S constructor "me". Should recognise
		// correct pro
		// anyway
		SPhraseSpec s5 = new SPhraseSpec("him", "chase", "me");
		Assert.assertEquals("He chases me.", this.realiser.realise(s5));

		s5.setPassive(true);
		Assert.assertEquals("I am chased by him.", this.realiser.realise(s5));

	}

	/**
	 * Tests tenses with modals.
	 */
	public void testModal() {

		// simple modal in present tense
		this.s3.setModal("should");
		Assert.assertEquals("The man should give the woman John's flower.",
				this.realiser.realise(this.s3));

		// modal + future -- uses present
		this.s3.setTense(Tense.FUTURE);
		Assert.assertEquals("The man should give the woman John's flower.",
				this.realiser.realise(this.s3));

		// modal + present progressive
		this.s3.setModal("should");
		this.s3.setProgressive(true);
		Assert.assertEquals(
				"The man should be giving the woman John's flower.",
				this.realiser.realise(this.s3));

		// modal + past tense
		this.s3.setTense(Tense.PAST);
		this.s3.setProgressive(false);
		Assert.assertEquals(
				"The man should have given the woman John's flower.",
				this.realiser.realise(this.s3));

		// modal + past progressive
		this.s3.setProgressive(true);
		Assert.assertEquals(
				"The man should have been giving the woman John's flower.",
				this.realiser.realise(this.s3));

	}
}
