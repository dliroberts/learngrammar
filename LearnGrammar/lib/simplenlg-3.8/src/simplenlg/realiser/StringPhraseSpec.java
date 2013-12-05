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

// TODO: Auto-generated Javadoc
/**
 * <code>StringPhraseSpec</code> represents a phrase specification as a string.
 * This is useful for canned text and such. String specs can be assigned a
 * grammatical category, such as {@link simplenlg.features.Category#ADJECTIVE},
 * which indicates that they function as "ready-made" adjectival phrases etc.
 * 
 * <P>
 * No grammatical operations are available with <code>StringPhraseSpec</code>s
 * except for coordination.
 * 
 * @author ereiter, agatt
 */
public class StringPhraseSpec extends PhraseSpec {

	/** The spec. */
	String spec;

	/** The plural. */
	boolean plural;

	/**
	 * Constructs an empty <code>StringPhraseSpec</code>.
	 */
	public StringPhraseSpec() {
		super();
		this.category = null;
		this.spec = null;
		this.plural = false;
	}

	/**
	 * Constructs a StringPhraseSpec with the specified <code>String</code>.
	 * 
	 * @param spec
	 *            The string
	 */
	public StringPhraseSpec(String spec) { // constructor
		this();
		this.spec = spec;
	}

	/**
	 * Constructs a <code>StirngPhraseSpec</code> with the specified string and
	 * category.
	 * 
	 * @param spec
	 *            The string
	 * @param cat
	 *            The category
	 */
	public StringPhraseSpec(String spec, Category cat) {
		this(spec);
		this.category = cat;
	}

	/**
	 * Gets the string.
	 * 
	 * @return The string wrapped in this <code>StringPhraseSpec</code>
	 */
	public String getString() {
		return this.spec;
	}

	/**
	 * Sets the grammatical category of the spec.
	 * 
	 * @param c
	 *            The {@link simplenlg.features.Category}
	 */
	public void setCategory(Category c) {
		this.category = c;
	}

	/**
	 * Sets whether this <code>StringPhraseSpec</code> should be considered
	 * plural, a useful feature in case this is added as an argument to a
	 * <code>VPPhraseSpec</code> which should agree with it inflectionally.
	 * 
	 * @param plur
	 *            - If <code>true</code>, this phrase is treated as plural.
	 */
	public void setPlural(boolean plur) {
		this.plural = plur;
	}

	/**
	 * Checks if is plural.
	 * 
	 * @return <code>true</code> if this <code>StringPhraseSpec</code> has been
	 *         set to plural
	 * 
	 * @see #setPlural(boolean)
	 */
	public boolean isPlural() {
		return this.plural;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Spec#realise(simplenlg.realiser.Realiser)
	 */
	@Override
	String realise(Realiser r) {
		return this.spec;
	}

	/**
	 * {@inheritDoc}
	 * <P>
	 * In the case of <code>StringPhraseSpec</code>, this method evaluates to
	 * <code>true</code> just in case <code>o</code> is a
	 * <code>StringPhraseSpec</code> and the two specs have the same category
	 * (possibly <code>null</code>) and wrap the same string (possibly
	 * <code>null</code>).
	 */
	@Override
	public boolean equals(Object o) {

		try {
			StringPhraseSpec sps = (StringPhraseSpec) o;

			if (this.spec != null && sps.spec != null) {
				return sps.spec.equals(this.spec)
						&& sps.category == this.category;
			} else {
				return sps.spec == this.spec && sps.category == this.category;
			}

		} catch (ClassCastException cce) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Phrase#coordinate(T[])
	 */
	public StringPhraseSpec coordinate(Phrase... coords) {

		if (coords.length == 0) {
			return this;
		}

		CoordinateStringPhraseSpec coord = new CoordinateStringPhraseSpec(this);

		try {

			for (Phrase p : coords) {
				coord.addCoordinates((StringPhraseSpec) p);
			}

			return coord;

		} catch (ClassCastException cce) {

			throw new SimplenlgException("Cannot coordinate: "
					+ "only phrases of the same type can be coordinated");
		}
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "(StringPhraseSpec " + this.spec + ")";
	}

}
