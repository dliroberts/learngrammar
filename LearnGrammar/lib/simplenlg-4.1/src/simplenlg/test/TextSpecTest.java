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

import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Feature;
import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.format.english.TextFormatter;
import simplenlg.framework.DocumentElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.PhraseElement;
import simplenlg.phrasespec.SPhraseSpec;

/**
 * Tests for the TextSpec class.
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

 * @author ereiter
 */
public class TextSpecTest extends SimpleNLG4Test {

	/**
	 * Instantiates a new text spec test.
	 * 
	 * @param name
	 *            the name
	 */
	public TextSpecTest(String name) {
		super(name);
	}

	/**
	 * Basic tests.
	 */
	@Test
	public void testBasics() {
		this.realiser.setFormatter(new TextFormatter());
		SPhraseSpec p1 = this.phraseFactory
				.createClause("you", "be", "happy");
		SPhraseSpec p2 = this.phraseFactory.createClause("I", "be", "sad"); 
		SPhraseSpec p3 = this.phraseFactory.createClause("they", "be",
				"nervous");

		DocumentElement s1 = this.phraseFactory.createSentence(p1);
		DocumentElement s2 = this.phraseFactory.createSentence(p2);
		DocumentElement s3 = this.phraseFactory.createSentence(p3);

		DocumentElement par1 = this.phraseFactory.createParagraph(Arrays.asList(
				s1, s2, s3));

		Assert.assertEquals("You are happy. I am sad. They are nervous.\n\n",
				this.realiser.realise(par1).getRealisation());

	}

	/**
	 * test whether sents can be embedded in a section without intervening paras
	 */
	@Test
	public void testEmbedding() {
		this.realiser.setFormatter(new TextFormatter());
		DocumentElement sent = phraseFactory.createSentence("This is a test");
		DocumentElement sent2 = phraseFactory.createSentence(phraseFactory.createClause("John", "be", "missing"));
		DocumentElement section = phraseFactory.createSection("SECTION TITLE");
		section.addComponent(sent);
		section.addComponent(sent2);
		
		Assert.assertEquals("SECTION TITLE\nThis is a test.\n\nJohn is missing.\n\n",
				this.realiser.realise(section).getRealisation());

		
//		this.phraseFactory.setLexicon(this.lexicon);
//
//		PhraseElement comp = this.phraseFactory.createNounPhrase("the", "baby"); //$NON-NLS-1$ //$NON-NLS-2$
//		PhraseElement p1 = this.phraseFactory.createClause(null, "give", comp); //$NON-NLS-1$
//		p1.setFeature(Feature.PASSIVE, true);
//
//		PhraseElement morphine = this.phraseFactory
//				.createNounPhrase("50mg of morphine"); //$NON-NLS-1$
//		morphine.setFeature(Feature.DISCOURSE_FUNCTION, DiscourseFunction.INDIRECT_OBJECT);
//		p1.addComplement(morphine);
//		p1.setFeature(Feature.CUE_PHRASE, "after"); //$NON-NLS-1$
//		p1.setFeature(Feature.FORM, Form.GERUND);
//		
//		Assert.assertEquals("after the baby's being given 50mg of morphine", //$NON-NLS-1$
//				this.realiser.realise(p1).getRealisation());
//
//		PhraseElement p2 = this.phraseFactory.createClause(null, "intubate", "the baby"); //$NON-NLS-1$ //$NON-NLS-2$
//		p2.setFeature(Feature.PASSIVE, true);
//		p2.setTense(Tense.PAST);
//
//		DocumentElement t1 = DocumentElement.createSentence(p1);
//		t1.addComponent(p2);
//		this.realiser.setDebugMode(true);
//		this.realiser.realise(p2);
//		this.realiser.setDebugMode(false);
//		Assert
//				.assertEquals(
//						"After the baby's being given 50mg of morphine, the baby was intubated. \n", //$NON-NLS-1$
//						this.realiser.realise(t1).getRealisation());
	}

	@Test
	public void testSections() {
		// // complex test involving doc which contains a section, subsection,
		// // indented list of para
		// TextSpec doc = new TextSpec();
		// doc.setDocument();
		// doc.setHeading("Test Document");
		//
		// TextSpec section = new TextSpec();
		// section.setDocStructure(DocStructure.SECTION);
		// section.setHeading("Test Section");
		// doc.addSpec(section);
		//
		// TextSpec subsection = new TextSpec();
		// subsection.setDocStructure(DocStructure.SUBSECTION);
		// subsection.setHeading("Test Subsection");
		// section.addSpec(subsection);
		//
		// TextSpec list = new TextSpec();
		// list.setDocStructure(DocStructure.PARAGRAPHSET);
		// list.setIndentedList(true);
		// subsection.addSpec(list);
		//
		// TextSpec para1 = new TextSpec();
		// para1.setParagraph();
		// para1.addChild("This is the first test paragraph.");
		// list.addSpec(para1);
		//
		// TextSpec para2 = new TextSpec();
		// para2.setParagraph();
		// para2.addChild("This is the second test paragraph.");
		// list.addSpec(para2);
		//
		// Assert
		// .assertEquals(
		// "Test Document\r\n\r\nTest Section\r\n\r\nTest Subsection\r\n\r\n * This is the first test paragraph.\r\n\r\n * This is the second test paragraph.\r\n",
		// this.realiser.realise(doc));
		//
		// Realiser htmlRealiser = new Realiser();
		// htmlRealiser.setHTML(true);
		// Assert
		// .assertEquals(
		// "<BODY><H1>Test Document</H1>\r\n<H2>Test Section</H2>\r\n<H3>Test Subsection</H3>\r\n<UL><LI>This is the first test paragraph.</LI>\r\n<LI>This is the second test paragraph.</LI>\r\n</UL>\r\n</BODY>\r\n",
		// htmlRealiser.realise(doc));
		//
		// // now lets try a doc with a header, header-less section and
		// subsection,
		// // and 2 paras (no list)
		// doc = new TextSpec();
		// doc.setDocument();
		// doc.setHeading("Test Document2");
		//
		// section = new TextSpec();
		// section.setDocStructure(DocStructure.SECTION);
		// ;
		// doc.addSpec(section);
		//
		// subsection = new TextSpec();
		// subsection.setDocStructure(DocStructure.SUBSECTION);
		// section.addSpec(subsection);
		//
		// // use list from above, with indent
		// subsection.addChild(list);
		// list.setIndentedList(false);
		//
		// Assert
		// .assertEquals(
		// "Test Document2\r\n\r\nThis is the first test paragraph.\r\n\r\nThis is the second test paragraph.\r\n",
		// this.realiser.realise(doc));
		//
		// Assert
		// .assertEquals(
		// "<BODY><H1>Test Document2</H1>\r\n<P>This is the first test paragraph.</P>\r\n<P>This is the second test paragraph.</P>\r\n</BODY>\r\n",
		// htmlRealiser.realise(doc));

	}

}
