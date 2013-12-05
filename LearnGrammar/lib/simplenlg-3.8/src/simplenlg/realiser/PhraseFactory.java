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

import simplenlg.features.Category;
import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.lexicon.lexicalitems.Adjective;
import simplenlg.lexicon.lexicalitems.Conjunction;
import simplenlg.lexicon.lexicalitems.Noun;
import simplenlg.lexicon.lexicalitems.Preposition;
import simplenlg.lexicon.lexicalitems.Verb;

// TODO: Auto-generated Javadoc
/**
 * This class provides a number of static factory methods to construct and
 * combine phrases. The aim is to facilitate phrase construction and
 * combination. Factory methods are provided for all the major headed phrase
 * categories, as well as coordinate phrases.
 * 
 * @author agatt
 */
public class PhraseFactory {

	/**
	 * Constructs a {@link simplenlg.realiser.StringPhraseSpec} given a
	 * grammatical category and the <code>String</code>
	 * 
	 * @param cat
	 *            A value of {@link simplenlg.features.Category}, possibly
	 *            <code>null</code>
	 * @param string
	 *            The <code>String</code> to be wrapper in this spec.
	 * 
	 * @return A <code>StringPhraseSpec</code> with the given
	 *         <code>String</code> and Category.
	 */
	public static StringPhraseSpec makeStringPhraseSpec(Category cat,
			String string) {
		StringPhraseSpec s = new StringPhraseSpec(string);

		if (cat != null) {
			s.setCategory(cat);
		}

		return s;
	}

	/**
	 * Constructs an {@link simplenlg.realiser.NPPhraseSpec}, given the set of
	 * parameters. Any of the parameters can be null, except the head.
	 * 
	 * @param det
	 *            The determiner, a <code>String</code>,
	 *            {@link simplenlg.lexicon.lexicalitems.Determiner} or
	 *            <code>null</code>
	 * @param head
	 *            The head, a <code>String</code>,
	 *            {@link simplenlg.lexicon.lexicalitems.Noun}
	 * @param premodifier
	 *            The premodifier, a <code>String</code>,
	 *            {@link simplenlg.realiser.Phrase} or <code>null</code>
	 * @param postmodifier
	 *            The postmodifier, a <code>String</code>,
	 *            {@link simplenlg.realiser.Phrase} or <code>null</code>
	 * @param complement
	 *            The complement, a <code>String</code>,
	 *            {@link simplenlg.realiser.Phrase} or <code>null</code>
	 * 
	 * @return The <code>NPPhraseSpec</code> if the specified head is non-null,
	 *         <code>null</code> otherwise
	 */
	public static NPPhraseSpec makeNP(Object det, Object head,
			Object premodifier, Object postmodifier, Object complement) {

		NPPhraseSpec np = (NPPhraseSpec) PhraseFactory.makePhrase(
				Category.NOUN, head);

		if (np == null) {
			return np;
		}

		if (det != null) {
			np.setSpecifier(det);
		}

		np = (NPPhraseSpec) PhraseFactory.preModify(np, premodifier);
		np = (NPPhraseSpec) PhraseFactory.postModify(np, premodifier);
		np = (NPPhraseSpec) PhraseFactory.setComplement(np, premodifier);
		return np;
	}

	/**
	 * Constructs an {@link simplenlg.realiser.VPPhraseSpec}, given the set of
	 * parameters. Any of the parameters can be null, except the head.
	 * 
	 * @param head
	 *            The head, a <code>String</code>,
	 *            {@link simplenlg.lexicon.lexicalitems.Verb}
	 * @param premodifier
	 *            The premodifier, a <code>String</code>,
	 *            {@link simplenlg.realiser.Phrase} or <code>null</code>
	 * @param postmodifier
	 *            The postmodifier, a <code>String</code>,
	 *            {@link simplenlg.realiser.Phrase} or <code>null</code>
	 * @param complement
	 *            The complement, a <code>String</code>,
	 *            {@link simplenlg.realiser.Phrase} or <code>null</code>
	 * 
	 * @return The <code>VPPhraseSpec</code> if the specified head is non-null,
	 *         <code>null</code> otherwise
	 */
	public static VPPhraseSpec makeVP(String head, Object premodifier,
			Object postmodifier, Object complement) {
		VPPhraseSpec vp = (VPPhraseSpec) PhraseFactory.makePhrase(
				Category.VERB, head);

		if (vp == null) {
			return vp;
		}

		vp = (VPPhraseSpec) PhraseFactory.preModify(vp, premodifier);
		vp = (VPPhraseSpec) PhraseFactory.postModify(vp, postmodifier);
		vp = (VPPhraseSpec) PhraseFactory.setComplement(vp, complement);
		return vp;
	}

