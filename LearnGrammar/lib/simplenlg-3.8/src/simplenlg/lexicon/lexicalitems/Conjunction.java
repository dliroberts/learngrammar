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

package simplenlg.lexicon.lexicalitems;

import simplenlg.features.Category;
import simplenlg.features.ConjunctionType;
import simplenlg.lexicon.LexiconInterface;

/**
 * Class representing conjunctions. In addition to an id, baseform and citation
 * form, conjunctions can be specified for two pieces of information:
 * <OL>
 * <LI>A {@link simplenlg.features.ConjunctionType}, specifying whether the
 * conjunction is coordinating or subordinating. By default, the conjunction has
 * the type {@link simplenlg.features.ConjunctionType#COORDINATING}</LI>
 * <LI>A category restriction, specifying that the conjunction can only be used
 * to conjoin phrases of a certain category. For example, the subordinator
 * <I>because</I> is used for sentences. By default, a conjunction is
 * unrestricted, i.e. it has the restriction
 * {@link simplenlg.features.Category#ANY}.</LI>
 * </OL>
 * 
 * Both these features are optionally specified.
 */
public class Conjunction extends Word {

	// the type of a particular conjunction
	private ConjunctionType conjType;

	// The category restriction, if any
	private Category categoryRestriction;

	/**
	 * Instantiates a new conjunction with the given baseform.
	 * 
	 * @param baseform
	 *            the baseform
	 */
	public Conjunction(String baseform) {
		super(baseform);
		setCitationForm(baseform);
		this.category = Category.CONJUNCTION;
		this.conjType = ConjunctionType.COORDINATING;
		this.categoryRestriction = Category.ANY;
	}

	/**
	 * Instantiates a new conjunction with the given id and baseform.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 */
	public Conjunction(String id, String baseform) {
		this(baseform);
		setID(id);
	}

	/**
	 * Instantiates a new conjunction with the given id, baseform and citation
	 * form.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 * @param citationform
	 *            the citationform
	 */
	public Conjunction(String id, String baseform, String citationform) {
		this(id, baseform);
		setCitationForm(citationform);
	}

	/**
	 * Instantiates a new conjunction with the given baseform and a conjunction
	 * type
	 * 
	 * @param form
	 *            the baseform
	 * @param t
	 *            the conjunction type
	 */
	public Conjunction(String form, ConjunctionType t) {
		this(form);
		this.conjType = t;
	}

	/**
	 * Instantiates a new conjunction, specifying the baseform and the parent
	 * lexicon to which this item belongs.
	 * 
	 * @param form
	 *            the form
	 * @param parentLexicon
	 *            the parent lexicon
	 * @deprecated As of Version 3.7, the parent lexicon should be set directly
	 *             using {@link #setParentLexicon(LexiconInterface)}
	 */
	@Deprecated
	public Conjunction(String form, LexiconInterface parentLexicon) {
		super(form, parentLexicon);
		this.category = Category.CONJUNCTION;
		this.conjType = ConjunctionType.COORDINATING;
	}

	/**
	 * Gets the conjunction type (coordinating or subordinating).
	 * 
	 * @return the conjunction type, if any has been specified,
	 *         <code>null</code> otherwise
	 */
	public ConjunctionType getConjType() {
		return this.conjType;
	}

	/**
	 * Sets the conjunction type (coordinating or subordinating).
	 * 
	 * @param conjType
	 *            the new conjunction type
	 */
	public void setConjType(ConjunctionType conjType) {
		this.conjType = conjType;
	}

	/**
	 * Gets the category restriction. By default, this is
	 * {@link simplenlg.features.Category#ANY}, that is, the conjunction can be
	 * used with phrases of any category.
	 * 
	 * @return the category restriction
	 */
	public Category getCategoryRestriction() {
		return this.categoryRestriction;
	}

	/**
	 * Sets the category restriction for this conjunction.
	 * 
	 * @param categoryRestriction
	 *            the new category restriction
	 */
	public void setCategoryRestriction(Category categoryRestriction) {
		this.categoryRestriction = categoryRestriction;
	}

	/**
	 * Check whether this conjunction can be used to conjoin two phrases of the
	 * given category.
	 * 
	 * @param cat
	 *            the cat
	 * 
	 * @return <code>true</code>, if the category restriction of this
	 *         conjunction is <code>Category.ANY</code>, or the category
	 *         restriction is the category passed as parameter.
	 */
	public boolean canConjoin(Category cat) {
		return this.categoryRestriction.equals(Category.ANY)
				|| cat.equals(this.categoryRestriction);
	}

	/**
	 * Get the <code>Conjunction</code> matching a <code>String</code>.
	 * 
	 * @param s
	 *            The <code>String</code>
	 * 
	 * @return The conjunction whose baseform is <code>s</code>, if one is
	 *         defined in this class, <code>null</code> otherwise.
	 * @deprecated As of release 3.7. Applications should retrieve conjunctions
	 *             from a lexicon.
	 * @deprecated Use {@link Constants#getConjunction(String)} instead
	 */
	@Deprecated
	public static Conjunction getConjunction(String s) {
		return Constants.getConjunction(s);
	}

	/**
	 * Checks if this is a coordinating conjunction. The method is equivalent to
	 * calling
	 * <code>Conjunction.getType() == ConjunctionType.COORDINATING</code>
	 * 
	 * @return <code>true</code> if this is a Coordinating conjunction (such as
	 *         "and")
	 */
	public boolean isCoordinating() {
		return this.conjType == ConjunctionType.COORDINATING;
	}

	/**
	 * Checks if this is a subordinating conjunction. The method is equivalent
	 * to calling
	 * <code>Conjunction.getType() == ConjunctionType.SUBORDINATING</code>
	 * 
	 * @return <code>true</code> if this is a Subordinating conjunction (such as
	 *         "but" and "because")
	 */
	public boolean isSubordinating() {
		return this.conjType == ConjunctionType.SUBORDINATING;
	}

}
