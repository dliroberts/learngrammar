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
package simplenlg.lexicon;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import simplenlg.exception.LexiconException;
import simplenlg.features.Category;
import simplenlg.lexicon.db.DBAccessor;
import simplenlg.lexicon.db.SQLAccessor;
import simplenlg.lexicon.lexicalitems.LexicalItem;
import simplenlg.lexicon.verbnet.VerbnetClass;

/**
 * An extension of the {@link simplenlg.lexicon.Lexicon} class, implementing the
 * {@link simplenlg.lexicon.DatabaseBackedLexicon} interface. The
 * <code>DBLexicon</code> accepts a {@link simplenlg.lexicon.db.DBAccessor}
 * which incorporates the code to load lexical information from the DB.
 * 
 * <P>
 * Unlike the <code>Lexicon</code> class, a <code>DBLexicon</code> does not
 * pre-load exception lists or symbols by default; this is because the full set
 * of relevant lexical items for an application is assumed to be present in the
 * database.
 * 
 * <P>
 * This class overrides the getters for lexical items in <code>Lexicon</code>:
 * whenever an item is to be retrieved which does not exist in the lexicon, the
 * accessor's
 * {@link simplenlg.lexicon.DatabaseBackedLexicon#loadItemByID(String)} or
 * {@link simplenlg.lexicon.DatabaseBackedLexicon#loadItemsByBaseform(String)}
 * methods are invoked to try and retrieve the item(s) from the database. This
 * also means that, while an entire lexicon can be loaded in a single step via
 * {@link #loadData()}, it is also possible to retrieve data on the fly.
 * 
 * 
 * @author agatt
 * @since Version 3.7
 */
public class DBLexicon extends Lexicon implements DatabaseBackedLexicon {

	// database accessor
	DBAccessor accessor;

	// all data loaded flag -- set to true if loadData() is called
	boolean allDataLoaded;

	Map<String, VerbnetClass> verbnetClasses;

	/**
	 * Instantiates a new (empty) <code>DBLexicon</code>.
	 */
	public DBLexicon() {
		this.verbnetClasses = new TreeMap<String, VerbnetClass>();
		this.itemsByID = new TreeMap<String, LexicalItem>();
		this.itemsByBaseform = new TreeMap<String, Set<String>>();
		this.itemsByCategory = new TreeMap<Category, Set<String>>();
		this.idPrefix = "I";
		this.allDataLoaded = false;
	}

	/**
	 * Instantiates a new (empty) <code>DBLexicon</code> with the specified
	 * <code>DBAccessor</code>.
	 * 
	 * @param accessor
	 *            the accessor
	 */
	public DBLexicon(DBAccessor accessor) {
		this();
		setAccessor(accessor);
	}

	/**
	 * Instantiates a new (empty) <code>DBLexicon</code> which uses an
	 * <code>SQLAccessor</code> to access the specified database. The accessor
	 * uses the specified driver, database url, username and password.
	 * 
	 * 
	 * @param driver
	 *            The driver
	 * @param url
	 *            The database url
	 * @param user
	 *            The username
	 * @param pw
	 *            The password
	 */
	public DBLexicon(String driver, String url, String user, String pw) {
		this();
		setAccessor(new SQLAccessor(driver, url, user, pw));
	}

	// ****************************************************************
	// DB - RELATED METHODS
	// ****************************************************************

	/**
	 * Sets the <code>DBAccessor</code> to be used for loading lexical data.
	 * This method also calls
	 * {@link simplenlg.lexicon.db.DBAccessor#setLexicon(DatabaseBackedLexicon)}
	 * , setting the lexicon into which the accessor loads data.
	 * 
	 * @param dbAccessor
	 *            the new accessor
	 */
	public void setAccessor(DBAccessor dbAccessor) {
		this.accessor = dbAccessor;
		dbAccessor.setLexicon(this);
	}

	/**
	 * @return <code>true</code>, if a <code>DBAccessor</code> has been set.
	 */
	public boolean hasAccessor() {
		return this.accessor != null;
	}

	/**
	 * @return the <code>DBAccessor</code> backing this lexicon, if set,
	 *         <code>null</code> otherwise.
	 */
	public DBAccessor getAccessor() {
		return this.accessor;
	}

