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

import org.junit.Test;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Gender;
import simplenlg.features.NumberAgr;
import simplenlg.lexicon.lexicalitems.Determiner;
import simplenlg.realiser.AdjPhraseSpec;
import simplenlg.realiser.CoordinateNPPhraseSpec;
import simplenlg.realiser.NPPhraseSpec;
import simplenlg.realiser.PPPhraseSpec;

// TODO: Auto-generated Javadoc
/**
 * Tests for the NPPhraseSpec and CoordinateNPPhraseSpec classes.
 * 
 * @author agatt
 */
public class NPTest extends SimplenlgTest {

	/**
	 * Instantiates a new nP test.
	 * 
	 * @param name
	 *            the name
	 */
	public NPTest(String name) {
		super(name);
	}

	/**
	 * Test the setPlural() method in noun phrases.
	 */
	@Test
	public void testPlural() {
		this.np4.setPlural(true);
		Assert.assertEquals("the rocks", this.realiser.realise(this.np4));

		this.np5.setPlural(true);
		Assert.assertEquals("the curtains", this.realiser.realise(this.np5));

		this.np5.setPlural(false);
		Assert.assertEquals(NumberAgr.SINGULAR, this.np5.getNumber());
		Assert.assertEquals("the curtain", this.realiser.realise(this.np5));

		this.np5.setPlural(true);
		Assert.assertEquals("the curtains", this.realiser.realise(this.np5));
	}

	/**
	 * Test the pronominalisation method for full NPs.
	 */
	@Test
	public void testPronominalisation() {
		// sing
		this.proTest1.setGender(Gender.FEMININE);
		this.proTest1.setPronominal(true);
		Assert.assertEquals("she", this.realiser.realise(this.proTest1));

		// sing, possessive
		this.proTest1.setPossessive(true);
		Assert.assertEquals("her", this.realiser.realise(this.proTest1));

		// plural pronoun
		this.proTest2.setPlural(true);
		this.proTest2.setPronominal(true);
		Assert.assertEquals("they", this.realiser.realise(this.proTest2));

		// accusative: "them"
		this.proTest2.setDiscourseFunction(DiscourseFunction.OBJECT);
		Assert.assertEquals("them", this.realiser.realise(this.proTest2));

		// accusative: "hers" in case function is prep_object && NP is
		// possessive
		this.proTest1.setDiscourseFunction(DiscourseFunction.PREP_OBJECT);
		Assert.assertEquals("hers", this.realiser.realise(this.proTest1));
	}

	/**
	 * Test premodification in NPS.
	 */
	@Test
	public void testPremodification() {
		this.man.setPremodifier(this.salacious);
		Assert.assertEquals("the salacious man", this.realiser
				.realise(this.man));

		this.woman.setPremodifier(this.beautiful);
		Assert.assertEquals("the beautiful woman", this.realiser
				.realise(this.woman));

		this.dog.setPremodifier(this.stunning);
		Assert
				.assertEquals("the stunning dog", this.realiser
						.realise(this.dog));
	}

	/**
	 * Test prepositional postmodification.
	 */
	@Test
	public void testPostmodification() {
		this.man.setComplement(this.onTheRock);
		Assert.assertEquals("the man on the rock", this.realiser
				.realise(this.man));

		this.woman.setComplement(this.behindTheCurtain);
		Assert.assertEquals("the woman behind the curtain", this.realiser
				.realise(this.woman));
	}

	/**
	 * Test possessive constructions.
	 */
	@Test
	public void testPossessive() {

		// simple possessive 's: 'a man's'
		NPPhraseSpec possNP = new NPPhraseSpec("a", "man");
		possNP.setPossessive(true);
		Assert.assertEquals("a man's", this.realiser.realise(possNP));

		// now set this possessive as specifier of the NP 'the dog'
		this.dog.setSpecifier(possNP);
		Assert.assertEquals("a man's dog", this.realiser.realise(this.dog));

		// convert possNP to pronoun and turn "a dog" into "his dog"
		// need to specify gender, as default is NEUTER
		possNP.setGender(Gender.MASCULINE);
		possNP.setPronominal(true);
		this.dog.setSpecifier(possNP);
		Assert.assertEquals("his dog", this.realiser.realise(this.dog));

		// make it slightly more complicated: "his dog's rock"
		this.dog.setPossessive(true); // his dog's
		this.np4.setSpecifier(this.dog); // his dog's rock (substituting "the"
		// for the
		// entire phrase)
		Assert.assertEquals("his dog's rock", this.realiser.realise(this.np4));
	}

