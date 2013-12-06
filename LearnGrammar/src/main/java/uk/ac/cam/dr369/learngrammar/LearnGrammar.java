package uk.ac.cam.dr369.learngrammar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.dr369.learngrammar.commonality.Commonality;
import uk.ac.cam.dr369.learngrammar.commonality.FeatureProfile;
import uk.ac.cam.dr369.learngrammar.commonality.ScoredDependencyStructure;
import uk.ac.cam.dr369.learngrammar.commonality.Twig;
import uk.ac.cam.dr369.learngrammar.commonality.Commonality.FeatureType;
import uk.ac.cam.dr369.learngrammar.commonality.Commonality.ScoreThresholder;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.Pos;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType;
import uk.ac.cam.dr369.learngrammar.parsing.CandcSyntacticParser;
import uk.ac.cam.dr369.learngrammar.parsing.DependencyStructure;
import uk.ac.cam.dr369.learngrammar.parsing.RaspSyntacticParser;
import uk.ac.cam.dr369.learngrammar.parsing.SyntacticParser;
import uk.ac.cam.dr369.learngrammar.semantics.VerbFrame;
import uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser;
import uk.ac.cam.dr369.learngrammar.util.Utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.mit.jwi.item.IVerbFrame;
import edu.mit.jwi.item.IWord;

/**
 * Entry point into application.
 * 
 * @author duncan.roberts
 */
public class LearnGrammar {
	private static final Logger LOGGER = LoggerFactory.getLogger(LearnGrammar.class);

	private static final double SUPERVISED_MINIMUM_PERCENT_THRESHOLD = 0.5d;
	
	/** WordNet files location. */
	private static final URL DICTIONARY_URL;
	/** where generated sentences are saved to. */
	private static final File OUTPUT_DIR = new File("output");
	
	static {
		try { // TODO use .properties file for wordnet path
			DICTIONARY_URL = new URL("file", null, Utils.getProperty("wordnet.path"));
			WordnetSemanticAnalyser.getInstance(DICTIONARY_URL); // initialises singleton internally with dictionary
		} catch (MalformedURLException e) { // Once the file path is in the props file this becomes possible; handle gracefully
			throw new Error();
		}
	}
	
	private static List<String> similarSentences = Lists.newArrayList();
	private static List<String> wrongAnswers = Lists.newArrayList();
	
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
		
		Random random = new Random();
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
				System.out.println("\nProvide sentences that all illustrate some grammatical point, one per line, " +
						"and then 'done' or an empty line when finished.");
				if (similarSentences != null && !similarSentences.isEmpty())
					System.out.println("Alternatively, type ?<filename> to save previous results to "+OUTPUT_DIR+File.separator+
							"<filename>.correct and .incorrect.");
				
				String exStr = getSentencesFromUser();
				System.out.println("Now provide some counter-examples - sentences that do not illustrate the grammatical point.");
				String ctrExStr = getSentencesFromUser();
				List<DependencyStructure> examples = parser.toDependencyStructures(exStr);
				List<DependencyStructure> counterExamples = parser.toDependencyStructures(ctrExStr);
				
				long t1 = System.currentTimeMillis();
				Commonality commonality = new Commonality(examples, counterExamples);
				long t2 = System.currentTimeMillis();
				System.out.println("\nThe examples given have the following features in common:\n");
				
