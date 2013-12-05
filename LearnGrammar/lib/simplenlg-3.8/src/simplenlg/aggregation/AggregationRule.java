package simplenlg.aggregation;

import java.util.Collection;

import simplenlg.realiser.CoordinateSPhraseSpec;
import simplenlg.realiser.SPhraseSpec;

/**
 * This class represents an aggregation rule. All such rules need to implement
 * an {@link #apply(SPhraseSpec...)} which takes an arbitrary number of
 * {@link simplenlg.realiser.SPhraseSpec}s and perform some form of aggregation
 * on them, returning an <code>SPhraseSpec</code> as a result, or
 * <code>null</code> if the operation fails.
 * 
 * @author agatt
 * 
 */
public abstract class AggregationRule {

	/**
	 * The main method in this class. Performs aggregation on an arbitrary
	 * number of sentences. The suggested way to implement this method in is to:
	 * (a) only perform aggregation if it is possible to do so on <I>all</I> the
	 * sentences supplied as parameters; (b) in case
	 * <code>sentences.length == 1</code>, return <code>sentences[0]</code>; (c)
	 * return <code>null</code> if <code>sentences.length == 0</code> or the
	 * operation can't be performed on all the supplied sentences..
	 * 
	 * @param sentences
	 *            the sentences
	 * @return the resulting sentence, if successful; <code>null</code>
	 *         otherwise
	 */
	public abstract SPhraseSpec apply(SPhraseSpec... sentences);

	/**
	 * Performs the same operation as {@link #apply(SPhraseSpec...)}, on the
	 * coordinates making up a coordinate clause.
	 * 
	 * <P>
	 * <strong>Implementation note:</strong>: this method involves a call to
	 * {@link #apply(SPhraseSpec...)}, supplying the components of the
	 * coordinate clause; new extensions of this class need not implement this
	 * method.
	 * </P>
	 * 
	 * @param sentence
	 *            the coordinate clause
	 * @return the result, if {@link #apply(SPhraseSpec...)} is successful
	 */
	public SPhraseSpec apply(CoordinateSPhraseSpec sentence) {
		Collection<SPhraseSpec> coords = sentence.getCoordinates();
		return apply(coords.toArray(new SPhraseSpec[coords.size()]));
	}

}
