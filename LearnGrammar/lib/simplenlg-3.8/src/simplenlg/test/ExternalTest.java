package simplenlg.test;

import junit.framework.Assert;

import org.junit.Test;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Form;
import simplenlg.features.InterrogativeType;
import simplenlg.features.Tense;
import simplenlg.lexicon.Lexicon;
import simplenlg.realiser.CoordinateNPPhraseSpec;
import simplenlg.realiser.DocStructure;
import simplenlg.realiser.NPPhraseSpec;
import simplenlg.realiser.PPPhraseSpec;
import simplenlg.realiser.Realiser;
import simplenlg.realiser.SPhraseSpec;
import simplenlg.realiser.TextSpec;
import simplenlg.realiser.VPPhraseSpec;

/**
 * Tests from third parties
 * 
 * @author ereiter
 * 
 */
public class ExternalTest extends SimplenlgTest {

	public ExternalTest(String name) {
		super(name);
	}

	/**
	 * Basic tests
	 * 
	 */
	@Test
	public void testForcher() {
		// Bjorn Forcher's tests
		SPhraseSpec s1 = new SPhraseSpec();
		s1.setHead("associate");
		s1.setPassive(true);
		s1.addComplement("Marie");
		PPPhraseSpec pp1 = new PPPhraseSpec("with");
		pp1.addComplement("Peter");
		pp1.addComplement("Paul");
		s1.addModifier(pp1);

		Assert.assertEquals("Marie is associated with Peter and Paul.",
				this.realiser.realise(s1));

		SPhraseSpec s2 = new SPhraseSpec();
		s2.addSubject("Peter");
		s2.setHead("have");
		s2.addComplement("something to do");
		s2.addModifier(new PPPhraseSpec("with", "Paul"));

		Assert.assertEquals("Peter has something to do with Paul.",
				this.realiser.realise(s2));
	}

	@Test
	public void testLu() {
		// Xin Lu's test
		SPhraseSpec s1 = new SPhraseSpec();
		s1.addSubject("we");
		s1.setHead("consider");
		s1.addComplement("John");
		s1.addPostmodifier("a friend");

		Assert.assertEquals("We consider John a friend.", this.realiser
				.realise(s1));
	}

	@Test
	public void testDwight() {
		// Rachel Dwight's test
		SPhraseSpec sentence1 = new SPhraseSpec();
		VPPhraseSpec verbPhrase1 = new VPPhraseSpec();
		verbPhrase1.setTense(Tense.PRESENT);
		verbPhrase1.setHead("have");
		sentence1.setVerbPhrase(verbPhrase1);
		NPPhraseSpec noun1 = new NPPhraseSpec();
		noun1.setHead("patient's mother");
		noun1.setSpecifier("the");
		NPPhraseSpec noun2 = new NPPhraseSpec();
		noun2.setHead("patient's father");
		noun2.setSpecifier("the");
		CoordinateNPPhraseSpec coordNoun1 = (CoordinateNPPhraseSpec) noun1
				.coordinate(noun2);
		coordNoun1.setConjunction("or");
		sentence1.addSubject(coordNoun1);
		NPPhraseSpec noun3 = new NPPhraseSpec();
		noun3.setHead("changed copy");
		noun3.addPremodifier("one");
		PPPhraseSpec prep1 = new PPPhraseSpec();
		prep1.setHead("of");
		NPPhraseSpec noun4 = new NPPhraseSpec();
		noun4.setHead("the FGFR3 gene in every cell");
		prep1.addComplement(noun4);
		noun3.addComplement(prep1);
		sentence1.addComplement(noun3);

		Assert
				.assertEquals(
						"The patient's mother or the patient's father has one changed copy of the FGFR3 gene in every cell.",
						this.realiser.realise(sentence1));
		// Rachel's second test

		Lexicon ourLexicon = new Lexicon();
		sentence1 = new SPhraseSpec();
		verbPhrase1 = new VPPhraseSpec();
		sentence1.setTense(Tense.PAST);
		verbPhrase1.setHead("perform");
		sentence1.setVerbPhrase(verbPhrase1);
		noun1 = new NPPhraseSpec();
		noun1.setHead("clinic");
		noun1.setSpecifier("the");
		sentence1.addSubject(noun1);
		noun2 = new NPPhraseSpec();
		noun2.setHead("LDL test");
		noun2.setSpecifier("an");
		noun3 = new NPPhraseSpec();
		noun3.setHead("gene test");
		noun3.setSpecifier("a");
		sentence1.addComplement(noun2);
		sentence1.addComplement(noun3);
		TextSpec phraseTextSpec1 = new TextSpec();
		phraseTextSpec1.setDocStructure(DocStructure.PHRASE);
		phraseTextSpec1.setListConjunct("");
		phraseTextSpec1.addSpec(sentence1);
		TextSpec phraseTextSpec2 = new TextSpec();
		phraseTextSpec2.addSpec(phraseTextSpec1);
		phraseTextSpec2.setSentence();
		Realiser r = new Realiser(ourLexicon);

		Assert.assertEquals(
				"The clinic performed an LDL test and a gene test.", r
						.realise(phraseTextSpec2));
	}

	@Test
	public void testNovelli() {
		// Nicole Novelli's test
		SPhraseSpec p = new SPhraseSpec("Mary", "chase", "George");
		PPPhraseSpec pp = new PPPhraseSpec("in", "the park");
		p.addModifier(pp);

		Assert.assertEquals("Mary chases George in the park.", this.realiser
				.realise(p));

		// another question from Nicole
		SPhraseSpec run = new SPhraseSpec("you", "go", "running");
		run.setModal("should");
		run.addModifier("really");
		SPhraseSpec think = new SPhraseSpec("I", "think");
		think.addComplement(run);
		run.suppressComplementiser(true);
		
		String text = this.realiser.realise(think);
		Assert.assertEquals("I think you should really go running.", text);
	}

	@Test
	public void testPiotrek() {
		// Piotrek Smulikowski's test
		SPhraseSpec sent = new SPhraseSpec();
		sent.addSubject("I");
		sent.setHead("shoot");
		sent.setTense(Tense.PAST);
		sent.addComplement("the duck");
		PPPhraseSpec loc = new PPPhraseSpec("at", "the Shooting Range");
		sent.addModifier(loc);
		sent.setCuePhrase("then");

		Assert.assertEquals("Then I shot the duck at the Shooting Range.",
				this.realiser.realise(sent));
	}

	@Test
	public void testPrescott() {
		// Michael Prescott's test
		SPhraseSpec embedded = new SPhraseSpec("Jill", "prod", "Spot");
		SPhraseSpec sent = new SPhraseSpec("Jack", "see", embedded);
		embedded.suppressComplementiser(true);
		embedded.setForm(Form.BARE_INFINITIVE);

		Assert.assertEquals("Jack sees Jill prod Spot.", this.realiser
				.realise(sent));
	}

	@Test
	public void testWissner() {
		// Michael Wissner's text

		SPhraseSpec p = new SPhraseSpec();
		p.setSubject("a wolf");
		p.setHead("eat");
		p.setInterrogative(InterrogativeType.WHAT, DiscourseFunction.OBJECT);

		Assert.assertEquals("What does a wolf eat?", this.realiser
				.realise(p));

	}

}
