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

import simplenlg.features.AdverbPosition;
import simplenlg.features.AdverbType;
import simplenlg.features.Category;
import simplenlg.features.Polarity;

/**
 * This class extends the {@link simplenlg.lexicon.lexicalitems.ContentWord}
 * abstract class for Adverbs. In addition to their baseform, id, and citation
 * form, an adverb is optionally specified for:
 * <OL>
 * <LI>its possible positions as a modifier (sentential, verbal etc), a subset
 * of the values of {@link simplenlg.features.AdverbPosition};</LI>
 * <LI>its semantic types (manner, temporal etc), a subset of the values of
 * {@link simplenlg.features.AdverbType};</LI>
 * <LI>its polarity, a feature of {@link simplenlg.features.Polarity}. Note
 * that, unless specified, this value is
 * {@link simplenlg.features.Polarity#POSITIVE} by default.</LI>
 * 
 * 
 * @author agatt
 */
public class Adverb extends ContentWord {

	// syntactic positions
	Set<AdverbPosition> positions;

	// adverb types
	Set<AdverbType> adverbTypes;

	// polarity
	Polarity polarity;

	/**
	 * Instantiates a new adverb with the given baseform.
	 * 
	 * @param baseform
	 *            the baseform
	 */
	public Adverb(String baseform) {
		super(baseform);
		this.positions = new HashSet<AdverbPosition>();
		this.adverbTypes = new HashSet<AdverbType>();
		this.category = Category.ADVERB;
		this.polarity = Polarity.POSITIVE;
	}

	/**
	 * Instantiates a new adverb with the given id and baseform.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 */
	public Adverb(String id, String baseform) {
		this(baseform);
		setID(id);
	}

	/**
	 * Instantiates a new adverb with the given id, baseform and citation form.
	 * 
	 * @param id
	 *            the id
	 * @param baseform
	 *            the baseform
	 * @param citationform
	 *            the citationform
	 */
	public Adverb(String id, String baseform, String citationform) {
		this(id, baseform);
		setCitationForm(citationform);
	}

	/**
	 * Gets the syntactic positions in which this adverb can occur.
	 * 
	 * @return the positions
	 */
	public Collection<AdverbPosition> getPositions() {
		return this.positions;
	}

	/**
	 * Sets the syntactic positions in which this adverb can occur.
	 * 
	 * @param pos
	 *            the positions for this adverb
	 */
	public void setPositions(Collection<AdverbPosition> pos) {
		this.positions.clear();
		this.positions.addAll(pos);
	}

	/**
	 * Adds a syntactic position in which this adverb can occur.
	 * 
	 * @param pos
	 *            the position
	 */
	public void addPosition(AdverbPosition pos) {
		this.positions.add(pos);
	}

	/**
	 * Checks if this adverb can occur in the specified position.
	 * 
	 * @param position
	 *            the position
	 * 
	 * @return <code>true</code>, if the position has been specified for this
	 *         adverb.
	 */
	public boolean hasPosition(AdverbPosition position) {
		return this.positions.contains(position);
	}

	/**
	 * Gets the types for which this adverb is specified.
	 * 
	 * @return the adverb types
	 */
	public Collection<AdverbType> getAdverbTypes() {
		return this.adverbTypes;
	}

	/**
	 * Sets the adverb types for this adverb.
	 * 
	 * @param types
	 *            the new adverb types
	 */
	public void setAdverbTypes(Collection<AdverbType> types) {
		this.adverbTypes.clear();
		this.adverbTypes.addAll(types);
	}

	/**
	 * Adds a type for this adverb.
	 * 
	 * @param type
	 *            the type
	 */
	public void addAdverbType(AdverbType type) {
		this.adverbTypes.add(type);
	}

	/**
	 * Checks if this adverb has been specified for this type.
	 * 
	 * @param type
	 *            the type
	 * 
	 * @return <code>true</code>, if this adverb has been specified as being of
	 *         this type.
	 */
	public boolean hasType(AdverbType type) {
		return this.adverbTypes.contains(type);
	}

	/**
	 * Gets the polarity for this adverb. This is positive by default, unless
	 * otherwise specified.
	 * 
	 * @return the polarity
	 */
	public Polarity getPolarity() {
		return this.polarity;
	}

	/**
	 * Sets the polarity of this adverb, overriding the default positive value.
	 * 
	 * @param polarity
	 *            the new polarity
	 */
	public void setPolarity(Polarity polarity) {
		this.polarity = polarity;
	}

}
