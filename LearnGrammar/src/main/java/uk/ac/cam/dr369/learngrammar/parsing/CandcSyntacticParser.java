package uk.ac.cam.dr369.learngrammar.parsing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.dr369.learngrammar.model.CandcPtbPos;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.FlagSubtype;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Subtype;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.TokenSubtype;
import uk.ac.cam.dr369.learngrammar.model.NamedEntityClass;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.util.Utils;

/**
 * Façade into the C&amp;C syntactic parser. Can interact with either the (*nix-only) command-line tool, or a webservice.
 * @author duncan.roberts
 *
 */
public class CandcSyntacticParser extends SyntacticParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(CandcSyntacticParser.class);

	private static final String CANDC_WS_URL = "http://svn.ask.it.usyd.edu.au/demo/demo2.cgi?printer=grs&sentence=";
	//I|I|PRP|I-NP|O|NP - example from server.
	//I|PRP|NP - example from local copy.
	//                                                          lex.item_      lemma_____    POS______      phraseType       named_enty    Supertag_
	private static final Pattern TOKEN_REGEX = Pattern.compile("([^\\|]+)(?:\\|([^\\|]+))?\\|([^\\|]+)(?:\\|([^\\|]+))?(?:\\|([^\\|]+))?\\|([^\\|]+)");

	// Oddness: A small minority of CCGbank derivations contain a GR starting with two (s. Hence "\\(+".
	//                                                         (TYPE________    SUBTYPE___________________ HEAD___________ DEPENDENT______    INITIAL______  )
	private static final Pattern GR_REGEX = Pattern.compile("\\(+([a-z0-9]+)(?: ([^ _]+_[0-9]+|_|[a-z]+))? ([^ _]+_[0-9]+) ([^ _]+_[0-9]+)(?: ([^ _)]+|_))?\\)");
	
	private boolean webservice;
	
	public CandcSyntacticParser(boolean webservice) {
		this.webservice = webservice;
	}
	
	public DependencyStructure toDependencyStructure(String sentence) throws Exception {
		sentence = sentence.replace('\u2018', '\'').replace('\u2019', '\'') // single left/right quotes
				.replace('\u201c', '"').replace('\u201d', '"'); // double left/right quotes
		
		String tokenised = Utils.tokenise(sentence);
		String output = webservice ? Utils.callWebservice(CANDC_WS_URL, tokenised) : Utils.runScript("./candc.sh", sentence);
		return getDependencyStructure(output);
	}

	public List<DependencyStructure> toDependencyStructures(String sentences) throws Exception {
		List<String> lines = Utils.tokeniseSentences(sentences.replace('\n', ' ').replaceAll(" +", " "));
		List<DependencyStructure> dses = new ArrayList<DependencyStructure>();
		for (String line : lines) {
			DependencyStructure ds = toDependencyStructure(line);
			if (ds != null)
				dses.add(ds);
		}
		return dses;
	}

	public Collection<DependencyStructure> loadCorpus(File grFileOrDir) throws IOException {
		return loadCorpus(grFileOrDir, null);
	}
	
	public Collection<DependencyStructure> loadCorpus(File grFileOrDir, Integer maxSentences) throws IOException {
		if (grFileOrDir.isDirectory())
			return loadCorpusFromCandcOutputDir(grFileOrDir);
		else if (grFileOrDir.getName().contains("candcout"))
			return loadCorpusFromCandcOutputFile(grFileOrDir, maxSentences);
		else
			return loadCorpusFromSerialized(grFileOrDir);
	}
	@SuppressWarnings("unchecked")
	public Collection<DependencyStructure> loadCorpusFromSerialized(File grSerializedFile) throws IOException {
		ObjectInputStream ois = null;
		try {
			InputStream is = getInputStream(grSerializedFile);
			ois = new ObjectInputStream(is);
			LOGGER.info("Reading from "+grSerializedFile+"...");
			Object o = ois.readObject();
			LOGGER.info("Read object from "+grSerializedFile+". Size: "+grSerializedFile.length());
			return (Set<DependencyStructure>) o;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to load serialised C&C corpus file.", e);
		}
		finally {
			if (ois != null)
				ois.close();
		}
	}
	/** Returns size of file itself, or if zipped, size of sole file inside zip after inflation. */
	private static long getFileSize(File file) throws IOException {
		if (file.getName().endsWith(".gz") || file.getName().endsWith(".zip")) {
		    ZipFile zf = null;
		    try {
		    	zf = new ZipFile(file); // assumes single file in archive
		    	return zf.entries().nextElement().getSize();
		    }
		    finally {
		    	if (zf != null) {
		    		zf.close();
		    	}
		    }
		}
		else {
			return file.length();
		}
	}
	private static InputStream getInputStream(File file) throws IOException {
		if (file.getName().endsWith(".gz") || file.getName().endsWith(".zip")) {
		    LOGGER.info("Opening zipped file...");
			ZipFile zf = null;
			try {
				zf = new ZipFile(file); // assumes single file in archive
				return zf.getInputStream(zf.entries().nextElement());
			}
		    finally {
		    	if (zf != null) {
		    		zf.close();
		    	}
		    }
		}
		else {
			return new FileInputStream(file);
		}
	}
	private Collection<DependencyStructure> loadCorpusFromCandcOutputFile(File grArchive, Integer maxSentences) throws IOException {
		LOGGER.info("Single C&C output file.");
//		long fileSize = getFileSize(grArchive);
		int fileSize = (int) getFileSize(grArchive);
		// Average bytes per sentence in file: 337. A big sentence (13 tokens) is about 467.
		// Using 800 just to be fairly sure that a small sample won't require more.
		// TODO make this not stupid. e.g. count blank lines (=sentences-1-sentenceswithoutgrs).
		byte[] buffer = new byte[maxSentences == null || (800 * maxSentences) > fileSize ? fileSize : 800 * maxSentences];
	    BufferedInputStream bis = null;
	    try {
	    	InputStream is = getInputStream(grArchive);
	        bis = new BufferedInputStream(is);
	        bis.read(buffer);
	    } finally {
	        if (bis != null) try {
	        	bis.close();
	        } catch (IOException ignored) {
	        	// je m'en tape
	        }
	    }
		return getDependencyStructures(new String(buffer), maxSentences);
	}
	private Collection<DependencyStructure> loadCorpusFromCandcOutputDir(File grDirectory) throws IOException {
		Collection<DependencyStructure> dses = new HashSet<DependencyStructure>();
		Queue<File> files = new LinkedList<File>();
		files.offer(grDirectory);
		do {
			File nextFile = files.poll();
			if (nextFile.isDirectory()) {
				File[] dirFiles = nextFile.listFiles();
				for (File df : dirFiles) {
					files.offer(df);
				}
			}
			else if (nextFile.isFile() && nextFile.getName().endsWith(".gr")) {
			    byte[] buffer = new byte[(int) nextFile.length()];
			    BufferedInputStream bis = null;
			    try {
			        bis = new BufferedInputStream(new FileInputStream(nextFile));
			        bis.read(buffer);
			    } finally {
			        if (bis != null) try {
			        	bis.close();
			        } catch (IOException ignored) {
			        	// je m'en tape
			        }
			    }
				dses.addAll(getDependencyStructures(new String(buffer)));
			}
		} while (!files.isEmpty());
		return dses;
	}
	
	private static DependencyStructure getDependencyStructure(String output) {
		Collection<DependencyStructure> dses = getDependencyStructures(output);
		if (dses.size() > 1)
			throw new IllegalArgumentException("output should contain only one sentence.");
		if (dses.isEmpty())
			return null;
		return dses.iterator().next();
	}
	
	private static Collection<DependencyStructure> getDependencyStructures(String output) {
		return getDependencyStructures(output, null);
	}
	private static Collection<DependencyStructure> getDependencyStructures(String output, Integer maxSentences) {
		Collection<DependencyStructure> dses = new HashSet<DependencyStructure>();
		String[] lines = output.split("\n");
		String tokensStr = null;
		StringBuilder grStr = new StringBuilder();
		
		for (String line : lines) {
			if (line.length() == 0) {
				if (grStr.length() > 0 && tokensStr != null) {
					dses.add(toDependencyStructure(grStr.toString(), tokensStr));
					if (maxSentences != null && dses.size() >= maxSentences)
						return dses;
				}
				// reset
				grStr = new StringBuilder();
				tokensStr = null;
			}
			else if (line.startsWith("#")) { // comment line; ignore
				
			}
			else if (line.startsWith("<c> ")) { // tokens + tags
				tokensStr = line.substring("<c> ".length());
				
//				hackery: uncomment these lines to write a subset of the corpus to a single file, based on sentence length in words:
//				int words = tokensStr.split(" ").length;
//				
//				StringBuilder t = new StringBuilder();
//				for (String word : tokensStr.split(" ")) {
//					String d = word.split("\\|")[0];
//					t.append(' ');
//					t.append(d);
//				}
//				String dt = Utils.detokenise(t.toString());
//				int max = 50;
//				for (int maxWords = words; maxWords <= max; maxWords++) {
////					outfile(grStr, line, maxWords, "all-candcout-1"); // write line with tags.
//					outfile(null, dt, maxWords, "cumul-detok"); // write detokenised sentence.
//				}
//				outfile(null, dt, words, "detok"); // write detokenised sentence.
			}
			else if (line.startsWith("(")) { // a GR
				grStr.append(line);
				grStr.append('\n');
			}
		}
		if (grStr.length() > 0 && tokensStr != null) {
			dses.add(toDependencyStructure(grStr.toString(), tokensStr));
		}
		return dses;
	}
	
