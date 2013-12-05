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

import java.util.List;

import simplenlg.exception.SimplenlgException;
import simplenlg.formatter.Formatter;
import simplenlg.formatter.HTMLFormatter;
import simplenlg.formatter.TextFormatter;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.LexiconInterface;

// TODO: Auto-generated Javadoc
/**
 * Realiser converts a text spec into text or HTML.
 * <P>
 * It has several parameters:
 * <UL>
 * <LI>simplenlg.lexicon (should not normally need to be specified)
 * <LI>lineLength - output poured into lines of this length (default 70)
 * <LI>HTML - if true, include HTML markups (default false)
 * </UL>
 * 
 * @author ereiter
 */
public class Realiser {

	// realiser parameters ************************************************
	/** The HTML. */
	private boolean HTML = false; // output HTML instead of text

	/** The line length. */
	private int lineLength = 70; // max line length

	/** The lexicon. */
	private LexiconInterface lexicon; // simplenlg.lexicon

	/** the markups. */
	private Formatter formatter;

	// constants for knowledge about characters

	/** The Constant FOLLOW_NOSPACE. */
	private final static String FOLLOW_NOSPACE = " ("; // don't need a space
	// after this
	/** The Constant START_NOSPACE. */
	private final static String START_NOSPACE = " ;,.!?)"; // don't need a
	// space before this

	// punctuation symbols, strongest first (in Nirenburg's sense)
	/** The Constant PUNCTUATION. */
	private final static String PUNCTUATION = "!?.:;-,";

	/** The Constant SENTENCE_TERMINATORS. */
	final static String SENTENCE_TERMINATORS = "!?."; // sentence-end symbols

	// constructors ********************************************************
	/**
	 * construct a default realiser.
	 */
	public Realiser() { // default constructor - use default simplenlg.lexicon
		this(new Lexicon());
	}

	/**
	 * construct a realiser with the specified simplenlg.lexicon
	 * 
	 * @param lexicon
	 *            the lexicon
	 */

	public Realiser(LexiconInterface lexicon) { // main constructor, including
		// simplenlg.lexicon
		this.lexicon = lexicon;
		this.formatter = new TextFormatter();
	}

	/**
	 * Gets the lexicon.
	 * 
	 * @return the lexicon
	 */
	public LexiconInterface getLexicon() {
		return this.lexicon;
	}

	// getters and setters for paramters ************************************
	/**
	 * Gets whether HTML is produced.
	 * 
	 * @return <code>true</code> if this realiser will output HTML formatted
	 *         text.
	 */
	public boolean isHTML() {
		return this.HTML;
	}

	/**
	 * specifies whether HTML is produced.
	 * 
	 * @param html
	 *            the html
	 */
	public void setHTML(boolean html) {
		this.HTML = html;
		if (html) {
			this.formatter = new HTMLFormatter();
		} else {
			this.formatter = new TextFormatter();
		}
	}

	/**
	 * sets output line length.
	 * 
	 * @param lineLength
	 *            the line length
	 */
	public void setLineLength(int lineLength) {
		this.lineLength = lineLength;
	}

	/**
	 * Gets output line length.
	 * 
	 * @return The length of lines (in characters) in the output.
	 */
	public int getLineLength() {
		return this.lineLength;
	}

	/**
	 * @return the formatter
	 */
	Formatter getFormatter() {
		return this.formatter;
	}

	/**
	 * Realise a <code>Spec</code>.
	 * 
	 * @param spec
	 *            The <code>Spec</code> to be realised.
	 * 
	 * @return The String realisation.
	 */
	public String realise(Object spec) {
		if (spec == null) {
			return "";
		} else if (spec instanceof String) {
			return (String) spec;
		} else if (spec instanceof PhraseSpec) {
			return ((PhraseSpec) spec).isElided() ? "" : ((PhraseSpec) spec)
					.realise(this);
		} else if (spec instanceof Spec) {
			return ((Spec) spec).realise(this);
		} else {
			throw new SimplenlgException("Can only realise Strings or Specs");
		}
	}

	/**
	 * Realises a <code>Spec</code> as a document. This makes use of the
	 * {@link simplenlg.realiser.Spec#promote(DocStructure)} method to promote
	 * the <code>Spec</code> to the document level.
	 * 
	 * @param spec
	 *            The <code>Spec</code>
	 * 
	 * @return The realisation at document level.
	 */
	public String realiseDocument(Spec spec) {
		return realise(spec.promote(DocStructure.DOCUMENT));
	}