	/**
	 * Loads lexical data from the database, using a pre-specified
	 * {@link simplenlg.lexicon.db.DBAccessor}. This method assumes that the
	 * accessor has already been provided with whatever parameters are needed to
	 * access the data (e.g. in case of a relational DB, a url, a driver, a
	 * username and a password).
	 * 
	 * <P>
	 * This method involves an invocation of the
	 * {@link simplenlg.lexicon.db.DBAccessor#loadData()} method in the
	 * lexicon's <code>DBAccessor</code>.
	 * 
	 * <P>
	 * <strong>Note:</strong> Loading all data in the NIH lexicon database can
	 * be highly memory-intensive and time-consuming, if the entire
	 * <code>entries</code> table is used, since this contains over 330,500
	 * items. If loading data at one go is required, is advisable to create a
	 * smaller <code>entries</code> table with the relevant subset of items.
	 * 
	 * @throws LexiconException
	 *             if the accessor encounters an error during loading
	 * 
	 * @see #setAccessor(DBAccessor)
	 * @see simplenlg.lexicon.db.DBAccessor
	 * @see simplenlg.lexicon.db.SQLAccessor
	 */
	public void loadData() throws LexiconException {

		if (!hasAccessor()) {
			throw new LexiconException("Cannot load data: No accessor set");
		}

		try {
			this.accessor.loadData();
			this.allDataLoaded = true;

		} catch (Exception e) {
			LexiconException le = new LexiconException(
					"Cannot load data. DBAccessor threw the following exception: "
							+ e.getClass() + " with message: " + e.getMessage());
			le.setStackTrace(e.getStackTrace());
			throw le;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.DatabaseBackedLexicon#loadItemByID(java.lang.String)
	 */
	public void loadItemByID(String id) throws LexiconException {

		try {
			this.accessor.loadItemByID(id);

		} catch (Exception e) {
			LexiconException ex = new LexiconException(
					"Failed to retrieve item. DBAccessor threw exception: "
							+ e.getClass() + " with message: " + e.getMessage());
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.DatabaseBackedLexicon#loadItemsByBaseform(java.lang
	 * .String)
	 */
	public void loadItemsByBaseform(String baseform) throws LexiconException {

		try {
			this.accessor.loadItemsByBaseform(baseform);

		} catch (Exception e) {
			LexiconException ex = new LexiconException(
					"Failed to retrieve item. DBAccessor threw exception: "
							+ e.getClass() + " with message: " + e.getMessage());
			ex.setStackTrace(e.getStackTrace());
			throw ex;
		}
	}

	/**
	 * Gets the item with the given unique id in the lexicon. If no item with
	 * the specified id is in the lexicon, an attempt is made to retrieve it
	 * from the database. This is only carried out if {@link #loadData()} has
	 * <strong>not</strong> been invoked, since if it has, all the data will be
	 * in the lexicon. This method returns <code>null</code> just in case there
	 * is no lexical item with the given id in the database or in the lexicon.
	 * If this succeeds, the item is stored in the lexicon for future access.
	 * 
	 * @param id
	 *            The unique identifier for the lexical item
	 * @return The item, if it is in the lexicon, or has been retrieved from the
	 *         database, <code>null</code> otherwise.
	 * @throws LexiconException
	 *             if an attempt is made to retrieve the item via the accessor,
	 *             and this results in an Exception
	 */
	@Override
	public LexicalItem getItemByID(String id) throws LexiconException {
		LexicalItem item = super.getItemByID(id);

		if (item == null && !this.allDataLoaded) {
			try {
				loadItemByID(id);

				if (this.itemsByID.containsKey(id)) {
					item = this.itemsByID.get(id);
				}

			} catch (Exception e) {
				LexiconException ex = new LexiconException(
						"Failed to retrieve item. Database access resulted in exception: "
								+ e.getClass() + " with message: "
								+ e.getMessage());
				ex.setStackTrace(e.getStackTrace());
				throw ex;
			}
		}

		return item;
	}

	/**
	 * Gets the items which have the given baseform. If no item with the
	 * specified baseform is in the lexicon, an attempt is made to retrieve it
	 * from the database. This is only carried out if {@link #loadData()} has
	 * <strong>not</strong> been invoked, since if it has, all the data will be
	 * in the lexicon. This method returns the empty list just in case there is
	 * no lexical item with the given id in the database or in the lexicon. If
	 * this succeeds, the items are stored in the lexicon for future access.
	 * 
	 * @param baseform
	 *            The baseform of the lexical item
	 * @return The item, if it is in the lexicon, or has been retrieved from the
	 *         database, the empty list otherwise.
	 * @throws LexiconException
	 *             if an attempt is made to retrieve the item via the accessor,
	 *             and this results in an Exception
	 */
	@Override
	public Collection<LexicalItem> getItems(String baseform)
			throws LexiconException {
		Collection<LexicalItem> items = super.getItems(baseform);

		if (items.isEmpty() && !this.allDataLoaded) {
			try {
				loadItemsByBaseform(baseform);

				if (this.itemsByBaseform.containsKey(baseform)) {
					for (String id : this.itemsByBaseform.get(baseform)) {
						items.add(this.itemsByID.get(id));
					}
				}

			} catch (Exception e) {
				LexiconException ex = new LexiconException(
						"Failed to retrieve item. Database access resulted in exception: "
								+ e.getClass() + " with message: "
								+ e.getMessage());
				ex.setStackTrace(e.getStackTrace());
				throw ex;
			}
		}

		return items;
	}

	/**
	 * Get a lexical item with the given baseform belonging to a specific
	 * category. If no item of the specified category and baseform is in the
	 * lexicon, an attempt is made to retrieve it from the database. This is
	 * only carried out if {@link #loadData()} has <strong>not</strong> been
	 * invoked, since if it has, all the data will be in the lexicon. If this
	 * succeeds, the item is stored in the lexicon for future access.
	 * 
	 * @param cat
	 *            The category
	 * @param baseform
	 *            The baseform
	 * @throws LexiconException
	 *             if an attempt is made to retrieve the item via the accessor,
	 *             and this results in an Exception
	 */
	@Override
	public LexicalItem getItem(Category cat, String baseform)
			throws LexiconException {

		if (cat == null || baseform == null) {
			return null;
		}

		LexicalItem item = super.getItem(cat, baseform);

		if (item == null && !this.allDataLoaded) {
			try {
				this.accessor.loadItem(cat, baseform);

				if (this.itemsByBaseform.containsKey(baseform)) {
					for (String id : this.itemsByBaseform.get(baseform)) {
						if (this.itemsByID.get(id).getCategory() == cat) {
							item = this.itemsByID.get(id);
						}
					}
				}

			} catch (Exception e) {
				LexiconException ex = new LexiconException(
						"Failed to retrieve item. Database access resulted in exception: "
								+ e.getClass() + " with message: "
								+ e.getMessage());
				ex.setStackTrace(e.getStackTrace());
				throw ex;
			}
		}

		return item;
	}

	/**
	 * Load the verbnet data
	 * 
	 * @since Version 3.8
	 */
	public void loadVerbnetData() throws LexiconException {
		if (this.accessor instanceof SQLAccessor) {
			try {
				((SQLAccessor) this.accessor).loadVerbnetData();

			} catch (Exception e) {
				LexiconException ex = new LexiconException(
						"Failed to load verbnet data: " + e.getClass()
								+ " with message: " + e.getMessage());
				ex.setStackTrace(e.getStackTrace());
				throw ex;
			}
		} else {
			throw new LexiconException(
					"Cannot load verbnet data: DBAccessor is not instance of SQLAccessor");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.Lexicon#reset()
	 */
	@Override
	public void reset() {
		super.reset();
		this.allDataLoaded = false;
	}

	/*
	 * Loads the data into the lexicon provided in an sql database. This uses
	 * the default {@link simplenlg.lexicon.db.SQLAccessor} class, which
	 * implements the <code>DBAccessor</code> interfavce, and requires a
	 * database that is conformant to the structure laid out in the
	 * documentation for that class.
	 * 
	 * @param driver The database access driver (e.g. "com.mysql.jdbc.Driver")
	 * 
	 * @param url The database url @param username The username for the database
	 * 
	 * @param password The password if any is required. Should be
	 * <code>null</code> if none is.
	 * 
	 * public void loadSQLData(String driver, String url, String username,
	 * String password) { accessor = new SQLAccessor(driver, url, username,
	 * password);
	 * 
	 * try { accessor.loadData(this); } catch (Exception e) { throw new
	 * LexiconException( "Cannot load data. DBAccessor threw the following
	 * exception: " + e.getClass() + " " + e.getMessage()); } }
	 */

}
