package simplenlg.realiser.comparators;

import java.util.Arrays;
import java.util.List;

import simplenlg.features.Category;
import simplenlg.features.DiscourseFunction;

/**
 * This class defines the default orderings by category, function and baseform
 * for phrase comparators.
 * 
 * @author agatt
 * 
 */
public class OrderDefaults {

	/**
	 * This list specifies the linear order of phrases by category.
	 */
	public static List<Category> CATEGORY_ORDER = Arrays.asList(
			Category.CLAUSE, Category.NOUN, Category.PREPOSITION, Category.ADVERB);

	/**
	 * This list specifies the linear order of phrases by function
	 */
	public static List<DiscourseFunction> FUNCTION_ORDER = Arrays.asList(
			DiscourseFunction.PREDICATIVE_COMPLEMENT, DiscourseFunction.OBJECT,
			DiscourseFunction.INDIRECT_OBJECT, DiscourseFunction.PREP_OBJECT);

	/**
	 * This list specifies the linear order of phrases by head word. This is
	 * particularly useful for ordering prepositional phrases (e.g. specifying
	 * that from-phrases occur prior to to-phrases, etc).
	 */
	public static List<String> BASEFORM_ORDER = Arrays.asList("from", "to");

}
