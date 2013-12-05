package simplenlg.lexicon.lexicalitems;

import java.util.Arrays;
import java.util.List;

import simplenlg.features.Category;
import simplenlg.features.Feature;

/**
 * Enumeration of possible derivational relations that can exist between lexical
 * items, such as nominalisation. Since this enum implements
 * {@link simplenlg.features.Feature}, every relation can be queried for the
 * categories it applies to. For example, a <I>nominalisation</I> relation only
 * applies to adjectives or verbs.
 * <P>
 * 
 * Every derivational relation can additionally be queried for the inverse
 * relation. For example, the inverse of a {@link #NOMINALISATION} relation is
 * {@link #NOMINALISES}. Whereas the former expresses the relation between an
 * adjective/verb and the noun that nominalises it, the latter expresses the
 * inverse relation between the noun and the adjective/verb.
 * 
 * @author agatt
 * @since version 3.8
 * 
 */
public enum DerivationalRelation implements Feature {

	/**
	 * The relation that holds between a lexical item (adjective or verb) and
	 * the noun that nominalises it.
	 */
	NOMINALISATION(Category.ADJECTIVE, Category.VERB),

	/**
	 * The inverse of {@link #NOMINALISATION}
	 */
	NOMINALISES(Category.NOUN);

	private List<Category> categories;

	DerivationalRelation(Category... categories) {
		this.categories = Arrays.asList(categories);
	}

	public boolean appliesTo(Category cat) {
		return this.categories.contains(cat);
	}

	public DerivationalRelation getInverse() {
		DerivationalRelation inverse = null;

		switch (this) {
		case NOMINALISATION:
			inverse = NOMINALISES;
			break;
		case NOMINALISES:
			inverse = NOMINALISATION;
			break;
		}

		return inverse;
	}

}
