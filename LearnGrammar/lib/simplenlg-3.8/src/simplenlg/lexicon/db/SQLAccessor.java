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

import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import simplenlg.exception.LexiconException;
import simplenlg.features.AdjectivePosition;
import simplenlg.features.AdjectiveType;
import simplenlg.features.AdverbPosition;
import simplenlg.features.AdverbType;
import simplenlg.features.Agreement;
import simplenlg.features.Case;
import simplenlg.features.Category;
import simplenlg.features.ComplementFrame;
import simplenlg.features.ComplementSlot;
import simplenlg.features.ComplementType;
import simplenlg.features.ConjunctionType;
import simplenlg.features.Deixis;
import simplenlg.features.Gender;
import simplenlg.features.InflectionType;
import simplenlg.features.InterpretationCode;
import simplenlg.features.Polarity;
import simplenlg.features.Possession;
import simplenlg.features.Quantification;
import simplenlg.features.Reflexivity;
import simplenlg.features.Transitivity;
import simplenlg.features.VerbType;
import simplenlg.lexicon.DatabaseBackedLexicon;
import simplenlg.lexicon.lexicalitems.Adjective;
import simplenlg.lexicon.lexicalitems.Adverb;
import simplenlg.lexicon.lexicalitems.Complementiser;
import simplenlg.lexicon.lexicalitems.Conjunction;
import simplenlg.lexicon.lexicalitems.ContentWord;
import simplenlg.lexicon.lexicalitems.DerivationalRelation;
import simplenlg.lexicon.lexicalitems.Determiner;
import simplenlg.lexicon.lexicalitems.LexicalItem;
import simplenlg.lexicon.lexicalitems.Noun;
import simplenlg.lexicon.lexicalitems.Preposition;
import simplenlg.lexicon.lexicalitems.Pronoun;
import simplenlg.lexicon.lexicalitems.Verb;
import simplenlg.lexicon.verbnet.VerbnetClass;
import simplenlg.lexicon.verbnet.VerbnetFrame;
import simplenlg.lexicon.verbnet.VerbnetSlotType;

/**
 * <P>
 * Class implementing the <code>DBAccessor</code> interface. This class provides
 * a way of loading lexical data into a <code>DBLexicon</code> given a database
 * that conforms to a predefined structure, essentially an SQL view of the
 * flatfile relational database found in the NIH Specialist Lexicon, a
 * wide-coverage lexicon provided under General Public License by the National
 * Library of Medicine (National Institutes of Health, USA).
 * </P>
 * 
 * <P>
 * The NIH lexicon and documentation can be found here:<BR>
 * <code>http://lexsrv3.nlm.nih.gov/SPECIALIST/Projects/lexicon/current/index.html</code>
 * <BR>
 * The SQL code for creating and populating the lexical database are provided
 * with this distribution of SimpleNLG.
 * </P>
 * 
 * <H2>Structure of the main lexicon table</H2>
 * <P>
 * The SQL database provided with this distribution consists of a main table
 * called <code>entries</code>. There are a number of subsidiary tables which
 * specify additional information for lexical items of particular categories
 * (such as adjectives and adverbs). This DB structure is assumed by
 * <code>SQLAccessor</code>. This class has been tested on a MySQL 5.0 version
 * of the database, though the data access methods allow for data access from
 * any relational database system, given the driver and url.
 * </P>
 * 
 * <P>
 * The main <code>entries</code> table has the following structure: <BR>
 * 
 * <pre>
 * | euid | baseform | category | numEntries | hasComp | infl | agr |
 * </pre>
 * 
 * <BR>
 * where:
 * <OL>
 * <LI><code>euid</code> is a unique identifier for an entity (a string)</LI>;
 * <LI><code>baseform</code> is the baseform of the entry, consisting of one or
 * more whitespace-separated tokens;</LI>
 * <LI><code>category</code> is the grammatical category</LI>;
 * <LI><code>numEntries</code> indicates the number of separate entries of this
 * lexical item in the subsidiary table <code>complements</code>, each of which
 * specifies a possible complement taken by the lexical item.</LI>
 * <LI><code>hascomp</code> is a boolean value (0 or 1) indicating whether the
 * lexical entry has complements</LI>;
 * <LI><code>infl</code> is a set-valued field whose value indicates the kind of
 * inflection a lexical item takes (regular, regular with consonant doubling,
 * invariant, etc);</LI>
 * <LI><code>agr</code> specifies the agreement of this entry (e.g. whether it
 * is count, mass, plural invariant, etc). This value can be <code>null</code>,
 * and is usually only specified for nominals (nouns and pronouns).</LI>
 * </OL>
 * </P>
 * 
 * <H2>Loading data into a lexicon</H2>
 * 
 * <P>
 * In addition to an implementation of the methods for loading data specified in
 * the <code>DBAccessor</code> interface, this class also provides methods to
 * directly load lexical items of a given category only (for example,
 * {@link #loadAdjectives()} will load only the adjectives in the
 * <code>entries</code> table).
 * 
 * 
 * <P>
 * Since the lexicon is rather large (ca. 330,500 entries), selecting every
 * single lexical item for a given application will usually amount to overkill,
 * and may also result in memory problems (loading the entire lexicon requires
 * at least 800m of dedicated memory for the JVM). Rather than loading all data
 * at once, it is also possible to load lexical items on the fly.
 * </P>
 * 
 * <P>
 * Lexical items loaded with the SQLAccessor have several features, which are
 * defined in the database, and which map to values of types defined in
 * {@link simplenlg.features}.
 * </P>
 * 
 * @author agatt
 * @since Version 3.7
 */
public class SQLAccessor implements DBAccessor {

	// db parameters
	private Connection con;
	private String url;
	private String username;
	private String password;
	private String driver;

	// regexes to match complex pp complements
	String ppRegex = "pphr\\((\\w+),np(\\([\\w\\s]+\\))?\\)";
	String cppRegex = "^pphr\\((\\w+),np(\\([\\w\\s]+\\))?," + this.ppRegex
			+ "\\)$";
	private Matcher cppMatcher;

	// flag whether DB is open or not
	private boolean connected;

	// the lexicon being populated
	private DatabaseBackedLexicon lexicon;

	// prepared statements to execute
	PreparedStatement reset, getEntryByCatBaseform, getEntryByID,
			getEntryByBaseform, getAllEntries, getEntriesByCategory,
			/*getVariants, */getDerivations, complement, adjFeature, advFeature,
			detFeature, conjFeature, proFeature, getVerbFrames;

	// xml doc builder for parsing verbframe xml
	DocumentBuilder builder;

	// xpath object to trace paths through verbframe xml
	XPath xpath;

	// *************************************************************************
	// CONSTRUCTORS
	// *************************************************************************

	/**
	 * Default (empty) constructor.
	 */
	public SQLAccessor() {

		try {
			this.builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			this.xpath = XPathFactory.newInstance().newXPath();
		} catch (ParserConfigurationException pce) {
			throw new RuntimeException(
					"Could not instantiate an XML document builder!");
		}

		this.con = null;
		this.url = null;
		this.username = null;
		this.password = null;
		this.driver = null;
		this.connected = false;
		this.lexicon = null;
		this.cppMatcher = Pattern.compile(this.cppRegex).matcher("");
	}

