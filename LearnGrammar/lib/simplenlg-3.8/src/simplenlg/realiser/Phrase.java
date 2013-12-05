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

package simplenlg.realiser;

import simplenlg.features.Category;
import simplenlg.features.DiscourseFunction;

// TODO: Auto-generated Javadoc
/**
 * This is the basic interface that all phrases in the simplenlg.realiser
 * package implement.
 * 
 * @author agatt
 */
public interface Phrase extends Cloneable {

	/**
	 * Gets the category.
	 * 
	 * @return The {@link simplenlg.features.Category} of the phrase (e.g.
	 *         {@link simplenlg.features.Category#NOUN} if it's a noun phrase.
	 */
	Category getCategory();

	/**
	 * Sets the {@link simplenlg.features.DiscourseFunction} (subject, object,
	 * modifier etc)
	 * 
	 * @param d
	 *            The function
	 */
	void setDiscourseFunction(DiscourseFunction d);

	/**
	 * Gets the discourse function.
	 * 
	 * @return the {@link simplenlg.features.DiscourseFunction}
	 */
	DiscourseFunction getDiscourseFunction();

	/**
	 * Promotes a phrase to the level of a {@link simplenlg.realiser.TextSpec}.
	 * This is useful for realisation purposes, where one or more phrases need
	 * to be realised in, say, a paragraph.
	 * 
	 * @param level
	 *            A value of {@link simplenlg.realiser.DocStructure}
	 * 
	 * @return The {@link simplenlg.realiser.TextSpec}
	 */
	TextSpec promote(DocStructure level);

	/**
	 * Checks if is coordinate.
	 * 
	 * @return <code>true</code> if a phrase is also a coordinate phrase.
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase
	 */
	boolean isCoordinate();

	/**
	 * Coordinates this phrase with another phrase, returning a Phrase. The
	 * actual category of the returned phrase depends on the category of this
	 * phrase. For instance coordinating NPs returns an NP, which is itself a
	 * {@link simplenlg.realiser.CoordinatePhrase}, that is, a
	 * {@link simplenlg.realiser.CoordinateNPPhraseSpec}.
	 * 
	 * @param specs
	 *            The phrases with which this one is to be coordinated. This
	 *            list can be empty.
	 * 
	 * @return A {@link simplenlg.realiser.CoordinatePhrase} if
	 *         <code>specs.length > </code>, this phrase otherwise.
	 */
	<T extends Phrase> Phrase coordinate(T... specs);

	/**
	 * Set a paramater indicating that this phrase is elided. Phrases which are
	 * elided should not be realised, or should be realised as the empty string.
	 * 
	 * @param elided
	 *            whether to elide the phrase
	 */
	public void setElided(boolean elided);

	/**
	 * Check whether this phrase is elided.
	 * 
	 * @return <code>true</code> if the phrase is elided
	 * 
	 * @see #setElided(boolean)
	 */
	public boolean isElided();

	/**
	 * Clone.
	 * 
	 * @return A <code>Phrase</code> which is a clone of this one.
	 * 
	 * @throws CloneNotSupportedException
	 *             the clone not supported exception
	 */
	public Phrase clone() throws CloneNotSupportedException;
}
