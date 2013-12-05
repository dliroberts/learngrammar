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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import simplenlg.exception.SimplenlgException;
import simplenlg.features.Category;
import simplenlg.features.DiscourseFunction;
import simplenlg.features.Form;
import simplenlg.features.NumberAgr;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.lexicon.LexiconInterface;
import simplenlg.lexicon.lexicalitems.Verb;

/**
 * This class represents a verb phrase. In addition to the simplenlg.features
 * (premodifiers, postmodifiers and complements) inherited from
 * {@link simplenlg.realiser.HeadedPhraseSpec}, verb phrases have the following
 * simplenlg.features, for which getter and setter methods are supplied.
 * 
 * <OL>
 * <LI>modal auxiliary: E.g. <i><strong>must</strong> be the man behind the
 * curtain</i></LI>
 * <LI>tense: This is a value in {@link simplenlg.features.Tense}</LI>
 * <LI>Inflectional agreement simplenlg.features, namely:
 * <UL>
 * <LI>{@link simplenlg.features.Person}; cf. the difference between <i>We
 * <strong>are</strong> all craxy</i> and <i>he <strong>is</strong> crazy</i></LI>
 * <LI>{@link simplenlg.features.NumberAgr}; cf. the examples above, where
 * <i>are</i> is plural and <i>is</i> singular</LI>
 * </UL>
 * These simplenlg.features can be set manually, but in case a full sentence is
 * constructed, they are also automatically handled within the
 * {@link simplenlg.realiser.SPhraseSpec}.</LI>
 * <LI>perfect: A <code>boolean</code> value specifying whether the VP is
 * perfective. This feature interacts with tense, as exemplified below:
 * <UL>
 * <LI><i><strong>was eaten</strong></i> (+past, -perfect)</LI>
 * <LI><i><strong>had been eaten</strong></i> (+past, +perfect)</LI>
 * <LI><i><strong>has been eaten</strong></i> (+present, +perfect)
 * </UL>
 * </LI>
 * <LI>progressive: A <code>boolean</code> value, exemplified below:
 * <UL>
 * <LI><i><strong>was eating</strong></i> (+progressive)</LI>
 * <LI><i><strong>ate</strong></i> (-progressive)</LI>
 * </UL>
 * </LI>
 * <LI>passive: <code>boolean</code>, determines whether the VP is passive.</LI>
 * <LI>negated: <code>boolean</code>, determines whether the VP is negated.</LI>
 * <LI>Form: a value of {@link simplenlg.features.Form}</LI>
 * </OL>
 * 
 * @author agatt
 */
public class VPPhraseSpec extends HeadedPhraseSpec<Verb> {

	/** The realise auxiliary. */
	boolean perfect, progressive, passive, negated, realiseAuxiliary;

	/** The modal. */
	String modal; // modal, eg "must"

	/** The tense. */
	Tense tense;

	/** The person. */
	Person person;

	/** The number. */
	NumberAgr number;

	// Mood mood;

	/** The form. */
	Form form;

	// private vars to hold components of a realisation
	/** The auxiliary realisation. */
	String auxiliaryRealisation; // realisation of auxiliaries

	/** The main verb realisation. */
	String mainVerbRealisation; // realisation of main verb (and trailing "not")

	/** The vg components. */
	Stack<String> vgComponents; // holds the components of the verb group

	// the argument to suppress in case of a WH question
	/** The suppress indirect object. */
	boolean suppressObject, suppressIndirectObject;

	/**
	 * Constructs an empty VPPhraseSpec.
	 */
	public VPPhraseSpec() {
		super();
		this.category = Category.VERB;
		this.perfect = false;
		this.progressive = false;
		this.passive = false;
		this.negated = false;
		this.modal = null;
		this.tense = Tense.PRESENT;
		this.person = Person.THIRD;
		this.number = NumberAgr.SINGULAR;
		// mood = Mood.NORMAL;
		this.form = Form.NORMAL;
		this.head = new Verb("");
		this.realiseAuxiliary = true;
		this.auxiliaryRealisation = "";
		this.mainVerbRealisation = "";
		this.vgComponents = new Stack<String>();
		this.suppressObject = false;
		this.suppressIndirectObject = false;
	}

	/**
	 * Constructs a <code>VPPhraseSpec</code> headed by a verb with the
	 * specified base form.
	 * 
	 * @param v
	 *            The baseform of the head verb, a <code>String</code>
	 */
	public VPPhraseSpec(String v) {
		this();
		setHead(v);
	}

