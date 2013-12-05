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
import simplenlg.features.ClauseStatus;
import simplenlg.features.DiscourseFunction;
import simplenlg.features.Form;
import simplenlg.lexicon.lexicalitems.Constants;
import simplenlg.lexicon.lexicalitems.Pronoun;

// TODO: Auto-generated Javadoc
/**
 * The Class PhraseSpec.
 */
public abstract class PhraseSpec extends Spec implements Phrase {

	/** The category. */
	Category category;

	/** The function. */
	DiscourseFunction function;

	/** The parent phrase. */
	Phrase parentPhrase;

	protected boolean elided;

	/**
	 * Instantiates a new phrase spec.
	 */
	public PhraseSpec() {
		this.elided = false;
		this.function = DiscourseFunction.NULL;
		this.parentPhrase = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Phrase#getCategory()
	 */
	public Category getCategory() {
		return this.category;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seesimplenlg.realiser.Phrase#setDiscourseFunction(simplenlg.features.
	 * DiscourseFunction)
	 */
	public void setDiscourseFunction(DiscourseFunction d) {
		this.function = d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Phrase#getDiscourseFunction()
	 */
	public DiscourseFunction getDiscourseFunction() {
		return this.function;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Spec#promote(simplenlg.realiser.DocStructure)
	 */
	@Override
	public TextSpec promote(DocStructure level) {
		return new TextSpec(level, this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Phrase#isCoordinate()
	 */
	public boolean isCoordinate() {
		return this instanceof CoordinatePhrase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public PhraseSpec clone() {

		try {
			return (PhraseSpec) super.clone();

		} catch (CloneNotSupportedException cnse) {
			return null;
		}
	}

	// utility routine to force a parameter to be a PhraseSpec
	/**
	 * Make phrase spec.
	 * 
	 * @param spec
	 *            the spec
	 * 
	 * @return the phrase spec
	 */
	protected PhraseSpec makePhraseSpec(Object spec) {

		if (spec instanceof String) {
			Pronoun p = Constants.getPronoun((String) spec);

			if (p != null) {
				NPPhraseSpec np = new NPPhraseSpec();
				np.setPronominal(true);
				np.setPronoun(p);
				np.setNumber(p.getNumber());
				np.setGender(p.getGender());
				np.setPerson(p.getPerson());
				return np;
			} else {
				return new StringPhraseSpec((String) spec);
			}
		}

		return (PhraseSpec) spec;
	}

	/*
	 * Turns a phrase spec into a constituent of another phrase.
	 */
	/**
	 * Make constituent.
	 * 
	 * @param arg
	 *            the arg
	 * @param func
	 *            the func
	 * 
	 * @return the phrase
	 */
	Phrase makeConstituent(Object arg, DiscourseFunction func) {
		Phrase p = makePhraseSpec(arg);
		((Spec) p).setParentSpec(this);

		// if this is a pronoun, check that it's the right one for the function
		// if( p instanceof NPPhraseSpec && ((NPPhraseSpec) p).isPronominal())

		if (func != null) {
			p.setDiscourseFunction(func);
		}

		if (p instanceof SPhraseSpec) {
			SPhraseSpec s = (SPhraseSpec) p;
			s.setClauseStatus(ClauseStatus.SUBORDINATE);

			if (func.equals(DiscourseFunction.OBJECT)
					|| func.equals(DiscourseFunction.INDIRECT_OBJECT)
					|| func.equals(DiscourseFunction.PREDICATIVE_COMPLEMENT)) {

				if (s.getForm().equals(Form.IMPERATIVE)) {
					s.setForm(Form.INFINITIVE);
					s.suppressComplementiser(true);

				} else if (s.getForm().equals(Form.GERUND) && !s.hasSubject()) {
					s.suppressComplementiser(true);
				}

			} else if (func.equals(DiscourseFunction.SUBJECT)) {
				s.setForm(Form.GERUND);
				s.suppressComplementiser(true);
			}

			return s;
		}

		return p;
	}

	public boolean isElided() {
		return this.elided;
	}

	public void setElided(boolean elided) {
		this.elided = elided;
	}

}
