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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc
/**
 * The Class PatternActionRule.
 */
public class PatternActionRule implements Comparable<PatternActionRule> {

	/** The Constant EXCEPTION. */
	public static final int EXCEPTION = 0;

	/** The Constant GENERIC. */
	public static final int GENERIC = 1;

	/** The Constant DEFAULT. */
	public static final int DEFAULT = 2;

	/** The left hand side. */
	private Matcher leftHandSide;

	/** The left hand string. */
	private String leftHandString;

	/** The offset. */
	private int offset;

	/** The suffix. */
	private String suffix;

	/** The type. */
	private Integer type = PatternActionRule.EXCEPTION;

	/**
	 * Instantiates a new pattern action rule.
	 * 
	 * @param regex
	 *            the regex
	 * @param truncate
	 *            the truncate
	 * @param suff
	 *            the suff
	 */
	public PatternActionRule(String regex, int truncate, String suff) {
		this.leftHandSide = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
				.matcher("\\w+");
		this.leftHandString = regex;
		this.offset = truncate;
		this.suffix = suff;
	}

	/**
	 * Instantiates a new pattern action rule.
	 * 
	 * @param regex
	 *            the regex
	 * @param truncate
	 *            the truncate
	 * @param suff
	 *            the suff
	 * @param newType
	 *            the new type
	 */
	public PatternActionRule(String regex, int truncate, String suff,
			int newType) {

		if (newType == PatternActionRule.EXCEPTION
				|| newType == PatternActionRule.DEFAULT
				|| newType == PatternActionRule.GENERIC) {
			this.type = newType;
		} else {
			throw new IllegalArgumentException(
					"Type of PatternActionRule is 0 (Exception), "
							+ "1 (Generic) or 2 (Default)");
		}

		// set the leftHandSide to a matcher for "word". To be reset
		// every time the rule is applied. Saves garbage from creation
		// of new matchers each time
		this.leftHandSide = Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
				.matcher("word");
		this.leftHandString = regex;
		this.offset = truncate;
		this.suffix = suff;
	}

	/**
	 * Sets the type.
	 * 
	 * @param newType
	 *            the new type
	 */
	public void setType(int newType) {

		if (newType == PatternActionRule.EXCEPTION
				|| this.type == PatternActionRule.DEFAULT
				|| this.type == PatternActionRule.GENERIC) {
			this.type = newType;
		} else {
			throw new IllegalArgumentException(
					"Type of PatternActionRule is 0 (Exception), "
							+ "1 (Generic) or 2 (Default)");
		}
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Gets the left hand side.
	 * 
	 * @return the left hand side
	 */
	public String getLeftHandSide() {
		return this.leftHandString;
	}

	/**
	 * Applies.
	 * 
	 * @param word
	 *            the word
	 * 
	 * @return true, if successful
	 */
	public boolean applies(String word) {
		word = word.trim();
		this.leftHandSide = this.leftHandSide.reset(word);
		return this.leftHandSide.find();
	}

	/**
	 * Fire.
	 * 
	 * @param word
	 *            the word
	 * 
	 * @return the string
	 */
	public String fire(String word) {
		word = word.trim();
		return truncate(word) + this.suffix;
	}

	/**
	 * Analyse.
	 * 
	 * @param word
	 *            the word
	 * 
	 * @return true, if successful
	 */
	public boolean analyse(String word) {

		if (this.suffix != "" && word.endsWith(this.suffix)) {
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(PatternActionRule rule) {
		return this.type.compareTo(rule.getType());
	}

	/**
	 * Truncate.
	 * 
	 * @param word
	 *            the word
	 * 
	 * @return the string
	 */
	private String truncate(String word) {

		if (this.offset == 0) {
			return word;
		}

		StringBuffer buffer = new StringBuffer(word);
		int i = 1;
		while (i <= this.offset) {
			buffer.deleteCharAt(buffer.length() - 1);
			i++;
		}

		return buffer.toString();
	}

}
