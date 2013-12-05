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

import simplenlg.realiser.CoordinatePPPhraseSpec;
import simplenlg.realiser.NPPhraseSpec;

// TODO: Auto-generated Javadoc
/**
 * This class groups together some tests for prepositional phrases and
 * coordinate prepositional phrases.
 * 
 * @author agatt
 */
public class PPTest extends SimplenlgTest {

	/**
	 * Instantiates a new pP test.
	 * 
	 * @param name
	 *            the name
	 */
	public PPTest(String name) {
		super(name);
	}

	/**
	 * Basic test for the pre-set PP fixtures.
	 */
	@Test
	public void testBasic() {
		Assert.assertEquals("in the room", this.realiser
				.realise(this.inTheRoom));
		Assert.assertEquals("behind the curtain", this.realiser
				.realise(this.behindTheCurtain));
		Assert.assertEquals("on the rock", this.realiser
				.realise(this.onTheRock));
	}

	/**
	 * Test for coordinate NP complements of PPs.
	 */
	@Test
	public void testComplementation() {
		this.inTheRoom.setComplement(new NPPhraseSpec("the", "room")
				.coordinate(new NPPhraseSpec("a", "car")));
		Assert.assertEquals("in the room and a car", this.realiser
				.realise(this.inTheRoom));
	}

	/**
	 * Test for PP coordination.
	 */
	public void testCoordination() {
		// simple coordination
		CoordinatePPPhraseSpec coord1 = (CoordinatePPPhraseSpec) this.inTheRoom
				.coordinate(this.behindTheCurtain);
		Assert.assertEquals("in the room and behind the curtain", this.realiser
				.realise(coord1));

		// change the conjunction
		coord1.setConjunction("or");
		Assert.assertEquals("in the room or behind the curtain", this.realiser
				.realise(coord1));

		// new coordinate
		CoordinatePPPhraseSpec coord2 = (CoordinatePPPhraseSpec) this.onTheRock
				.coordinate(this.underTheTable);
		coord2.setConjunction("or");
		Assert.assertEquals("on the rock or under the table", this.realiser
				.realise(coord2));

		// coordinate two coordinates
		CoordinatePPPhraseSpec coord3 = (CoordinatePPPhraseSpec) coord1
				.coordinate(coord2);
		
		String text = this.realiser.realise(coord3);
		Assert
				.assertEquals(
						"in the room or behind the curtain and on the rock or under the table",
						text);
	}
}
