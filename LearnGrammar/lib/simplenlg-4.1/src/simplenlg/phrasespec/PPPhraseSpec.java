/*
 * PPPhraseSpec.java - A representation of a clause
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

import java.util.List;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.InternalFeature;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.NLGFactory;

/**
 * <p>
 * This class defines a prepositional phrase.  It is essentially
 * a wrapper around the <code>PhraseElement</code> class, with methods
 * for setting common constituents such as object.
 * For example, the <code>setPreposition</code> method in this class sets
 * the head of the element to be the specified preposition
 *
 * From an API perspective, this class is a simplified version of the PPPhraseSpec
 * class in simplenlg V3.  It provides an alternative way for creating syntactic
 * structures, compared to directly manipulating a V4 <code>PhraseElement</code>.
 * 
 * Methods are provided for setting and getting the following constituents:
 * <UL>
 * <LI>Preposition		(eg, "in")
 * <LI>Object     (eg, "the shop")
 * </UL>
 * 
 * NOTE: PPPhraseSpec do not usually have modifiers or (user-set) features
 * 
 * <code>PPPhraseSpec</code> are produced by the <code>createPrepositionalPhrase</code>
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
public class PPPhraseSpec extends PhraseElement {

	public PPPhraseSpec(NLGFactory phraseFactory) {
		super(PhraseCategory.PREPOSITIONAL_PHRASE);
		this.setFactory(phraseFactory);
	}
	
	/** sets the preposition (head) of a prepositional phrase
	 * @param preposition
	 */
	public void setPreposition(Object preposition) {
		if (preposition instanceof NLGElement)
			setHead(preposition);
		else {
			// create noun as word
			NLGElement prepositionalElement = getFactory().createWord(preposition, LexicalCategory.PREPOSITION);

			// set head of NP to nounElement
			setHead(prepositionalElement);
		}
	}

	/**
	 * @return preposition (head) of prepositional phrase
	 */
	public NLGElement getPreposition() {
		return getHead();
	}
	
	/** Sets the  object of a PP
	 *
	 * @param object
	 */
	public void setObject(Object object) {
		PhraseElement objectPhrase = getFactory().createNounPhrase(object);
		objectPhrase.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.OBJECT);
		addComplement(objectPhrase);
	}
	
	
	/**
	 * @return object of PP (assume only one)
	 */
	public NLGElement getObject() {
		List<NLGElement> complements = getFeatureAsElementList(InternalFeature.COMPLEMENTS);
		for (NLGElement complement: complements)
			if (complement.getFeature(InternalFeature.DISCOURSE_FUNCTION) == DiscourseFunction.OBJECT)
				return complement;
		return null;
	}

}
