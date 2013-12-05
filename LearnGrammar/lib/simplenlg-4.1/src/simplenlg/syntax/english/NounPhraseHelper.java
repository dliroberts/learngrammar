/*
 * NounPhraseHelper - a helper class for the syntax processor that 
 * realises noun phrases.
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
package simplenlg.syntax.english;

import java.util.ArrayList;
import java.util.List;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.Person;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;

/**
 * <p>
 * This class contains static methods to help the syntax processor realise noun
 * phrases.
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
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
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
 * You should have received a copy of the GNU Lesser General Public License in
 * the zip file. If not, see <a
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
 * @author E. Reiter and D. Westwater, University of Aberdeen.
 * @version 4.0
 */
abstract class NounPhraseHelper {

	/** The qualitative position for ordering premodifiers. */
	private static final int QUALITATIVE_POSITION = 1;

	/** The colour position for ordering premodifiers. */
	private static final int COLOUR_POSITION = 2;

	/** The classifying position for ordering premodifiers. */
	private static final int CLASSIFYING_POSITION = 3;

	/** The noun position for ordering premodifiers. */
	private static final int NOUN_POSITION = 4;

	/**
	 * The main method for realising noun phrases.
	 * 
	 * @param parent
	 *            the <code>SyntaxProcessor</code> that called this method.
	 * @param phrase
	 *            the <code>PhraseElement</code> to be realised.
	 * @return the realised <code>NLGElement</code>.
	 */
	static NLGElement realise(SyntaxProcessor parent, PhraseElement phrase) {
		ListElement realisedElement = null;
		if (phrase != null
				&& !phrase.getFeatureAsBoolean(Feature.ELIDED).booleanValue()) {

			realisedElement = new ListElement();
			if (phrase.getFeatureAsBoolean(Feature.PRONOMINAL).booleanValue()) {
				realisedElement.addComponent(createPronoun(parent, phrase));
			} else {
				realiseSpecifier(phrase, parent, realisedElement);
				realisePreModifiers(phrase, parent, realisedElement);
				realiseHeadNoun(phrase, parent, realisedElement);
				PhraseHelper.realiseList(parent, realisedElement, phrase
						.getFeatureAsElementList(InternalFeature.COMPLEMENTS),
						DiscourseFunction.COMPLEMENT);

				PhraseHelper.realiseList(parent, realisedElement, phrase
						.getPostModifiers(), DiscourseFunction.POST_MODIFIER);
			}
		}
		return realisedElement;
	}

