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

import java.util.List;

import simplenlg.features.NumberAgr;
import simplenlg.features.Person;
import simplenlg.lexicon.lexicalitems.Conjunction;

// TODO: Auto-generated Javadoc
/**
 * This class represents a coordinate Noun Phrase consisting of:
 * <ul>
 * <li>An arbitrary number of coordinates</li>
 * <li>A {@link simplenlg.lexicon.lexicalitems.Conjunction}. The default is
 * "and"</li>
 * </ul>
 * 
 * <p>
 * Various parameters can be set to determine how the phrase is realised. For
 * example, whether the phrase has one wide-scope specifier (e.g. <i>John's son
 * and daughter</i>) or several narrow-scope specifiers (<i>John's son and
 * John's daughter</i>). Since this class extends
 * {@link simplenlg.realiser.NPPhraseSpec}, it is also possible to set pre- and
 * post-modifiers, and complements for the coordinate NP itself.
 * 
 * <p>
 * Examples:
 * <ul>
 * <li><strong>The man, the boy and the girl</strong> were in the park</li>
 * <li><strong>Every woman and her little sister</strong> went to the meeting</li>
 * <li><strong>The tall fat man and thin woman</strong> are here</li>
 * </ul>
 * 
 * <p>
 * By default, all the child NPs of a coordinate NP are realised with their own
 * specifier. However, if the specifier of the
 * <code>CoordinateNPPhraseSpec</code> is set, then all daughter NPs will have
 * their specifier unset.
 * 
 * @see simplenlg.realiser.NPPhraseSpec
 * @author agatt
 */
