package uk.ac.cam.dr369.learngrammar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.Pos;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.parsing.CandcSyntacticParser;
import uk.ac.cam.dr369.learngrammar.parsing.DependencyStructure;
import uk.ac.cam.dr369.learngrammar.parsing.RaspSyntacticParser;
import uk.ac.cam.dr369.learngrammar.parsing.SyntacticParser;
import uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser;
import uk.ac.cam.dr369.learngrammar.util.Utils;

import com.google.common.collect.Lists;


/**
 * Entry point into application.
 * 
 * @author duncan.roberts
 */
public class LearnGrammar {
	static final Logger LOGGER = LoggerFactory.getLogger(LearnGrammar.class);

	static final double SUPERVISED_MINIMUM_PERCENT_THRESHOLD = 0.5d;
	
	/** WordNet files location. */
	static final URL DICTIONARY_URL;
	/** where generated sentences are saved to. */
	static final File OUTPUT_DIR = new File("output");
	
	static {
		try { // TODO use .properties file for wordnet path
			DICTIONARY_URL = new URL("file", null, Utils.getProperty("wordnet.path"));
			WordnetSemanticAnalyser.getInstance(DICTIONARY_URL); // initialises singleton internally with dictionary
		} catch (MalformedURLException e) { // Once the file path is in the props file this becomes possible; handle gracefully
			throw new Error();
		}
	}
	
	static List<String> similarSentences = Lists.newArrayList();
	static List<String> wrongAnswers = Lists.newArrayList();
	
	public static void main(String[] args) throws Exception {
		SyntacticParser parser = Utils.getProperty("parser").equals("rasp") ?
				new RaspSyntacticParser() :
				new CandcSyntacticParser(Boolean.parseBoolean(Utils.getProperty("candc.webservice"))); // TODO ioc/dependency injection
		boolean supervised = Boolean.parseBoolean(Utils.getProperty("supervised"));
		boolean commonalityOnly = Boolean.parseBoolean(Utils.getProperty("commonality.only"));
		
		Collection<DependencyStructure> corpusDeps;
		if (parser instanceof CandcSyntacticParser) { // TODO abstract away dependency on C&C
			long start = new Date().getTime();
			CandcSyntacticParser ccp = (CandcSyntacticParser) parser;
			String corpus = Utils.getProperty("candc.corpus.file");
			String maxCorpusLines = Utils.getProperty("candc.corpus.lines");
			corpusDeps = ccp.loadCorpus(
					new File(
							ClassLoader.getSystemResource(corpus).toURI()
						), maxCorpusLines.equals("unlimited") ? null : Integer.parseInt(maxCorpusLines)
					);
			LOGGER.info("{} sentences loaded from corpus file {}.", corpusDeps.size(), corpus);
			long end = new Date().getTime();
			LOGGER.info("Took {}s to load corpus.", (int) ((end-start)/1000));
		}
		else {
			System.out.println("Type some sample sentences, one per line, and then 'done' or an empty line when finished.");
			corpusDeps = parser.toDependencyStructures(getSentencesFromUser());
		}
		
		DependencyStructure lastDs = null;
		Map<Pos, List<Token>> tagTokenMap = null;
		Map<GrammaticalRelation.GrType, List<GrammaticalRelation>> typeGrMap = null;
		if (!commonalityOnly) {
			tagTokenMap = new HashMap<Pos, List<Token>>();
			typeGrMap = new HashMap<GrammaticalRelation.GrType, List<GrammaticalRelation>>();
			populateMaps(corpusDeps, tagTokenMap, typeGrMap);
			
			if (LOGGER.isDebugEnabled()) {
				StringBuilder sb = new StringBuilder();
				sb.append("Corpus, grouped by tag:");
				sb.append(Utils.NEWLINE);
				for (Pos tag : tagTokenMap.keySet()) {
					sb.append(tag.getLabel());
					sb.append(": ");
					sb.append(Utils.formatForPrinting(tagTokenMap.get(tag), 5));
					sb.append(Utils.NEWLINE);
				}
				LOGGER.debug(sb.toString());
				sb = new StringBuilder();
				sb.append("Corpus, grouped by GR type:");
				for (GrammaticalRelation.GrType type : typeGrMap.keySet()) {
					sb.append(type.getLabel());
					sb.append(": ");
					sb.append(Utils.formatForPrinting(typeGrMap.get(type), 5));
					sb.append(Utils.NEWLINE);
				}
				LOGGER.debug(sb.toString());
			}
			
			System.out.println("Type sentence to mutate, and then...");
			System.out.println("\"!word>gr\" to view subsets of dependencies of the previous sentence (e.g. !are>ncsubj)");
			System.out.println("\"$word>gr\" to replace GRs (e.g. $are>ncsubj)");
			System.out.println("\"%tag1,tag2,...\" to replace words of the given tags (e.g. %NNS,NNP)");
			System.out.println("\"&word\" to view semantic information.");
			System.out.println("\"?common\" to look for common features within a set of example sentences.");
			System.out.println("\"exit\" to exit.");
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String sentence;
			if (commonalityOnly) {
				sentence = "?common"; // TODO refactor to make this not a dirty hacky mess
			}
			else {
				System.out.print("> ");
				sentence = br.readLine();
			}
			
			if (sentence.trim().isEmpty()) {
				continue;
			}
			else if (sentence.equals("?common")) {
				CommandHandler.findCommonality(parser, supervised, corpusDeps);
			}
			else if (sentence.equalsIgnoreCase("exit")) {
				System.exit(0);
			}
			else if (sentence.startsWith("!") && lastDs != null) {
				CommandHandler.getDependency(lastDs, sentence);
			}
			else if (sentence.startsWith("&") && lastDs != null) {
				CommandHandler.getSemanticInfo(lastDs, sentence);
			}
			else if (sentence.startsWith("$") && lastDs != null) {
				CommandHandler.replaceGr(lastDs, typeGrMap, sentence);
			}
			else if (sentence.startsWith("%") && lastDs != null) {
				CommandHandler.replacePos(lastDs, tagTokenMap, sentence);
			}
			else if (sentence.startsWith("?serialize ")) {
				CommandHandler.serialiseCorpus(corpusDeps, sentence);
			}
			else {
				lastDs = parseSentence(parser, sentence);
			}
		}
	}

