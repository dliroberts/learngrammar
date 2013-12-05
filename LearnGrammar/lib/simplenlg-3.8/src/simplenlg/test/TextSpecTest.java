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
package simplenlg.test;

import junit.framework.Assert;

import org.junit.Test;

import simplenlg.features.Form;
import simplenlg.features.Tense;
import simplenlg.realiser.DocStructure;
import simplenlg.realiser.NPPhraseSpec;
import simplenlg.realiser.Realiser;
import simplenlg.realiser.SPhraseSpec;
import simplenlg.realiser.TextSpec;

// TODO: Auto-generated Javadoc
/**
 * Tests for the TextSpec class.
 * 
 * @author ereiter
 */
public class TextSpecTest extends SimplenlgTest {

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
		SPhraseSpec p1 = new SPhraseSpec("You", "be", "happy");
		SPhraseSpec p2 = new SPhraseSpec("I", "be", "sad");
		SPhraseSpec p3 = new SPhraseSpec("they", "be", "nervous");

		// try as a single TextSpec, should be one sentence
		TextSpec t = new TextSpec(p1, p2, p3); // wrap into a TextSpec
		Assert.assertEquals("You are happy, I am sad and they are nervous.",
				this.realiser.realise(t));

		// try as seperate text specs, should be sep sentences
		TextSpec t2 = new TextSpec(new TextSpec(p1), new TextSpec(p2),
				new TextSpec(p3));

		Assert.assertEquals("You are happy. I am sad. They are nervous.",
				this.realiser.realise(t2));
	}

	/**
	 * combine two S's using cue phrase and gerund.
	 */
	@Test
	public void testGerund() {
		NPPhraseSpec comp = new NPPhraseSpec("the", "baby");
		SPhraseSpec p1 = new SPhraseSpec();
		p1.setHead("give");
		p1.addComplement(comp);
		p1.setPassive(true);
		p1.addIndirectObject("50mg of morphine");
		p1.setCuePhrase("after");
		p1.setForm(Form.GERUND);
		Assert.assertEquals("After the baby's being given 50mg of morphine.",
				this.realiser.realise(p1));

		SPhraseSpec p2 = new SPhraseSpec();
		p2.setHead("intubate");
		p2.addComplement("the baby");
		p2.setPassive(true);
		p2.setTense(Tense.PAST);

		TextSpec t1 = new TextSpec(p1, p2);
		Assert
				.assertEquals(
						"After the baby's being given 50mg of morphine, the baby was intubated.",
						this.realiser.realise(t1));
	}

	@Test
	public void testSections() {
		// complex test involving doc which contains a section, subsection,
		// indented list of para
		TextSpec doc = new TextSpec();
		doc.setDocument();
		doc.setHeading("Test Document");

		TextSpec section = new TextSpec();
		section.setDocStructure(DocStructure.SECTION);
		section.setHeading("Test Section");
		doc.addSpec(section);

		TextSpec subsection = new TextSpec();
		subsection.setDocStructure(DocStructure.SUBSECTION);
		subsection.setHeading("Test Subsection");
		section.addSpec(subsection);

		TextSpec list = new TextSpec();
		list.setDocStructure(DocStructure.PARAGRAPHSET);
		list.setIndentedList(true);
		subsection.addSpec(list);

		TextSpec para1 = new TextSpec();
		para1.setParagraph();
		para1.addChild("This is the first test paragraph.");
		list.addSpec(para1);

		TextSpec para2 = new TextSpec();
		para2.setParagraph();
		para2.addChild("This is the second test paragraph.");
		list.addSpec(para2);

		Assert
				.assertEquals(
						"Test Document\r\n\r\nTest Section\r\n\r\nTest Subsection\r\n\r\n * This is the first test paragraph.\r\n\r\n * This is the second test paragraph.\r\n",
						this.realiser.realise(doc));

		Realiser htmlRealiser = new Realiser();
		htmlRealiser.setHTML(true);
		Assert
				.assertEquals(
						"<BODY><H1>Test Document</H1>\r\n<H2>Test Section</H2>\r\n<H3>Test Subsection</H3>\r\n<UL><LI>This is the first test paragraph.</LI>\r\n<LI>This is the second test paragraph.</LI>\r\n</UL>\r\n</BODY>\r\n",
						htmlRealiser.realise(doc));

		// now lets try a doc with a header, header-less section and subsection,
		// and 2 paras (no list)
		doc = new TextSpec();
		doc.setDocument();
		doc.setHeading("Test Document2");

		section = new TextSpec();
		section.setDocStructure(DocStructure.SECTION);
		;
		doc.addSpec(section);

		subsection = new TextSpec();
		subsection.setDocStructure(DocStructure.SUBSECTION);
		section.addSpec(subsection);

		// use list from above, with indent
		subsection.addChild(list);
		list.setIndentedList(false);

		Assert
				.assertEquals(
						"Test Document2\r\n\r\nThis is the first test paragraph.\r\n\r\nThis is the second test paragraph.\r\n",
						this.realiser.realise(doc));

		Assert
				.assertEquals(
						"<BODY><H1>Test Document2</H1>\r\n<P>This is the first test paragraph.</P>\r\n<P>This is the second test paragraph.</P>\r\n</BODY>\r\n",
						htmlRealiser.realise(doc));

	}

}
