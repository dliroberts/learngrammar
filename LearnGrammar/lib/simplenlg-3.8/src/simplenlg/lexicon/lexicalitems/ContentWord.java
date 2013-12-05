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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import simplenlg.features.ComplementFrame;
import simplenlg.features.ComplementSlot;
import simplenlg.features.ComplementType;

/**
 * Class implementing content words. This class is used to represent lexical
 * items belonging to the "open" grammatical classes (nouns, verbs, adjectives,
 * adverbs).
 * 
 * <P>
 * In addition to the features inherited from class <code>Word</code>, content
 * words can have an arbitray number of
 * {@link simplenlg.features.ComplementFrame}s. Utility methods are also
 * provided to check whether a particular content word can take complements of
 * the various types defined in {@link simplenlg.features.ComplementType}.
 */
public abstract class ContentWord extends Word {

	/** The complementation frames. */
	List<ComplementFrame> complementationFrames;

	// flags for clause types, for easy querying
	boolean whfin, whinf, inf, binf, fin, as, ed, ing, np, adj, adv, pp;

	/*
	 * Implicit constructor: Instantiates a new content word.
	 */
	ContentWord() {
		super();
		this.inflectionType = null;
		this.complementationFrames = new ArrayList<ComplementFrame>();
		// derivations = new HashMap<LexicalRelation, List<LexicalItem>>();
	}

	/**
	 * Constructs a new instance of ContentWord with the given baseform.
	 * 
	 * @param baseform
	 *            The baseform of this lexical item (a word)
	 */
	public ContentWord(String baseform) {
		this();
		this.baseForm = baseform;
	}

	/**
	 * Constructs a new instance of ContentWord, with a baseform and a unique
	 * ID.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 */
	public ContentWord(String id, String baseform) {
		this(baseform);
		setID(id);
	}

	/**
	 * Constructs a new instance of <code>ContentWord</code>, with the given id,
	 * baseform and citation form.
	 * 
	 * @param id
	 *            A unique identifier
	 * @param baseform
	 *            The baseform
	 * @param citationform
	 *            The citation form (possibly different from the baseform)
	 */
	public ContentWord(String id, String baseform, String citationform) {
		this(id, baseform);
		setCitationForm(citationform);
	}

	/**
	 * Gets the complementation frames.
	 * 
	 * @return the complementation frames, if any are defined, the empty list
	 *         otherwise
	 */
	public Collection<ComplementFrame> getComplementationFrames() {
		return this.complementationFrames;
	}

	/**
	 * Sets the complementation frames. This removes any complementation frames
	 * that may have been previously set, replacing them with the new ones.
	 * 
	 * @param compFrames
	 *            the new complementation frames
	 */
	public void setComplementationFrames(Collection<ComplementFrame> compFrames) {
		this.complementationFrames.clear();

		for (ComplementFrame frame : compFrames) {
			addComplementFrame(frame);
		}
	}

	/**
	 * Checks for complementation frames
	 * 
	 * @return <code>true</code>, if any frames have been set for this lexical
	 *         item
	 */
	public boolean hasComplementationFrame() {
		return !this.complementationFrames.isEmpty();
	}

