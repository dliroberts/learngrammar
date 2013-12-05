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

import junit.framework.TestCase;

import org.junit.Before;

import simplenlg.realiser.AdjPhraseSpec;
import simplenlg.realiser.NPPhraseSpec;
import simplenlg.realiser.PPPhraseSpec;
import simplenlg.realiser.Realiser;
import simplenlg.realiser.VPPhraseSpec;

// TODO: Auto-generated Javadoc
/**
 * This class is the base class for all JUnit simplenlg.test cases for
 * simplenlg. It sets up a a JUnit fixture, i.e. the basic objects (basic
 * constituents) that all other tests can use.
 * 
 * @author agatt
 */
public abstract class SimplenlgTest extends TestCase {

	/** The realiser. */
	Realiser realiser;

	/** The pro test2. */
	NPPhraseSpec man, woman, dog, boy, np4, np5, np6, proTest1, proTest2;

	/** The salacious. */
	AdjPhraseSpec beautiful, stunning, salacious;

	/** The under the table. */
	PPPhraseSpec onTheRock, behindTheCurtain, inTheRoom, underTheTable;

	/** The say. */
	VPPhraseSpec kick, kiss, walk, talk, getUp, fallDown, give, say;

	/**
	 * Instantiates a new simplenlg test.
	 * 
	 * @param name
	 *            the name
	 */
	public SimplenlgTest(String name) {
		super(name);
	}

	/**
	 * Set up the variables we'll need for this simplenlg.test to run (Called
	 * automatically by JUnit)
	 */
	@Override
	@Before
	protected void setUp() {
		this.realiser = new Realiser();

		this.man = new NPPhraseSpec("the", "man");
		this.woman = new NPPhraseSpec("the", "woman");
		this.dog = new NPPhraseSpec("the", "dog");
		this.boy = new NPPhraseSpec("the", "boy");

		this.beautiful = new AdjPhraseSpec("beautiful");
		this.stunning = new AdjPhraseSpec("stunning");
		this.salacious = new AdjPhraseSpec("salacious");

		this.onTheRock = new PPPhraseSpec("on");
		this.np4 = new NPPhraseSpec("the", "rock");
		this.onTheRock.setComplement(this.np4);

		this.behindTheCurtain = new PPPhraseSpec("behind");
		this.np5 = new NPPhraseSpec("the", "curtain");
		this.behindTheCurtain.setComplement(this.np5);

		this.inTheRoom = new PPPhraseSpec("in");
		this.np6 = new NPPhraseSpec("the", "room");
		this.inTheRoom.setComplement(this.np6);

		this.underTheTable = new PPPhraseSpec("under");
		this.underTheTable.setComplement(new NPPhraseSpec("the", "table"));

		this.proTest1 = new NPPhraseSpec("the", "singer");
		this.proTest2 = new NPPhraseSpec("some", "person");

		this.kick = new VPPhraseSpec("kick");
		this.kiss = new VPPhraseSpec("kiss");
		this.walk = new VPPhraseSpec("walk");
		this.talk = new VPPhraseSpec("talk");
		this.getUp = new VPPhraseSpec("get up");
		this.fallDown = new VPPhraseSpec("fall down");
		this.give = new VPPhraseSpec("give");
		this.say = new VPPhraseSpec("say");
	}
}
