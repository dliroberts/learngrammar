/*
 * NPPhraseSpec.java - A representation of a clause
 * 
 * Copyright (C) 2010, University of Aberdeen
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package simplenlg.phrasespec;

import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.Person;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;

/**
 * <p>
 * This class defines a noun phrase.  It is essentially
 * a wrapper around the <code>PhraseElement</code> class, with methods
 * for setting common constituents such as specifier.
 * For example, the <code>setNoun</code> method in this class sets
 * the head of the element to be the specified noun
 *
 * From an API perspective, this class is a simplified version of the NPPhraseSpec
 * class in simplenlg V3.  It provides an alternative way for creating syntactic
 * structures, compared to directly manipulating a V4 <code>PhraseElement</code>.
 * 
 * Methods are provided for setting and getting the following constituents:
 * <UL>
 * <li>Specifier        (eg, "the")
 * <LI>PreModifier		(eg, "green")
 * <LI>Noun				(eg, "apple")
 * <LI>PostModifier     (eg, "in the shop")
 * </UL>
 *
 * NOTE: The setModifier method will attempt to automatically determine whether
 * a modifier should be expressed as a PreModifier, or PostModifier
 * 
 * NOTE: Specifiers are currently pretty basic, this needs more development
 * 
 * Features (such as number) must be accessed via the <code>setFeature</code> and
 * <code>getFeature</code> methods (inherited from <code>NLGElement</code>).
 * Features which are often set on NPPhraseSpec include
 * <UL>
 * <LI>Number         (eg, "the apple" vs "the apples")
 * <LI>Possessive     (eg, "John" vs "John's")
 * <LI>Pronominal     (eg, "the apple" vs "it")
 * </UL>
 * 
 * <code>NPPhraseSpec</code> are produced by the <code>createNounPhrase</code>
 * method of a <code>PhraseFactory</code>
 * </p>
 * 
 * <hr>
 * 
 * <p>
 * Copyright (C) 2010, University of Aberdeen
 * </p>
 * 
 * <p>
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * </p>
 * 
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * </p>
 * 
 * <p>
 * You should have received a copy of the GNU Lesser General Public License in the zip
 * file. If not, see <a
 * href="http://www.gnu.org/licenses/">www.gnu.org/licenses</a>.
 * </p>
 * 
 * <p>
 * For more details on SimpleNLG visit the project website at <a
 * href="http://www.csd.abdn.ac.uk/research/simplenlg/"
 * >www.csd.abdn.ac.uk/research/simplenlg</a> or email Dr Ehud Reiter at
 * e.reiter@abdn.ac.uk
 * </p>
 * 
 * @author E. Reiter, University of Aberdeen.
 * @version 4.1
 * 
 */
public class NPPhraseSpec extends PhraseElement {

	public NPPhraseSpec(NLGFactory phraseFactory) {
		super(PhraseCategory.NOUN_PHRASE);
		this.setFactory(phraseFactory);
	}
	
	/* (non-Javadoc)
	 * @see simplenlg.framework.PhraseElement#setHead(java.lang.Object)
	 * This version sets NP default features from the head
	 */
	@Override
	public void setHead(Object newHead) {
		super.setHead(newHead);
		setNounPhraseFeatures(getFeatureAsElement(InternalFeature.HEAD));
	}
	
