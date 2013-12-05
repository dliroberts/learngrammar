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
import java.util.Comparator;
import java.util.List;

import simplenlg.features.DiscourseFunction;
import simplenlg.lexicon.lexicalitems.LexicalItem;
import simplenlg.realiser.comparators.PhraseComparator;

// TODO: Auto-generated Javadoc
/**
 * This class is a representation of a headed syntactic phrase. Headed phrases
 * consist of (at least) the following components:
 * 
 * <OL>
 * <LI>Minimally, a lexical head, usually a content word such as a noun or verb
 * (as opposed to a function word like a determiner). For example, <i> kick </i>
 * is a minimal verb phrase (one consisting only of a lexical head). *</LI>
 * <LI>Zero or more complements, which are themselves phrases. For example:
 * <i>kick <strong>the ball</strong></i>, where the complement is a noun phrase.
 * It is possible to set the order in which complements are realised; see
 * {@link #setComplementOrder(Comparator) };</LI>
 * <LI>Zero or more premodifiers, themselves phrases. For example:
 * <i><strong>slowly</strong> kick the ball</i> It is possible to set the order
 * in which premodifers are realised; see
 * {@link #setPremodifierOrder(Comparator)};</LI>
 * <LI>Zero or more postmodifiers, themselves phrases For example: <i>slowly
 * kick the ball <strong>in the field</strong></i>. It is possible to set the
 * order in which postmodifers are realised; see
 * {@link #setPostmodifierOrder(Comparator)};</LI>
 * </OL>
 * 
 * <P>
 * These components are usually realised in this order: PREMODIFIER >> HEAD >>
 * COMPLEMENT >> POSTMODIFIER.
 * 
 * <P>
 * <strong>Note</strong>: Getters and setters for complements and modifiers are
 * provided in this class and inherited or overridden in classes extending
 * <code>HeadedPhraseSpec</code>. Unless otherwise specified, complements and
 * modifier phrases set using these methods <i>must</i> be either
 * <code>String</code>s or {@link simplenlg.realiser.Phrase}s. No other
 * restriction is placed on what can occur in a <code>HeadedPhraseSpec</code>.
 * 
 * <P>
 * The category of a headed phrase is determined by the category of its lexical
 * head. Thus, the method {@link #getCategory()}, inherited from
 * <code>Phrase</code>, will always return the category of the head of a
 * <code>HeadedPhraseSpec</code>. For the same reason, the
 * <code>HeadedPhraseSpec</code> class is parametrised, the parameter being the
 * class of {@link simplenlg.lexicon.lexicalitems.LexicalItem} that heads the
 * phrase. For example, the {@link simplenlg.realiser.NPPhraseSpec} class
 * extends <code>HeadedPhraseSpec&lt;Noun&gt;></code>
 * 
 * <P>
 * In the <code>simplenlg.realiser</code> api, this abstract class is extended
 * by phrases representing all the major lexical categories listed in
 * {@link simplenlg.features.Category}. Many of these classes specify other
 * simplenlg.features for headed phrases. For example, a noun phrase also allows
 * a specifier (usually, but not exclusively, a determiner). Each of these
 * classes is in turn extended by its corresponding coordinate class, which
 * implements the {@link simplenlg.realiser.CoordinatePhrase} interface.
 * 
 * @param <T>
 *            The class of the lexical head of the <code>HeadedPhrase</code>, a
 *            subclass of {@link simplenlg.lexicon.lexicalitems.LexicalItem}.
 */
