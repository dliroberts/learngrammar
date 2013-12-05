package simplenlg.aggregation;

import java.util.ArrayList;
import java.util.List;

import simplenlg.features.DiscourseFunction;
import simplenlg.lexicon.lexicalitems.Verb;
import simplenlg.realiser.SPhraseSpec;
import simplenlg.realiser.VPPhraseSpec;

/**
 * This class contains a number of utility methods for checking and collecting
 * sentence components during the process of aggregation.
 * 
 * @author agatt
 * 
 */
public abstract class PhraseChecker {

	/**
	 * Check that the sentences supplied are identical
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if for every pair of sentences <code>s1</code>
	 *         and <code>s2</code>, <code>s1.equals(s2)</code>.
	 */
	public static boolean sameSentences(SPhraseSpec... sentences) {
		boolean equal = false;

		if (sentences.length >= 2) {
			for (int i = 1; i < sentences.length; i++) {
				equal = sentences[i - 1].equals(sentences[i]);
			}
		}

		return equal;
	}

	/**
	 * Check that none of the sentences in the parameter list is a coordinate
	 * sentence
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if no sentences in the list is a
	 *         CoordinateSPhraseSpec
	 */
	public static boolean areNotCoordinate(SPhraseSpec... sentences) {
		boolean notCoord = true;

		for (int i = 0; i < sentences.length; i++) {
			notCoord = !sentences[i].isCoordinate();
		}

		return notCoord;
	}

	/**
	 * Check that the sentences supplied have identical front modifiers
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if for every pair of sentences <code>s1</code>
	 *         and <code>s2</code>,
	 *         <code>s1.getFrontModifiers().equals(s2.getFrontModifiers())</code>
	 *         .
	 */
	public static boolean sameFrontMods(SPhraseSpec... sentences) {
		boolean equal = true;

		if (sentences.length >= 2) {
			for (int i = 1; i < sentences.length && equal; i++) {

				if (!sentences[i - 1].hasCuePhrase() && !sentences[i].hasCuePhrase()) {
					equal = sentences[i - 1].getFrontModifiers().equals(
							sentences[i].getFrontModifiers());
					
				} else if (sentences[i - 1].hasCuePhrase() && sentences[i].hasCuePhrase()) {
					equal = sentences[i - 1].getFrontModifiers().equals(
							sentences[i].getFrontModifiers())
							&& sentences[i].getCuePhrase().equals(
									sentences[i - 1].getCuePhrase());
				
				} else {
					equal = false;
				}			
			}
		}

		return equal;
	}

	/**
	 * Check that the sentences supplied have identical subjects
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if for every pair of sentences <code>s1</code>
	 *         and <code>s2</code>
	 *         <code>s1.getSubjects().equals(s2.getSubjects())</code>.
	 */
	public static boolean sameSubjects(SPhraseSpec... sentences) {
		boolean equal = sentences.length >= 2;

		for (int i = 1; i < sentences.length && equal; i++) {
			equal = sentences[i - 1].getSubjects().equals(
					sentences[i].getSubjects());
		}

		return equal;
	}

	/**
	 * Check that the sentences have the same complemts raised to subject
	 * position in the passive
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if the passive raising complements are the same
	 */
	public static boolean samePassiveRaisingSubjects(SPhraseSpec... sentences) {
		boolean samePassiveSubjects = sentences.length >= 2;

		for (int i = 1; i < sentences.length && samePassiveSubjects; i++) {
			VPPhraseSpec vp1 = sentences[i - 1].getVerbPhrase();
			VPPhraseSpec vp2 = sentences[i].getVerbPhrase();
			samePassiveSubjects = vp1.getPassiveRaisingComplements().equals(
					vp2.getPassiveRaisingComplements());
		}

		return samePassiveSubjects;
	}

	/**
	 * Check whether all sentences are passive
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if for every sentence <code>s</code>,
	 *         <code>s.isPassive() == true</code>.
	 */
	public static boolean allPassive(SPhraseSpec... sentences) {
		boolean passive = true;

		for (int i = 0; i < sentences.length && passive; i++) {
			passive = sentences[i].isPassive();
		}

		return passive;
	}

	/**
	 * Check whether all sentences are active
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if for every sentence <code>s</code>,
	 *         <code>s.isPassive() == false</code>.
	 */
	public static boolean allActive(SPhraseSpec... sentences) {
		boolean active = true;

		for (int i = 0; i < sentences.length && active; i++) {
			active = !sentences[i].isPassive();
		}

		return active;
	}

	/**
	 * Check whether the sentences have the same <I>surface</I> subjects, that
	 * is, they are either all active and have the same subjects, or all passive
	 * and have the same passive raising subjects.
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if the sentences have the same surface subjects
	 */
	public static boolean sameSurfaceSubjects(SPhraseSpec... sentences) {
		return PhraseChecker.allActive(sentences)
				&& PhraseChecker.sameSubjects(sentences)
				|| PhraseChecker.allPassive(sentences)
				&& PhraseChecker.samePassiveRaisingSubjects(sentences);
	}

	/**
	 * Check that a list of sentences have the same verb
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if for every pair of sentences <code>s1</code>
	 *         and <code>s2</code>
	 *         <code>s1.getVerbPhrase().getHead().equals(s2.getVerbPhrase().getHead())</code>
	 */
	public static boolean sameVPHead(SPhraseSpec... sentences) {
		boolean equal = sentences.length >= 2;

		for (int i = 1; i < sentences.length && equal; i++) {
			VPPhraseSpec vp1 = sentences[i - 1].getVerbPhrase();
			VPPhraseSpec vp2 = sentences[i].getVerbPhrase();

			if (vp1 != null && vp2 != null) {
				Verb h1 = vp1.getHead();
				Verb h2 = vp2.getHead();
				equal = h1 != null && h2 != null ? h1.equals(h2) : false;
			} else {
				equal = false;
			}
		}

		return equal;
	}