	/**
	 * Constructs a <code>VPPhraseSpec</code> headed by the specified
	 * {@link simplenlg.lexicon.lexicalitems.Verb}.
	 * 
	 * @param v
	 *            The verb
	 */
	public VPPhraseSpec(Verb v) {
		this();
		setHead(v);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.HeadedPhraseSpec#setHead(java.lang.String)
	 */
	@Override
	public void setHead(String v) {
		this.head = castToVerb(v);
	}

	/**
	 * {@inheritDoc simplenlg.realiser.HeadedPhraseSpec}. In the verb phrase,
	 * complements can be direct objects or indirect objects. By default, any
	 * object passed as complement is assigned the function
	 * {@link simplenlg.features.DiscourseFunction#OBJECT}.
	 * 
	 * <P>
	 * Complements can be phrases or strings (which are converted to
	 * {@link simplenlg.realiser.StringPhraseSpec}s. If a complement is a
	 * sentence, i.e. an {@link simplenlg.realiser.SPhraseSpec}, the following
	 * occurs:
	 * <OL>
	 * <LI>the sentence is set to subordinate; see
	 * {@link simplenlg.realiser.SPhraseSpec#setClauseStatus(simplenlg.features.ClauseStatus)}
	 * </LI>
	 * <LI>if the sentence is in the imperative, its form is set to
	 * {@link simplenlg.features.Form#INFINITIVE}. E.g. a sentence like <i>Go
	 * out</i>, embedded as a complement of <i>tell</i> is realised as <i>tell
	 * (X) to go out</i>.</LI>
	 * </OL>
	 * 
	 * @param comp
	 *            the comp
	 * 
	 * @see #addComplement(Object, DiscourseFunction)
	 */
	@Override
	public void addComplement(Object comp) {
		addComplement(comp, DiscourseFunction.OBJECT);
	}

	/**
	 * Passes a complement, which is assigned the specified function.
	 * 
	 * @param comp
	 *            The complement.
	 * @param function
	 *            The discourse function (e.g.
	 *            {@link simplenlg.features.DiscourseFunction#INDIRECT_OBJECT})
	 * 
	 * @throws simplenlg.exception.SimplenlgException
	 *             if the specified function is not compatible with a complement
	 *             of a VP, specifically, if it is
	 *             {@link simplenlg.features.DiscourseFunction#FRONT_MODIFIER},
	 *             {@link simplenlg.features.DiscourseFunction#PREMODIFIER},
	 *             {@link simplenlg.features.DiscourseFunction#POSTMODIFIER},
	 *             {@link simplenlg.features.DiscourseFunction#SUBJECT} or
	 *             {@link simplenlg.features.DiscourseFunction#PREP_OBJECT}
	 * 
	 * @see #addComplement(Object)
	 */
	public void addComplement(Object comp, DiscourseFunction function) {

		if (function.equals(DiscourseFunction.FRONT_MODIFIER)
				|| function.equals(DiscourseFunction.PREMODIFIER)
				|| function.equals(DiscourseFunction.POSTMODIFIER)
				|| function.equals(DiscourseFunction.SUBJECT)
				|| function.equals(DiscourseFunction.PREP_OBJECT)) {
			throw new SimplenlgException(
					"Complements of a verb phrase can only be assigned the functions"
							+ "OBJECT, INDIRECT_OBJECT");
		}

		this.complements.add(makeConstituent(comp, function));
	}

	public void addIndirectObject(Object object) {
		addComplement(object, DiscourseFunction.INDIRECT_OBJECT);
	}

	/**
	 * Unlike {@link #setComplement(Object)}, which resets all complements this
	 * method will only replace the complements with the given function, if
	 * there are any.
	 * 
	 * @param comp
	 *            The new complement
	 * @param function
	 *            The function
	 */
	public void setComplement(Object comp, DiscourseFunction function) {
		this.complements.removeAll(getComplements(function));
		addComplement(comp, function);
	}

	/**
	 * Sets the form of this <code>verbGroup</code>.
	 * 
	 * @param f
	 *            The {@link simplenlg.features.Form}
	 */
	public void setForm(Form f) {
		this.form = f;

		// can't have PAST or FUTURE with gerunds or infinitives
		if (this.form == Form.GERUND || this.form == Form.INFINITIVE
				|| this.form == Form.BARE_INFINITIVE) {
			this.tense = Tense.PRESENT;
		}
	}

	/**
	 * Gets the form.
	 * 
	 * @return The {@link simplenlg.features.Form} of this verb.
	 */
	public Form getForm() {
		return this.form;
	}

	/**
	 * Sets the </code>passive</code> feature of this <code>VPPhraseSpec</code>.
	 * If the verb is set to passive, then the following occurs at realisation
	 * stage:
	 * <OL>
	 * <LI>If the VP has indirect objects, these are suppressed. Thus, for
	 * example, the VP <i>give <strong>Mary<sub>IO</sub></strong> the
	 * book<sub>DO</sub></i> is realised as <i>is given to Mary</i>. In the
	 * context of a sentence, the indirect object is raised to subject position
	 * (e.g. <i>the book is given to Mary</i>)</LI>
	 * Otherwise, the object is suppressed. Thus, the VP <i>kick John</i>
	 * becomes <i>is kicked</i></LI>
	 * </OL>
	 * 
	 * @param pass
	 *            Whether the verb phrase is passive
	 */
	public void setPassive(boolean pass) {
		this.passive = pass;
	}

	/**
	 * Checks if is passive.
	 * 
	 * @return <code>true</code> if this <code>VPPhraseSpec</code> is passive.
	 */
	public boolean isPassive() {
		return this.passive;
	}

	/**
	 * Sets the </code>progressive</code> feature of this
	 * <code>VPPhraseSpec</code>.
	 * 
	 * @param prog
	 *            the prog
	 */
	public void setProgressive(boolean prog) {
		this.progressive = prog;
	}

	/**
	 * Checks if is progressive.
	 * 
	 * @return <code>true<code> if this <code>VPPhraseSpec</code> is
	 *         progressive.
	 */
	public boolean isProgressive() {
		return this.progressive;
	}

	/**
	 * Sets the </code>perfect</code> feature of this <code>VPPhraseSpec</code>.
	 * 
	 * @param perf
	 *            the perf
	 */
	public void setPerfect(boolean perf) {
		this.perfect = perf;
	}

	/**
	 * Checks if is perfect.
	 * 
	 * @return <code>true<code> if this <code>VPPhraseSpec</code> is passive.
	 */
	public boolean isPerfect() {
		return this.perfect;
	}

	/**
	 * Sets the </code>negated</code> feature of this <code>VPPhraseSpec</code>.
	 * This will result in automatic <i>do-</i>insertion at realisation stage if
	 * required. (e.g. <i>did not do</i>).
	 * 
	 * @param neg
	 *            the neg
	 */
	public void setNegated(boolean neg) {
		this.negated = neg;
	}

	/**
	 * Checks if is negated.
	 * 
	 * @return <code>true<code> if this <code>VPPhraseSpec</code> is negated.
	 */
	public boolean isNegated() {
		return this.negated;
	}

	/**
	 * Sets the tense of the verb.
	 * 
	 * @param t
	 *            A value of {@link simplenlg.features.Tense}
	 */
	public void setTense(Tense t) {
		// if (t.equals(Tense.FUTURE) && (modal == null))
		// modal = "will";
		this.tense = t;
	}

	/**
	 * Gets the tense.
	 * 
	 * @return the {@link simplenlg.features.Tense} of this phrase
	 */
	public Tense getTense() {
		return this.tense;
	}

	/**
	 * Gets the modal.
	 * 
	 * @return the modal auxiliary of this phrase if set, <code>null</code>
	 *         otherwise.
	 * 
	 * @see #setModal(String)
	 */
	public String getModal() {
		return this.modal;
	}

	/**
	 * Sets the modal auxiliary of the verb (eg, "can", "must").
	 * 
	 * @param modal
	 *            The modal auxiliary.
	 */
	public void setModal(String modal) {
		this.modal = modal;
	}

	/**
	 * Set the person feature of this verb.
	 * 
	 * @param p
	 *            The {@link simplenlg.features.Person} value.
	 */
	public void setPerson(Person p) {
		this.person = p;
	}

	/**
	 * Gets the person.
	 * 
	 * @return the person
	 */
	public Person getPerson() {
		return this.person;
	}

	/**
	 * Set the number feature of this phrase.
	 * 
	 * @param n
	 *            The {@link simplenlg.features.NumberAgr} value.
	 */
	public void setNumber(NumberAgr n) {
		this.number = n;
	}

	/**
	 * Gets the number.
	 * 
	 * @return The {@link simplenlg.features.NumberAgr} feature of this phrase.
	 */
	public NumberAgr getNumber() {
		return this.number;
	}

	/**
	 * For use in case the head verb is phrasal. (e.g. "get up"). The method
	 * adds the particle to the main verb of this phrase.
	 * 
	 * @param particle
	 *            The particle
	 * 
	 * @throws <code>NullPointerException</code> if the head verb has not been
	 *         specified.
	 */
	public void setParticle(String particle) {
		this.head.setParticle(particle);
	}

	/**
	 * Unlike the {@link #getComplements()} method, inherited from
	 * {@link simplenlg.realiser.HeadedPhraseSpec}, this method will return only
	 * complements with a given discourse function (e.g. Direct object(s) only).
	 * 
	 * @param func
	 *            The {@link simplenlg.features.DiscourseFunction}
	 * 
	 * @return A List if complements with this function, if any. An empty list
	 *         otherwise.
	 */
	public List<Phrase> getComplements(DiscourseFunction func) {
		List<Phrase> compsToReturn = new ArrayList<Phrase>();

		for (Phrase p : this.complements) {
			if (p.getDiscourseFunction() == func) {
				compsToReturn.add(p);
			}
		}

		return compsToReturn;
	}

	/**
	 * This method returns those complements which would be raised to subject
	 * position within a sentence, if the sentence (and the verb phrase) is
	 * passive. This is worked out as follows:
	 * <OL>
	 * <LI>If there are direct objects, then they are returned.</LI>
	 * <LI>Indirect objects are returned otherwise. If the indirect object(s) is
	 * a prepositional phrase, its complement is returned.</LI>
	 * </OL>
	 * 
	 * @return the complements to raise to subject, a
	 *         <code>java.util.List&lt;Phrase&gt;</code>
	 * 
	 * @see #setPassive(boolean)
	 */
	public List<Phrase> getPassiveRaisingComplements() {
		List<Phrase> objects = getComplements(DiscourseFunction.OBJECT);

		if (objects.isEmpty()) {
			for (Phrase p : getComplements(DiscourseFunction.INDIRECT_OBJECT)) {
				if (p instanceof PPPhraseSpec && !p.isElided()) {
					objects.addAll(((PPPhraseSpec) p).getComplements());
				} else if (!p.isElided()) {
					objects.add(p);
				}
			}
		}

		return flagRaisedNPs(objects, true);
	}

	/**
	 * Compare two <code>VPPhraseSpecs/code>s on the basis of grammatical
	 * simplenlg.features.
	 * 
	 * @param vp
	 *            The <code>VPPhraseSpec</code> that will be compared to this
	 *            one.
	 * 
	 * @return <code>true</code> if the two <code>VPPhraseSpec</code>s have
	 *         identical values on all simplenlg.features (tense, progressive,
	 *         passive, perfect, negated, person and number).
	 */
	public boolean hasSameFeatures(VPPhraseSpec vp) {
		return this.passive == vp.passive && this.perfect == vp.perfect
				&& this.negated == vp.negated
				&& this.progressive == vp.progressive
				&& this.person == vp.person && this.number == vp.number;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Phrase#coordinate(T[])
	 */
	public VPPhraseSpec coordinate(Phrase... coords) {

		if (coords.length == 0) {
			return this;
		}

		CoordinateVPPhraseSpec coord = new CoordinateVPPhraseSpec(this);

		try {

			for (Phrase p : coords) {
				coord.addCoordinates((VPPhraseSpec) p);
			}

			return coord;

		} catch (ClassCastException cce) {
			throw new SimplenlgException("Cannot coordinate: "
					+ "only phrases of the same type can be coordinated");
		}
	}

	// ***************************************
	// Realisation
	// ***************************************

	/**
	 * Determines whether only the main verb of this phrase is to be realised,
	 * or whether the full verb phrase, including auxiliary should be realised.
	 * This feature is useful for aggregation purposes, in case the
	 * <code>VPPhraseSpec</code> is a
	 * {@link simplenlg.realiser.CoordinateVPPhraseSpec}.
	 * 
	 * @param aux
	 *            If <code>false</code>, causes only the main verb to be
	 *            returned at realisation stage.
	 * 
	 * @see simplenlg.realiser.CoordinateVPPhraseSpec#aggregateAuxiliary(boolean)
	 */
	public void realiseAuxiliary(boolean aux) {
		this.realiseAuxiliary = aux;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.HeadedPhraseSpec#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		boolean eq = false;

		if (o instanceof VPPhraseSpec) {
			VPPhraseSpec vp = (VPPhraseSpec) o;

			if (super.equals(vp)) {
				eq = this.tense == vp.tense && this.perfect == vp.perfect
						&& this.passive == vp.passive
						&& this.progressive == vp.progressive;
			}
		}

		return eq;
	}

	/*
	 * Overload of the realise(Realiser) method, permitting realisation for
	 * interrogative sentences. We don't want to set a field inside the VP, as
	 * it's the containing sentence, not the VP, which is interrogative.
	 */
	String realise(Realiser r, boolean interrogative) {
		realiseVerbGroup(r, interrogative);
		computeRealisation(r, interrogative);
		String preMod = realisePremodifier(r);
		String comp = realiseComplement(r);
		String postMod = realisePostmodifier(r);

		if (this.realiseAuxiliary) {
			return r.appendSpace(this.auxiliaryRealisation, preMod, 
					this.mainVerbRealisation, comp, postMod);
		} else {
			return this.head.isCopular() ? r.appendSpace(
					this.mainVerbRealisation, preMod, comp, postMod) : r
					.appendSpace(preMod, this.mainVerbRealisation, comp,
							postMod);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.HeadedPhraseSpec#realise(simplenlg.realiser.Realiser)
	 */
	@Override
	String realise(Realiser r) {
		return realise(r, false);
	}

	/*
	 * Returns the element of the verb group that would be fronted in case this
	 * was being realised in the context of a question.
	 */
	String getInterrogativeFrontedElement(LexiconInterface lex) {

		// case 1: VP has no AUX && it;'s not copular
		if (this.vgComponents.size() == 1 && !this.head.isCopular()) {
			return lex.getVerbForm("do", this.tense, this.person, this.number);
		} else if (!this.vgComponents.empty()) {
			return this.vgComponents.peek();
		} else {
			return null;
		}
	}

	/*
	 * compute two components of realisation, auxiliary and main verb note that
	 * main verb realisation includes trailing "not". NB: Should be called after
	 * realiseVerbGroup(Realiser)
	 * 
	 * Parameters include a boolean, which if true causes the fronted aux to be
	 * omitted in questions).
	 */
	void computeRealisation(Realiser r, boolean interrogative) {
		this.mainVerbRealisation = ""; // main verb, plus ending "not" if
		// necessary
		this.auxiliaryRealisation = ""; // auxiliaries (including endModifiers)
		boolean mainVerbSeen = false; // have we seen the main verb
		String omit = null;

		// if it's interrogative, we don't realise the fronted part
		if (interrogative) {
			omit = getInterrogativeFrontedElement(r.getLexicon());
		}

		for (String word : this.vgComponents) {
			// flag this word if we need to skip it
			boolean skip = omit != null && word.equals(omit);

			if (!mainVerbSeen) {

				if (!skip) {
					this.mainVerbRealisation = r.appendSpace(word,
							this.mainVerbRealisation);
				}

				if (!word.equalsIgnoreCase("not")) {
					mainVerbSeen = true;
				}

			} else if (!skip) {
				this.auxiliaryRealisation = r.appendSpace(word,
						this.auxiliaryRealisation);
			}
		}
	}

	/*
	 * build verb group from head verb out, populating the stack vgComponents
	 * element 0 being last word frontVG is Verb currently at front of VG,
	 * restVG is rest of VG
	 */
	void realiseVerbGroup(Realiser r, boolean interrogative) {
		this.vgComponents.clear(); // clear the components stack
		LexiconInterface lex = r.getLexicon();

		// VP will be realised as perfective if the verb is modal and past
		boolean modalPast = false;

		// compute modal -- this affects tense
		String actualModal = null;

		if (this.form == Form.INFINITIVE) {
			actualModal = "to";
		} else if (this.form.allowsTense()) {
			if (this.tense == Tense.FUTURE && this.modal == null) {
				actualModal = "will";
			} else if (this.modal != null) {
				actualModal = this.modal;

				if (this.tense.equals(Tense.PAST)) {
					modalPast = true;
				}
			}
		}

		// start off with main verb
		Verb frontVG = this.head;

		// passive
		if (this.passive) {
			this.vgComponents.push(frontVG.getPastParticiple());
			frontVG = (Verb) lex.getItem(Category.VERB, "be");
		}

		// progressive
		if (this.progressive) {
			this.vgComponents.push(frontVG.getPresentParticiple());
			frontVG = (Verb) lex.getItem(Category.VERB, "be");
		}

		// perfect
		if (this.perfect || modalPast) {
			this.vgComponents.push(frontVG.getPastParticiple());
			frontVG = (Verb) lex.getItem(Category.VERB, "have");
		}

		if (actualModal != null) {
			this.vgComponents.push(frontVG.getBaseForm());
			frontVG = null;
		}

		// negated
		if (this.negated) {
			if (!this.vgComponents.empty() || frontVG != null
					&& frontVG.isCopular()) {
				this.vgComponents.push("not");
			} else {
				if (frontVG != null) {
					this.vgComponents.push(frontVG.getBaseForm());
				}

				this.vgComponents.push("not");
				frontVG = (Verb) lex.getItem(Category.VERB, "do");
			}
		}

		// now inflect frontVG (if it exists) and push it on restVG
		if (frontVG != null) {
			if (this.form == Form.GERUND) {
				this.vgComponents.push(frontVG.getPresentParticiple());
			} else if (this.form == Form.PAST_PARTICIPLE) {
				this.vgComponents.push(frontVG.getPastParticiple());
			} else if (this.form == Form.PRESENT_PARTICIPLE) {
				this.vgComponents.push(frontVG.getPresentParticiple());
			} else if (!this.form.allowsTense() || interrogative
					&& !this.head.isCopular() && this.vgComponents.isEmpty()) {
				this.vgComponents.push(frontVG.getBaseForm());
			} else {
				NumberAgr numToUse = determineNumber();
				this.vgComponents.push(lex.getVerbForm(frontVG.getBaseForm(),
						this.tense, this.person, numToUse));
			}
		}

		// add modal, and we're done
		if (actualModal != null) {
			this.vgComponents.push(actualModal);
		}
	}

	/*
	 * Check for the right grammatical number for the verb. If the verb is
	 * copular and there are multiple complements, numnber is plural only if at
	 * least one complement is plural.
	 */
	private NumberAgr determineNumber() {
		NumberAgr num = this.number;
		Spec parent = getParentSpec();

		if (parent instanceof SPhraseSpec
				&& ((SPhraseSpec) parent).isExpletiveSubject()) {
			if (this.head.isCopular()) {
				if (hasPluralComplement()) {
					num = NumberAgr.PLURAL;
				} else {
					num = NumberAgr.SINGULAR;
				}
			}
		}

		return num;
	}

	private boolean hasPluralComplement() {
		boolean plur = false;
		for (Phrase comp : this.complements) {
			if (comp instanceof NPPhraseSpec
					&& ((NPPhraseSpec) comp).getNumber() == NumberAgr.PLURAL) {
				plur = true;
				break;
			}
		}

		return plur;
	}

	/*
	 * need to override the complement realisation, as direct objects need to be
	 * suppressed in the passive
	 */
	@Override
	String realiseComplement(Realiser r) {
		List<Phrase> complementsToRealise = new ArrayList<Phrase>(
				this.complements);

		if (this.passive) {
			complementsToRealise.removeAll(getPassiveRaisingComplements());
		}

		// ER - added. Split into types of complements
		List<Phrase> objectsToRealise = new ArrayList<Phrase>();
		List<Phrase> indirectObjectsToRealise = new ArrayList<Phrase>();
		List<Phrase> predicativeComplements = new ArrayList<Phrase>();
		List<Phrase> otherComplementsToRealise = new ArrayList<Phrase>();
		// not sure if OtherComplementsToRealise needed, may always be
		// null at moment, but provides some robustness for future

		for (Phrase p : complementsToRealise) {
			DiscourseFunction function = p.getDiscourseFunction();
			switch (function) {
			case OBJECT:
				objectsToRealise.add(p);
				break;
			case PREDICATIVE_COMPLEMENT:
				predicativeComplements.add(p);
				break;
			case INDIRECT_OBJECT:
				indirectObjectsToRealise.add(p);
				break;
			default:
				otherComplementsToRealise.add(p);
				break;
			}
		}

		Collections.sort(objectsToRealise, this.complementComparator);
		Collections.sort(predicativeComplements, this.complementComparator);
		Collections.sort(indirectObjectsToRealise, this.complementComparator);
		Collections.sort(otherComplementsToRealise, this.complementComparator);

		String text = "";

		if (!this.suppressIndirectObject) {
			text = r
					.appendSpace(
							text,
							r.realiseList(collectIndirectObjectsByCategory(indirectObjectsToRealise)));

		}

		if (!this.suppressObject) {
			text = r.appendSpace(text, r.realiseAndList(objectsToRealise));
			text = r
					.appendSpace(text, r.realiseAndList(predicativeComplements));
		}

		text = r.appendSpace(text, r.realiseAndList(otherComplementsToRealise));
		return text;
	}

	/*
	 * Collect indirect objects so that: - NPs are coordinated - PPs are only
	 * coordinated if they have the same head
	 */
	private List<Phrase> collectIndirectObjectsByCategory(
			List<Phrase> indirectObjects) {
		ListIterator<Phrase> iterator = indirectObjects.listIterator();
		List<Phrase> result = new ArrayList<Phrase>();
		NPPhraseSpec lastNP = null;

		while (iterator.hasNext()) {
			Phrase next = iterator.next();

			if (next instanceof NPPhraseSpec) {
				NPPhraseSpec np = (NPPhraseSpec) next;

				if (lastNP != null) {
					result.remove(lastNP);

					if (lastNP instanceof CoordinateNPPhraseSpec) {
						((CoordinateNPPhraseSpec) lastNP).addCoordinates(np);
						np = lastNP;

					} else {
						np = lastNP.coordinate(np);
					}
				}

				lastNP = np;
				result.add(np);

			} else {
				result.add(next);
			}

		}

		return result;
	}

	/*
	 * String realisePostmodifier(Realiser r) { List<Phrase> postMods = new
	 * ArrayList<Phrase>();
	 * 
	 * for( Phrase p: postmodifiers ) if( !(p instanceof AdvPhraseSpec))
	 * postMods.add(p);
	 * 
	 * if (postmodifierComparator != null) Collections.sort(postMods,
	 * postmodifierComparator);
	 * 
	 * return r.realiseList(postmodifiers); }
	 */

	/*
	 * Realise adverb phrases separately: by default, these are placed before
	 * the main verb, not in the default postmodifier position.
	 */
	/*
	 * String realiseAdverbials(Realiser r) { List<Phrase> adverbials = new
	 * ArrayList<Phrase>();
	 * 
	 * for( Phrase p: postmodifiers) if( p instanceof AdvPhraseSpec )
	 * adverbials.add(p);
	 * 
	 * return r.realiseAndList(adverbials); }
	 */

	// *****************************************************************************
	// PRIVATE/PROTECTED UTILITY METHODS
	// *****************************************************************************
	/*
	 * suppress some argument during realisation
	 */
	void suppressArg(DiscourseFunction function) {

		if (function.equals(DiscourseFunction.OBJECT)) {
			this.suppressObject = true;
		} else if (function.equals(DiscourseFunction.INDIRECT_OBJECT)) {
			this.suppressIndirectObject = true;
		}
	}

	/*
	 * reset argument suppression && complement raising
	 */
	void initialiseArgs() {
		this.suppressIndirectObject = false;
		this.suppressObject = false;
		flagRaisedNPs(this.complements, false);
	}

	/*
	 * check if a verb is a form of "be"
	 */
	boolean isBeVerb(String verb) {
		boolean beVerb = false;

		if (verb != null) {
			// returns T if this verb is a form of "be"
			beVerb = verb.equalsIgnoreCase("be") || verb.equalsIgnoreCase("am")
					|| verb.equalsIgnoreCase("are")
					|| verb.equalsIgnoreCase("is")
					|| verb.equalsIgnoreCase("was")
					|| verb.equalsIgnoreCase("were");
		}

		return beVerb;
	}

	/*
	 * create a verb from a string
	 */
	Verb castToVerb(String verb) {
		Verb v = isBeVerb(verb) ? new Verb("be") : new Verb(verb);
		return v;
	}

	/*
	 * flag NPs which are passive-raised
	 */
	List<Phrase> flagRaisedNPs(List<Phrase> flagged, boolean raise) {

		for (Phrase p : flagged) {
			if (p instanceof NPPhraseSpec) {
				((NPPhraseSpec) p).setRaised(raise);
			}
		}

		return flagged;
	}

}
