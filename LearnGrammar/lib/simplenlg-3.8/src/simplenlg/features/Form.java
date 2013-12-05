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
 * Enumerated type of the possible forms of the verb (and its associated
 * sentence).
 * 
 * @author agatt
 */

public enum Form implements Feature {

	/**
	 * Typically the declarative sentence, but in the current implementation,
	 * used as the default form to the exclusion of the others.
	 */
	NORMAL,

	/** The INFINITIVE, e.g. <i>to eat an apple</i> */
	INFINITIVE,

	/** Gerund form of the VP, e.g. <i>eating an apple</i> */
	GERUND,

	/** The form, e.g. <I>eat an apple!</I> */
	IMPERATIVE,

	/** Bare infinitive VP, e.g. <i>eat an apple</i>. */
	BARE_INFINITIVE,

	/** Subjunctive form, e.g. <i>if I were a rich man</i>. */
	SUBJUNCTIVE,

	PRESENT_PARTICIPLE,

	PAST_PARTICIPLE;

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.features.Feature#appliesTo(simplenlg.features.Category)
	 */
	public boolean appliesTo(Category cat) {
		return cat.equals(Category.VERB) || cat.equals(Category.CLAUSE);
	}

	/**
	 * Check whether a sentence in a given form can have a subject. This is only
	 * true of the following cases:
	 * <UL>
	 * <LI><code>NORMAL</code>: <i><strong>the man</strong> went to the pub</i>
	 * <LI><code>GERUND</code>: <i><strong>the man's</strong> going to the
	 * pub</i>
	 * </UL>
	 * 
	 * <P>
	 * Note that restrictions exist on the form of the subject of a
	 * <code>GERUND</code>. This is usually a genitive noun phrase.
	 * 
	 * @return <code>true</code> if a sentence in this form can have a subjects.
	 * 
	 * @see simplenlg.realiser.VPPhraseSpec#setForm(Form)
	 * @see simplenlg.realiser.SPhraseSpec#setForm(Form)
	 */
	public boolean hasSubject() {
		return this == NORMAL || this == GERUND;
	}

	/**
	 * Check if a sentence in this form can be embedded within another sentence.
	 * This is allowed for the values <code>INFINITIVE</code>,
	 * <code>GERUND</code> and <code>NORMAL</code>. Examples:
	 * 
	 * <ul>
	 * <li><code>NORMAL</code>: <i>John said that <strong>Bill went to his
	 * place</strong>.</i></li>
	 * <li><code>INFINITIVE</code>: <i><strong>To eat an apple</strong> is
	 * dangerous.</i></li>
	 * <li><code>GERUND</code> <i><strong>Eating an apple</strong> is
	 * dangerous.</i></li>
	 * </ul>
	 * 
	 * @return <code>true</code> if a sentence in this form can be embedded.
	 */
	public boolean isEmbedded() {
		return this == INFINITIVE || this == GERUND;
	}

	/**
	 * Check if this form allows tenses (past, future). This is only true for
	 * <code>NORMAL</code> form.
	 * 
	 * @return <code>true</code> if a sentence in this form allows tense
	 *         inflection
	 */
	public boolean allowsTense() {
		return this == NORMAL;
	}

	/*
	 * Tests the compatibility of a given <code>Form</code> with a given {@link
	 * simplenlg.features.Mood}. In particular, you cannot have a
	 * <code>GERUND</code> or <code>INFINITIVE</code> form for a clause if the
	 * clause is a <code>SUBJUNCTIVE</code> or a <code>QUESTION</code>.
	 * 
	 * @param m A value of {@link simplenlg.features.Mood} @return
	 * <code>true</code> if this mood and this form are compatible.
	 */
	/*
	 * public boolean isCompatible(Mood m) { switch (m) { case NORMAL: return
	 * true;
	 * 
	 * case SUBJUNCTIVE: return (this == NORMAL);
	 * 
	 * default: return false; // unnecessary, but makes java happy } }
	 */

}
