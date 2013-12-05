/*
 * MorphologyProcessor.java - the processing module for handling morphology.
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
package simplenlg.morphology.english;

import java.util.ArrayList;
import java.util.List;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.InternalFeature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.ElementCategory;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGModule;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;

/**
 * <p>
 * This is the processor for handling morphology within the SimpleNLG. The
 * processor inflects words form the base form depending on the features applied
 * to the word. For example, <em>kiss</em> is inflected to <em>kissed</em> for
 * past tense, <em>dog</em> is inflected to <em>dogs</em> for pluralisation.
 * </p>
 * 
 * <p>
 * As a matter of course, the processor will first use any user-defined
 * inflection for the world. If no inflection is provided then the lexicon, if
 * it exists, will be examined for the correct inflection. Failing this a set of
 * very basic rules will be examined to inflect the word.
 * </p>
 * 
 * <p>
 * All processing modules perform realisation on a tree of
 * <code>NLGElement</code>s. The modules can alter the tree in whichever way
 * they wish. For example, the syntax processor replaces phrase elements with
 * list elements consisting of inflected words while the morphology processor
 * replaces inflected words with string elements.
 * </p>
 * 
 * <p>
 * <b>N.B.</b> the use of <em>module</em>, <em>processing module</em> and
 * <em>processor</em> is interchangeable. They all mean an instance of this
 * class.
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
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 */
public class MorphologyProcessor extends NLGModule {

	@Override
	public void initialise() {
		// Do nothing
	}

	@Override
	public NLGElement realise(NLGElement element) {
		NLGElement realisedElement = null;
		if (element instanceof InflectedWordElement) {
			realisedElement = doMorphology((InflectedWordElement) element);
		} else if (element instanceof StringElement) {
			realisedElement = element;
		} else if (element instanceof WordElement) {
			String baseForm = ((WordElement) element).getBaseForm();
			if (baseForm != null) {
				realisedElement = new StringElement(baseForm);
			}
		} else if (element instanceof DocumentElement) {
			List<NLGElement> children = element.getChildren();
			((DocumentElement) element).setComponents(realise(children));
			realisedElement = element;
		} else if (element instanceof ListElement) {
			realisedElement = new ListElement();
			((ListElement) realisedElement).addComponents(realise(element
					.getChildren()));
		} else if (element instanceof CoordinatedPhraseElement) {
			List<NLGElement> children = element.getChildren();
			((CoordinatedPhraseElement) element).clearCoordinates();

			if (children != null && children.size() > 0) {
				((CoordinatedPhraseElement) element)
						.addCoordinate(realise(children.get(0)));
				for (int index = 1; index < children.size(); index++) {
					((CoordinatedPhraseElement) element)
							.addCoordinate(realise(children.get(index)));
				}
				realisedElement = element;
			}
		} else if (element != null) {
			realisedElement = element;
		}
		return realisedElement;
	}

	/**
	 * This is the main method for performing the morphology. It effectively
	 * examines the lexical category of the element and calls the relevant set
	 * of rules from <code>MorphologyRules</em>.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>
	 * @return an <code>NLGElement</code> reflecting the correct inflection for
	 *         the word.
	 */
	private NLGElement doMorphology(InflectedWordElement element) {
		NLGElement realisedElement = null;
		if (element.getFeatureAsBoolean(InternalFeature.NON_MORPH).booleanValue()) {
			realisedElement = new StringElement(element.getBaseForm());
			realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, element
					.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		} else {
			NLGElement baseWord = element
					.getFeatureAsElement(InternalFeature.BASE_WORD);

			if (baseWord == null && this.lexicon != null) {
				baseWord = this.lexicon.lookupWord(element.getBaseForm());
			}
			ElementCategory category = element.getCategory();
			if (category instanceof LexicalCategory) {
				switch ((LexicalCategory) category) {
				case PRONOUN:
					realisedElement = MorphologyRules
							.doPronounMorphology(element);
					break;

				case NOUN:
					realisedElement = MorphologyRules.doNounMorphology(element,
							(WordElement) baseWord);
					break;

				case VERB:
					realisedElement = MorphologyRules.doVerbMorphology(element,
							(WordElement) baseWord);
					break;

				case ADJECTIVE:
					realisedElement = MorphologyRules.doAdjectiveMorphology(
							element, (WordElement) baseWord);
					break;

				case ADVERB:
					realisedElement = MorphologyRules.doAdverbMorphology(
							element, (WordElement) baseWord);
					break;

				default:
					realisedElement = new StringElement(element.getBaseForm());
					realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION,
							element.getFeature(InternalFeature.DISCOURSE_FUNCTION));
				}
			}
		}
		return realisedElement;
	}

	@Override
	public List<NLGElement> realise(List<NLGElement> elements) {
		List<NLGElement> realisedElements = new ArrayList<NLGElement>();
		NLGElement currentElement = null;
		NLGElement determiner = null;

		if (elements != null) {
			for (NLGElement eachElement : elements) {
				currentElement = realise(eachElement);
				if (currentElement != null) {
					realisedElements.add(realise(currentElement));
					if (determiner == null
							&& DiscourseFunction.SPECIFIER
									.equals(currentElement
											.getFeature(InternalFeature.DISCOURSE_FUNCTION))) {
						determiner = currentElement;
						determiner.setFeature(Feature.NUMBER, eachElement
								.getFeature(Feature.NUMBER));
					} else if (determiner != null) {
						MorphologyRules.doDeterminerMorphology(determiner,
								currentElement.getRealisation());
						determiner = null;
					}
				}
			}
		}
		return realisedElements;
	}
}