//	private static void outfile(StringBuilder grStr, String line, int maxWords, String name) {
//		try {
//			FileWriter fstream = new FileWriter(
//					"/home/dliroberts/Documenti/LearnGrammar/working-area/GR/1file/grLenSubsetAll/"+name+"-"+maxWords+"wds.gr", true);
//	        BufferedWriter out = new BufferedWriter(fstream);
//		    if (grStr != null)
//		    	out.write(grStr.toString());
//		    out.write(line);
//		    out.write(grStr == null ? "\n" : "\n\n");
//		    out.close();
//	    } catch (Exception e){ // Catch exception if any
//	    	System.err.println("Error: " + e.getMessage());
//	    }
//	}
	
	private static DependencyStructure toDependencyStructure(String grStr, String tokensStr) {
		if (tokensStr == null)
			throw new IllegalStateException();
		
		List<GrammaticalRelation> grs = new ArrayList<GrammaticalRelation>();
		List<Token> tokens = new ArrayList<Token>();
		Map<String, Token> tokenMap = new HashMap<String, Token>();
		String[] tokensArr = tokensStr.split(" ");
		int i = 0;
		for (String tokenStr : tokensArr) {
			Token token = toToken(tokenStr, i++);
			tokens.add(token);
			tokenMap.put(token.getWord() + "_" + token.getIndex(), token);
		}
		
		String[] grStrs = grStr.toString().split("\n");
		for (String line : grStrs) {
			if (line.length() > 0) {
				// MASSIVE HACK TO WORK AROUND BUG IN C&C
				// TODO write log entry
				// Very occasionally (for 16 GRs in 50,000 SENTENCES in CCGbank), C&C generates GRs in the form:
				// (dobj)
				// Happens with a variety of GR types - not just dobj.
				if (line.matches("\\([a-z2_]+\\)")) // unparseable; informationless
					continue;
				
				GrammaticalRelation gr = toGr(line, tokenMap);
				grs.add(gr);
			}
		}
		return new DependencyStructure(grs, tokens);
	}

	private static Token toToken(String tokenStr, int index) {
		Matcher tokenMatcher = TOKEN_REGEX.matcher(tokenStr);
		if (!tokenMatcher.matches())
			throw new IllegalStateException("Cannot parse: "+tokenStr);
		if (tokenMatcher.groupCount() == 3) {
			String word = tokenMatcher.group(1).replace("\\/", "/"); // These \\/ replacements are required because of the data in CCGbank.
			String tag = tokenMatcher.group(2);
			String supertag = tokenMatcher.group(3);
			tag = posTagHacks(word, tag);
			return new Token(null, null, index, CandcPtbPos.valueOfByLabel(tag), supertag, word);
		}
		else if (tokenMatcher.groupCount() == 6) {
			String word = tokenMatcher.group(1).replace("\\/", "/");
//			String lemma = tokenMatcher.group(2);
			String tag = tokenMatcher.group(3);
//			String phraseType = tokenMatcher.group(4); // not currently used. Doing anything with it would just mean a heavier object tree...
			String namedEntity = tokenMatcher.group(5);
			String supertag = tokenMatcher.group(6);
			tag = posTagHacks(word, tag);
			return new Token(null, null, index, CandcPtbPos.valueOfByLabel(tag), supertag, word, NamedEntityClass.valueOfByLabel(namedEntity));
		}
		else
			throw new IllegalStateException("Can't parse: "+tokenStr);
	}
	
	private static GrammaticalRelation toGr(String gr, Map<String, Token> tokens) {
		Matcher tokenMatcher = GR_REGEX.matcher(gr);
		if (!tokenMatcher.matches())
			throw new IllegalStateException("Cannot parse: "+gr);
		if (tokenMatcher.groupCount() != 5)
			throw new IllegalStateException("Can't parse: "+gr);
		
		String type = tokenMatcher.group(1);
		String subtypeStr = tokenMatcher.group(2);
		if (subtypeStr != null)
			subtypeStr = subtypeStr.replace("\\/", "/");
		String headStr = tokenMatcher.group(3).replace("\\/", "/");
		String dependentStr = tokenMatcher.group(4).replace("\\/", "/");
		if (dependentStr != null)
			dependentStr = dependentStr.replace("\\/", "/");
		String initialGrValue = tokenMatcher.group(5);
		
		Subtype subtype;
		if (subtypeStr == null || subtypeStr.equals("_")) {
			subtype = null;
		}
		else if (subtypeStr.matches("[^ _]+_[0-9]+")) {
			subtype = new TokenSubtype(tokens.get(subtypeStr));
		}
		else if (subtypeStr.matches("[a-z]+")) {
			subtype = new FlagSubtype(subtypeStr);
		}
		else {
			throw new IllegalStateException("Invalid subtype: "+subtypeStr);
		}
		
		// MASSIVE HACK TO WORK AROUND BUG IN C&C
		// TODO write log entry
		// Very occasionally (for 9 GRs in 50,000 SENTENCES - in CCGbank), C&C generates GRs in the form:
		// (poss x y)
		if (type.equals("poss")) {
			type = "ncmod";
			subtype = new FlagSubtype("poss");
		}
		
		return new GrammaticalRelation(GrammaticalRelation.GrType.valueOfByLabel(type), subtype, initialGrValue, tokens.get(headStr), tokens.get(dependentStr));
	}
	
	// C&C has several non-standard POS tags. Some of these seem reasonable, e.g. $ for currencies, but some just look like errors.
	// This function replaces the seemingly incorrect tags with valid PTB tags.
	private static String posTagHacks(String word, String pos) {
		if (pos.equals("SO"))
			return "RB";
		if (pos.equals("NP"))
			return "DT";
		if (pos.equals("SBAR"))
			return "LCB";
		if (pos.equals(",")) { // Tokens: 2, underwriters, Wa, an, section, ,
			if (word.equals("2"))
				return "CD";
			if (word.equals("underwriters"))
				return "NNS";
			if (word.equals("Wa"))
				return "NNP";
			if (word.equals("an"))
				return "DT";
			if (word.equals("section"))
				return "NN";
			// otherwise fall through - simple comma (or unknown)
		}
		return pos;
	}
}