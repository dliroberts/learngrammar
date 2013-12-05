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

import simplenlg.features.Agreement;
import simplenlg.features.Category;
import simplenlg.features.Deixis;
import simplenlg.features.Quantification;

/**
 * Class representing determiners. Determiners have a features, which they share
 * in common with pronouns, namely Deixis: a value of
 * {@link simplenlg.features.Deixis}.
 * 
 * <P>
 * In addition, determiners take an {@link simplenlg.features.Agreement} feature
 * value, which specifies what kind of agreement features a noun needs to have
 * in order for the determiner to combine with it. For example, the determiner
 * <I>both</I> should take the feature
 * {@link simplenlg.features.Agreement#FIXED_PLUR}, because it can only
 * felicitously combine with plural nouns.
 * 
 * @author agatt
 */
public class Determiner extends Word {

	// acronynms that start with these letters need "an" instead of "a"
	// (usually)
	private final static String ACRONYM_AN_LETTERS = "AEFHILMNORSX";

	// deixis feature
	Deixis deixis;

	// quantification feature
	Quantification quantification;

	// agr feature
	Agreement agreement;

	/**
	 * Instantiates a new determiner with the given baseform.
	 * 
	 * @param baseform
	 *            the baseform
	 */
	public Determiner(String baseform) {
		super(baseform);
		this.category = Category.DETERMINER;
	}

	/**
	 * Instantiates a new determiner with the given id and baseform.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 */
	public Determiner(String id, String baseform) {
		this(baseform);
		setID(id);
	}

	/**
	 * Instantiates a new determiner with the given id, baseform and citation
	 * form.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 * @param citationform
	 *            the citationform
	 */
	public Determiner(String id, String baseform, String citationform) {
		this(id, baseform);
		setCitationForm(citationform);
	}

	/**
	 * Instantiates a new determiner.
	 * 
	 * @param baseform
	 *            the baseform
	 * @param definite
	 *            the definite
	 * @deprecated As of version 3.7
	 */
	@Deprecated
	public Determiner(String baseform, boolean definite) {
		this(baseform);

	}

	/**
	 * Checks if this is a definite determiner. Equivalent to
	 * <code>Determiner.getQuantification() == Quantification.DEFINITE</code>
	 * 
	 * @return <code>true</code> if this is a definite determiner
	 */
	public boolean isDefinite() {
		return this.quantification == Quantification.DEFINITE;
	}

	/**
	 * Get the deixis feature of this determiner
	 * 
	 * @return the deixis, if set, <code>null</code> otherwise
	 */
	public Deixis getDeixis() {
		return this.deixis;
	}

	/**
	 * Set the Deixis feature of this determiner
	 * 
	 * @param deixis
	 *            the new deixis feature
	 */
	public void setDeixis(Deixis deixis) {
		this.deixis = deixis;
	}

	/**
	 * Gets the quantification feature of this determiner
	 * 
	 * @return the quantification feature, if set, <code>null</code> otherwise
	 */
	public Quantification getQuantification() {
		return this.quantification;
	}

	/**
	 * Sets the quantification feature for a determiner
	 * 
	 * @param quantification
	 *            the new quantification feature
	 */
	public void setQuantification(Quantification quantification) {
		this.quantification = quantification;
	}

	/**
	 * Gets the agreement feature value of the determiner. This should be
	 * interpreted as specifying the agreement features that a noun must have in
	 * order to be specified by this determiner.
	 * 
	 * @return the agreement feature
	 */
	public Agreement getAgreement() {
		return this.agreement;
	}

	/**
	 * Sets the agreement feature of this determiner, which specifies the
	 * agreement features that a noun must have in order to be specified by this
	 * determiner.
	 * 
	 * @param agreement
	 *            the new agreement feature
	 */
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}

	/**
	 * Gets the form of a determiner that agrees phonetically with a noun. This
	 * always returns the baseform, except in the case of the indefinite
	 * determiner, where it returns "an" in some cases (such as when the noun
	 * begins with a vowel).
	 * 
	 * @param phonAgreementString
	 *            the string with which the determiner has to agree phonetically
	 * 
	 * @return the form
	 */
	public String getForm(String phonAgreementString) {

		if (this.baseForm.equals("a")) {
			char firstChar = phonAgreementString.charAt(0);

			// not an acronyms
			if (Character.isLowerCase(firstChar)) {

				if (firstChar == 'a' || firstChar == 'e' || firstChar == 'i'
						|| firstChar == 'o' || firstChar == 'u') {
					return "an";
				} else if (firstChar == 'x'
						&& phonAgreementString.charAt(1) == '-') {
					return "an";
				}
			} else if (Determiner.ACRONYM_AN_LETTERS.indexOf(firstChar) >= 0) {
				return "an";
			}
		}

		return this.baseForm;
	}

	/**
	 * Gets the determiner with a specific baseform.
	 * 
	 * @param word
	 *            A <code>String</code>
	 * 
	 * @return The <code>Determiner</code> object which matches
	 *         <code>word</code>
	 * @deprecated Use {@link Constants#getDeterminer(String)} instead
	 */
	@Deprecated
	public static Determiner getDeterminer(String word) {
		return Constants.getDeterminer(word);
	}

}
