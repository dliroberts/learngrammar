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

package simplenlg.lexicon;

import java.util.Collection;

import simplenlg.exception.LexiconException;
import simplenlg.features.Category;
import simplenlg.features.NumberAgr;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.lexicon.lexicalitems.LexicalItem;
import simplenlg.lexicon.morph.MorphologicalRule;

/**
 * This is the basic interface implementing a lexicon. A
 * <code>LexiconInterface</code> is conceived as a repository or container of
 * <code>LexicalItem</code>s, each of which is defined for a finite number of
 * morphological and syntactic operations, depending on its grammatical
 * category. Lexical items may also be defined for a variety of syntactic
 * features; see {@link simplenlg.features}.
 * 
 * @author agatt
 */
public interface LexiconInterface {

	// ******************************************************************
	// Addition/retrieval of lexical items
	// ******************************************************************

	/**
	 * Add a <code>LexicalItem</code> to this <code>LexiconInterface</code>.
	 * 
	 * @param lex
	 *            The <code>LexicalItem<code> to be added.
	 * 
	 * @see simplenlg.lexicon.lexicalitems.LexicalItem
	 */
	void addItem(LexicalItem lex);

	/**
	 * Add an item to this <code>LexiconInterface</code> by passing it a word
	 * and a <code>Category</code> The <code>LexiconInterface</code> will
	 * construct a new item, depending on the category passed.
	 * 
	 * @param cat
	 *            The <code>Category</code>
	 * @param word
	 *            The baseform of the lexical item.
	 * 
	 * @see simplenlg.features.Category
	 */
	void addItem(Category cat, String word);

	/**
	 * Get the <code>LexicalItem</code>s which have a certain baseform. For
	 * example, if the baseform is <I>bank</I>, this might return the two nouns
	 * corresponding to the two senses of <I>bank</I>, as well as the verb <I>to
	 * bank</I>.
	 * 
	 * @param baseform
	 *            The baseform
	 * 
	 * @return A collection of <code>LexicalItem</code>s with the baseform, if
	 *         any are contained in this lexicon, the empty list otherwise.
	 */
	Collection<LexicalItem> getItems(String baseform);

	/**
	 * Gets a lexical item of a given category which has the specified baseform.
	 * 
	 * @param cat
	 *            the category
	 * @param baseform
	 *            the baseform
	 * 
	 * @return the item, if one exists, <code>null</code> otherwise
	 */
	LexicalItem getItem(Category cat, String baseform);

	/**
	 * Gets the items of the specified category.
	 * 
	 * @param cat
	 *            the category
	 * 
	 * @return the items, if any are stored in the lexicon, an empty collection
	 *         otherwise.
	 */
	Collection<LexicalItem> getItems(Category cat);

	/**
	 * Gets the item with the given unique id in the lexicon.
	 * 
	 * @param id
	 *            the id
	 * 
	 * @return the item, if found, <code>null</code> otherwise
	 */
	LexicalItem getItemByID(String id);

	/**
	 * Gets the number of items of a specific category in the lexicon.
	 * 
	 * @param cat
	 *            the cat
	 * 
	 * @return the number of items
	 */
	int getNumberOfItems(Category cat);

	/**
	 * Check whether this <code>LexiconInterface</code> contains a
	 * <code>LexicalItem</code> of <code>Category</code> cat with baseform
	 * <code>word</code>.
	 * 
	 * @param cat
	 *            The <code>Category</code>
	 * @param word
	 *            The baseform
	 * 
	 * @return <code>true</code> if the <code>LexiconInterface</code> contains
	 *         an item with this <code>Category</code> and this baseform.
	 */
	boolean hasItem(Category cat, String word);

	/**
	 * Check whether this <code>LexiconInterface</code> contains a
	 * <code>LexicalItem</code> with baseform <code>word</code>.
	 * 
	 * @param word
	 *            The baseform
	 * 
	 * @return <code>true</code> if the <code>LexiconInterface</code> contains
	 *         an item with this this baseform.
	 */
	boolean hasItem(String word);

	/**
	 * Checks whether an item with the given id is stored in the lexicon.
	 * 
	 * @param id
	 *            the id
	 * 
	 * @return <code>true</code> if an item with this id has been stored in the
	 *         lexicon
	 */
	boolean hasItemID(String id);

	/**
	 * Gets the number of items in the lexicon
	 * 
	 * @return the number of items
	 */
	int getNumberOfItems();

	/**
	 * Gets the form of a verb given the tense, person and number.
	 * 
	 * @param baseform
	 *            the baseform
	 * @param tense
	 *            the tense
	 * @param person
	 *            the person
	 * @param number
	 *            the number
	 * 
	 * @return the verb form
	 */
	String getVerbForm(String baseform, Tense tense, Person person,
			NumberAgr number);

	/**
	 * Add a morphological rule to a lexicon. The rule must implement the
	 * <code>MorphologicalRule</code> interface. This is the easiest way to add
	 * functionality to the lexicon. New <code>MorphologicalRule</code>s added
	 * in this way are indexed by their name, the return value of the
	 * <code>MorphologicalRule.getName()</code> method.
	 * 
	 * @param rule
	 *            The <code>MorphologicalRule</code> to be added.
	 * @throws LexiconException
	 *             if the rule does not have a name with which it can be indexed
	 * @see simplenlg.lexicon.morph.MorphologicalRule
	 */
	public <T extends LexicalItem> void addRule(MorphologicalRule<T> rule)
			throws LexiconException;

	/**
	 * Gets the morphological rules that are available in this lexicon.
	 * 
	 * @return the rules
	 */
	public Collection<MorphologicalRule<?>> getRules();

	/**
	 * Apply a <code>MorphologicalRule</code> with a given name to a
	 * <code>LexicalItem</code>. The rule must already have been added to the
	 * <code>LexiconInterface</code>. If no such rule exists, or if the rule in
	 * question cannot be applied to the lexical item of the specified class,
	 * the return value is <code>null</code>.
	 * 
	 * @param ruleName
	 *            The name of the <code>MorphologicalRule</code>
	 * @param lex
	 *            The <code>LexicalItem</code> to which the rule should be
	 *            applied.
	 * 
	 * @return The result of applying the rule, if it exists, and if the rule
	 *         can be applied to the lexical item, <code>null</code> otherwise.
	 */
	public <T extends LexicalItem> String applyRule(String ruleName, T lex);

	/**
	 * Apply a <code>MorphologicalRule</code> with a given name to a given
	 * baseform. The rule must already have been added to the
	 * <code>LexiconInterface</code>. If no such rule exists, the return value
	 * is <code>null</code>. Note that, unlike
	 * {@link #applyRule(String, LexicalItem)} this method provides no way of
	 * checking whether the rule is in fact applicable to a word.
	 * 
	 * @param ruleName
	 *            The name of the <code>MorphologicalRule</code>
	 * @param baseform
	 *            The string to which the rule should be applied.
	 * 
	 * @return The result of applying the rule, if it exists, <code>null</code>
	 *         otherwise.
	 */
	public String applyRule(String ruleName, String baseform);

	/**
	 * Resets the lexicon. This method results in all lexical items being
	 * removed from storage.
	 */
	void reset();

	public void addLexicalClass(LexicalClass<?> lexClass);

	public boolean hasLexicalClass(String classID);

	public LexicalClass<?> getLexicalClass(String id);
}