	/**
	 * Test NP coordination.
	 */
	@Test
	public void testCoordination() {

		// simple coordination
		CoordinateNPPhraseSpec cnp1 = (CoordinateNPPhraseSpec) this.dog
				.coordinate(this.woman);
		Assert.assertEquals("the dog and the woman", this.realiser
				.realise(cnp1));

		// simple coordination with complementation of entire coordinate NP
		cnp1.setComplement(this.behindTheCurtain);
		Assert.assertEquals("the dog and the woman behind the curtain",
				this.realiser.realise(cnp1));

		// raise the specifier in this cnp
		Assert.assertEquals(true, cnp1.raiseSpecifier()); // should return
		// true as all
		// sub-nps have same spec
		// assertEquals("the dog and woman behind the curtain",
		// realiser.realise(cnp1));
	}

	/**
	 * Another battery of tests for NP coordination.
	 */
	@Test
	public void testCoordination2() {

		// simple coordination of complementised nps
		this.dog.setComplement(this.onTheRock);
		this.woman.setComplement(this.behindTheCurtain);
		CoordinateNPPhraseSpec cnp2 = (CoordinateNPPhraseSpec) this.dog
				.coordinate(this.woman);
		Assert.assertEquals(
				"the dog on the rock and the woman behind the curtain",
				this.realiser.realise(cnp2));

		// complementised coordinates + outer pp modifier
		cnp2.setPostmodifier(this.inTheRoom);
		Assert
				.assertEquals(
						"the dog on the rock and the woman behind the curtain in the room",
						this.realiser.realise(cnp2));

		// set the specifier for this cnp; should unset specifiers for all inner
		// coordinates
		Assert.assertTrue(cnp2.setSpecifier(new Determiner("every")));
		Assert.assertTrue(cnp2.hasSpecifier());
		Assert
				.assertEquals(
						"every dog on the rock and every woman behind the curtain in the room",
						this.realiser.realise(cnp2));

		// pronominalise one of the constituents
		this.dog.setPronominal(true); // ="it"

		// raising spec still returns true as spec has been set
		Assert.assertTrue(cnp2.raiseSpecifier());

		// CNP should be realised with pronominal internal const
		Assert.assertEquals(
				"it and every woman behind the curtain in the room",
				this.realiser.realise(cnp2));
	}

	/**
	 * Test possessives in coordinate NPs.
	 */
	@Test
	public void testPossessiveCoordinate() {
		// simple coordination
		CoordinateNPPhraseSpec cnp2 = (CoordinateNPPhraseSpec) this.dog
				.coordinate(this.woman);
		Assert.assertEquals("the dog and the woman", this.realiser
				.realise(cnp2));

		// set possessive -- wide-scope by default
		cnp2.setPossessive(true);
		Assert.assertEquals("the dog and the woman's", this.realiser
				.realise(cnp2));

		// set possessive with pronoun
		this.dog.setPronominal(true);
		this.dog.setPossessive(true);
		cnp2.setPossessive(true);
		Assert.assertEquals("its and the woman's", this.realiser.realise(cnp2));

	}

	/**
	 * Test A vs An.
	 */
	@Test
	public void testAAn() {
		NPPhraseSpec dog = new NPPhraseSpec("a", "dog");
		Assert.assertEquals("a dog", this.realiser.realise(dog));

		dog.addPremodifier("enormous");
		Assert.assertEquals("an enormous dog", this.realiser.realise(dog));

		NPPhraseSpec elephant = new NPPhraseSpec("a", "elephant");
		Assert.assertEquals("an elephant", this.realiser.realise(elephant));

		elephant.addPremodifier("big");
		Assert.assertEquals("a big elephant", this.realiser.realise(elephant));

		// test treating of plural specifiers
		dog.setPlural(true);
		Assert.assertEquals("some enormous dogs", this.realiser.realise(dog));
	}

	/**
	 * Test Modifier "guess" placement.
	 */
	@Test
	public void testModifier() {
		NPPhraseSpec dog = new NPPhraseSpec("a", "dog");
		dog.addModifier("angry");
		Assert.assertEquals("an angry dog", this.realiser.realise(dog));

		dog.addModifier("in the park");
		Assert.assertEquals("an angry dog in the park", this.realiser
				.realise(dog));

		NPPhraseSpec cat = new NPPhraseSpec("a", "cat");
		cat.addModifier(new AdjPhraseSpec("angry"));
		Assert.assertEquals("an angry cat", this.realiser.realise(cat));

		cat.addModifier(new PPPhraseSpec("in", "the park"));
		Assert.assertEquals("an angry cat in the park", this.realiser
				.realise(cat));

	}
}
