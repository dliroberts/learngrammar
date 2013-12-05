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
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import simplenlg.features.Category;
import simplenlg.lexicon.DBLexicon;
import simplenlg.lexicon.db.SQLAccessor;

// TODO: Auto-generated Javadoc
/**
 * Tests for database access using a DBAccessor on the entire NIH Lexicon
 * database. NB: Requires at least 500 meg of heapspace, else JVM runs out of
 * memory. This is due to the size of the main lexicon table (ca. 350,000
 * entries).
 * 
 * <P>
 * To run: set jvm arguments -Xmx1g. This test is very time-consuming.
 * 
 * @author agatt
 */
public class NIHLexiconDBTest extends TestCase {

	// accessor object -- the default SQLAccessor class
	SQLAccessor accessor;

	// lexicon object -- an instance of Lexicon
	DBLexicon lexicon;

	// db params
	static String DRIVER = "com.mysql.jdbc.Driver";

	// dburl
	static String DBURL = "jdbc:mysql://shallot.csd.abdn.ac.uk:3306/lexicon";

	// db username
	static String USER = "simplenlg";

	// db password
	static String PASS = "simplenlg";

	@Override
	@Before
	/*
	 * * Sets up the accessor and runs it -- takes ca. 26 sec
	 */
	public void setUp() {
		this.accessor = new SQLAccessor(NIHLexiconDBTest.DRIVER,
				NIHLexiconDBTest.DBURL, NIHLexiconDBTest.USER,
				NIHLexiconDBTest.PASS);
		this.lexicon = new DBLexicon();
		this.lexicon.setAccessor(this.accessor);
		this.lexicon.loadData();
	}

	/**
	 * Test for number of items loaded.
	 */
	@Test
	public void testItemCounts() {

		// lexicon should have 330,455 individual entries
		Assert.assertEquals(330455, this.lexicon.getNumberOfItems());

		// of which 46893 are adjectives
		Assert.assertEquals(46893, this.lexicon
				.getNumberOfItems(Category.ADJECTIVE));

		// 5881 adverbs
		Assert.assertEquals(5881, this.lexicon
				.getNumberOfItems(Category.ADVERB));

		// 1 complementiser
		Assert.assertEquals(1, this.lexicon
				.getNumberOfItems(Category.COMPLEMENTISER));

		// 65 conjunctions
		Assert.assertEquals(65, this.lexicon
				.getNumberOfItems(Category.CONJUNCTION));

		// 38 determiners
		Assert.assertEquals(38, this.lexicon
				.getNumberOfItems(Category.DETERMINER));

		// 153 prepositions
		Assert.assertEquals(153, this.lexicon
				.getNumberOfItems(Category.PREPOSITION));

		// 82 pronouns
		Assert
				.assertEquals(82, this.lexicon
						.getNumberOfItems(Category.PRONOUN));

		// 10283 verbs
		Assert
				.assertEquals(10282, this.lexicon
						.getNumberOfItems(Category.VERB));

		// 267060 nouns
		Assert.assertEquals(267060, this.lexicon
				.getNumberOfItems(Category.NOUN));

	}

}
