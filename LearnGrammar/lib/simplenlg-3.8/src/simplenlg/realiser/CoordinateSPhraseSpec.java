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

import simplenlg.features.ClauseStatus;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.lexicon.lexicalitems.Conjunction;

// TODO: Auto-generated Javadoc
/**
 * This class represents a coordinate clause. It inherits all the
 * simplenlg.features of {@link simplenlg.realiser.SPhraseSpec}.
 * 
 * <P>
 * <U>NB</U> <strong>Many simplenlg.features of this class are in experimental
 * phrase. This is especially true of the methods inherited from
 * <code>SPhraseSpec</code> which set premodifiers complements and so on. With
 * the simplenlg.exception of the actual sentential coordinates, anything added
 * to a <code>CoordinateSPhraseSpec</code> will be ignored at realisation
 * stage.</strong>
 * 
 * @author agatt
 */
public class CoordinateSPhraseSpec extends SPhraseSpec implements
		CoordinatePhrase<SPhraseSpec> {

	/** The coordinator. */
	private CoordinatePhraseSet<SPhraseSpec> coordinator;

	/** The external comp. */
	boolean externalComp;

	/**
	 * Constructs a new instance of a <code>CoordinateSPhraseSpec</code>.
	 * 
	 * @param coords
	 *            The daughter sentences of the coordinate sentence.
	 */
	public CoordinateSPhraseSpec(SPhraseSpec... coords) {
		super();
		this.coordinator = new CoordinatePhraseSet<SPhraseSpec>(this);
		this.coordinator.addCoordinates(coords);
		this.externalComp = false;
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
	 * @see simplenlg.realiser.CoordinatePhrase#setConjunction(java.lang.String)
	 */
	public void setConjunction(String conj) {
		this.coordinator.setConjunction(conj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.realiser.CoordinatePhrase#setConjunction(simplenlg.lexicon.
	 * lexicalitems.Conjunction)
	 */
	public void setConjunction(Conjunction conj) {
		this.coordinator.setConjunction(conj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#getCoordinates()
	 */
	public List<SPhraseSpec> getCoordinates() {
		return this.coordinator.getCoordinates();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#addCoordinates(T[])
	 */
	public void addCoordinates(SPhraseSpec... coords) {
		this.coordinator.addCoordinates(coords);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#setCoordinates(T[])
	 */
	public void setCoordinates(SPhraseSpec... coords) {
		this.coordinator.clearCoordinates();
		addCoordinates(coords);
	}

	/**
	 * Suppresses the complementiser, like
	 * {@link SPhraseSpec#suppressComplementiser(boolean)}. This feature is
	 * applied at the level of the coordinate clause. Thus:
	 * 
	 * <OL>
	 * <LI>If the parameter is <code>true</code>, then in case the coordinate
	 * sentence is subordinate, each daughter sentence will have a
	 * complementiser. E.g. <I>Peter said <U>that John walks and that Sally
	 * talks</U></I>.</LI>
	 * <LI>If the parameter is <code>true</code>, then in case the coordinate
	 * sentence is subordinate, there will be only an outer complementiser. E.g.
	 * <I>Peter said <U>that John walks and Sally talks</U></I>.</LI>
	 * </OL>
	 * 
	 * @param comp
	 *            If <code>true</code>, then the complementiser is suppressed.
	 * 
	 * @see simplenlg.realiser.SPhraseSpec#suppressComplementiser
	 */
	@Override
	public void suppressComplementiser(boolean comp) {
		this.suppressComp = comp;

		// if comp is not suppressed at coordinate level, it's suppressed for
		// all S daughters UNLESS this is subordinate and gerund
		boolean daughterComp = isSubordinateClause()
				&& getForm().equals(Form.GERUND) ? true : !comp;

		for (SPhraseSpec child : this.coordinator.coordinates) {
			child.suppressComplementiser(daughterComp);
		}
	}

	/**
	 * By default, if a coordinate clause is given subordinate status, it is
	 * realised with a single complementiser with scope over the entire phrase.
	 * Example: <i>that [John walked and Mary talked]</i>, unless this behaviour
	 * is changed using
	 * {@link simplenlg.realiser.CoordinateSPhraseSpec#suppressComplementiser}
	 * 
	 * @param type
	 *            The clause type for this clause, also inherited by its
	 *            daughter clauses.
	 * 
	 * @see simplenlg.realiser.SPhraseSpec#setClauseStatus(simplenlg.features.ClauseStatus)
	 * @see simplenlg.realiser.CoordinateSPhraseSpec#suppressComplementiser(boolean)
	 */
	@Override
	void setClauseStatus(ClauseStatus type) {
		super.setClauseStatus(type);

		for (SPhraseSpec child : this.coordinator.coordinates) {
			child.setClauseStatus(type);

			if (type == ClauseStatus.SUBORDINATE) {
				child.suppressComplementiser(true);
			}
		}
	}

	/**
	 * Like
	 * {@link simplenlg.realiser.SPhraseSpec#setDefaultComplementiser(String)},
	 * but also propagates the change down to the daughter clauses.
	 * 
	 * @param comp
	 *            The new complementiser
	 * 
	 * @see simplenlg.realiser.SPhraseSpec#setDefaultComplementiser(String)
	 */
	@Override
	public void setDefaultComplementiser(String comp) {
		this.complementiser = comp;

		for (SPhraseSpec child : this.coordinator.coordinates) {
			child.setDefaultComplementiser(comp);
		}
	}

	/**
	 * Sets whether this clause (i.e. its daughter verb phrase) is passive. This
	 * feature is inherited by all the daughter clauses of the coordinate
	 * clause.
	 * 
	 * @param pass
	 *            Determines whether this clause and its daughters are passive
	 * 
	 * @see simplenlg.realiser.SPhraseSpec#setPassive(boolean)
	 */
	@Override
	public void setPassive(boolean pass) {

		for (SPhraseSpec child : this.coordinator.coordinates) {
			child.setPassive(pass);
		}
	}

	/**
	 * Sets the form of this clause and all its daughters.
	 * 
	 * @param f
	 *            The new Form.
	 * 
	 * @see simplenlg.realiser.SPhraseSpec#setForm(Form)
	 */
	@Override
	public void setForm(Form f) {
		super.setForm(f);

		for (SPhraseSpec s : this.coordinator.coordinates) {
			s.setForm(f);
		}
	}

	/**
	 * Sets the tense of this clause and all its daughters.
	 * 
	 * @param t
	 *            The new Tense
	 */
	@Override
	public void setTense(Tense t) {
		super.setTense(t);

		for (SPhraseSpec s : this.coordinator.coordinates) {
			s.setTense(t);
		}

	}

	/**
	 * Sets whether the verb phrase of this clause and all its daughter clauses
	 * are perfective.
	 * 
	 * @param perf
	 *            Whether the sentence is perfective
	 * 
	 * @see simplenlg.realiser.SPhraseSpec#setPerfect(boolean)
	 */
	@Override
	public void setPerfect(boolean perf) {

		for (SPhraseSpec child : this.coordinator.coordinates) {
			child.setPerfect(perf);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.SPhraseSpec#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		boolean eq = false;

		if (o instanceof CoordinateSPhraseSpec) {
			CoordinateSPhraseSpec c = (CoordinateSPhraseSpec) o;
			eq = c.coordinator.equals(this.coordinator);
		}

		return eq;
	}

	// just realise the coordinates
	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.SPhraseSpec#realise(simplenlg.realiser.Realiser)
	 */
	@Override
	String realise(Realiser r) {
		String mainSent = this.coordinator.realise(r);

		if (isSubordinateClause() && !this.suppressComp
				&& !getForm().equals(Form.GERUND)) {
			mainSent = r.appendSpace(this.complementiser, mainSent);
		}

		return hasParentSpec() ? mainSent : r
				.applySentenceOrthography(mainSent);
	}

}
