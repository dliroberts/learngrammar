package simplenlg.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import simplenlg.aggregation.BackwardConjunctionReduction;
import simplenlg.aggregation.ClauseAggregator;
import simplenlg.aggregation.ClauseCoordination;
import simplenlg.aggregation.ForwardConjunctionReduction;
import simplenlg.features.Tense;
import simplenlg.realiser.AdvPhraseSpec;
import simplenlg.realiser.CoordinateSPhraseSpec;
import simplenlg.realiser.NPPhraseSpec;
import simplenlg.realiser.SPhraseSpec;

public class AggregationTest extends SimplenlgTest {
	private ClauseAggregator _aggregator;
	SPhraseSpec s1, s2, s3;
	AdvPhraseSpec adv1, adv2, cue1, cue2;

	public AggregationTest(String name) {
		super(name);
	}

	@Override
	@Before
	public void setUp() {
		super.setUp();
		this._aggregator = new ClauseAggregator();
		this._aggregator.addRule(new ForwardConjunctionReduction());
		this._aggregator.addRule(new BackwardConjunctionReduction());

		this.adv1 = new AdvPhraseSpec();
		this.adv2 = new AdvPhraseSpec();
		this.cue1 = new AdvPhraseSpec("however");
		this.cue2 = new AdvPhraseSpec("perhaps");
		this.adv1.setHead("since");
		this.adv1.setComplement("yesterday");
		this.adv2.setHead("since");
		this.adv2.setComplement("yesterday");
		this.s1 = new SPhraseSpec();
		this.s2 = new SPhraseSpec();
		this.s3 = new SPhraseSpec();
		this.s1.setSubject(new NPPhraseSpec("the", "man"));
		this.s2.setSubject(new NPPhraseSpec("the", "man"));
		this.s3.setSubject(new NPPhraseSpec("the", "man"));
		this.kick.addComplement(new NPPhraseSpec("the", "woman"));
		this.walk.addComplement(this.onTheRock);
		this.kiss.addComplement(new NPPhraseSpec("the", "woman"));
		this.s1.setVerbPhrase(this.kick);
		this.s2.setVerbPhrase(this.walk);
		this.s3.setVerbPhrase(this.kiss);
		this.s1.setTense(Tense.PAST);
		this.s2.setTense(Tense.PAST);
		this.s3.setTense(Tense.PAST);
	}

	/**
	 * Test forward conjunction reduction based on same subject
	 */
	@Test
	public void testSubjectFCR() {
		SPhraseSpec result = this._aggregator.apply(this.s1, this.s2);
		Assert.assertTrue(result instanceof CoordinateSPhraseSpec);
		Assert.assertEquals("The man kicked the woman and walked on the rock.",
				this.realiser.realise(result));

		// if s2 has a different subject, there is no elision
		this.s2.setSubject(new NPPhraseSpec("the bird"));
		result = this._aggregator.apply(this.s1, this.s2);
		Assert.assertTrue(result == null);
	}

	/**
	 * Test forward conjunction reduction based on same front mods
	 */
	public void testFrontModifierFCR() {
		this.s1.addFrontModifier(this.adv1);
		this.s2.addFrontModifier(this.adv2);

		SPhraseSpec result = this._aggregator.apply(this.s1, this.s2);
		Assert.assertTrue(result instanceof CoordinateSPhraseSpec);
		Assert
				.assertEquals(
						"Since yesterday, the man kicked the woman and walked on the rock.",
						this.realiser.realise(result));

		// now add different cue phrases
		this.s1.setCuePhrase(this.cue1);
		this.s2.setCuePhrase(this.cue2);
		result = this._aggregator.apply(this.s1, this.s2);
		Assert.assertTrue(result instanceof CoordinateSPhraseSpec);
		Assert
				.assertEquals(
						"However since yesterday, the man kicked the woman and perhaps walked on the rock.",
						this.realiser.realise(result));
	}

	/**
	 * Test forward conjunction reduction with a multiple (>2) coordinate
	 * phrases, eliding components of the left periphery of each coordinate.
	 */
	public void testMultipleFCR() {
		SPhraseSpec result = this._aggregator.apply(this.s1, this.s2, this.s3);
		Assert.assertTrue(result instanceof CoordinateSPhraseSpec);
		Assert
				.assertEquals(
						"The man kicked the woman, walked on the rock and kissed the woman.",
						this.realiser.realise(result));
	}

	/**
	 * Test backward conjunction reduction in addition to FCR over 2 conjuncts
	 */
	public void testBCR() {
		SPhraseSpec result = this._aggregator.apply(this.s1, this.s3);
		Assert.assertTrue(result instanceof CoordinateSPhraseSpec);
		Assert.assertEquals("The man kicked and kissed the woman.",
				this.realiser.realise(result));
	}

	/**
	 * Test backward conjunction reduction in addition to FCR over >2 conjuncts:
	 * elision only applies to contiguous conjuncts. BCR fails here
	 */
	public void testFCRAndBCR() {
		SPhraseSpec result = this._aggregator.apply(this.s1, this.s2, this.s3);
		Assert.assertTrue(result instanceof CoordinateSPhraseSpec);
		Assert
				.assertEquals(
						"The man kicked the woman, walked on the rock and kissed the woman.",
						this.realiser.realise(result));
	}

	/**
	 * Test backward conjunction reduction in addition to FCR over >2 conjuncts:
	 * elision only applies if it applies to all conjuncts. FCR succeeds here,
	 * but not BCR
	 */
	public void testFCRAndBCR2() {
		SPhraseSpec result = this._aggregator.apply(this.s1, this.s3, this.s2);
		Assert.assertTrue(result instanceof CoordinateSPhraseSpec);
		Assert
				.assertEquals(
						"The man kicked the woman, kissed the woman and walked on the rock.",
						this.realiser.realise(result));
	}

	/**
	 * Test of the conjunction rules (subject)
	 */
	public void testConjunctionRule() {
		ClauseCoordination coord = new ClauseCoordination();
		SPhraseSpec result = coord.apply(this.s1, this.s2, this.s3);
		// parent is not a coordinate phrase: only subject conjoined
		Assert.assertFalse(result instanceof CoordinateSPhraseSpec);
		Assert
				.assertEquals(
						"The man kicked the woman, walked on the rock and kissed the woman.",
						this.realiser.realise(result));
	}

	/**
	 * Test aggregation with passives
	 */
	public void testPassiveConjunction() {
		ClauseCoordination coord = new ClauseCoordination();
		this.s1.setPassive(true);
		this.s3.setPassive(true);
		SPhraseSpec result = coord.apply(this.s1, this.s3);

		// parent is not a coordinate phrase: only subject conjoined
		Assert.assertFalse(result instanceof CoordinateSPhraseSpec);
		Assert.assertEquals("The woman was kicked and kissed by the man.",
				this.realiser.realise(result));
	}

	/**
	 * Simple aggregation test
	 */
	public void testSimpleAggregation() {
		ClauseAggregator aggregator = ClauseAggregator.newInstance();
		SPhraseSpec s1 = new SPhraseSpec("the man", "be", "hungry");
		SPhraseSpec s2 = new SPhraseSpec("the man", "buy", "an apple");

		SPhraseSpec result = aggregator.apply(s1, s2);

		Assert.assertEquals("The man is hungry and buys an apple.",
				this.realiser.realise(result));
	}
}
