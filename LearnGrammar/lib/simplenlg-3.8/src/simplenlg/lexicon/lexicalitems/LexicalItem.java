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

import simplenlg.features.Category;
import simplenlg.features.InflectionType;
import simplenlg.lexicon.LexiconInterface;

/**
 * The basic interface that any element stored in a
 * <code>simplenlg.lexicon.LexiconInterface</code> must implement. A
 * <code>LexicalItem</code> holds basic information about the baseform, the
 * citation form (optional) and the inflectional forms of a word. In addition, a
 * <code>LexicalItem</code> provides methods to specify a unique ID for the
 * item. This is useful to distinguish cases where items have the same baseform,
 * but are actually different (as in cases of homonymy, such as <I>bank</I>, and
 * zero-derivation, as in <I>run</I> (N) and <I>run</I> (V). If a
 * <code>LexicalItem</code> is added to an object instantiating the
 * {@link simplenlg.lexicon.LexiconInterface}, but does not have an ID
 * specified, it is assigned one automatically.
 * 
 * <P>
 * By default, the citation form and the baseform of a word should be identical,
 * but setters are provided to set the citation form explicitly, overriding this
 * default behaviour.
 * 
 * <P>
 * Any implementation should provide three "standard" constructors: a
 * constructor with a single argument of type <code>String</code>, specifying
 * the baseform of the new <code>LexicalItem</code>, a constructor with two
 * arguments of type <code>String</code>, a unique ID and a baseform, and a
 * constructor with three arguments of type <code>String</code>, a unique Id, a
 * baseform and a citation form. See {@link simplenlg.lexicon.lexicalitems.Word}
 * for an instantiation.
 * 
 * <P>
 * Every class that implements this interface should also be specified for a
 * {@link simplenlg.features.Category}.
 * 
 * @author agatt
 */
public interface LexicalItem {

	/**
	 * Get the string representation of this <code>LexicalItem</code>, its lemma
	 * or baseform.
	 * 
	 * @return The baseform.
	 */
	String getBaseForm();

	/**
	 * Add an orthographic variant to this word, in addition to the baseform.
	 * For example, <I>bowlderization</I> is an orthographic variant of
	 * <I>bowlderisation</I> (or vice versa).
	 * 
	 * @param variant
	 *            The orthographic variant
	 */
	void addVariant(String variant);

	/**
	 * Check whether this lexical item has the specified orthographic variant.
	 * 
	 * @param variant
	 *            the variant
	 * @return <code>true</code> if the variant has been specified for this item
	 *         using {@link #addVariant(String)}, or if the specified variant is
	 *         identical to the baseform of this item.
	 */
	boolean hasVariant(String variant);

	/**
	 * Gets all the orthographic variants specified for this lexical item,
	 * including the baseform.
	 * 
	 * @return the variants of this item.
	 */
	Collection<String> getVariants();

	/**
	 * Gets the citation form.
	 * 
	 * @return the citation form
	 */
	String getCitationForm();

	/**
	 * Sets the citation form.
	 * 
	 * @param citationform
	 *            the new citation form
	 */
	void setCitationForm(String citationform);

	/**
	 * Get the unique id for this lexical item.
	 * 
	 * @return the id if specified, <code>null</code> otherwise.
	 */
	String getID();

	/**
	 * Sets a unique id for this lexical item. This is mainly useful for
	 * identifying lexical items with their corresponding entries in a database,
	 * and for distinguishing between homonymous items (i.e. those with the same
	 * baseform but different meanings)
	 * 
	 * @param id
	 *            the new id
	 */
	void setID(String id);

	/**
	 * Checks whether an id has been set for this item.
	 * 
	 * @return true, if successful
	 */
	boolean hasID();

	/**
	 * Get the <code>Category</code> of this item.
	 * 
	 * @return a {@link simplenlg.features.Category}
	 */
	Category getCategory();

	/**
	 * Set the <code>LexiconInterface</code> to which this
	 * <code>LexicalItem</code> belongs.
	 * 
	 * @param lex
	 *            An instance of a {@link simplenlg.lexicon.LexiconInterface}
	 */
	void setParentLexicon(LexiconInterface lex);

	/**
	 * Gets the parent lexicon.
	 * 
	 * @return The instance of {@link simplenlg.lexicon.LexiconInterface} to
	 *         which this word belongs.
	 */
	LexiconInterface getParentLexicon();

	/**
	 * Set the {@link simplenlg.features.InflectionType} of this
	 * <code>LexicalItem</code>. This specifies the kind of inflection that it
	 * undergoes, irrespective of its category.
	 * 
	 * Any item can have one, and only one, type.
	 * 
	 * @param infl
	 *            The <code>InflectionType</code> of this
	 *            <code>LexicalItem</code>
	 */
	void setInflectionType(InflectionType infl);

	/**
	 * Gets the inflection type.
	 * 
	 * @return the inflection type, if set, <code>null</code> otherwise
	 */
	InflectionType getInflectionType();

	/**
	 * Checks for inflection type.
	 * 
	 * @return <code>true</code> if {@link #getInflectionType()} !=
	 *         <code>null</code>
	 */
	boolean hasInflectionType();

	/**
	 * Checks if this item has regular inflection.
	 * 
	 * @return true, if the item is regular
	 */
	boolean isRegular();

	/**
	 * Checks if this item is invariant, that is, is never inflected.
	 * 
	 * @return true, if the item is invariant
	 */
	boolean isInvariant();

	/**
	 * Checks if this item takes regular inflection, with consonant doubling.
	 * This usually applies to verbs and adjectives, e.g.:
	 * 
	 * <UL>
	 * <LI>handbag (V) --> handba<strong>gg</strong>ing</LI>
	 * <LI>fat (ADJ) --> fa<strong>tt</strong>er</LI>
	 * <LI>
	 * </UL>
	 * 
	 * @return true, if is regular with consonant doubling
	 */
	boolean isDLRegular();

	/**
	 * Checks if this item takes regular inflection with greco-latin
	 * suffixation. Usually applies to nouns only, e.g.:
	 * <UL>
	 * <LI>bacillus (N) --> bacilli</LI>
	 * </UL>
	 * 
	 * @return true, if is the item is greco-latin regular
	 */
	boolean isGLRegular();

	/**
	 * Check whether this item has the specified derivational relation to any
	 * other item.
	 * 
	 * @param rel
	 *            the relation
	 * @return <code>true</code> if the relation has been specified between this
	 *         item and at least one other item.
	 * @since version 3.8
	 */
	boolean hasDerivationalRelation(DerivationalRelation rel);

	/**
	 * Check whether a relation of the specified type exists between this item
	 * and the specified item.
	 * 
	 * @param rel
	 *            the relation
	 * @param item
	 *            the related item
	 * @return <code>true</code> if the relation between this item and the other
	 *         one exists
	 */
	boolean hasDerivationalRelation(DerivationalRelation rel, LexicalItem item);

	/**
	 * Adds a derivational relation between this lexical item and another.
	 * 
	 * @param rel
	 *            the relation
	 * @param lex
	 *            the related lexical item
	 * @since version 3.8
	 */
	void addDerivationalRelation(DerivationalRelation rel, LexicalItem lex);

	/**
	 * Gets the lexical items related to this one by the specified relation.
	 * 
	 * @param rel
	 *            the relation
	 * @return the items, if any
	 * @since version 3.8
	 */
	Collection<LexicalItem> getDerivations(DerivationalRelation rel);

}
