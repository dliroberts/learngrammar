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

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.NumberAgr;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.lexicon.lexicalitems.Verb;
import simplenlg.realiser.AdjPhraseSpec;
import simplenlg.realiser.CoordinateSPhraseSpec;
import simplenlg.realiser.CoordinateVPPhraseSpec;
import simplenlg.realiser.NPPhraseSpec;
import simplenlg.realiser.Phrase;
import simplenlg.realiser.SPhraseSpec;
import simplenlg.realiser.VPPhraseSpec;

// TODO: Auto-generated Javadoc
/**
 * These are tests for the verb phrase and coordinate VP classes.
 * 
 * @author agatt
 */
public class VPTest extends SimplenlgTest {

	/**
	 * Instantiates a new vP test.
	 * 
	 * @param name
	 *            the name
	 */
	public VPTest(String name) {
		super(name);
	}

	/**
	 * Some tests to check for an early bug which resulted in reduplication of
	 * verb particles in the past tense e.g. "fall down down" or "creep up up"
	 */
	@Test
	public void testVerbParticle() {
		Verb v = new Verb("fall down");
		Assert.assertEquals("down", v.getParticle());
		Assert.assertEquals("fall down", v.getBaseForm());
		Assert.assertEquals("fell down", v.getPast(Person.THIRD,
				NumberAgr.PLURAL));
		Assert.assertEquals("fallen down", v.getPastParticiple());
	}

	/**
	 * Tests for the tense and aspect.
	 */
	@Test
	public void testSimplePast() {
		// "fell down"
		this.fallDown.setTense(Tense.PAST);
		Assert.assertEquals("fell down", this.realiser.realise(this.fallDown));

	}

	/**
	 * Test tense aspect.
	 */
	@Test
	public void testTenseAspect() {
		// had fallen down
		this.fallDown.setTense(Tense.PAST);
		this.fallDown.setPerfect(true);
		Assert.assertEquals("had fallen down", this.realiser
				.realise(this.fallDown));

		// had been falling down
		this.fallDown.setProgressive(true);
		Assert.assertEquals("had been falling down", this.realiser
				.realise(this.fallDown));

		// will have been kicked
		this.kick.setPassive(true);
		this.kick.setPerfect(true);
		this.kick.setTense(Tense.FUTURE);
		Assert.assertEquals("will have been kicked", this.realiser
				.realise(this.kick));

		// will have been being kicked
		this.kick.setProgressive(true);
		Assert.assertEquals("will have been being kicked", this.realiser
				.realise(this.kick));

		// will not have been being kicked
		this.kick.setNegated(true);
		Assert.assertEquals("will not have been being kicked", this.realiser
				.realise(this.kick));

		// passivisation should suppress the complement
		this.kick.setComplement(this.man);
		Assert.assertEquals("will not have been being kicked", this.realiser
				.realise(this.kick));

		// de-passivisation should now give us "will have been kicking the man"
		this.kick.setPassive(false);
		Assert.assertEquals("will not have been kicking the man", this.realiser
				.realise(this.kick));

		// remove the future tense --
		// this is a test of an earlier bug that would still realise "will"
		this.kick.setTense(Tense.PRESENT);
		Assert.assertEquals("has not been kicking the man", this.realiser
				.realise(this.kick));
	}

