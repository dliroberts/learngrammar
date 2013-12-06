package uk.ac.cam.dr369.learngrammar.commonality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.LearningTask;
import uk.ac.cam.dr369.learngrammar.model.Pos;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType;
import uk.ac.cam.dr369.learngrammar.parsing.DependencyStructure;
import uk.ac.cam.dr369.learngrammar.semantics.VerbFrame;
import uk.ac.cam.dr369.learngrammar.semantics.WordnetVerbFrame;
import uk.ac.cam.dr369.learngrammar.util.Utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

/**
 * Encapsulates what the example sentences have in common.
 * 
 * @author duncan.roberts
 */
public class Commonality {
	private static final double SEMICOHERENT_THRESHOLD = 0.95d;
	private static final double CORRECT_QUALITY_THRESHOLD = 0.68d;
	private static final double INCORRECT_QUALITY_THRESHOLD = 0.45d; // we need more incorrect answers, and are less fussy about them!
	
	// The weights in this table are crying out for optimisation, e.g. with a maximum entropy model
	private static final List<FeatureProfile> FEATURE_PROFILES = ImmutableList.of(
			//                 TOKEN_LEVEL_________ GR_LVL GENERAL____________________
			//                 lemmas POS    Suptag GrType rcurse base multi maxheight
//			new FeatureProfile(true,  true,  false, true,  false, 20,  10,   3),   // lemmas, POS and GRs. Scoring: lvl 2-3.
//			
//			new FeatureProfile(true,  false, false, true,  true,  16,  8,    5),   // lemmas and GRs. Scoring: 2-5.
//			new FeatureProfile(false, true,  false, true,  true,  14,  7,    5),   // POS and GRs (recursive). Scoring: 2-5.
//			new FeatureProfile(false, false, true,  true,  true,  15,  8,    5),   // Supertag and GRs. Scoring: 2-5.
//			new FeatureProfile(true,  true,  false, false, true,  1,   3,    3),   // POS and lemma (i.e. word, pretty much. Worthwhile?). 1,3.
//			
//			new FeatureProfile(true,  false, false, false, false, 2,   2,    1),   // lemmas only. 1.
//			new FeatureProfile(false, true,  false, false, true,  1,   1,    1),   // POS only (recursive). 1.
//			new FeatureProfile(false, false, true,  false, false, 1,   1,    1),   // Supertag only. 1.
//			new FeatureProfile(false, false, false, true,  true,  1,   1,    2));  // GRs only (recursive). 2.
			new FeatureProfile(true,  true,  false, true,  false, 100, 50,   3),   // lemmas, POS and GRs. Scoring: lvl 2-3.
			
			new FeatureProfile(true,  false, false, true,  true,  40,  22,   5),   // lemmas and GRs. Scoring: 2-5.
			new FeatureProfile(false, true,  false, true,  true,  14,  8,    5),   // POS and GRs (recursive). Scoring: 2-5.
			new FeatureProfile(false, false, true,  true,  true,  16,  10,   5),   // Supertag and GRs (recursive). Scoring: 2-5.
			
			new FeatureProfile(true,  true,  false, false, true,  12,  15,   3),   // POS and lemma (i.e. word, pretty much. Worthwhile?). 1,3.
			new FeatureProfile(true,  false, true,  false, true,  14,  18,   3),   // Supertag and lemma (recursive). 1,3.
			
			new FeatureProfile(true,  false, false, false, false, 5,   1,    1),   // lemmas only. 1.
			new FeatureProfile(false, true,  false, false, true,  1,   1,    1),   // POS only (recursive). 1.
			new FeatureProfile(false, false, true,  false, false, 2,   1,    1),   // Supertag only. 1.
			new FeatureProfile(false, false, false, true,  true,  1,   1,    2));  // GRs only (recursive). 2.
	
	private static final int VERB_FRAME_SCORE = 40; // given at most once per DependencyStructure.
	
	// these maps have a Boolean level, where:
	// Boolean.TRUE: this feature is in all examples, and in none of the counter-examples.
	// Boolean.FALSE: this feature is in none of the examples, and all the counter-examples.
	// TODO use something from Guava here? Table?
	private final Map<FeatureType,Map<Boolean,Map<VerbFrame, List<Token>>>> verbFrames;
	private final Map<FeatureType,Map<Boolean,Map<FeatureProfile,Map<Integer,Set<Twig>>>>> dependencyStructures;
	
	private final List<DependencyStructure> examples;
	private final List<DependencyStructure> counterExamples;
	
