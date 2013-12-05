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

import java.util.ArrayList;
import java.util.List;

import simplenlg.lexicon.lexicalitems.LexicalItem;

/**
 * <P>
 * This class implements a Complementation Frame, an abstract, template-like
 * representation of the complement(s) that a lexical item can have. The frame
 * represents the grammatical properties of the complements of a lexical item.
 * Lexical items can have more than one such frame, representing the fact that
 * they occur with several (grammatically distinct) complementation patterns.
 * For example, the verb <I>show</I> allows the following among others, where
 * the two are syntactically distinct, and the second allows dative movement
 * (<I>he showed John the book</I>) where the first does not:
 * </P>
 * 
 * <OL>
 * <LI>he showed <I>John to the door</I> (NP + prepositional sentential adverb)</LI>
 * <LI>he showed <I>the book to John</I> (NP + to-pp)
 * </OL>
 * 
 * <P>
 * It is not only verbs that allow complements, as the following examples show:
 * </P>
 * 
 * <OL>
 * <LI><I>John's <U>right</U> <U>to remain silent</U></I> (noun + infinitive VP
 * complement)</LI>
 * <LI><I>John is <U>easy</U> <U>to please</U></I> (adjective + infinitive VP
 * complement)</LI>
 * </OL>
 * 
 * <P>
 * Complementation frames are not syntactic structures, but specify what kinds
 * of syntactic structures heads of phrases may allow. Only lexical items whose
 * class extends {@link simplenlg.lexicon.lexicalitems.ContentWord} allows
 * complements.
 * </P>
 * 
 * <P>
 * A complementation frame consists of:
 * </P>
 * 
 * <OL>
 * <LI>One or more {@link simplenlg.features.ComplementSlot}. Each such slot
 * corresponds to a syntactic phrase. If a complementation frame has more than
 * one slot, then it corresponds to a structure in which the lexical item has
 * more than one complement occurring consecutively. For example, to represent
 * the ditransitive nature of <I>give</I>, a complementation frame has two
 * slots, one corresponding to the direct object, one to the indirect object.
 * The order of slots is important, as it should reflect the order in which they
 * are realised.</LI>
 * <LI>A feature indicating the transitivity value of the complement represented
 * by the frame. This is a value of {@link simplenlg.features.Transitivity}.</LI>
 * 
 * <LI>A number of boolean features which represent whether the frame:
 * <OL>
 * <LI>allows dative shift: <code>true</code> if the complements in this frame
 * permit dative movement (to generate, for example, <I>John gave Mary a
 * shoe</I> from <I>John gave a shoe to Mary</I></LI>;
 * 
 * <LI>allows passive raising: <code>true</code> if the object complement in
 * this frame can be raised to subject position in a passive construction. This
 * is not always the case, as witness <I>John had a coffee</I> vs. <I>*a coffee
 * was had by John</I>.</LI>;
 * 
 * <LI>has a clausal complement: <code>true</code> if this frame contains at
 * least one complement slot which is clausal. For example, the complement frame
 * representing such structures as <I>Bill said <U>that he would go to the
 * farm</U></I> would have this value set to <code>true</code>;</LI>
 * 
 * <LI>has an infinitive complement: <code>true</code> if (3) above is
 * <code>true</code> and at least one of the clausal complements is infinitive,
 * e.g. <I>John advised us <U>to go to the farm</U></I>;</LI>
 * 
 * <LI>has a bare infinitive complement: <code>true</code> if (3) above is
 * <code>true</code> and, moreover, at least one of the infinitive clausal
 * complements is bare (can occur without <I>to</I>), as in <I>John helped
 * <U>write the program</U></I>;</LI>
 * 
 * <LI>has a wh-clausal complement: <code>true</code> if (3) above is
 * <code>true</code> and, moreover, at least one of the clausal complements is a
 * WH-clause, e.g. <I>John asked <U>who the strange lady was</U></I>;</LI>
 * 
 * <LI>has a finite clause complement: <code>true</code> if (3) above is
 * <code>true</code> and, moreover, at least one of the clausal complements is
 * finite.</LI>
 * </OL>
 * </LI>
 * </OL>
 * 
 * 
 * <P>
 * Note that, since ComplementationFrames represent a <B>series</B> of
 * complements, several of the above may be true at the same time.
 * </P>
 * 
 * @see simplenlg.features.ComplementSlot
 * @see simplenlg.features.ComplementType
 * 
 * @author agatt
 * @since Version 3.7
 */
