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

package simplenlg.lexicon.lexicalitems;

import simplenlg.exception.LexiconException;
import simplenlg.features.Agreement;
import simplenlg.features.Category;
import simplenlg.lexicon.morph.NounInflection;

/**
 * This class extends the {@link simplenlg.lexicon.lexicalitems.ContentWord}
 * abstract class for nouns. Nouns can be specified for their
 * {@link simplenlg.features.Agreement} properties.
 * 
 * @author agatt
 */
public class Noun extends ContentWord {

	// plural form
	String pluralForm;

	// agreement type
	Agreement agreement;

	/**
	 * Initialises a noun with the given baseform.
	 * 
	 * @param baseform
	 *            The base form of the noun
	 */
	public Noun(String baseform) {
		super(baseform);
		this.category = Category.NOUN;
		this.agreement = null;
	}

	/**
	 * Instantiates a new noun with the given id and baseform.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 */
	public Noun(String id, String baseform) {
		this(baseform);
		setID(id);
	}

	/**
	 * Instantiates a new noun with the given id, baseform and citation form.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 * @param citationform
	 *            the citationform
	 */
	public Noun(String id, String baseform, String citationform) {
		this(id, baseform);
		setCitationForm(citationform);
	}

	/**
	 * Processes the baseform of this <code>Noun</code> to return the plural.
	 * The form is stored in a field the first time this method is called, thus
	 * reducing processing overhead on subsequent calls.
	 * <p>
	 * This method involves a call to
	 * {@link simplenlg.lexicon.morph.NounInflection#PLURAL} if the plural form
	 * is not already set.
	 * 
	 * @return The plural form (a <code>String</code>)
	 */
	public String getPlural() {

		if (this.pluralForm == null) {
			this.pluralForm = NounInflection.PLURAL.apply(this.baseForm);
		}

		return this.pluralForm;
	}

	/**
	 * Sets the plural form of this noun, which is useful if the return value of
	 * {@link #getPlural()} needs to be overridden.
	 * 
	 * @param plur
	 *            - The plural form of this noun.
	 */
	public void setPlural(String plur) {
		this.pluralForm = plur;
	}

	/**
	 * Gets the agreement code for this noun, specifying its count, mass, or
	 * group properties.
	 * 
	 * @return the agreement code
	 */
	public Agreement getAgreement() {
		return this.agreement;
	}

	/**
	 * Sets the agreement code for this noun, specifying its count, mass, or
	 * group properties.
	 * 
	 * @param agr
	 *            the new agreement
	 * @throws LexiconException
	 *             if the Agreement value is
	 *             {@link simplenlg.features.Agreement#FREE}, which cannot apply
	 *             to nouns.
	 */
	public void setAgreement(Agreement agr) {

		if (agr.appliesTo(this.category)) {
			this.agreement = agr;
		} else {
			throw new LexiconException(agr
					+ " cannot be applied to category NOUN");
		}
	}

	/**
	 * Check whether this is a count noun. Equivalent to
	 * <code>Noun.getAgreement() == Agreement.COUNT</code>
	 * 
	 * @return <code>true</code> if the agreement code of this noun has been set
	 *         to {@link simplenlg.features.Agreement#COUNT}
	 * @see #setAgreement(Agreement)
	 */
	public boolean isCountNoun() {
		return this.agreement == Agreement.COUNT;
	}

	/**
	 * Check whether this is a mass noun. Equivalent to
	 * <code>Noun.getAgreement() == Agreement.MASS</code>
	 * 
	 * @return <code>true</code> if the agreement code of this noun has been set
	 *         to {@link simplenlg.features.Agreement#MASS}
	 * @see #setAgreement(Agreement)
	 */
	public boolean isMassNoun() {
		return this.agreement == Agreement.MASS;
	}

	/**
	 * Check whether this is a group-denoting noun, allowing both singular and
	 * plural agreement. Equivalent to
	 * <code>Noun.getAgreement() == Agreement.GROUP</code>
	 * 
	 * @return <code>true</code> if the agreement code of this noun has been set
	 *         to {@link simplenlg.features.Agreement#GROUP}
	 * @see #setAgreement(Agreement)
	 */
	public boolean isGroupNoun() {
		return this.agreement == Agreement.GROUP;
	}

	/**
	 * Check whether this is a fixed plural noun, i.e. a noun that always
	 * exhibits plural agreement. Equivalent to
	 * <code>Noun.getAgreement() == Agreement.FIXED_PLUR</code>
	 * 
	 * @return <code>true</code> if the agreement code of this noun has been set
	 *         to {@link simplenlg.features.Agreement#FIXED_PLUR}
	 * @see #setAgreement(Agreement)
	 */
	public boolean isFixedPluralNoun() {
		return this.agreement == Agreement.FIXED_PLUR;
	}

	/**
	 * Check whether this is a fixed singular noun, i.e. a noun that always
	 * exhibits singular agreement and cannot be pluralised. Equivalent to
	 * <code>Noun.getAgreement() == Agreement.FIXED_SING</code>
	 * 
	 * @return <code>true</code> if the agreement code of this noun has been set
	 *         to {@link simplenlg.features.Agreement#FIXED_SING}
	 * @see #setAgreement(Agreement)
	 */
	public boolean isFixedSingularNoun() {
		return this.agreement == Agreement.FIXED_SING;
	}

}