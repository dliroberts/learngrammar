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

/**
 * This is an enumeration of the grammatical categories which are relevant to
 * the <code>simplenlg.lexicon</code> and <code>simplenlg.realiser</code>
 * packages.
 * <p>
 * All classes that implement the
 * {@link simplenlg.lexicon.lexicalitems.LexicalItem} and
 * {@link simplenlg.realiser.Phrase} interfaces implement a
 * <code>getCategory()</code> method.
 * <P>
 * The <code>Category</code> of a <code>LexicalItem</code> also determines which
 * (if any) morphological rules can apply to it.
 * <p>
 * Recognised grammatical categories are:
 * <code>NOUN, ADJECTIVE, VERB, DETERMINER, PRONOUN, CONJUNCTION, PREPOSITION, SYMBOL, CLAUSE</code>
 * 
 * @author agatt
 */
public enum Category {

	/** A default value, indicating an unspecified category. */
	ANY,

	/** The NOUN. */
	NOUN,

	/** The ADJECTIVE. */
	ADJECTIVE,

	/** The ADVERB. */
	ADVERB,

	/** The VERB. */
	VERB,

	/** The DETERMINER. */
	DETERMINER,

	/** The PRONOUN. */
	PRONOUN,

	/** The CONJUNCTION. */
	CONJUNCTION,

	/** The PREPOSITION. */
	PREPOSITION,

	/** The SYMBOL. */
	SYMBOL,

	/** The CLAUSE. */
	CLAUSE,

	/** The COMPLEMENTISER. */
	COMPLEMENTISER;

}
