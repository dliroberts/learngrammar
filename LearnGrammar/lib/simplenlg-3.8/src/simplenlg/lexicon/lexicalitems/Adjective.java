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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import simplenlg.features.AdjectivePosition;
import simplenlg.features.AdjectiveType;
import simplenlg.features.Category;
import simplenlg.features.InflectionType;
import simplenlg.lexicon.morph.AdjectiveInflection;

/**
 * This class extends the {@link simplenlg.lexicon.lexicalitems.ContentWord}
 * abstract class for Adjectives. In addition to their baseform, id, and
 * citation form, an adjective can optionally be specified for:
 * 
 * <OL>
 * <LI>the syntactic positions it can occupy (prenominal, postnominal etc), a
 * subset of the values of {@link simplenlg.features.AdjectivePosition};</LI>
 * <LI>its type, a value of {@link simplenlg.features.AdjectiveType}, indicating
 * whether it's stative or not.</LI>
 * </OL>
 * 
 * @author agatt
 */
public class Adjective extends ContentWord {

	// The superlative and comparative forms.
	private String comparativeForm, superlativeForm;

	// The positions.
	Set<AdjectivePosition> positions;

	// The type.
	AdjectiveType type;

	/**
	 * Instantiates a new adjective with the given baseform.
	 * 
	 * @param baseform
	 *            the baseform
	 */
	public Adjective(String baseform) {
		super(baseform);
		this.category = Category.ADJECTIVE;
		this.positions = new HashSet<AdjectivePosition>();
		this.type = AdjectiveType.NON_STATIVE;
	}

	/**
	 * Instantiates a new adjective with the given id and baseform.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 */
	public Adjective(String id, String baseform) {
		this(baseform);
		setID(id);
	}

	/**
	 * Instantiates a new adjective with an id, baseform and citation form.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 * @param citationform
	 *            the citationform
	 */
	public Adjective(String id, String baseform, String citationform) {
		this(id, baseform);
		setCitationForm(citationform);
	}

	/**
	 * Gets the comparative form of this adjective, if it exists. For example,
	 * if the baseform of this adjective is <I>nice</I>, this returns
	 * <I>nicer</I>.
	 * 
	 * @return the comparative ("er") form if it exists, null otherwise
	 */
	public String getComparative() {

		if (this.comparativeForm == null
				&& this.inflectionType != InflectionType.PERIPHRASTIC) {
			this.comparativeForm = AdjectiveInflection.COMPARATIVE
					.apply(this.baseForm);
		}

		return this.comparativeForm;
	}

	/**
	 * Sets the comparative form of this adjective. Useful to override the
	 * default behaviour from the in-built rules.
	 * 
	 * @param comp
	 *            - The comparative form
	 */
	public void setComparative(String comp) {
		this.comparativeForm = comp;
	}

	/**
	 * Gets the superlative form of this adjective
	 * 
	 * <BR>
	 * 
	 * <strong>Note</strong> This feature is experimental and is not guaranteed
	 * to return correct results. It is expected to improve in future releases.
	 * 
	 * @return the superlative form of this adjective if it exists, null
	 *         otherwise
	 */
	public String getSuperlative() {

		if (this.superlativeForm == null) {
			this.superlativeForm = AdjectiveInflection.SUPERLATIVE
					.apply(this.baseForm);
		}

		return this.superlativeForm;
	}

	/**
	 * Sets the superlative form of this adjective. Useful to override the
	 * default behaviour from the in-built rules.
	 * 
	 * @param sup
	 *            - The superlative form
	 */
	public void setSuperlative(String sup) {
		this.superlativeForm = sup;
	}

	/**
	 * Gets the syntactic positions in which this adjective can occur, if any
	 * have been specified.
	 * 
	 * @return a list of positions, if specified, the empty list otherwise
	 */
	public Collection<AdjectivePosition> getPositions() {
		return this.positions;
	}

	/**
	 * Adds a syntactic position for this adjective.
	 * 
	 * @param position
	 *            the position to be added
	 */
	public void addPosition(AdjectivePosition position) {
		this.positions.add(position);
	}

	/**
	 * Sets the syntactic positions in which this adjective can occur.
	 * 
	 * @param pos
	 *            the positions for this adjective
	 */
	public void setPositions(Collection<AdjectivePosition> pos) {
		this.positions.clear();
		this.positions.addAll(pos);
	}

	/**
	 * Checks if this adjective can occur in a particular syntactic position.
	 * 
	 * @param position
	 *            the position
	 * 
	 * @return <code>true</code> if the adjective has been specified for this
	 *         position.
	 */
	public boolean hasPosition(AdjectivePosition position) {
		return this.positions.contains(position);
	}

	/**
	 * Gets the type of the adjective, which indicates whether it is stative or
	 * not.
	 * 
	 * @return the type, if set, <code>null</code> otherwise
	 */
	public AdjectiveType getType() {
		return this.type;
	}

	/**
	 * Sets the type of this adjective, indicating whether it's stative or not.
	 * 
	 * @param type
	 *            the new type to set for the adjective
	 */
	public void setType(AdjectiveType type) {
		this.type = type;
	}

	/**
	 * Checks if this adjective is stative, that is, whether its type is
	 * {@link simplenlg.features.AdjectiveType#STATIVE}. This method is
	 * equivalent to <code>Adjective.getType() == AdjectiveType.STATIVE</code>
	 * 
	 * @return true, if the adjective is stative
	 */
	public boolean isStative() {
		return this.type == AdjectiveType.STATIVE;
	}

}