public class CoordinateNPPhraseSpec extends NPPhraseSpec implements
		CoordinatePhrase<NPPhraseSpec> {

	/** The coordinator. */
	private CoordinatePhraseSet<NPPhraseSpec> coordinator;

	/** The contains pronoun. */
	boolean containsPronoun; // flags if one of the coords is a pron

	/** The wide scope specifier. */
	boolean wideScopeSpecifier; // flags if specifier should have wide scope

	/**
	 * Constructs a new <code>CoordinateNPPhraseSpec</code> with the given set
	 * of <code>NPPhraseSpec</code> children.
	 * 
	 * @param coords
	 *            The daughter NPs of this Coordinate NP
	 */
	public CoordinateNPPhraseSpec(NPPhraseSpec... coords) {
		super();
		this.containsPronoun = false;
		this.wideScopeSpecifier = false;
		this.coordinator = new CoordinatePhraseSet<NPPhraseSpec>(this, coords);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.CoordinatePhrase#setConjunction(simplenlg.lexicon.
	 * lexicalitems.Conjunction)
	 */
	public void setConjunction(Conjunction coord) {
		this.coordinator.setConjunction(coord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#setConjunction(java.lang.String)
	 */
	public void setConjunction(String coord) {
		this.coordinator.setConjunction(coord);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#getConjunction()
	 */
	public Conjunction getConjunction() {
		return this.coordinator.getConjunction();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#getConjunctionAsString()
	 */
	public String getConjunctionAsString() {
		return this.coordinator.getConjunctionAsString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#addCoordinates(T[])
	 */
	public void addCoordinates(NPPhraseSpec... coords) {
		this.coordinator.addCoordinates(coords);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#setCoordinates(T[])
	 */
	public void setCoordinates(NPPhraseSpec... coords) {
		this.coordinator.clearCoordinates();
		addCoordinates(coords);
	}

	/**
	 * Gets the coordinates.
	 * 
	 * @return The <code>java.util.List</code> of {@link NPPhraseSpec} children
	 *         of this <code>CoordinateNPPhraseSpec</code>.
	 */
	public List<NPPhraseSpec> getCoordinates() {
		return this.coordinator.getCoordinates();
	}

	/**
	 * Overrides {@link simplenlg.realiser.NPPhraseSpec#setSpecifier(Object)}.
	 * Setting the specifier of a <code>CoordinateNPPhraseSpec</code> has the
	 * following effect on the realisation:
	 * 
	 * <OL>
	 * <LI>If none of the coordinates is pronominal, then the specifier is by
	 * default inherited by the daughter NPs. For example, if the coordinates in
	 * the phrase are <I>the boy</I> and <I>the cat</I>, setting the specifier
	 * using this method, and passing it <I>a</I> will result in the phrase <I>a
	 * boy and a cat</I>. u
	 * 
	 * 
	 * is set, its daughter noun phrases will automatically have their specifier
	 * unset. This avoids anomalies such as <i>[the [the man] and [the
	 * woman]]</i>, arising from having a wide-scope determiner plus inner
	 * determiners.
	 * 
	 * <p>
	 * There is one case where setting the specifier for a coordinate NP results
	 * in a false value, and that's when one of the coordinates is a pronominal
	 * NP. This avoids anomalies such as '[the [it] and [woman]]'
	 * </p>
	 * 
	 * @param spec
	 *            the spec
	 * 
	 * @return <code>true</code> if the specifier has been set.
	 * 
	 * @see simplenlg.realiser.NPPhraseSpec#setSpecifier(Object)
	 */
	@Override
	public boolean setSpecifier(Object spec) {
		return super.setSpecifier(spec);
	}

	/**
	 * Override the default realisation of coordinate NPs, causing the specifier
	 * of the <code>CoordinateNPPhraseSpec</code> to be realised with scope over
	 * the entire set of coordinates (which have their own specifiers, if any,
	 * suppressed).
	 * 
	 * <P>
	 * This method will return <code>false</code> unless the specifier of the
	 * phrase has previously been set using {@link #setSpecifier(Object)}.
	 * 
	 * <P>
	 * Note that, if one of the coordinates is a pronoun, then this method
	 * cannot be used to override the default behaviour of having the specifier
	 * inherited by all non-pronominal daughter NPs. This is in order to avoid
	 * anomalies such as <I>the he, boy, and woman</I>.
	 * 
	 * @param wideScope
	 *            Whether the specifier of this coordinate should be realised
	 *            with wide scope.
	 * 
	 * @return <code>true</code> if the specifier has been previously set.
	 */
	public boolean setWideScopeSpecifier(boolean wideScope) {

		if (this.specifier != null) {
			this.wideScopeSpecifier = wideScope;
			return true;

		} else {
			return false;
		}

	}

	/**
	 * Raise the specifier of the inner coordinates to have wide scope over all
	 * coordinates. For example, given <i>the dog and the cat</i>, this turns
	 * the phrase into: <i>the dog and cat</i>
	 * 
	 * <p>
	 * This method will only work if all internal coordinates have the same
	 * specifier. For instance nothing will change by calling this method on
	 * <i>the dog and every cat</i>. In this case, one should use
	 * {@link simplenlg.realiser.CoordinateNPPhraseSpec#setSpecifier(Object)}
	 * </p>
	 * 
	 * @return <code>true</code> if the specifier has been raised successfully,
	 *         specifically, if all daughter NPs of this phrase (a) have a
	 *         non-null specifier and (b) have the same specifier.
	 */
	public boolean raiseSpecifier() {
		List<NPPhraseSpec> coords = this.coordinator.getCoordinates();

		Object spec = coords.get(0).getSpecifier();

		if (spec == null) {
			return false;
		}

		// check that all nps have the same spec
		for (int i = 1; i < this.coordinator.coordinates.size(); i++) {
			NPPhraseSpec np = coords.get(i);
			Object nextSpec = np.getSpecifier();

			if (nextSpec == null || !nextSpec.equals(spec)) {
				return false;
			}
		}

		return setSpecifier(spec) && setWideScopeSpecifier(true);
	}

	/**
	 * This method always returns {@link simplenlg.features.Person#THIRD} since
	 * a coordinate NP always triggers third-person agreement with the verb.
	 * 
	 * @return The {@link simplenlg.features.Person} of this phrase.
	 */
	@Override
	public Person getPerson() {
		return Person.THIRD;
	}

	// ****************************************************************************
	// REALISATION
	// ****************************************************************************

	/*
	 * Utility method: Used to check if new coordinates a pronominal. Called at
	 * realisation stage.
	 */
	/**
	 * Check pronoun.
	 * 
	 * @param coords
	 *            the coords
	 */
	private void checkPronoun(NPPhraseSpec... coords) {
		this.containsPronoun = false;

		for (NPPhraseSpec np : this.coordinator.coordinates) {
			if (np.isPronominal()) {
				this.containsPronoun = true;
				break;
			}
		}
	}

	/*
	 * calculate the number of a coordinated NP: called at realisation stage
	 */
	/**
	 * Calculate number.
	 */
	private void calculateNumber() {
		// if only one constituent, use its number
		if (this.coordinator.getCoordinateCount() == 1) {
			setNumber(this.coordinator.getCoordinate(0).getNumber());
		} else if (!this.coordinator.getConjunction().getBaseForm()
				.equalsIgnoreCase("or")) {
			setNumber(NumberAgr.PLURAL);
		} else {
			NumberAgr number = NumberAgr.SINGULAR;

			for (NPPhraseSpec np : this.coordinator.getCoordinates()) {
				if (np.isPlural()) {
					number = NumberAgr.PLURAL;
					break;
				}
			}

			setNumber(number);

		}
	}

	/**
	 * Check possessive.
	 */
	private void checkPossessive() {

		// if this hasn;t been set to poss, nothing to be done
		if (!this.possessive) {
			return;
		}

		if (this.containsPronoun) {
			for (NPPhraseSpec child : this.coordinator.coordinates) {
				child.setPossessive(true);
				this.possessive = false;
			}
		} else {
			for (NPPhraseSpec child : this.coordinator.coordinates) {
				child.setPossessive(false);
			}
		}
	}

	/*
	 * compute the specifiers of this coordinate np and its daughters: - if none
	 * of the daughters is a pronoun, then the specifier, if set, is wide-scope
	 * (the man and boy) - if at least one of the daughters is a pronoun, then
	 * the specifier, if set, is narrow scope (he and the boy)
	 * 
	 * This method should only be called at realisation stage.
	 */
	/**
	 * Check specifiers.
	 */
	void checkSpecifiers() {
		if (this.specifier != null) {
			if (this.wideScopeSpecifier && !this.containsPronoun) {
				setChildSpecifier(null);
			} else {
				setChildSpecifier(this.specifier);
				this.wideScopeSpecifier = false;
			}
		}
	}

	/*
	 * Sets the specifier of the child coordinates
	 */
	/**
	 * Sets the child specifier.
	 * 
	 * @param spec
	 *            the new child specifier
	 */
	private void setChildSpecifier(Object spec) {

		for (NPPhraseSpec child : this.coordinator.getCoordinates()) {
			child.setSpecifier(spec);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.NPPhraseSpec#realiseSpec(simplenlg.realiser.Realiser,
	 * java.lang.String)
	 */
	@Override
	String realiseSpec(Realiser r, String agreementString) {
		if (this.wideScopeSpecifier) {
			return super.realiseSpec(r, agreementString);
		}

		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.NPPhraseSpec#realise(simplenlg.realiser.Realiser)
	 */
	@Override
	String realise(Realiser r) {
		checkPronoun();
		calculateNumber();
		checkPossessive();
		checkSpecifiers();
		return super.realise(r);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.NPPhraseSpec#realiseHead(simplenlg.realiser.Realiser)
	 */
	@Override
	String realiseHead(Realiser r) {
		String coord = this.coordinator.realise(r);
		return coord;
	}
}