				for (FeatureType featureType : commonality.getDependencyStructures().keySet()) {
					Map<Boolean, Map<FeatureProfile, Map<Integer, Set<Twig>>>> x = commonality.getDependencyStructures().get(featureType);
					for (boolean positive : x.keySet()) {
						Map<FeatureProfile, Map<Integer, Set<Twig>>> multiLevelProfileCommonStructures =
							x.get(positive);
						
						for (FeatureProfile profile : multiLevelProfileCommonStructures.keySet()) {
							Map<Integer, Set<Twig>> multiLevelCommonStructures = multiLevelProfileCommonStructures.get(profile);
							
							for (Integer level : multiLevelCommonStructures.keySet()) {
								Set<Twig> commonStructures = multiLevelCommonStructures.get(level);
								if (!commonStructures.isEmpty()) {
									System.out.println((positive ? "Presence of " : "Absence of ")
										+profile.featureDescription()+", "+level+"-level ("+featureType+"): "
										+Utils.formatForPrinting(commonStructures));
								}
							}
						}
					}
				}
				Set<VerbFrame> strongVfs = commonality.getVerbFrames().get(FeatureType.STRONG).get(true).keySet();
				if (!strongVfs.isEmpty())
					System.out.println("Presence of verb frames: "+Utils.formatForPrinting(strongVfs));
				Set<VerbFrame> weakVfs = commonality.getVerbFrames().get(FeatureType.WEAK).get(true).keySet();
				if (!weakVfs.isEmpty())
					System.out.println("Presence of verb frames (weak): "+Utils.formatForPrinting(weakVfs));
				Set<VerbFrame> absenceOfVfs = commonality.getVerbFrames().get(FeatureType.STRONG).get(false).keySet();
				if (!absenceOfVfs.isEmpty())
					System.out.println("Absence of verb frames: "+Utils.formatForPrinting(absenceOfVfs));
				
