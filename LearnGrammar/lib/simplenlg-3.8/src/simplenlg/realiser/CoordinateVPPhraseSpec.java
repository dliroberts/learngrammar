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

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Form;
import simplenlg.features.NumberAgr;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.lexicon.lexicalitems.Conjunction;

// TODO: Auto-generated Javadoc
/**
 * This class represents the coordination of two or more verb phrases. All the
 * simplenlg.features of {@link simplenlg.realiser.VPPhraseSpec} are inherited
 * so that it is possible to specify simplenlg.features like tense, mood and so
 * forth from this class, which will be inherited by all the daughter VPs.
 * However, daughter VPs need not all have the same simplenlg.features.
 * 
 * <P>
 * In general, whenever a feature inherited from <code>VPPhraseSpec</code> is
 * set in a <code>CoordinateVPPhraseSpec</code>, the following happens:
 * 
 * <OL>
 * <LI>The coordinate phrase has the new feature value, so that calling the
 * getter method (e.g. {@link #getForm()} for this feature will return that
 * value.</LI>
 * <LI>All the daughter phrases inherit that feature value.</LI>
 * </OL>
 * 
 * The above applies to these simplenlg.features:
 * <OL>
 * <LI>person</LI>
 * <LI>number</LI>
 * <LI>form</LI>
 * <LI>modal</LI>
 * <LI>mood</LI>
 * <LI>perfect</LI>
 * <LI>passive</LI>
 * <LI>negated</LI>
 * <LI>progressive</LI>
 * </OL>
 * 
 * Correspondingly, if the value of a feature is not set for the coordinate
 * phrase, calling the corresponding getter method will simply return the
 * default value or <code>null</code>. See the documentation for
 * {@link simplenlg.realiser.VPPhraseSpec} for details of the default feature
 * values.
 * 
 * <P>
 * As the simplenlg.features of a verb phrase mostly determine the structure of
 * its auxiliary phrase, this class also gives the option, via the method
 * {@link #aggregateAuxiliary(boolean)} to <i>aggregate</i> the coordinate verb
 * phrase, that is, realise only one auxiliary with wide-scope over the entire
 * set of coordinated head-verbs.
 * 
 * <P>
 * Examples of possible structures:
 * 
 * <UL>
 * <LI>walked and talked</LI>
 * <LI>[[had been walking] and [is being washed]] [non-aggregated]</LI>
 * <LI>[had been [walking and talking]] [aggregated]</LI>
 * </UL>
 * 
 * @author agatt
 */
