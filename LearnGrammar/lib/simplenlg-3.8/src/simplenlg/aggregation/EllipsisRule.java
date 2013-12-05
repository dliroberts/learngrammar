package simplenlg.aggregation;

import simplenlg.realiser.SPhraseSpec;

/**
 * Abstract extension of {@link AggregationRule}, representing a procedure to
 * carry out clause-level ellipsis. Ellipsis involves the selective "deletion"
 * of components of a clause if certain conditions on the recoverability of the
 * deleted information apply.
 * 
 * @author agatt
 * 
 */
public abstract class EllipsisRule extends AggregationRule {

	/**
	 * Default (empty) constructor
	 */
	public EllipsisRule() {
		super();
	}

	/**
	 * Check that none of the sentences passed as arguments are passivised.
	 * 
	 * @param sentences
	 *            the sentences
	 * @return <code>true</code> if the sentences are all active
	 */
	protected boolean notPassive(SPhraseSpec... sentences) {
		boolean nopass = true;

		for (int i = 0; i < sentences.length && nopass; i++) {
			nopass = !sentences[i].isPassive();
		}

		return nopass;
	}
}
