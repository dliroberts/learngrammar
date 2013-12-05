/*
 * VPPhraseSpec.java - A representation of a verb phrase
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
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.InternalFeature;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.framework.CoordinatedPhraseElement;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseCategory;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.WordElement;
/**
 * <p>
 * This class defines a verb phrase.  It is essentially
 * a wrapper around the <code>PhraseElement</code> class, with methods
 * for setting common constituents such as Objects.
 * For example, the <code>setVerb</code> method in this class sets
 * the head of the element to be the specified verb
 *
 * From an API perspective, this class is a simplified version of the SPhraseSpec
 * class in simplenlg V3.  It provides an alternative way for creating syntactic
 * structures, compared to directly manipulating a V4 <code>PhraseElement</code>.
 * 
 * Methods are provided for setting and getting the following constituents: 
 * <UL>
 * <LI>PreModifier		(eg, "reluctantly")
 * <LI>Verb				(eg, "gave")
 * <LI>IndirectObject	(eg, "Mary")
 * <LI>Object	        (eg, "an apple")
 * <LI>PostModifier     (eg, "before school")
 * </UL>
 * 
 * NOTE: If there is a complex verb group, a preModifer set at the VP level appears before
 * the verb, while a preModifier set at the clause level appears before the verb group.  Eg
 *   "Mary unfortunately will eat the apple"  ("unfortunately" is clause preModifier)
 *   "Mary will happily eat the apple"  ("happily" is VP preModifier)
 *   
 * NOTE: The setModifier method will attempt to automatically determine whether
 * a modifier should be expressed as a PreModifier or PostModifier
 * 
 * Features (such as negated) must be accessed via the <code>setFeature</code> and
 * <code>getFeature</code> methods (inherited from <code>NLGElement</code>).
 * Features which are often set on VPPhraseSpec include
 * <UL>
 * <LI>Modal    (eg, "John eats an apple" vs "John can eat an apple")
 * <LI>Negated  (eg, "John eats an apple" vs "John does not eat an apple")
 * <LI>Passive  (eg, "John eats an apple" vs "An apple is eaten by John")
 * <LI>Perfect  (eg, "John ate an apple" vs "John has eaten an apple")
 * <LI>Progressive  (eg, "John eats an apple" vs "John is eating an apple")
 * <LI>Tense    (eg, "John ate" vs "John eats" vs "John will eat")
 * </UL>
 * Note that most VP features can be set on an SPhraseSpec, they will automatically
 * be propogated to the VP
 * 
 * <code>VPPhraseSpec</code> are produced by the <code>createVerbPhrase</code>
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
public class VPPhraseSpec extends PhraseElement {

	
	/** create an empty clause
	 */
	public VPPhraseSpec(NLGFactory phraseFactory) {
		super(PhraseCategory.VERB_PHRASE);
		this.setFactory(phraseFactory);
		
		// set default feature values
		setFeature(Feature.PERFECT, false);
		setFeature(Feature.PROGRESSIVE, false);
		setFeature(Feature.PASSIVE, false);
		setNegated(false);
		setTense(Tense.PRESENT);
		setFeature(Feature.PERSON, Person.THIRD);
		setPlural(false);
		setFeature(Feature.FORM, Form.NORMAL);
		setFeature(InternalFeature.REALISE_AUXILIARY, true);
	}
	
	/** sets the verb (head) of a verb phrase
	 * @param verb
	 */
	public void setVerb(Object verb) {
		NLGElement verbElement = getFactory().createNLGElement(verb, LexicalCategory.VERB);
		setHead(verbElement);
	}

	/**
	 * @return verb (head) of verb phrase
	 */
	public NLGElement getVerb() {
		return getHead();
	}
	
	/** Sets the direct object of a clause  (assumes this is the only direct object)
	 *
	 * @param object
	 */
	public void setObject(Object object) {
		NLGElement objectPhrase;
		if (object instanceof PhraseElement || object instanceof CoordinatedPhraseElement)
			objectPhrase = (NLGElement) object;
		else
			objectPhrase = getFactory().createNounPhrase(object);

		objectPhrase.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.OBJECT);
		addComplement(objectPhrase);
	}
	
	
	/** Returns the direct object of a clause (assumes there is only one)
	 * 
	 * @return subject of clause (assume only one)
	 */
	public NLGElement getObject() {
		List<NLGElement> complements = getFeatureAsElementList(InternalFeature.COMPLEMENTS);
		for (NLGElement complement: complements)
			if (complement.getFeature(InternalFeature.DISCOURSE_FUNCTION) == DiscourseFunction.OBJECT)
				return complement;
		return null;
	}

	/** Set the indirect object of a clause (assumes this is the only direct indirect object)
	 *
	 * @param indirectObject
	 */
	public void setIndirectObject(Object indirectObject) {
		NLGElement indirectObjectPhrase;
		if (indirectObject instanceof PhraseElement || indirectObject instanceof CoordinatedPhraseElement)
			indirectObjectPhrase = (NLGElement) indirectObject;
		else
			indirectObjectPhrase = getFactory().createNounPhrase(indirectObject);

		indirectObjectPhrase.setFeature(InternalFeature.DISCOURSE_FUNCTION, DiscourseFunction.INDIRECT_OBJECT);
		addComplement(indirectObjectPhrase);
	}
	
	/** Returns the indirect object of a clause (assumes there is only one)
	 * 
	 * @return subject of clause (assume only one)
	 */
	public NLGElement getIndirectObject() {
		List<NLGElement> complements = getFeatureAsElementList(InternalFeature.COMPLEMENTS);
		for (NLGElement complement: complements)
			if (complement.getFeature(InternalFeature.DISCOURSE_FUNCTION) == DiscourseFunction.INDIRECT_OBJECT)
				return complement;
		return null;
	}

	// note that addFrontModifier, addPostModifier, addPreModifier are inherited from PhraseElement
	// likewise getFrontModifiers, getPostModifiers, getPreModifiers

	
	/** Add a modifier to a verb phrase
	 * Use heuristics to decide where it goes
	 * @param modifier
	 */
	@Override
	public void addModifier(Object modifier) {
		// adverb is preModifier
		// string which is one lexicographic word is looked up in lexicon,
		// if it is an adverb than it becomes a preModifier
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
		
		// if no modifier element, must be a complex string
		if (modifierElement == null) {
			addPostModifier((String)modifier);
			return;
		}
		
		// extract WordElement if modifier is a single word
		WordElement modifierWord = null;
		if (modifierElement != null && modifierElement instanceof WordElement)
			modifierWord = (WordElement) modifierElement;
		else if (modifierElement != null && modifierElement instanceof InflectedWordElement)
			modifierWord = ((InflectedWordElement) modifierElement).getBaseWord();
		
		if (modifierWord != null && modifierWord.getCategory() == LexicalCategory.ADVERB) {
			addPreModifier(modifierWord);
			return;
		}
		
		// default case
		addPostModifier(modifierElement);
	}


}
