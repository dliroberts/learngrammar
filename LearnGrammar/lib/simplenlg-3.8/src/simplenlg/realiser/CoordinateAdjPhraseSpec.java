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
 * The Class CoordinateAdjPhraseSpec.
 */
public class CoordinateAdjPhraseSpec extends AdjPhraseSpec implements
		CoordinatePhrase<AdjPhraseSpec> {

	/** The coordinator. */
	private CoordinatePhraseSet<AdjPhraseSpec> coordinator;

	/**
	 * Constructs a new <code>CoordinateNPPhraseSpec</code> with the given set
	 * of <code>NPPhraseSpec</code> children.
	 * 
	 * @param coords
	 *            the coords
	 */
	public CoordinateAdjPhraseSpec(AdjPhraseSpec... coords) {
		super();
		this.coordinator = new CoordinatePhraseSet<AdjPhraseSpec>(this, coords);
	}

	/**
	 * Sets the conjunction.
	 * 
	 * @param coord
	 *            The {@link simplenlg.lexicon.lexicalitems.Conjunction} to use
	 *            in this coordinate NP.
	 */
	public void setConjunction(Conjunction coord) {
		this.coordinator.setConjunction(coord);
	}

	/**
	 * Sets the String form of the conjunction to use in this coordinate NP. The
	 * method tries to find the conjunction with this string in the
	 * {@link simplenlg.lexicon.lexicalitems.Conjunction} <code>enum</code>,
	 * failing which it intialises a new <code>Conjunction</code> with the given
	 * baseform.
	 * 
	 * @param coord
	 *            The conjunction
	 */
	public void setConjunction(String coord) {
		this.coordinator.setConjunction(coord);
	}

	/**
	 * Gets the conjunction.
	 * 
	 * @return The {@link simplenlg.lexicon.lexicalitems.Conjunction} in this
	 *         coordinate NP.
	 */
	public Conjunction getConjunction() {
		return this.coordinator.getConjunction();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#getConjunctionAsString()
	 */
	public String getConjunctionAsString() {
		return this.coordinator.getConjunctionAsString();
	}

	/**
	 * Add new coordinates to this <code>CoordinateAdjPhraseSpec</code>.
	 * 
	 * @param coords
	 *            The new coordinates
	 */
	public void addCoordinates(AdjPhraseSpec... coords) {
		this.coordinator.addCoordinates(coords);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#setCoordinates(T[])
	 */
	public void setCoordinates(AdjPhraseSpec... coords) {
		this.coordinator.clearCoordinates();
		addCoordinates(coords);
	}

	/**
	 * Gets the coordinates.
	 * 
	 * @return The <code>java.util.List</code> of {@link NPPhraseSpec} children
	 *         of this <code>CoordinateNPPhraseSpec</code>.
	 */
	public List<AdjPhraseSpec> getCoordinates() {
		return this.coordinator.getCoordinates();
	}

	// **********************************************
	// REALISATION
	// **********************************************

	// only need to override the realise head method -- return the realisation
	// of the coordinates
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.HeadedPhraseSpec#realiseHead(simplenlg.realiser.Realiser
	 * )
	 */
	@Override
	String realiseHead(Realiser r) {
		return this.coordinator.realise(r);
	}

}
