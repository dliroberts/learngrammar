package uk.ac.cam.dr369.learngrammar.model.test;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.BeforeClass;
import org.junit.Test;

import uk.ac.cam.dr369.learngrammar.model.CandcPtbPos;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser;

public class TokenTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
		URL dictUrl = new URL("file", null, "lib/wordnet"+File.separator+"WordNet-3.0"+File.separator+"dict");
		WordnetSemanticAnalyser.getInstance(dictUrl); // initialises singleton internally with dictionary
	}

	@Test
	public void testTokenStringStringIntPosStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testTokenStringStringIntPosStringStringVerbFrame() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSupertag() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetWord() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLemma() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPos() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSuffix() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIndex() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGrs() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetVerbFrame() {
		fail("Not yet implemented");
	}

	@Test
	public void testInitialiseGrs() {
		fail("Not yet implemented");
	}

	@Test
	public void testNormaliseCapitalisation() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsHeadOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsDependentOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsSubtypeOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testHeadOfGrType() {
		fail("Not yet implemented");
	}

	@Test
	public void testDependentOfGrType() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubtypeOfGrType() {
		fail("Not yet implemented");
	}

	@Test
	public void testHeadOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testDependentOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testSubtypeOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testChildOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testXOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsXOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAncestorTokens() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAncestorGrs() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAncestorGrHierarchies() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAncestorTokenHierarchies() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAncestorHierarchies() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAncestors() {
		fail("Not yet implemented");
	}

	@Test
	public void testClone() {
		fail("Not yet implemented");
	}

	@Test
	public void testEqualsObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testToString() {
		fail("Not yet implemented");
	}

	@Test
	public void testDescribe() {
		Token tok = new Token(
				"llama", "ed", // lemma, suffix
				4, CandcPtbPos.VERB_PAST_PARTICIPLE, // index, POS
				"(N/N)\\N", "llamad"); // supertag, word
		assertEquals("llama+ed:4|VBN#(N/N)\\N", tok.describe(true));
		assertEquals(
				"a token with the lemma 'llama' and the suffix 'ed', occurring as word #5 in the sentence, with " +
				"POS tag 'VBN' (past participle verb) and C&C supertag '(N/N)\\N'", tok.describe(false));
		tok = new Token(
				"llama", null, // lemma, suffix
				4, CandcPtbPos.VERB_PAST_PARTICIPLE, // index, POS
				"(N/N)\\N", "llamad"); // supertag, word
		assertEquals("llamad:4|VBN#(N/N)\\N", tok.describe(true));
		assertEquals(
				"the word 'llamad', occurring as word #5 in the sentence, with " +
				"POS tag 'VBN' (past participle verb) and C&C supertag '(N/N)\\N'", tok.describe(false));
		tok = new Token(
				"llama", null, // lemma, suffix
				4, CandcPtbPos.VERB_PAST_PARTICIPLE, // index, POS
				"(N/N)\\N", null); // supertag, word
		assertEquals("llama:4|VBN#(N/N)\\N", tok.describe(true));
		assertEquals(
				"a token with the lemma 'llama', occurring as word #5 in the sentence, with " +
				"POS tag 'VBN' (past participle verb) and C&C supertag '(N/N)\\N'", tok.describe(false));
		tok = new Token(
				"llama", null, // lemma, suffix
				-4, CandcPtbPos.VERB_PAST_PARTICIPLE, // index, POS
				"(N/N)\\N", null); // supertag, word
		assertEquals("llama|VBN#(N/N)\\N", tok.describe(true));
		assertEquals(
				"a token with the lemma 'llama', " +
				"POS tag 'VBN' (past participle verb) and C&C supertag '(N/N)\\N'", tok.describe(false));
		tok = new Token(
				"llama", null, // lemma, suffix
				-4, CandcPtbPos.VERB_PAST_PARTICIPLE, // index, POS
				null, null); // supertag, word
		assertEquals("llama|VBN", tok.describe(true));
		assertEquals(
				"a token with the lemma 'llama' and POS tag 'VBN' (past participle verb)", tok.describe(false));
		tok = new Token(
				"llama", null, // lemma, suffix
				-4, null, // index, POS
				"(N/N)\\N", null); // supertag, word
		assertEquals("llama#(N/N)\\N", tok.describe(true));
		assertEquals(
				"a token with the lemma 'llama' and C&C supertag '(N/N)\\N'", tok.describe(false));
		tok = new Token(
				null, null, // lemma, suffix
				-4, null, // index, POS
				"(N/N)\\N", null); // supertag, word
		assertEquals("#(N/N)\\N", tok.describe(true));
		assertEquals("a token with C&C supertag '(N/N)\\N'", tok.describe(false));
		tok = new Token(
				"llama", null, // lemma, suffix
				-4, null, // index, POS
				null, null); // supertag, word
		assertEquals("llama", tok.describe(true));
		assertEquals("a token with the lemma 'llama'", tok.describe(false));
		tok = new Token(
				null, null, // lemma, suffix
				-4, null, // index, POS
				null, null); // supertag, word
		assertEquals("NULL", tok.describe(true));
		assertEquals("a token", tok.describe(false));
	}

	@Test
	public void testCompareTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsVerb() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsAdjective() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsAdverb() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsNoun() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsOpenClass() {
		fail("Not yet implemented");
	}

}
