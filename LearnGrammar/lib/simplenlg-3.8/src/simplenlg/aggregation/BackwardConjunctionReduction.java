package simplenlg.aggregation;

import java.util.List;

import simplenlg.realiser.CoordinateSPhraseSpec;
import simplenlg.realiser.SPhraseSpec;

/**
 * Implementation of the backward conjunction reduction rule. Given two
 * sentences <code>s1</code> and <code>s2</code>, this rule elides any
 * constituent in the right periphery of <code>s1</code> which is
 * <I>form-identical</I> to a constituent with the same function in
 * <code>s2</code>, that is, the two constituents are essentially identical in
 * their final, realised, form.
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
public class BackwardConjunctionReduction extends EllipsisRule {

	/**
	 * Creates a new <code>BackwardConjunctionReduction</code>.
	 */
	public BackwardConjunctionReduction() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SPhraseSpec apply(SPhraseSpec... sentences) {
		boolean success = false;

		if (notPassive(sentences)) {
			List<PhraseSet> rightPeriphery = PhraseChecker
					.rightPeriphery(sentences);

			for (PhraseSet pair : rightPeriphery) {

				if (pair.lemmaIdentical()) {
					success = true;
					pair.elideLeftmost();
				}
			}
		}

		return success ? new CoordinateSPhraseSpec(sentences) : null;

	}

}
