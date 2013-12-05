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

import simplenlg.lexicon.lexicalitems.Constants;
import simplenlg.realiser.AdvPhraseSpec;
import simplenlg.realiser.CoordinateAdjPhraseSpec;
import simplenlg.realiser.SPhraseSpec;
import simplenlg.realiser.StringPhraseSpec;

// TODO: Auto-generated Javadoc
/**
 * This class incorporates a few tests for adjectival phrases.
 * Also tests for adverbial phrase specs, which are very similar
 * 
 * @author agatt
 */
public class AdjPTest extends SimplenlgTest {

	/**
	 * Instantiates a new adj p test.
	 * 
	 * @param name
	 *            the name
	 */
	public AdjPTest(String name) {
		super(name);
	}

	/**
	 * Test premodification & coordination of Adjective Phrases (Not much else
	 * to simplenlg.test)
	 */
	@Test
	public void testAdj() {

		// form the adjphrase "incredibly salacious"
		this.salacious.addPremodifier("incredibly");
		Assert.assertEquals("incredibly salacious", this.realiser
				.realise(this.salacious));

		// form the adjphrase "incredibly beautiful"
		this.beautiful.addPremodifier("amazingly");
		Assert.assertEquals("amazingly beautiful", this.realiser
				.realise(this.beautiful));

		// coordinate the two aps
		CoordinateAdjPhraseSpec coordap = (CoordinateAdjPhraseSpec) this.salacious
				.coordinate(this.beautiful);
		Assert.assertEquals("incredibly salacious and amazingly beautiful",
				this.realiser.realise(coordap));

		// changing the inner conjunction
		coordap.setConjunction(Constants.OR);
		Assert.assertEquals("incredibly salacious or amazingly beautiful",
				this.realiser.realise(coordap));

		// coordinate this with a new AdjPhraseSpec
		CoordinateAdjPhraseSpec coord2 = (CoordinateAdjPhraseSpec) coordap
				.coordinate(this.stunning);
		Assert.assertEquals(
				"incredibly salacious or amazingly beautiful and stunning",
				this.realiser.realise(coord2));

		// add a premodifier the coordinate phrase, yielding
		// "seriously and undeniably incredibly salacious or amazingly beautiful
		// and stunning"
		StringPhraseSpec preMod = new StringPhraseSpec("seriously")
				.coordinate(new StringPhraseSpec("undeniably"));
		coord2.addPremodifier(preMod);
		Assert
				.assertEquals(
						"seriously and undeniably incredibly salacious or amazingly beautiful and stunning",
						this.realiser.realise(coord2));

		// adding a coordinate rather than coordinating should give a different
		// result
		coordap.addCoordinates(this.stunning);
		Assert.assertEquals(
				"incredibly salacious, amazingly beautiful or stunning",
				this.realiser.realise(coordap));

	}
	
	/**
	 * Simple test of adverbials
	 */
	@Test
	public void testAdv() {

		SPhraseSpec sent = new SPhraseSpec("John", "eat");
		
		AdvPhraseSpec adv = new AdvPhraseSpec("quickly");
		
		sent.addPremodifier(adv);
		
		Assert.assertEquals("John quickly eats.", this.realiser
				.realise(sent));
		
		adv.addPremodifier("very");
		
		Assert.assertEquals("John very quickly eats.", this.realiser
				.realise(sent));

	}
}
