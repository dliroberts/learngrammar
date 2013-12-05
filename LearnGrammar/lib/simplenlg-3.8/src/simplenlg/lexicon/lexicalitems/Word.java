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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import simplenlg.exception.LexiconException;
import simplenlg.features.Category;
import simplenlg.features.InflectionType;
import simplenlg.lexicon.LexiconInterface;

/**
 * This is an abstract implementation of the
 * {@link simplenlg.lexicon.lexicalitems.LexicalItem} interface, from which all
 * other lexical item classes defined in this package inherit.
 * 
 * 
 * @author agatt
 */
public abstract class Word implements LexicalItem, Cloneable {

	String baseForm, citationForm, id;

	Category category;

	LexiconInterface parentLexicon;

	InflectionType inflectionType;

	Set<String> variants;

	Map<DerivationalRelation, Set<LexicalItem>> derivations;

	/*
	 * Implicit constructor: Constructs a new (empty) instance of
	 * <code>Word</code>.
	 */
	Word() {
		this.baseForm = "";
		this.parentLexicon = null;
		this.citationForm = "";
		this.variants = new HashSet<String>();
		this.derivations = new HashMap<DerivationalRelation, Set<LexicalItem>>();
	}

	/**
	 * Constructs a new instance of word with the given baseform.
	 * 
	 * @param baseform
	 *            The baseform of this lexical item (a word)
	 */
	public Word(String baseform) {
		this();

		if (baseform != null) {
			this.variants.add(baseform);
			this.baseForm = baseform;
			this.citationForm = this.baseForm;
		}
	}

	/**
	 * Instantiates a new word with the given id, baseform and citation form
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 * @param citationform
	 *            the citation form
	 */
	public Word(String id, String baseform, String citationform) {
		this(baseform);
		this.id = id;
		this.citationForm = citationform;
	}

	/**
	 * Constructs a new instance of a Word, with a given baseform and parent
	 * lexicon. The word is automatically inserted into the lexicon.
	 * 
	 * @param baseform
	 *            The baseform
	 * @param lexicon
	 *            The parent lexicon
	 * @deprecated As of Version 3.7, all extensions of the <code>Word</code>
	 *             class should set the parent lexicon directly using
	 *             {@link #setParentLexicon(LexiconInterface)}
	 */
	@Deprecated
	public Word(String baseform, LexiconInterface lexicon) {
		this(baseform);
		this.parentLexicon = lexicon;
		this.parentLexicon.addItem(this);
	}

