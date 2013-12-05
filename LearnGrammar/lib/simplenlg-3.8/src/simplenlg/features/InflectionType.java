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

import java.util.Arrays;
import java.util.Collection;

/**
 * Enumerated type specifying the kind of inflection that lexical items. All
 * classes implementing <code>LexicalItem</code> are specified for this feature.
 * Values of the feature are mutually exclusive. Not all values apply to all
 * categories.
 * 
 * @author agatt
 * @since Version 3.7
 */
public enum InflectionType implements Feature {

	/**
	 * Specifies that an item inflects in a regular fashion (e.g. a noun like
	 * <I>forest</I> inflects for the plural by adding the <code>-s</code>
	 * suffix). Applies to any category.
	 */
	REGULAR(),

	/**
	 * Specifies that an item inflects in an irregular fashion (e.g. a noun like
	 * <I>woman</I> inflects for the plural as <I>women</I>). Applies to any
	 * category.
	 */
	IRREGULAR(),

	/**
	 * Specifies that an item inflects in a regular fashion, but ends in a
	 * consonant that needs to be doubled (e.g. the verb <I>handbag</I> takes
	 * consonant doubling, as in <I>handbagging</I>, <I>handbagged</I> etc).
	 * Applies to verbs, adjectives and adverbs.
	 */
	REG_DOUBLING(Category.VERB, Category.ADJECTIVE, Category.ADVERB),

	/**
	 * Specifies that an item inflects in a regular fashion with a Greco-Latin
	 * suffix (e.g. a noun like <I>calculus</I> pluralises as <I>calculi</I>).
	 * Applies only to nouns.
	 */
	GL_REGULAR(Category.NOUN),

	/**
	 * Specifies that an item doesn't inflect, or has zero affixation in
	 * inflection. All function words are invariant under this definition.
	 * Applies to any category.
	 */
	INVARIANT(),

	/**
	 * Applicable to adjectives or adverbs, and indicates that they take a
	 * periphrastic form of the comparative and superlative (e.g. the adjective
	 * <I>beautiful</I> becomes <I>more beautiful</I>).
	 */
	PERIPHRASTIC(Category.ADJECTIVE, Category.ADVERB);

	// categories allowed
	private Collection<Category> categories;

	// constructor
	InflectionType() {
		this.categories = Arrays.asList(Category.values());
	}

	// constructor with applicable categories
	InflectionType(Category... categories) {
		this.categories = Arrays.asList(categories);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.features.Feature#appliesTo(simplenlg.features.Category)
	 */
	public boolean appliesTo(Category c) {
		return this.categories.contains(c);
	}

}
