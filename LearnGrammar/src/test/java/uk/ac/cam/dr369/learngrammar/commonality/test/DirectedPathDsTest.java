package uk.ac.cam.dr369.learngrammar.commonality.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import uk.ac.cam.dr369.learngrammar.commonality.FeatureProfile;
import uk.ac.cam.dr369.learngrammar.commonality.Twig;
import uk.ac.cam.dr369.learngrammar.model.CandcPtbPos;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType;
import uk.ac.cam.dr369.learngrammar.model.Token;

import com.google.common.collect.Lists;

public class DirectedPathDsTest {

	@Test
	public void testDescribe() {
		// Initial assertions for Token.describe() are just to isolate the error.
		Token tokLlama = new Token(
				"llama", null, // lemma, suffix
				-4, null, // index, POS
				null, null); // supertag, word
		assertEquals("llama", tokLlama.describe(true));
		assertEquals("a token with the lemma 'llama'", tokLlama.describe(false));
		
		Token tokSupertag = new Token(
				null, null, // lemma, suffix
				-4, null, // index, POS
				"(N/N)\\N", null); // supertag, word
		assertEquals("#(N/N)\\N", tokSupertag.describe(true));
		assertEquals("a token with C&C supertag '(N/N)\\N'", tokSupertag.describe(false));
		
		GrammaticalRelation gr = new GrammaticalRelation(
				GrType.DETERMINER, null, null, tokSupertag, tokLlama);
		
		FeatureProfile fp = new FeatureProfile(true, true, true, true, true, 1, 1, 1);
		Twig dpds = new Twig(Lists.newArrayList(gr), Lists.newArrayList(tokLlama, tokSupertag), fp);
		assertEquals("<llama>(det)<#(N/N)\\N>", dpds.describe(true));
		assertEquals("a token with the lemma 'llama' which is a determiner of a token with C&C supertag '(N/N)\\N'", dpds.describe(false));
		
		gr = gr.clone();
		tokLlama = tokLlama.clone();
		tokSupertag = tokSupertag.clone();
		fp = new FeatureProfile(true, true, true, false, true, 1, 1, 1);
		dpds = new Twig(Lists.newArrayList(gr), Lists.newArrayList(tokLlama, tokSupertag), fp);
		assertEquals("<llama><#(N/N)\\N>", dpds.describe(true));
		assertEquals("a token with the lemma 'llama' grammatically linked to a token with C&C supertag '(N/N)\\N'", dpds.describe(false));
		
		gr = gr.clone();
		tokLlama = tokLlama.clone();
		tokSupertag = tokSupertag.clone();
		fp = new FeatureProfile(false, false, false, true, true, 1, 1, 1);
		dpds = new Twig(Lists.newArrayList(gr), Lists.newArrayList(tokLlama, tokSupertag), fp);
		assertEquals("(det)", dpds.describe(true));
		assertEquals("a determiner", dpds.describe(false));
		
		Token tokSmall = new Token(
				"small", null, // lemma, suffix
				-4, CandcPtbPos.ADJECTIVE, // index, POS
				null, null); // supertag, word
		assertEquals("small|JJ", tokSmall.describe(true));
		assertEquals("a token with the lemma 'small' and POS tag 'JJ' (adjective)", tokSmall.describe(false));
		
		Token tokGoat = new Token(
				"goat", null, // lemma, suffix
				-4, CandcPtbPos.NOUN_SINGULAR_OR_MASS, // index, POS
				null, null); // supertag, word
		assertEquals("goat|NN", tokGoat.describe(true));
		assertEquals("a token with the lemma 'goat' and POS tag 'NN' (singular or mass noun)", tokGoat.describe(false));
		
		GrammaticalRelation grSmallGoat = new GrammaticalRelation(
				GrType.NON_CLAUSAL_MODIFIER, null, null, tokGoat, tokSmall);
		
		fp = new FeatureProfile(true, true, false, true, true, 1, 1, 1);
		dpds = new Twig(Lists.newArrayList(grSmallGoat), Lists.newArrayList(tokGoat, tokSmall), fp);
		assertEquals("<small|JJ>(ncmod)<goat|NN>", dpds.describe(true));
		assertEquals("a token with the lemma 'small' and POS tag 'JJ' (adjective) which is a non-clausal modifier of " +
				"a token with the lemma 'goat' and POS tag 'NN' (singular or mass noun)", dpds.describe(false));
		
		gr = gr.clone();
		tokLlama = tokLlama.clone();
		tokSupertag = tokSupertag.clone();
		grSmallGoat = grSmallGoat.clone();
		tokSmall = tokSmall.clone();
		tokGoat = tokGoat.clone();
		
		Token tokBe = new Token(
				"be", null, // lemma, suffix
				-4, CandcPtbPos.VERB_NON_3SG_PRESENT, // index, POS
				null, null); // supertag, word
		assertEquals("be|VBP", tokBe.describe(true));
		assertEquals("a token with the lemma 'be' and POS tag 'VBP' (non-3rd person singular present verb)", tokBe.describe(false));
		
		GrammaticalRelation grBeGoat = new GrammaticalRelation(
				GrType.DIRECT_OBJECT, null, null, tokBe, tokGoat);
		
		fp = new FeatureProfile(false, false, false, true, true, 1, 1, 1);
		dpds = new Twig(Lists.newArrayList(grSmallGoat, grBeGoat), Lists.newArrayList(tokGoat, tokSmall, tokBe), fp);
		assertEquals("(ncmod)(dobj)", dpds.describe(true));
		assertEquals("a non-clausal modifier grammatically linked to a direct object", dpds.describe(false));
		
		gr = gr.clone();
		grBeGoat = grBeGoat.clone();
		tokLlama = tokLlama.clone();
		tokSupertag = tokSupertag.clone();
		grSmallGoat = grSmallGoat.clone();
		tokSmall = tokSmall.clone();
		tokGoat = tokGoat.clone();
		tokBe = tokBe.clone();
		
		Token tokNull = new Token(
				null, null, // lemma, suffix
				-4, null, // index, POS
				null, null); // supertag, word
		assertEquals("NULL", tokNull.describe(true));
		assertEquals("a token", tokNull.describe(false));
		
		GrammaticalRelation grBeNull = new GrammaticalRelation(null, null, null, tokNull, tokBe);
		
		GrammaticalRelation grBeNull2 = new GrammaticalRelation(null, null, null, null, tokBe);
		
		fp = new FeatureProfile(false, false, false, true, true, 1, 1, 1);
		dpds = new Twig(Lists.newArrayList(grSmallGoat, grBeGoat, grBeNull2), Lists.newArrayList(tokGoat, tokSmall, tokBe), fp);
		assertEquals("(ncmod)(dobj)(null)", dpds.describe(true));
		assertEquals("a non-clausal modifier grammatically linked to a direct object whose head is the head of the sentence", dpds.describe(false));
		
		gr = gr.clone();
		tokLlama = tokLlama.clone();
		grBeGoat = grBeGoat.clone();
		tokSupertag = tokSupertag.clone();
		grSmallGoat = grSmallGoat.clone();
		tokSmall = tokSmall.clone();
		tokGoat = tokGoat.clone();
		tokBe = tokBe.clone();
		tokNull = tokNull.clone();
		grBeNull = grBeNull.clone();
		
		fp = new FeatureProfile(true, false, false, false, true, 1, 1, 1);
		dpds = new Twig(Lists.newArrayList(grSmallGoat, grBeGoat, grBeNull), Lists.newArrayList(tokGoat, tokSmall, tokBe, tokNull), fp);
		assertEquals("<small|JJ><goat|NN><be|VBP><null>", dpds.describe(true));
		assertEquals("a token with the lemma 'small' and POS tag 'JJ' (adjective) grammatically linked to " +
				"a token with the lemma 'goat' and POS tag 'NN' (singular or mass noun) " +
				"grammatically linked to a token with the lemma 'be' and POS tag 'VBP' (non-3rd person singular present verb) " +
				"which is the head of the sentence", dpds.describe(false));
		
		gr = gr.clone();
		tokLlama = tokLlama.clone();
		grBeGoat = grBeGoat.clone();
		tokSupertag = tokSupertag.clone();
		grSmallGoat = grSmallGoat.clone();
		tokSmall = tokSmall.clone();
		tokGoat = tokGoat.clone();
		tokBe = tokBe.clone();
		tokNull = tokNull.clone();
		grBeNull = grBeNull.clone();
		grBeNull2 = grBeNull2.clone();
		
		fp = new FeatureProfile(true, false, false, true, true, 1, 1, 1);
		dpds = new Twig(Lists.newArrayList(grSmallGoat, grBeGoat, grBeNull2), Lists.newArrayList(tokGoat, tokSmall, tokBe), fp);
		assertEquals("<small|JJ>(ncmod)<goat|NN>(dobj)<be|VBP>(null)", dpds.describe(true));
		assertEquals("a token with the lemma 'small' and POS tag 'JJ' (adjective) which is a non-clausal modifier of " +
				"a token with the lemma 'goat' and POS tag 'NN' (singular or mass noun) " +
				"which is a direct object of a token with the lemma 'be' and POS tag 'VBP' (non-3rd person singular present verb) " +
				"which is the head of the sentence", dpds.describe(false));
	}
	
	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testCompareTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testDirectedPathDsListOfGrammaticalRelationListOfTokenFeatureProfile() {
		fail("Not yet implemented");
	}

	@Test
	public void testDirectedPathDsListOfGrammaticalRelationListOfTokenBooleanFeatureProfile() {
		fail("Not yet implemented");
	}

	@Test
	public void testProfile() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubsumes() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetScore() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubstructuresOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubstructure() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLeaf() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPartialStructuresTokenFeatureProfileInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPartialStructuresTokenFeatureProfileIntBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testWeight() {
		fail("Not yet implemented");
	}

	@Test
	public void testIterator() {
		fail("Not yet implemented");
	}

	@Test
	public void testGet() {
		fail("Not yet implemented");
	}

}