	// *****************************************************
	// GETTERS/SETTERS
	// *****************************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#setID(java.lang.String)
	 */
	public void setID(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#getID()
	 */
	public String getID() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#hasID()
	 */
	public boolean hasID() {
		return this.id != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#getBaseForm()
	 */
	public String getBaseForm() {
		return this.baseForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.lexicalitems.LexicalItem#setCitationForm(java.lang.
	 * String)
	 */
	public void setCitationForm(String s) {
		this.citationForm = s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#getCitationForm()
	 */
	public String getCitationForm() {
		return this.citationForm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#getCategory()
	 */
	public Category getCategory() {
		return this.category;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.lexicalitems.LexicalItem#setParentLexicon(simplenlg
	 * .lexicon.LexiconInterface)
	 */
	public void setParentLexicon(LexiconInterface lex) {
		this.parentLexicon = lex;
		this.parentLexicon.addItem(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#getParentLexicon()
	 */
	public LexiconInterface getParentLexicon() {
		return this.parentLexicon;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#hasInflectionType()
	 */
	public boolean hasInflectionType() {
		return this.inflectionType != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#getInflectionType()
	 */
	public InflectionType getInflectionType() {
		return this.inflectionType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.lexicalitems.LexicalItem#setInflectionType(simplenlg
	 * .features.InflectionType)
	 */
	public void setInflectionType(InflectionType inflectionType) {

		if (inflectionType == null) {
			this.inflectionType = null;
		} else if (inflectionType.appliesTo(this.category)) {
			this.inflectionType = inflectionType;
		} else {
			throw new LexiconException("InflectionType " + inflectionType
					+ " cannot apply to Lexical Items of category "
					+ this.category);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#isDLRegular()
	 */
	public boolean isDLRegular() {
		return this.inflectionType == InflectionType.REG_DOUBLING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#isRegular()
	 */
	public boolean isRegular() {
		return this.inflectionType == InflectionType.REGULAR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#isGLRegular()
	 */
	public boolean isGLRegular() {
		return this.inflectionType == InflectionType.GL_REGULAR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#isInvariant()
	 */
	public boolean isInvariant() {
		return this.inflectionType == InflectionType.INVARIANT;
	}

	/**
	 * Checks if this word is periphrastic. This is equivalent to
	 * <code>Word.getInflectionType().equals(InflectionType#PERIPHRASTIC)</code>
	 * .
	 * 
	 * @return true, if the word is periphrastic
	 */
	public boolean isPeriphrastic() {
		return this.inflectionType == InflectionType.PERIPHRASTIC;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.lexicalitems.LexicalItem#addVariant(java.lang.String)
	 */
	public void addVariant(String variant) {
		this.variants.add(variant);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.lexicalitems.LexicalItem#hasVariant(java.lang.String)
	 */
	public boolean hasVariant(String variant) {
		return this.variants.contains(variant);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem#getVariants()
	 */
	public Collection<String> getVariants() {
		return this.variants;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.lexicalitems.LexicalItem#addDerivationalRelation(simplenlg
	 * .lexicon.lexicalitems.DerivationalRelation,
	 * simplenlg.lexicon.lexicalitems.LexicalItem)
	 */
	public void addDerivationalRelation(DerivationalRelation rel,
			LexicalItem lex) {
		if (this.derivations.containsKey(rel)) {
			this.derivations.get(rel).add(lex);
		} else if (rel.appliesTo(this.category)) {
			Set<LexicalItem> set = new HashSet<LexicalItem>();
			set.add(lex);
			this.derivations.put(rel, set);

		} else {
			throw new LexiconException("Relation " + rel
					+ " cannot apply to items of category " + this.category);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.lexicalitems.LexicalItem#hasDerivationalRelation(simplenlg
	 * .lexicon.lexicalitems.DerivationalRelation)
	 */
	public boolean hasDerivationalRelation(DerivationalRelation rel) {
		return this.derivations.containsKey(rel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.lexicalitems.LexicalItem#hasDerivationalRelation(simplenlg
	 * .lexicon.lexicalitems.DerivationalRelation,
	 * simplenlg.lexicon.lexicalitems.LexicalItem)
	 */
	public boolean hasDerivationalRelation(DerivationalRelation rel,
			LexicalItem lex) {
		if (hasDerivationalRelation(rel)) {
			return this.derivations.get(rel).contains(lex);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.lexicalitems.LexicalItem#getDerivations(simplenlg.lexicon
	 * .lexicalitems.DerivationalRelation)
	 */
	public Collection<LexicalItem> getDerivations(DerivationalRelation rel) {
		if (this.derivations.containsKey(rel)) {
			return this.derivations.get(rel);
		}

		return Collections.emptySet();
	}

	// *****************************************************
	// UTILITIES
	// *****************************************************

	/**
	 * Returns a String representation of the Word consisting of just its
	 * baseform.
	 * 
	 * @return The String representation
	 */
	@Override
	public String toString() {
		return this.category.toString() + "(" + this.baseForm + ")";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Word clone() {

		try {
			return (Word) super.clone();

		} catch (CloneNotSupportedException cnse) {
			return null;
		}
	}

	/**
	 * Returns <code>true</code> if, and only if:
	 * <UL>
	 * <LI>the object is a <code>Word</code>
	 * <LI>the two items have exactly the same baseform (ignoring case)
	 * <LI>the two items have the same grammatical category
	 * </UL>
	 * .
	 * 
	 * @param o
	 *            the object to which to compare this word
	 * 
	 * @return <code>true</code> if the object is equal to this one
	 */
	@Override
	public boolean equals(Object o) {
		boolean eq = false;

		if (o instanceof Word) {
			Word cw = (Word) o;

			if (this.baseForm != null && cw.baseForm != null) {
				eq = this.baseForm.equals(cw.baseForm)
						&& this.category == cw.category;
			}
		}

		return eq;
	}

}