				similarSentences = Lists.newArrayList();
				wrongAnswers = Lists.newArrayList();
				ScoreThresholder st = supervised ? new SupervisedScoreThresholder() : new Commonality.UnsupervisedScoreThresholder();
				commonality.findCorpusMatchesAsStrings(corpusDeps, st, similarSentences, wrongAnswers);
				long t3 = System.currentTimeMillis();
				LOGGER.info("Commonality calculation took {}s; corpus search took {}s.", ((t2-t1)/1000), ((t3-t2)/1000));
				if (similarSentences.size() > 0) {
					System.out.println("\nHopefully, the following sentence illustrates the same point as the examples...\n");
					int max = 10;
					for (String sentenceStr : similarSentences) {
						System.out.println(sentenceStr);
						
//						LOGGER.debug(ds.getSentence() + " - " + ds);
//						LOGGER.debug("(Sentence similarity score: "+ds.score()/*+". Verbosity penalty: "+ds.getVerbosityPenalty()*/
//								+". Matched on the basis of "+ds.scores()+")");
						if (max-- <= 0)
							break;
					}
					
					System.out.println("\nSome hopefully coherent wrong answers:\n");
					max = 10;
					for (String sentenceStr : wrongAnswers) {
						System.out.println(sentenceStr);
//						System.out.println(sentenceStr.getSentence() + " - " + sentenceStr);
//						System.out.println("(Sentence similarity score: "+sentenceStr.score()/*+". Verbosity penalty: "+ds.getVerbosityPenalty()*/
//								+". Matched on the basis of "+sentenceStr.scores()+")");
						if (max-- <= 0)
							break;
					}
				}
				else
					System.out.println("No similar sentences found.");
				LOGGER.info("Commonality analysis took {}s; search took {}s.", (t2-t1)/1000, (t3-t2)/1000);
			}
			else if (sentence.equalsIgnoreCase("exit")) {
				System.exit(0);
			}
			else if (sentence.startsWith("!") && lastDs != null) {
				String[] command = sentence.substring(1).split(">");
				String word = command[0];
				String gr = command[1];
				try {
					SortedSet<Token> toks = findDependencySubset(lastDs, word, gr);
					StringBuilder sb = new StringBuilder();
					for (Token t : toks) {
						sb.append(t.getWord());
						sb.append(' ');
					}
					System.out.println(Utils.detokenise(sb.toString().trim()));
				} catch (IllegalArgumentException e) {
					System.out.println("No matches.");
					continue;
				}
			}
			else if (sentence.startsWith("&") && lastDs != null) {
				String word = sentence.substring(1);
				WordnetSemanticAnalyser wsa = WordnetSemanticAnalyser.getInstance(DICTIONARY_URL);
				for (Token t : lastDs.getTokens()) {
					if (t.getWord().equals(word)) {
						if (t.isOpenClass()) {
							IWord wordSense = wsa.findSense(t, lastDs.getSentence());
							if (wordSense == null) {
								System.out.println("Could not match the usage of "+t.getLemma()+
								" in this sentence against a verb subcategorisation frame.");
								break;
							}
							String gloss = wordSense.getSynset().getGloss();
							System.out.println("Definition: "+gloss);
							System.out.print("Synonyms of "+wordSense.getLemma()+" (lemma of "+word+"): ");
							StringBuilder sb = new StringBuilder();
							List<IWord> synonyms = wsa.getSynonyms(t, wordSense);
							if (synonyms.size() == 0) {
								System.out.println("No synonyms.");
								break;
							}
							for (IWord syn : synonyms) {
								sb.append(syn.getLemma());
								sb.append(" (");
								sb.append(wsa.reinflect(t, syn).getWord());
								sb.append("), ");
							}
							String s = sb.toString();
							System.out.println(s.substring(0, s.length()-2));
							
							int idx = random.nextInt(synonyms.size());
							IWord syn = synonyms.get(idx);
							Token substitutionToken = wsa.reinflect(t, syn);
							sb = new StringBuilder(); 
							if (substitutionToken.isVerb()) {
								List<IVerbFrame> vfs = syn.getVerbFrames();
								for (IVerbFrame vf : vfs) {
									sb.append(vf.getTemplate());
									sb.append(", ");
								}
								s = sb.toString();
								System.out.println(s.substring(0, s.length()-2));
							}
							
							DependencyStructure modDs = lastDs.substitute(t, substitutionToken);
							System.out.println(modDs);
							System.out.println(modDs.getSentence().replace('_', ' ')); // FIXME hax - deal with multiword subtitutions properly (e.g. rerun through parser)
							break;
						}
						else {
							System.out.println(word+" is closed-class; cannot perform semantic analysis.");
						}
					}
				}
			}
			else if (sentence.startsWith("$") && lastDs != null) {
				String[] command = sentence.substring(1).split(">");
				String word = command[0];
				String gr = command[1];
				List<GrammaticalRelation> grInstancesFromCorpus = typeGrMap.get(GrType.valueOfByLabel(gr));
				if (grInstancesFromCorpus == null) {
					System.out.println("No target instances of '" + gr + "' to use as a substitute.");
					continue;
				}
				int idx = random.nextInt(grInstancesFromCorpus.size());
				GrammaticalRelation substitutionGr = grInstancesFromCorpus.get(idx);
				LOGGER.debug("Replacing with: " + substitutionGr);
				try {
					DependencyStructure ds = replaceStructure(lastDs, word, gr, substitutionGr);
					System.out.println(ds.getSentence());
					System.out.println(ds);
				} catch (IllegalArgumentException e) {
					System.out.println("No matches.");
					continue;
				}
			}
			else if (sentence.startsWith("%") && lastDs != null) {
				Set<String> tags = new HashSet<String>(Arrays.asList(sentence.substring(1).split(", ?")));
				LOGGER.debug("Tags: {}", tags);
				
				Map<Token,Token> toReplace = new HashMap<Token,Token>();
				for (Token token : lastDs.getTokens()) {
					Pos tag = token.pos();
					if (tags.contains(tag.getLabel())) { // substitute from 'corpus'
						List<Token> tagInstancesFromCorpus = tagTokenMap.get(tag);
						if (tagInstancesFromCorpus == null) {
							System.out.println("No target instances of '" + tag + "' to use as a substitute.");
							continue;
						}
						int idx = random.nextInt(tagInstancesFromCorpus.size());
						Token substitutionToken = tagInstancesFromCorpus.get(idx);
						toReplace.put(token, substitutionToken);
					}
				}
				DependencyStructure modDs = lastDs;
				for (Token from : toReplace.keySet()) {
					Token to = toReplace.get(from);
					modDs = modDs.substitute(from, to);
				}
				System.out.println(modDs.toString());
			}
			else if (sentence.startsWith("?serialize ")) {
				ObjectOutputStream out = null;
				try {
					File outFile = new File(sentence.split(" ")[1]);
					out = new ObjectOutputStream(new FileOutputStream(outFile));
					out.writeObject(corpusDeps);
					System.out.println("Serialised to "+outFile+". Size: "+outFile.length());
				}
				finally {
					if (out != null)
						out.close();
				}
			}
			else {
				lastDs = parser.toDependencyStructure(sentence);
				if (lastDs != null)
					System.out.println(lastDs.toString());
				else
					System.out.println("No parse possible.");
			}
		}
	}
	
	public static class SupervisedScoreThresholder implements ScoreThresholder {
		public void apply(List<ScoredDependencyStructure> sdses, List<String> out) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			
			// 1. Find unique scores.
			SortedMap<Double,String> uniqueScores = Maps.newTreeMap();
			double topScore = sdses.get(0).score(); // list is sorted
			for (ScoredDependencyStructure sds : sdses) {
				double score = sds.score();
				if (!uniqueScores.containsKey(score)) {
					uniqueScores.put(score, sds.getSentence());
					if (score < (topScore * SUPERVISED_MINIMUM_PERCENT_THRESHOLD)) {
						break;
					}
				}
			}
			
			// 2. Find threshold
			List<Double> scores = Lists.newArrayList(uniqueScores.keySet());
			Collections.reverse(scores);
			System.out.println("(Top score="+topScore+"; bottom score="+scores.get(scores.size()-1)+"; unique scores="+scores.size()+")");
			double threshold = topScore;
			for (int i = 0; i < 5; i++) {
				int nScores = scores.size();
				int thresholdIdx = nScores / 2;
				
				double candThreshold = scores.get(thresholdIdx);
				String sentence = uniqueScores.get(candThreshold);
				System.out.print("("+candThreshold+") ");
				System.out.println(sentence);
				System.out.print("Is this a good example? (Y/N) > ");
				boolean good;
				try {
					good = br.readLine().trim().toLowerCase().matches("y(es)?");
					if (good) { // trim everything from the first (top-scoring) element to the threshold (exclusive)
						scores.subList(0, thresholdIdx+1).clear();
						if (candThreshold < threshold) {
							threshold = candThreshold;
							System.out.println("Threshold now "+threshold+".");
						}
					}
					else { // trim everything from this (inclusive) to the last (lowest-scoring) index
						scores.subList(thresholdIdx, nScores).clear();
					}
					if (scores.size() == 1)
						break;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			System.out.println("Threshold set to "+threshold+".");
			
			// 3. Trim list down to only sentences with score above threshold
	//		for (ListIterator<ScoredDependencyStructure> li = sdses.listIterator(); li.hasNext();) {
	//			ScoredDependencyStructure sds = li.next();
	//			if (sds.score() < threshold) {
	//				break;
	//			}
	//		}
			out.clear();
			for (ScoredDependencyStructure sds : sdses) {
				if (sds.score() < threshold) {
					break;
				}
				out.add(sds.getSentence());
			}
		}
	}
	
	private static String getSentencesFromUser() throws Exception {
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
						correctOut.write('\n');
					}
					incorrectOut = new BufferedWriter(new FileWriter(incorrectFilename));
					for (String sent : wrongAnswers) {
						incorrectOut.write(sent);
						incorrectOut.write('\n');
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
	
	private static SortedSet<Token> findDependencySubset(DependencyStructure ds, String word, String gr) {
		for (Token tok : ds.getTokens()) {
			if (tok.getWord().equals(word)) {
				for (GrammaticalRelation grel : tok.getGrs()) {
					if (grel.type().getLabel().equals(gr)) {
						return grel.getDependentTokens(true);
					}
				}
			}
		}
		throw new IllegalArgumentException("Unable to find " + word + ">" + gr + " for: " + ds);
	}
	
	private static DependencyStructure replaceStructure(DependencyStructure ds, String word, String gr, GrammaticalRelation toGr) {
		for (Token tok : ds.getTokens()) {
			if (tok.getWord().equals(word)) {
				for (GrammaticalRelation fromGr : tok.getGrs()) {
					if (fromGr.type().getLabel().equals(gr)) {
						return ds.substitute(fromGr, toGr);
					}
				}
			}
		}
		throw new IllegalArgumentException("Unable to find " + word + ">" + gr + " for: " + ds);
	}
}