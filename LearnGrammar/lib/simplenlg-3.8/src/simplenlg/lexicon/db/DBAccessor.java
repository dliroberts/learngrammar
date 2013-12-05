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
package simplenlg.lexicon.db;

import simplenlg.features.Category;
import simplenlg.lexicon.DatabaseBackedLexicon;

/**
 * <P>
 * Basic interface defining an accessor class. A <code>DBAccessor</code> is
 * required if a lexicon is backed by a database, that is, implements the
 * <code>DatabaseBackedLexicon</code> interface.
 * 
 * <P>
 * the <code>DBAccessor</code> acts as a delegate, to which the lexicon
 * delegates the task of retrieving and constructing lexical items based on
 * information in a repository of some kind (e.g. relational, flat-file,
 * structured repositories such as Protege/OWL, etc). The definition of this
 * interface aims to make the process of loading data into a lexicon as flexible
 * as possible: the same lexicon can be made to access different repositories by
 * defining different implementations of the <code>DBAccessor</code>.
 * </P>
 * 
 * 
 * <H2>Loading lexical data</H2>
 * <P>
 * The <code>DBAccessor<code> defines two ways of
 * loading data from a database into a lexicon:
 * 
 *  <OL>
 *  <LI> all the data in the database (or some predefined subset of it) can be loaded at once. 
 *  This is carried out via {@link #loadData()}
 *  <LI> data can be loaded on the fly by a lexicon every time it is queried. 
 *  This is carried out either via querying the DB for an item by its unique identifier 
 *  ({@link #loadItemByID(String)}), or by querying the DB for the items with a given 
 *  baseform ({@link #loadItemsByBaseform(String)}).
 *  </OL>
 *  
 *  <P>
 * In case an accessor
 * is defined which requires specific parameters for database access
 * (such as a db url, username, password, database access driver, etc),
 * these should be passed as parameters to the constructor or using specific setter methods defined in the implementation.
 * </P>
 * 
 * <H2>General guidelines for using an accessor</H2>
 * <P>
 * The recommended procedure for using an accessor to load lexical data is as
 * follows:
 * <OL>
 * <LI>Initialise a <code>DatabaseBackedLexicon</code>;</LI>
 * <LI>Initialise a <code>DBAccessor</code> with the correct database access
 * parameters (if required);</LI>
 * <LI>Pass the accessor to the lexicon.</LI>
 * </OL>
 * 
 * 
 * @author agatt
 * @since Version 3.7
 */
public interface DBAccessor {

	/**
	 * Set the <code>DatabaseBackedLexicon</code> to which this accessor
	 * belongs, and into which it will load lexical items.
	 * 
	 * @param lex
	 *            The lexicon
	 * @see #hasLexicon()
	 */
	public void setLexicon(DatabaseBackedLexicon lex);

	/**
	 * Check if this accessor has a lexicon set.
	 * 
	 * @return <code>true</code> if this accessor has been assigned to a
	 *         lexicon.
	 * @see #setLexicon(DatabaseBackedLexicon)
	 */
	public boolean hasLexicon();

	/**
	 * Loads data into the lexicon from a database.
	 * 
	 * @throws Exception
	 *             The method should throw an exception of the appropriate type
	 *             should access to the data source fail, or if the lexicon into
	 *             which data is to be loaded has not been set.
	 */
	public void loadData() throws Exception;

	/**
	 * Loads only lexical items of the specified categories.
	 * 
	 * @param categories
	 *            the categories of the items to load
	 * @throws Exception
	 *             the method should throw an exception of the appropriate type
	 *             should access to the data source fail, or if the lexicon into
	 *             which data is to be loaded has not been set.
	 */
	public void loadData(Category... categories) throws Exception;

	/**
	 * Load a single lexical item of the specified category, with the specified
	 * baseform.
	 * 
	 * @param cat
	 *            the category
	 * @param baseform
	 *            the baseform *
	 * @throws Exception
	 *             the method should throw an exception of the appropriate type
	 *             should access to the data source fail, or if the lexicon into
	 *             which data is to be loaded has not been set.
	 */
	public void loadItem(Category cat, String baseform) throws Exception;

	/**
	 * Loads a single lexical item from the DB, based on its ID. This method is
	 * provided to allow a lexicon to have on-the-fly access to database
	 * information, rather than necessarily loading all data required in the
	 * lifetime of an application.
	 * 
	 * <P>
	 * Implementations should guarantee that this method load one, and only one,
	 * item, since the purpose of an Id is to serve as a unique distinguisher.
	 * 
	 * @param id
	 *            The id of the lexical item, as defined in the DB
	 * @throws Exception
	 *             The method should throw an exception of the appropriate type
	 *             should access to the data source fail, or if the lexicon has
	 *             not been set.
	 */
	public void loadItemByID(String id) throws Exception;

	/**
	 * Loads a set of lexical items from the DB with the given baseform. This
	 * method is provided to allow a lexicon to have on-the-fly access to
	 * database information, rather than necessarily loading all data required
	 * in the lifetime of an application.
	 * 
	 * <P>
	 * Unlike {@link #loadItemByID(String)}, this method cannot guarantee that a
	 * single item will be loaded, since different items can have the same
	 * baseform.
	 * 
	 * @param baseform
	 *            The baseform of the lexical item, as defined in the DB
	 * @throws Exception
	 *             The method should throw an exception of the appropriate type
	 *             should access to the data source fail, or if the lexicon has
	 *             not been set.
	 */
	public void loadItemsByBaseform(String baseform) throws Exception;

	/**
	 * Connect to the database. This method should be used to implement any
	 * connection mechanisms, for example, connecting to a relational database
	 * server, opening a flatfile, etc.
	 * 
	 * @throws Exception
	 *             The method should throw an exception of the appropriate type
	 *             should access to the data source fail.
	 */
	public void connect() throws Exception;

}