	public Commonality(List<DependencyStructure> examples, List<DependencyStructure> counterExamples) {
		verbFrames = new HashMap<FeatureType,Map<Boolean,Map<VerbFrame, List<Token>>>>();
		findCommonality(verbFrames, examples, counterExamples);
		dependencyStructures = new HashMap<FeatureType,Map<Boolean,Map<FeatureProfile,Map<Integer,Set<Twig>>>>>();
		findCommonality(dependencyStructures, examples, counterExamples, FEATURE_PROFILES);
		this.examples = examples;
		this.counterExamples = counterExamples;
	}
	private void findCommonality(
			Map<FeatureType,Map<Boolean,Map<FeatureProfile,Map<Integer,Set<Twig>>>>> features,
			List<DependencyStructure> examples, List<DependencyStructure> counterExamples, List<FeatureProfile> profiles) {
		Map<Boolean, Map<FeatureProfile, Map<Integer, Set<Twig>>>> weak = Utils.establishMap(features, FeatureType.WEAK);
		Map<Boolean, Map<FeatureProfile, Map<Integer, Set<Twig>>>> strong = Utils.establishMap(features, FeatureType.STRONG);
		for (FeatureProfile profile : profiles) {
			// Find positives: things in common in all examples that aren't in any counter-example.
			Map<Integer,Set<Twig>> structs = findStructCommonality(examples, counterExamples, profile, profile.maxHeight());
			Utils.establishMap(strong, true).put(profile, structs);
			
			// Find negatives: things that aren't in any example that are in all the counter-examples.
			structs = findStructCommonality(counterExamples, examples, profile, profile.maxHeight());
			Utils.establishMap(strong, false).put(profile, structs);
			
			// Find 'weak positives': things that're in all examples, disregarding those in the counter-examples.
			structs = findStructCommonality(examples, null, profile, profile.maxHeight());
			Utils.establishMap(weak, true).put(profile, structs);
		}
		prune();
	}
	private void prune() {
//		ImmutableMap<FeatureType, Map<Boolean, Map<FeatureProfile, Map<Integer, Set<DirectedPathDs>>>>> m1 = ImmutableMap.copyOf(dependencyStructures);
		Utils.pruneMultilevelMap(dependencyStructures);
//		ImmutableMap<FeatureType, Map<Boolean, Map<FeatureProfile, Map<Integer, Set<DirectedPathDs>>>>> m2 = ImmutableMap.copyOf(dependencyStructures);
//		MapDifference<FeatureType, Map<Boolean, Map<FeatureProfile, Map<Integer, Set<DirectedPathDs>>>>> m1m2diff = Maps.difference(m1, m2);
		
		pruneStructs();
		
		// Deletions may result in empty sets/maps; reprune.
		Utils.pruneMultilevelMap(dependencyStructures);
		
//		boolean positive = true;
//		FeatureType ft = FeatureType.STRONG;
//		FeatureProfile fp = FEATURE_PROFILES.get(2);
//		int level = 2;
//		Map<FeatureProfile, Map<Integer, Set<DirectedPathDs>>> ml = dependencyStructures.get(ft).get(positive);
//		Set<DirectedPathDs> commonStructs = dependencyStructures.get(ft).get(positive).get(fp).get(level);
//		pruneStruct(ft, positive, ml, fp, level, commonStructs);
	}
	private void pruneStructs() {
		for (FeatureType featureType : dependencyStructures.keySet()) {
			Map<Boolean, Map<FeatureProfile, Map<Integer, Set<Twig>>>> x = dependencyStructures.get(featureType);
			for (boolean positive : x.keySet()) {
				Map<FeatureProfile, Map<Integer, Set<Twig>>> multiLevelProfileCommonStructures =
					x.get(positive);
				
				for (FeatureProfile profile : multiLevelProfileCommonStructures.keySet()) {
					Map<Integer, Set<Twig>> multiLevelCommonStructures = multiLevelProfileCommonStructures.get(profile);
					// TODO why not have separate sets for each leaf token? would shrink these sets down...
					for (Integer level : multiLevelCommonStructures.keySet()) {
						Set<Twig> commonStructures = multiLevelCommonStructures.get(level);
						pruneStruct(featureType, positive, multiLevelProfileCommonStructures, profile, level, commonStructures);
					}
				}
			}
		}
	}
	private void pruneStruct(FeatureType featureType, boolean positive,
			Map<FeatureProfile, Map<Integer, Set<Twig>>> multiLevelProfileCommonStructures, FeatureProfile profile, Integer level,
			Set<Twig> commonStructures) {
		if (commonStructures.isEmpty())
			return;
		// 1. If it's in STRONG, it'll also redundantly be in WEAK.
		if (positive && featureType.equals(FeatureType.STRONG)) {
			dependencyStructures.get(FeatureType.WEAK).get(positive).get(profile).get(level).removeAll(commonStructures);
		}
		// 2. Get rid of equivalent structs belonging to profiles that have a strictly less demanding requirement set.
		//pruneSubsumed(multiLevelProfileCommonStructures, profile, level, commonStructures); // do not use - prunes nonredundant stuff
		// 3. Get rid of any struct that is less specific than some other struct (within the same profile).
		pruneStructSet(commonStructures, profile);
	}
//	/**
//	 * Get rid of structs belonging to profiles that have a strictly less demanding requirement set. E.g. if we match <eat|VBP> with a
//	 * 'lemma+POS' profile, kill off any instances of <eat> with 'lemma' profile, or <|VBP> with 'POS' profile.
//	 * @param multiLevelProfileCommonStructures
//	 * @param profile
//	 * @param level
//	 * @param commonStructures
//	 * @deprecated Such less demanding profile's structs aren't necessarily redundant and should not be pruned.
//	 */
//	private void pruneSubsumed(Map<FeatureProfile, Map<Integer, Set<Twig>>> multiLevelProfileCommonStructures, FeatureProfile profile,
//			Integer level, Set<Twig> commonStructures) {
//		for (FeatureProfile otherProfile : multiLevelProfileCommonStructures.keySet()) {
//			if (otherProfile != profile && profile.subsumes(otherProfile)) {
//				Set<Twig> otherProfStructs;
//				try {
//					// 1. See if an equivalent struct set exists for otherProfile - will (cleanly) crash out if not, saving further bother.
//					otherProfStructs = multiLevelProfileCommonStructures.get(otherProfile).get(level);
//				} catch (NullPointerException e) {
//					// Equivalent structure not in place for that feature profile. Not an error.
//					continue;
//				}
//				if (otherProfStructs == null)
//					continue;
//				// 2. Underspecify commonStructures to match otherProfile.
//				List<Twig> furtherUnderspecdStructs = Lists.newArrayList(commonStructures);
//				for (ListIterator<Twig> li = furtherUnderspecdStructs.listIterator(); li.hasNext();) {
//					Twig dpds = li.next();
//					List<GrammaticalRelation> grs = dpds.getGrs();
//					List<Token> toks = dpds.getTokens();
//					for (ListIterator<GrammaticalRelation> lig = grs.listIterator(); lig.hasNext();) {
//						GrammaticalRelation gr = lig.next();
//						lig.set(new GrammaticalRelation(
//								otherProfile.grTypes() ? gr.type() : null, gr.getSubtype(),
//								otherProfile.initialGrValues() ? gr.getInitialGrValue() : null, gr.getHead(), gr.getDependent())
//						);
//					}
//					
//					for (ListIterator<Token> lit = toks.listIterator(); lit.hasNext();) {
//						Token tok = lit.next();
//						Token newTok = new Token(otherProfile.lemmas() ? tok.getLemma() : null,
//								tok.getSuffix(), tok.getIndex(), otherProfile.pos() ? tok.pos() : null,
//										otherProfile.supertags() ? tok.getSupertag() : null, tok.getWord());
//						for (ListIterator<GrammaticalRelation> lig = grs.listIterator(); lig.hasNext();) {
//							GrammaticalRelation gr = lig.next();
//							boolean change = false;
//							Subtype st = gr.getSubtype();
//							Token hd = gr.getHead();
//							Token dp = gr.getDependent();
//							if (st != null && tok.equals(((TokenSubtype) st).token())) {
//								st = new TokenSubtype(newTok);
//								change = true;
//							}
//							else if (hd != null && tok.equals(hd)) {
//								hd = newTok;
//								change = true;
//							}
//							else if (dp != null && tok.equals(dp)) {
//								dp = newTok;
//								change = true;
//							}
//							
//							if (change) {
//								lig.set(new GrammaticalRelation(
//									otherProfile.grTypes() ? gr.type() : null, st,
//									otherProfile.initialGrValues() ? gr.getInitialGrValue() : null, hd, dp)
//								);
//							}
//						}
//						lit.set(newTok);
//					}
//					li.set(new Twig(grs, toks, true, otherProfile));
//				}
//				// 3. Remove from otherProfile set.
//				otherProfStructs.removeAll(furtherUnderspecdStructs);
//			}
//		}
//	}
	private void pruneStructSet(Set<Twig> commonStructures, FeatureProfile profile) {
		if (commonStructures.size() < 2 || !profile.recurseHierarchy())
			return;
		
		boolean pos = profile.pos();
		boolean grTypes = profile.grTypes();
		
		Twig dpds = commonStructures.iterator().next();
		int height = dpds.getGrs().size() + dpds.getTokens().size();
		// Table where...
		// Row = hash of all other nodes.
		// Column = variable (node or edge) in path to be excluded from hash. Aka 'exclusion index'.
		Table<Integer, Integer, List<Twig>> similarStructs = HashBasedTable.create();
		
		// Helper table to avoid recalculating partial hashes.
		// Row = exclusion index.
		// Column = struct.
		Table<Integer, DependencyStructure, Integer> partialHashes = HashBasedTable.create();
		
		// 1. Populate tables.
		for (Twig struct : commonStructures) {
			for (int exclusionIdx = 1; exclusionIdx <= height; exclusionIdx++) { // every struct is guaranteed to have the same height within a set.
				int partialHash = calcPartialHash(struct, exclusionIdx, height);
				Utils.establishListInTable(similarStructs, partialHash, exclusionIdx).add(struct);
				partialHashes.put(exclusionIdx, struct, partialHash);
			}
		}
		Set<Twig> forDeletion = Sets.newHashSet();
		// 2. Find structs similar to each other struct and mark for deletion.
		for (Twig struct : commonStructures) {
			Iterator<PathItem> itA = struct.iterator();
			
			for (int exclusionIdx = 1; exclusionIdx <= height; exclusionIdx++) {
				int partialHash = partialHashes.get(exclusionIdx, struct);
				List<Twig> verySimilarStructs = similarStructs.get(partialHash, exclusionIdx);
				GrammaticalRelation grA = null;
				GrType grTypeA = null;
				Pos posA = null;
				Token tokA = null;
				String lemmaA = null;
				String supertagA = null;
				if (exclusionIdx % 2 == 0) { // GR level
					grA = itA.next().gr();
					grTypeA = grA.type();
				}
				else { // token level
					tokA = itA.next().token();
					lemmaA = tokA.getLemma();
					supertagA = tokA.getSupertag();
					posA = tokA.pos();
				}
				
				for (Twig verySimilarStruct : verySimilarStructs) {
					if (verySimilarStruct == struct)
						continue;
					
					if (areHierarchiesPartiallyEqual(struct, verySimilarStruct, exclusionIdx, height)) {
						if (exclusionIdx % 2 == 0) { // GR level
							GrammaticalRelation grB = verySimilarStruct.get(exclusionIdx).gr(); // TODO use iterator; is currently inefficient.
							GrType grTypeB = grB.type();
							if (grTypes && grTypeA != null && grTypeB != null) {
								if (grTypeA.descendentOf(grTypeB)) {
									forDeletion.add(verySimilarStruct);
								}
								else if (grTypeA.ancestorOf(grTypeB)) {
									forDeletion.add(struct);
								}
							}
						}
						else { // token level
							Token tokB = verySimilarStruct.get(exclusionIdx).token(); // TODO use iterator; is currently inefficient.
							String lemmaB = tokB.getLemma();
							String supertagB = tokB.getSupertag();
							// Ensure last aspect of required equality holds...
							if (pos && (lemmaA == null ? lemmaB == null : lemmaA.equals(lemmaB)) &&
									(supertagA == null ? supertagB == null : supertagA.equals(supertagB))) {
								Pos posB = tokB.pos();
								if (posA.descendentOf(posB)) {
									forDeletion.add(verySimilarStruct);
								}
								else if (posA.ancestorOf(posB)) {
									forDeletion.add(struct);
								}
							}
						}
					}
				}
			}
		}
		// 3. Perform the deletion.
		commonStructures.removeAll(forDeletion);
	}
	/**
	 * Required for the one-in-a-million case of hashcode collisions.
	 */
	private boolean areHierarchiesPartiallyEqual(Twig a, Twig b, int exclusionIdx, int height) {
		Iterator<PathItem> aIt = a.iterator();
		Iterator<PathItem> bIt = b.iterator();
		for (int i = 1; i <= height; i++) {
			if (i % 2 == 0) { // GR level
				GrType grTypeA = aIt.next().gr().type();
				GrType grTypeB = bIt.next().gr().type();
				if (i != exclusionIdx && (grTypeA == null ? grTypeB != null : !grTypeA.equals(grTypeB)))
					return false;
			}
			else { // token level
				Pos posA = aIt.next().token().pos();
				Pos posB = bIt.next().token().pos();
				if (i != exclusionIdx && (posA == null ? posB != null : !posA.equals(posB)))
					return false;
			}
		}
		return true;
	}
	private static int calcPartialHash(Twig struct, int exclude, int height) {
//		Iterator<GrammaticalRelation> grIt = struct.getGrs().iterator();
//		Iterator<Token> tokIt = struct.getTokens().iterator();
		Iterator<PathItem> sIt = struct.iterator();
		final int prime = 31;
		int result = 1;
		for (int i = 1; i <= height; i++) {
			if (i % 2 == 0) { // GR level
				GrType grt = sIt.next().gr().type();
				if (i != exclude) {
					result = prime * result + (grt == null ? 1231 : grt.hashCode());
				}
			}
			else { // token level
				Pos pos = sIt.next().token().pos();
				if (i != exclude) {
					result = prime * result + (pos == null ? 1237 : pos.hashCode());
				}
			}
		}
		return result;
	}
	private void findCommonality(Map<FeatureType,Map<Boolean,Map<VerbFrame, List<Token>>>> features,
			List<DependencyStructure> examples, List<DependencyStructure> counterExamples) {
		Map<Boolean,Map<VerbFrame, List<Token>>> weak = Utils.establishMap(features, FeatureType.WEAK);
		Map<Boolean,Map<VerbFrame, List<Token>>> strong = Utils.establishMap(features, FeatureType.STRONG);
		
		// Find positives: things in common in all examples that aren't in any counter-example.
		Map<VerbFrame, List<Token>> verbFrames = new HashMap<VerbFrame, List<Token>>();
		findVerbFrameCommonality(examples, counterExamples, verbFrames);
		strong.put(true, verbFrames);
		
		// Find negatives: things that aren't in any example that are in all the counter-examples.
		verbFrames = new HashMap<VerbFrame, List<Token>>();
		findVerbFrameCommonality(counterExamples, examples, verbFrames);
		strong.put(false, verbFrames);
		
		// Find 'weak positives': things that're in all examples, disregarding those in the counter-examples.
		verbFrames = new HashMap<VerbFrame, List<Token>>();
		findVerbFrameCommonality(examples, null, verbFrames);
		weak.put(true, verbFrames);
	}
	private Map<Integer,Set<Twig>> findStructCommonality(
			List<DependencyStructure> examples, List<DependencyStructure> counterExamples,
			FeatureProfile profile, int maxHeight) {
		// Beyond top level (int key here represents depth of structure):
		// Key: candidate common structure; values: sentences that this structure was observed in
		Map<Integer,Map<Twig, Set<DependencyStructure>>> structsMultilevel =
			new HashMap<Integer,Map<Twig, Set<DependencyStructure>>>();
		
		// 1. Find features for each example.
		for (DependencyStructure example : examples) {
			
			for (Token tok : example.getTokens()) {
				List<Twig> d = Twig.getPartialStructures(tok, profile, profile.maxHeight());
				int i = maxHeight;
				while (true) {
					if (!d.isEmpty())
						Utils.putValueInSetForKeys(Utils.establishMap(structsMultilevel, i), d, example);
					
					i = lowerSalientStructureHeight(profile, i);
					
					if (i == 0)
						break;
					
					d = Twig.getPartialStructures(tok, profile, i);
				}
			}
		}
		// 2. Remove features that occur in a counter-example.
		if (counterExamples != null) {
			for (DependencyStructure counterExample : counterExamples) {
				for (Token tok : counterExample.getTokens()) {
					List<Twig> d = Twig.getPartialStructures(tok, profile, profile.maxHeight());
					int i = maxHeight;
					while (true) {
						Map<Twig, Set<DependencyStructure>> x = structsMultilevel.get(i);
						if (x != null) {
							for (DependencyStructure struct : d) {
								x.remove(struct);
							}
						}
						i = lowerSalientStructureHeight(profile, i);
						if (i == 0)
							break;
						d = Twig.getPartialStructures(tok, profile, i);
					}
				}
			}
		}
		// 3. Remove any features that don't occur in ALL examples (i.e. reduce to intersection/commonality).
		Collections.sort(examples);
		Map<Integer,Set<Twig>> out = new HashMap<Integer,Set<Twig>>();
		for (int i : structsMultilevel.keySet()) {
			Map<Twig, Set<DependencyStructure>> structs = structsMultilevel.get(i);
			out.put(i, structs.keySet());
			
			for (Iterator<Twig> it = structs.keySet().iterator(); it.hasNext();) {
				DependencyStructure commonStruct = it.next();
				List<DependencyStructure> l = new ArrayList<DependencyStructure>(structs.get(commonStruct)); // initially a set to remove duplicates
				Collections.sort(l);
				if (!l.equals(examples))
					it.remove();
			}
		}
		return out;
	}
	static int lowerSalientStructureHeight(FeatureProfile profile, int height) {
		boolean grs = profile.grTypes();
		boolean tokens = profile.lemmas() || profile.pos();
		int minIdx = grs && tokens ? 1 : 0; // if mixing GR and token features, having one without 'tother isn't worth points.
		while (--height > minIdx) {
			if (height % 2 == 1) { // odd; so token level
				if (tokens)
					return height;
			}
			else if (grs) // even; so GR level
				return height;
		}
		return 0;
	}
	private void findVerbFrameCommonality(List<DependencyStructure> examples, List<DependencyStructure> counterExamples,
			Map<VerbFrame, List<Token>> verbFrames) {
		examples = new ArrayList<DependencyStructure>(examples);
		Collections.sort(examples);
		
		Map<VerbFrame, Set<DependencyStructure>> verbFramesDsCoverage = new HashMap<VerbFrame, Set<DependencyStructure>>();
		
		for (DependencyStructure ds : examples) {
			for (Token t : ds.getTokens()) {
				if (t.isVerb()) {
					Set<VerbFrame> vfs = WordnetVerbFrame.getAcceptingFrames(t);
					for (VerbFrame vf : vfs) {
						Utils.establishList(verbFrames, vf).add(t);
						Utils.establishSet(verbFramesDsCoverage, vf).add(ds);
					}
				}
			}
		}
		if (counterExamples != null) {
			for (DependencyStructure ds : counterExamples) {
				for (Token t : ds.getTokens()) {
					if (t.isVerb()) {
						Set<VerbFrame> vfs = WordnetVerbFrame.getAcceptingFrames(t);
						for (VerbFrame vf : vfs) {
							verbFrames.remove(vf);
							verbFramesDsCoverage.remove(vf);
						}
					}
				}
			}
		}
		// Ensure that each feature is present in every example. If not, remove. This effectively gives us the INTERSECTION of example features,
		// rather than the UNION (which is what we'd have without this step).
		for (VerbFrame a : verbFramesDsCoverage.keySet()) {
			List<DependencyStructure> l = new ArrayList<DependencyStructure>(verbFramesDsCoverage.get(a)); // initially a set to remove duplicates
			Collections.sort(l);
			if (!l.equals(examples))
				verbFrames.remove(a);
		}
	}
	public void findCorpusMatches(Collection<DependencyStructure> corpusDeps,
			List<ScoredDependencyStructure> similarSentences, List<ScoredDependencyStructure> incorrectSentences) {
		similarSentences.clear();
		similarSentences.addAll(findSimilar(corpusDeps));
		Collections.sort(similarSentences);
		Collections.reverse(similarSentences);
		incorrectSentences.clear();
		incorrectSentences.addAll(findCoherentWrongAnswers(similarSentences));
		
		boolean fragments = false;
		for (DependencyStructure counterExample : counterExamples) {
			if (isFragment(counterExample, 2)) {
				fragments = true;
				break;
			}
		}
		if (!fragments) {
			for (ListIterator<ScoredDependencyStructure> it = incorrectSentences.listIterator(); it.hasNext();) {
				DependencyStructure ds = it.next();
				
//				Set<String> oddities = ImmutableSet.of("Sales increased 10% to $2.65 billion from $2.41 billion.",
//						"Ginnie Mae 13% securities were down about 1/4 at 109 30/32.");
//				
//				if (oddities.contains(ds.getSentence())) {
//					int i = 3; i++;
//				}
				
				if (isFragment(ds, 5))
					it.remove();
			}
		}
	}
	public void findCorpusMatchesAsStrings(Collection<DependencyStructure> corpusDeps, ScoreThresholder exampleThresholder,
			List<String> similarSentences, List<String> incorrectSentences) {
		List<ScoredDependencyStructure> similarSentencesSds = Lists.newArrayList();
		List<ScoredDependencyStructure> incorrectSentencesSds = Lists.newArrayList();
		findCorpusMatches(corpusDeps, similarSentencesSds, incorrectSentencesSds);
		
		exampleThresholder.apply(similarSentencesSds, similarSentences);
		for (DependencyStructure ds : examples) {
			similarSentences.add(ds.getSentence());
		}
		
		double topIncorrectScore = incorrectSentencesSds.get(0).score();
		double bottomScore = incorrectSentencesSds.get(incorrectSentencesSds.size()-1).score();
		double maxWrong = similarSentences.size() * LearningTask.INCORRECT_ANSWERS;
		incorrectSentences.clear();
		for (ScoredDependencyStructure sds : incorrectSentencesSds) {
			if ((sds.score() - bottomScore < (topIncorrectScore - bottomScore) * INCORRECT_QUALITY_THRESHOLD) || incorrectSentences.size() >= maxWrong)
				break;
			incorrectSentences.add(sds.getSentence() + "\t\t\t*" + sds.score() + "*" + sds.scores());
		}
	}
	public interface ScoreThresholder {
		void apply(List<ScoredDependencyStructure> similarSentencesSds, List<String> similarSentences);
	}
	public static class UnsupervisedScoreThresholder implements ScoreThresholder {
		public void apply(List<ScoredDependencyStructure> similarSentencesSds, List<String> similarSentences) {
			double topCorrectScore = similarSentencesSds.get(0).score(); // list is sorted
			similarSentences.clear();
			for (ScoredDependencyStructure sds : similarSentencesSds) {
				if (sds.score() < topCorrectScore * CORRECT_QUALITY_THRESHOLD)
					break;
				similarSentences.add(sds.getSentence() + "\t\t\t*" + sds.score() + "*" + sds.scores());
			}
		}
	}
	
