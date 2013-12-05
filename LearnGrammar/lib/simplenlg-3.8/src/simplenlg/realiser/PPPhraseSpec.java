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

import simplenlg.exception.SimplenlgException;
import simplenlg.features.Category;
import simplenlg.features.DiscourseFunction;
import simplenlg.lexicon.lexicalitems.Constants;
import simplenlg.lexicon.lexicalitems.Preposition;

// TODO: Auto-generated Javadoc
/**
 * This class represents a prepositional phrase.
 * 
 * <P>
 * Typically, a <code>PPPhraseSpec</code> consists of:
 * <UL>
 * <LI>the head, a {@link simplenlg.lexicon.lexicalitems.Preposition}</LI>
 * <LI>zero or more object(s) phrases.</LI>
 * </UL>
 * These are linearised in the above order
 * 
 * @author ereiter, agatt
 */
public class PPPhraseSpec extends HeadedPhraseSpec<Preposition> {

	// **************************************************
	// constructors
	// **************************************************

	/**
	 * Constructs an empty PPPhraseSpec.
	 */
	public PPPhraseSpec() {
		super();
		this.category = Category.PREPOSITION;
	}

	/**
	 * Constructs PPPhraseSpec headed by the preposition whose baseform is the
	 * specified string.
	 * 
	 * @param preposition
	 *            The baseform of the head
	 */
	public PPPhraseSpec(String preposition) {
		this();
		this.head = new Preposition(preposition);
	}

	/**
	 * Constructs a <code>PPPhraseSpec</code> with the specified head.
	 * 
	 * @param prep
	 *            The head, a {@link simplenlg.lexicon.lexicalitems.Preposition}
	 */
	public PPPhraseSpec(Preposition prep) {
		this();
		this.head = prep;
	}

	/**
	 * Constructs a <code>PPPhraseSpec</code> with the specified preposition and
	 * complement.
	 * 
	 * @param preposition
	 *            The preposition, a <code>String</code>
	 * @param comp
	 *            The complement, a <code>String</code> or
	 *            {@link simplenlg.realiser.Phrase}
	 */
	public PPPhraseSpec(String preposition, Object comp) {
		this(preposition);
		setComplement(comp);
	}

	/**
	 * Constructs a <code>PPPhraseSpec</code> with the specified preposition and
	 * complement.
	 * 
	 * @param prep
	 *            The head, a {@link simplenlg.lexicon.lexicalitems.Preposition}
	 * @param comp
	 *            The complement, a {@link simplenlg.realiser.Phrase}
	 */
	public PPPhraseSpec(Preposition prep, Phrase comp) {
		this.head = prep;
		setComplement(comp);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.HeadedPhraseSpec#setHead(java.lang.String)
	 */
	@Override
	public void setHead(String h) {
		this.head = Constants.getPreposition(h);

		if (this.head == null) {
			this.head = new Preposition(h);
		}
	}
	
	public void setPreposition(String preposition) {
		setHead(preposition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.HeadedPhraseSpec#addComplement(java.lang.Object)
	 */
	@Override
	public void addComplement(Object comp) {
		Phrase complement = makeConstituent(comp, DiscourseFunction.PREP_OBJECT);
		this.complements.add(complement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Phrase#coordinate(T[])
	 */
	public PPPhraseSpec coordinate(Phrase... coords) {

		if (coords.length == 0) {
			return this;
		}

		CoordinatePPPhraseSpec coord = new CoordinatePPPhraseSpec(this);

		try {

			for (Phrase p : coords) {
				coord.addCoordinates((PPPhraseSpec) p);
			}

			return coord;

		} catch (ClassCastException cce) {
			throw new SimplenlgException("Cannot coordinate: "
					+ "only phrases of the same type can be coordinated");
		}

	}

}