	// orthography, layout, spacing code
	// ********************************************

	// sentence orthography *********************************

	/*
	 * Apply sentence orthography. Calls the method
	 * applySentenceOrthography(String, char) with the default terminator '.',
	 * i.e. a fullstop.
	 */
	/**
	 * Apply sentence orthography.
	 * 
	 * @param body
	 *            the body
	 * 
	 * @return the string
	 */
	String applySentenceOrthography(String body) {
		return applySentenceOrthography(body, '.', true);
	}

	/*
	 * Applies sentence orthography with the given punctuation mark (e.g. ".",
	 * "?", etc) applySentenceOrthography - corrects orthography for a sentence
	 * Make sure that - its first word is capitalised - the sentence ends with a
	 * full stop or other sentence terminator - leading/trailink blanks are
	 * eliminated.
	 * 
	 * Sentence is rendered with the given punctuation mark.
	 */
	/**
	 * Apply sentence orthography - capitalise first letter, make sure we have
	 * sentence-ending punctuation
	 * 
	 * @param body
	 *            the body
	 * @param terminator
	 *            the terminator
	 * @param capitalise
	 *            whether the first letter of the sentence should be capitalised
	 *            (false in case the subject NP is an acronym such as pH)
	 * 
	 * 
	 * @return the string
	 */
	String applySentenceOrthography(String body, char terminator,
			boolean capitalise) {
		String result;

		// stop if string is empty
		if (body.length() == 0) {
			return body;
		}

		// eliminate leading/trailing blanks
		result = body.trim();

		// Capitalise first word if it starts with a letter
		char firstChar = result.charAt(0);
		if (capitalise && Character.isLetter(firstChar)
				&& Character.isLowerCase(firstChar)) {
			result = Character.toUpperCase(firstChar) + result.substring(1);
		}

		// Add "." (unless it is absorbed)
		result = addPunctuation(result, terminator);

		return result;
	}

	// paragraph orthography ****************************************

	/**
	 * Apply paragraph orthography - pour into limited line length Markups are
	 * added by the formatter, before this is called
	 * 
	 * @param body
	 *            the body
	 * 
	 * @return the string
	 */
	String applyParagraphOrthography(String body) {

		String result = pour(body, this.lineLength);

		return result;
	}

	// document orthography ****************************************

	/**
	 * Apply document orthography - get rid of initial CRLF Markups are added by
	 * the formatter, before this is called
	 * 
	 * @param body
	 *            the body
	 * 
	 * @return the string
	 */
	String applyDocumentOrthography(String body) {

		String result = body;

		// get rid of initial NEW_LINE
		while (result.startsWith(this.formatter.getNEW_LINE())) {
			result = result.substring(this.formatter.getNEW_LINE().length());
		}

		return result;
	}

	// spacing and punctuation code
	// ***************************************************

	// append N strings, adding a space if needed
	/**
	 * Append space.
	 * 
	 * @param strings
	 *            the strings
	 * 
	 * @return the string
	 */
	String appendSpace(String... strings) {
		String result = "";
		for (String s : strings) {
			if (s == null) {
				continue;
			} else if (result.length() == 0) {
				result = s;
			} else if (s == null) {
				; // do nothing
			} else if (spaceNeeded(result, s)) {
				result = result + " " + s;
			} else {
				result = result + s;
			}
		}
		return result;
	}

	// convenience method to append a string and a char
	/**
	 * Append space.
	 * 
	 * @param body
	 *            the body
	 * @param c
	 *            the c
	 * 
	 * @return the string
	 */
	String appendSpace(String body, char c) {
		return appendSpace(body, Character.toString(c));
	}

	// spaceNeeded - return TRUE if need to add a space between segments
	/**
	 * Space needed.
	 * 
	 * @param firstString
	 *            the first string
	 * @param secondString
	 *            the second string
	 * 
	 * @return true, if successful
	 */
	private boolean spaceNeeded(String firstString, String secondString) {
		// Don't need a space if either string is empty
		if (firstString == null || secondString == null
				|| firstString.length() == 0 | secondString.length() == 0) {
			return false;
		}

		// Don't need a space if firstString ends with char in FOLLOW_NOSPACE
		// or if it is whitesoace itself
		char lastChar = firstString.charAt(firstString.length() - 1);
		if (Character.isWhitespace(lastChar)
				|| Realiser.FOLLOW_NOSPACE.indexOf(lastChar) >= 0) {
			return false;
		}

		// Don't need a space if secondString starts with a char in
		// START_NOSPACE
		// or if it is whitesoace itself
		char firstChar = secondString.charAt(0);
		if (Character.isWhitespace(firstChar)
				|| Realiser.START_NOSPACE.indexOf(firstChar) >= 0) {
			return false;
		}

		// All tests passed, space needed so return true
		return true;
	}