	/**
	 * Check that the sentences supplied are either all active or all passive.
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if the sentences have the same voice
	 */
	public static boolean haveSameVoice(SPhraseSpec... sentences) {
		boolean samePassive = true;
		boolean prevIsPassive = false;

		if (sentences.length > 1) {
			prevIsPassive = sentences[0].isPassive();

			for (int i = 1; i < sentences.length && samePassive; i++) {
				samePassive = sentences[i].isPassive() == prevIsPassive;
			}
		}

		return samePassive;
	}

	/**
	 * Check that the sentences supplied are not existential sentences (i.e. of
	 * the form <I>there be...</I>)
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if none of the sentences is existential
	 */
	public static boolean areNotExistential(SPhraseSpec... sentences) {
		boolean notex = true;

		for (int i = 0; i < sentences.length && notex; i++) {
			notex = !sentences[i].isExistential();
		}

		return notex;
	}

	/**
	 * Check that the sentences supplied have identical verb phrases
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if for every pair of sentences <code>s1</code>
	 *         and <code>s2</code>,
	 *         <code>s1.getVerbPhrase().equals(s2.getVerbPhrase())</code>.
	 */
	public static boolean sameVP(SPhraseSpec... sentences) {
		boolean equal = sentences.length >= 2;

		for (int i = 1; i < sentences.length && equal; i++) {
			equal = sentences[i - 1].getVerbPhrase().equals(
					sentences[i].getVerbPhrase());
		}

		return equal;
	}

	/**
	 * Check that the sentences supplied have the same complements at VP level.
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if for every pair of sentences <code>s1</code>
	 *         and <code>s2</code>, their VPs have the same pre- and
	 *         post-modifiers and complements.
	 */
	public static boolean sameVPArgs(SPhraseSpec... sentences) {
		boolean equal = sentences.length >= 2;

		for (int i = 1; i < sentences.length && equal; i++) {
			VPPhraseSpec vp1 = sentences[i - 1].getVerbPhrase();
			VPPhraseSpec vp2 = sentences[i].getVerbPhrase();

			equal = vp1.getComplements().equals(vp2.getComplements());
		}

		return equal;
	}

	public static boolean sameVPModifiers(SPhraseSpec... sentences) {
		boolean equal = sentences.length >= 2;

		for (int i = 1; i < sentences.length && equal; i++) {
			VPPhraseSpec vp1 = sentences[i - 1].getVerbPhrase();
			VPPhraseSpec vp2 = sentences[i].getVerbPhrase();

			equal = vp1.getPostmodifiers().equals(vp2.getPostmodifiers())
					&& vp1.getPremodifiers().equals(vp2.getPremodifiers())
					&& vp1.getAttributivePostmodifiers().equals(
							vp2.getAttributivePostmodifiers());
		}

		return equal;
	}

	/**
	 * Collect a list of pairs of constituents with the same syntactic function
	 * from the left periphery of two sentences. The left periphery encompasses
	 * the subjects, front modifiers and cue phrases of the sentences.
	 * 
	 * @param sentences
	 *            the list of sentences
	 * @return a list of pairs of constituents with the same function, if any
	 *         are found
	 */
	public static List<PhraseSet> leftPeriphery(SPhraseSpec... sentences) {
		List<PhraseSet> funcsets = new ArrayList<PhraseSet>();
		PhraseSet cue = new PhraseSet(DiscourseFunction.CUE_PHRASE);
		PhraseSet front = new PhraseSet(DiscourseFunction.FRONT_MODIFIER);
		PhraseSet subj = new PhraseSet(DiscourseFunction.SUBJECT);

		for (SPhraseSpec s : sentences) {
			if (s.hasCuePhrase()) {
				cue.addPhrase(s.getCuePhrase());
			}

			if (s.hasFrontModifiers()) {
				front.addPhrases(s.getFrontModifiers());
			}

			if (s.hasSubject()) {
				subj.addPhrases(s.getSubjects());
			}
		}

		funcsets.add(cue);
		funcsets.add(front);
		funcsets.add(subj);
		return funcsets;
	}

	/**
	 * Collect a list of pairs of constituents with the same syntactic function
	 * from the right periphery of two sentences. The right periphery
	 * encompasses the complements of the main verb, and its postmodifiers.
	 * 
	 * @param sentences
	 *            the list of sentences
	 * @return a list of pairs of constituents with the same function, if any
	 *         are found
	 */
	public static List<PhraseSet> rightPeriphery(SPhraseSpec... sentences) {
		List<PhraseSet> funcsets = new ArrayList<PhraseSet>();
		PhraseSet comps = new PhraseSet(DiscourseFunction.OBJECT);
		new PhraseSet(DiscourseFunction.INDIRECT_OBJECT);
		PhraseSet pmods = new PhraseSet(DiscourseFunction.POSTMODIFIER);

		for (SPhraseSpec s : sentences) {

			if (s.hasComplements()) {
				comps.addPhrases(s.getComplements());
			}

			if (s.hasPostmodifiers()) {
				pmods.addPhrases(s.getPostmodifiers());
			}
		}

		funcsets.add(comps);
		funcsets.add(pmods);
		return funcsets;
	}

}
