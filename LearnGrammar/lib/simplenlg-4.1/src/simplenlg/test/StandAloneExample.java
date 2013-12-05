/*
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
package simplenlg.test;

import java.util.ArrayList;

import simplenlg.features.Feature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.Tense;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.LexicalCategory;
import simplenlg.framework.ListElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.framework.NLGFactory;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;
import simplenlg.lexicon.Lexicon;
import simplenlg.lexicon.NIHDBLexicon;
import simplenlg.lexicon.XMLLexicon;
import simplenlg.morphology.english.MorphologyProcessor;
import simplenlg.phrasespec.AdjPhraseSpec;
import simplenlg.phrasespec.NPPhraseSpec;
import simplenlg.phrasespec.PPPhraseSpec;
import simplenlg.phrasespec.SPhraseSpec;
import simplenlg.realiser.english.Realiser;

/**
 *  <hr>
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
 * @author D. Westwater, Data2Text Ltd
 *
 */
public class StandAloneExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// below is a simple complete example of using simplenlg V4
		// afterwards is an example of using simplenlg just for morphology
		
		// set up
		Lexicon lexicon = new XMLLexicon();                          // default simplenlg lexicon
		NLGFactory nlgFactory = new NLGFactory(lexicon);             // factory based on lexicon

		// create sentences
		// 	"John did not go to the bigger park. He played football there."
		NPPhraseSpec thePark = nlgFactory.createNounPhrase("the", "park");   // create an NP
		AdjPhraseSpec bigp = nlgFactory.createAdjectivePhrase("big");        // create AdjP
		bigp.setFeature(Feature.IS_COMPARATIVE, true);                       // use comparative form ("bigger")
		thePark.addModifier(bigp);                                        // add adj as modifier in NP
		// above relies on default placement rules.  You can force placement as a premodifier
		// (before head) by using addPreModifier
		PPPhraseSpec toThePark = nlgFactory.createPrepositionPhrase("to");    // create a PP
		toThePark.setObject(thePark);                                     // set PP object
		// could also just say nlgFactory.createPrepositionPhrase("to", the Park);

		SPhraseSpec johnGoToThePark = nlgFactory.createClause("John",      // create sentence
				"go", toThePark);

		johnGoToThePark.setFeature(Feature.TENSE,Tense.PAST);              // set tense
		johnGoToThePark.setFeature(Feature.NEGATED, true);                 // set negated
		
		// note that constituents (such as subject and object) are set with setXXX methods
		// while features are set with setFeature

		DocumentElement sentence = nlgFactory							// create a sentence DocumentElement from SPhraseSpec
				.createSentence(johnGoToThePark);

		// below creates a sentence DocumentElement by concatenating strings
		StringElement hePlayed = new StringElement("he played");        
		StringElement there = new StringElement("there");
		WordElement football = new WordElement("football");

		DocumentElement sentence2 = nlgFactory.createSentence();
		sentence2.addComponent(hePlayed);
		sentence2.addComponent(football);
		sentence2.addComponent(there);

		// now create a paragraph which contains these sentences
		DocumentElement paragraph = nlgFactory.createParagraph();
		paragraph.addComponent(sentence);
		paragraph.addComponent(sentence2);

		// create a realiser.  Note that a lexicon is specified, this should be
		// the same one used by the NLGFactory
		Realiser realiser = new Realiser(lexicon);
		//realiser.setDebugMode(true);     // uncomment this to print out debug info during realisation
		NLGElement realised = realiser.realise(paragraph);

		System.out.println(realised.getRealisation());

		// end of main example
		
		// second example - using simplenlg just for morphology
		// in V4 morphology is done by a MorphologyProcessor, not by the lexicon
		
		MorphologyProcessor morph = new MorphologyProcessor();

		// create inflected word
		NLGElement word = nlgFactory.createWord("child", LexicalCategory.NOUN);
		// setPlural is an exception to the general rule that features are set with setFeature
		word.setPlural(true);
		
		// get result from morph processor
		String result = morph.realise(word).getRealisation();
		
		System.out.println(result);
	}
}
