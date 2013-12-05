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

package simplenlg.realiser;

// TODO: Auto-generated Javadoc
/**
 * DocStructure is the document structure level.
 * <P>
 * This (partially) follows Powers et al (2003)
 * <P>
 * Structures are:
 * <UL>
 * <LI>PHRASE - phrase withing a sentence
 * <LI>PHRASESET - set of phrases
 * <LI>CLAUSE - sentence
 * <LI>SENTENCESET - set of sentences
 * <LI>PARAGRAPH - paragraph
 * <LI>PARAGRAPHSET - set of paragraphs
 * <LI>SUBSECTION - document subsection
 * <LI>SECTION - document
 * <LI>DOCUMENT - complete document
 * </UL>
 * 
 * Document structures are ordered, and a structure cannot contain as a
 * component a higher-order structure (eg, a paragraph cannot contain a
 * section). SET structures can contain themselves as components (eg, a
 * SENTENCESET can contain other SENTENCESET), otherwise structures cannot
 * contain themselves (eg, a PARAGRAPH cannot contain a PARAGRAPH)
 * 
 * @author ereiter
 */
public enum DocStructure {

	/**
	 * A simplenlg PhraseSpec or equivalent
	 */
	PHRASE,
	/** A set of phrases, within a sentence. */
	PHRASESET,
	/** A sentence. */
	SENTENCE,
	/** A set of sentences, within a paragraph. */
	SENTENCESET,
	/** A paragraph. */
	PARAGRAPH,
	/** A set of paragraphs. */
	PARAGRAPHSET,
	/** A subsection, within a section */
	SUBSECTION,
	/** A section, within a document */
	SECTION,
	/** A document */
	DOCUMENT;

	/**
	 * Checks if is sentence.
	 * 
	 * @return true, if is sentence
	 */
	public boolean isSentence() {
		return this == SENTENCE;
	}

	/**
	 * Checks if is paragraph.
	 * 
	 * @return true, if is paragraph
	 */
	public boolean isParagraph() {
		return this == PARAGRAPH;
	}

	/**
	 * Checks if is sentence or lower.
	 * 
	 * @return true, if is sentence or lower
	 */
	public boolean isSentenceOrLower() {
		// return T if this structure is a sentence or part of a sentence
		return compareTo(SENTENCE) <= 0;
	}

	/**
	 * Checks if is sentence component.
	 * 
	 * @return true, if is sentence component
	 */
	public boolean isSentenceComponent() {
		// return T if this structure is part of a sentence
		return compareTo(SENTENCE) < 0;
	}

	/**
	 * Return maximum of two doc structures
	 * 
	 * @param d2
	 *            other doc structure
	 * 
	 * @return the doc structure
	 */
	public DocStructure max(DocStructure d2) {
		// return maximum of self and d2
		if (compareTo(d2) > 0) {
			return this;
		} else {
			return d2;
		}
	}

	/**
	 * Next highest document structure
	 * 
	 * @return the doc structure
	 */
	public DocStructure next() {
		// return next highest level
		switch (this) {
		case PHRASE:
			return PHRASESET;
		case PHRASESET:
			return SENTENCE;
		case SENTENCE:
			return SENTENCESET;
		case SENTENCESET:
			return PARAGRAPH;
		case PARAGRAPH:
			return PARAGRAPHSET;
		case PARAGRAPHSET:
			return SUBSECTION;
		case SUBSECTION:
			return SECTION;
		case SECTION:
			return DOCUMENT;
		case DOCUMENT:
			return DOCUMENT; // no higher level!
		default:
			return DOCUMENT; // don't really need this, but makes
			// Java happy
		}
	}

	/**
	 * Previous (next lowest) document structure
	 * 
	 * @return the doc structure
	 */
	public DocStructure previous() {
		// return next lowest level
		switch (this) {
		case PHRASE:
			return PHRASE; // no lower level!
		case PHRASESET:
			return PHRASE;
		case SENTENCE:
			return PHRASESET;
		case SENTENCESET:
			return SENTENCE;
		case PARAGRAPH:
			return SENTENCESET;
		case PARAGRAPHSET:
			return PARAGRAPH;
		case SUBSECTION:
			return PARAGRAPHSET;
		case SECTION:
			return SUBSECTION;
		case DOCUMENT:
			return SECTION;
		default:
			return PHRASE; // don't really need this, but makes
			// Java happy
		}
	}

	/**
	 * lowestParent - return lowest document structure which I can be part of.
	 * This is myself if I am a SET, otherwise the next highest structure
	 * 
	 * @return the doc structure
	 */
	public DocStructure lowestParent() {
		// return self if a set, otherwise next highest level which is a set
		switch (this) {
		case PHRASESET:
			return PHRASESET;
		case SENTENCESET:
			return SENTENCESET;
		default:
			return next(); // next highest doc structure
		}
	}

}