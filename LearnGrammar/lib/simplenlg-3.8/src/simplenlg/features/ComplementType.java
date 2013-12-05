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
 * Enumerated type classifying the different types of complements. These types
 * are part of the cluster of features making up a
 * {@link simplenlg.features.ComplementSlot}, the basic unit for defining a
 * {@link simplenlg.features.ComplementFrame}.
 * 
 * @author agatt
 * @since Version 3.7
 */
public enum ComplementType {

	/**
	 * Infinitive clause complements, e.g. <I>John advised us <U>to wear a
	 * hat</U></I>
	 */
	INF(true, false, false),

	/** Bare infinitives, e.g. <I>I helped John <U>write a book</U></I>. */
	BARE_INF(true, false, false),

	/**
	 * Present participle complements, e.g. <I>I like <U>singing (stupid
	 * songs)</U></I>
	 */
	ING(true, false, true),

	/**
	 * Past participle complements, e.g. <I>He wanted a new house
	 * <U>built</U></I>
	 */
	ED(true, false, true),

	/** Finite clause complement, e.g. <I>He said <U>he wanted a car</U></I> */
	FINCOMP(true, false, true),

	/**
	 * WH finite complements, covering clauses with <I>why, whether, where,
	 * who</I> and <I>if</I>, e.g <I>he asked <U>whether pigs can fly</U></I>
	 */
	WHFINCOMP(true, true, true),

	/**
	 * WH-infinitive complements, e.g. <I>I don't know <U>whether to send the
	 * money</U></I>
	 */
	WHINFCOMP(true, true, false),

	/**
	 * `As' absolute clauses, e.g. <I>We viewed the matter as <U>(being) well
	 * worth investigating</U></I>
	 */
	AS_COMP(true, false, true),

	/**
	 * Prepositional phrase complements, e.g. <I>He fell <U>into a deep
	 * depression</U></I>
	 */
	PP(false, false, false),

	/** Noun phrase complement, e.g. <I>John kissed <U>his cat</U></I> */
	NP(false, false, false),

	/**
	 * Adjectival phrase complement, e.g. <I>John considered Mary
	 * <U>reckless</U></I>, <I>I laughed myself <U>silly</U></I>
	 */
	ADJP(false, false, false),

	/**
	 * Adverbial phrase complement, including prepositional sentential
	 * adverbials, e.g. <I>He showed Peter <U>to the door</U></I>
	 */
	ADVP(false, false, false);

	// true if this is a clause complement type
	boolean isClause;

	// true if this is a wh clause complement type
	boolean isWh;

	// true if this is a finite clause
	boolean isFinite;

	/*
	 * Constructor, specifying some boolean values
	 */
	ComplementType(boolean isClause, boolean isWh, boolean finite) {
		this.isClause = isClause;
		this.isWh = isWh;
		this.isFinite = finite;

	}

	/**
	 * Check whether this complement type is a clause type.
	 * 
	 * @return <code>true</code> if this complement type is not <code>PP</code>
	 *         <code>ADJP</code>, <code>NP</code> or <code>ADVP</code>.
	 */
	public boolean isClause() {
		return this.isClause;
	}

	/**
	 * Check whether this complement type is a wh-clause.
	 * 
	 * @return <code>true</code> if {@link #isClause()} is <code>true</code>,
	 *         and this type either <code>WHFINCOMP</code> or
	 *         <code>WHINFCOMP</code>
	 */
	public boolean isWH() {
		return this.isWh;
	}
}