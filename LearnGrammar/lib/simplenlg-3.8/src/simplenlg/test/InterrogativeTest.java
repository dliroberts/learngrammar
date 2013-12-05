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
import simplenlg.features.InterrogativeType;
import simplenlg.features.SModifierPosition;
import simplenlg.features.Tense;
import simplenlg.realiser.NPPhraseSpec;
import simplenlg.realiser.SPhraseSpec;
import simplenlg.realiser.VPPhraseSpec;

// TODO: Auto-generated Javadoc
/**
 * JUnit test case for interrogatives.
 * 
 * @author agatt
 */
public class InterrogativeTest extends SimplenlgTest {

	// set up a few more fixtures
	/** The s5. */
	SPhraseSpec s1, s2, s3, s4, s5;

	/**
	 * Instantiates a new interrogative test.
	 * 
	 * @param name
	 *            the name
	 */
	public InterrogativeTest(String name) {
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
		this.s1.setComplement(this.man, DiscourseFunction.OBJECT);

		// there is the dog on the rock
		this.s2 = new SPhraseSpec();
		this.s2.setSubject("there");
		this.s2.setHead("be");
		this.s2.setComplement(this.dog);
		this.s2.addModifier(SModifierPosition.POST_VERB, this.onTheRock);

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

		this.s5 = new SPhraseSpec();
		this.s5.setSubject(new NPPhraseSpec("the", "dog"));
		this.s5.setHead("be");
		this.s5.setComplement(new NPPhraseSpec("the", "rock"),
				DiscourseFunction.OBJECT);

	}

	/**
	 * Tests a couple of fairly simple questions.
	 */
	@Test
	public void testSimpleQuestions() {

		// simple present
		this.s1.setTense(Tense.PRESENT);
		this.s1.setInterrogative(InterrogativeType.YES_NO);
		Assert.assertEquals("Does the woman kiss the man?", this.realiser
				.realise(this.s1));

		// simple past
		// sentence: "the woman kissed the man"
		this.s1.setTense(Tense.PAST);
		this.s1.setInterrogative(InterrogativeType.YES_NO);
		Assert.assertEquals("Did the woman kiss the man?", this.realiser
				.realise(this.s1));

		// revert back to declarative
		this.s1.setInterrogative(null);
		Assert.assertEquals("The woman kissed the man.", this.realiser
				.realise(this.s1));

		// copular/existential: be-fronting
		// sentence = "there is the dog on the rock"
		this.s2.setInterrogative(InterrogativeType.YES_NO);
		Assert.assertEquals("Is there the dog on the rock?", this.realiser
				.realise(this.s2));

		// perfective
		// sentence -- "there has been the dog on the rock"
		this.s2.setPerfect(true);
		Assert.assertEquals("Has there been the dog on the rock?",
				this.realiser.realise(this.s2));

		// progressive
		// sentence: "the man was giving the woman John's flower"
		this.s3.setTense(Tense.PAST);
		this.s3.setProgressive(true);
		this.s3.setInterrogative(InterrogativeType.YES_NO);
		Assert.assertEquals("Was the man giving the woman John's flower?",
				this.realiser.realise(this.s3));

		// modal
		// sentence: "the man should be giving the woman John's flower"
		this.s3.setProgressive(false);
		this.s3.setModal("should");
		this.s3.setInterrogative(InterrogativeType.YES_NO);
		Assert.assertEquals(
				"Should the man have given the woman John's flower?",
				this.realiser.realise(this.s3));

		// complex case with cue phrases
		// sentence: "however, tomorrow, Jane and Andrew will pick up the balls
		// in the shop"
		// this gets the front modifier "tomorrow" shifted to the end
		this.s4.setInterrogative(InterrogativeType.YES_NO);
		Assert
				.assertEquals(
						"However, will Jane and Andrew pick up the balls in the shop tomorrow?",
						this.realiser.realise(this.s4));
	}

	/**
	 * Test for sentences with negation.
	 */
	@Test
	public void testNegatedQuestions() {

		// sentence: "the woman did not kiss the man"
		this.s1.setTense(Tense.PAST);
		this.s1.setNegated(true);
		this.s1.setInterrogative(InterrogativeType.YES_NO);
		Assert.assertEquals("Did the woman not kiss the man?", this.realiser
				.realise(this.s1));

		// sentence: however, tomorrow, Jane and Andrew will not pick up the
		// balls in the shop
		this.s4.setNegated(true);
		this.s4.setInterrogative(InterrogativeType.YES_NO);
		Assert
				.assertEquals(
						"However, will Jane and Andrew not pick up the balls in the shop tomorrow?",
						this.realiser.realise(this.s4));
	}

