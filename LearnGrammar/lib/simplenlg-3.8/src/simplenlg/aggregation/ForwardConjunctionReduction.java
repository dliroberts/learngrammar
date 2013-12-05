package simplenlg.aggregation;

import java.util.List;

import simplenlg.realiser.CoordinateSPhraseSpec;
import simplenlg.realiser.SPhraseSpec;

/**
 * <P>
 * Implementation of the forward conjunction rule. Given two sentences
 * <code>s1</code> and <code>s2</code>, this rule elides any constituent in the
 * left periphery of <code>s2</code> which is also in <code>s1</code>. The left
 * periphery in simplenlg is roughly defined as the subjects, front modifiers
 * and cue phrase of an {@link simplenlg.realiser.SPhraseSpec}.
 * </P>
 * 
 * <P>
 * This rule elides any constituent in the left periphery of <code>s2</code>
 * which is <I>lemma-identical</I> to a constituent with the same function in
 * <code>s1</code>, that is, it is headed by the same lexical item, though
 * possibly with different inflectional features and/or modifiers. Note that
 * this means that <I>the tall man</I> and <I>the man</I> are counted as
 * "identical" for the purposes of this rule. This is only justifiable insofar
 * as the two NPs are co-referential. Since SimpleNLG does not make assumptions
 * about the referentiality (or any part of the semantics) of phrases, it is up
 * to the developer to ensure that this is always the case.
 * </P>
 * 
 * <P>
 * The current implementation is loosely based on the algorithm in Harbusch and
 * Kempen (2009), which is described here:
 * 
 * <a href="http://aclweb.org/anthology-new/W/W09/W09-0624.pdf">
 * http://aclweb.org/anthology-new/W/W09/W09-0624.pdf</a>
 * </P>
 * 
 * <P>
 * <strong>Implementation note:</strong> The current implementation only applies
 * ellipsis to phrasal constituents (i.e. not to their component lexical items).
 * </P>
 * 
 * @author agatt
 * 
 */
public class ForwardConjunctionReduction extends EllipsisRule {

	/**
	 * Creates a new <code>ForwardConjunctionReduction</code>.
	 */
	public ForwardConjunctionReduction() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SPhraseSpec apply(SPhraseSpec... sentences) {
		boolean success = false;

		if (notPassive(sentences)) {
			List<PhraseSet> leftPeriphery = PhraseChecker
					.leftPeriphery(sentences);

			for (PhraseSet pair : leftPeriphery) {

				if (pair.formIdentical()) {
					success = true;
					pair.elideRightmost();
				}
			}
		}

		return success ? new CoordinateSPhraseSpec(sentences) : null;
	}

}
