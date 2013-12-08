package uk.ac.cam.dr369.learngrammar.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class Utils {
	/** For convenience */
	public static final String NEWLINE = System.getProperty("line.separator");
	
	private static final Properties PROPERTIES = new Properties();
	
	private static final List<String> COMMON_TOKENISER_MAP_TOK = Lists.newArrayList();
	private static final List<String> COMMON_TOKENISER_MAP_DETOK = Lists.newArrayList();
	private static final List<String> TOKENISER_MAP_TOK = Lists.newArrayList();
	private static final List<String> TOKENISER_MAP_DETOK = Lists.newArrayList();
	private static final List<String> DETOKENISER_MAP_TOK = Lists.newArrayList();
	private static final List<String> DETOKENISER_MAP_DETOK = Lists.newArrayList();
	
	static {
		try {
			PROPERTIES.load(Utils.class.getClassLoader().getResourceAsStream(("LearnGrammar.properties")));
		} catch (IOException e) {
			throw new RuntimeException("Unable to load properties file.", e);
		}
		
		COMMON_TOKENISER_MAP_TOK.add(" -LRB- "); COMMON_TOKENISER_MAP_DETOK.add(" (");
		COMMON_TOKENISER_MAP_TOK.add(" -RRB- "); COMMON_TOKENISER_MAP_DETOK.add("); ");
		COMMON_TOKENISER_MAP_TOK.add(" -LSB- "); COMMON_TOKENISER_MAP_DETOK.add(" [");
		COMMON_TOKENISER_MAP_TOK.add(" -RSB- "); COMMON_TOKENISER_MAP_DETOK.add("[ ");
		COMMON_TOKENISER_MAP_TOK.add(" -LCB- "); COMMON_TOKENISER_MAP_DETOK.add(" {");
		COMMON_TOKENISER_MAP_TOK.add(" -RCB- "); COMMON_TOKENISER_MAP_DETOK.add("} ");
		COMMON_TOKENISER_MAP_TOK.add(" .");      COMMON_TOKENISER_MAP_DETOK.add(".");
		COMMON_TOKENISER_MAP_TOK.add(" !");      COMMON_TOKENISER_MAP_DETOK.add("!");
		COMMON_TOKENISER_MAP_TOK.add(" ?");      COMMON_TOKENISER_MAP_DETOK.add("?");
		COMMON_TOKENISER_MAP_TOK.add(" ,");      COMMON_TOKENISER_MAP_DETOK.add(",");
		COMMON_TOKENISER_MAP_TOK.add(" :");      COMMON_TOKENISER_MAP_DETOK.add(":");
		COMMON_TOKENISER_MAP_TOK.add(" ;");      COMMON_TOKENISER_MAP_DETOK.add(";");
		COMMON_TOKENISER_MAP_TOK.add("( ");      COMMON_TOKENISER_MAP_DETOK.add("(");
		COMMON_TOKENISER_MAP_TOK.add("[ ");      COMMON_TOKENISER_MAP_DETOK.add("[");
		COMMON_TOKENISER_MAP_TOK.add("{ ");      COMMON_TOKENISER_MAP_DETOK.add("{");
		COMMON_TOKENISER_MAP_TOK.add(" )");      COMMON_TOKENISER_MAP_DETOK.add(");");
		COMMON_TOKENISER_MAP_TOK.add(" ]");      COMMON_TOKENISER_MAP_DETOK.add("]");
		COMMON_TOKENISER_MAP_TOK.add(" }");      COMMON_TOKENISER_MAP_DETOK.add("}");
		COMMON_TOKENISER_MAP_TOK.add(" / ");     COMMON_TOKENISER_MAP_DETOK.add("/");
		COMMON_TOKENISER_MAP_TOK.add(" %");      COMMON_TOKENISER_MAP_DETOK.add("%");
		COMMON_TOKENISER_MAP_TOK.add("$ ");      COMMON_TOKENISER_MAP_DETOK.add("$");
		COMMON_TOKENISER_MAP_TOK.add("£ ");     COMMON_TOKENISER_MAP_DETOK.add("£"); // XXX odd encoding-related schenanigans...
		
		// Using \t as temporary character for instances of ' that are a part of a token while we substitute quotation marks.
		TOKENISER_MAP_TOK.add(" \ts ");   TOKENISER_MAP_DETOK.add("'s ");
		TOKENISER_MAP_TOK.add(" \td ");   TOKENISER_MAP_DETOK.add("'d ");
		TOKENISER_MAP_TOK.add(" n\tt");   TOKENISER_MAP_DETOK.add("n't");
		TOKENISER_MAP_TOK.add(" \tm");    TOKENISER_MAP_DETOK.add("'m");
		TOKENISER_MAP_TOK.add(" \tll ");  TOKENISER_MAP_DETOK.add("'ll ");
		TOKENISER_MAP_TOK.add(" \tre");   TOKENISER_MAP_DETOK.add("'re");
		TOKENISER_MAP_TOK.add(" \tve");   TOKENISER_MAP_DETOK.add("'ve");
		TOKENISER_MAP_TOK.add("\tt was"); TOKENISER_MAP_DETOK.add("'twas");
		TOKENISER_MAP_TOK.add("\tT was"); TOKENISER_MAP_DETOK.add("'Twas");
		TOKENISER_MAP_TOK.add("\t ");     TOKENISER_MAP_DETOK.add(" ' ");
		TOKENISER_MAP_TOK.add(" \t");     TOKENISER_MAP_DETOK.add(" ' ");
		TOKENISER_MAP_TOK.add("\" ");     TOKENISER_MAP_DETOK.add(" \" ");
		TOKENISER_MAP_TOK.add(" \"");     TOKENISER_MAP_DETOK.add(" \" ");
		TOKENISER_MAP_TOK.add("'");       TOKENISER_MAP_DETOK.add("\t");
		
		DETOKENISER_MAP_TOK.add(" 's ");   DETOKENISER_MAP_DETOK.add("'s ");
		DETOKENISER_MAP_TOK.add(" 'd ");   DETOKENISER_MAP_DETOK.add("'d ");
		DETOKENISER_MAP_TOK.add(" n't");   DETOKENISER_MAP_DETOK.add("n't");
		DETOKENISER_MAP_TOK.add(" 'm");    DETOKENISER_MAP_DETOK.add("'m");
		DETOKENISER_MAP_TOK.add(" 'll ");  DETOKENISER_MAP_DETOK.add("'ll ");
		DETOKENISER_MAP_TOK.add(" 're");   DETOKENISER_MAP_DETOK.add("'re");
		DETOKENISER_MAP_TOK.add(" 've");   DETOKENISER_MAP_DETOK.add("'ve");
		DETOKENISER_MAP_TOK.add("'t was"); DETOKENISER_MAP_DETOK.add("'twas");
		DETOKENISER_MAP_TOK.add("'T was"); DETOKENISER_MAP_DETOK.add("'Twas");
	}
	public static String detokenise(String text) {
		text = (' '+text+' ');
		Iterator<String> detokIt = DETOKENISER_MAP_DETOK.iterator();
		for (String from : DETOKENISER_MAP_TOK) {
			String to = detokIt.next();
			text = text.replace(from, to);
		}
		detokIt = COMMON_TOKENISER_MAP_DETOK.iterator();
		for (String from : COMMON_TOKENISER_MAP_TOK) {
			String to = detokIt.next();
			text = text.replace(from, to);
		}
		// Naive handling of quotes. Alternate between assuming left and right quotes.
		boolean left = true;
		int idx = -2;
		while ((idx = text.indexOf(" \" ")) >= 0) {
			String before = text.substring(0, idx);
			String after = text.substring(idx+3);
			text = before + (left ? " \"" : "\" ") + after;
			left = !left;
		}
		if (text.indexOf(" ' ", text.indexOf(" ' ")) == -1) { // Only one ' - so guess that it's an apostrophe, not a quote mark...
			text.replace(" ' ", "' ");
		}
		else {
			while ((idx = text.indexOf(" ' ")) >= 0) {
				String before = text.substring(0, idx);
				String after = text.substring(idx+3);
				text = before + (left ? " '" : "' ") + after;
				left = !left;
			}
		}
		text = text
			.replace(" -- ", " \u2012 ") // break in sentence - replace with 'em-dash'
			.replace("--", "\u2013"); // range of values - replace with 'en-dash'
		return text.trim();
	}
	public static String tokenise(String text) {
		text = (' '+text+' ');
		Iterator<String> detokIt = TOKENISER_MAP_DETOK.iterator();
		for (String to : TOKENISER_MAP_TOK) {
			String from = detokIt.next();
			text = text.replace(from, to);
		}
		detokIt = COMMON_TOKENISER_MAP_DETOK.iterator();
		for (String to : COMMON_TOKENISER_MAP_TOK) {
			String from = detokIt.next();
			text = text.replace(from, to);
		}
		text = text.replaceAll(" +", " ");
		return text.trim();
	}
	public static List<String> tokeniseSentences(String sentences) {
		List<String> sentenceList = Lists.newArrayList();
		BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(Locale.US); // because we're using the WSJ corpus!
		sentenceIterator.setText(sentences);
		int start = sentenceIterator.first();
		for (int end = sentenceIterator.next(); end != BreakIterator.DONE; start = end, end = sentenceIterator.next()) {
			sentenceList.add(sentences.substring(start, end).trim());
		}
		return sentenceList;
	}
	public static <S> List<S> sublistSafe(List<S> src, int from, int to) {
		if (from < 0)
			from = 0;
		if (to > src.size()) {
			to = src.size() == 0 ? 0 : src.size();
		}
		return src.subList(from, to);
	}
	/**
	 * Determines whether the value exists for a given key. If not, it's created and inserted.
	 * @param map
	 * @param arg
	 * @return
	 */
	public static <T, U> List<U> establishList(Map<T, List<U>> map, T arg) {
		List<U> list = map.get(arg);
		if (list == null) {
			list = Lists.newArrayList();
			map.put(arg, list);
		}
		return list;
	}
	public static <T, U> Set<U> establishSet(Map<T, Set<U>> map, T arg) {
		Set<U> set = map.get(arg);
		if (set == null) {
			set = Sets.newHashSet();
			map.put(arg, set);
		}
		return set;
	}
	public static <T, U, V> Map<U, V> establishMap(Map<T, Map<U, V>> map, T arg) {
		Map<U, V> submap = map.get(arg);
		if (submap == null) {
			submap = Maps.newHashMap();
			map.put(arg, submap);
		}
		return submap;
	}
	public static <R, C, V> Set<V> establishSetInTable(Table<R, C, Set<V>> multitable, R row, C col) {
		Set<V> cellValueSet = multitable.get(row, col);
		if (cellValueSet == null) {
			cellValueSet = Sets.newHashSet();
			multitable.put(row, col, cellValueSet);
		}
		return cellValueSet;
	}
	public static <R, C, V> List<V> establishListInTable(Table<R, C, List<V>> multitable, R row, C col) {
		List<V> cellValueSet = multitable.get(row, col);
		if (cellValueSet == null) {
			cellValueSet = Lists.newArrayList();
			multitable.put(row, col, cellValueSet);
		}
		return cellValueSet;
	}
	public static <R, C, K, V> Map<K, V> establishMapInTable(Table<R, C, Map<K, V>> multitable, R row, C col) {
		Map<K, V> cellValueMap = multitable.get(row, col);
		if (cellValueMap == null) {
			cellValueMap = Maps.newHashMap();
			multitable.put(row, col, cellValueMap);
		}
		return cellValueMap;
	}
	/**
	 * Sets the value of all elements of <code>keys</code> to <code>value</code> in <code>dest</code>.
	 * 
	 * @param dest
	 * @param keys
	 * @param value
	 */
	public static <T,U> void putSameValue(Map<T,U> dest, Collection<T> keys, U value) {
		for (T t : keys) {
			dest.put(t, value);
		}
	}
	/**
	 * Adds <code>value</code> to the value list associated with each key in <code>keys</code>. If no mapping exists for a key, a mapping is created
	 * with a new <code>ArrayList</code>.
	 * @param <T> Key type.
	 * @param <U> Element of value list type.
	 * @param dest Map in which keys/values are to be added.
	 * @param keys Keys to add.
	 * @param value Element to add to value list for each key.
	 */
	public static <T,U> void putValueInListForKeys(Map<T,List<U>> dest, Collection<T> keys, U value) {
		for (T key : keys) {
			establishList(dest, key).add(value);
		}
	}
	public static <T,U> void putValueInSetForKeys(Map<T,Set<U>> dest, Collection<T> keys, U value) {
		for (T key : keys) {
			establishSet(dest, key).add(value);
		}
	}
	public static <T,U> void removeFromSetForKeys(Map<T,Set<U>> src, Collection<T> keys, Collection<U> toRemove) {
		for (T key : keys) {
			if (src.containsKey(key)) {
				Set<U> values = src.get(key);
				values.removeAll(toRemove);
			}
		}
	}
	public static <T> List<T> combine(Collection<T> a, Collection<T> b) {
		List<T> combined = new ArrayList<T>();
		if (a != null)
			combined.addAll(a);
		if (b != null)
			combined.addAll(b);
		
		return combined;
	}
	public static <T> Set<T> union(Collection<T> a, Collection<T> b) {
		Set<T> unioned = new HashSet<T>();
		if (a != null)
			unioned.addAll(a);
		if (b != null)
			unioned.addAll(b);
		return unioned;
	}
	public static <T, U, V> Set<T> unionKeys(Map<T, ?> a, Map<T, ?> b) {
		Set<T> unioned = new HashSet<T>();
		if (a != null)
			unioned.addAll(a.keySet());
		if (b != null)
			unioned.addAll(b.keySet());
		return unioned;
	}
	public static <T> Set<T> intersection(Collection<T> a, Collection<T> b) {
		Set<T> intersected = new HashSet<T>(a);
		intersected.retainAll(b);
		return intersected;
	}
	public static <T> Set<T> complement(Collection<T> a, Collection<T> b) {
		Set<T> complement = new HashSet<T>(a);
		complement.removeAll(b);
		return complement;
	}
	@SuppressWarnings("unchecked") // uncheckable!
	public static <T> void pruneMultilevelMap(Map<T,?> map) { // FIXME doesn't work properly
		for (Iterator<?> it = map.entrySet().iterator(); it.hasNext();) {
			Map.Entry<T, ?> me = (Map.Entry<T, ?>) it.next();
			Object value = me.getValue();
			if (value instanceof Map) {
				Map<T,?> m = (Map<T,?>) value;
				pruneMultilevelMap(m);
				if (m.keySet().isEmpty()) {
					it.remove();
				}
			}
			else if (value instanceof Set) {
				Set<?> s = (Set<?>) value;
				if (s.isEmpty()) {
					it.remove();
				}
			}
		}
	}
	public static <T extends VeryCloneable<T>> List<T> deepCopy(List<T> list) {
		List<T> dc = new ArrayList<T>();
		for (T t : list) {
			dc.add(t.clone());
		}
		return dc;
	}
	public static <T extends VeryCloneable<T>> SortedSet<T> deepCopy(SortedSet<T> list) {
		SortedSet<T> dc = new TreeSet<T>();
		for (T t : list) {
			dc.add(t.clone());
		}
		return dc;
	}
	public static boolean initialCapitalOnly(String text) {
		return text.matches("[^a-z][^A-Z]*");
	}
	public static boolean allCapitals(String text) {
		return text.equals(text.toUpperCase());
	}
	public static boolean allLowerCase(String text) {
		return text.equals(text.toLowerCase());
	}
	public static String toInitialCapitalOnly(String text) {
		String lc = text.toLowerCase();
		return String.valueOf(lc.charAt(0)).toUpperCase() + lc.substring(1).toLowerCase();
	}
	/**
	 * Does what Cloneable was supposed to do.
	 * @author duncan.roberts
	 *
	 * @param <T>
	 */
	public interface VeryCloneable<T> extends Cloneable {
		public T clone();
	}
	/**
	 * Provides a String representation of collection intended for presentation to
	 * the user: comma-separated, except between the last two elements, which
	 * are separated by an 'and'.
	 * 
	 * @param <S> The class of the elements in collection.
	 * @param collection The elements to be listed.
	 * @return A formatted representation of all elements in collection.
	 */
	public static <S> String formatForPrinting(Collection<S> collection) {
		return formatForPrinting(collection, collection.size());
	}
	/**
	 * Provides a String representation of collection intended for presentation to
	 * the user. The listed elements are comma-separated. If collection.length() is
	 * less than or equal to nElements, an 'and' is used instead of a comma between
	 * the last two elements. If collection.length() is greater than nElements,
	 * only nElements of the collection will be quoted, followed by text stating
	 * the number of elements not displayed. 
	 * 
	 * @param <S> The class of the elements in collection.
	 * @param collection The elements to be listed.
	 * @param nElements The maximum number of elements to be printed.
	 * @return A formatted representation of all elements in collection.
	 */
	public static <S> String formatForPrinting(Collection<S> collection, int nElements) {
		StringBuilder sb = new StringBuilder();
		Collection<S> s = new ArrayList<S>();
		if (collection.size() > nElements) {
			Iterator<S> iterator = collection.iterator();
			for (int i = 0; i < nElements; i++) {
				s.add(iterator.next());
			}
		}
		else {
			s.addAll(collection);
		}
		
		int sz = s.size();
		int i = 0;
		for (Object o : s) {
			sb.append(o.toString());
			if (i < sz-2)
				sb.append(", ");
			else if (i == sz-2) {
				if (sz == collection.size())
					sb.append(" and ");
				else
					sb.append(", ");
			}
			i++;
		}
		if (collection.size() > nElements) {
			sb.append("... (");
			sb.append(collection.size() - nElements);
			sb.append(" more)");
		}
		return sb.toString();
	}
	/** Allows methods to 'return' by modifying references to immutable argument types. */
	public static class Wrapper<T> {
		public T wrapped;
	}

	public static List<GrammaticalRelation.GrType> getGrTypes(List<GrammaticalRelation> grs) {
		List<GrammaticalRelation.GrType> grTypes = Lists.newArrayList();
		for (GrammaticalRelation gr : grs) {
			grTypes.add(gr.type());
		}
		return grTypes;
	}
	public static void unzip(ZipFile zipFile, File extractTo) {
		try {
			for (Enumeration<? extends ZipEntry> en = zipFile.entries(); en.hasMoreElements();) {
				ZipEntry entry = en.nextElement();
				File file = new File(extractTo, entry.getName());
				if (entry.isDirectory() && !file.exists()) {
					file.mkdirs();
				}
				else {
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					InputStream in = null;
					BufferedOutputStream out = null;
					try {
						in = zipFile.getInputStream(entry);
						out = new BufferedOutputStream(new FileOutputStream(file));
						byte[] buffer = new byte[8192];
						int read = -2;
						while ((read = in.read(buffer)) > 0) {
							out.write(buffer, 0, read);
						}
					}
					finally {
						if (in != null)
							in.close();
						if (out != null)
							out.close();
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String getProperty(String key) {
		return PROPERTIES.getProperty(key);
	}
	public static String runScript(String script, String input) throws Exception {
			// FIXME massive exploit in code below; should sanitise input before running to avoid arbitrary code exec
			String[] parseTask = new String[] {"/bin/bash", script, input};
			Process process = Runtime.getRuntime().exec(parseTask);
			if(process.waitFor() != 0)
				throw new IllegalStateException("WaitFor != 0");
			if(process.exitValue() != 0)
				throw new IllegalStateException("ExitCode != 0");
			
			String line;
	//		BufferedReader es = new BufferedReader(new InputStreamReader(process.getErrorStream()));
	//		if ((line = es.readLine()) != null) {
	//			throw new IllegalStateException("Error: "+line);
	//		}
			BufferedReader os = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			StringBuilder out = new StringBuilder();
			for (line = null; (line = os.readLine()) != null;) {
				out.append(line);
				out.append('\n');
			}
			return out.toString();
		}
	public static String callWebservice(String urlPrefix, String sentence) throws Exception {
		String sentenceEnc = URLEncoder.encode(sentence, "UTF-8");
		
		URL url = new URL(urlPrefix + sentenceEnc);
		
		StringBuffer sb;
		BufferedReader rd = null;
		URLConnection conn;
		try {
			conn = url.openConnection();
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			sb = new StringBuffer();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			if (rd != null)
				rd.close();
		}
		return sb.toString();
	}
}