	private List<ScoredDependencyStructure> findSimilar(Collection<DependencyStructure> corpusDeps) {
		// Get scores based off 'strong' features: intersection(examples) - union(ctrExamples)
		Map<DependencyStructure, List<Score>> similarSentences = findSimilarPrivt(
				corpusDeps, Utils.establishMap(verbFrames, FeatureType.STRONG),
				Utils.establishMap(dependencyStructures, FeatureType.STRONG), FeatureType.STRONG);
		// Get scores based off 'weak' features: intersection(examples)
		Map<DependencyStructure, List<Score>> similarSentencesWeaklyRated = findSimilarPrivt(
				corpusDeps, Utils.establishMap(verbFrames, FeatureType.WEAK),
				Utils.establishMap(dependencyStructures, FeatureType.WEAK), FeatureType.WEAK);
		
		List<ScoredDependencyStructure> scoredDs = new ArrayList<ScoredDependencyStructure>();
		Set<DependencyStructure> dses = Utils.union(similarSentences.keySet(), similarSentencesWeaklyRated.keySet());
		for (DependencyStructure ds : dses) {
			List<Score> scores = new ArrayList<Score>();
			List<Score> scores1 = similarSentences.get(ds);
			List<Score> scores2 = similarSentencesWeaklyRated.get(ds);
			if (scores1 != null)
				scores.addAll(scores1);
			if (scores2 != null)
				scores.addAll(scores2);
			// if I'm not going to use verbosity penalties any more, stop calculating them.
			scoredDs.add(new ScoredDependencyStructure(ds.getGrs(), ds.getTokens(), scores, 1d/*verbosityPenalty(ds, examples)*/));
		}
		return scoredDs;
	}
	private void findSimilarByStructure(Collection<DependencyStructure> corpusDeps, Map<DependencyStructure, List<Score>> similarSentences,
			Map<Boolean,Map<FeatureProfile,Map<Integer,Set<Twig>>>> commonMultiProfileLevelStructures, FeatureType featureType) {
		for (DependencyStructure candidateDs : corpusDeps) { // candidate from corpus
			
			// All structures observed in a sentence.
			Map<Integer,Set<Twig>> observed = new HashMap<Integer,Set<Twig>>();
			// All sentences that have been assigned a score.
			Map<Integer,Set<Twig>> scored = new HashMap<Integer,Set<Twig>>();
			
//			if (candidateDs.getSentence().equals("Become a Lobbyist")) {
//				int ie = 8; ie++;
//			}

			Map<FeatureProfile, Map<Integer, Set<Twig>>> commonMultiProfileLevelNegStructs =
				commonMultiProfileLevelStructures.get(false);
			Map<FeatureProfile, Map<Integer, Set<Twig>>> commonPosMultiProfileLevelStructs =
				commonMultiProfileLevelStructures.get(true);
			Set<FeatureProfile> profiles = Utils.unionKeys(commonPosMultiProfileLevelStructs, commonMultiProfileLevelNegStructs);
			
			Table<FeatureProfile, Integer, Map<Token, List<Twig>>> partialStructures = HashBasedTable.create();
			
			for (Token candidateToken : candidateDs.getTokens()) {
				
				// 1. Get partial structures required for checking both positive AND negative structs.
				
				for (FeatureProfile profile : profiles) {
					Set<Integer> indices = Sets.newHashSet();
					Map<Integer, Set<Twig>> a = commonPosMultiProfileLevelStructs == null ?
							null : commonPosMultiProfileLevelStructs.get(profile);
					Map<Integer, Set<Twig>> b = commonMultiProfileLevelNegStructs == null ? 
							null : commonMultiProfileLevelNegStructs.get(profile);
					if (a != null)
						indices.addAll(a.keySet());
					if (b != null)
						indices.addAll(b.keySet());
					for (int index : indices) {
						List<Twig> partialStructs = Twig.getPartialStructures(candidateToken, profile, index, false);
						Utils.establishMapInTable(partialStructures, profile, index).put(candidateToken, partialStructs);
						for (Twig struct : partialStructs) {
							Utils.establishSet(observed, index).add(struct);
						}
					}
				}
				
				// 2. Check positive structs.
				
				if (commonPosMultiProfileLevelStructs == null)
					continue;
				
				for (FeatureProfile profile : commonPosMultiProfileLevelStructs.keySet()) {
					Map<Integer,Set<Twig>> commonMultilevelStructs = commonPosMultiProfileLevelStructs.get(profile);
					
					for (int depth : commonMultilevelStructs.keySet()) { // GR hierarchy depth
						
//						if (candidateDs.getSentence().equals("I'm going to hold on.") &&
//								profile.toString().equals("lemmas, POS and GR types") &&
//								featureType.equals(FeatureType.STRONG) &&
//								depth == 3) {
//							int feaojfea = 3; feaojfea++;
//						}
						
						Set<Twig> commonStructures = commonMultilevelStructs.get(depth);
						if (commonStructures != null) {
							for (Twig candidateStructure : partialStructures.get(profile, depth).get(candidateToken)) {
								
								for (Twig commonStructure : commonStructures) {
									if (commonStructure.subsumes(candidateStructure) &&
											(!scored.containsKey(depth) || !scored.get(depth).contains(commonStructure))) {
										String desc = depth+"-level structure ("+profile.featureDescription()+")";
										Utils.establishList(similarSentences, candidateDs).add(
												new Score(profile.score(commonStructure), desc,
												commonStructure, candidateStructure, featureType));
										Utils.establishSet(scored, depth).add(commonStructure);
									}
								}
							}
						}
					}
				}
			}
			if (commonMultiProfileLevelNegStructs != null) {
				for (FeatureProfile profile : commonMultiProfileLevelNegStructs.keySet()) {
					Map<Integer, Set<Twig>> commonMultiLevelNegStructs = commonMultiProfileLevelNegStructs.get(profile);
					
				    for (int i : commonMultiLevelNegStructs.keySet()) {
				    	Set<Twig> commonNegStructs = commonMultiLevelNegStructs.get(i);
	                    
				    	negStructLoop:for (Twig commonNegStruct : commonNegStructs) {
	                    	Set<Twig> obsLvlI = observed.get(i);
	                    	
	                    	if (obsLvlI != null) {
					    		for (Twig observedStruct : obsLvlI) {
					    			if (commonNegStruct.subsumes(observedStruct)) {
				                        continue negStructLoop;
					    			}
					    		}
	                    	}
                        	Utils.establishSet(observed, i).add(commonNegStruct); // dirty hack to avoid scoring multiple times without extra list
                        	String desc = "Absence of "+i+"-level structure";
                            Utils.establishList(similarSentences, candidateDs).add(
                            		new Score(profile.score(commonNegStruct), desc, commonNegStruct, featureType));
	                    }
				    }
				}
			}
		}
	}
	private Map<DependencyStructure, List<Score>> findSimilarPrivt(Collection<DependencyStructure> corpusDeps,
			Map<Boolean,Map<VerbFrame, List<Token>>> verbFrames,
			Map<Boolean,Map<FeatureProfile,Map<Integer,Set<Twig>>>> commonMultiProfileLevelStructures,
			FeatureType featureType) {
		Map<DependencyStructure, List<Score>> similarSentences = new HashMap<DependencyStructure, List<Score>>();
		for (DependencyStructure candidateDs : corpusDeps) { // candidate from corpus
			
			// Duplicate matches are not counted - e.g. five instances of "somebody ___s" can only be counted once within a single sentence.
			Set<VerbFrame> vfScored = new HashSet<VerbFrame>();
			
			// Used for negative matches: list everything we observe, then check intersection of each category with common elements of that category.
			Set<VerbFrame> vfsObserved = new HashSet<VerbFrame>();
			
			for (Token candidateToken : candidateDs.getTokens()) {
				if (candidateToken.isVerb()) {
					Set<VerbFrame> candidateVerbFrames = WordnetVerbFrame.getAcceptingFrames(candidateToken);
					Set<VerbFrame> commonVerbFrames = verbFrames.get(true).keySet();
					Set<VerbFrame> intersection = Utils.intersection(candidateVerbFrames, commonVerbFrames);
					intersection.removeAll(vfScored);
					if (intersection.size() > 0) { // max one such score tallied up, even if multiple VFs match
						vfScored.addAll(intersection);
						Utils.establishList(similarSentences, candidateDs).add(new Score(VERB_FRAME_SCORE, "verb frames", intersection, featureType));
					}
					vfsObserved.addAll(candidateVerbFrames);
				}
			}
			if (verbFrames.get(false) != null) {
				for (VerbFrame negVf : verbFrames.get(false).keySet()) {
					if (!vfsObserved.contains(negVf)) {
						Utils.establishList(similarSentences, candidateDs).add(
								new Score(VERB_FRAME_SCORE, "Absence of verb frame", negVf, featureType));
						break; // scored a max of once per sentence
					}
				}
			}
		}
		findSimilarByStructure(corpusDeps, similarSentences, commonMultiProfileLevelStructures, featureType);
		return similarSentences;
	}

//	private double verbosityPenalty(DependencyStructure candidate, List<DependencyStructure> references) {
//		int sentenceLength = candidate.getTokens().size();
//		int diff = Integer.MAX_VALUE;
//		int refLength = Integer.MAX_VALUE;
//		for (DependencyStructure ds : references) {
//			int rl = ds.getTokens().size();
//			int d2 = Math.abs(sentenceLength - rl);
//			if (d2 < diff) {
//				diff = d2;
//				refLength = rl;
//			}
//		}
//		if (sentenceLength > refLength) // apply penalty
//			return Math.exp(1 - ((double) refLength) / ((double) sentenceLength));
//		return 1d;
//	}
	
