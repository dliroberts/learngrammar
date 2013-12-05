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
import simplenlg.features.Case;
import simplenlg.features.Category;
import simplenlg.features.Deixis;
import simplenlg.features.DiscourseFunction;
import simplenlg.features.Gender;
import simplenlg.features.InflectionType;
import simplenlg.features.NumberAgr;
import simplenlg.features.Person;
import simplenlg.features.Polarity;
import simplenlg.features.Possession;
import simplenlg.features.Quantification;
import simplenlg.features.Reflexivity;
import simplenlg.lexicon.LexiconInterface;

/**
 * Class representing pronouns. This class inherits a number of methods from the
 * {@link simplenlg.lexicon.lexicalitems.Noun} class. In addition, pronouns can
 * be specified for the following features:
 * 
 * <OL>
 * <LI>polarity: a value of {@link simplenlg.features.Polarity}, which indicates
 * if the pronoun is a negative polarity item;
 * <LI>quantification: a value of {@link simplenlg.features.Quantification};
 * <LI>possession: a value of {@link simplenlg.features.Possession}, indicating
 * whether the pronoun is possessive;
 * <LI>reflexivity: a value of {@link simplenlg.features.Reflexivity},
 * indicating whether the pronoun is reflexive (e.g. <I>himself</I>) or not;
 * <LI>deixis: a value of {@link simplenlg.features.Deixis}, indicating whether
 * it is a demonstrative pronoun or not;
 * <LI>case: a value of {@link simplenlg.features.Case}, indicating whether the
 * pronoun occurs as subject, object or both;
 * <LI>person: a value of {@link simplenlg.features.Person};
 * <LI>number: a value of {@link simplenlg.features.NumberAgr};
 * <LI>gender: a value of {@link simplenlg.features.Gender};
 * <LI>expletive: a boolean value specifying whether this is an expletive
 * pronoun.
 * </OL>
 * 
 * @author agatt
 */

public class Pronoun extends Noun {

	Polarity polarity;

	Quantification quantification;

	Possession possession;

	Reflexivity reflexivity;

	Deixis deixis;

	Case caseValue;

	Person person;

	NumberAgr number;

	Gender gender;

	String objectForm;

	boolean expletive;

	// ****************************************
	// CONSTRUCTORS
	// ****************************************

	/**
	 * Instantiates a new pronoun with the given baseform.
	 * 
	 * @param base
	 *            the base
	 */
	public Pronoun(String base) {
		super(base);
		this.person = Person.THIRD;
		this.number = NumberAgr.SINGULAR;
		this.gender = null;
		this.category = Category.PRONOUN;
		this.expletive = false;
		this.inflectionType = InflectionType.INVARIANT;
		this.agreement = Agreement.COUNT;
		this.caseValue = Case.NOM_ACC;
		this.possession = null;
		this.reflexivity = Reflexivity.NON_REFLEXIVE;
		this.quantification = Quantification.DEFINITE;
		this.deixis = Deixis.NON_DEMONSTRATIVE;
	}

	/**
	 * Instantiates a new pronoun with the given id and baseform.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 */
	public Pronoun(String id, String baseform) {
		this(baseform);
		setID(id);
	}

	/**
	 * Instantiates a new pronoun with the given id, baseform and citation form.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 * @param citationform
	 *            the citation form
	 */
	public Pronoun(String id, String baseform, String citationform) {
		this(id, baseform);
		setCitationForm(citationform);
	}

	/**
	 * Instantiates a new pronoun with a given baseform, setting the pronoun's
	 * parent lexicon.
	 * 
	 * @param base
	 *            the base
	 * @param lex
	 *            the lex
	 * @deprecated As of Version 3.7, the parent lexicon should be set via
	 *             {@link #setParentLexicon(LexiconInterface)}
	 */
	@Deprecated
	public Pronoun(String base, LexiconInterface lex) {
		this(base);
		this.parentLexicon = lex;
	}

	/**
	 * Instantiates a new pronoun with a baseform, specifying its person, number
	 * and gender features.
	 * 
	 * @param base
	 *            the baseform
	 * @param p
	 *            the person
	 * @param n
	 *            the number feature
	 * @param g
	 *            the gender feature
	 */
	public Pronoun(String base, Person p, NumberAgr n, Gender g) {
		this(base);
		this.person = p;
		this.number = n;
		this.gender = g;
	}

	/**
	 * Instantiates a new pronoun with a baseform, specifying its case, person,
	 * number and gender features.
	 * 
	 * @param base
	 *            the baseform
	 * @param c
	 *            the case feature
	 * @param p
	 *            the person feature
	 * @param n
	 *            the number feature
	 * @param g
	 *            the gender feature
	 */
	public Pronoun(String base, Case c, Person p, NumberAgr n, Gender g) {
		this(base);
		this.caseValue = c;
		this.person = p;
		this.number = n;
		this.gender = g;
	}