	/**
	 * A helper method to set the features required for noun phrases, from the head noun
	 * 
	 * @param phraseElement
	 *            the phrase element.
	 * @param nounElement
	 *            the element representing the noun.
	 */
	private void setNounPhraseFeatures(NLGElement nounElement) {
		if (nounElement == null)
			return;
		
		setFeature(Feature.POSSESSIVE,
				nounElement != null ? nounElement
						.getFeatureAsBoolean(Feature.POSSESSIVE)
						: Boolean.FALSE);
		setFeature(InternalFeature.RAISED, false);
		setFeature(InternalFeature.ACRONYM, false);

		if (nounElement != null && nounElement.hasFeature(Feature.NUMBER)) {

			setFeature(Feature.NUMBER, nounElement
					.getFeature(Feature.NUMBER));
		} else {
			setPlural(false);
		}
		if (nounElement != null && nounElement.hasFeature(Feature.PERSON)) {

			setFeature(Feature.PERSON, nounElement
					.getFeature(Feature.PERSON));
		} else {
			setFeature(Feature.PERSON, Person.THIRD);
		}
		if (nounElement != null && nounElement.hasFeature(LexicalFeature.GENDER)) {

			setFeature(LexicalFeature.GENDER, nounElement
					.getFeature(LexicalFeature.GENDER));
		} else {
			setFeature(LexicalFeature.GENDER, Gender.NEUTER);
		}
		
		if (nounElement != null && nounElement.hasFeature(LexicalFeature.EXPLETIVE_SUBJECT)) {

			setFeature(LexicalFeature.EXPLETIVE_SUBJECT, nounElement
					.getFeature(LexicalFeature.EXPLETIVE_SUBJECT));
		}

		setFeature(Feature.ADJECTIVE_ORDERING, true);
	}

	
	/** sets the noun (head) of a noun phrase
	 * @param noun
	 */
	public void setNoun(Object noun) {
		NLGElement nounElement = getFactory().createNLGElement(noun, LexicalCategory.NOUN);
		setHead(nounElement);
	}

	/**
	 * @return noun (head) of noun phrase
	 */
	public NLGElement getNoun() {
		return getHead();
	}
	
	/** sets the specifier of a noun phrase.  Can be determiner (eg "the"), possessive (eg, "John's")
	 * @param specifier
	 */
	public void setSpecifier (Object specifier) {
		if (specifier instanceof NLGElement)
			setFeature(InternalFeature.SPECIFIER, specifier);
		else {
			// create specifier as word (assume determiner)
			NLGElement specifierElement = getFactory().createWord(specifier, LexicalCategory.DETERMINER);

			// set specifier feature
			setFeature(InternalFeature.SPECIFIER, specifierElement);
		}
	}

	/**
	 * @return specifier (eg, determiner) of noun phrase
	 */
	public NLGElement getSpecifier() {
		return getFeatureAsElement(InternalFeature.SPECIFIER);
	}
	
	/** Add a modifier to an NP
	 * Use heuristics to decide where it goes
	 * @param modifier
	 */
	@Override
	public void addModifier(Object modifier) {
		// string which is one lexicographic word is looked up in lexicon,
		// adjective is preModifier
		// Everything else is postModifier
		if (modifier == null)
			return;

		// get modifier as NLGElement if possible
		NLGElement modifierElement = null;
		if (modifier instanceof NLGElement)
			modifierElement = (NLGElement) modifier;
		else if (modifier instanceof String) {
			String modifierString = (String)modifier;
			if (modifierString.length() > 0 && !modifierString.contains(" "))
				modifierElement = getFactory().createWord(modifier, LexicalCategory.ANY);
		}

		// if no modifier element, must be a complex string, add as postModifier
		if (modifierElement == null) {
			addPostModifier((String)modifier);
			return;
		}
		
		// AdjP is premodifer
		if (modifierElement instanceof AdjPhraseSpec) {
			addPreModifier(modifierElement);
			return;
		}

		// else extract WordElement if modifier is a single word
		WordElement modifierWord = null;
		if (modifierElement != null && modifierElement instanceof WordElement)
			modifierWord = (WordElement) modifierElement;
		else if (modifierElement != null && modifierElement instanceof InflectedWordElement)
			modifierWord = ((InflectedWordElement) modifierElement).getBaseWord();

		// check if modifier is an adjective
		if (modifierWord != null && modifierWord.getCategory() == LexicalCategory.ADJECTIVE) {
			addPreModifier(modifierWord);
			return;
		}

		// default case
		addPostModifier(modifierElement);
	}
}