	/** Input must be PRE-SORTED, with highest scorers FIRST. */
	private List<ScoredDependencyStructure> findCoherentWrongAnswers(List<ScoredDependencyStructure> sortedSentences) {
		List<ScoredDependencyStructure> out = new ArrayList<ScoredDependencyStructure>();
		
		// Identify small contributors to overall score - sentences with lots of these get high scores.
		Set<Score> acceptScores = new HashSet<Score>();
		// ...And exclude sentences with the 'big hitter' features.
		Set<Score> rejectScores = new HashSet<Score>();
		
		// Populate acceptScores and rejectScores.
		int maxIndex = sortedSentences.size() > 100 ? 10 : sortedSentences.size() / 5;
		for (ListIterator<ScoredDependencyStructure> it = sortedSentences.listIterator(); it.nextIndex() < maxIndex;) {
			ScoredDependencyStructure sentence = it.next();
			double totalScore = sentence.score();
			List<Score> sortedScores = new ArrayList<Score>(sentence.scores());
			Collections.sort(sortedScores);
			double scoreAccountedFor = 0d;
			double threshold = totalScore * SEMICOHERENT_THRESHOLD;
			for (Score score : sortedScores) {
				scoreAccountedFor += score.value();
				if (scoreAccountedFor < threshold)
					rejectScores.add(score);
				else
					acceptScores.add(score);
			}
		}
		acceptScores.removeAll(rejectScores); // eliminate overlap
		
		// Rank sentences according to these small contributions.
		for (ScoredDependencyStructure sentence : sortedSentences) {
			Set<Score> accepts = Utils.intersection(sentence.scores(), acceptScores);
			List<Score> rejects = Lists.newArrayList(Utils.intersection(sentence.scores(), rejectScores));
			for (ListIterator<Score> it = rejects.listIterator(); it.hasNext();) {
				Score score = it.next();
				it.set(new Score(-score.value(), score.description(), score.actual(), score.type())); // negate score
			}
			out.add(new ScoredDependencyStructure(sentence.getGrs(), sentence.getTokens(),
					new ArrayList<Score>(Utils.union(accepts, rejects)), sentence.getVerbosityPenalty()));
		}
		Collections.sort(out);
		Collections.reverse(out);
		return out;
	}
	