	/**
	 * Instantiates a new pronoun, specifying baseform, object form (e.g.
	 * <I>us</I> for <I>we</I>), person, number and gender.
	 * 
	 * @param base
	 *            the base form
	 * @param objform
	 *            the object form
	 * @param p
	 *            the person feature
	 * @param n
	 *            the number feature
	 * @param g
	 *            the gender feature *
	 * @deprecated As of Version 3.7, subject/object forms of a pronoun are
	 *             treated as separate lexical entries distinguished by a
	 *             {@link simplenlg.features.Case} feature.
	 */
	@Deprecated
	public Pronoun(String base, String objform, Person p, NumberAgr n, Gender g) {
		this(base, p, n, g);
		this.objectForm = objform;
	}

	/**
	 * Instantiates a new pronoun with a baseform, person, number and gender
	 * features, and a boolean flag indicating whether the pronoun is expletive.
	 * 
	 * @param base
	 *            the baseform
	 * @param p
	 *            the person
	 * @param n
	 *            the number
	 * @param g
	 *            the gender
	 * @param expl
	 *            whether this is an expletive pronoun
	 */
	public Pronoun(String base, Person p, NumberAgr n, Gender g, boolean expl) {
		this(base, p, n, g);
		this.expletive = expl;
	}

	/**
	 * Instantiates a new pronoun with the given baseform, object form, person,
	 * number, gender, and expletive
	 * 
	 * @param base
	 *            the baseform
	 * @param objform
	 *            the object form
	 * @param p
	 *            the person feature
	 * @param n
	 *            the number feature
	 * @param g
	 *            the gender feature
	 * @param expl
	 *            whether this is an expletive pronoun
	 * @deprecated As of Version 3.7, subject/object forms of a pronoun are
	 *             treated as separate lexical entries distinguished by a
	 *             {@link simplenlg.features.Case} feature.
	 */
	@Deprecated
	public Pronoun(String base, String objform, Person p, NumberAgr n,
			Gender g, boolean expl) {
		this(base, objform, p, n, g);
		this.expletive = expl;
	}

	// ****************************************
	// GETTERS & SETTERS
	// ****************************************

	/**
	 * Get the person feature of this pronoun.
	 * 
	 * @return The Person feature, if set, <code>null</code> otherwise.
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * Get the number feature of this pronoun.
	 * 
	 * @return The number feature
	 */
	public NumberAgr getNumber() {
		return this.number;
	}

	/**
	 * Gets the form of the pronoun for the given DiscourseFunction; e.g. if the
	 * pronoun is <I>we</I>, this method returns <I>us</I> if the function is
	 * <code>DiscourseFunction.OBJECT</code>.
	 * 
	 * @param function
	 *            the function
	 * 
	 * @return the form
	 * @deprecated As of Version 3.7, the various forms of a pronoun are treated
	 *             as separate lexical entries, distinguished by their
	 *             {@link simplenlg.features.Case} feature.
	 */
	@Deprecated
	public String getForm(DiscourseFunction function) {

		switch (function) {

		case OBJECT:
		case INDIRECT_OBJECT:
		case PREP_OBJECT:
			if (this.objectForm != null) {
				return this.objectForm;
			}

		default:
			return this.baseForm;
		}
	}

	/**
	 * Gets the predicative form.
	 * 
	 * @return the predicative form
	 */
	public String getPredicativeForm() {
		return this.objectForm;
	}

	// ****************************************
	// UTILITY METHODS
	// ****************************************

	/**
	 * Utility method: Get the personal pronoun corresponding to a specific
	 * {@link simplenlg.features.Person}, {@link simplenlg.features.NumberAgr}
	 * and {@link simplenlg.features.Gender} combination.
	 * 
	 * @param p
	 *            A value of {@link simplenlg.features.Person}
	 * @param n
	 *            A value of {@link simplenlg.features.NumberAgr} (singular or
	 *            plural)
	 * @param g
	 *            A value of {@link simplenlg.features.Gender} (masc. or fem.)
	 * 
	 * @return The personal pronoun with these microplanner.features
	 * @deprecated Use
	 *             {@link Constants#getPersonalPronoun(Person,NumberAgr,Gender)}
	 *             instead
	 */
	@Deprecated
	public static Pronoun getPersonalPronoun(Person p, NumberAgr n, Gender g) {
		return Constants.getPersonalPronoun(p, n, g);
	}