public class ComplementFrame implements Feature {

	// boolean features
	private boolean allowsDativeShift, allowsPassive;

	// transitivity value
	private Transitivity transitivity;

	// actual frame contents
	private List<ComplementSlot> contents;

	/**
	 * Instantiates a new (empty) complementation frame.
	 */
	public ComplementFrame() {
		this.contents = new ArrayList<ComplementSlot>();
		this.allowsDativeShift = false;
		this.allowsPassive = true;
		this.transitivity = null;
	}

	/**
	 * Returns an ordered list of complement slots representing the phrases in
	 * this complement frame.
	 * 
	 * @return the complement slots
	 */
	public List<ComplementSlot> getComplementSlots() {
		return this.contents;
	}

	/**
	 * Gets the transitivity value, i.e. whether this frame represents a
	 * monotransitive, ditransitive, etc use of the lexical item.
	 * 
	 * @return the transitivity
	 */
	public Transitivity getTransitivity() {
		return this.transitivity;
	}

	/**
	 * Sets the transitivity value of this complement frame.
	 * 
	 * @param transitivity
	 *            the new transitivity
	 */
	public void setTransitivity(Transitivity transitivity) {
		this.transitivity = transitivity;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean appliesTo(Category c) {
		return c.equals(Category.VERB);
	}

	/**
	 * Checks whether this complement frame is one that allows dative shift.
	 * This shift is exemplified by <I>John gave <U>the mouse to Peter</U></I>,
	 * where <I>give</I> permits the dative shift to yield <I>John gave <U>Peter
	 * the mouse</U></I>.
	 * 
	 * @return <code>true</code> if the dative shift is allowed
	 */
	public boolean allowsDativeShift() {
		return this.allowsDativeShift;
	}

	/**
	 * Sets whether this frame allows the dative shift.
	 * 
	 * @param allowsDativeShift
	 *            Sets whether dative shift is allowed
	 */
	public void setAllowsDativeShift(boolean allowsDativeShift) {
		this.allowsDativeShift = allowsDativeShift;
	}

	/**
	 * Sets whether this frame allows passive raising of object to subject
	 * position. This is disallowed with some verbs, for example, <I>have</I> in
	 * <I>John had <U>a nice cup</U></I> does not allow passive raising of
	 * <I>nice</I> cup (cf. <I>*a nice cup was had by John</I>
	 * 
	 * @return <code>true</code>, if the frame allowed passive raising
	 */
	public boolean allowsPassive() {
		return this.allowsPassive;
	}

	/**
	 * Sets whether this frame allows passive raising.
	 * 
	 * @param allowsPassive
	 *            whether this frame allows passive
	 */
	public void setAllowsPassive(boolean allowsPassive) {
		this.allowsPassive = allowsPassive;
	}

	/**
	 * Adds a complement slot to this frame, specifying the type, interpretation
	 * code and a lexical item. The latter two can be null. Lexical items are
	 * passed as parameters just in case a complement can only be realised with
	 * a particular head. For example, a PP complement with a given verb may be
	 * restricted to always require the head <I>with</I>.
	 * 
	 * <P>
	 * Complements are held in a complement frame in the order in which they are
	 * added. When a complement is added, it is internally represented as a
	 * {@link simplenlg.features.ComplementSlot}.
	 * </P>
	 * 
	 * @param type
	 *            the ComplementType
	 * @param code
	 *            the interpretation code (possibly null)
	 * @param restriction
	 *            the head lexical item (possibly null)
	 * 
	 * @see simplenlg.features.ComplementSlot
	 */
	public void addComplement(ComplementType type, InterpretationCode code,
			LexicalItem restriction) {
		addComplement(new ComplementSlot(type, code, restriction));
	}

	/**
	 * Adds a complement template to this frame directly in the form of a
	 * ComplementSlot.
	 * 
	 * @param slot
	 *            The slot representing the features of the complement in this
	 *            frame
	 */
	public void addComplement(ComplementSlot slot) {
		this.contents.add(slot);
	}

}