public class CoordinateVPPhraseSpec extends VPPhraseSpec implements
		CoordinatePhrase<VPPhraseSpec> {

	/** The coordinator. */
	private CoordinatePhraseSet<VPPhraseSpec> coordinator;

	/** The aggregate auxiliary. */
	private boolean aggregateAuxiliary; // realise only a wide scope aux

	/**
	 * Constructs a new instance of a <code>CoordinateVPPhraseSpec</code>.
	 * 
	 * @param coords
	 *            the coords
	 */
	public CoordinateVPPhraseSpec(VPPhraseSpec... coords) {
		super();
		this.aggregateAuxiliary = false;
		this.coordinator = new CoordinatePhraseSet<VPPhraseSpec>(this, coords);
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
	public void addCoordinates(VPPhraseSpec... coords) {
		this.coordinator.addCoordinates(coords);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#setCoordinates(T[])
	 */
	public void setCoordinates(VPPhraseSpec... coords) {
		this.coordinator.clearCoordinates();
		addCoordinates(coords);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.CoordinatePhrase#getCoordinates()
	 */
	public List<VPPhraseSpec> getCoordinates() {
		return this.coordinator.getCoordinates();
	}

	// *******************************************************************************
	// feature getters and setters: need to be overridden as they percolate down
	// to coordinates
	// *******************************************************************************

	/**
	 * Sets whether this coordinate should be realised with a single auxiliary
	 * with scope over the entire set of coordinate phrases. This is achieved by
	 * suppressing the auxiliary realisation of each daughter
	 * {@link VPPhraseSpec} and realising only the auxiliary of this phrase. The
	 * default value of this feature is <code>false</code>, so that coordinate
	 * verb phrases are realised as in the following example.
	 * 
	 * <UL>
	 * <LI>John [had been walking] and [had been talking]</LI>
	 * </UL>
	 * 
	 * whereas setting this to <code>true</code> would yield:
	 * 
	 * <UL>
	 * <LI>John [had been [walking and talking]]</LI>
	 * </UL>
	 * 
	 * @param aggr
	 *            If <code>true</code>, causes this
	 */
	public void aggregateAuxiliary(boolean aggr) {
		this.aggregateAuxiliary = aggr;

		for (VPPhraseSpec child : this.coordinator.getCoordinates()) {
			child.realiseAuxiliary(false);
		}
	}

	/**
	 * Sets the form of this <code>CoordinateVPPhrasespec</code> and all
	 * daughter <code>VPPhraseSpec</code>s.
	 * 
	 * @param f
	 *            The form
	 */
	@Override
	public void setForm(Form f) {
		super.setForm(f);

		for (VPPhraseSpec child : this.coordinator.coordinates) {
			child.setForm(f);
		}
	}

	/**
	 * Sets whether this <code>CoordinateVPPhrasespec</code> and all daughter
	 * <code>VPPhraseSpec</code>s are passive or not.
	 * 
	 * @param pass
	 *            the pass
	 */
	@Override
	public void setPassive(boolean pass) {
		super.setPassive(pass);

		for (VPPhraseSpec child : this.coordinator.coordinates) {
			child.setPassive(pass);
		}

	}

	/**
	 * Sets the </code>progressive</code> feature of this
	 * <code>CoordinateVPPhraseSpec</code> and all its daughters.
	 * 
	 * @param prog
	 *            the prog
	 */
	@Override
	public void setProgressive(boolean prog) {
		super.setProgressive(prog);

		for (VPPhraseSpec child : this.coordinator.coordinates) {
			child.setProgressive(prog);
		}
	}

	/**
	 * Sets the </code>perfect</code> feature of this
	 * <code>CoordinateVPPhraseSpec</code> and all its daughter phrases.
	 * 
	 * @param perf
	 *            the perf
	 */
	@Override
	public void setPerfect(boolean perf) {
		super.setPerfect(perf);

		for (VPPhraseSpec child : this.coordinator.coordinates) {
			child.setPerfect(perf);
		}
	}

	/**
	 * Sets the </code>negated</code> feature of this
	 * <code>CoordinateVPPhraseSpec</code> and all its daughters. This will
	 * result in automatic do-insertion if required at simplenlg.realiser stage
	 * (e.g. <i>did not do</i>).
	 * 
	 * @param neg
	 *            the neg
	 */
	@Override
	public void setNegated(boolean neg) {
		super.setNegated(true);

		for (VPPhraseSpec child : this.coordinator.coordinates) {
			child.setNegated(neg);
		}
	}

	/**
	 * Sets the tense of this <code>CoordinateVPPhraseSpec</code> and all its
	 * daughter <code>VPPhraseSpec</code>s.
	 * 
	 * @param t
	 *            the t
	 */
	@Override
	public void setTense(Tense t) {
		super.setTense(t);

		for (VPPhraseSpec child : this.coordinator.coordinates) {
			child.setTense(t);
		}

	}

	/**
	 * Sets the modal auxiliary of this <code>CoordinateVPPhraseSpec</code> (eg,
	 * "can", "must") and all its daughter <code>VPPhraseSpec</code>s.
	 * 
	 * @param modal
	 *            the modal
	 */
	@Override
	public void setModal(String modal) {
		super.setModal(modal);

		for (VPPhraseSpec child : this.coordinator.coordinates) {
			child.setModal(modal);
		}
	}

	/**
	 * Set the person feature of this <code>CoordinateVPPhraseSpec</code> and
	 * all its daughter <code>VPPhraseSpec</code>s.
	 * 
	 * @param p
	 *            The {@link simplenlg.features.Person} value.
	 */
	@Override
	public void setPerson(Person p) {
		super.setPerson(p);

		for (VPPhraseSpec child : this.coordinator.coordinates) {
			child.setPerson(p);
		}
	}

	/**
	 * Sets the number simplenlg.features (sing/plu) of this
	 * <code>CoordinateVPPhraseSpec</code> and all its daughter
	 * <code>VPPhraseSpec</code>s
	 * 
	 * @param n
	 *            The {@link simplenlg.features.NumberAgr} value.
	 */
	@Override
	public void setNumber(NumberAgr n) {
		super.setNumber(n);

		for (VPPhraseSpec child : this.coordinator.coordinates) {
			child.setNumber(n);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seesimplenlg.realiser.VPPhraseSpec#getComplements(simplenlg.features.
	 * DiscourseFunction)
	 */
	@Override
	public List<Phrase> getComplements(DiscourseFunction function) {
		List<Phrase> comps = super.getComplements(function);

		for (VPPhraseSpec child : this.coordinator.getCoordinates()) {
			comps.addAll(child.getComplements(function));
		}

		return comps;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.realiser.VPPhraseSpec#realise(simplenlg.realiser.Realiser,
	 * boolean)
	 */
	@Override
	String realise(Realiser r, boolean interrogative) {

		// interrogative realisation always requires AUX to be aggregated
		if (interrogative && !this.aggregateAuxiliary) {
			aggregateAuxiliary(true);
		}

		if (this.aggregateAuxiliary) {
			realiseVerbGroup(r, interrogative);
			computeRealisation(r, interrogative);
		}

		String preModText = realisePremodifier(r);
		String compText = realiseComplement(r);
		String postModText = realisePostmodifier(r);
		String coordText = this.coordinator.realise(r);

		return r.appendSpace(preModText, this.auxiliaryRealisation, coordText,
				compText, postModText);
	}

	/*
	 * (non-Javadoc) coordinateVP may have its own pre- and post-mods as well as
	 * its own complement
	 * 
	 * 
	 * @see simplenlg.realiser.VPPhraseSpec#realise(simplenlg.realiser.Realiser)
	 */
	@Override
	String realise(Realiser r) {
		return realise(r, false);
	}

	/**
	 * This method returns those complements which would be raised to subject
	 * position within a sentence, if the sentence (and the verb phrase) is
	 * passive. This is worked out as follows:
	 * <OL>
	 * <LI>If there are direct objects, then they are returned.</LI>
	 * <LI>Indirect objects are returned otherwise. If the indirect object(s) is
	 * a prepositional phrase, its complement is returned.</LI>
	 * </OL>
	 * 
	 * @return the complements to raise to subject, a
	 *         <code>java.util.List&lt;Phrase&gt;</code>
	 * 
	 * @see #setPassive(boolean)
	 */
	@Override
	public List<Phrase> getPassiveRaisingComplements() {
		List<Phrase> objects = getComplements(DiscourseFunction.OBJECT);

		if (objects.isEmpty()) {
			for (Phrase p : getComplements(DiscourseFunction.INDIRECT_OBJECT)) {
				if (p instanceof PPPhraseSpec && !p.isElided()) {
					objects.addAll(((PPPhraseSpec) p).getComplements());
				} else if (!p.isElided()) {
					objects.add(p);
				}
			}
		}

		return flagRaisedNPs(objects, true);
	}

}