	/**
	 * Test for realisation of VP complements.
	 */
	@Test
	public void testComplementation() {

		// was kissing Mary
		NPPhraseSpec mary = new NPPhraseSpec("Mary");
		mary.setDiscourseFunction(DiscourseFunction.OBJECT);
		this.kiss.setComplement(mary);
		this.kiss.setProgressive(true);
		this.kiss.setTense(Tense.PAST);
		Assert.assertEquals("was kissing Mary", this.realiser
				.realise(this.kiss));

		// add another complement -- should come out as "Mary and Susan"
		mary = mary.coordinate(new NPPhraseSpec("Susan"));
		this.kiss.setComplement(mary);
		Assert.assertEquals("was kissing Mary and Susan", this.realiser
				.realise(this.kiss));

		// passivise -- should make the direct object complement disappear
		// Note: The verb doesn't come out as plural because agreement
		// is determined by the sentential subjects and this VP isn't inside a
		// sentence
		this.kiss.setPassive(true);
		Assert.assertEquals("was being kissed", this.realiser
				.realise(this.kiss));

		// make it plural (this is usually taken care of in SPhraseSpec)
		this.kiss.setNumber(NumberAgr.PLURAL);
		Assert.assertEquals("were being kissed", this.realiser
				.realise(this.kiss));

		// depassivise and add post-mod: yields "was kissing Mary in the room"
		this.kiss.addPostmodifier(this.inTheRoom);
		this.kiss.setPassive(false);
		this.kiss.setNumber(NumberAgr.SINGULAR);
		Assert.assertEquals("was kissing Mary and Susan in the room",
				this.realiser.realise(this.kiss));

		// passivise again: should make direct object disappear, but not postMod
		// ="was being kissed in the room"
		this.kiss.setPassive(true);
		this.kiss.setNumber(NumberAgr.PLURAL);
		Assert.assertEquals("were being kissed in the room", this.realiser
				.realise(this.kiss));
	}

	/**
	 * This tests for the default complement ordering, relative to pre and
	 * postmodifiers.
	 */
	@Test
	public void testComplementation2() {
		// give the woman the dog
		this.woman.setDiscourseFunction(DiscourseFunction.INDIRECT_OBJECT);
		this.dog.setDiscourseFunction(DiscourseFunction.OBJECT);
		this.give.addComplement(this.dog, DiscourseFunction.OBJECT);
		this.give.addComplement(this.woman, DiscourseFunction.INDIRECT_OBJECT);
		Assert.assertEquals("gives the woman the dog", this.realiser
				.realise(this.give));

		// add a few premodifiers and postmodifiers
		this.give.addPremodifier("slowly");
		this.give.addPostmodifier(this.behindTheCurtain);
		this.give.addPostmodifier(this.inTheRoom);
		Assert
				.assertEquals(
						"slowly gives the woman the dog behind the curtain in the room",
						this.realiser.realise(this.give));

		// reset the arguments
		this.give.setComplement(this.dog, DiscourseFunction.OBJECT);
		this.give.setComplement(this.woman.coordinate(this.boy),
				DiscourseFunction.INDIRECT_OBJECT);

		// if we unset the passive, we should get the indirect objects
		// they won't be coordinated
		this.give.setPassive(false);
		Assert
				.assertEquals(
						"slowly gives the woman and the boy the dog behind the curtain in the room",
						this.realiser.realise(this.give));

		// set them to a coordinate instead
		// set ONLY the complement INDIRECT_OBJECT, leaves OBJECT intact
		this.give.setComplement(this.woman.coordinate(this.boy),
				DiscourseFunction.INDIRECT_OBJECT);
		List<Phrase> indirects = this.give
				.getComplements(DiscourseFunction.INDIRECT_OBJECT);
		Assert.assertEquals(1, indirects.size()); // only one indirect object
		// where
		// there were two before
		Assert
				.assertEquals(
						"slowly gives the woman and the boy the dog behind the curtain in the room",
						this.realiser.realise(this.give));
	}

	/**
	 * Test for complements raised in the passive case.
	 */
	@Test
	public void testPassiveComplement() {
		// add some arguments
		this.give.addComplement(this.dog, DiscourseFunction.OBJECT);
		this.give.addComplement(this.woman, DiscourseFunction.INDIRECT_OBJECT);
		Assert.assertEquals("gives the woman the dog", this.realiser
				.realise(this.give));

		// add a few premodifiers and postmodifiers
		this.give.addPremodifier("slowly");
		this.give.addPostmodifier(this.behindTheCurtain);
		this.give.addPostmodifier(this.inTheRoom);
		Assert
				.assertEquals(
						"slowly gives the woman the dog behind the curtain in the room",
						this.realiser.realise(this.give));

		// passivise: This should suppress "the dog"
		this.give.setPassive(true);
		Assert.assertEquals(
				"is slowly given the woman behind the curtain in the room",
				this.realiser.realise(this.give));
	}

