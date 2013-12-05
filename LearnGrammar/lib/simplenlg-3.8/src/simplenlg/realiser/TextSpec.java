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

import java.util.ArrayList;
import java.util.List;

import simplenlg.exception.SimplenlgException;
import simplenlg.formatter.Formatter;

// TODO: Auto-generated Javadoc
/**
 * simplenlg.TextSpec represents a text specification.
 * <P>
 * This follows the terminology of Reiter and Dale (2000), and is also partially
 * inspired by Powers et al (2003)
 * <P>
 * A TextSpec is a tree. It consists of
 * <UL>
 * <LI>A list of children (which can be TextSpec or PhraseSpec)
 * <LI>A document structure level (eg, sentence)
 * <LI>A list conjunct (if PHRASE, PHRASESPEC, SENTENCE): if specified, the
 * TextSpec is realised as something like "C1, C2 and C3" (with "and" replaced
 * by the specified list conjunct)
 * <LI>indentedList (if SENTENCESET, PARAGRAPHSET): if set this will be realised
 * as an indented list.
 * <LI>A heading (if DOCUMENT, SECTION, SUBSECTION): if present this will be
 * realised as a heading/title
 * </UL>
 * <P>
 * 
 * @author ereiter
 */
public class TextSpec extends Spec {

	// parameters - children and document structure

	/** The children. */
	private List<Spec> children; // constituent phrase/text specs

	/** The doc structure. */
	private DocStructure docStructure; // doc structure (eg, paragraph)

	/** The list conjunct. */
	private String listConjunct; // conjunct for list (null if no list)

	/** indentedList flag */
	private boolean indentedList;

	/** heading */
	private PhraseSpec heading;

	private boolean defaultCaps;

	// constructors *************************************
	/**
	 * create an empty TextSpec.
	 */
	public TextSpec() {
		super();
		this.listConjunct = null;
		this.children = new ArrayList<Spec>();
		this.docStructure = DocStructure.SENTENCE;
		this.defaultCaps = true;
	}

	/**
	 * create a TextSpec with one or more constituents.
	 * <P>
	 * If a constituent is a DocStructure, it sets the document structure of the
	 * TextSpec
	 * 
	 * @param specs
	 *            the specs
	 */
	public TextSpec(Object... specs) { // construct a TextSpec from a list of
		// specs
		// specs are Object because we also allow Strings
		// also, if a spec is a DocStructure, it sets the doc structure
		this(); //

		for (Object spec : specs) {
			if (spec instanceof DocStructure) {
				setDocStructure((DocStructure) spec);
			} else {
				addSpec(spec);
			}
		}
	}

	// getters, setters, and also addSpec (which is a sort of setter)

	/**
	 * set the document structure (replaces existing document structure).
	 * 
	 * @param docStructure
	 *            the doc structure
	 * 
	 * @see TextSpec#setDocument()
	 * @see TextSpec#setParagraph()
	 * @see TextSpec#setSentence()
	 */
	public void setDocStructure(DocStructure docStructure) {
		this.docStructure = docStructure;
	}

	/**
	 * gets the document structure.
	 * 
	 * @return the doc structure
	 */
	public DocStructure getDocStructure() {
		return this.docStructure;
	}

	/**
	 * sets the document structure to CLAUSE.
	 */
	public void setSentence() { // convenience method for sentence DS
		setDocStructure(DocStructure.SENTENCE);
	}

	/**
	 * sets the document structure to PARAGRAPH.
	 */
	public void setParagraph() { // convenience method for paragraph DS
		setDocStructure(DocStructure.PARAGRAPH);
	}

	/**
	 * sets the document structure to DOCUMENT.
	 */
	public void setDocument() { // convenience method for document DS
		setDocStructure(DocStructure.DOCUMENT);
	}

	/**
	 * sets the list conjunct (replaces existing list conjunct.
	 * 
	 * @param listConjunct
	 *            the list conjunct
	 */
	public void setListConjunct(String listConjunct) {
		this.listConjunct = listConjunct;
	}

	/**
	 * gets the list conjunct (returns null if none specified).
	 * 
	 * @return the list conjunct
	 */
	public String getListConjunct() {
		return this.listConjunct;
	}

	/**
	 * @return the indentedList flag
	 */
	public boolean isIndentedList() {
		return this.indentedList;
	}

