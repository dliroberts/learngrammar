package simplenlg.aggregation;

/**
 * Abstract extension of {@link AggregationRule}, representing a procedure to
 * carry out conjunction, by reducing. Unlike ellipsis rules, coordination
 * simply checks sentences for identity of certain components (such as subjects
 * or VPs), and constructs a sentences in which the identical component(s) are
 * represented once, and the non-identical components are conjoined.
 * 
 * @author agatt
 * 
 */
public abstract class CoordinationRule extends AggregationRule {

	/**
	 * Default (empty) constructor
	 */
	public CoordinationRule() {
		super();
	}

}