	/**
	 * Tests for coordinate VPs in question form.
	 */
	@Test
	public void testCoordinateVPQuestions() {

		// create a complex vp: "kiss the dog and walk in the room"
		VPPhraseSpec complex;
		this.kiss.setComplement(this.dog);
		this.walk.setComplement(this.inTheRoom);
		complex = this.kiss.coordinate(this.walk);

		// sentence: "However, tomorrow, Jane and Andrew will kiss the dog and
		// will walk in the room"
		this.s4.setVerbPhrase(complex);
		String text = this.realiser.realise(this.s4);
		System.out.println(text);
		Assert
				.assertEquals(
						"However, tomorrow Jane and Andrew will kiss the dog and will walk in the room.",
						text);

		// setting to interrogative should automatically give us a single,
		// wide-scope aux
		this.s4.setInterrogative(InterrogativeType.YES_NO);
		Assert
				.assertEquals(
						"However, will Jane and Andrew kiss the dog and walk in the room tomorrow?",
						this.realiser.realise(this.s4));

		// slightly more complex -- perfective
		this.s4.setPerfect(true);
		Assert
				.assertEquals(
						"However, will Jane and Andrew have kissed the dog and walked in the room tomorrow?",
						this.realiser.realise(this.s4));
	}

	/**
	 * Test for simple WH questions in present tense.
	 */
	@Test
	public void testSimpleQuestions2() {
		SPhraseSpec s = new SPhraseSpec("the woman", "kiss", "the man");

		// try with the simple yes/no type first
		s.setInterrogative(InterrogativeType.YES_NO);
		Assert.assertEquals("Does the woman kiss the man?", this.realiser
				.realise(s));

		// now in the passive
		s.setPassive(true);
		Assert.assertEquals("Is the man kissed by the woman?", this.realiser
				.realise(s));

		// revert back to active
		s.setPassive(false);

		// subject interrogative with simple present
		// sentence: "the woman kisses the man"
		s.setInterrogative(InterrogativeType.WHO, DiscourseFunction.SUBJECT);
		Assert.assertEquals("Who kisses the man?", this.realiser.realise(s));

		// object interrogative with simple present
		s.setInterrogative(InterrogativeType.WHO, DiscourseFunction.OBJECT);
		Assert.assertEquals("Who does the woman kiss?", this.realiser
				.realise(s));

		// subject interrogative with passive
		s.setPassive(true);
		s.setInterrogative(InterrogativeType.WHO, DiscourseFunction.SUBJECT);
		Assert.assertEquals("Who is the man kissed by?", this.realiser
				.realise(s));
	}

	/**
	 * Test for wh questions.
	 */
	@Test
	public void testWHQuestions() {

		// subject interrogative
		this.s4.setInterrogative(InterrogativeType.WHO,
				DiscourseFunction.SUBJECT);
		Assert.assertEquals(
				"However, who will pick up the balls in the shop tomorrow?",
				this.realiser.realise(this.s4));

		// subject interrogative in passive
		this.s4.setPassive(true);
		Assert
				.assertEquals(
						"However, who will the balls be picked up in the shop by tomorrow?",
						this.realiser.realise(this.s4));

		// object interrogative
		this.s4.setPassive(false);
		this.s4.setInterrogative(InterrogativeType.WHAT,
				DiscourseFunction.OBJECT);
		Assert
				.assertEquals(
						"However, what will Jane and Andrew pick up in the shop tomorrow?",
						this.realiser.realise(this.s4));

		// object interrogative with passive
		this.s4.setPassive(true);
		this.s4.setInterrogative(InterrogativeType.WHAT,
				DiscourseFunction.OBJECT);
		Assert
				.assertEquals(
						"However, what will be picked up in the shop by Jane and Andrew tomorrow?",
						this.realiser.realise(this.s4));

		// how-question + passive
		this.s4.setInterrogative(InterrogativeType.HOW);
		Assert
				.assertEquals(
						"However, how will the balls be picked up in the shop by Jane and Andrew tomorrow?",
						this.realiser.realise(this.s4));

		// why-question + passive
		this.s4.setInterrogative(InterrogativeType.WHY);
		Assert
				.assertEquals(
						"However, why will the balls be picked up in the shop by Jane and Andrew tomorrow?",
						this.realiser.realise(this.s4));

		// how question with modal
		this.s4.setModal("should");
		this.s4.setInterrogative(InterrogativeType.HOW);
		Assert
				.assertEquals(
						"However, how should the balls be picked up in the shop by Jane and Andrew tomorrow?",
						this.realiser.realise(this.s4));

		// indirect object
		this.s3.setInterrogative(InterrogativeType.WHO,
				DiscourseFunction.INDIRECT_OBJECT);
		Assert.assertEquals("Who does the man give John's flower?",
				this.realiser.realise(this.s3));
	}

	/**
	 * Test questyions in the tutorial.
	 */
	@Test
	public void testTutorialQuestions() {
		SPhraseSpec p = new SPhraseSpec();
		p.setSubject("Mary");
		p.setHead("chase");
		p.addComplement("George");
		p.setInterrogative(InterrogativeType.YES_NO);
		Assert
				.assertEquals("Does Mary chase George?", this.realiser
						.realise(p));

		p = new SPhraseSpec();
		p.setSubject("Mary");
		p.setHead("chase");
		p.addComplement("George");
		p.setInterrogative(InterrogativeType.WHO, DiscourseFunction.OBJECT);
		Assert.assertEquals("Who does Mary chase?", this.realiser.realise(p));

	}
}