	/**
	 * sets whether this text spec should be realised as an indented list only
	 * works for SENTENCESET and PARAGRAPHSET structures
	 * 
	 * @param indentedList
	 *            if true, use indented list
	 */
	public void setIndentedList(boolean indentedList) {
		this.indentedList = indentedList;
	}

	/**
	 * Set whether to apply default sentence orthography by capitalising the
	 * first character of child phrases of this spec. It is sometimes desirable
	 * to override this, e.g. when a sentence begins with an acronym, such as
	 * <I>pH</I>. Note: Setting this to <code>false</code> means that no
	 * capitalisation is automatically applied to any of the children of this
	 * text spec.
	 * 
	 * @param caps
	 *            whether or not to apply default capitalisation
	 */
	public void setDefaultCaps(boolean caps) {
		this.defaultCaps = caps;
	}

	/**
	 * Check whether this text spec applies default capitalisation.
	 * 
	 * @return <code>true</code> if default capitalisation is applied
	 * @see #setDefaultCaps(boolean)
	 */
	public boolean hasDefaultCaps() {
		return this.defaultCaps;
	}

	/**
	 * @return the heading
	 */
	public PhraseSpec getHeading() {
		return this.heading;
	}

	/**
	 * @return <code>true</code> if the text spec has a heading
	 */
	public boolean hasHeading() {
		return this.heading != null;
	}

	/**
	 * 
	 * @return <code>true</code> if the text spec has children
	 */
	public boolean hasChildren() {
		return !this.children.isEmpty();
	}

	/**
	 * @param heading
	 *            the heading to set (String ot PhraseSpec)
	 */
	public void setHeading(Object heading) {
		this.heading = (PhraseSpec) convertToSpec(heading);
		// don't set heading.parent, as heading isn't really a child
	}

	/**
	 * gets the children of this text spec.
	 * 
	 * @return the children
	 */
	public List<Spec> getChildren() {
		return this.children;
	}

	/**
	 * sets the children of this text spec (replaces existing children).
	 * 
	 * @param children
	 *            the children
	 */
	public void setChildren(List<Spec> children) {
		this.children = children;
	}

	/**
	 * adds a child to this text spec (in addition to existing children).
	 * 
	 * @param spec
	 *            the spec (Spec or String)
	 */
	public void addChild(Object spec) {
		addSpec(spec);
	}

	/**
	 * Remove a child from this text spec.
	 * 
	 * @param child
	 *            the child to remove.
	 */
	public void removeChild(Object child) {
		this.children.remove(child);
	}

	/**
	 * adds a child to this text spec (in addition to existing children).
	 * 
	 * @param spec
	 *            the spec (Spec or String)
	 * 
	 * @see TextSpec#addChild(Object)
	 */
	public void addSpec(Object spec) {

		Spec pspec = convertToSpec(spec);
		pspec.setParentSpec(this);
		this.children.add(pspec);
	}

	/**
	 * convert a String or Spec into a Spec. Strings get converted to
	 * StringPhraseSpec
	 * 
	 * @param spec
	 *            the spec
	 * 
	 * @return the spec, converted into a PhraseSpec if necessary
	 */
	private Spec convertToSpec(Object spec) {

		if (spec instanceof Spec) {
			return (Spec) spec;

		} else if (spec instanceof String) {
			return new StringPhraseSpec((String) spec);

		} else {
			throw new SimplenlgException(
					"addSpec: spec must be a PhraseSpec, TextSpec, or String");
		}
	}

