/*
 * CoordinatedPhraseHelper - a helper class for the syntax processor that 
 * realises coordinated phrases.
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

import java.util.List;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;

/**
 * <p>
 * This class contains static methods to help the syntax processor realise
 * coordinated phrases.
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
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 */
abstract class CoordinatedPhraseHelper {

	/**
	 * The main method for realising coordinated phrases.
	 * 
	 * @param parent
	 *            the <code>SyntaxProcessor</code> that called this method.
	 * @param phrase
	 *            the <code>CoordinatedPhrase</code> to be realised.
	 * @return the realised <code>NLGElement</code>.
	 */
	static NLGElement realise(SyntaxProcessor parent,
			CoordinatedPhraseElement phrase) {
		ListElement realisedElement = null;

		if (phrase != null) {
			realisedElement = new ListElement();
			PhraseHelper.realiseList(parent, realisedElement, phrase
					.getPreModifiers(), DiscourseFunction.PRE_MODIFIER);

			CoordinatedPhraseElement coordinated = new CoordinatedPhraseElement();

			List<NLGElement> children = phrase.getChildren();
			String conjunction = phrase.getFeatureAsString(Feature.CONJUNCTION);
			coordinated.setFeature(Feature.CONJUNCTION, conjunction);
			coordinated.setFeature(Feature.CONJUNCTION_TYPE, phrase
					.getFeature(Feature.CONJUNCTION_TYPE));

			InflectedWordElement conjunctionElement = null;

			if (children != null && children.size() > 0) {
				if (phrase.getFeatureAsBoolean(Feature.RAISE_SPECIFIER)
						.booleanValue()) {

					raiseSpecifier(children);
				}

				NLGElement child = phrase.getLastCoordinate();
				child.setFeature(Feature.POSSESSIVE, phrase
						.getFeature(Feature.POSSESSIVE));

				child = children.get(0);

				setChildFeatures(phrase, child);

				coordinated.addCoordinate(parent.realise(child));
				for (int index = 1; index < children.size(); index++) {
					child = children.get(index);
					setChildFeatures(phrase, child);
					if (phrase.getFeatureAsBoolean(Feature.AGGREGATE_AUXILIARY)
							.booleanValue()) {
						child.setFeature(InternalFeature.REALISE_AUXILIARY, false);
					}

					if (child.isA(PhraseCategory.CLAUSE)) {
						child
								.setFeature(
										Feature.SUPRESSED_COMPLEMENTISER,
										phrase
												.getFeature(Feature.SUPRESSED_COMPLEMENTISER));
					}

					conjunctionElement = new InflectedWordElement(conjunction,
							LexicalCategory.CONJUNCTION);
					conjunctionElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
							DiscourseFunction.CONJUNCTION);
					coordinated.addCoordinate(conjunctionElement);
					coordinated.addCoordinate(parent.realise(child));
				}
				realisedElement.addComponent(coordinated);
			}

			PhraseHelper.realiseList(parent, realisedElement, phrase
					.getPostModifiers(), DiscourseFunction.POST_MODIFIER);
			PhraseHelper.realiseList(parent, realisedElement, phrase
					.getComplements(), DiscourseFunction.COMPLEMENT);
		}
		return realisedElement;
	}

	/**
	 * Sets the common features from the phrase to the child element.
	 * 
	 * @param phrase
	 *            the <code>CoordinatedPhraseElement</code>
	 * @param child
	 *            a single coordinated <code>NLGElement</code> within the
	 *            coordination.
	 */
	private static void setChildFeatures(CoordinatedPhraseElement phrase,
			NLGElement child) {

		if (phrase.hasFeature(Feature.PROGRESSIVE)) {
			child.setFeature(Feature.PROGRESSIVE, phrase
					.getFeature(Feature.PROGRESSIVE));
		}
		if (phrase.hasFeature(Feature.PERFECT)) {
			child.setFeature(Feature.PERFECT, phrase
					.getFeature(Feature.PERFECT));
		}
		if (phrase.hasFeature(InternalFeature.SPECIFIER)) {
			child.setFeature(InternalFeature.SPECIFIER, phrase
					.getFeature(InternalFeature.SPECIFIER));
		}
		if (phrase.hasFeature(LexicalFeature.GENDER)) {
			child.setFeature(LexicalFeature.GENDER, phrase.getFeature(LexicalFeature.GENDER));
		}
		if (phrase.hasFeature(Feature.NUMBER)) {
			child.setFeature(Feature.NUMBER, phrase.getFeature(Feature.NUMBER));
		}
		if (phrase.hasFeature(Feature.TENSE)) {
			child.setTense(phrase.getTense());
		}
		if (phrase.hasFeature(Feature.PERSON)) {
			child.setFeature(Feature.PERSON, phrase.getFeature(Feature.PERSON));
		}
		if (phrase.hasFeature(Feature.NEGATED)) {
			child.setNegated(phrase.isNegated());
		}
		if (phrase.hasFeature(Feature.MODAL)) {
			child.setFeature(Feature.MODAL, phrase.getFeature(Feature.MODAL));
		}
		if (phrase.hasFeature(InternalFeature.DISCOURSE_FUNCTION)) {
			child.setFeature(InternalFeature.DISCOURSE_FUNCTION, phrase
					.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		}
		if (phrase.hasFeature(Feature.FORM)) {
			child.setFeature(Feature.FORM, phrase.getFeature(Feature.FORM));
		}
		if (phrase.hasFeature(InternalFeature.CLAUSE_STATUS)) {
			child.setFeature(InternalFeature.CLAUSE_STATUS, phrase
					.getFeature(InternalFeature.CLAUSE_STATUS));
		}
		if (phrase.hasFeature(Feature.INTERROGATIVE_TYPE)) {
			child.setFeature(InternalFeature.IGNORE_MODAL, true);
		}
	}

	/**
	 * Checks to see if the specifier can be raised and then raises it. In order
	 * to be raised the specifier must be the same on all coordinates. For
	 * example, <em>the cat and the dog</em> will be realised as
	 * <em>the cat and dog</em> while <em>the cat and any dog</em> will remain
	 * <em>the cat and any dog</em>.
	 * 
	 * @param children
	 *            the <code>List</code> of coordinates in the
	 *            <code>CoordinatedPhraseElement</code>
	 */
	private static void raiseSpecifier(List<NLGElement> children) {
		boolean allMatch = true;
		NLGElement child = children.get(0);
		NLGElement specifier = null;
		String test = null;
		if (child != null) {
			specifier = child.getFeatureAsElement(InternalFeature.SPECIFIER);
			if (specifier != null) {
				test = specifier.getFeatureAsString(LexicalFeature.BASE_FORM);
			}
			if (test != null) {
				int index = 1;
				while (index < children.size() && allMatch) {
					child = children.get(index);
					if (child == null) {
						allMatch = false;
					} else {
						specifier = child
								.getFeatureAsElement(InternalFeature.SPECIFIER);
						if (!test.equals(specifier
								.getFeatureAsString(LexicalFeature.BASE_FORM))) {
							allMatch = false;
						}
					}
					index++;
				}
				if (allMatch) {
					for (int eachChild = 1; eachChild < children.size(); eachChild++) {
						child = children.get(eachChild);
						child.setFeature(InternalFeature.RAISED, true);
					}
				}
			}
		}
	}
}