	/**
	 * Adds a complement frame to this content word.
	 * 
	 * @param complementFrame
	 *            the new complement frame
	 */
	public void addComplementFrame(ComplementFrame complementFrame) {
		this.complementationFrames.add(complementFrame);

		for (ComplementSlot slot : complementFrame.getComplementSlots()) {
			updateFeatures(slot.getType());
		}
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of a clause, or contains a clause in addition to other phrase types.
	 * Clausal complements including all infinitive, bare infinitive, finite and
	 * wh- clauses.
	 * 
	 * @return true, if a clausal complement is possible with this content word
	 * @see simplenlg.features.ComplementType#BARE_INF
	 * @see simplenlg.features.ComplementType#INF
	 * @see simplenlg.features.ComplementType#WHFINCOMP
	 * @see simplenlg.features.ComplementType#WHINFCOMP
	 * @see simplenlg.features.ComplementType#FINCOMP
	 */
	public boolean allowsClauseComp() {
		return this.whfin || this.whinf || this.inf || this.binf || this.fin;
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of a WH-Clause, or contains a WH-Clause in addition to other phrase
	 * types. A WH-clause complement can be finite or non-finite.
	 * 
	 * @return true, if a WH-clausal complement is possible with this content
	 *         word
	 * @see simplenlg.features.ComplementType#WHFINCOMP
	 * @see simplenlg.features.ComplementType#WHINFCOMP
	 */
	public boolean allowsWhComp() {
		return this.whfin || this.whinf;
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of a finite clause, or contains a finite clause in addition to other
	 * phrase types. Finite clauses can be WH- or non-WH.
	 * 
	 * @return true, if a finite clausal complement is possible with this
	 *         content word
	 * @see simplenlg.features.ComplementType#FINCOMP
	 * @see simplenlg.features.ComplementType#WHFINCOMP
	 */
	public boolean allowsFinComp() {
		return this.fin || this.whfin;
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of an infinitival clause, or contains an infinitival clause in addition
	 * to other phrase types. Note that an infinitival can be either a "full"
	 * infinitive (<I>he allowed us <U>to see his garden</U></I>), a bare
	 * infinitive (<I>he helped us <U>cook a meal</U></I>) or a wh-infinitive
	 * (<I>he asked <U>whether to send the money</U></I>).
	 * 
	 * @return true, if an infinitival clausal complement is possible with this
	 *         content word
	 * @see simplenlg.features.ComplementType#INF
	 * @see simplenlg.features.ComplementType#BARE_INF
	 * @see simplenlg.features.ComplementType#WHINFCOMP
	 */
	public boolean allowsInfComp() {
		return this.inf || this.binf || this.whinf;
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of a bare infinitive, or contains a bare infinitive in addition to other
	 * phrase types.
	 * 
	 * @return true, if a bare infinitive complement is possible with this
	 *         content word
	 * @see simplenlg.features.ComplementType#BARE_INF
	 */
	public boolean allowsBareInfComp() {
		return this.binf;
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of an as-complement, or contains an as-complement in addition to other
	 * phrase types.
	 * 
	 * @return true, if an as-complement is possible with this content word
	 * @see simplenlg.features.ComplementType#AS_COMP
	 */
	public boolean allowsAsComp() {
		return this.as;
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of a noun phrase, or contains a noun phrase in addition to other phrase
	 * types.
	 * 
	 * @return true, if a noun phrase is possible with this content word
	 * @see simplenlg.features.ComplementType#NP
	 */
	public boolean allowsNPComp() {
		return this.np;
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of an adverbial phrase, or contains an adverbial phrase in addition to
	 * other phrase types.
	 * 
	 * @return true, if an adverbial phrase is possible with this content word
	 * @see simplenlg.features.ComplementType#ADVP
	 */
	public boolean allowsAdvComp() {
		return this.adv;
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of an adjectival phrase, or contains an adjectival phrase in addition to
	 * other phrase types.
	 * 
	 * @return true, if an adjectival phrase is possible with this content word
	 * @see simplenlg.features.ComplementType#ADJP
	 */
	public boolean allowsAdjComp() {
		return this.adj;
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of a prepositional phrase, or contains a prepositional phrase in addition
	 * to other phrase types.
	 * 
	 * @return true, if a prepositional phrase is possible with this content
	 *         word
	 * @see simplenlg.features.ComplementType#PP
	 */
	public boolean allowsPPComp() {
		return this.pp;
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of a past participle verb phrase, or contains of a past participle verb
	 * phrase in addition to other phrase types.
	 * 
	 * @return true, if a past participle verb phrase is possible with this
	 *         content word
	 * @see simplenlg.features.ComplementType#ED
	 */
	public boolean allowsEdComp() {
		return this.ed;
	}

	/**
	 * Checks whether at least one complementation frame of this word consists
	 * of a gerundive or present participle verb phrase, or contains this phrase
	 * type in addition to other phrase types.
	 * 
	 * @return true, if a gerundive or present participle verb phrase is
	 *         possible with this content word
	 * @see simplenlg.features.ComplementType#ING
	 */
	public boolean allowsIngComp() {
		return this.ing;
	}

	// ***********************************************************************************
	// PRIVATE METHODS
	// ***********************************************************************************

	/*
	 * Updates some flags for easy query of complement type.
	 */
	private void updateFeatures(ComplementType type) {

		switch (type) {
		case INF:
			this.inf = true;
			break;

		case BARE_INF:
			this.binf = true;
			break;

		case ED:
			this.ed = true;
			break;

		case ING:
			this.ing = true;
			break;

		case FINCOMP:
			this.fin = true;
			break;

		case WHFINCOMP:
			this.whfin = true;
			break;

		case WHINFCOMP:
			this.whinf = true;
			break;

		case AS_COMP:
			this.as = true;
			break;

		case PP:
			this.pp = true;
			break;

		case NP:
			this.np = true;
			break;

		case ADJP:
			this.adj = true;
			break;

		case ADVP:
			this.adv = true;
			break;
		}

	}

}