	// realise method *************************************************
	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Spec#realise(simplenlg.realiser.Realiser)
	 */
	@Override
	public String realise(Realiser r) { // realise a TextSpec
		// force sentence and below to be "and" conjuncts,
		// unless another conject is specified or cue phrases are included

		// increase my doc structure so its higher than my children
		this.docStructure = this.docStructure.max(maxDSChildren()
				.lowestParent());

		// if I am higher-than sentence, promote children to sentence-level
		List<Spec> promotedChildren = this.children;

		if (!this.docStructure.isSentenceOrLower()) {
			promotedChildren = new ArrayList<Spec>();
			for (Spec child : this.children) {
				promotedChildren.add(child.promote(DocStructure.SENTENCE));
			}
		}

		// now merge together, with appropriate conjunct
		String result = "";
		if (this.listConjunct != null) {
			result = r.realiseConjunctList(promotedChildren, this.listConjunct);
		} else if (this.docStructure.isSentenceOrLower()) {
			if (!hasCuePhrase()) {
				result = r.realiseAndList(promotedChildren);
			} else {
				result = r.realiseConjunctList(promotedChildren, ",");
			}
		} else if (this.indentedList) {
			result = r.realiseIndentedList(promotedChildren);
		} else {
			result = r.realiseList(promotedChildren);
		}

		if (this.docStructure.isSentence()) {
			char terminator = '.';
			if (this.children != null && this.children.size() > 0) {
				Spec childSpec = this.children.get(0);
				if (childSpec != null && childSpec instanceof SPhraseSpec) {
					if (((SPhraseSpec)(childSpec)).isInterrogative()) {
						terminator = '?';
					}
				}
			}
			result = r.applySentenceOrthography(result, terminator, this.defaultCaps);
		}

		if (this.docStructure.isParagraph()) {
			result = r.applyParagraphOrthography(result);
		}

		// apply markups and orthography
		String header = null;
		if (this.heading != null) {
			header = r.realise(this.heading);
		}

		Formatter formatter = r.getFormatter();

		switch (this.docStructure) {
		case SENTENCE:
			result = r.applySentenceOrthography(result, '.', this.defaultCaps);
			break;
		case PARAGRAPH:
			result = r.applyParagraphOrthography(formatter
					.addParagraphMarkup(result));
			break;
		case SUBSECTION:
			result = formatter.addSubSectionMarkup(result, header);
			break;
		case SECTION:
			result = formatter.addSectionMarkup(result, header);
			break;
		case DOCUMENT:
			result = r.applyDocumentOrthography(formatter.addDocumentMarkup(
					result, header));
			break;
		default:
			break;
		}

		return result;
	}

	// utility methods ******************************

	/**
	 * returns number of children.
	 * 
	 * @return the int
	 */
	public int size() { // number of component specs
		return this.children.size();
	}

	/**
	 * Checks for cue phrase.
	 * 
	 * @return true, if successful
	 */
	private boolean hasCuePhrase() {
		// return T if I or any of my grandchildren is an SPhraseSpec
		// with a cue phrase
		// just sentence-level SPhraseSpec, ignores embedded
		for (Spec spec : this.children) {
			if (spec instanceof SPhraseSpec
					&& ((SPhraseSpec) spec).hasCuePhrase()) {
				return true;
			} else if (spec instanceof TextSpec
					&& ((TextSpec) spec).hasCuePhrase()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Max ds children.
	 * 
	 * @return the doc structure
	 */
	private DocStructure maxDSChildren() {
		// return the highest DS of my children
		DocStructure maxDSvalue = DocStructure.PHRASE;
		for (Spec child : this.children) {
			if (child instanceof TextSpec) {
				maxDSvalue = maxDSvalue.max(((TextSpec) child).maxDS());
			}
		}
		return maxDSvalue;
	}

	/**
	 * Max ds.
	 * 
	 * @return the doc structure
	 */
	private DocStructure maxDS() {
		// return highest DS of node and its children
		return getDocStructure().max(maxDSChildren());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.Spec#promote(simplenlg.realiser.DocStructure)
	 */
	@Override
	public TextSpec promote(DocStructure level) {
		// return a TextSpec with the same content, but at the
		// target level (or higher)
		if (this.docStructure.compareTo(level) >= 0) {
			return this;
		}
		TextSpec newts = new TextSpec(this.docStructure.next(), this);
		return newts.promote(level);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// return String for a text spec
		return toString("", new Realiser()); // call main method with null
		// indent
	}

	/**
	 * To string.
	 * 
	 * @param indent
	 *            the indent
	 * @param r
	 *            the r
	 * 
	 * @return the string
	 */
	private String toString(String indent, Realiser r) {
		String result = indent + this.docStructure + ": ";
		if (this.listConjunct != null) {
			result = result + "TextSpec-List " + this.listConjunct;
		} else {
			result = result + "TextSpec";
		}

		if (this.children.size() == 1
				&& !(this.children.get(0) instanceof TextSpec)) {
			result = result + " -- " + r.realise(this.children.get(0)) + "\n";
		} else {
			result = result + "\n";
			for (Spec child : this.children) {
				if (child instanceof TextSpec) {
					result = result
							+ ((TextSpec) child).toString(indent + "+", r);
				} else {
					result = result + indent + "+" + r.realise(child) + "\n";
				}
			}
		}

		return result;
	}

}