	// realise lists (with or without conjuncts)
	/**
	 * Realise list.
	 * 
	 * @param elements
	 *            the elements
	 * 
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	String realiseList(List elements) {
		// realise list; concatenate together with spaces as needed
		// List elements are specs or strings
		String realisation = null;

		if (elements == null || elements.isEmpty()) {
			realisation = "";
		} else if (elements.size() == 1) {
			realisation = realise(elements.get(0));
		} else {
			realisation = appendSpace(realise(elements.get(0)),
					realiseList(elements.subList(1, elements.size())));
		}

		return realisation == null ? "" : realisation;
	}

	String realiseIndentedList(List<Spec> elements) {
		if (elements == null || elements.isEmpty()) {
			return "";
		}

		String result = "";
		for (Spec spec : elements) {
			result = result
					+ this.formatter.addListElementMarkup(realise(spec));
		}

		return this.formatter.addIndentedListMarkup(result);
	}

	// realise a list, including a conjunct before last element
	// separator is "," unless an element includes "," or ";", in which
	// case it is ";"
	// separator is absorbed if necessary, so works when conjoining sentences
	/**
	 * Realise conjunct list.
	 * 
	 * @param elements
	 *            the elements
	 * @param conjunct
	 *            the conjunct
	 * 
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	protected String realiseConjunctList(List elements, String conjunct) {

		if (elements == null) {
			return "";
		}
		switch (elements.size()) {
		case 0:
			return "";
		case 1:
			return realise(elements.get(0));
		case 2:
			return appendSpace(realise(elements.get(0)), conjunct,
					realise(elements.get(1))); // eg, apples and oranges
		default:
			// large list
			// eg, pears, apples, and oranges
			// get all elements
			String[] strings = new String[elements.size()];
			char separator = ',';
			int index = 0;
			for (Object e : elements) {
				String s = realise(e);
				if (s.contains(",") || s.contains(";")) {
					separator = ';';
				}
				strings[index] = s;
				index++;
			}
			// now merge together
			String result = strings[0];
			for (index = 1; index < strings.length - 1; index++) {
				result = appendSpace(addPunctuation(result, separator),
						strings[index]);
			}
			return appendSpace(result, conjunct, strings[strings.length - 1]);

		}
	}

	// convenience method to realise a conjunct list with "and" as conjunct
	/**
	 * Realise and list.
	 * 
	 * @param elements
	 *            the elements
	 * 
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	String realiseAndList(List elements) {
		return realiseConjunctList(elements, "and");
	}

	// punctuation merging code ****************************
	// this adds a punc symbol unless it is absorbed (as in Nirenburg)

	/**
	 * Adds the punctuation.
	 * 
	 * @param body
	 *            the body
	 * @param punctuation
	 *            the punctuation
	 * 
	 * @return the string
	 */
	private String addPunctuation(String body, char punctuation) {
		char lastChar = body.charAt(body.length() - 1);

		// if body does not end in a punct symbol, just append punc
		if (Realiser.PUNCTUATION.indexOf(lastChar) < 0) {
			return appendSpace(body, punctuation);
		}

		// else drop the weaker punct symbol
		if (Realiser.PUNCTUATION.indexOf(lastChar) <= Realiser.PUNCTUATION
				.indexOf(punctuation)) {
			return body;
		} else {
			return appendSpace(body.substring(0, body.length() - 1),
					punctuation);
		}

	}

	// pouring code *****************************************************
	/**
	 * Pour.
	 * 
	 * @param body
	 *            the body
	 * @param length
	 *            the length
	 * 
	 * @return the string
	 */
	private String pour(String body, int length) {
		// pour string into lines of specified length

		if (body.length() <= length) {
			return body;
		}

		for (int i = length; i > 0; i--) {
			if (Character.isWhitespace(body.charAt(i))) {
				return body.substring(0, i) + this.formatter.getNEW_LINE()
						+ pour(body.substring(i + 1), length);
			}
		}

		// cannot pour, so just return body and hope for the best...
		return body;
	}

}
