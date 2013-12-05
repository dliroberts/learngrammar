/*
 * SyntaxProcessor.java - the processing module for handling syntax.
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

import simplenlg.features.Feature;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.ElementCategory;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.NLGModule;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.WordElement;

/**
 * <p>
 * This is the processor for handling syntax within the SimpleNLG. The processor
 * translates phrases into lists of words.
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
public class SyntaxProcessor extends NLGModule {

	@Override
	public void initialise() {
		// Do nothing
	}

	@Override
	public NLGElement realise(NLGElement element) {
		NLGElement realisedElement = null;

		if (element != null
				&& !element.getFeatureAsBoolean(Feature.ELIDED).booleanValue()) {
			if (element instanceof DocumentElement) {
				List<NLGElement> children = element.getChildren();
				((DocumentElement) element).setComponents(realise(children));
				realisedElement = element;
			} else if (element instanceof PhraseElement) {
				realisedElement = realisePhraseElement((PhraseElement) element);
			} else if (element instanceof ListElement) {
				realisedElement = new ListElement();
				((ListElement) realisedElement).addComponents(realise(element
						.getChildren()));
			} else if (element instanceof InflectedWordElement) {
				String baseForm = ((InflectedWordElement) element)
						.getBaseForm();
				ElementCategory category = element.getCategory();
				if (this.lexicon != null && baseForm != null) {
					WordElement word = ((InflectedWordElement) element)
							.getBaseWord();
					if (word == null) {
						if (category instanceof LexicalCategory) {
							word = this.lexicon.lookupWord(baseForm,
									(LexicalCategory) category);
						} else {
							word = this.lexicon.lookupWord(baseForm);
						}
					}
					if (word != null) {
						((InflectedWordElement) element).setBaseWord(word);
					}
				}
				realisedElement = element;
			} else if (element instanceof CoordinatedPhraseElement) {
				realisedElement = CoordinatedPhraseHelper.realise(this,
						(CoordinatedPhraseElement) element);
			} else {
				realisedElement = element;
			}
		}

		// Remove the spurious ListElements that have only one element.
		if (realisedElement instanceof ListElement) {
			if (((ListElement) realisedElement).size() == 1) {
				realisedElement = ((ListElement) realisedElement).getFirst();
			}
		}
		return realisedElement;
	}

	@Override
	public List<NLGElement> realise(List<NLGElement> elements) {
		List<NLGElement> realisedList = new ArrayList<NLGElement>();
		NLGElement childRealisation = null;

		if (elements != null) {
			for (NLGElement eachElement : elements) {
				if (eachElement != null) {
					childRealisation = realise(eachElement);
					if (childRealisation != null) {
						if (childRealisation instanceof ListElement) {
							realisedList
									.addAll(((ListElement) childRealisation)
											.getChildren());
						} else {
							realisedList.add(childRealisation);
						}
					}
				}
			}
		}
		return realisedList;
	}

	/**
	 * Realises a phrase element.
	 * 
	 * @param phrase
	 *            the element to be realised
	 * @return the realised element.
	 */
	private NLGElement realisePhraseElement(PhraseElement phrase) {
		NLGElement realisedElement = null;
		if (phrase != null) {
			ElementCategory category = phrase.getCategory();
			if (category instanceof PhraseCategory) {
				switch ((PhraseCategory) category) {

				case CLAUSE:
					realisedElement = ClauseHelper.realise(this, phrase);
					break;

				case NOUN_PHRASE:
					realisedElement = NounPhraseHelper.realise(this, phrase);
					break;

				case VERB_PHRASE:
					realisedElement = VerbPhraseHelper.realise(this, phrase);
					break;

				case PREPOSITIONAL_PHRASE:
				case ADJECTIVE_PHRASE:
				case ADVERB_PHRASE:
					realisedElement = PhraseHelper.realise(this, phrase);
					break;

				default:
					realisedElement = phrase;
					break;
				}
			}
		}
		return realisedElement;
	}
}
