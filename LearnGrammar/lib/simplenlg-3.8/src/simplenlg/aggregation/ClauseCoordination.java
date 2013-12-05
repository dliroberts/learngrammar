package simplenlg.aggregation;

import simplenlg.features.DiscourseFunction;
import simplenlg.features.Tense;
import simplenlg.realiser.CoordinateVPPhraseSpec;
import simplenlg.realiser.Phrase;
import simplenlg.realiser.SPhraseSpec;
import simplenlg.realiser.VPPhraseSpec;

/**
 * Implementation of a clausal coordination rule. The rule performs the
 * following operations on sentences:
 * 
 * <OL>
 * <LI>If the sentences have the same subject, a new sentence is returned with
 * that subject, and the VP from the component sentences conjoined</LI>
 * <LI>If the sentences have the same VP, a new sentence is returned with that
 * VP, and the subjects from the component sentences conjoined</LI>
 * </OL>
 * 
 * <P>
 * These operations only apply to sentences whose front modifiers are identical,
 * that is, sentences where, for every pair <code>s1</code> and <code>s2</code>,
 * <code>s1.getFrontModifiers().equals(s2.getFrontModifiers())</code>.
 * 
 * @author agatt
 */
public class ClauseCoordination extends CoordinationRule {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SPhraseSpec apply(SPhraseSpec... sentences) {
		SPhraseSpec result = null;
		SPhraseSpec s1 = sentences[0];
		Tense t = s1.getTense();
		boolean perf = s1.isPerfect();
		boolean pass = s1.isPassive();
		boolean prog = s1.isProgressive();

		if (PhraseChecker.areNotCoordinate(sentences)) {

			if (PhraseChecker.sameSentences(sentences)) {
				result = sentences[0];

			} else if (PhraseChecker.sameFrontMods(sentences)
					&& PhraseChecker.sameSurfaceSubjects(sentences)) {

				if (!PhraseChecker.sameVPArgs(sentences)
						&& PhraseChecker.sameVPHead(sentences)
						&& PhraseChecker.sameVPModifiers(sentences)) {

					VPPhraseSpec vp = new VPPhraseSpec();
					vp.setHead(sentences[0].getHead());
					result = new SPhraseSpec();

					for (SPhraseSpec s : sentences) {
						for (Phrase comp : s.getComplements()) {
							DiscourseFunction function = comp.getDiscourseFunction();
							vp.addComplement(comp, function);
						}
					}

					result.setVerbPhrase(vp);

					for (Phrase pre : sentences[0].getPremodifiers()) {
						vp.addPremodifier(pre);
					}

					for (Phrase post : sentences[0].getPostmodifiers()) {
						vp.addPostmodifier(post);
					}

				} else if (PhraseChecker.areNotExistential(sentences)) {
					CoordinateVPPhraseSpec cvp = new CoordinateVPPhraseSpec();
					result = new SPhraseSpec();

					for (SPhraseSpec s : sentences) {
						cvp.addCoordinates(s.getVerbPhrase());
					}

					cvp.aggregateAuxiliary(true);
					result.setVerbPhrase(cvp);
				}

				for (Phrase subj : sentences[0].getSubjects()) {
					result.addSubject(subj);
				}

				for (Phrase front : sentences[0].getFrontModifiers()) {
					result.addFrontModifier(front);
				}

				if (sentences[0].hasCuePhrase()) {
					result.setCuePhrase(sentences[0].getCuePhrase());
				}

			} else if (PhraseChecker.sameVP(sentences)) {
				result = new SPhraseSpec();

				for (SPhraseSpec s : sentences) {
					for (Phrase f : s.getFrontModifiers()) {
						result.addFrontModifier(f);
					}

					for (Phrase subj : s.getSubjects()) {
						result.addSubject(subj);
					}
				}

				result.setVerbPhrase(sentences[0].getVerbPhrase());
			}

			// need to reset all VP features
			if (result != null) {
				result.setTense(t);
				result.setPassive(pass);
				result.setPerfect(perf);
				result.setProgressive(prog);
			}
		}

		return result;

	}
}