	private static final boolean isFragment(DependencyStructure ds, int minLength) {
		String sentence = ds.getSentence();
		List<Token> tokens = ds.getTokens();
		
		if (sentence.split(" ").length < minLength) // Very short sentences more likely to be fragments. Note we're splitting on the DEtokenised string.
			return true;
		if (sentence.substring(0, 1).matches("[^A-Z]")) // first word should start with initial capital
			return true;
		if (sentence.substring(sentence.length()-1).matches("[^.!?]")) // should end with sentence-terminal punctuation
			return true;
		
		boolean verbFound = false;
		boolean containsLowercaseInitial = false;
		int nFigures = 0;
		for (Token tok : tokens) {
			if (tok.getWord().matches(
					"([0-9]+/[0-9]+" + // fraction
					"|" +
					"yen" +
					"|" +
					"dollars" +
					"|" +
					"cents" +
					"|" +
					"francs" +
					"|" +
					"([A-Z]*[£$%]))")) { // stock quotes are syntactically boring.
				nFigures++;
			}
			else if (tok.isVerb()) {
				verbFound = true;
//				if (containsLowercaseInitial)
//					break;
			}
			if (tok.getWord().substring(0, 1).matches("[a-z]")) {
				containsLowercaseInitial = true;
//				if (verbFound)
//					break;
			}
		}
		if (!verbFound || !containsLowercaseInitial || nFigures > 1)
			return true;
		return false;
	}
	
