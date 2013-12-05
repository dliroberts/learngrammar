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
 * Interpretation codes for clausal complements. These specify syntactic
 * properties of a clausal complement type (e.g. whether it is subject-raising).
 * An interpretation code is part of the cluster of features making up a
 * {@link simplenlg.features.ComplementSlot}, which is the main component of a
 * {@link simplenlg.features.ComplementFrame}.
 * 
 * @author agatt
 * @since Version 3.7
 */
public enum InterpretationCode {

	/**
	 * Marks a complement as object control, i.e. the direct object in the
	 * higher clause is logically both the object of the higher verb and the
	 * subject of the embedded (non-finite) clause. Example: <I>I advised him to
	 * go.</I>
	 */
	OBJ_CONTROL,

	/**
	 * Object raising means that the direct object in the higher clause is
	 * logically the subject of the nonfinite clause and not also the logical
	 * object of the higher clause. Example: <I>I believe him to have stolen my
	 * watch.</I>
	 */
	OBJ_RAISING,

	/**
	 * Subject Control means that subject of the higher clause is also the
	 * logical subject of the embedded infinitival clause. Example: <I>John
	 * promised to leap over the wall</I>
	 */
	SUBJ_CONTROL,

	/**
	 * Subject Raising indicates that the subject of the higher clause is the
	 * logical subject of the embedded infinitival clause. Example: <I>John
	 * seems to have criticized Martha.</I>
	 */
	SUBJ_RAISING,

	/**
	 * Arbitrary control indicates that the subject of the lower clause is not
	 * linguistically controlled. Example: <I>I helped write this program</I>.
	 * This implies that <I>I helped <U>someone</U> write this program</I>. The
	 * subjects of the embedded and matrix clause are not (necessarily) the
	 * same.
	 */
	ARB_CONTROL,

	/**
	 * Non-subject control means that the subject of the matrix verb controls a
	 * missing non-subject np in a present participle complement clause.
	 * Example: <I>This code needs cleaning up.</I> Here, the understood object
	 * of <I>cleaning up</I> is <I>this code</I>.
	 */
	NON_SUBJ_CONTROL,

	/**
	 * Indicates that a finite clause allows the complentiser <I>that</I> to be
	 * dropped. Example: <I>I hope (that) he will come.</I>
	 */
	OPT_THAT,

	/**
	 * Indicates that a finite clause obligatorily requires the complentiser
	 * <I>that</I>.
	 */
	REQ_THAT,

	/**
	 * This code represents a finite clause with an optional <I>that</I>
	 * complementizer which may be realized as (the proform) <I>so</I> or
	 * <I>not</I>. Example: <I>I told him (that) his brother was friendly</I>
	 * can become <I>I told him <U>so</U></I>
	 */
	OPT_THAT_PRO,

	/**
	 * This code represents a subjunctive clause with an optional <I>that</I>
	 * complementizer. Example: <I>I suggest (that) you (should) be here</I>
	 */
	SUBJUNCTIVE_OPT_THAT,

	/**
	 * This code represents a subjunctive clause with a required that
	 * complementizer. Example: <I>I require that you (should) be here</I>
	 */
	SUBJUNCTIVE_REQ_THAT,

	/**
	 * Indicates that a finite clause obligatorily requires the complentiser
	 * <I>that</I>, and can be realised using the proform <I>so</I> or
	 * <I>not</I>.
	 */
	REQ_THAT_PRO,

	/**
	 * This code represents a subjunctive clause with an optional <I>that</I>
	 * complementizer, which may be realized as <I>so</I> or <I>not</I>.
	 */
	SUBJUNCTIVE_OPT_THAT_PRO,

	/**
	 * This code represents a finite clause with a required <I>that</I>
	 * complementizer, which may be realized as <I>so</I> or <I>not</I>.
	 */
	SUBJUNCTIVE_REQ_THAT_PRO,

	/**
	 * Indicates that a finite clause complement is an extraposed subject. That
	 * is, it appears postverbally only with a pleonastic <I>it</I> subject.
	 * Example: <I>It appears that John won.</I>
	 */
	EXTRAPOSED_SUBJ;

	/**
	 * Checks whether an <code>InterpretationCode</code> is compatible with a
	 * particular <code>ComplementType</code>, in which case, a complement slot
	 * with that type can have this code as restriction. The codes enumerated
	 * here are only compatible with clausal complements; however, different
	 * types of clausal complements can have different codes, and not all codes
	 * apply to all clausal complement types.
	 * 
	 * <P>
	 * For compatibility between types and codes, see {@link ComplementType}
	 * </P>
	 * 
	 * @param type
	 *            the complement type
	 * 
	 * @return true, if it is compatible
	 */
	public boolean compatibleWith(ComplementType type) {

		switch (this) {
		case OBJ_CONTROL:
		case OBJ_RAISING:
		case SUBJ_CONTROL:
		case SUBJ_RAISING:
		case ARB_CONTROL:
		case NON_SUBJ_CONTROL:
		case EXTRAPOSED_SUBJ:
			return type.isClause;

		case OPT_THAT:
		case REQ_THAT:
		case OPT_THAT_PRO:
		case SUBJUNCTIVE_OPT_THAT:
		case SUBJUNCTIVE_REQ_THAT:
		case REQ_THAT_PRO:
		case SUBJUNCTIVE_REQ_THAT_PRO:
			return type.isClause && type.isFinite;

		default:
			return true;
		}
	}
}