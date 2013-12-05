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

/* change log 
 * 19-Dec-06:  do nothing if add null subjects/complement/etc
 * */
package simplenlg.realiser;

import java.util.ArrayList;
import java.util.List;

import simplenlg.exception.SimplenlgException;
import simplenlg.features.Category;
import simplenlg.features.ClauseStatus;
import simplenlg.features.DiscourseFunction;
import simplenlg.features.Form;
import simplenlg.features.InterrogativeType;
import simplenlg.features.NumberAgr;
import simplenlg.features.Person;
import simplenlg.features.SModifierPosition;
import simplenlg.features.Tense;
import simplenlg.lexicon.lexicalitems.Noun;
import simplenlg.lexicon.lexicalitems.Verb;

/**
 * Simplenlg.SPhraseSpec is a simple syntactic representation of a sentential
 * phrase or clause.
 * 
 * <P>
 * It consists of
 * <UL>
 * <LI>An optional cue phrase: this can be a {@link simplenlg.realiser.Phrase}
 * of any type
 * <LI>Zero or more fronted adverbials: : these can be a
 * {@link simplenlg.realiser.Phrase} of any type
 * <LI>Zero or more subjects phrases: : these can be a
 * {@link simplenlg.realiser.Phrase} of any type. By default they are realised
 * as a coordinate phrase.
 * <LI>A verb phrase (including particle): this is a
 * {@link simplenlg.realiser.VPPhraseSpec}
 * </UL>
 * These are linearised in the above order. This class provides methods to set
 * all of the above, as well as to manipulate simplenlg.features of the verb
 * phrase and other constituents (inluding tense, form aspect, negation etc).
 * Thus, it is possible to either construct a sentence by first specifying all
 * of its constituents, or to construct it piecemeal by using the methods
 * provided in the class itself. For example, complements and pre- or
 * post-modifiers of the verb phrase can be determined from this class which
 * then calls the corresponding methods in
 * {@link simplenlg.realiser.VPPhraseSpec}.
 * 
 * <P>
 * <U>Note</U> Various setter methods are provided for setting the constituents
 * listed above (and their components). With the simplenlg.exception of the verb
 * phrase, which must be a {@link simplenlg.realiser.VPPhraseSpec}, no
 * restriction is placed on the type of these constituents. Methods to set
 * constituents can take either a <code>java.lang.String</code> or any instance
 * of a type implementing the {@link simplenlg.realiser.Phrase} interface.
 * 
 * <P>
 * <U>Examples of possible sentences:</U>
 * <table>
 * <tr>
 * <td>cue phrase</td>
 * <td>fronted adverb</td>
 * <td>subjects</td>
 * <td>verb phrase</td>
 * </tr>
 * <tr>
 * <td>As a result</td>
 * <td></td>
 * <td>John and Mary</td>
 * <td>quickly ate the cake</td>
 * </tr>
 * <tr>
 * <td></td>
 * <td>Quickly,</td>
 * <td>John and Mary</td>
 * <td>ate the cake</td>
 * </tr>
 * <td></td>
 * <td></td>
 * <td>John's kicking Bill</td>
 * <td>really upset Peter</td>
 * </tr>
 * </table>
 * 
 * @author ereiter, agatt
 */
public class SPhraseSpec extends HeadedPhraseSpec<Verb> {

	/** The cue phrase. */
	Phrase cuePhrase;

	/** The front modifiers. */
	List<Phrase> subjects, frontModifiers;

	/** The verb phrase. */
	VPPhraseSpec verbPhrase;

	// List<Phrase> surfaceSubjects; // used only during realisation
	PPPhraseSpec passiveComplement;

	/** The surface subject. */
	CoordinatePhraseSet<Phrase> surfaceSubject;

	/** The complementiser. */
	String complementiser;

	/** The status. */
	ClauseStatus status;

	/** The suppress comp. */
	boolean suppressComp; // suppress the complementiser

	/** The expletive subject. */
	boolean expletiveSubject; // true if the subject is expletive "there"

	/** The suppress genitive in gerund. */
	boolean suppressGenitiveInGerund;

	// interrogative features
	/** The interrogative. */
	boolean interrogative; // true if sentence is a question

	/** The interrog type. */
	InterrogativeType interrogType;

	/** The wh argument. */
	DiscourseFunction whArgument;

	// the punctuation mark
	/** The terminator. */
	char terminator;

	// set to true if user specifies their own sentence terminator
	/** The suppress punctuation default. */
	boolean suppressPunctuationDefault;

	// **************************************
	// CONSTRUCTORS
	// **************************************

	/**
	 * Constructs an empty SPhraseSpec.
	 */
	public SPhraseSpec() {
		super();
		this.cuePhrase = null; // no cue phrase
		this.verbPhrase = new VPPhraseSpec();
		this.verbPhrase.setParentSpec(this);

		this.subjects = new ArrayList<Phrase>();
		this.frontModifiers = new ArrayList<Phrase>();

		// Surface arguments
		this.surfaceSubject = new CoordinatePhraseSet<Phrase>(this);
		this.passiveComplement = null;

		this.status = ClauseStatus.MATRIX;
		this.category = Category.CLAUSE;
		this.complementiser = "that";

		this.suppressComp = false;
		this.expletiveSubject = false;
		this.interrogative = false;
		this.interrogType = null;
		this.whArgument = null;
		this.suppressGenitiveInGerund = false;
		this.terminator = '.';
		this.suppressPunctuationDefault = false;
	}

	/**
	 * Constructs an SPhraseSpec with specified subjects, verb, object.
	 * 
	 * @param subject
	 *            The subject phrase, a <code>String</code> or
	 *            {@link simplenlg.realiser.Phrase}
	 * @param verb
	 *            The main verb, a {@link simplenlg.lexicon.lexicalitems.Verb}
	 *            or a <code>String</code>
	 * @param complement
	 *            The complement phrase, a <code>String</code> or
	 *            {@link simplenlg.realiser.Phrase}
	 */

	public SPhraseSpec(Object subject, Object verb, Object complement) {
		this(subject, verb);
		addComplement(complement);
	}

	/**
	 * Check whether this is an existential sentence
	 * 
	 * @return <code>true</code> if the sentence has <I>there</I> as subject NP,
	 *         and a copular verb as head.
	 */
	public boolean isExistential() {
		boolean ex = false;

		if (this.subjects.size() == 1) {
			Phrase subj = this.subjects.get(0);

			if (subj instanceof NPPhraseSpec) {
				Noun head = ((NPPhraseSpec) subj).getHead();

				if (head != null) {
					ex = head.getBaseForm().equals("there");
				}
			} else if (subj instanceof StringPhraseSpec) {
				String string = ((StringPhraseSpec) subj).getString();

				if (string != null) {
					ex = string.equals("there");
				}
			}
		}

		return ex;
	}

	/**
	 * Constructs an SPhraseSpec with specified subjects, verb (no object).
	 * 
	 * @param subject
	 *            The subject phrase, a <code>String</code> or
	 *            {@link simplenlg.realiser.Phrase}
	 * @param verb
	 *            The main verb, a {@link simplenlg.lexicon.lexicalitems.Verb}
	 *            or <code>String</code>
	 */
	public SPhraseSpec(Object subject, Object verb) {
		this();

		if (verb instanceof String) {
			setHead((String) verb);
		} else if (verb instanceof Verb) {
			setHead((Verb) verb);
		} else {
			throw new SimplenlgException(
					"Verb argument to SPhraseSpec constructor must be Verb or String");
		}

		addSubject(subject);

	}

