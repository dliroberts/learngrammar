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

package simplenlg.formatter;

import java.util.List;

/**
 * Formatter applies markups for paragraphs, documents, etc. The abstract
 * Formatter class is specialised by formatters for specific output formats
 * 
 * @author ereiter
 */
public abstract class Formatter {

	// methods to return markups

	// NEW_LINE code in text
	protected static final String NEW_LINE = "\r\n";

	/**
	 * @return the NEW LINE sequence
	 */
	public String getNEW_LINE() {
		return Formatter.NEW_LINE;
	}

	/** get markup for start of a document */
	abstract protected String getDocStart();

	/** get markup for end of a document */
	abstract protected String getDocEnd();

	/** get markup for start of a document header/title */
	abstract protected String getDocHeaderStart();

	/** get markup for end of a document header/title */
	abstract protected String getDocHeaderEnd();

	/** get markup for start of a section header/title */
	abstract protected String getSectionHeaderStart();

	/** get markup for end of a section header/title */
	abstract protected String getSectionHeaderEnd();

	/** get markup for start of a subsection header/title */
	abstract protected String getSubSectionHeaderStart();

	/** get markup for end of a subsection header/title */
	abstract protected String getSubSectionHeaderEnd();

	/** get markup for start of para */
	abstract protected String getParaStart();

	/** get markup for end of para */
	abstract protected String getParaEnd();

	/** get markup for start of indented list */
	abstract protected String getIndentedListStart();

	/** get markup for end of indented list */
	abstract protected String getIndentedListEnd();

	/** get markup for start of indented list element */
	abstract protected String getListElementStart();

	/** get markup for end of indented list element */
	abstract protected String getListElementEnd();

	/** get list of start markups which can (be) absorbed, strongest first */
	abstract protected List<String> getAbsorbableStartMarkups();

	/** get list of end markups which can (be) absorbed, strongest first */
	abstract protected List<String> getAbsorbableEndMarkups();

	// methods to apply markups
	// Following Nunberg's model, we associate strengths with different markups
	// If we have two adjacent markups, the stronger "absorbs" the weaker
	// (ie, the weaker is dropped). If we have two adjacent markups which are
	// identical, one is dropped.

	/**
	 * apply document markup to a text
	 * 
	 * @param text
	 *            document body to be marked up
	 * @param header
	 *            title of document
	 * @return text with markup added
	 */
	public String addDocumentMarkup(String text, String header) {

		String result = text;
		if (header != null && header.length() > 0) {
			result = addMarkups(getDocHeaderStart(), header, getDocHeaderEnd())
					+ text;
		}

		return addMarkups(getDocStart(), result, getDocEnd());
	}

	/**
	 * apply section markup to a text
	 * 
	 * @param text
	 *            section body to be marked up
	 * @param header
	 *            title of section
	 * @return text with markup added
	 */
	public String addSectionMarkup(String text, String header) {

		String result = text;
		if (header != null && header.length() > 0) {
			result = addMarkups(getSectionHeaderStart(), header,
					getSectionHeaderEnd())
					+ text;
		}

		// no section markups (for now)
		return result;
	}

	/**
	 * apply subsection markup to a text
	 * 
	 * @param text
	 *            subsection body to be marked up
	 * @param header
	 *            title of subsection
	 * @return text with markup added
	 */
	public String addSubSectionMarkup(String text, String header) {

		String result = text;
		if (header != null && header.length() > 0) {
			result = addMarkups(getSubSectionHeaderStart(), header,
					getSubSectionHeaderEnd())
					+ text;
		}

		// no section markups (for now)
		return result;
	}

	/**
	 * apply paragraph markup to a text
	 * 
	 * @param text
	 * @return text with markup added
	 */
	public String addParagraphMarkup(String text) {

		// para don't have headers

		return addMarkups(getParaStart(), text, getParaEnd());
	}

	/**
	 * apply list element markup to a text
	 * 
	 * @param text
	 * @return text with markup added
	 */
	public String addListElementMarkup(String text) {

		return addMarkups(getListElementStart(), text, getListElementEnd());
	}

	/**
	 * apply indented list markup to a text
	 * 
	 * @param text
	 * @return text with markup added
	 */
	public String addIndentedListMarkup(String text) {

		return addMarkups(getIndentedListStart(), text, getIndentedListEnd());
	}

	/**
	 * add start and end markups to a text
	 * 
	 * @param startMarkup
	 * @param text
	 * @param endMarkup
	 * @return the modified text with the mark-ups added.
	 */
	private String addMarkups(String startMarkup, String text, String endMarkup) {
		return addEndMarkup(addStartMarkup(text, startMarkup), endMarkup);
	}

	/**
	 * add a starting (initial) markup. If text already starts with a markup,
	 * suppress the weaker one
	 * 
	 * @param text
	 * @param markup
	 * @return The modified text with the markup added to the beginning.
	 */
	private String addStartMarkup(String text, String markup) {
		// get index (strength) of markup to be added
		int markupIndex = getAbsorbableStartMarkups().indexOf(markup);
		// just prepend markup if it isn't absorbable
		if (markupIndex < 0) {
			return markup + text;
		}

		// see if text currently starts with an absorbable markup
		String oldMarkup = null;
		for (String possibleMarkup : getAbsorbableStartMarkups()) {
			if (text.startsWith(possibleMarkup)) {
				oldMarkup = possibleMarkup;
				break;
			}
		}

		// if not, just prepend markup
		int oldMarkupIndex = getAbsorbableStartMarkups().indexOf(oldMarkup);
		if (oldMarkupIndex < 0) {
			return markup + text;
		}

		// if new markup is weaker than old, don't make any changes
		if (oldMarkupIndex <= markupIndex) {
			return text;
			// otherwise get rid of old markup, add new markup
		} else {
			return markup + text.substring(oldMarkup.length());
		}

	}

	/**
	 * add an ending markup. If text already ends with a markup, suppress the
	 * weaker one
	 * 
	 * @param text
	 * @param markup
	 * @return the modified text with the markup on the end.
	 */
	private String addEndMarkup(String text, String markup) {
		// get index (strength) of markup to be added
		int markupIndex = getAbsorbableEndMarkups().indexOf(markup);
		// just append markup if it isn't absorbable
		if (markupIndex < 0) {
			return text + markup;
		}

		// see if text currently starts with an absorbable markup
		String oldMarkup = null;
		for (String possibleMarkup : getAbsorbableEndMarkups()) {
			if (text.endsWith(possibleMarkup)) {
				oldMarkup = possibleMarkup;
				break;
			}
		}

		// if not, just append markup
		int oldMarkupIndex = getAbsorbableEndMarkups().indexOf(oldMarkup);
		if (oldMarkupIndex < 0) {
			return text + markup;
		}

		// if new markup is weaker than old, don't make any changes
		if (oldMarkupIndex <= markupIndex) {
			return text;
			// otherwise get rid of old markup, add new markup
		} else {
			return text.substring(0, text.length() - oldMarkup.length())
					+ markup;
		}

	}
}
