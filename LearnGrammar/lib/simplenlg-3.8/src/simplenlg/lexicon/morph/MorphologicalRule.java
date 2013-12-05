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

package simplenlg.lexicon.morph;

import simplenlg.lexicon.lexicalitems.LexicalItem;

/**
 * This is the basic interface for all morphological rules. Such rules operate
 * on lexical items or strings, and return strings as the result of their
 * operations.
 * 
 * <P>
 * This is a generic interface, whose type parameter identifies the class of
 * lexical item to which a rule can apply, capturing the categorical
 * restrictions on rule application. Thus, for example, pluralisation in English
 * should apply exclusively to nouns, while past-tense formation applies
 * exclusively to verbs.
 * 
 * <P>
 * There are three ways to apply a rule to a <code>LexicalItem</code>:
 * 
 * <OL>
 * <LI>Applying the rule directly from the item: The concrete implementations of
 * <code>LexicalItem</code> in the {@link simplenlg.lexicon.lexicalitems}
 * package provide methods to apply a number of morphological rules directly to
 * their baseforms, and return the result. These operations are restricted to
 * the rules defined in this package, which are the main inflection rules for
 * the main open word classes.</LI>
 * 
 * <LI>Adding a rule to a lexicon, and applying the rule by calling it from the
 * lexicon; see
 * {@link simplenlg.lexicon.LexiconInterface#applyRule(String, String)} and
 * {@link simplenlg.lexicon.LexiconInterface#applyRule(String, LexicalItem)}.
 * The implementations of {@link simplenlg.lexicon.LexiconInterface} in this
 * distribution automatically load a number of default inflectional rules, which
 * are also defined in the {@link simplenlg.lexicon.morph} sub-package.
 * User-defined rules can also be added to a lexicon; see
 * {@link simplenlg.lexicon.LexiconInterface#addRule(MorphologicalRule)}</LI>
 * </LI>
 * 
 * <LI>Passing a lexical item or a string to the rule itself; see
 * {@link #apply(String)} and {@link #apply(LexicalItem)}.</LI>
 * </OL>
 * 
 * @author agatt
 */
public interface MorphologicalRule<T extends LexicalItem> {

	/**
	 * Apply this rule to a string.
	 * 
	 * @param word
	 *            The <code>String</code> to which the rule will apply.
	 * 
	 * @return The <code>String</code> result of applying this rule.
	 */
	String apply(String word);

	/**
	 * Apply this rule to a <code>LexicalItem</code>.
	 * 
	 * @param lex
	 *            The <code>LexicalItem</code> to which the rule will apply.
	 * 
	 * @return The <code>String</code> result of applying this rule.
	 */
	String apply(T lex);

	/**
	 * Get the name of this rule. This is usually a <code>String</code> that is
	 * used for fast indexing when <code>MorphologicalRules</code> are added to
	 * a <code>LexiconInterface</code>, and enables them to be called by name.
	 * 
	 * @return The name of this rule.
	 * 
	 * @see simplenlg.lexicon.Lexicon#addRule(MorphologicalRule)
	 */
	String getName();

	/**
	 * Set the name of this morphological rule. For example, a rule to return
	 * the past participle of a verb might be named "VPPRule". Names are used by
	 * a lexicon to store morphological rules and index them for fast access.
	 * 
	 * @param name
	 *            The name of the rule
	 */
	void setName(String name);

	/**
	 * Checks whether a rule has a name. The name of a rule is used by a lexicon
	 * to index it internally, so that it can be called directly from the
	 * lexicon on <code>LexicalItem</code>s or strings.
	 * 
	 * @return <code>true</code> if the rule has a name.
	 */
	boolean hasName();
}