	// **************************************
	// Getters, setters for VERB
	// *************************************

	/**
	 * Sets verb (replaces existing verb). A particle can be specified, for
	 * example "split up". Using this method, the verb group of the sentence can
	 * be specified using simply a <code>String</code> (this is then internally
	 * rendered as head of the {@link simplenlg.realiser.VPPhraseSpec} of the
	 * sentence.
	 * 
	 * @param verb
	 *            The new verb, an instance of
	 *            {@link simplenlg.lexicon.lexicalitems.Verb} or a
	 *            <code>String</code>
	 */
	public void setVerb(Object verb) {

		if (verb instanceof Verb) {
			setHead((Verb) verb);
		} else if (verb instanceof String) {
			setHead((String) verb);
		} else {
			throw new SimplenlgException(
					"Argument to SPhraseSpec.setVerb(Object) must be a Verb or String");
		}
	}

	/**
	 * Sets the entire verb phrase. If the sentence had a previous verb phrase,
	 * and the tense, progressive, passive and perfective features have been
	 * set, the new VP will also have these features.
	 * 
	 * @param vp
	 *            The new {@link simplenlg.realiser.VPPhraseSpec} for this
	 *            clause.
	 */
	public void setVerbPhrase(VPPhraseSpec vp) {
		boolean perf = isPerfect();
		boolean prog = isProgressive();
		boolean pass = isPassive();
		Tense t = getTense();

		vp.setParentSpec(this);
		this.verbPhrase = vp;
		this.verbPhrase.setTense(t);
		this.verbPhrase.setPerfect(perf);
		this.verbPhrase.setProgressive(prog);
		this.verbPhrase.setPassive(pass);
	}

	/**
	 * Adds a verb phrase to this sentence. If one already exists, the new one
	 * is coordinated with the existing verb phrase. Note that adding the new
	 * verb phrase does not give it the same temporal, aspectual and other
	 * simplenlg.features as other verb phrases that may have been specified
	 * earlier. Thus, it is possible to have sentences like <i>John
	 * <strong>walks</strong> and <strong>had not talked</strong></i>
	 * 
	 * @param vp
	 *            The new {@link simplenlg.realiser.VPPhraseSpec}
	 */
	public void addVerbPhrase(VPPhraseSpec vp) {
		this.verbPhrase = this.verbPhrase.coordinate(vp);
		this.verbPhrase.setParentSpec(this);
	}

