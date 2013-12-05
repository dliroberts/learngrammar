package simplenlg.realiser.comparators;

import java.util.Comparator;
import java.util.List;

import simplenlg.features.Category;
import simplenlg.features.DiscourseFunction;
import simplenlg.lexicon.lexicalitems.LexicalItem;
import simplenlg.realiser.HeadedPhraseSpec;
import simplenlg.realiser.Phrase;

/**
 * This class implements a comparator for phrases. These comparators are used to
 * determine the order of pre- and post modifiers, as well as complements.
 * Comparison is done by specifying an ordering of phrases by category, by
 * function, and by baseform. In comparing two phrases, these lists are
 * consulted in the following order until a non-zero comparison value is
 * obtained: (a) category; (b) function; (c) baseform. In each case, if the
 * relevant item is not in the list, then a zero value is assumed.
 * 
 * @author agatt
 * 
 */
public class PhraseComparator implements Comparator<Phrase> {

	/**
	 * Convenience method, which creates a new instance of a phrase comparator
	 * using the predefined orderings in {@link OrderDefaults}.
	 * 
	 * @return the created <code>PhraseComparator</code>.
	 */
	public static PhraseComparator defaultInstance() {
		return new PhraseComparator(OrderDefaults.CATEGORY_ORDER,
				OrderDefaults.FUNCTION_ORDER, OrderDefaults.BASEFORM_ORDER);
	}

	protected List<Category> _catOrder;
	protected List<DiscourseFunction> _funcOrder;
	protected List<String> _baseOrder;

	/**
	 * Construct a new PhraseComparatior with the relevant lists.
	 * 
	 * @param catOrder
	 *            the order of phrases by category
	 * @param functionOrder
	 *            the order of phrases by function
	 * @param baseformOrder
	 *            the order of phrases by baseform
	 */
	public PhraseComparator(List<Category> catOrder,
			List<DiscourseFunction> functionOrder, List<String> baseformOrder) {
		setBaseformOrder(baseformOrder);
		setCategoryOrder(catOrder);
		setFunctionOrder(functionOrder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Phrase p1, Phrase p2) {
		Category c1 = p1.getCategory();
		Category c2 = p2.getCategory();
		DiscourseFunction f1 = p1.getDiscourseFunction();
		DiscourseFunction f2 = p2.getDiscourseFunction();
		int comparison = 0;

		if (c1 != c2 && this._catOrder.contains(c1)
				&& this._catOrder.contains(c2)) {
			comparison = ((Integer) this._catOrder.indexOf(c1))
					.compareTo(this._catOrder.indexOf(c2));

		} else if (f1 != f2 && this._funcOrder.contains(f1)
				&& this._funcOrder.contains(f2)) {
			comparison = ((Integer) this._funcOrder.indexOf(f1))
					.compareTo(this._funcOrder.indexOf(f2));

		} else if (p1 instanceof HeadedPhraseSpec
				&& p2 instanceof HeadedPhraseSpec) {
			LexicalItem head1 = ((HeadedPhraseSpec<?>) p1).getHead();
			LexicalItem head2 = ((HeadedPhraseSpec<?>) p2).getHead();

			if (head1 != null && head2 != null) {
				String base1 = head1.getBaseForm();
				String base2 = head2.getBaseForm();

				if (this._baseOrder.contains(base1)
						&& this._baseOrder.contains(base2)) {
					comparison = ((Integer) this._baseOrder.indexOf(base1))
							.compareTo(this._baseOrder.indexOf(base2));
				}
			}

		}

		return comparison;
	}

	/**
	 * Set the order of phrases by category
	 * 
	 * @param categories
	 *            the category order
	 */
	public void setCategoryOrder(List<Category> categories) {
		this._catOrder = categories;
	}

	/**
	 * Get the list representing the ordering by category.
	 * 
	 * @return the category ordering
	 */
	public List<Category> getCategoryOrder() {
		return this._catOrder;
	}

	/**
	 * Get the list representing the ordering by function.
	 * 
	 * @return the function ordering
	 */
	public List<DiscourseFunction> getFunctionOrder() {
		return this._funcOrder;
	}

	/**
	 * Get the list representing the ordering by baseform.
	 * 
	 * @return the baseform ordering
	 */
	public List<String> getBaseformOrder() {
		return this._baseOrder;
	}

	/**
	 * Set the order of phrases by discourse function
	 * 
	 * @param functions
	 *            the function order
	 */
	public void setFunctionOrder(List<DiscourseFunction> functions) {
		this._funcOrder = functions;
	}

	/**
	 * Set the order of phrases by baseform.
	 * 
	 * @param forms
	 *            the baseform order
	 */
	public void setBaseformOrder(List<String> forms) {
		this._baseOrder = forms;
	}

}