	/**
	 * Initialises an <code>SQLAccessor</code> with the database parameters.
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
	public SQLAccessor(String driver, String url, String user, String pw) {
		this();
		setDriver(driver);
		setUrl(url);
		setUsername(user);
		setPassword(pw);
	}

	// *************************************************************************
	// GETTERS/SETTERS FOR DB PARAMS
	// *************************************************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @seesimplenlg.lexicon.db.DBAccessor#setLexicon(simplenlg.lexicon.
	 * DatabaseBackedLexicon)
	 */
	public void setLexicon(DatabaseBackedLexicon lex) {
		this.lexicon = lex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.db.DBAccessor#hasLexicon()
	 */
	public boolean hasLexicon() {
		return this.lexicon != null;
	}

	/**
	 * Gets the database url.
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 * Sets the database url.
	 * 
	 * @param url
	 *            the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the database username.
	 * 
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * Sets the db username.
	 * 
	 * @param username
	 *            the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the db password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Sets the db password.
	 * 
	 * @param password
	 *            the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the name of the db driver class.
	 * 
	 * @return the driver
	 */
	public String getDriver() {
		return this.driver;
	}

	/**
	 * Sets the driver class to be used in establishing a database connection.
	 * 
	 * @param driverClass
	 *            the driver class
	 */
	public void setDriver(String driverClass) {
		this.driver = driverClass;
	}

	// *****************************************************************
	// DB Connection and prepare statement methods
	// *****************************************************************

	/**
	 * Connects to the lexical database, using pre-set url, driver class,
	 * username and password (if any are required). This method also initialises
	 * some prepared SQL statements for data access.
	 * 
	 * @throws ClassNotFoundException
	 *             if the database driver class is not found
	 * @throws SQLException
	 *             if the connection cannot be established
	 * @see #setDriver(String)
	 * @see #setUrl(String)
	 * @see #setUsername(String)
	 * @see #setPassword(String)
	 */
	public void connect() throws ClassNotFoundException, SQLException {
		Class.forName(this.driver);
		this.con = DriverManager.getConnection(this.url, this.username,
				this.password);
		this.connected = true;
		this.getEntryByID = this.con.prepareStatement(SQLConfig.entryByID);
		this.getEntryByBaseform = this.con
				.prepareStatement(SQLConfig.entryByBaseform);
		this.getAllEntries = this.con.prepareStatement(SQLConfig.allEntries);
		this.complement = this.con.prepareStatement(SQLConfig.complementQuery);
		this.adjFeature = this.con.prepareStatement(SQLConfig.adjFeatureQuery);
		this.advFeature = this.con.prepareStatement(SQLConfig.advFeatureQuery);
		this.detFeature = this.con.prepareStatement(SQLConfig.detFeatureQuery);
		this.conjFeature = this.con
				.prepareStatement(SQLConfig.conjFeatureQuery);
		this.proFeature = this.con.prepareStatement(SQLConfig.proFeatureQuery);
		this.getEntryByCatBaseform = this.con
				.prepareStatement(SQLConfig.entryByCatBaseform);
//		this.getVariants = this.con.prepareStatement(SQLConfig.wordVariants);
		this.getDerivations = this.con.prepareStatement(SQLConfig.derivations);
		this.getVerbFrames = this.con.prepareStatement(SQLConfig.verbFrames);
	}

	/**
	 * Disconnects from the database.
	 * 
	 * @throws SQLException
	 *             in case the connection cannot be closed
	 */
	public void disconnect() throws SQLException {
		this.con.close();
		this.connected = false;
	}

	/**
	 * Checks if the <code>SQLAccessor</code> already has an open database
	 * connection.
	 * 
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return this.connected;
	}

	// *************************************************************************
	// METHODS FOR DATA LOAD AND ACCESS
	// *************************************************************************

	/**
	 * Loads all the data in the database. In the setup assumed by the
	 * SQLAccessor, this will mean that every individual entry in the
	 * <code>entries</code> table is loaded into the lexicon if it is selected.
	 * Therefore, it is strongly recommended that a subset of the lexicon be
	 * defined prior to calling this method.
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not already
	 * connected to the DB via an explicit invocation of this method.
	 * 
	 * @throws ClassNotFoundException
	 *             if the database driver class cannot be loaded
	 * @throws SQLException
	 *             if an error occurs while establishing a connection to the db.
	 * @throws LexiconException
	 *             if the lexicon has not been set
	 */
	public void loadData() throws SQLException, ClassNotFoundException {
		loadData(Category.values());
	}

	/**
	 * Loads only the entries in the DB which are of the specified grammatical
	 * categories. The categories in the DB are nouns, verbs, adjectives,
	 * adverbs, conjunctions, prepositions, complementisers, pronouns and
	 * determiners.
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not already
	 * connected to the DB via an explicit invocation of this method.
	 * 
	 * <P>
	 * This method will load every entry of the specified categories in the
	 * entries table, together with their additional features, depending on the
	 * category, by calling the public methods defined in this class for
	 * retrieving data specific to each category, namely:
	 * {@link #loadAdjectives()}, {@link #loadAdverbs()},
	 * {@link #loadComplementisers()}, {@link #loadConjunctions()},
	 * {@link #loadDeterminers()}, {@link #loadNouns()},
	 * {@link #loadPrepositions()}, and/or {@link #loadPronouns()}
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not connected
	 * to the DB via an explicit invocation of this method.
	 * 
	 * @param categories
	 *            the categories of entries to load
	 * @throws SQLException
	 *             if the attempt to retrieve data from the Db fails
	 * @throws ClassNotFoundException
	 *             if an attempt to establish a DB connection was made, and the
	 *             driver class cannot be found
	 * @throws LexiconException
	 *             if the lexicon has not been set
	 */
	public void loadData(Category... categories) throws SQLException,
			ClassNotFoundException {

		checkConnection();

		for (Category c : categories) {
			switch (c) {
			case NOUN:
				loadNouns();
				break;
			case VERB:
				loadVerbs();
				break;
			case ADJECTIVE:
				loadAdjectives();
				break;
			case ADVERB:
				loadAdverbs();
				break;
			case COMPLEMENTISER:
				loadComplementisers();
				break;
			case CONJUNCTION:
				loadConjunctions();
				break;
			case DETERMINER:
				loadDeterminers();
				break;
			case PREPOSITION:
				loadPrepositions();
				break;
			case PRONOUN:
				loadPronouns();
				break;
			}
		}

	}

	// *************************************************************************
	// SQL HANDLING
	// *************************************************************************

	/**
	 * Loads verbs in the database and adds them to the lexicon. Verbs are
	 * specified for:
	 * 
	 * <OL>
	 * <LI>their inflection type; see {@link simplenlg.features.InflectionType};
	 * </LI>
	 * <LI>the complements they can take; see
	 * {@link simplenlg.features.ComplementFrame};</LI>
	 * <LI>the verb type; see {@link simplenlg.features.VerbType}.</LI>
	 * </OL>
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not already
	 * connected to the DB via an explicit invocation of this method.
	 * 
	 * @throws SQLException
	 *             if the attempt to retrieve data from the Db fails
	 * @throws ClassNotFoundException
	 *             if an attempt to establish a DB connection is made, and the
	 *             driver class cannot be found
	 * @throws LexiconException
	 *             if the lexicon has not been set
	 */
	public void loadVerbs() throws SQLException, ClassNotFoundException {
		checkConnection();
		String query = SQLConfig.entriesByCategory
				+ "where( e.category=\"verb\" or e.category=\"aux\" or e.category=\"modal\" )";
		PreparedStatement verbs = this.con.prepareStatement(query);

		if (verbs.execute()) {
			ResultSet results = verbs.getResultSet();

			while (results.next()) {
				String id = results.getString("euid");
				boolean hasComp = results.getBoolean("hasComp");
				Verb v = null;

				if (this.lexicon.hasItemID(id)) {
					v = (Verb) this.lexicon.getItemByID(id);
				}

				if (v == null) {
					String baseform = results.getString("baseform");
					v = new Verb(id, baseform);
					v.setVerbType(getVerbType(results.getString("category")));
					v.setInflectionType(getInflectionCode(results
							.getString("infl")));
				}

				// check if there's a complement at this row
				if (hasComp) {
					v = (Verb) addComplements(v);
				}

				//loadVariants(v);
				this.lexicon.addItem(v);
			}
		}
	}

	/**
	 * Loads nouns in the database and adds them to the lexicon. Nouns are
	 * specified for:
	 * 
	 * <OL>
	 * <LI>their inflection type; see {@link simplenlg.features.InflectionType};
	 * </LI>
	 * <LI>the complements they can take; see
	 * {@link simplenlg.features.ComplementFrame};</LI>
	 * <LI>the agreement type; see {@link simplenlg.features.Agreement}.</LI>
	 * </OL>
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not already
	 * connected to the DB via an explicit invocation of this method.
	 * 
	 * @throws SQLException
	 *             if the attempt to retrieve data from the Db fails
	 * @throws ClassNotFoundException
	 *             if an attempt to establish a DB connection is made, and the
	 *             driver class cannot be found
	 * @throws LexiconException
	 *             if the lexicon has not been set
	 */
	public void loadNouns() throws SQLException, ClassNotFoundException {
		checkConnection();
		String query = SQLConfig.entriesByCategory
				+ "where( e.category=\"noun\" );";
		PreparedStatement nouns = this.con.prepareStatement(query);

		if (nouns.execute()) {
			ResultSet results = nouns.getResultSet();

			while (results.next()) {
				String id = results.getString("euid");
				boolean hasComp = results.getBoolean("hasComp");
				Noun n = null;

				if (this.lexicon.hasItemID(id)) {
					n = (Noun) this.lexicon.getItemByID(id);
				}

				if (n == null) {
					String baseform = results.getString("baseform");
					n = new Noun(id, baseform);
					n.setInflectionType(getInflectionCode(results
							.getString("infl")));
					n.setAgreement(getAgreementCode(results.getString("agr")));
				}

				// check if there's a complement at this row
				if (hasComp) {
					n = (Noun) addComplements(n);
				}

				//loadVariants(n);
				loadDerivations(n);
				this.lexicon.addItem(n);
			}
		}
	}

	/**
	 * Loads adjectives in the database and adds them to the lexicon. Adjectives
	 * are specified for:
	 * 
	 * <OL>
	 * <LI>their inflection type; see {@link simplenlg.features.InflectionType};
	 * </LI>
	 * <LI>the complements they can take; see
	 * {@link simplenlg.features.ComplementFrame};</LI>
	 * <LI>the syntactic positions they can occupy; see
	 * {@link simplenlg.features.AdjectivePosition};</LI>
	 * <LI>the type of the adjective (stative or not); see
	 * {@link simplenlg.features.AdjectiveType}.</LI>
	 * </OL>
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not already
	 * connected to the DB via an explicit invocation of this method.
	 * 
	 * @throws SQLException
	 *             if the attempt to retrieve data from the Db fails
	 * @throws ClassNotFoundException
	 *             if an attempt to establish a DB connection is made, and the
	 *             driver class cannot be found
	 * @throws LexiconException
	 *             if the lexicon has not been set
	 */
	public void loadAdjectives() throws SQLException, ClassNotFoundException {
		checkConnection();
		String query = SQLConfig.entriesByCategory
				+ "where( e.category=\"adj\" );";
		PreparedStatement adjectives = this.con.prepareStatement(query);

		if (adjectives.execute()) {
			ResultSet results = adjectives.getResultSet();

			while (results.next()) {
				String id = results.getString("euid");
				boolean hasComp = results.getBoolean("hasComp");
				Adjective a = null;

				if (this.lexicon.hasItemID(id)) {
					a = (Adjective) this.lexicon.getItemByID(id);
				}

				if (a == null) {
					String baseform = results.getString("baseform");
					InflectionType infl = getInflectionCode(results
							.getString("infl"));
					a = new Adjective(id, baseform);
					a.setInflectionType(infl);
					a = addAdjFeatures(a);
				}

				// check if there's a complement at this row
				if (hasComp) {
					a = (Adjective) addComplements(a);
				}

				//loadVariants(a);
				this.lexicon.addItem(a);
			}
		}
	}

	/**
	 * Loads adverbs in the database and adds them to the lexicon. Adverbs are
	 * specified for:
	 * 
	 * <OL>
	 * <LI>their inflection type; see {@link simplenlg.features.InflectionType};
	 * </LI>
	 * <LI>the complements they can take; see
	 * {@link simplenlg.features.ComplementFrame};</LI>
	 * <LI>the syntactic position they can occupy; see
	 * {@link simplenlg.features.AdverbPosition};</LI>
	 * <LI>the type of adverb (manner, temporal etc); see
	 * {@link simplenlg.features.AdverbType};</LI>
	 * </OL>
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not already
	 * connected to the DB via an explicit invocation of this method.
	 * 
	 * @throws SQLException
	 *             if the attempt to retrieve data from the Db fails
	 * @throws ClassNotFoundException
	 *             if an attempt to establish a DB connection is made, and the
	 *             driver class cannot be found
	 * @throws LexiconException
	 *             if the lexicon has not been set
	 */
	public void loadAdverbs() throws SQLException, ClassNotFoundException {
		checkConnection();
		String query = SQLConfig.entriesByCategory
				+ "where( e.category=\"adv\" );";
		PreparedStatement adverbs = this.con.prepareStatement(query);

		if (adverbs.execute()) {
			ResultSet results = adverbs.getResultSet();

			while (results.next()) {
				String id = results.getString("euid");
				boolean hasComp = results.getBoolean("hasComp");
				Adverb a = null;

				if (this.lexicon.hasItemID(id)) {
					a = (Adverb) this.lexicon.getItemByID(id);
				} else {
					String baseform = results.getString("baseform");
					a = new Adverb(id, baseform);
					a.setInflectionType(getInflectionCode(results
							.getString("infl")));
					a = addAdverbFeatures(a);
				}

				// check if there's a complement at this row
				if (hasComp) {
					a = (Adverb) addComplements(a);
				}

				//loadVariants(a);
				this.lexicon.addItem(a);
			}
		}
	}

	/**
	 * Loads prepositions in the database and adds them to the lexicon.
	 * Prepositions are specified for:
	 * 
	 * <OL>
	 * <LI>their inflection type; see {@link simplenlg.features.InflectionType};
	 * </LI>
	 * </OL>
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not already
	 * connected to the DB via an explicit invocation of this method.
	 * 
	 * @throws SQLException
	 *             if the attempt to retrieve data from the Db fails
	 * @throws ClassNotFoundException
	 *             if an attempt to establish a DB connection is made, and the
	 *             driver class cannot be found
	 * @throws LexiconException
	 *             if the lexicon has not been set
	 */
	public void loadPrepositions() throws SQLException, ClassNotFoundException {
		checkConnection();
		String query = SQLConfig.entriesByCategory
				+ "where e.category=\"prep\";";
		PreparedStatement prepositions = this.con.prepareStatement(query);

		if (prepositions.execute()) {
			ResultSet results = prepositions.getResultSet();

			while (results.next()) {
				String id = results.getString(1);
				Preposition p = null;

				if (this.lexicon.hasItemID(id)) {
					p = (Preposition) this.lexicon.getItemByID(id);
				} else {
					String baseform = results.getString(2);
					p = new Preposition(id, baseform);
					p.setInflectionType(getInflectionCode(results
							.getString("infl")));
				}

				//loadVariants(p);
				this.lexicon.addItem(p);
			}
		}
	}

	/**
	 * Loads complementisers in the database and adds them to the lexicon.
	 * Complementisers are specified for:
	 * 
	 * <OL>
	 * <LI>their inflection type; see {@link simplenlg.features.InflectionType};
	 * </LI>
	 * </OL>
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not already
	 * connected to the DB via an explicit invocation of this method.
	 * 
	 * @throws SQLException
	 *             if the attempt to retrieve data from the Db fails
	 * @throws ClassNotFoundException
	 *             if an attempt to establish a DB connection is made, and the
	 *             driver class cannot be found
	 * @throws LexiconException
	 *             if the lexicon has not been set
	 */
	public void loadComplementisers() throws SQLException,
			ClassNotFoundException {
		checkConnection();
		String query = SQLConfig.entriesByCategory
				+ "where e.category=\"compl\";";
		PreparedStatement complementisers = this.con.prepareStatement(query);

		if (complementisers.execute()) {
			ResultSet results = complementisers.getResultSet();

			// e.euid, e.baseform, e.infl
			while (results.next()) {
				String id = results.getString("euid");
				Complementiser c = null;

				if (this.lexicon.hasItemID(id)) {
					c = (Complementiser) this.lexicon.getItemByID(id);
				} else {
					String baseform = results.getString("baseform");
					c = new Complementiser(id, baseform);
					c.setInflectionType(getInflectionCode(results
							.getString("infl")));
				}

				//loadVariants(c);
				this.lexicon.addItem(c);
			}
		}
	}

	/**
	 * Loads determiners in the database and adds them to the lexicon.
	 * Determiners are specified for:
	 * 
	 * <OL>
	 * <LI>their inflection type; see {@link simplenlg.features.InflectionType};
	 * </LI>
	 * </OL>
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not already
	 * connected to the DB via an explicit invocation of this method.
	 * 
	 * @throws SQLException
	 *             if the attempt to retrieve data from the Db fails
	 * @throws ClassNotFoundException
	 *             if an attempt to establish a DB connection is made, and the
	 *             driver class cannot be found
	 * @throws LexiconException
	 *             if the lexicon has not been set
	 */
	public void loadDeterminers() throws SQLException, ClassNotFoundException {
		checkConnection();
		String query = SQLConfig.entriesByCategory
				+ "where e.category=\"det\";";
		PreparedStatement determiners = this.con.prepareStatement(query);

		if (determiners.execute()) {
			ResultSet results = determiners.getResultSet();

			while (results.next()) {
				String id = results.getString("euid");
				Determiner d = null;

				if (this.lexicon.hasItemID(id)) {
					d = (Determiner) this.lexicon.getItemByID(id);
				} else {
					String baseform = results.getString("baseform");
					d = new Determiner(id, baseform);
					d.setInflectionType(getInflectionCode(results
							.getString("infl")));
					d.setAgreement(getAgreementCode(results.getString("agr")));
					d = addDetFeatures(d);
				}

				//loadVariants(d);
				this.lexicon.addItem(d);
			}
		}
	}

	/**
	 * Loads conjunctions in the database and adds them to the lexicon.
	 * Conjunctions are specified for:
	 * 
	 * <OL>
	 * <LI>their inflection type; see {@link simplenlg.features.InflectionType};
	 * </LI>
	 * <LI>the type of conjunction; see
	 * {@link simplenlg.features.ConjunctionType};</LI>
	 * <LI>a category restriction, indicating which category the conjunction can
	 * apply to (e.g. most subordinating conjunctions only conjoin clauses)</LI>
	 * </OL>
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not already
	 * connected to the DB via an explicit invocation of this method.
	 * 
	 * @throws SQLException
	 *             if the attempt to retrieve data from the Db fails
	 * @throws ClassNotFoundException
	 *             if an attempt to establish a DB connection is made, and the
	 *             driver class cannot be found
	 * @throws LexiconException
	 *             if the lexicon has not been set
	 * 
	 */
	public void loadConjunctions() throws SQLException, ClassNotFoundException {
		checkConnection();
		String query = SQLConfig.entriesByCategory
				+ "where e.category=\"conj\";";
		PreparedStatement conjunctions = this.con.prepareStatement(query);

		if (conjunctions.execute()) {
			ResultSet results = conjunctions.getResultSet();

			// e.euid, e.baseform, e.infl, c.conjType, c.argType
			while (results.next()) {
				String id = results.getString("euid");
				Conjunction c = null;

				if (this.lexicon.hasItemID(id)) {
					c = (Conjunction) this.lexicon.getItemByID(id);
				} else {
					String baseform = results.getString("baseform");
					c = new Conjunction(id, baseform);
					c.setInflectionType(getInflectionCode(results
							.getString("infl")));
					c = addConjFeatures(c);
				}

				//loadVariants(c);
				this.lexicon.addItem(c);
			}
		}
	}

	/**
	 * Loads pronouns in the database and adds them to the lexicon. Pronouns are
	 * specified for:
	 * 
	 * <OL>
	 * <LI>their inflection type; see {@link simplenlg.features.InflectionType};
	 * </LI>
	 * <LI>their agreement type; see {@link simplenlg.features.Agreement};</LI>
	 * <LI>their gender; see {@link simplenlg.features.Gender};</LI>
	 * <LI>their case; see {@link simplenlg.features.Case};</LI>
	 * <LI>whether they are possessive or not; see
	 * {@link simplenlg.features.Possession};</LI>
	 * <LI>whether they are reflexive or not; see
	 * {@link simplenlg.features.Reflexivity};</LI>
	 * <LI>whether they are deictic or not; see
	 * {@link simplenlg.features.Deixis};</LI>
	 * <LI>their polarity; see {@link simplenlg.features.Polarity};</LI>
	 * <LI>their quantification properties; see
	 * {@link simplenlg.features.Quantification};</LI>
	 * </OL>
	 * 
	 * <P>
	 * This method invokes {@link #connect()} if the lexicon has not already
	 * connected to the DB via an explicit invocation of this method.
	 * 
	 * @throws SQLException
	 *             if the attempt to retrieve data from the Db fails
	 * @throws ClassNotFoundException
	 *             if an attempt to establish a DB connection is made, and the
	 *             driver class cannot be found
	 * @throws LexiconException
	 *             if the lexicon has not been set
	 */
	public void loadPronouns() throws SQLException {
		String query = SQLConfig.entriesByCategory
				+ "where e.category=\"pron\";";
		PreparedStatement pronouns = this.con.prepareStatement(query);

		if (pronouns.execute()) {
			ResultSet results = pronouns.getResultSet();

			while (results.next()) {
				String id = results.getString("euid");
				Pronoun p = null;

				if (this.lexicon.hasItemID(id)) {
					p = (Pronoun) this.lexicon.getItemByID(id);
				} else {
					String baseform = results.getString("baseform");
					p = new Pronoun(id, baseform);
					p.setInflectionType(getInflectionCode(results
							.getString("infl")));
					p.setAgreement(getAgreementCode(results.getString("agr")));
					p = addPronounFeatures(p);
				}

				//loadVariants(p);
				this.lexicon.addItem(p);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.db.DBAccessor#loadItemByID(java.lang.String)
	 */
	public void loadItemByID(String id) throws SQLException,
			ClassNotFoundException {
		checkConnection();
		this.getEntryByID.setString(1, id);

		if (this.getEntryByID.execute()) {
			ResultSet results = this.getEntryByID.getResultSet();

			if (results.next()) {
				String baseform = results.getString("baseform");
				String category = results.getString("category");
				String infl = results.getString("infl");
				String agr = results.getString("agr");
				Boolean hasComp = results.getBoolean("hasComp");
				LexicalItem item = constructItem(id, baseform, hasComp,
						category, infl, agr);

				if (item != null) {
					this.lexicon.addItem(item);
					loadDerivations(item);
					//loadVariants(item);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.db.DBAccessor#loadItem(simplenlg.features.Category,
	 * java.lang.String)
	 */
	public void loadItem(Category cat, String baseform) throws SQLException,
			ClassNotFoundException {
		checkConnection();
		String c;

		switch (cat) {
		case NOUN:
		case VERB:
			c = cat.toString().toLowerCase();
			break;
		case PRONOUN:
			c = "pron";
			break;
		case DETERMINER:
			c = "det";
			break;
		case COMPLEMENTISER:
			c = "comp";
			break;
		case CONJUNCTION:
			c = "conj";
			break;
		case ADJECTIVE:
			c = "adj";
			break;
		case ADVERB:
			c = "adv";
			break;
		case PREPOSITION:
			c = "prep";
			break;
		default:
			return;

		}

		this.getEntryByCatBaseform.setString(1, c);
		this.getEntryByCatBaseform.setString(2, baseform);

		if (this.getEntryByCatBaseform.execute()) {
			ResultSet results = this.getEntryByCatBaseform.getResultSet();

			if (results.next()) {
				String id = results.getString("euid");
				String infl = results.getString("infl");
				String agr = results.getString("agr");
				Boolean hasComp = results.getBoolean("hasComp");
				LexicalItem item = constructItem(id, baseform, hasComp, c,
						infl, agr);
				loadDerivations(item);
				//loadVariants(item);
				this.lexicon.addItem(item);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.db.DBAccessor#loadItemsByBaseform(java.lang.String)
	 */
	public void loadItemsByBaseform(String baseform) throws SQLException,
			ClassNotFoundException {
		checkConnection();
		this.getEntryByBaseform.setString(1, baseform);

		if (this.getEntryByBaseform.execute()) {
			ResultSet results = this.getEntryByBaseform.getResultSet();

			while (results.next()) {
				String id = results.getString("euid");
				String category = results.getString("category");
				String infl = results.getString("infl");
				String agr = results.getString("agr");
				Boolean hasComp = results.getBoolean("hasComp");
				LexicalItem item = constructItem(id, baseform, hasComp,
						category, infl, agr);
				//loadVariants(item);
				loadDerivations(item);
				this.lexicon.addItem(item);
			}
		}

	}

	/**
	 * Load orthographic variants of a lexical item, if any are specified. This
	 * method assumes the existence of the <code>baseforms</code> table in the
	 * lexical database provided, which pairs word forms with euids, where
	 * multiple forms may exist for a single euid (for example, multiple
	 * spellings of <I>cooperativise</I>, including <I>co-operativise</I>,
	 * <I>cooperativize</I> etc).
	 * 
	 */
/*	public void loadVariants(LexicalItem lex) throws SQLException {
		this.getVariants.setString(1, lex.getID());
		this.getVariants.execute();
		ResultSet results = this.getVariants.getResultSet();

		while (results.next()) {
			lex.addVariant(results.getString(1));
		}
	}
*/
	/**
	 * 
	 * @throws SQLException
	 * @since version 3.8
	 */
	public void loadVerbnetData() throws SQLException, ClassNotFoundException {
		checkConnection();
		ResultSet result = this.getVerbFrames.executeQuery();

		if (result == null) {
			return;
		}

		// record of frames seen -- so XMl parsed only once per frame
		Map<Integer, VerbnetFrame> seenFrames = new HashMap<Integer, VerbnetFrame>();

		while (result.next()) {
			String verbID = result.getString(1);
			Verb verb = (Verb) this.lexicon.getItemByID(verbID);

			// ignore data for verbs we don't have (this should never happen)
			if (verb == null) {
				continue;
			}

			String className = result.getString(2);
			String superclassName = result.getString(3);
			VerbnetClass vnClass, vnSuperClass;

			// check if vn class is already in lexicon, else create it
			if (this.lexicon.hasLexicalClass(className)) {
				vnClass = (VerbnetClass) this.lexicon
						.getLexicalClass(className);
			} else {
				vnClass = new VerbnetClass(className);
				this.lexicon.addLexicalClass(vnClass);

				// check if this class has a superclass
				// (if yes, it's already in the lexicon)
				if (superclassName != null) {
					vnSuperClass = (VerbnetClass) this.lexicon
							.getLexicalClass(superclassName);

					if (vnSuperClass != null) {
						vnSuperClass.addSubclass(vnClass);
					}
				}
			}

			int frameid = result.getInt(4);
			VerbnetFrame frame;

			// check if we've seen the frame already
			if (seenFrames.containsKey(frameid)) {
				frame = seenFrames.get(frameid);
			} else {
				String syntax = result.getString(7);
				frame = parseVerbframeSyntax(syntax);

				// verb frame descriptors
				String desc1 = result.getString(5);
				String desc2 = result.getString(6);
				frame.setMainDescription(desc1);
				frame.setSubDescription(desc2);
				seenFrames.put(frameid, frame);
			}

			vnClass.addFrame(frame);
			vnClass.addMember(verb);
		}
	}

	// *************************************************************************
	// PRIVATE METHODS: various utilities for retrieving features of items
	// *************************************************************************
	private void checkConnection() throws ClassNotFoundException, SQLException {
		if (!hasLexicon()) {
			throw new LexiconException(
					"Cannot load data: the lexicon has not been set");
		}

		if (!this.connected) {
			connect();
		}
	}

	/*
	 * Parse an XML fragment to build a verb frame
	 */
	private VerbnetFrame parseVerbframeSyntax(String xml) {

		try {
			StringReader reader = new StringReader(xml);
			InputSource source = new InputSource(reader);
			// Document doc = builder.parse(new InputSource(reader));
			VerbnetFrame frame = new VerbnetFrame();

			NodeList nodeset = (NodeList) this.xpath.evaluate(
					SQLConfig.SYNTAX_PATH, source, XPathConstants.NODESET);

			for (int i = 0; i < nodeset.getLength(); i++) {
				Node node = nodeset.item(i);
				String name = node.getNodeName();
				NamedNodeMap attributes = node.getAttributes();

				// map the node name to a slot type
				VerbnetSlotType type = VerbnetSlotType.valueOf(name);

				// map the content
				Node content = attributes
						.getNamedItem(SQLConfig.SLOT_ELEMENT_VALUE_ATT);

				if (content != null) {
					String cont = content.getNodeValue();
					// content may be more than one
					String[] contents = cont.split(" ");
					frame.addSlot(type, contents);
				} else {
					frame.addSlot(type);
				}
			}

			return frame;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * construct a lexical item, based on db info
	 */
	private LexicalItem constructItem(String id, String baseform,
			boolean hasComp, String category, String infl, String agr)
			throws SQLException {
		LexicalItem item = null;

		if (category.equals("noun")) {
			item = new Noun(id, baseform);
			((Noun) item).setAgreement(getAgreementCode(agr));

		} else if (category.equals("verb")) {
			item = new Verb(id, baseform);
			((Verb) item).setVerbType(VerbType.MAIN);

		} else if (category.equals("aux")) {
			item = new Verb(id, baseform);
			((Verb) item).setVerbType(VerbType.AUX);

		} else if (category.equals("modal")) {
			item = new Verb(id, baseform);
			((Verb) item).setVerbType(VerbType.MODAL);

		} else if (category.equals("adj")) {
			item = new Adjective(id, baseform);
			item = addAdjFeatures((Adjective) item);

		} else if (category.equals("adv")) {
			item = new Adverb(id, baseform);
			item = addAdverbFeatures((Adverb) item);

		} else if (category.equals("prep")) {
			item = new Preposition(id, baseform);
		} else if (category.equals("pron")) {
			item = new Pronoun(id, baseform);
			((Pronoun) item).setAgreement(getAgreementCode(agr));
			item = addPronounFeatures((Pronoun) item);

		} else if (category.equals("det")) {
			item = new Determiner(id, baseform);
			((Determiner) item).setAgreement(getAgreementCode(agr));
			item = addDetFeatures((Determiner) item);

		} else if (category.equals("comp")) {
			item = new Complementiser(id, baseform);
		} else if (category.equals("conj")) {
			item = new Conjunction(id, baseform);
			item = addConjFeatures((Conjunction) item);
		}

		// finally, add inflection code
		if (item != null) {
			item.setInflectionType(getInflectionCode(infl));
		}

		if (hasComp) {
			item = addComplements((ContentWord) item);
		}

		//loadVariants(item);
		return item;
	}

	/*
	 * Parse the category of verb and get its type
	 */
	private VerbType getVerbType(String category) {
		if (category.indexOf("aux") > -1) {
			return VerbType.AUX;
		} else if (category.indexOf("modal") > -1) {
			return VerbType.MODAL;
		} else {
			return VerbType.MAIN;
		}
	}

	/*
	 * Map DB agreement value to Agreement
	 */
	private Agreement getAgreementCode(String agr) {

		if (agr.indexOf("plur") > -1) {
			return Agreement.FIXED_PLUR;
		} else if (agr.indexOf("sing") > -1) {
			return Agreement.FIXED_SING;
		} else if (agr.indexOf("group") > -1) {
			return Agreement.GROUP;
		} else if (agr.indexOf("uncount") > -1) {
			return Agreement.MASS;
		} else {
			return Agreement.COUNT;
		}

	}

	/*
	 * Map inflection db value to InflectionType
	 */
	private InflectionType getInflectionCode(String infl) {

		if (infl.indexOf("glreg") > -1) {
			return InflectionType.GL_REGULAR;
		} else if (infl.indexOf("regd") > -1) {
			return InflectionType.REG_DOUBLING;
		} else if (infl.indexOf("irreg") > -1) {
			return InflectionType.IRREGULAR;
		} else if (infl.indexOf("reg") > -1) {
			return InflectionType.REGULAR;
		} else if (infl.indexOf("inv") > -1) {
			return InflectionType.INVARIANT;
		} else if (infl.indexOf("periph") > -1) {
			return InflectionType.PERIPHRASTIC;
		}

		return null;
	}

	/*
	 * Retrieve the complements for a single lexical item at a given row of the
	 * complements table
	 */
	private ContentWord addComplements(ContentWord word) throws SQLException {
		ComplementFrame frame;
		String id = word.getID();
		this.complement.setString(1, id);

		if (this.complement.execute()) {
			ResultSet results = this.complement.getResultSet();

			while (results.next()) {
				String c1 = results.getString("comp1");
				String cr1 = results.getString("comp1Restr");
				String c2 = results.getString("comp2");
				String cr2 = results.getString("comp2Restr");
				String r1 = results.getString("gRestr1");
				String r2 = results.getString("gRestr2");
				frame = makeFrame(c1, cr1, c2, cr2, r1, r2);

				if (frame != null) {
					frame.setTransitivity(getTrans(results
							.getString("transitivity")));
					word.addComplementFrame(frame);
				}
			}
		}
		return word;
	}

	/*
	 * Get transitivity value for a complement
	 */
	private Transitivity getTrans(String t) {

		if (t == null) {
			return null;
		} else if (t.indexOf("cplxtran") > -1) {
			return Transitivity.COMPLEX_TRANS;
		} else if (t.indexOf("ditran") > -1) {
			return Transitivity.DITRANS;
		} else if (t.indexOf("intran") > -1) {
			return Transitivity.INTRANS;
		} else if (t.indexOf("tran") > -1) {
			return Transitivity.MONOTRANS;
		} else if (t.indexOf("link") > -1) {
			return Transitivity.LINKING;
		} else {
			return Transitivity.INTRANS;
		}
	}

	/*
	 * Get the complement type value
	 */
	private ComplementFrame makeFrame(String c1, String cr1, String c2,
			String cr2, String r1, String r2) {

		if (c1 == null) {
			return null;
		}

		ComplementFrame frame = new ComplementFrame();

		// passive and dative restrictions
		String[] passDat = new String[] { r1, r2 };
		for (String pd : passDat) {
			if (pd == null) {
				continue;
			} else if (pd.indexOf("nopass") > -1) {
				frame.setAllowsPassive(false);
			} else if (pd.indexOf("datmvt") > 1) {
				frame.setAllowsDativeShift(true);
			}
		}

		// ppMatcher.reset(c);
		this.cppMatcher.reset(c1);

		// check for PPs first
		if (this.cppMatcher.matches()) {
			String restr1 = this.cppMatcher.group(1);
			String restr2 = this.cppMatcher.group(3);
			Preposition[] heads = new Preposition[2];
			heads[0] = new Preposition(restr1);

			if (restr2 != null) {
				heads[1] = new Preposition(restr2);
			}

			for (Preposition p : heads) {
				ComplementSlot slot = new ComplementSlot(ComplementType.PP,
						null, p);
				frame.addComplement(slot);
			}

		} else {
			// get slots and codes
			ComplementSlot slot1 = parseCompType(c1);
			ComplementSlot slot2 = parseCompType(c2);
			InterpretationCode code1 = parseCode(cr1);
			InterpretationCode code2 = parseCode(cr2);

			// ignore null slots
			if (slot1 != null) {

				if (code1 != null) {
					slot1.setCode(code1);
				}

				frame.addComplement(slot1);
			}

			if (slot2 != null) {

				if (code2 != null) {
					slot2.setCode(code2);
				}

				frame.addComplement(slot2);
			}
		}

		return frame;
	}

	/*
	 * Construct a complement slot
	 */
	private ComplementSlot parseCompType(String c) {

		if (c == null) {
			return null;
		} else if (c.indexOf("np") > -1) {
			return new ComplementSlot(ComplementType.NP);
		} else if (c.indexOf("ingcomp") > -1) {
			return new ComplementSlot(ComplementType.ING);
		} else if (c.indexOf("binfcomp") > -1) {
			return new ComplementSlot(ComplementType.BARE_INF);
		} else if (c.indexOf("infcomp") > -1) {
			return new ComplementSlot(ComplementType.INF);
		} else if (c.indexOf("ascomp") > -1) {
			return new ComplementSlot(ComplementType.AS_COMP);
		} else if (c.indexOf("edcomp") > -1) {
			return new ComplementSlot(ComplementType.ED);
		} else if (c.indexOf("whfincomp") > -1) {
			return new ComplementSlot(ComplementType.WHFINCOMP);
		} else if (c.indexOf("whinfcomp") > -1) {
			return new ComplementSlot(ComplementType.WHINFCOMP);
		} else if (c.indexOf("adj") > -1) {
			return new ComplementSlot(ComplementType.ADJP);
		} else if (c.indexOf("advbl") > -1) {
			return new ComplementSlot(ComplementType.ADVP);
		} else if (c.indexOf("fincomp") > -1) {
			return new ComplementSlot(ComplementType.FINCOMP);
		}

		return null;
	}

	/*
	 * Retrieve an interpretation code
	 */
	private InterpretationCode parseCode(String code) {

		if (code == null) {
			return null;
		}

		if (code.indexOf("arbc") > -1) {
			return InterpretationCode.ARB_CONTROL;
		} else if (code.indexOf("nsr") > -1) {
			return InterpretationCode.NON_SUBJ_CONTROL;
		} else if (code.indexOf("subjr") > -1) {
			return InterpretationCode.SUBJ_RAISING;
		} else if (code.indexOf("subjc") > -1) {
			return InterpretationCode.SUBJ_CONTROL;
		} else if (code.indexOf("objr") > -1) {
			return InterpretationCode.OBJ_RAISING;
		} else if (code.indexOf("objc") > -1) {
			return InterpretationCode.OBJ_CONTROL;
		} else if (code.indexOf("tp") > -1) {
			return InterpretationCode.REQ_THAT_PRO;
		} else if (code.indexOf("tsp") > -1) {
			return InterpretationCode.SUBJUNCTIVE_REQ_THAT_PRO;
		} else if (code.indexOf("ts") > -1) {
			return InterpretationCode.SUBJUNCTIVE_REQ_THAT;
		} else if (code.indexOf("o") > -1) {
			return InterpretationCode.OPT_THAT;
		} else if (code.indexOf("t") > -1) {
			return InterpretationCode.REQ_THAT;
		} else if (code.indexOf("p") > -1) {
			return InterpretationCode.OPT_THAT_PRO;
		} else if (code.indexOf("s") > -1) {
			return InterpretationCode.SUBJUNCTIVE_OPT_THAT;
		}

		return null;
	}

	/*
	 * parse the string indicating category restriction for conjunction
	 */
	private Category getCatRestriction(String catRestr) {

		try {
			return Category.valueOf(catRestr.toUpperCase());

		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * parse the string indicating conjunction type (coord/subord)
	 */
	private ConjunctionType getConjType(String type) {

		try {
			return ConjunctionType.valueOf(type.toUpperCase());

		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * get adverb position value
	 */
	private AdverbPosition getAdvPos(String pos) {

		if (pos == null) {
			return null;
		} else if (pos.indexOf("verb") > -1) {
			return AdverbPosition.VERBAL;
		} else if (pos.indexOf("sentence") > -1) {
			return AdverbPosition.SENTENTIAL;
		} else if (pos.indexOf("particle") > -1) {
			return AdverbPosition.VERB_PARTICLE;
		}

		return null;
	}

	/*
	 * get adverb type
	 */
	private AdverbType getAdvType(String pos) {

		if (pos == null) {
			return null;
		} else if (pos.indexOf("temporal") > -1) {
			return AdverbType.TEMPORAL;
		} else if (pos.indexOf("manner") > -1) {
			return AdverbType.MANNER;
		} else if (pos.indexOf("locative") > -1) {
			return AdverbType.LOCATIVE;
		} else if (pos.indexOf("intensifier") > -1) {
			return AdverbType.INTENSIFIER;
		}

		return null;

	}

	/*
	 * get adjective position value
	 */
	private List<AdjectivePosition> getAdjPos(String posString) {
		List<AdjectivePosition> positions = new ArrayList<AdjectivePosition>();
		String[] values = posString.split(",");

		for (String pos : values) {
			if (pos == null) {
				continue;
			} else if (pos.indexOf("post") > -1) {
				positions.add(AdjectivePosition.POST_NOMINAL);
			} else if (pos.indexOf("pred") > -1) {
				positions.add(AdjectivePosition.PREDICATIVE);
			} else if (pos.indexOf("attribc") > -1) {
				positions.add(AdjectivePosition.ATTRIB_C);
			} else if (pos.indexOf("attrib(1)") > -1) {
				positions.add(AdjectivePosition.ATTRIB_1);
			} else if (pos.indexOf("attrib(2)") > -1) {
				positions.add(AdjectivePosition.ATTRIB_2);
			} else if (pos.indexOf("attrib(3)") > -1) {
				positions.add(AdjectivePosition.ATTRIB_3);
			}
		}

		return positions;
	}

	/*
	 * Add features position and type to adjective
	 */
	private Adjective addAdjFeatures(Adjective a) throws SQLException {
		String id = a.getID();
		this.adjFeature.setString(1, id);

		if (this.adjFeature.execute()) {
			ResultSet results = this.adjFeature.getResultSet();

			if (results.next()) {
				String positions = results.getString("positions");
				boolean stat = results.getBoolean("stative");

				for (AdjectivePosition p : getAdjPos(positions)) {
					a.addPosition(p);
				}

				if (stat) {
					a.setType(AdjectiveType.STATIVE);
				} else {
					a.setType(AdjectiveType.NON_STATIVE);
				}
			}
		}

		return a;
	}

	/*
	 * Add features to an adverb
	 */
	private Adverb addAdverbFeatures(Adverb a) throws SQLException {
		String id = a.getID();
		this.advFeature.setString(1, id);

		if (this.advFeature.execute()) {
			ResultSet results = this.advFeature.getResultSet();

			if (results.next()) {
				String positions = results.getString("positions");
				String polarity = results.getString("polarity");

				// set the positions allowed for this adv, and its type
				for (String s : positions.split(",")) {
					AdverbPosition p = getAdvPos(s);
					AdverbType t = getAdvType(s);

					if (p != null) {
						a.addPosition(p);
					}

					if (t != null) {
						a.addAdverbType(t);
					}
				}

				a.setPolarity(getPolarity(polarity));
			}
		}

		return a;
	}

	/*
	 * get gender depending on DB code
	 */
	private Gender getGender(String gend) {

		if (gend == null) {
			return null;
		}

		if (gend.indexOf("pers(masc)") > -1) {
			return Gender.MASCULINE;
		} else if (gend.indexOf("pers(fem)") > -1) {
			return Gender.FEMININE;
		}

		return Gender.NEUTER;
	}

	/*
	 * get case value depending on DB code
	 */
	private Case getCase(String cas) {

		if (cas == null) {
			return null;
		} else if (cas.indexOf("subj_obj") > -1) {
			return Case.NOM_ACC;
		} else if (cas.indexOf("subj") > -1) {
			return Case.NOMINATIVE;
		} else if (cas.indexOf("obj") > -1) {
			return Case.ACCUSATIVE;
		}

		return null;
	}

	/*
	 * get the Possession value depending on db code
	 */
	private Possession getPoss(String pos) {

		if (pos == null) {
			return null;
		} else if (pos.indexOf("poss_possnom") > -1) {
			return Possession.ANY;
		} else if (pos.indexOf("possnom") > -1) {
			return Possession.POSSESSIVE_NOMINATIVE;
		} else if (pos.indexOf("poss") > -1) {
			return Possession.POSSESSIVE;
		}

		return Possession.NON_POSSESSIVE;

	}

	/*
	 * get the quantification value depending on db code
	 */
	private Quantification getQuant(String quant) {

		if (quant == null) {
			return null;
		} else if (quant.indexOf("univ") > -1) {
			return Quantification.UNIVERSAL;
		} else if (quant.indexOf("indef(nonassert)") > -1) {
			return Quantification.INDEF_NON_ASSERT;
		} else if (quant.indexOf("indef(assert)") > -1) {
			return Quantification.INDEF_ASSERT;
		} else if (quant.indexOf("indef(neg)") > -1) {
			return Quantification.INDEF_NEGATIVE;
		}

		return Quantification.DEFINITE;
	}

	/*
	 * check whether this pronoun is reflexive
	 */
	private Reflexivity getRefl(String t) {

		if (t != null && t.indexOf("reflexive") > -1) {
			return Reflexivity.REFLEXIVE;
		}

		return Reflexivity.NON_REFLEXIVE;
	}

	/*
	 * check whether this pro is demonstrative
	 */
	private Deixis getDeixis(String t) {

		if (t != null && t.indexOf("demonstrative") > -1) {
			return Deixis.DEMONSTRATIVE;
		}

		return Deixis.NON_DEMONSTRATIVE;
	}

	/*
	 * check if this is a negative polarity item
	 */
	private Polarity getPolarity(String t) {

		if (t == null) {
			return Polarity.POSITIVE;
		}

		if (t.indexOf("broad_negative") > -1) {
			return Polarity.BROAD_NEGATIVE;
		} else if (t.indexOf("negative") > -1) {
			return Polarity.NEGATIVE;
		} else {
			return Polarity.POSITIVE;
		}

	}

	/*
	 * Set features for a determiner
	 */
	private Determiner addDetFeatures(Determiner d) throws SQLException {
		this.detFeature.setString(1, d.getID());

		if (this.detFeature.execute()) {
			ResultSet results = this.detFeature.getResultSet();

			if (results.next()) {
				d.setDeixis(getDeixis(results.getString("detType")));
			}
		}

		return d;
	}

	/*
	 * add features to a conjunction
	 */
	private Conjunction addConjFeatures(Conjunction c) throws SQLException {
		this.conjFeature.setString(1, c.getID());

		if (this.conjFeature.execute()) {
			ResultSet results = this.conjFeature.getResultSet();

			if (results.next()) {
				String ctype = results.getString("conjType");
				String atype = results.getString("argType");
				c.setConjType(getConjType(ctype));
				c.setCategoryRestriction(getCatRestriction(atype));
			}
		}

		return c;
	}

	/*
	 * Add features to a pronoun
	 */
	private Pronoun addPronounFeatures(Pronoun p) throws SQLException {
		this.proFeature.setString(1, p.getID());

		if (this.proFeature.execute()) {
			ResultSet results = this.proFeature.getResultSet();

			if (results.next()) {
				String gender = results.getString("gender");
				String cse = results.getString("case1");
				String poss = results.getString("possession");
				String quant = results.getString("quantification");
				String type = results.getString("pronType");
				p.setGender(getGender(gender));
				p.setCaseValue(getCase(cse));
				p.setPossession(getPoss(poss));
				p.setQuantification(getQuant(quant));
				p.setReflexivity(getRefl(type));
				p.setDeixis(getDeixis(type));
				p.setPolarity(getPolarity(type));
			}
		}

		return p;
	}

	/*
	 * Load derivations for a lexical item, if any
	 */
	private void loadDerivations(LexicalItem lex) throws SQLException {
		this.getDerivations.setString(1, lex.getID());

		if (this.getDerivations.execute()) {
			ResultSet results = this.getDerivations.getResultSet();

			while (results.next()) {
				String relatedItemID = results.getString(1);
				String relation = results.getString(2);
				DerivationalRelation dr = parseDerivRelation(relation);

				if (dr != null) {
					LexicalItem relatedItem = this.lexicon
							.getItemByID(relatedItemID);
					lex.addDerivationalRelation(dr, relatedItem);

					DerivationalRelation inv = dr.getInverse();

					if (inv != null) {
						relatedItem.addDerivationalRelation(inv, lex);
					}

				}
			}
		}
	}

	/*
	 * parses the relation field value in table derivations
	 */
	private DerivationalRelation parseDerivRelation(String rel) {
		for (DerivationalRelation dr : DerivationalRelation.values()) {
			if (dr.toString().equalsIgnoreCase(rel)) {
				return dr;
			}
		}

		return null;
	}
}