	/**
	 * Gets the verb phrase.
	 * 
	 * @return The verb phrase
	 */
	public VPPhraseSpec getVerbPhrase() {
		return this.verbPhrase;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.HeadedPhraseSpec#setHead(simplenlg.lexicon.lexicalitems
	 * .LexicalItem)
	 */
	@Override
	public void setHead(Verb head) {
		this.verbPhrase.setHead(head);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.HeadedPhraseSpec#setHead(java.lang.String)
	 */
	@Override
	public void setHead(String head) {
		this.verbPhrase.setHead(head);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.HeadedPhraseSpec#getHead()
	 */
	@Override
	public Verb getHead() {
		return this.verbPhrase.getHead();
	}

	/**
	 * An alias for {@link #getHead()}.
	 * 
	 * @return The verb that heads the verb phrase in this sentence.
	 * 
	 * @deprecated This method is included for backward compatibility. The use
	 *             of {@link #getHead()} is encouraged.
	 * @see #getHead()
	 */
	@Deprecated
	public Verb getVerb() {
		return getHead();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.HeadedPhraseSpec#getHeadAsString()
	 */
	@Override
	public String getHeadAsString() {
		Verb v = getHead();

		if (v != null) {
			return v.getBaseForm();
		}

		return null;
	}

	/**
	 * An alias for {@link #getHeadAsString()}.
	 * 
	 * @return the baseform of the verb heading the verb phrase
	 * 
	 * @deprecated This method is included for backward compatibility. The use
	 *             of {@link #getHeadAsString()} is now recommended.
	 * @see #getHeadAsString()
	 */
	@Deprecated
	public String getVerbAsString() {
		return getHeadAsString();
	}

	/**
	 * Add a verb (in addition to the existing verb). A new
	 * {@link simplenlg.realiser.VPPhraseSpec} is constructed, and it is forced
	 * to inherit all previously defined temporal, aspectual and other
	 * simplenlg.features (unlike {@link #addVerbPhrase(VPPhraseSpec)}.
	 * Examples:
	 * <ul>
	 * <li>the man walks and talks (present tense)</li>
	 * <li>the man is walking and talking (progressive)</li>
	 * <li>the man had been walking, talking and singing (past, perfect,
	 * progressive)</li>
	 * </ul>
	 * 
	 * @param verb
	 *            The new verb to be added, a
	 *            {@link simplenlg.lexicon.lexicalitems.Verb} or
	 *            <code>String</code>
	 * 
	 * @see #setVerb(Object)
	 * @see #setVerbPhrase(VPPhraseSpec)
	 * @see #addVerbPhrase(VPPhraseSpec)
	 */
	public void addVerb(Object verb) {
		VPPhraseSpec newVP = new VPPhraseSpec();

		if (verb instanceof String) {
			newVP.setHead((String) verb);
		} else if (verb instanceof Verb) {
			newVP.setHead((Verb) verb);
		} else {
			throw new SimplenlgException(
					"Can only add String or Verb as head of SPhraseSpec.");
		}

		newVP = fixAllFeatures(newVP);

		if (this.verbPhrase.hasHead()) {

			if (this.verbPhrase.isCoordinate()) {
				((CoordinateVPPhraseSpec) this.verbPhrase)
						.addCoordinates(newVP);
			} else {
				this.verbPhrase = this.verbPhrase.coordinate(newVP);
			}
		} else {
			this.verbPhrase = newVP;
		}
	}

	/**
	 * Gets the verb particle.
	 * 
	 * @return The verb particle if the head of the verb phrase has one,
	 *         <code>null</code> otherwise.
	 */
	public String getVerbParticle() {

		if (this.verbPhrase.hasHead()) {
			return this.verbPhrase.getHead().getParticle();
		}

		return null;
	}

	/**
	 * Sets the particle to the main verb, constructing a phrasal verb such as
	 * <i>get <strong>up</strong></i>. This only works if the verb has actually
	 * been set, that is, the verbPhrase has a head.
	 * 
	 * @param particle
	 *            The particle
	 */
	public void setVerbParticle(String particle) {

		if (this.verbPhrase.hasHead()) {
			this.verbPhrase.setParticle(particle);
		}
	}

	/**
	 * Sets whether the SPhraseSpec is to undergo its default behaviour, which
	 * is to render any nominal subjects as possessive if its form its changed
	 * to gerund (<I>John's kissing Mary</I>).
	 * 
	 * @param suppress
	 *            Whether to suppress the default behaviour.
	 */
	public void suppressGenitiveInGerund(boolean suppress) {
		this.suppressGenitiveInGerund = suppress;
	}

	// **************************************
	// Getters, setters for PHRASAL COMPONENTS
	// *************************************

	/**
	 * Add an adverbial modifier to this sentence. These modifiers can occur in
	 * a variety of positions, and this is indicated by passing a value of
	 * {@link SModifierPosition} to the method call. Adverbial modifiers can be
	 * strings or phrases.
	 * 
	 * <P>
	 * Currently, the following possibilities are allowed:
	 * 
	 * <OL>
	 * <LI><i>Fronted adverbial (front of sentence)</i>:
	 * <strong>Slowly</strong>, Bill walked to the park.</LI>
	 * <LI><i>Verb pre-modifier (before the verb)</i>: Bill
	 * <strong>quickly</strong> ate his dinner.</LI>
	 * <LI><i>Verb post-modifier (after the verb)</i>: Bill ate his dinner
	 * <strong>quickly</strong>.</LI>
	 * </OL>
	 * 
	 * <P>
	 * No restriction is placed on the category of the phrase passed as a
	 * modifier. For example, modifiers can be prepositional phrases (<i>Bill
	 * ate his dinner <strong>in double-quick time</strong></i>).
	 * 
	 * @param position
	 *            The position of the adverbial modifier, a value of
	 *            {@link SModifierPosition}
	 * @param modifier
	 *            The modifier itself. This must be a <code>String</code> or
	 *            {@link simplenlg.realiser.Phrase}
	 */
	public void addModifier(SModifierPosition position, Object modifier) {

		switch (position) {

		case FRONT:
			this.frontModifiers.add(makeConstituent(modifier,
					DiscourseFunction.PREMODIFIER));
			break;

		case PRE_VERB:
			this.verbPhrase.addPremodifier(modifier);
			break;

		case POST_VERB:
			this.verbPhrase.addPostmodifier(modifier);
			break;
		}
	}

	/**
	 * Sets the adverbial phrase in the specified position, replacing existing
	 * adverbials in that position.
	 * 
	 * @param position
	 *            The position of the adverbial modifier, a value of
	 *            {@link SModifierPosition}
	 * @param modifier
	 *            The modifier itself. This must be a <code>String</code> or
	 *            {@link simplenlg.realiser.Phrase}
	 * 
	 * @see #addModifier(simplenlg.features.SModifierPosition, Object) for a
	 *      description of modifier types
	 * @see #setPremodifier(Object)
	 * @see #addPremodifier(Object)
	 * @see #setPostmodifier(Object)
	 * @see #addPostmodifier(Object)
	 */
	public void setModifier(SModifierPosition position, Object modifier) {

		switch (position) {

		case FRONT:
			this.frontModifiers.clear();
			this.frontModifiers.add(makeConstituent(modifier,
					DiscourseFunction.FRONT_MODIFIER));
			break;

		case PRE_VERB:
			this.verbPhrase.setPremodifier(modifier);
			break;

		case POST_VERB:
			this.verbPhrase.setPostmodifier(modifier);
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 * <P>
	 * In <code>SPhraseSpec</code>, this results in the modifier being added to
	 * the verb phrase
	 * 
	 * @see simplenlg.realiser.VPPhraseSpec#addPremodifier(Object)
	 */
	@Override
	public void addPremodifier(Object mod) {
		this.verbPhrase.addPremodifier(mod);
	}

	/**
	 * {@inheritDoc}
	 * <P>
	 * In <code>SPhraseSpec</code>, this results in the modifier being set in
	 * the verb phrase
	 * 
	 * @see simplenlg.realiser.VPPhraseSpec#setPremodifier(Object)
	 */
	@Override
	public void setPremodifier(Object mod) {
		this.verbPhrase.setPremodifier(mod);
	}

	/**
	 * {@inheritDoc}
	 * <P>
	 * In <code>SPhraseSpec</code>, this results in the modifier being added to
	 * the verb phrase
	 * 
	 * @see simplenlg.realiser.VPPhraseSpec#addPostmodifier(Object)
	 */
	@Override
	public void addPostmodifier(Object mod) {
		this.verbPhrase.addPostmodifier(mod);
	}

	/**
	 * {@inheritDoc}
	 * <P>
	 * In <code>SPhraseSpec</code>, this results in the modifier being set in
	 * the verb phrase
	 * 
	 * @see simplenlg.realiser.VPPhraseSpec#setPostmodifier(Object)
	 */
	@Override
	public void setPostmodifier(Object mod) {
		this.verbPhrase.setPostmodifier(mod);
	}

	/**
	 * Gets the subjects.
	 * 
	 * @return The list of subjects specified for this sentence if any, the
	 *         empty list otherwise.
	 */
	public List<Phrase> getSubjects() {
		return this.subjects;
	}

	/**
	 * Gets the surface subjects. Unless the sentence is passive, the return
	 * value of this method is identical to that of {@link #getSubjects()}; in
	 * case the sentence is passive, the VP complements are returned.
	 * 
	 * @return The surface subjects if any, the empty list otherwise.
	 */
	public List<Phrase> getSurfaceSubjects() {
		return this.verbPhrase.getPassiveRaisingComplements();
	}

	/**
	 * Adds a subjects (in addition to existing subjects). Internally, adding a
	 * subjects involves the following special operations:
	 * <OL>
	 * <LI>If the object is a <code>String</code>, it is converted to a
	 * {@link simplenlg.realiser.StringPhraseSpec} <I>unless</I> the string is
	 * the baseform of a personal pronoun, such as <i>I</i> or <I>he</I></LI>
	 * <LI>If the object is an <code>SPhraseSpec</code>, then the the sentence
	 * {@link simplenlg.features.Form} is changed to
	 * {@link simplenlg.features.Form#GERUND}. For example, suppose the current
	 * sentence is <i>X upset Peter</i> and the sentence <i>John kissed Mary</i>
	 * is passed as subjects. Changing its form to gerund causes it to be
	 * realised as <i>John's kissing Mary</i>, so that the full sentence is
	 * <i>John's kissing Mary upset Peter</i>.
	 * </OL>
	 * 
	 * @param sub
	 *            The subjects to be added, a <code>String</code> or
	 *            {@link simplenlg.realiser.Phrase}
	 */
	public void addSubject(Object sub) { // add a subjects
		Phrase subjectPhrase = makeConstituent(sub, DiscourseFunction.SUBJECT);
		this.subjects.add(subjectPhrase);
	}

	/**
	 * Sets the subjects (replaces existing subjects).
	 * 
	 * @param sub
	 *            The subjects to be added, a <code>String</code> or
	 *            {@link simplenlg.realiser.Phrase}
	 * 
	 * @see simplenlg.realiser.SPhraseSpec#addSubject(Object)
	 */
	public void setSubject(Object sub) { // set a subjects (gets rid of
		this.subjects.clear();
		addSubject(sub);
	}

	/**
	 * Checks for subject.
	 * 
	 * @return <code>true</code> if this sentence has one or more subjects
	 */
	public boolean hasSubject() {
		return this.subjects.size() > 0;
	}

	/**
	 * Gets the modifiers.
	 * 
	 * @param position
	 *            The position required, a value of
	 *            {@link simplenlg.features.SModifierPosition}
	 * 
	 * @return A <code>java.util.List</code> of the phrases functioning as
	 *         modifiers in this position.
	 */
	public List<Phrase> getModifiers(SModifierPosition position) {
		switch (position) {
		case FRONT:
			return this.frontModifiers;
		case PRE_VERB:
			return this.verbPhrase.getPremodifiers();
		case POST_VERB:
			return this.verbPhrase.getPostmodifiers();
		default:
			return null; // not required, makes java happy
		}
	}

	/**
	 * Adds a complement (in addition to existing complements). Internally, this
	 * results in a call to
	 * {@link simplenlg.realiser.VPPhraseSpec#addComplement(Object)} in the verb
	 * phrase. As per the documentation for that method, the complement is by
	 * default specified as having
	 * {@link simplenlg.features.DiscourseFunction#OBJECT}.
	 * 
	 * @param comp
	 *            The new complement
	 * 
	 * @see #addComplement(DiscourseFunction, Object)
	 * @see simplenlg.realiser.VPPhraseSpec#addComplement(Object)
	 */
	@Override
	public void addComplement(Object comp) {
		this.verbPhrase.addComplement(comp);
	}

	/**
	 * Replaces any existing complements with the new complement. This involves
	 * a call to {@link simplenlg.realiser.VPPhraseSpec#setComplement(Object)}
	 * in the verb phrase.
	 * 
	 * @param comp
	 *            The new complement
	 * 
	 * @see simplenlg.realiser.SPhraseSpec#addComplement(Object)
	 * @see simplenlg.realiser.VPPhraseSpec#setComplement(Object)
	 */
	@Override
	public void setComplement(Object comp) {
		this.verbPhrase.setComplement(comp);
	}

	/**
	 * Unlike {@link #setComplement(Object)}, which resets all complements, this
	 * method will only replace the complements with the given function, if
	 * there are any.
	 * 
	 * @param comp
	 *            The new complement
	 * @param function
	 *            The function
	 * 
	 * @see simplenlg.realiser.SPhraseSpec#addComplement(Object)
	 * @see simplenlg.realiser.VPPhraseSpec#setComplement(Object)
	 */
	public void setComplement(Object comp, DiscourseFunction function) {
		this.verbPhrase.setComplement(comp, function);
	}

	/**
	 * Add a complement to the verbphrase of this sentence. Unlike
	 * {@link SPhraseSpec#addComplement(Object)}, this method permits the
	 * specification of the function of the new complement. For example, it is
	 * possible to specify that this complement is an
	 * {@link simplenlg.features.DiscourseFunction#INDIRECT_OBJECT}.
	 * 
	 * <P>
	 * Like {@link SPhraseSpec#addComplement(Object)}, this method calls the
	 * {@link VPPhraseSpec#addComplement(Object)} in the class
	 * {@link simplenlg.realiser.VPPhraseSpec}
	 * 
	 * @param func
	 *            The discourse function of the new complement
	 * @param comp
	 *            The complement itself, a {@link simplenlg.realiser.Phrase} or
	 *            <code>String</code>.
	 * 
	 * @see simplenlg.realiser.VPPhraseSpec#addComplement(Object)
	 * @see simplenlg.realiser.SPhraseSpec#addComplement(Object)
	 */
	public void addComplement(DiscourseFunction func, Object comp) {
		this.verbPhrase.addComplement(comp, func);
	}

	/**
	 * Gets the complements.
	 * 
	 * @return The complements of the verb phrase, a <code>java.util.List</code>
	 *         of phrases.
	 */
	@Override
	public List<Phrase> getComplements() {
		return this.verbPhrase.getComplements();
	}

	/**
	 * Returns the complements of the verb phrase with the given
	 * <code>DiscourseFunction</code>.
	 * 
	 * @param func
	 *            The function
	 * 
	 * @return A <code>List</code> containing the VP complements with this
	 *         function, if any.
	 */
	public List<Phrase> getComplements(DiscourseFunction func) {
		return this.verbPhrase.getComplements(func);
	}

	/**
	 * Returns the indirect object complements of the verb phrase.
	 * 
	 * @return A <code>List</code> containing the indirect object complements of
	 *         the VP, if any.
	 * 
	 * @deprecated This method is included for backward compatibility. The use
	 *             of {@link #getComplements(DiscourseFunction)} is now
	 *             recommended.
	 */
	@Deprecated
	public List<Phrase> getIndirectObjects() {
		return this.verbPhrase
				.getComplements(DiscourseFunction.INDIRECT_OBJECT);
	}

	/**
	 * Sets the cue phrase (replaces existing cue phrase).
	 * 
	 * @param cue
	 *            The new cue phrase, a <code>String</code> or
	 *            {@link simplenlg.realiser.Phrase}
	 */
	public void setCuePhrase(Object cue) {
		this.cuePhrase = makeConstituent(cue, DiscourseFunction.CUE_PHRASE);
	}

	/**
	 * Gets the cue phrase.
	 * 
	 * @return The cue phrase if one has been specified, null otherwise.
	 */
	public Phrase getCuePhrase() {
		return this.cuePhrase;
	}

	/**
	 * This method sets the complementiser to be used in case this sentence is
	 * subordinated. The default is <i>that</i>, as in <i>Bill said <strong>that
	 * John kissed Mary</strong></i>.
	 * 
	 * <P>
	 * Note that setting the complementiser does not make the sentence
	 * subordinate, it merely changes the word used should this sentence be made
	 * subordinate. Moreover, the complementiser can be suppressed.
	 * 
	 * @param comp
	 *            The new complementiser, a <code>String</code>
	 * 
	 * @see SPhraseSpec#setClauseStatus(simplenlg.features.ClauseStatus)
	 * @see SPhraseSpec#suppressComplementiser(boolean)
	 */
	public void setDefaultComplementiser(String comp) {
		this.complementiser = comp;
	}

	/**
	 * Gets the default complementiser.
	 * 
	 * @return The default complementiser. By default, <i>that</i>, but can be
	 *         reset.
	 * 
	 * @see SPhraseSpec#setDefaultComplementiser(String)
	 */
	public String getDefaultComplementiser() {
		return this.complementiser;
	}

	/**
	 * Checks if the <strong>Verb Phrase</strong> in this sentence has
	 * pre-modifiers.
	 * 
	 * @return <code>true</code> if the verb phrase is premodified.
	 */
	@Override
	public boolean hasPremodifiers() {
		return this.verbPhrase.hasPremodifiers();
	}

	/**
	 * Checks if the <strong>Verb Phrase</strong> in this sentence has
	 * complements.
	 * 
	 * @return <code>true</code> if the verb phrase has complements.
	 */
	@Override
	public boolean hasComplements() {
		return this.verbPhrase.hasComplements();
	}

	/**
	 * Checks if the <strong>Verb Phrase</strong> in this sentence has
	 * postmodifiers.
	 * 
	 * @return <code>true</code> if the verb phrase is postmodified.
	 */
	@Override
	public boolean hasPostmodifiers() {
		return this.verbPhrase.hasPostmodifiers();
	}

	/**
	 * Checks if the <strong>Sentence</strong> has front modifiers.
	 * 
	 * @return <code>true</code> if the front modifiers of this sentence have
	 *         been set.
	 * 
	 * @see #addModifier(SModifierPosition, Object)
	 * @see #addFrontModifier(Object)
	 */
	public boolean hasFrontModifiers() {
		return !this.frontModifiers.isEmpty();
	}

	// **********************************************************************************
	// GETTERS AND SETTERS INCLUDED FOR BACKWARD COMPATIBILITY
	// **********************************************************************************

	/**
	 * Gets (beginning of phrase) modifiers.
	 * 
	 * @return A <code>List</code> of sentential modifiers, if any.
	 * 
	 * @see #getModifiers(simplenlg.features.SModifierPosition)
	 */
	public List<Phrase> getFrontModifiers() {
		return this.frontModifiers;
	}

	/**
	 * Adds an indirect object (in addition to existing indirect objects).
	 * 
	 * @param indirectObject
	 *            The new indirect object, a <code>Phrase</code> or
	 *            <code>String</code>
	 * 
	 * @see #addComplement(Object)
	 * @see #addComplement(DiscourseFunction, Object)
	 * @see #setIndirectObject(Object)
	 */
	public void addIndirectObject(Object indirectObject) { // add a complement
		this.verbPhrase.addComplement(indirectObject,
				DiscourseFunction.INDIRECT_OBJECT);
	}

	/**
	 * Sets the indirect object (replacing existing ones) in the verb phrase.
	 * 
	 * @param indirectObject
	 *            The new indirect object.
	 */
	public void setIndirectObject(Object indirectObject) {
		this.verbPhrase.setComplement(indirectObject,
				DiscourseFunction.INDIRECT_OBJECT);
	}

	/**
	 * Adds a (beginning of phrase) modifier (in addition to existing front
	 * endModifiers).
	 * 
	 * @param modifier
	 *            The new modifier
	 */
	public void addFrontModifier(Object modifier) { // add a front modifier
		// put new front endModifiers at beginning of list
		if (modifier != null) {
			PhraseSpec spec = makePhraseSpec(modifier);
			spec.setDiscourseFunction(DiscourseFunction.FRONT_MODIFIER);
			this.frontModifiers.add(0, spec);
		}
	}

	// **************************************
	// BOOLEAN simplenlg.features
	// **************************************

	/**
	 * Checks for cue phrase.
	 * 
	 * @return <code>true</code> if phrase spec has a cue phrase
	 */
	public boolean hasCuePhrase() {
		return this.cuePhrase != null;
	}

	/**
	 * Checks if is subordinate clause.
	 * 
	 * @return <code>true</code> if this clause has been subordinated.
	 * 
	 * @see SPhraseSpec#setClauseStatus(simplenlg.features.ClauseStatus)
	 */
	public boolean isSubordinateClause() {
		return this.status.equals(ClauseStatus.SUBORDINATE);
	}

	/**
	 * Checks if is main clause.
	 * 
	 * @return <code>true</code> if this clause has not been subordinated.
	 * 
	 * @see SPhraseSpec#setClauseStatus(simplenlg.features.ClauseStatus)
	 */
	public boolean isMainClause() {
		return this.status.equals(ClauseStatus.MATRIX);
	}

	/**
	 * This method determines whether or not an <code>SPhraseSpec</code> which
	 * has been subordinated should be expressed using the complementiser or
	 * not. This is <i>true</i> by default, yielding realisations such as
	 * <i>John said <strong>that Bill kissed Mary</strong></i>. If set to false,
	 * the realisation is <i>John said <strong>Bill kissed Mary</strong></i>.
	 * 
	 * <P>
	 * Note that the suppression of the complementiser must be done within the
	 * subordinate, not the main clause.
	 * </P>
	 * 
	 * @param suppress
	 *            Whether or not to suppress the complementiser.
	 * 
	 * @see SPhraseSpec#setDefaultComplementiser(String)
	 * @see SPhraseSpec#setClauseStatus(simplenlg.features.ClauseStatus)
	 */
	public void suppressComplementiser(boolean suppress) {
		this.suppressComp = suppress;
	}

	/**
	 * Sets the clause status (main or subordinate) of this sentence.
	 * 
	 * @param type
	 *            A value of {@link ClauseStatus}
	 */
	void setClauseStatus(ClauseStatus type) {
		this.status = type;
	}

	/**
	 * Set whether to realise this sentence as a question, and if so, what type
	 * of question. This method covers basic "closed-ended" (yes/no) questions,
	 * as well as all WH-questions except those beginning with <I>which</I>.
	 * 
	 * <P>
	 * See {@link simplenlg.features.InterrogativeType} for the different
	 * options available.
	 * 
	 * <P>
	 * By default, if the type paramater passed to this method is one of the
	 * wh-question types, the sentence will be realised as a
	 * <strong>subject</strong> WH question. For example, invoking
	 * <code>setInterrogative(InterrogativeType.WHO)</code> from the sentence
	 * <I>John became a professor.</I> will make it be realised as the question
	 * <I>Who became a professor?</I>.
	 * 
	 * <P>
	 * The alternative method
	 * {@link #setInterrogative(InterrogativeType, DiscourseFunction)} is
	 * provided to override this default behaviour.
	 * 
	 * <P>
	 * To set a sentence back to declarative, it suffices to pass
	 * <code>null</code> as argument to this method.
	 * 
	 * @param type
	 *            The type of interrogative
	 * 
	 * @see #setInterrogative(InterrogativeType, DiscourseFunction)
	 */
	public void setInterrogative(InterrogativeType type) {

		if (type == null) {
			this.interrogative = false;
			this.interrogType = null;
			this.whArgument = null;

			// revert to default punct unless user-set
			if (!this.suppressPunctuationDefault) {
				this.terminator = '.';
			}

		} else {
			this.interrogType = type;
			this.interrogative = true;

			if (type.equals(InterrogativeType.YES_NO)
					|| type.equals(InterrogativeType.HOW)) {
				this.whArgument = null;
			} else {
				this.whArgument = DiscourseFunction.SUBJECT;
			}

			// change default terminator unless user-set
			if (!this.suppressPunctuationDefault) {
				this.terminator = '?';
			}
		}
	}

	/**
	 * Overload of {@link #setInterrogative(InterrogativeType)} for
	 * wh-questions, allowing the specification of which argument (subject,
	 * object or indirect object) the wh-pronoun refers to. The argument will
	 * not be realised explicitly.
	 * 
	 * <P>
	 * If the type of interrogative passed as first parameter is
	 * {@link simplenlg.features.InterrogativeType#YES_NO} or
	 * {@link simplenlg.features.InterrogativeType#HOW}, the second parameter
	 * will be ignored, as simple yes/no questions and how-questions do not
	 * result in suppression of arguments.
	 * 
	 * <P>
	 * If the type of interrogative is <code>null</code>, this result in the
	 * sentence being realised as declarative.
	 * 
	 * <P>
	 * <STRONG>NB</STRONG>: The argument that is specified is the <I>logical</I>
	 * argument, not the surface argument. For example, the logical subject of
	 * <I>John was chased by the cat<I> is <I>the cat</I>. Therefore, invoking
	 * this method and specifying that the WH-pronoun refers to the SUBJECT will
	 * make the sentence be realised as <I>Who/What was John chased by?</I>. To
	 * obtain the realisation <I>Who was chased by the cat?</I>, it is the
	 * OBJECT that must be specified.
	 * </P>
	 * 
	 * @param type
	 *            The type of interrogative
	 * @param func
	 *            The argument which will be replaced by the WH-pronoun
	 *            (subject, object or indirect object)
	 */
	public void setInterrogative(InterrogativeType type, DiscourseFunction func) {

		if (!(func.equals(DiscourseFunction.SUBJECT)
				|| func.equals(DiscourseFunction.OBJECT) || func
				.equals(DiscourseFunction.INDIRECT_OBJECT))) {
			throw new SimplenlgException("Only SUBJECT, OBJECT or "
					+ "INDIRECT_OBJECT are covered by WH questions");
		}

		setInterrogative(type);

		if (type.isWhQuestion()) {
			this.whArgument = func;
		}
	}

	/**
	 * Checks whether this sentence will be realised as an interrogative.
	 * 
	 * @return <code>true</code> if, and only if, the sentence has been set to
	 *         an interrogative type.
	 * 
	 * @see #setInterrogative(InterrogativeType)
	 * @see #setInterrogative(InterrogativeType, DiscourseFunction)
	 */
	public boolean isInterrogative() {
		return this.interrogative;
	}

	/**
	 * Gets the type of interrogative set for this sentence.
	 * 
	 * @return The Interrogative type, if any has been set, <code>null</code>
	 *         otherwise.
	 * 
	 * @see #setInterrogative(InterrogativeType)
	 * @see #setInterrogative(InterrogativeType, DiscourseFunction)
	 */
	public InterrogativeType getInterrogativeType() {
		return this.interrogType;
	}

	/**
	 * If this sentence is interrogative, and specified as a WH-type
	 * interrogative (who, which or where), this method returns the argument
	 * (i.e. the function, such as SUBJECT or OBJECT) to which the WH-pronoun
	 * refers.
	 * 
	 * @return the WH-argument, just in case this sentence is a
	 *         wh-interrogative, <code>null</code> otherwise.
	 * 
	 * @see #setInterrogative(InterrogativeType)
	 * @see #setInterrogative(InterrogativeType, DiscourseFunction)
	 */
	public DiscourseFunction getWhArgument() {
		return this.whArgument;
	}

	/**
	 * Sets the punctuation mark to terminate this sentence. Unless set
	 * explicitly, the sentence is realised with a default punctuation mark,
	 * namely a full stop (.) if declarative, a question mark (?) if
	 * interrogative.
	 * 
	 * @param terminator
	 *            The new terminator
	 */
	public void setSentenceTerminator(char terminator) {
		this.terminator = terminator;
		this.suppressPunctuationDefault = true;
	}

	/**
	 * Gets the punctuation mark used to terminate this sentence.
	 * 
	 * @return the sentence-final punctuation mark
	 */
	public char getSentenceTerminator() {
		return this.terminator;
	}

	// **************************************
	// TENSE, ASPECT, MOOD etc simplenlg.features
	// **************************************

	/**
	 * Sets the tense of a phrase. Involves a call to
	 * {@link simplenlg.realiser.VPPhraseSpec#setTense(Tense)} in the verb
	 * phrase.
	 * 
	 * @param t
	 *            The tense, a value of {@link simplenlg.features.Tense}
	 */
	public void setTense(Tense t) {
		this.verbPhrase.setTense(t);
	}

	/**
	 * Gets the tense.
	 * 
	 * @return The {@link simplenlg.features.Tense} of a phrase
	 */
	public Tense getTense() {
		return this.verbPhrase.getTense();
	}

	/**
	 * Gets the modal.
	 * 
	 * @return the modal auxiliary of this phrase
	 */
	public String getModal() {
		return this.verbPhrase.getModal();
	}

	/**
	 * Sets the modal auxiliary of the verb (eg, "can", "must"). Involves a call
	 * to {@link simplenlg.realiser.VPPhraseSpec#setModal(String)} in the verb
	 * phrase.
	 * 
	 * @param modal
	 *            The new modal
	 */
	public void setModal(String modal) {
		this.verbPhrase.setModal(modal);
	}

	/**
	 * This method sets the "form" (infinitive, bare infitive, subjunctive,
	 * gerund or default) of the sentence.
	 * <p>
	 * This method calls the
	 * {@link simplenlg.realiser.VPPhraseSpec#setForm(Form)} method of the child
	 * verb phrase.
	 * </p>
	 * 
	 * @param f
	 *            The {@link simplenlg.features.Form} of this sentence.
	 * 
	 * @see VPPhraseSpec#setForm(Form)
	 */
	public void setForm(Form f) {
		try {
			this.verbPhrase.setForm(f);

		} catch (SimplenlgException se) {
			throw se;
		}
	}

	/**
	 * Gets the form.
	 * 
	 * @return The {@link simplenlg.features.Form} of this sentence.
	 */
	public Form getForm() {
		return this.verbPhrase.getForm();
	}

	/**
	 * Sets whether the phrase is perfective or not. This involves a call the
	 * {@link simplenlg.realiser.VPPhraseSpec#setPerfect(boolean)} in the verb
	 * phrase.
	 * 
	 * @param perf
	 *            Determines whether the verb phrase is perfective
	 */
	public void setPerfect(boolean perf) {
		this.verbPhrase.setPerfect(perf);
	}

	/**
	 * Checks if is perfect.
	 * 
	 * @return <code>true</code> if this sentence is perfect
	 * 
	 * @see simplenlg.realiser.VPPhraseSpec#isPerfect()
	 */
	public boolean isPerfect() {
		return this.verbPhrase.isPerfect();
	}

	/**
	 * Sets whether the sentence has progressive aspect.
	 * <P>
	 * For example,
	 * <UL>
	 * <li>"John eats" (not progressive)
	 * <li>"John is eating" (progressive)
	 * </UL>
	 * 
	 * <P>
	 * This method involves a call to
	 * {@link simplenlg.realiser.VPPhraseSpec#setProgressive(boolean)}
	 * 
	 * @param prog
	 *            progressive aspect if <code>true</code>
	 */
	public void setProgressive(boolean prog) {
		this.verbPhrase.setProgressive(prog);
	}

	/**
	 * Checks if is progressive.
	 * 
	 * @return <code>true</code> if sentence is in Progressive aspect.
	 * 
	 * @see SPhraseSpec#setProgressive(boolean)
	 */
	public boolean isProgressive() {
		return this.verbPhrase.isProgressive();
	}

	/**
	 * Sets whether the sentence is negated. Involves a call to
	 * {@link simplenlg.realiser.VPPhraseSpec#setNegated(boolean)}.
	 * 
	 * <P>
	 * For example,
	 * <UL>
	 * <li>"John eats" (not negated)
	 * <li>"John does not eat" (negated)
	 * </UL>
	 * 
	 * @param neg
	 *            sentence is negated if <code>true</code>
	 */
	public void setNegated(boolean neg) {
		this.verbPhrase.setNegated(neg);
	}

	/**
	 * Checks if is negated.
	 * 
	 * @return <code>true</code> if sentence is negated.
	 * 
	 * @see SPhraseSpec#setNegated(boolean)
	 */
	public boolean isNegated() {
		return this.verbPhrase.isNegated();
	}

	/**
	 * Checks if is passive.
	 * 
	 * @return <code>true</code> if sentence is in passive voice.
	 * 
	 * @see SPhraseSpec#setPassive(boolean)
	 */
	public boolean isPassive() {
		return this.verbPhrase.isPassive();
	}

	/**
	 * Sets whether the sentence is passive voice. Involves a call to
	 * {@link simplenlg.realiser.VPPhraseSpec#setPassive(boolean)}.
	 * 
	 * <P>
	 * For example,
	 * <UL>
	 * <li>"John eats an apple" (not passive)
	 * <li>"An apple is eaten by John" (passive)
	 * </UL>
	 * 
	 * @param pass
	 *            passive voice if <code>true</code>
	 * 
	 * @see simplenlg.realiser.VPPhraseSpec#setPassive(boolean)
	 */
	public void setPassive(boolean pass) {
		this.verbPhrase.setPassive(pass);
	}

	// ****************************************
	// Coordination
	// ****************************************

	/**
	 * {@inheritDoc}
	 */
	public SPhraseSpec coordinate(Phrase... coords) {

		if (coords.length == 0) {
			return this;
		}

		CoordinateSPhraseSpec csp = new CoordinateSPhraseSpec(this);

		try {

			for (Phrase p : coords) {
				csp.addCoordinates((SPhraseSpec) p);
			}

			return csp;

		} catch (ClassCastException cce) {
			throw new SimplenlgException("Cannot coordinate: "
					+ "only phrases of the same type can be coordinated");
		}
	}

	@Override
	public boolean equals(Object o) {
		boolean eq = false;

		if (o instanceof SPhraseSpec) {
			SPhraseSpec s = (SPhraseSpec) o;
			eq = (this.cuePhrase == s.cuePhrase || this.cuePhrase
					.equals(s.cuePhrase))
					&& (this.verbPhrase == s.verbPhrase || this.verbPhrase
							.equals(s.verbPhrase))
					&& this.frontModifiers.equals(s.frontModifiers)
					&& this.subjects.equals(s.subjects);
		}

		return eq;
	}

	// ****************************************
	// Realisation methods
	// ****************************************

	/**
	 * Checks if is be verb.
	 * 
	 * @param verb
	 *            the verb
	 * 
	 * @return true, if is be verb
	 */
	boolean isBeVerb(String verb) {
		// returns T if this verb is a form of "be"
		return verb.equalsIgnoreCase("be") || verb.equalsIgnoreCase("am")
				|| verb.equalsIgnoreCase("are") || verb.equalsIgnoreCase("is")
				|| verb.equalsIgnoreCase("was")
				|| verb.equalsIgnoreCase("were");
	}

	/*
	 * Realise an Sphrase spec: 1. Compute the surface arguments 2. Compute the
	 * VP agreement 3. Compute the right Form
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.HeadedPhraseSpec#realise(simplenlg.realiser.Realiser)
	 */
	@Override
	String realise(Realiser r) {
		// initialise args for VP -- in case we've suppressed anything during a
		// previous op
		this.verbPhrase.initialiseArgs();

		// first compute surface subject(s) and passive comp if required
		computeSurfaceArgs();

		// set the right form for the realisation
		computeForm();

		// compute agreement features for VP
		computeVPAgreement();

		// now realise components
		String cuePhraseText = r.realise(this.cuePhrase);
		String frontModifierText = r.realiseConjunctList(this.frontModifiers,
				",");
		String verbText = this.verbPhrase.realise(r, needsFrontAuxiliary());
		String frontedElements = getFrontedElements(r);
		String compText = r.realise(this.passiveComplement);
		String mainSent = ""; // main sentence

		if (this.interrogative) {
			mainSent = r.appendSpace(cuePhraseText, frontedElements,
					this.surfaceSubject.realise(r), verbText, compText,
					frontModifierText);

		} else {
			String frontSeparator = frontModifierText.length() == 0 || 
			(cuePhraseText != null && cuePhraseText.endsWith(",")) ? "" : ",";
			mainSent = r.appendSpace(cuePhraseText, frontModifierText,
					frontSeparator, this.surfaceSubject.realise(r), verbText,
					compText);
		}

		// add complementiser if req.
		if (isSubordinateClause() && !this.suppressComp) {
			mainSent = r.appendSpace(this.complementiser, mainSent);
		}

		String result;

		// if parent is a text spec, check whether to override default
		// capitalisation
		if (this.parentSpec != null) {
			if (this.parentSpec instanceof TextSpec
					&& ((TextSpec) parentSpec).hasDefaultCaps()) {
				((TextSpec) this.parentSpec)
						.setDefaultCaps(canCapitaliseFirstChar());
			}
			result = mainSent;
		} else {
			result = r.applySentenceOrthography(mainSent, this.terminator,
					canCapitaliseFirstChar());
		}

		return result;
	}

	/*
	 * Check whether the first subject of this sentence can be capitalised. This
	 * returns false just in case the first subject is an NPPhraseSpec which is
	 * marked as an acronym and doesn't have a specifier.
	 */
	boolean canCapitaliseFirstChar() {
		List<Phrase> subjects = this.surfaceSubject.getCoordinates();
		boolean capitalise = true;

		if (!subjects.isEmpty()) {
			Phrase first = subjects.get(0);

			if (first instanceof NPPhraseSpec) {
				NPPhraseSpec np = (NPPhraseSpec) first;
				capitalise = !np.isAcronym() || np.hasSpecifier();
			}
		}

		return capitalise;
	}

	/*
	 * check whether this needs a fronted aux or not. Boolean value can be
	 * passed to VPPhraseSpec.realise() to stop it from realising the aux when
	 * it's fronted.
	 */
	boolean needsFrontAuxiliary() {

		if (this.interrogative) {
			// wh questions only need fronted AUX with passive subject
			if (this.interrogType.isWhQuestion()) {
				return !this.whArgument.equals(DiscourseFunction.SUBJECT)
						|| isPassive();
			} else {
				return true;
			}
		}

		return false;
	}

	/*
	 * If this is interrogative, returns the wh pronoun (if any) + fronted aux
	 * (if any). Empty string otherwise.
	 */
	String getFrontedElements(Realiser r) {
		String fronted = "";

		// operations for interrogatives: front elements except when subordinate
		if (this.interrogative && !isSubordinateClause()) {

			// frontmost element is always the wh pro unless yes/no
			if (!this.interrogType.equals(InterrogativeType.YES_NO)) {
				fronted = this.interrogType.toString().toLowerCase();
			}

			if (needsFrontAuxiliary()) {
				fronted = r.appendSpace(fronted, this.verbPhrase
						.getInterrogativeFrontedElement(r.getLexicon()));
			}
		}

		return fronted;
	}

	/*
	 * build a passive complement if required. Boolean arg indicates whether to
	 * also have the complements, or simply have "by" (used for questions like
	 * "who does John get kicked by?")
	 */
	void buildPassiveComplement(boolean comps) {
		List<Phrase> actualSubjects = getNonElidedSubjects();

		if (!actualSubjects.isEmpty()) {
			this.passiveComplement = new PPPhraseSpec("by");

			if (comps) {
				for (Phrase p : actualSubjects) {
					p.setDiscourseFunction(DiscourseFunction.PREP_OBJECT);
					this.passiveComplement.addComplement(p);
				}
			}
		}
	}

	/*
	 * identidy (deep) subjects which are non-elided
	 */
	List<Phrase> getNonElidedSubjects() {
		List<Phrase> subjects = new ArrayList<Phrase>();

		for (Phrase p : this.subjects) {
			if (!p.isElided()) {
				subjects.add(p);
			}
		}

		return subjects;
	}

	/*
	 * Compute the surface subjects and object raise complement to subjects and
	 * vice versa. This sets the fields surfaceSubjects and passiveComplement.
	 * 
	 * If this is a WH (who/what/where) question, then: - if it's a subject Wh,
	 * suppress the non-passive subject or the passive complement (logical
	 * subject) - if it's an object Wh, suppress the non-passive object or
	 * passive subject (logical object)
	 */
	protected void computeSurfaceArgs() {
		// initialise the surface subject to empty
		this.surfaceSubject.clearCoordinates();

		// set passive complement to null
		this.passiveComplement = null;

		// passive case first
		if (isPassive()) {

			// case 1: this is interrogative and is WH
			if (this.interrogative && this.interrogType.isWhQuestion()) {
				switch (this.whArgument) {

				// no surface subject
				case SUBJECT:
					buildPassiveComplement(false);
					this.surfaceSubject.addCoordinates(this.verbPhrase
							.getPassiveRaisingComplements());
					break;

				// suppress indirect object
				case INDIRECT_OBJECT:
					this.verbPhrase
							.suppressArg(DiscourseFunction.INDIRECT_OBJECT);
					break;

				// no surface subject
				case OBJECT:
					buildPassiveComplement(true);
					break;
				}
			} else {
				this.surfaceSubject.addCoordinates(this.verbPhrase
						.getPassiveRaisingComplements());
				buildPassiveComplement(true);
			}

			// non-passive case
		} else if (this.interrogative && this.interrogType.isWhQuestion()) {
			switch (this.whArgument) {

			// empty surface subject
			case SUBJECT:
				break;

			// suppress arg otherwise
			case OBJECT:
			case INDIRECT_OBJECT:
				this.verbPhrase.suppressArg(this.whArgument);
				this.surfaceSubject.addCoordinates(this.subjects);
				break;
			}
		} else {
			this.surfaceSubject.addCoordinates(this.subjects);
		}
	}

	/*
	 * Once surface args are computed, we need to deal with the FORM of the
	 * sentence. Example: (1.) to be dead (INFINITIVE) (2.) being dead (GERUND,
	 * -SUBJ) (3.) john's being dead, john's killing a pig (GERUND, +SUBJ) (4.)
	 * be dead (IMPERATIVE).
	 * 
	 * If the form is infinitive, we also shift any front modifiers to the
	 * post-VP position. E.g. 'tomorrow pick up the balls from the shop' + INF
	 * => 'to pick up the balls from the shop tomorrow'
	 */
	protected void computeForm() {

		switch (getForm()) {

		// imperative VP is in second person
		case IMPERATIVE:
			// surfaceSubjects.clear();
			this.surfaceSubject.clearCoordinates();
			break;

		// for GERUND, we set subject NP to possessive unless otherwise
		// specified
		// we suppress the complementiser
		case GERUND:

			if (isSubordinateClause()) {
				suppressComplementiser(true);
			}

			if (this.suppressGenitiveInGerund) {
				break;
			}

			for (Phrase p : this.surfaceSubject.coordinates) {
				if (p instanceof NPPhraseSpec) {
					((NPPhraseSpec) p).setPossessive(true);
				}
			}

			break;

		// for infinitive, we don't have subjects
		// and we set front modifiers to post-vp
		case INFINITIVE:
			this.surfaceSubject.clearCoordinates();

			for (Phrase p : this.frontModifiers) {
				this.verbPhrase.addPostmodifier(p);
			}

			this.frontModifiers.clear();
			break;

		default:
			break;
		}
	}

	/*
	 * Find the NP that verb should agree with and set VP agr simplenlg.features
	 * depends on whether this is a copular sentence and whether subjects is
	 * "there".
	 * 
	 * NB: Rather flakey at the moment
	 */
	protected void computeVPAgreement() {
		Verb head = this.verbPhrase.getHead();
		boolean copular = head == null ? false : head.isCopular();
		boolean expletiveSubj = isExpletiveSubject();
		boolean passive = isPassive();
		List<Phrase> agreeNP = null;

		if (copular && expletiveSubj && !passive) {
			agreeNP = this.verbPhrase.getComplements(DiscourseFunction.OBJECT);
		} else {
			agreeNP = this.surfaceSubject.getCoordinates();
		}

		if (!this.verbPhrase.getForm().equals(Form.IMPERATIVE)) {
			this.verbPhrase.setPerson(getPersonFeature(agreeNP));
		}

		this.verbPhrase.setNumber(getNumberFeature(agreeNP));
		this.verbPhrase.setPerson(getPersonFeature(agreeNP));
	}

	/*
	 * get the Person feature of this sentence (depending on the subjects)
	 */
	protected Person getPersonFeature(List<Phrase> agreeNP) {

		if (agreeNP.size() == 1) {
			Phrase agr = agreeNP.get(0);

			if (agr instanceof NPPhraseSpec) {
				return ((NPPhraseSpec) agr).getPerson();
			}
		}

		return Person.THIRD;
	}

	/*
	 * get the Number feature of this sentence
	 */
	protected NumberAgr getNumberFeature(List<Phrase> agreeNP) {

		if (agreeNP.size() == 1) {
			Phrase agr = agreeNP.get(0);

			if (agr instanceof NPPhraseSpec) {
				return ((NPPhraseSpec) agr).getNumber();
			} else if (agr instanceof StringPhraseSpec
					&& ((StringPhraseSpec) agr).isPlural()) {
				return NumberAgr.PLURAL;
			} else if (agr.isCoordinate()) {
				return NumberAgr.PLURAL;
			}
		}

		else if (agreeNP.size() > 1) {
			return NumberAgr.PLURAL;
		}

		return NumberAgr.SINGULAR;
	}

	/*
	 * Utility method: when a new VP is added, its simplenlg.features of tense,
	 * mood etc are set to be compatible with previous ones.
	 */
	protected VPPhraseSpec fixAllFeatures(VPPhraseSpec newVP) {
		newVP.setForm(getForm());
		newVP.setModal(getModal());
		newVP.setNegated(isNegated());
		newVP.setTense(getTense());
		newVP.setPerfect(isPerfect());
		newVP.setPassive(isPassive());
		newVP.setProgressive(isProgressive());
		return newVP;
	}

	/*
	 * Check whether any of the subjects is expletive "there" this determines
	 * whether subject or complement is the agreement phrase
	 */
	protected boolean isExpletiveSubject() {

		if (this.subjects.size() > 1) {
			return false;
		} else if (this.subjects.size() == 1) {
			Phrase subjectNP = this.subjects.get(0);

			if (subjectNP instanceof NPPhraseSpec) {
				return ((NPPhraseSpec) subjectNP).isExpletive();
			} else if (subjectNP instanceof StringPhraseSpec) {
				return ((StringPhraseSpec) subjectNP).getString().equals(
						"there");
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SENTENCE: " + "[");
		builder.append("Verb = ");

		if (this.verbPhrase != null) {
			Verb head = this.verbPhrase.getHead();
			builder.append(head == null ? "null" : head.getBaseForm());
		}

		builder.append("]");
		return builder.toString();
	}
}
