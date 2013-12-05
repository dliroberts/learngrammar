package simplenlg.lexicon;

import simplenlg.exception.LexiconException;
import simplenlg.lexicon.db.DBAccessor;

/**
 * Extension of the {@link LexiconInterface} for lexicons that are backed by a
 * database of lexical information, in whatever format. A
 * <code>DatabaseBackedLexicon</code> is supplied with a
 * {@link simplenlg.lexicon.db.DBAccessor} to which it delegates the task of
 * loading lexical information from the database, and instantiating lexical
 * items which are then stored in the lexicon itself.
 * 
 * @author agatt
 * 
 */
public interface DatabaseBackedLexicon extends LexiconInterface {

	/**
	 * Set the <code>DBAccessor</code> that provides the link between this
	 * lexicon and the lexical database.
	 * 
	 * @param accessor
	 *            The accessor
	 */
	void setAccessor(DBAccessor accessor);

	/**
	 * 
	 * @return The <code>DBAccessor</code> if one has been set,
	 *         <code>null</code> otherwise.
	 */
	DBAccessor getAccessor();

	/**
	 * 
	 * @return <code>true</code> if a DBAccessor has been set.
	 */
	boolean hasAccessor();

	/**
	 * Loads all the data in the database, via a call to
	 * {@link simplenlg.lexicon.db.DBAccessor#loadData()}.
	 * 
	 * @throws LexiconException
	 *             In case any exceptions are thrown by
	 *             <code>DBAccessor.loadData()</code>, they should be wrapped in
	 *             a <code>LexiconException</code>.
	 */
	void loadData() throws LexiconException;

	/**
	 * Load a single item from the database, given its unique identifier.
	 * 
	 * <P>
	 * This method should involve an invocation of the
	 * {@link simplenlg.lexicon.db.DBAccessor#loadItemByID(String)} method in
	 * the lexicon's <code>DBAccessor</code>.
	 * 
	 * @param id
	 *            The identifier of the lexical item
	 * @throws LexiconException
	 *             In case any exceptions are thrown by <code>DBAccessor</code>,
	 *             they should be wrapped in a <code>LexiconException</code>.
	 */
	void loadItemByID(String id) throws LexiconException;

	/**
	 * Load the items in the database with the given database.
	 * 
	 * <P>
	 * This method should involve an invocation of the
	 * {@link simplenlg.lexicon.db.DBAccessor#loadItemByID(String)} method in
	 * the lexicon's <code>DBAccessor</code>.
	 * 
	 * @param baseform
	 *            The baseform
	 * 
	 * @throws LexiconException
	 *             In case any exceptions are thrown by
	 *             <code>DBAccessor.loadData()</code>, they should be wrapped in
	 *             a <code>LexiconException</code>.
	 * 
	 */
	void loadItemsByBaseform(String baseform) throws LexiconException;

}