	/**
	 * Constructs an {@link simplenlg.realiser.AdjPhraseSpec}, given the set of
	 * parameters. Any of the parameters can be null, except the head.
	 * 
	 * @param head
	 *            The head, a <code>String</code>,
	 *            {@link simplenlg.lexicon.lexicalitems.Adjective}
	 * @param premodifier
	 *            The premodifier, a <code>String</code>,
	 *            {@link simplenlg.realiser.Phrase} or <code>null</code>
	 * @param postmodifier
	 *            The postmodifier, a <code>String</code>,
	 *            {@link simplenlg.realiser.Phrase} or <code>null</code>
	 * @param complement
	 *            The complement, a <code>String</code>,
	 *            {@link simplenlg.realiser.Phrase} or <code>null</code>
	 * 
	 * @return The <code>AdjPhraseSpec</code> if the specified head is non-null,
	 *         <code>null</code> otherwise
	 */
	public static AdjPhraseSpec makeAdjP(String head, Object premodifier,
			Object postmodifier, Object complement) {
		AdjPhraseSpec phrase = (AdjPhraseSpec) PhraseFactory.makePhrase(
				Category.ADJECTIVE, head);

		if (phrase == null) {
			return phrase;
		}

		phrase = (AdjPhraseSpec) PhraseFactory.preModify(phrase, premodifier);
		phrase = (AdjPhraseSpec) PhraseFactory.postModify(phrase, postmodifier);
		phrase = (AdjPhraseSpec) PhraseFactory
				.setComplement(phrase, complement);
		return phrase;
	}

	/**
	 * Constructs an {@link simplenlg.realiser.PPPhraseSpec}, given the set of
	 * parameters. Any of the parameters can be null, except the head.
	 * 
	 * @param head
	 *            The head, a <code>String</code>,
	 *            {@link simplenlg.lexicon.lexicalitems.Adjective}
	 * @param complement
	 *            The complement, a <code>String</code>,
	 *            {@link simplenlg.realiser.Phrase} or <code>null</code>
	 * 
	 * @return The <code>PPPhraseSpec</code> if the specified head is non-null,
	 *         <code>null</code> otherwise
	 */
	public static PPPhraseSpec makePP(String head, Object complement) {
		PPPhraseSpec phrase = (PPPhraseSpec) PhraseFactory.makePhrase(
				Category.PREPOSITION, head);

		if (phrase == null) {
			return phrase;
		}

		phrase = (PPPhraseSpec) PhraseFactory.setComplement(phrase, complement);
		return phrase;
	}

	/**
	 * Coordinates two phrases to form a new phrase. Any of the parameters can
	 * be null. In case one of the <code>Phrase</code> arguments is null, the
	 * other argument is returned.
	 * 
	 * @param phrase1
	 *            the phrase1
	 * @param phrase2
	 *            the phrase2
	 * @param conj
	 *            The conjunction to use. If this is <code>null</code>, the
	 *            default conjunction is <i>and</i>
	 * 
	 * @return A coordinate phrase if phrase1 and phrase2 instantiate the same
	 *         class (e.g. both are <code>NPPhraseSpec</code>s).
	 */
	public static Phrase coordinate(Phrase phrase1, Phrase phrase2,
			Conjunction conj) {

		if (phrase1 == null) {
			return phrase2;
		} else if (phrase2 == null) {
			return phrase1;
		}

		CoordinatePhrase<?> result = null;

		if (PhraseFactory.areSameCategory(phrase1, phrase2)) {
			if (phrase1 instanceof NPPhraseSpec) {
				result = new CoordinateNPPhraseSpec((NPPhraseSpec) phrase1,
						(NPPhraseSpec) phrase2);
			} else if (phrase1 instanceof VPPhraseSpec) {
				result = new CoordinateVPPhraseSpec((VPPhraseSpec) phrase1,
						(VPPhraseSpec) phrase2);
			} else if (phrase1 instanceof PPPhraseSpec) {
				result = new CoordinatePPPhraseSpec((PPPhraseSpec) phrase1,
						(PPPhraseSpec) phrase2);
			} else if (phrase1 instanceof SPhraseSpec) {
				result = new CoordinateSPhraseSpec((SPhraseSpec) phrase1,
						(SPhraseSpec) phrase2);
			} else if (phrase1 instanceof AdjPhraseSpec) {
				result = new CoordinateAdjPhraseSpec((AdjPhraseSpec) phrase1,
						(AdjPhraseSpec) phrase2);
			} else if (phrase1 instanceof StringPhraseSpec) {
				result = new CoordinateStringPhraseSpec(
						(StringPhraseSpec) phrase1, (StringPhraseSpec) phrase2);
			}
		}

		if (conj != null && result != null) {
			result.setConjunction(conj);
		}

		return result;
	}

	// **************************************************************************
	// PRIVATE METHODS
	// **************************************************************************

