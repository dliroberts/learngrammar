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

// TODO: Auto-generated Javadoc
/**
 * Enumerated type specifying what kind of agreement a nominal (noun or pronoun)
 * has. These values also apply to determiners, in which case they specify the
 * agreement properties a noun must have in order to be specified by a
 * determiner.
 * 
 * <P>
 * Value of this enum apply to pronouns, nouns and determiners. The values of
 * this type are mutually exclusive.
 * 
 * @author agatt
 * @since Version 3.7
 */
public enum Agreement implements Feature {

	/**
	 * Indicates that the nominal is a mass noun, e.g. <I>morphine</I>. Mass
	 * nouns tend not to be pluralised, and always take singular agreement with
	 * the verb phrase, e.g. <I><U>morphine</U> is/was/*were given to the
	 * baby</I>
	 */
	MASS,

	/**
	 * Indicates that the nominal is a count noun, hence whether it takes
	 * singular or plural agreement depends on its inflectional form.
	 */
	COUNT,

	/**
	 * Used to indicate that a nominal is singular, and cannot be inflected for
	 * plural. This is mainly used for pronouns, such as <I>he</I>, but also for
	 * names such as <I>Amsterdam</I>.
	 */
	FIXED_SING,

	/**
	 * Used to indicate that a nominal is always plural. This is mainly used for
	 * pronouns, such as <I>they</I>, but also for other nouns like
	 * <I>Midlands</I>.
	 */
	FIXED_PLUR,

	/**
	 * Specifies a group noun, which can typically take both singular and plural
	 * agreement, for example <I><U>the Academy of General Medicine</U> issued a
	 * statement saying it/they are closing shop.</I>
	 */
	GROUP,

	/**
	 * Applied exclusively to determiners, this value indicates that the
	 * determiner can apply to a noun of any kind (singular, plural, mass, group
	 * or count).
	 */
	FREE;

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.features.Feature#appliesTo(simplenlg.features.Category)
	 */
	public boolean appliesTo(Category c) {
		switch (this) {
		case FREE:
			return c.equals(Category.DETERMINER);
		default:
			return c.equals(Category.NOUN) || c.equals(Category.PRONOUN)
					|| c.equals(Category.DETERMINER);
		}

	}

}