public abstract class HeadedPhraseSpec<T extends LexicalItem> extends
		PhraseSpec {

	/** The head. */
	protected T head;

	/** The complements. */
	protected List<Phrase> complements;

	/** The premodifiers. */
	protected List<Phrase> premodifiers;

	/** The postmodifiers. */
	protected List<Phrase> postmodifiers;

	protected List<Phrase> attributiveModifiers;

	/** The postmodifier comparator. */
	Comparator<Phrase> complementComparator, premodifierComparator,
			postmodifierComparator;

	/**
	 * Constructs an empty headed phrase.
	 */
	public HeadedPhraseSpec() {
		super();
		this.head = null;
		this.complements = new ArrayList<Phrase>();
		this.premodifiers = new ArrayList<Phrase>();
		this.postmodifiers = new ArrayList<Phrase>();
		this.attributiveModifiers = new ArrayList<Phrase>();
		this.category = null;
		this.complementComparator = PhraseComparator.defaultInstance();
		this.premodifierComparator = null;
		this.postmodifierComparator = PhraseComparator.defaultInstance();
	}

	/**
	 * Constructs a headed phrase with a specified head.
	 * 
	 * @param head
	 *            The head of this phrase.
	 */
	public HeadedPhraseSpec(T head) {
		this();
		this.head = head;
	}

	/**
	 * Gets the head.
	 * 
	 * @return The head of a phrase if specified, null otherwise.
	 */
	public T getHead() {
		return this.head;
	}

	/**
	 * Sets the head of the phrase, replacing any existing head.
	 * 
	 * @param head
	 *            The new head.
	 */
	public void setHead(T head) {
		this.head = head;
	}

	/**
	 * Gets the head as string.
	 * 
	 * @return A string representation of the head of the phrase (the baseform
	 *         of the lexical item), if a head is specified, null otherwise.
	 */
	public String getHeadAsString() {
		return this.head == null ? null : this.head.getBaseForm();
	}

	/**
	 * Given a <code>java.util.Comparator&lt;Phrase&gt;</code>, this will
	 * determine the order in which complements are realised.
	 * 
	 * With the simplenlg.exception of {@link simplenlg.realiser.VPPhraseSpec},
	 * all headed phrases just order complements in the order given. There is
	 * therefore no need to pass an ordering function.
	 * 
	 * @param comp
	 *            The comparator
	 */
	public void setComplementOrder(Comparator<Phrase> comp) {
		this.complementComparator = comp;
	}

	/**
	 * Given a <code>java.util.Comparator&lt;Phrase&gt;</code>, this will
	 * determine the order in which premodifiers are realised.
	 * 
	 * All headed phrases just order premodifiers in the order given. There is
	 * therefore no need to pass an ordering function.
	 * 
	 * <P>
	 * Conceivable uses of a premodifier comparator include the linearisation of
	 * prenominal adjectives in a noun phrase, where, say, the ordering <i>
	 * large red candle</i> is considered by most speakers as better to <i> red,
	 * large candle</i>, thus suggesting a semantic ordering relation.
	 * 
	 * @param comp
	 *            The comparator
	 */
	public void setPremodifierOrder(Comparator<Phrase> comp) {
		this.premodifierComparator = comp;
	}

	/**
	 * Given a <code>java.util.Comparator&lt;Phrase&gt;</code>, this will
	 * determine the order in which postmodifiers are realised.
	 * 
	 * All headed phrases just order postmodifiers in the order given. There is
	 * therefore no need to pass an ordering function.
	 * 
	 * @param comp
	 *            The comparator
	 */
	public void setPostmodifierOrder(Comparator<Phrase> comp) {
		this.postmodifierComparator = comp;
	}

	/**
	 * Sets the complement of this phrase, replacing any existing complements.
	 * 
	 * @param comp
	 *            The new complement.
	 */
	public void setComplement(Object comp) {
		this.complements.clear();
		addComplement(comp);
	}

	/**
	 * Adds a complement to this phrase, in addition to existing complements.
	 * 
	 * @param comp
	 *            The complement to add.
	 */
	public void addComplement(Object comp) {
		Phrase complement = makeConstituent(comp, DiscourseFunction.OBJECT);
		this.complements.add(complement);
	}

	/**
	 * Sets the premodifier to this phrase, replacing existing premodifiers.
	 * 
	 * @param mod
	 *            The new premodifier
	 */
	public void setPremodifier(Object mod) {
		this.premodifiers.clear();
		addPremodifier(mod);
	}

	/**
	 * Adds a premodifier to this phrase, in addition to existing premodifiers.
	 * 
	 * @param mod
	 *            The premodifier to add.
	 */
	public void addPremodifier(Object mod) {
		this.premodifiers.add(makeConstituent(mod,
				DiscourseFunction.PREMODIFIER));
	}

	/**
	 * Sets the postmodifier to this phrase, replacing existing postmodifiers.
	 * 
	 * @param mod
	 *            The new postmodifier
	 */
	public void setPostmodifier(Object mod) {
		this.postmodifiers.clear();
		addPostmodifier(mod);
	}

	/**
	 * Adds a postmodifier to this phrase, in addition to existing
	 * postmodifiers.
	 * 
	 * @param mod
	 *            The postmodifier to add.
	 */
	public void addPostmodifier(Object mod) {
		this.postmodifiers.add(makeConstituent(mod,
				DiscourseFunction.POSTMODIFIER));
	}

	/**
	 * Add an attributive postmodifier. This is typically realised after the
	 * full headed phrase, in a parenthetical construction involving commas.
	 * E.g. <I>the tall man, <U>who was rather ugly,</U>...</I>.
	 * 
	 * @param mod
	 *            the modifier
	 */
	public void addAttributivePostmodifier(Object mod) {
		this.attributiveModifiers.add(makeConstituent(mod,
				DiscourseFunction.POSTMODIFIER));
	}

	/**
	 * Checks for complements.
	 * 
	 * @return <code>true</code> if this phrase has specified complements,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasComplements() {
		return !this.complements.isEmpty();
	}

	/**
	 * Checks for premodifiers.
	 * 
	 * @return <code>true</code> if this phrase has specified premodifiers,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasPremodifiers() {
		return !this.premodifiers.isEmpty();
	}

	/**
	 * Checks for postmodifiers.
	 * 
	 * @return <code>true</code> if this phrase has specified postmodifiers,
	 *         <code>false</code> otherwise.
	 */
	public boolean hasPostmodifiers() {
		return !this.postmodifiers.isEmpty();
	}

	/**
	 * Check for attributive postmodifiers
	 * 
	 * @return <code>true</code> if this phrase has attribtue postmodifiers
	 *         specified.
	 */
	public boolean hasAttributivePostmodifiers() {
		return !this.attributiveModifiers.isEmpty();
	}

	/**
	 * Gets the complements.
	 * 
	 * @return The complements of this phrase, in the order in which they were
	 *         added. An empty list is returned if there are none.
	 */
	public List<Phrase> getComplements() {
		return this.complements;
	}

	/**
	 * Gets the premodifiers.
	 * 
	 * @return The premodifiers of this phrase, in the order in which they were
	 *         added. An empty list is returned if there are none.
	 */
	public List<Phrase> getPremodifiers() {
		return this.premodifiers;
	}

	/**
	 * Gets the postmodifiers.
	 * 
	 * @return The postmodifiers of this phrase, in the order in which they were
	 *         added. An empty list is returned if there are none.
	 */
	public List<Phrase> getPostmodifiers() {
		return this.postmodifiers;
	}

	/**
	 * Get the attributive postmodifiers defined for this phrase
	 * 
	 * @return the attributive postmodifiers, if any have been defined.
	 */
	public List<Phrase> getAttributivePostmodifiers() {
		return this.attributiveModifiers;
	}

	/**
	 * Adds a modifier, trying to guess whether it is a pre- or a post-modifier.
	 * The decision is carried out as follows:
	 * 
	 * <UL>
	 * <LI>If the modifier is a string with no whitespace, then it is assumed to
	 * be a premodifier, and is converted to a
	 * {@link simplenlg.realiser.StringPhraseSpec} and added to the premodifiers
	 * of this phrase.</LI>
	 * <LI>If the modifier is a phrase, then it is a postmodifier</LI>
	 * </UL>
	 * 
	 * <P>
	 * <strong>Note:</strong> This method is in experimental stage and may be
	 * improved in future releases.
	 * </P>
	 * 
	 * @param modifier
	 *            The new modifier, a {@link simplenlg.realiser.Phrase} or
	 *            <code>String</code>.
	 */
	public void addModifier(Object modifier) {

		if (modifier instanceof String) {

			if (!((String) modifier).contains(" ")) {
				addPremodifier(modifier);
			} else {
				addPostmodifier(modifier);
			}
		} else {
			addPostmodifier(modifier);
		}
	}

	/**
	 * Checks for head.
	 * 
	 * @return <code>true</code> if a lexical head has been specified for this
	 *         phrase, <code>false</code> otherwise.
	 */
	public boolean hasHead() {
		return this.head != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.PhraseSpec#promote(simplenlg.realiser.DocStructure)
	 */
	@Override
	public TextSpec promote(DocStructure level) {
		return new TextSpec(level, this);
	}

	/**
	 * Checks for equality between a phrase and another object.
	 * 
	 * @param o
	 *            The object to be compared to this one
	 * 
	 * @return <code>true</code> if, and only if:
	 *         <OL>
	 *         <LI> <code>o</code> is a <code>HeadedPhrase</code> of the same
	 *         type as this phrase
	 *         <LI> <code>o</code> has the same head as this phrase
	 *         <LI> <code>o</code> has the same list of complements as this
	 *         phrase, in the same order
	 *         <LI> <code>o</code> has the same list of premodifiers as this
	 *         phrase, in the same order
	 *         <LI> <code>o</code> has the same list of postmodifiers as this
	 *         phrase, in the same order
	 *         </OL>
	 */
	@Override
	public boolean equals(Object o) {

		if (o == null) {
			return false;
		}

		try {
			HeadedPhraseSpec<?> hps = (HeadedPhraseSpec<?>) o;
			return sameHead(hps)
					&& hps.premodifiers.equals(this.premodifiers)
					&& hps.complements.equals(this.complements)
					&& hps.postmodifiers.equals(this.postmodifiers)
					&& hps.attributiveModifiers
							.equals(this.attributiveModifiers);
			
		} catch (ClassCastException cce) {
			return false;
		}
	}

	// ******************************************************************
	// REALISATION:
	// this method is split up into components which can be overridden
	// individually
	// *******************************************************************
	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Spec#realise(simplenlg.realiser.Realiser)
	 */
	@Override
	String realise(Realiser r) {
		return r.appendSpace(realisePremodifier(r), realiseHead(r),
				realiseComplement(r), realisePostmodifier(r));
	}

	/**
	 * Realise premodifier.
	 * 
	 * @param r
	 *            the r
	 * 
	 * @return the string
	 */
	String realisePremodifier(Realiser r) {

		if (this.premodifierComparator != null) {
			Collections.sort(this.premodifiers, this.premodifierComparator);
		}

		String pre = r.realiseList(this.premodifiers);

		return pre == null ? "" : pre;
	}

	/**
	 * Realise postmodifier.
	 * 
	 * @param r
	 *            the r
	 * 
	 * @return the string
	 */
	String realisePostmodifier(Realiser r) {

		if (this.postmodifierComparator != null) {
			Collections.sort(this.postmodifiers, this.postmodifierComparator);
			Collections.sort(this.attributiveModifiers,
					this.postmodifierComparator);
		}

		String post = r.realiseList(this.postmodifiers);
		String post2 = r.realiseList(this.attributiveModifiers);

		if (post2 != null && post2.length() > 0) {
			StringBuilder builder = (post == null ? new StringBuilder()
					: new StringBuilder(post));
			post = builder.append(", ").append(post2).append(", ").toString();
		}

		return post == null ? "" : post;
	}

	/**
	 * Realise complement.
	 * 
	 * @param r
	 *            the r
	 * 
	 * @return the string
	 */
	String realiseComplement(Realiser r) {
		String comp = r.realiseAndList(this.complements);
		return comp == null ? "" : comp;
	}

	/**
	 * Realise head.
	 * 
	 * @param r
	 *            the r
	 * 
	 * @return the string
	 */
	String realiseHead(Realiser r) {
		return this.head == null ? "" : this.head.getBaseForm();
	}

	// *********************************************************
	// UTILITY METHODS
	// *********************************************************

	/**
	 * Clear complements.
	 */
	void clearComplements() {
		this.complements.clear();
	}

	// TODO: Might make this public
	/**
	 * Same head.
	 * 
	 * @param hps
	 *            the hps
	 * 
	 * @return true, if successful
	 */
	boolean sameHead(HeadedPhraseSpec<?> hps) {

		if (this.head == hps.head) {
			return true;
		} else if (this.head == null || hps.head == null) {
			return false;
		} else {
			return this.head.equals(hps.head);
		}
	}

	// *********************************************************
	// ABSTRACT METHODS
	// *********************************************************
	/**
	 * Sets the head of this phrase, given its baseform.
	 * 
	 * @param head
	 *            The baseform of the head.
	 */
	abstract void setHead(String head);

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Phrase: " + this.category + "[");
		builder.append(this.head == null ? null : this.head.getBaseForm());
		builder.append("]");
		return builder.toString();
	}
}