	// Not actually needed/desirable: we're only comparing the 'expected' bit of each score, and so subsumption doesn't come into it as the
	// scores will have been pruned at this point.
//	private static List<Score> intersectSubsumingScores(Set<Score> toBePruneds, Set<Score> references) {
//		List<Score> out = new ArrayList<Score>(toBePruneds);
//		outer:for (ListIterator<Score> candIt = out.listIterator(); candIt.hasNext();) {
//			Score candidateScore = candIt.next();
//			Object candidateExpected = candidateScore.expected();
//			if (candidateExpected instanceof Twig) {
//				Twig candidateTwig = (Twig) candidateExpected;
//				inner:for (Score refScore : references) {
//					Object refExpected = refScore.expected();
//					if (refExpected instanceof Twig)
//						continue inner;
//					Twig refTwig = (Twig) refExpected;
//					if (refTwig.subsumes(candidateTwig))
//						continue outer;
//				}
//				candIt.remove();
//			}
//			else if (!references.contains(candidateExpected)) {
//				candIt.remove();
//			}
//		}
//		return out;
//	}
	
	public Map<FeatureType,Map<Boolean, Map<FeatureProfile, Map<Integer, Set<Twig>>>>> getDependencyStructures() {
		return dependencyStructures;
	}

	public Map<FeatureType,Map<Boolean,Map<VerbFrame, List<Token>>>> getVerbFrames() {
		return verbFrames;
	}

	public List<DependencyStructure> getExamples() {
		return examples;
	}

	public List<DependencyStructure> getCounterExamples() {
		return counterExamples;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((verbFrames == null) ? 0 : verbFrames.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Commonality other = (Commonality) obj;
		if (verbFrames == null) {
			if (other.verbFrames != null)
				return false;
		} else if (!verbFrames.equals(other.verbFrames))
			return false;
		return true;
	}

	public static enum FeatureType {
		STRONG(5), WEAK(2); // prune() will be faster if STRONG comes first, as it can remove items from WEAK.
		private final int multiplier;
		FeatureType(int multiplier) {
			this.multiplier = multiplier;
		}
		public int multiplier() {
			return multiplier;
		}
		public String toString() {
			return name().toLowerCase();
		}
	}
}