	private static DependencyStructure parseSentence(SyntacticParser parser,
			String sentence) throws Exception {
		DependencyStructure lastDs;
		lastDs = parser.toDependencyStructure(sentence);
		if (lastDs != null)
			System.out.println(lastDs.toString());
		else
			System.out.println("No parse possible.");
		return lastDs;
	}

	static String getSentencesFromUser() throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		StringBuilder sentences = new StringBuilder();
	    String sentence;
	    while (true) {
			System.out.print("> ");
			sentence = br.readLine();
			if (sentence.startsWith("?") && similarSentences != null) { // Some nasty side-effect programming...
				String filename = sentence.substring(1);
				File correctFilename = new File(OUTPUT_DIR, filename+".correct.txt");
				File incorrectFilename = new File(OUTPUT_DIR, filename+".incorrect.txt");
				BufferedWriter correctOut = null;
				BufferedWriter incorrectOut = null;
				try {
					correctOut = new BufferedWriter(new FileWriter(correctFilename));
					for (String sent : similarSentences) {
						correctOut.write(sent);
						correctOut.write(Utils.NEWLINE);
					}
					incorrectOut = new BufferedWriter(new FileWriter(incorrectFilename));
					for (String sent : wrongAnswers) {
						incorrectOut.write(sent);
						incorrectOut.write(Utils.NEWLINE);
					}
					System.out.println("Successfully saved "+similarSentences.size()+" correct and "+wrongAnswers.size()+" incorrect sentences to "+
							correctFilename+" and "+incorrectFilename+" respectively.");
			    } catch (Exception e){ // Catch exception if any
			    	System.err.println("Error saving to disk: " + e.getMessage());
			    } finally {
			    	if (correctOut != null)
			    		correctOut.close();
			    	if (incorrectOut != null)
			    		incorrectOut.close();
			    }
			}
			else if (sentence.equalsIgnoreCase("done") || sentence.trim().length() == 0) {
				break;
			}
			else {
				sentences.append(sentence.trim());
				if (!sentence.matches(".*[.?!]$"))
					sentences.append(".");
				sentences.append('\n');
			}
		}
	    return sentences.toString();
	}
	
	private static void populateMaps(Collection<DependencyStructure> deps,
			Map<Pos, List<Token>> tagTokenMap, Map<GrammaticalRelation.GrType, List<GrammaticalRelation>> typeGrMap) {
		for (DependencyStructure ds : deps) {
			LOGGER.debug(ds.toString());
			for (Token token : ds.getTokens()) {
				List<Token> toks = tagTokenMap.get(token.pos());
				if (toks == null)
					tagTokenMap.put(token.pos(), toks = new ArrayList<Token>());
				toks.add(token);
			}
			for (GrammaticalRelation gr : ds.getGrs()) {
				List<GrammaticalRelation> grl = typeGrMap.get(gr.type());
				if (grl == null)
					typeGrMap.put(gr.type(), grl = new ArrayList<GrammaticalRelation>());
				grl.add(gr);
			}
		}
	}
}