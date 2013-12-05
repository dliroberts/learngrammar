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
import java.util.Iterator;
import java.util.List;

import simplenlg.lexicon.lexicalitems.Conjunction;
import simplenlg.lexicon.lexicalitems.Constants;

// TODO: Auto-generated Javadoc
/**
 * This class represents a collection of the daughter phrases of a
 * {@link simplenlg.realiser.CoordinatePhrase}. The class duplicates the methods
 * specified in the interface, so that method calls to classes implementing
 * <code>CoordinatePhrase</code> are delegated to this class. (This is
 * convenient from the software engineering point of view.)
 * 
 * <P>
 * This class also facilitates feature inheritance from a
 * <code>CoordinatePhrase</code> to its daughters. For example, if a
 * CoordinateVerbPhrase is set to the past tense, then its components (daughter
 * VPs) will also be in the past. This kind of feature inheritance is also
 * delegated to the <code>CoordinatePhraseSet</code> which extends the
 * <code>java.beans.PropertyChangeSupport</code> and provides a method for
 * propagating simplenlg.features to phrase daughters.
 * 
 * <P>
 * Every class that implements the <code>CoordinatePhrase</code> interface in
 * the {@link simplenlg.realiser} package holds its coordinates in a
 * <code>CoordinatePhraseSet</code>
 * 
 * <P>
 * Users who use the {@link simplenlg.realiser} api will not need to deal
 * directly with this class, since implementations of
 * <code>CoordinatePhrase</code> are provided for all the major categories.
 * 
 * @param <T>
 *            The type of the daughter phrases of the
 *            <code>CoordinatePhrase</code> of which this
 *            <code>CoordinatePhraseSet</code> is a component.
 * 
 * @author agatt
 */
public class CoordinatePhraseSet<T extends Phrase> {

	/** The coordinates. */
	List<T> coordinates;

	/** The conjunction. */
	Conjunction conjunction;

	/** The parent phrase. */
	Phrase parentPhrase;

	/**
	 * Constructs an instance of a <code>CoordinatePhraseSet</code>.
	 * 
	 * @param parent
	 *            The parent {@link simplenlg.realiser.CoordinatePhrase} which
	 *            instantiates the <code>CoordinatePhraseSet</code>.
	 */
	public CoordinatePhraseSet(T parent) {
		this.coordinates = new ArrayList<T>();
		this.conjunction = Constants.AND;
		this.parentPhrase = parent;
	}

	/**
	 * Constructs an instance of a <code>CoordinatePhraseSet</code>.
	 * 
	 * @param parent
	 *            The parent {@link simplenlg.realiser.CoordinatePhrase} which
	 *            instantiates the <code>CoordinatePhraseSet</code>.
	 * @param coords
	 *            The coordinates which are to be held in this phrase set.
	 */
	public CoordinatePhraseSet(T parent, T... coords) {
		this(parent);
		addCoordinates(coords);
	}

	/**
	 * Gets the coordinates.
	 * 
	 * @return The list of coordinates
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#getCoordinates()
	 */
	public List<T> getCoordinates() {
		return this.coordinates;
	}

	/**
	 * Gets the coordinate.
	 * 
	 * @param index
	 *            the index
	 * 
	 * @return Returns the coordinate at the specified index if it exists, null
	 *         otherwise.
	 */
	public T getCoordinate(int index) {
		Iterator<T> iter = this.coordinates.iterator();
		int i = -1;
		T result = null;

		while (iter.hasNext() && i <= index) {
			i++;

			if (i == index) {
				result = iter.next();
			}
		}

		return result;
	}

	/**
	 * Check whether this set contains any phrases.
	 * 
	 * @return <code>true</code> if the set contains at least one coordinate
	 */
	public boolean hasCoordinates() {
		return !this.coordinates.isEmpty();
	}

	/**
	 * Removes all the coordinates in this <code>CoordinatePhraseSet</code>.
	 */
	public void clearCoordinates() {
		this.coordinates.clear();
	}

	/**
	 * Sets the coordinates.
	 * 
	 * @param coords
	 *            the coords
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#setCoordinates(Phrase...)
	 */
	public void setCoordinates(T... coords) {
		this.coordinates.clear();
		addCoordinates(coords);
	}

	/**
	 * Adds the coordinates.
	 * 
	 * @param coords
	 *            the coords
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#addCoordinates(Phrase...)
	 */
	public void addCoordinates(T... coords) {

		for (T coord : coords) {
			//if (!this.coordinates.contains(coord)) {
				((Spec) coord).setParentSpec((Spec) this.parentPhrase);
				this.coordinates.add(coord);
			//}
		}
	}

	/**
	 * Adds the coordinates.
	 * 
	 * @param coords
	 *            the coords
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#addCoordinates(Phrase...)
	 */
	public void addCoordinates(List<T> coords) {

		for (T coord : coords) {
			if (!this.coordinates.contains(coord)) {
				((Spec) coord).setParentSpec((Spec) this.parentPhrase);
				this.coordinates.add(coord);
			}
		}
	}

	/**
	 * Sets the conjunction.
	 * 
	 * @param c
	 *            the c
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#setConjunction(Conjunction)
	 */
	public void setConjunction(Conjunction c) {
		this.conjunction = c;
	}

	/**
	 * Sets the conjunction.
	 * 
	 * @param c
	 *            the c
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#setConjunction(String)
	 */
	public void setConjunction(String c) {
		this.conjunction = Constants.getConjunction(c);

		if (this.conjunction == null) {
			this.conjunction = new Conjunction(c);
		}
	}

	/**
	 * Gets the conjunction.
	 * 
	 * @return The conjunction
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#getConjunction()
	 */
	public Conjunction getConjunction() {
		return this.conjunction;
	}

	/**
	 * Gets the conjunction as string.
	 * 
	 * @return The conjunction as a string
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#getConjunctionAsString()
	 */
	public String getConjunctionAsString() {
		return this.conjunction.getBaseForm();
	}

	/**
	 * Gets the coordinate count.
	 * 
	 * @return The number of coordinates in this
	 *         <code>CoordinatePhraseSet</code>
	 */
	public int getCoordinateCount() {
		return this.coordinates.size();
	}

	@Override
	public boolean equals(Object o) {
		boolean eq = false;

		if (o instanceof CoordinatePhraseSet) {
			CoordinatePhraseSet<?> c = (CoordinatePhraseSet<?>) o;
			eq = c.conjunction.equals(this.conjunction)
					&& c.coordinates.equals(this.coordinates);
		}

		return eq;
	}

	/**
	 * Realise.
	 * 
	 * @param r
	 *            the r
	 * 
	 * @return the string
	 */
	String realise(Realiser r) {
		return r.realiseConjunctList(getCoordinates(), this.conjunction
				.getBaseForm());
	}
}