	/**
	 * Test VP with sentential complements. This tests for structures like "said
	 * that John was walking"
	 */
	@Test
	public void testSententialComp() {
		// Create a sentence first
		SPhraseSpec s = new SPhraseSpec();
		s.setSubject(new NPPhraseSpec("John"));
		NPPhraseSpec maryAndSusan = new NPPhraseSpec("Mary")
				.coordinate(new NPPhraseSpec("Susan"));
		this.kiss.setComplement(maryAndSusan);
		s.setVerbPhrase(this.kiss);
		s.setProgressive(true);
		s.setTense(Tense.PAST);
		s.addPostmodifier(this.inTheRoom);

		Assert.assertEquals("John was kissing Mary and Susan in the room.",
				this.realiser.realise(s));

		// make the main VP past
		this.say.setTense(Tense.PAST);
		Assert.assertEquals("said", this.realiser.realise(this.say));

		// now add the sentence as complement of "say". Should make the sentence
		// subordinate
		// note that sentential punctuation is suppressed
		this.say.addComplement(s);
		Assert.assertEquals(
				"said that John was kissing Mary and Susan in the room",
				this.realiser.realise(this.say));

		// add a postModifier to the main VP
		// yields [says [that John was kissing Mary and Susan in the room]
		// [behind the curtain]]
		this.say.addPostmodifier(this.behindTheCurtain);
		Assert
				.assertEquals(
						"said that John was kissing Mary and Susan in the room behind the curtain",
						this.realiser.realise(this.say));

		// create a new sentential complement
		SPhraseSpec s2 = new SPhraseSpec(new NPPhraseSpec("all"),
				new Verb("be"), new AdjPhraseSpec("fine"));
		s2.setTense(Tense.FUTURE);
		Assert.assertEquals("All will be fine.", this.realiser.realise(s2));

		// add the new complement to the VP
		// yields [said [that John was kissing Mary and Susan in the room and
		// all will be fine] [behind the curtain]]
		s = s.coordinate(s2);
		this.say.setComplement(s);

		// first with outer complementiser suppressed
		((CoordinateSPhraseSpec) s).suppressComplementiser(false);
		Assert.assertEquals(
				"said that John was kissing Mary and Susan in the room "
						+ "and all will be fine behind the curtain",
				this.realiser.realise(this.say));

		// then with complementiser not suppressed and not aggregated
		((CoordinateSPhraseSpec) s).suppressComplementiser(true);
		Assert.assertEquals(
				"said that John was kissing Mary and Susan in the room and "
						+ "that all will be fine behind the curtain",
				this.realiser.realise(this.say));

	}

	/**
	 * Test VP coordination and aggregation:
	 * <OL>
	 * <LI>If the simplenlg.features of a coordinate VP are set, they should be
	 * inherited by its daughter VP;</LI>
	 * <LI>2. We can aggregate the coordinate VP so it's realised with one
	 * wide-scope auxiliary</LI>
	 */
	@Test
	public void testCoordination() {
		// simple case
		this.kiss.setComplement(this.dog);
		this.kick.setComplement(this.boy);
		VPPhraseSpec coord1 = this.kiss.coordinate(this.kick);
		coord1.setPerson(Person.THIRD);
		coord1.setTense(Tense.PAST);
		Assert.assertEquals("kissed the dog and kicked the boy", this.realiser
				.realise(coord1));

		// with negation: should be inherited by all components
		((CoordinateVPPhraseSpec) coord1).setNegated(true);
		Assert.assertEquals("did not kiss the dog and did not kick the boy",
				this.realiser.realise(coord1));

		// set a modal
		coord1.setModal("could");
		Assert
				.assertEquals(
						"could not have kissed the dog and could not have kicked the boy",
						this.realiser.realise(coord1));

		// set perfect and progressive
		coord1.setPerfect(true);
		coord1.setProgressive(true);
		Assert.assertEquals("could not have been kissing the dog and "
				+ "could not have been kicking the boy", this.realiser
				.realise(coord1));

		// now aggregate
		((CoordinateVPPhraseSpec) coord1).aggregateAuxiliary(true);
		Assert.assertEquals(
				"could not have been kissing the dog and kicking the boy",
				this.realiser.realise(coord1));
	}

}
