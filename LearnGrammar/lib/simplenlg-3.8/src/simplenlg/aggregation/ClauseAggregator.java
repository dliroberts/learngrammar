package simplenlg.aggregation;

import java.util.ArrayList;
import java.util.List;

import simplenlg.realiser.CoordinateSPhraseSpec;
import simplenlg.realiser.SPhraseSpec;

/**
 * This class implements a generic aggregation procedure for clauses. The idea
 * is to supply it in advance with an arbitrary number of
 * {@link AggregationRule}s. These are then applied in sequence to the
 * parameters supplied on invocation of {@link #apply(SPhraseSpec...)}. The
 * order of application is the order in which rules are supplied.
 * 
 * <P>
 * Note that rules can interact, in the sense that the application of one rule
 * can stop another rule from applying.
 * </P>
 * 
 * @see #addRule(AggregationRule)
 * 
 * @author agatt
 * 
 */
public class ClauseAggregator {
	private List<AggregationRule> _rules;

	/**
	 * Factory method to facilitate creation of a ClauseAggregator. The returned
	 * instance wraps a {@link ForwardConjunctionReduction} rule, a
	 * {@link BackwardConjunctionReduction} rule, and a
	 * {@link ClauseCoordination} rule, in that order.
	 * 
	 * @return the aggregator
	 */
	public static ClauseAggregator newInstance() {
		ClauseAggregator aggregator = new ClauseAggregator();
		aggregator.addRule(new ForwardConjunctionReduction());
		aggregator.addRule(new BackwardConjunctionReduction());
		aggregator.addRule(new ClauseCoordination());
		return aggregator;
	}

	/**
	 * Constructs a ClauseAggregator
	 */
	public ClauseAggregator() {
		this._rules = new ArrayList<AggregationRule>();
	}

	/**
	 * Add an aggregation rule to this aggregator: rules are applied in the
	 * order in which they are added.
	 * 
	 * @param rule
	 *            the ellipsis rule
	 */
	public void addRule(AggregationRule rule) {
		this._rules.add(rule);
	}

	/**
	 * 
	 * @return the rules supplied to this aggregator
	 */
	public List<AggregationRule> getRules() {
		return this._rules;
	}

	/**
	 * Apply aggregation and ellipsis to an arbitrary number of sentences. This
	 * method will test each rule in the order given, applying it to the
	 * parameters or the result of the last successful rule.
	 * 
	 * @param sentences
	 *            the sentences to aggregate
	 * @return the result of aggregation
	 */
	public SPhraseSpec apply(SPhraseSpec... sentences) {
		SPhraseSpec result = null;

		if (sentences.length > 1 && !this._rules.isEmpty()) {

			for (AggregationRule rule : this._rules) {
				if (result == null) {
					result = rule.apply(sentences);

				} else if (result instanceof CoordinateSPhraseSpec) {
					SPhraseSpec intermediateResult = rule
							.apply((CoordinateSPhraseSpec) result);

					if (intermediateResult != null
							&& intermediateResult instanceof CoordinateSPhraseSpec) {
						result = intermediateResult;
					}
				}
			}
		}

		return result;
	}

	public SPhraseSpec apply(List<SPhraseSpec> sentences) {
		return apply(sentences.toArray(new SPhraseSpec[sentences.size()]));
	}

}