	/*
	 * used to intialise a phrase
	 */
	/**
	 * Make phrase.
	 * 
	 * @param cat
	 *            the cat
	 * @param head
	 *            the head
	 * 
	 * @return the phrase
	 */
	private static Phrase makePhrase(Category cat, Object head) {

		switch (cat) {

		case NOUN:

			if (head instanceof Noun) {
				return new NPPhraseSpec((Noun) head);
			} else if (head instanceof String) {
				return new NPPhraseSpec(new Noun((String) head));
			} else {
				return null;
			}

		case VERB:

			if (head instanceof Verb) {
				return new VPPhraseSpec((Verb) head);
			} else if (head instanceof String) {
				return new VPPhraseSpec(new Verb((String) head));
			} else {
				return null;
			}

		case ADJECTIVE:

			if (head instanceof Adjective) {
				return new AdjPhraseSpec((Adjective) head);
			} else if (head instanceof String) {
				return new AdjPhraseSpec(new Adjective((String) head));
			} else {
				return null;
			}

		case PREPOSITION:

			if (head instanceof Preposition) {
				return new PPPhraseSpec((Preposition) head);
			} else if (head instanceof String) {
				return new PPPhraseSpec(new Preposition((String) head));
			} else {
				return null;
			}

		case CLAUSE:
			SPhraseSpec s = new SPhraseSpec();

			if (head instanceof Verb) {
				s.setHead((Verb) head);
				return s;
			}

			else if (head instanceof String) {
				s.setHead((String) head);
				return s;
			} else {
				return null;
			}

		default:
			return null;
		}

	}

	/*
	 * premodify a phrase
	 */
	/**
	 * Pre modify.
	 * 
	 * @param spec
	 *            the spec
	 * @param premodifier
	 *            the premodifier
	 * 
	 * @return the headed phrase spec<?>
	 */
	private static HeadedPhraseSpec<?> preModify(HeadedPhraseSpec<?> spec,
			Object premodifier) {

		if (premodifier != null) {
			spec.setPremodifier(PhraseFactory.castToPhrase(premodifier));
		}

		return spec;
	}

	/*
	 * postmodify a phrase
	 */
	/**
	 * Post modify.
	 * 
	 * @param spec
	 *            the spec
	 * @param postmodifier
	 *            the postmodifier
	 * 
	 * @return the headed phrase spec<?>
	 */
	private static HeadedPhraseSpec<?> postModify(HeadedPhraseSpec<?> spec,
			Object postmodifier) {

		if (postmodifier != null) {
			spec.setPostmodifier(PhraseFactory.castToPhrase(postmodifier));
		}

		return spec;
	}

	/*
	 * set the complement of a phrase
	 */
	/**
	 * Sets the complement.
	 * 
	 * @param spec
	 *            the spec
	 * @param complement
	 *            the complement
	 * 
	 * @return the headed phrase spec<?>
	 */
	private static HeadedPhraseSpec<?> setComplement(HeadedPhraseSpec<?> spec,
			Object complement) {

		if (complement != null) {
			spec.setComplement(PhraseFactory.castToPhrase(complement));
		}

		return spec;
	}

	/*
	 * check if two phrases can be conjoined
	 */
	/**
	 * Are same category.
	 * 
	 * @param phrase1
	 *            the phrase1
	 * @param phrase2
	 *            the phrase2
	 * 
	 * @return true, if successful
	 */
	private static boolean areSameCategory(Phrase phrase1, Phrase phrase2) {

		if (phrase1.getCategory() != phrase2.getCategory()) {
			return false;
		} else if (phrase1 instanceof SPhraseSpec
				&& !(phrase2 instanceof SPhraseSpec)) {
			return false;
		}

		return true;
	}

	/**
	 * Apply.
	 * 
	 * @param spec
	 *            the spec
	 * @param f
	 *            the f
	 * 
	 * @return the phrase
	 */
	private static Phrase apply(Phrase spec, Feature f) {

		if (spec instanceof NPPhraseSpec) {
			NPPhraseSpec np = (NPPhraseSpec) spec;

			if (f instanceof simplenlg.features.NumberAgr) {
				np.setNumber((simplenlg.features.NumberAgr) f);
			} else if (f instanceof Gender) {
				np.setGender((Gender) f);
			} else if (f instanceof Person) {
				np.setPerson((Person) f);
			}

		} else if (spec instanceof VPPhraseSpec) {
			VPPhraseSpec vp = (VPPhraseSpec) spec;

			if (f instanceof simplenlg.features.NumberAgr) {
				vp.setNumber((simplenlg.features.NumberAgr) f);
			} else if (f instanceof Person) {
				vp.setPerson((Person) f);
			} else if (f instanceof Tense) {
				vp.setTense((Tense) f);
			}

		}
		return spec;
	}

	/**
	 * Apply feature.
	 * 
	 * @param phrase
	 *            the phrase
	 * @param feature
	 *            the feature
	 * 
	 * @return the phrase
	 */
	@SuppressWarnings("unused")
	private static Phrase applyFeature(Phrase phrase, Feature... feature) {

		for (Feature f : feature) {
			if (f.appliesTo(phrase.getCategory())) {
				phrase = PhraseFactory.apply(phrase, f);
			}
		}

		return phrase;
	}

	/**
	 * Cast to phrase.
	 * 
	 * @param o
	 *            the o
	 * 
	 * @return the phrase spec
	 */
	private static PhraseSpec castToPhrase(Object o) {

		if (o instanceof String) {
			return new StringPhraseSpec((String) o);
		} else {
			return (PhraseSpec) o;
		}
	}

}
