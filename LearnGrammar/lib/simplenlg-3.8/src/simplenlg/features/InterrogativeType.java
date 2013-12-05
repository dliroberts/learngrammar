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
 * Enumerated type listing the kinds of questions that are handled by Simplenlg.
 * Values of this type are passed to {@link simplenlg.realiser.SPhraseSpec}
 * whenever a sentence needs to be rendered as a question.
 * 
 * <P>
 * In wh-questions with <I>what</I>, <I>who</I> and <I>where</I>, the wh-pronoun
 * replaces one of the arguments (subject, object or indirect object).
 * </P>
 * 
 * @see simplenlg.realiser.SPhraseSpec#setInterrogative(InterrogativeType)
 * @see simplenlg.realiser.SPhraseSpec#setInterrogative(InterrogativeType,
 *      DiscourseFunction)
 * 
 * @author agatt
 * @since Version 3.6
 */
public enum InterrogativeType implements Feature {

	/**
	 * Simple yes/no questions. Rendering a declarative sentence as a yes/no
	 * question usually involves fronting the auxiliary verb (if there is one)
	 * or <I>do</I> if there is no auxiliary. For copular and existential
	 * sentences without an auxiliary, it is the main verb that is fronted (e.g.
	 * <I>John is a professor</I> becomes <I>Is John a professor</I>).
	 */
	YES_NO,

	/**
	 * The type of <I>what</I>-questions. A sentence such as <I>John ate the
	 * cake</I> becomes <I>What did John eat?</I>
	 */
	WHAT,

	/**
	 * The type of <I>where</I>-questions. e.g. <I>John went to the beach</I>
	 * becomes <I>Where did John go?</I>
	 */
	WHERE,

	/**
	 * The type of <I>who</I>-questions. E.g. <I>John kissed Mary</I> can be
	 * rendered as <I>Who did John kiss?</I> (object) or <I>Who kissed Mary?</I>
	 * (subject)
	 */
	WHO,

	/**
	 * The type of <I>how</I>-questions. E.g. <I>John kissed Mary</I> becomes
	 * <I>How did John kiss Mary?</I>
	 */
	HOW,

	/**
	 * The type of <I>why</I>-questions. E.g. <I>John kissed Mary</I> becomes
	 * <I>Why did John kiss Mary?</I>
	 */
	WHY;

	/**
	 * {@inheritDoc}
	 */
	public boolean appliesTo(Category c) {
		return c.equals(Category.CLAUSE);
	}

	/**
	 * Check whether this is a WH question where the wh pronoun stands in for
	 * one of the arguments (i.e. who/what/where).
	 * 
	 * @return <code>True</code> if this is {@link #WHO}, {@link #WHAT} or
	 *         {@link #WHERE}
	 */
	public boolean isWhQuestion() {
		return equals(WHO) || equals(WHAT) || equals(WHERE);
	}

}
