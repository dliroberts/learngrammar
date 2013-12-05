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
package simplenlg.features;

/**
 * A small enumeration of the grammatical functions that a phrase (typically,
 * but not exclusively an NP) can play, for example, Subject or Object. Any
 * function has a priority value associated with it, reflecting its "discourse
 * salience". This refers to the fairly standard idea that NPs realised as
 * subjects are more prominent than objects, and so on.
 * 
 * <p>
 * Salience ordering among functions is realised as a value (stored as a
 * <code>double</code>, where a lower value indicates higher salience. By
 * default, functions are ordered as:
 * 
 * <p>
 * <code> SUBJECT >> OBJECT >> INDIRECT_OBJECT >> PREP_OBJECT >> RELATUM
 * >> INTENSIVE_OBJECT >> MODIFIER >> NULL </code>
 * 
 * <p>
 * This default behaviour can be changed: see {@link #getSalience()} and
 * {@link #setSalience(double sal)}.
 * 
 * @author agatt
 */
public enum DiscourseFunction implements Feature {

	/** Subject of sentence. E.g. <i><u>John</u> gave Mary a book</i> */
	SUBJECT(6),

	/** Object of VP. E.g. <i>John gave Mary <u>a book</u></i> */
	OBJECT(5),

	/** Indirect object. E.g. <i>John gave <u>Mary</u> a book</i> */
	INDIRECT_OBJECT(4),

	/** Predicative complement. E.g. <I>John is <u>a cool bloke</u></I> */
	PREDICATIVE_COMPLEMENT(3.5),

	/** Prepositional object. E.g. <i>John gave a book to <u>Mary</u></i> */
	PREP_OBJECT(3),

	/** Premodifiers of headed phrases */
	PREMODIFIER(2),

	/** Postmodifiers of headed phrases */
	POSTMODIFIER(2),

	/** Fronted modifiers in clauses */
	FRONT_MODIFIER(2),

	/** Pre- or post-modifier of a head. */
	MODIFIER(2),

	/** Cue-phrase in a sentence */
	CUE_PHRASE(1),

	/**
	 * The null function: every phrase has this as default unless otherwise set.
	 */
	NULL(-1);

	/** The salience. */
	private Double salience;

	/**
	 * Instantiates a new discourse function.
	 * 
	 * @param sal
	 *            the sal
	 */
	private DiscourseFunction(double sal) {
		this.salience = new Double(sal);
	}

	/**
	 * Get the salience of this grammatical function.
	 * 
	 * @return A <code>double</code> representing the salience value.
	 */
	public double getSalience() {
		return this.salience;
	}

	/**
	 * Set the salience of a function, reflecting its prominence relative to the
	 * other functions.
	 * 
	 * @param sal
	 *            A double representing the salience value for phrases with this
	 *            function
	 */
	public void setSalience(double sal) {
		this.salience = new Double(sal);
	}

	/**
	 * Get the next highest ranked DiscourseFunction.
	 * 
	 * @return The next DiscourseFunction highest in prominence, if one exists,
	 *         <code>null</code> otherwise.
	 */
	public DiscourseFunction nextUp() {
		switch (this) {
		case OBJECT:
			return SUBJECT;
		case INDIRECT_OBJECT:
			return OBJECT;
		default:
			return null;
		}
	}

	/**
	 * Next down.
	 * 
	 * @return the discourse function
	 */
	public DiscourseFunction nextDown() {

		switch (this) {
		case SUBJECT:
			return OBJECT;
		case OBJECT:
			return INDIRECT_OBJECT;
		default:
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.features.Feature#appliesTo(simplenlg.features.Category)
	 */
	public boolean appliesTo(Category cat) {
		return cat.equals(Category.NOUN);
	}

	/**
	 * Compare the relative salience of two discourse functions. This is an
	 * integer representing the prominence of this function relative to
	 * <code>f</code>: 0 if they have the same prominence, -1 if this function
	 * is more prominent 1 if <code>f</code> is more prominent
	 * 
	 * @param f
	 *            A <code>DiscourseFunction</code>
	 * 
	 * @return the relative salience
	 */

	public int compareSalience(DiscourseFunction f) {
		return -this.salience.compareTo(f.getSalience());
	}

	/**
	 * Utility method which gets the case value corresponding to a discourse
	 * function. This is mainly useful for realising pronouns in the correct
	 * form. For example, if the 3rd person, singular masculine pronoun
	 * <I>he</I> is included in a sentence as the object of the vrb phrase (i.e.
	 * the discourse function of the corresponding <code>NPPhraseSpec</code> is
	 * {@link #OBJECT}, then the corresponding case value is
	 * {@link simplenlg.features.Case#ACCUSATIVE}.
	 * 
	 * @return the case value, or <code>null</code> in case the function is
	 *         {@link #NULL}
	 */
	public Case getCaseValue() {
		switch (this) {
		case SUBJECT:
		case FRONT_MODIFIER:
		case PREMODIFIER:
		case POSTMODIFIER:
			return Case.NOMINATIVE;
		case OBJECT:
		case PREP_OBJECT:
			return Case.ACCUSATIVE;
		default:
			return null;
		}
	}

}
