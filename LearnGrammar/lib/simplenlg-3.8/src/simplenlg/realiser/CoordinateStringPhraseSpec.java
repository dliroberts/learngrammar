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

import simplenlg.features.Category;
import simplenlg.lexicon.lexicalitems.Conjunction;

// TODO: Auto-generated Javadoc
/**
 * This class represents a coordination of
 * {@link simplenlg.realiser.StringPhraseSpec}. It is mainly included for
 * completeness, and to give greater flexibility to users who prefer to
 * pre-specify canned text using Strings only.
 * 
 * @author agatt
 */
public class CoordinateStringPhraseSpec extends StringPhraseSpec implements
		CoordinatePhrase<StringPhraseSpec> {

	/** The coordinator. */
	private CoordinatePhraseSet<StringPhraseSpec> coordinator;

	/**
	 * Constructs a new <code>CoordinateNPPhraseSpec</code> with the given set
	 * of <code>StringPhraseSpec</code> children.
	 * 
	 * @param coords
	 *            The daughter phrases.
	 */
	public CoordinateStringPhraseSpec(StringPhraseSpec... coords) {
		super();
		this.coordinator = new CoordinatePhraseSet<StringPhraseSpec>(this);

		if (coords.length > 0) {
			this.coordinator.addCoordinates(coords);
			setCategory(coords[0].getCategory());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.CoordinatePhrase#setConjunction(simplenlg.lexicon.
	 * lexicalitems.Conjunction)
	 */
	public void setConjunction(Conjunction coord) {
		this.coordinator.setConjunction(coord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.StringPhraseSpec#setCategory(simplenlg.features.Category
	 * )
	 */
	@Override
	public void setCategory(Category c) {
		this.category = c;

		for (StringPhraseSpec string : this.coordinator.coordinates) {
			string.setCategory(c);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#setConjunction(java.lang.String)
	 */
	public void setConjunction(String coord) {
		this.coordinator.setConjunction(coord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#getConjunction()
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#addCoordinates(T[])
	 */
	public void addCoordinates(StringPhraseSpec... coords) {
		this.coordinator.addCoordinates(coords);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#setCoordinates(T[])
	 */
	public void setCoordinates(StringPhraseSpec... coords) {
		this.coordinator.clearCoordinates();
		addCoordinates(coords);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#getCoordinates()
	 */
	public List<StringPhraseSpec> getCoordinates() {
		return this.coordinator.getCoordinates();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.StringPhraseSpec#realise(simplenlg.realiser.Realiser)
	 */
	@Override
	String realise(Realiser r) {
		return this.coordinator.realise(r);
	}

}