	/**
	 * Gets the possessive pronoun with the given features.
	 * 
	 * @param p
	 *            the person feature
	 * @param n
	 *            the number feature
	 * @param g
	 *            the gender feature
	 * 
	 * @return the possessive pronoun, if one is found, <code>null</code>
	 *         otherwise.
	 * @deprecated Use
	 *             {@link Constants#getPossessivePronoun(Person,NumberAgr,Gender)}
	 *             instead
	 */
	@Deprecated
	public static Pronoun getPossessivePronoun(Person p, NumberAgr n, Gender g) {
		return Constants.getPossessivePronoun(p, n, g);
	}

	/**
	 * Gets the pronoun with the given baseform
	 * 
	 * @param baseform
	 *            the baseform
	 * 
	 * @return the pronoun, if one is found, <code>null</code> otherwise.
	 * @deprecated Use {@link Constants#getPronoun(String)} instead
	 */
	@Deprecated
	public static Pronoun getPronoun(String baseform) {
		return Constants.getPronoun(baseform);
	}

	/**
	 * Gets the polarity of this pronoun.
	 * 
	 * @return the polarity, if set, <code>null</code> otherwise.
	 */
	public Polarity getPolarity() {
		return this.polarity;
	}

	/**
	 * Sets the polarity of this pronoun.
	 * 
	 * @param polarity
	 *            the new polarity value
	 */
	public void setPolarity(Polarity polarity) {
		this.polarity = polarity;
	}

	/**
	 * Gets the quantification feature of this pronoun.
	 * 
	 * @return the quantification feature, if set, <code>null</code> otherwise.
	 */
	public Quantification getQuantification() {
		return this.quantification;
	}

	/**
	 * Sets the quantification feature of this pronoun.
	 * 
	 * @param quantification
	 *            the new quantification feature
	 */
	public void setQuantification(Quantification quantification) {
		this.quantification = quantification;
	}

	/**
	 * Gets the possession feature of this pronoun.
	 * 
	 * @return the possession feature if set, <code>null</code> otherwise.
	 */
	public Possession getPossession() {
		return this.possession;
	}

	/**
	 * Sets the possession feature of this pronoun.
	 * 
	 * @param possession
	 *            the new possession feature
	 */
	public void setPossession(Possession possession) {
		this.possession = possession;
	}

	/**
	 * Gets the reflexivity feature of this pronoun/
	 * 
	 * @return the reflexivity feature if set, <code>null</code> otherwise.
	 */
	public Reflexivity getReflexivity() {
		return this.reflexivity;
	}

	/**
	 * Sets the reflexivity feature for the pronoun.
	 * 
	 * @param reflexivity
	 *            the new reflexivity feature
	 */
	public void setReflexivity(Reflexivity reflexivity) {
		this.reflexivity = reflexivity;
	}

	/**
	 * Gets the deixis feature of this pronoun.
	 * 
	 * @return the deixis feature if set, <code>null</code> otherwise.
	 */
	public Deixis getDeixis() {
		return this.deixis;
	}

	/**
	 * Sets the deixis feature of this pronoun
	 * 
	 * @param deixis
	 *            the new deixis feature
	 */
	public void setDeixis(Deixis deixis) {
		this.deixis = deixis;
	}

	/**
	 * Gets the case feature of this pronoun
	 * 
	 * @return the case feature if set, <code>null</code> otherwise
	 */
	public Case getCaseValue() {
		return this.caseValue;
	}

	/**
	 * Sets the case feature of this pronoun
	 * 
	 * @param caseValue
	 *            the new case value
	 */
	public void setCaseValue(Case caseValue) {
		this.caseValue = caseValue;
	}

	/**
	 * Sets the gender feature of this pronoun
	 * 
	 * @param gender
	 *            the new gender feature
	 */
	public void setGender(Gender gender) {
		this.gender = gender;
	}

	/**
	 * Gets the gender feature of this pronoun
	 * 
	 * @return The gender feature if set, <code>null</code> otherwise.
	 */
	public Gender getGender() {
		return this.gender;
	}

	/**
	 * Sets whether this pronoun is expletive. Expletive pronouns are those,
	 * like <I>it</I>, which have a non-referential use (e.g. <I><U>it</U> is
	 * raining).
	 * 
	 * @param expletive
	 *            whether this pronoun is expletive
	 */
	public void setExpletive(boolean expletive) {
		this.expletive = expletive;
	}

	/**
	 * Checks whether this pronoun is expletive. This will only return
	 * <code>true</code> if the boolean value has been set using
	 * {@link #setExpletive(boolean)}
	 * 
	 * @return <code>true</code> if this pronoun is expletive.
	 */
	public boolean isExpletive() {
		return this.expletive;
	}

}