	/**
	 * Realises the head noun of the noun phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realiseHeadNoun(PhraseElement phrase,
			SyntaxProcessor parent, ListElement realisedElement) {
		NLGElement headElement = phrase.getHead();
		if (headElement != null) {
			headElement.setFeature(LexicalFeature.GENDER, phrase
					.getFeature(LexicalFeature.GENDER));
			headElement.setFeature(InternalFeature.ACRONYM, phrase
					.getFeature(InternalFeature.ACRONYM));
			headElement.setFeature(Feature.NUMBER, phrase
					.getFeature(Feature.NUMBER));
			headElement.setFeature(Feature.PERSON, phrase
					.getFeature(Feature.PERSON));
			headElement.setFeature(Feature.POSSESSIVE, phrase
					.getFeature(Feature.POSSESSIVE));
			headElement.setFeature(Feature.PASSIVE, phrase
					.getFeature(Feature.PASSIVE));
			NLGElement currentElement = parent.realise(headElement);
			currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
					DiscourseFunction.SUBJECT);
			realisedElement.addComponent(currentElement);
		}
	}

	/**
	 * Realises the pre-modifiers of the noun phrase. Before being realised,
	 * pre-modifiers undergo some basic sorting based on adjective ordering.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realisePreModifiers(PhraseElement phrase,
			SyntaxProcessor parent, ListElement realisedElement) {

		List<NLGElement> preModifiers = phrase.getPreModifiers();
		if (phrase.getFeatureAsBoolean(Feature.ADJECTIVE_ORDERING)
				.booleanValue()) {
			preModifiers = sortNPPreModifiers(phrase.getPreModifiers());
		}
		PhraseHelper.realiseList(parent, realisedElement, preModifiers,
				DiscourseFunction.PRE_MODIFIER);
	}

	/**
	 * Realises the specifier of the noun phrase.
	 * 
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param realisedElement
	 *            the current realisation of the noun phrase.
	 */
	private static void realiseSpecifier(PhraseElement phrase,
			SyntaxProcessor parent, ListElement realisedElement) {
		NLGElement specifierElement = phrase
				.getFeatureAsElement(InternalFeature.SPECIFIER);

		if (specifierElement != null
				&& !phrase.getFeatureAsBoolean(InternalFeature.RAISED).booleanValue()) {

			if (!specifierElement.isA(LexicalCategory.PRONOUN)) {
				specifierElement.setFeature(Feature.NUMBER, phrase
						.getFeature(Feature.NUMBER));
			}
			NLGElement currentElement = parent.realise(specifierElement);
			if (currentElement != null) {
				currentElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
						DiscourseFunction.SPECIFIER);
				realisedElement.addComponent(currentElement);
			}
		}
	}

	/**
	 * Sort the list of premodifiers for this noun phrase using adjective
	 * ordering (ie, "big" comes before "red")
	 * 
	 * @param originalModifiers
	 *            the original listing of the premodifiers.
	 * @return the sorted <code>List</code> of premodifiers.
	 */
	private static List<NLGElement> sortNPPreModifiers(
			List<NLGElement> originalModifiers) {

		List<NLGElement> orderedModifiers = null;

		if (originalModifiers == null || originalModifiers.size() <= 1) {
			orderedModifiers = originalModifiers;
		} else {
			orderedModifiers = new ArrayList<NLGElement>(originalModifiers);
			boolean changesMade = false;
			do {
				changesMade = false;
				for (int i = 0; i < orderedModifiers.size() - 1; i++) {
					if (getMinPos(orderedModifiers.get(i)) > getMaxPos(orderedModifiers
							.get(i + 1))) {
						NLGElement temp = orderedModifiers.get(i);
						orderedModifiers.set(i, orderedModifiers.get(i + 1));
						orderedModifiers.set(i + 1, temp);
						changesMade = true;
					}
				}
			} while (changesMade == true);
		}
		return orderedModifiers;
	}

	/**
	 * Determines the minimim position at which this modifier can occur.
	 * 
	 * @param modifier
	 *            the modifier to be checked.
	 * @return the minimum position for this modifier.
	 */
	private static int getMinPos(NLGElement modifier) {
		int position = QUALITATIVE_POSITION;

		if (modifier.isA(LexicalCategory.NOUN)
				|| modifier.isA(PhraseCategory.NOUN_PHRASE)) {

			position = NOUN_POSITION;
		} else if (modifier.isA(LexicalCategory.ADJECTIVE)
				|| modifier.isA(PhraseCategory.ADJECTIVE_PHRASE)) {
			WordElement adjective = getHeadWordElement(modifier);

			if (adjective.getFeatureAsBoolean(LexicalFeature.QUALITATIVE)
					.booleanValue()) {
				position = QUALITATIVE_POSITION;
			} else if (adjective.getFeatureAsBoolean(LexicalFeature.COLOUR)
					.booleanValue()) {
				position = COLOUR_POSITION;
			} else if (adjective.getFeatureAsBoolean(LexicalFeature.CLASSIFYING)
					.booleanValue()) {
				position = CLASSIFYING_POSITION;
			}
		}
		return position;
	}

	/**
	 * Determines the maximim position at which this modifier can occur.
	 * 
	 * @param modifier
	 *            the modifier to be checked.
	 * @return the maximum position for this modifier.
	 */
	private static int getMaxPos(NLGElement modifier) {
		int position = NOUN_POSITION;

		if (modifier.isA(LexicalCategory.ADJECTIVE)
				|| modifier.isA(PhraseCategory.ADJECTIVE_PHRASE)) {
			WordElement adjective = getHeadWordElement(modifier);

			if (adjective.getFeatureAsBoolean(LexicalFeature.CLASSIFYING)
					.booleanValue()) {
				position = CLASSIFYING_POSITION;
			} else if (adjective.getFeatureAsBoolean(LexicalFeature.COLOUR)
					.booleanValue()) {
				position = COLOUR_POSITION;
			} else if (adjective.getFeatureAsBoolean(LexicalFeature.QUALITATIVE)
					.booleanValue()) {
				position = QUALITATIVE_POSITION;
			} else {
				position = CLASSIFYING_POSITION;
			}
		}
		return position;
	}

	/**
	 * Retrieves the correct representation of the word from the element. This
	 * method will find the <code>WordElement</code>, if it exists, for the
	 * given phrase or inflected word.
	 * 
	 * @param element
	 *            the <code>NLGElement</code> from which the head is required.
	 * @return the <code>WordElement</code>
	 */
	private static WordElement getHeadWordElement(NLGElement element) {
		WordElement head = null;

		if (element instanceof WordElement)
			head = (WordElement) element;
		else if (element instanceof InflectedWordElement) {
			head = (WordElement) element.getFeature(InternalFeature.BASE_WORD);
		} else if (element instanceof PhraseElement) {
			head = getHeadWordElement(((PhraseElement) element).getHead());
		}
		return head;
	}

	/**
	 * Creates the appropriate pronoun if the subject of the noun phrase is
	 * pronominal.
	 * 
	 * @param parent
	 *            the parent <code>SyntaxProcessor</code> that will do the
	 *            realisation of the complementiser.
	 * @param phrase
	 *            the <code>PhraseElement</code> representing this noun phrase.
	 * @return the <code>NLGElement</code> representing the pronominal.
	 */
	private static NLGElement createPronoun(SyntaxProcessor parent,
			PhraseElement phrase) {

		String pronoun = "it"; //$NON-NLS-1$
		NLGFactory phraseFactory = phrase.getFactory();
		Object personValue = phrase.getFeature(Feature.PERSON);

		if (Person.FIRST.equals(personValue)) {
			pronoun = "I"; //$NON-NLS-1$
		} else if (Person.SECOND.equals(personValue)) {
			pronoun = "you"; //$NON-NLS-1$
		} else {
			Object genderValue = phrase.getFeature(LexicalFeature.GENDER);
			if (Gender.FEMININE.equals(genderValue)) {
				pronoun = "she"; //$NON-NLS-1$
			} else if (Gender.MASCULINE.equals(genderValue)) {
				pronoun = "he"; //$NON-NLS-1$
			}
		}
		NLGElement element = phraseFactory.createWord(pronoun,
				LexicalCategory.PRONOUN);
		element.setFeature(InternalFeature.DISCOURSE_FUNCTION,
				DiscourseFunction.SPECIFIER);
		element.setFeature(Feature.POSSESSIVE, phrase
				.getFeature(Feature.POSSESSIVE));
		element.setFeature(Feature.NUMBER, phrase.getFeature(Feature.NUMBER));
		if (phrase.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
			element.setFeature(InternalFeature.DISCOURSE_FUNCTION, phrase
					.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		}
		return element;
	}
}
