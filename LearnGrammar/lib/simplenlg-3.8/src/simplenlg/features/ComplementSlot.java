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

import simplenlg.exception.LexiconException;
import simplenlg.lexicon.lexicalitems.LexicalItem;

/**
 * A complement slot represents a cluster of grammatical features corresponding
 * to a single syntactic phrase in a complement frame. It is the principal
 * element making up a {@link ComplementFrame}. It is made up of three main
 * elements:
 * 
 * <OL>
 * <LI>The <B>type</B>: An value of {@link simplenlg.features.ComplementType},
 * representing the kind of complement (e.g. whether it's an NP, a clause, a
 * bare infinitive clause, etc). For example, the verb <I>whistle</I> allows a
 * prepositional complement (<I>Pete whistled <U>to the dog</U></I>); therefore
 * one of its complement frames has a slot with <code>PP</code> as its type.
 * Similarly, it also has a frame with a single slot of type <code>NP</code>,
 * covering cases such as <I>John whistled <U>a tune</U></I>, where the
 * complement is an NP.<BR>
 * Every slot must have a type.</LI>
 * 
 * <LI>an <B>Interpretation code</B>: this is an element of the
 * {@link simplenlg.features.InterpretationCode} enumerated type and specifies
 * the control properties of the verb-complement relation (such as whether it's
 * a subject-raising complement). This feature applies only to complements which
 * are clausal.<BR>
 * This feature is optional, and may be <code>null</code>.</LI>
 * 
 * <LI>a <B>head restriction</B>: this is a
 * {@link simplenlg.lexicon.lexicalitems.LexicalItem} which restricts the
 * complement to always having the same lexical head. This is useful, for
 * example, to represent the fact that the phrasal verb <I>give in</I> can have
 * a prepositional phrase complement, as in <I>give in <U>to pressure</U></I>
 * which is always headed by the preposition <I>to</I>. This feature is
 * optional, and may be <code>null</code>.</LI>
 * </OL>
 * 
 * @see simplenlg.features.ComplementFrame
 * @see simplenlg.features.ComplementType
 * @see simplenlg.features.InterpretationCode
 * 
 * @author agatt
 * @since Version 3.7
 */
public class ComplementSlot {

	// type of phrase
	private ComplementType type;

	// interpretation code
	private InterpretationCode code;

	// head restriction
	private LexicalItem headRestriction;

	/**
	 * Default constructor for a complementation frame slot -- not public.
	 */
	ComplementSlot() {
		;// does nothing
	}

	/**
	 * Instantiates a ComplementSlot with the given type.
	 * 
	 * @param t
	 *            The type
	 */
	public ComplementSlot(ComplementType t) {
		this();
		setType(t);
	}

	/**
	 * Instantiates a new complement slot with a type, an interpretation code
	 * and a lexical item. The lexical item restricts the complement represented
	 * by the slot to always have the same lexical head.
	 * 
	 * @param t
	 *            the type
	 * @param c
	 *            the code, if any
	 * @param r
	 *            the restriction, if any
	 */
	public ComplementSlot(ComplementType t, InterpretationCode c, LexicalItem r) {
		this(t);
		setCode(c);
		setHeadRestriction(r);
	}

	/**
	 * Get the type of this slot.
	 * 
	 * @return The type
	 * 
	 */
	public ComplementType getType() {
		return this.type;
	}

	/**
	 * Sets the type of this slot. If the slot has its interpretation code set,
	 * and the new type is not compatible with the interpretation code, an
	 * exception is thrown.
	 * 
	 * @param type
	 *            The new type
	 * 
	 * @throws LexiconException
	 *             in case the new type is not compatible with the
	 *             interpretation code (if one exists) of this slot.
	 */
	public void setType(ComplementType type) throws LexiconException {

		if (this.code != null) {
			if (this.code.compatibleWith(type)) {
				this.type = type;
				return;

			} else {
				throw new LexiconException("The interpretation code "
						+ this.code
						+ " is incompatible with the complement type " + type);
			}
		}

		this.type = type;
	}

	/**
	 * Gets the interpretation code.
	 * 
	 * @return the interpretation code, if one has been set, <code>null</code>
	 *         otherwise.
	 * 
	 * @see simplenlg.features.InterpretationCode
	 */
	public InterpretationCode getCode() {
		return this.code;
	}

	/**
	 * Sets the interpretation code.
	 * 
	 * @param code
	 *            The new code
	 * 
	 */
	public void setCode(InterpretationCode code) {

		if (code == null) {
			return;
		}

		if (code.compatibleWith(this.type)) {
			this.code = code;
		} else {
			throw new LexiconException("The interpretation code " + code
					+ " is incompatible with the complement type " + this.type);
		}
	}

	/**
	 * Gets the lexical item restricting the head of the complement represented
	 * by this slot.
	 * 
	 * @return The head restriction, if one has been specified,
	 *         <code>null</code> otherwise.
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem
	 */
	public LexicalItem getHeadRestriction() {
		return this.headRestriction;
	}

	/**
	 * Set the lexical item restricting the head of the complement represented
	 * by this slot.
	 * 
	 * @param headRestriction
	 *            the lexical item
	 */
	public void setHeadRestriction(LexicalItem headRestriction) {
		this.headRestriction = headRestriction;
	}

	/**
	 * Check whether this slot has a head restriction.
	 * 
	 * @return <code>true</code> if a head restriction has been set in this
	 *         slot.
	 */
	boolean hasHeadRestriction() {
		return this.headRestriction != null;
	}

	/**
	 * Check whether this slot has an interpretation code.
	 * 
	 * @return <code>true</code> if an interpretation code has been set in this
	 *         slot.
	 */
	boolean hasInterpretationCode() {
		return this.code != null;
	}

}