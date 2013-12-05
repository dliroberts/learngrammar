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

import java.util.List;

import simplenlg.lexicon.lexicalitems.Conjunction;

// TODO: Auto-generated Javadoc
/**
 * This class represents the basic interface that all coordinate phrases must
 * implement. Coordinate phrases can be viewed as a collection of daughter
 * phrases, conjoined by a linguistic conjunction. However, a coordinate phrase
 * also inherits properties and methods from its immediate simple phrase
 * supertype. Thus, a coordinate noun phrase is also a noun phrase.
 * 
 * <P>
 * For this reason, classes that implement this interface are also specified as
 * subclasses of the phrases they extend.
 * 
 * <P>
 * Also for this reason, the interface is parametrised, so that any class that
 * implements it specifies which subtype of {@link simplenlg.realiser.Phrase}
 * this <code>CoordinatePhrase</code> can include.
 * 
 * @param <T>
 *            The type of the daughters of this coordinate phrase, a subtype of
 *            {@link simplenlg.realiser.Phrase}.
 * 
 * @author agatt
 */

public interface CoordinatePhrase<T extends Phrase> extends Phrase {

	/**
	 * Add daughter phrases to this <code>CoordinatePhrase</code>, in addition
	 * to previously added daughters. Daughters are realised in the order in
	 * which they are added.
	 * 
	 * @param coords
	 *            The new daughters of the phrase.
	 */
	void addCoordinates(T... coords);

	/**
	 * Resets the daughter phrase of this <code>CoordinatePhrase</code>,
	 * replacing the old ones.
	 * 
	 * @param coords
	 *            The new daughter phrases.
	 */
	void setCoordinates(T... coords);

	/**
	 * Gets the coordinates.
	 * 
	 * @return The daughter phrases, a <code>java.util.List</code> whose
	 *         contents depend on the parameter <code>T</code>
	 */
	List<T> getCoordinates();

	/**
	 * Sets the conjunction of this phrase, overriding the default, which is
	 * {@link simplenlg.lexicon.lexicalitems.Constants#AND}
	 * 
	 * @param c
	 *            The new <code>Conjunction</code>
	 * 
	 * @see simplenlg.lexicon.lexicalitems.Conjunction
	 */
	void setConjunction(Conjunction c);

	/**
	 * Set the conjunction and override the default ("and") by passing a
	 * <code>String</code>. The supplied argument is internally initialised as a
	 * new instance of {@link simplenlg.lexicon.lexicalitems.Conjunction}.;
	 * 
	 * @param c
	 *            The <code>String</code> representation of the new conjunction.
	 */
	void setConjunction(String c);

	/**
	 * Gets the conjunction.
	 * 
	 * @return The {@link simplenlg.lexicon.lexicalitems.Conjunction}
	 */
	Conjunction getConjunction();

	/**
	 * Gets the conjunction as string.
	 * 
	 * @return The <code>String</code> representation (baseform) of the
	 *         conjunction.
	 */
	String getConjunctionAsString();
}
