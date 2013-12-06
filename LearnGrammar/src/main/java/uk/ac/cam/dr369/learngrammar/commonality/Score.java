/**
 * 
 */
package uk.ac.cam.dr369.learngrammar.commonality;

import java.text.NumberFormat;
import java.util.Locale;

import uk.ac.cam.dr369.learngrammar.commonality.Commonality.FeatureType;

/**
 * For every kind of commonality a sentence exhibits with the samples, it's allocated a score instance. Sentences with a high score total are displayed to the user.
 * Low-scoring sentences are also used as counter-examples.
 * 
 * @author duncan.roberts
 */
public class Score implements Comparable<Score> {
	private final String description;
	private final double value;
	private final Object expected;
	private final Object actual;
	private final FeatureType type;
	
	public Score(double value, String description, Object actual, FeatureType type) {
		this(value, description, null, actual, type);
	}
	public Score(double value, String description, Object expected, Object actual, FeatureType type) {
		this.value = value * type.multiplier();
		this.description = description;
		this.expected = expected;
		this.actual = actual;
		this.type = type;
	}
	public FeatureType type() {
		return type;
	}
	public Object actual() {
		return actual;
	}
	public Object expected() {
		return expected;
	}
	public double value() {
		return value;
	}
	public String description() {
		return description;
	}
	private static final NumberFormat DECIMAL_FORMAT = NumberFormat.getNumberInstance(); // I18n can come enseguida...
	@Override
	public String toString() {
		return /*"\n"+*/description+" ("
			+(expected == null ? "" : "expected: " + expected + "; actual: ")
			+(actual == null ? "" : actual + "; ")
			+"strength: "+type.toString().toLowerCase()+"; points: "+DECIMAL_FORMAT.format(value)+")";
	}
	@Override
	public int hashCode() { // Intentionally does not check 'actual', as this would break logic in Commonality.findCoherentWrongAnswers.
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((expected == null) ? 0 : expected.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		long temp;
		temp = Double.doubleToLongBits(value);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) { // Intentionally does not check 'actual', as this would break logic in Commonality.findCoherentWrongAnswers.
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Score other = (Score) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (expected == null) {
			if (other.expected != null)
				return false;
		} else if (!expected.equals(other.expected))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (Double.doubleToLongBits(value) != Double
				.doubleToLongBits(other.value))
			return false;
		return true;
	}
	
	public int compareTo(Score o) {
		double v = o.value - value;
		if (v > 0)
			return 1;
		else if (v < 0)
			return -1;
		return 0;
	}
}