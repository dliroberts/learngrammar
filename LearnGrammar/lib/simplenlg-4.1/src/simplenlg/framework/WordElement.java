/*
 * WordElement.java - The element representing a word has defined by a lexicon.
 * 
 * Copyright (C) 2010, University of Aberdeen
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package simplenlg.framework;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This is the class for a lexical entry (ie, a word). Words are stored in a
 * {@link simplenlg.lexicon.Lexicon}, and usually the developer retrieves a
 * WordElement via a lookup method in the lexicon
 * 
 * Words always have a base form, and usually have a
 * {@link simplenlg.framework.LexicalCategory}. They may also have a Lexicon ID.
 * 
 * Words also have features (which are retrieved from the Lexicon), these are
 * held in the standard NLGElement feature map
 * <hr>
 * 
 * <p>
 * Copyright (C) 2010, University of Aberdeen
 * </p>
 * 
 * <p>
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * </p>
 * 
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * </p>
 * 
 * <p>
 * You should have received a copy of the GNU Lesser General Public License in the zip
 * file. If not, see <a
 * href="http://www.gnu.org/licenses/">www.gnu.org/licenses</a>.
 * </p>
 * 
 * <p>
 * For more details on SimpleNLG visit the project website at <a
 * href="http://www.csd.abdn.ac.uk/research/simplenlg/"
 * >www.csd.abdn.ac.uk/research/simplenlg</a> or email Dr Ehud Reiter at
 * e.reiter@abdn.ac.uk
 * </p>
 * 
 * @author E. Reiter, University of Aberdeen.
 * @version 4.0
 */

public class WordElement extends NLGElement {

	// Words have baseForm, category, id, and features
	// features are inherited from NLGElement

	String baseForm; // base form, eg "dog". currently also in NLG Element, but
	// will be removed from there

	String id; // id in lexicon (may be null);
//	LexicalCategory category; // type of word

	/**********************************************************/
	// constructors
	/**********************************************************/

	/**
	 * empty constructor
	 * 
	 */
	public WordElement() {
		super();
		this.baseForm = null;
		setCategory(LexicalCategory.ANY);
		this.id = null;
	}

	/**
	 * create a WordElement with the specified baseForm (no category or ID
	 * specified)
	 * 
	 * @param baseForm
	 *            - base form of WordElement
	 */
	public WordElement(String baseForm) {
		super();
		this.baseForm = baseForm;
		setCategory(LexicalCategory.ANY);
		this.id = null;
	}

	/**
	 * create a WordElement with the specified baseForm and category
	 * 
	 * @param baseForm
	 *            - base form of WordElement
	 * @param category
	 *            - category of WordElement
	 */
	public WordElement(String baseForm, LexicalCategory category) {
		super();
		this.baseForm = baseForm;
		setCategory(category);
		this.id = null;
	}

	/**
	 * create a WordElement with the specified baseForm, category, ID
	 * 
	 * @param baseForm
	 *            - base form of WordElement
	 * @param category
	 *            - category of WordElement
	 * @param id
	 *            - ID of word in lexicon
	 */
	public WordElement(String baseForm, LexicalCategory category, String id) {
		super();
		this.baseForm = baseForm;
		setCategory(category);
		this.id = id;
	}

	/**********************************************************/
	// getters and setters
	/**********************************************************/

	/**
	 * @return the baseForm
	 */
	public String getBaseForm() {
		return this.baseForm;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @param baseForm
	 *            the baseForm to set
	 */
	public void setBaseForm(String baseForm) {
		this.baseForm = baseForm;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @param category
	 *            the category to set
	 */
//	public void setCategory(LexicalCategory category) {
//		this.category = category;
//	}

	/**********************************************************/
	// other methods
	/**********************************************************/

	@Override
	public String toString() {
		ElementCategory _category = getCategory();
		StringBuffer buffer = new StringBuffer("WordElement["); //$NON-NLS-1$
		buffer.append(getBaseForm()).append(':');
		if (_category != null) {
			buffer.append(_category.toString());
		} else {
			buffer.append("no category"); //$NON-NLS-1$
		}
		buffer.append(']');
		return buffer.toString();
	}

	public String toXML() {
		String xml = String.format("<word>%n"); //$NON-NLS-1$
		if (getBaseForm() != null)
			xml = xml + String.format("  <base>%s</base>%n", getBaseForm()); //$NON-NLS-1$
		if (getCategory() != LexicalCategory.ANY)
			xml = xml + String.format("  <category>%s</category>%n", //$NON-NLS-1$
					getCategory().toString().toLowerCase());
		if (getId() != null)
			xml = xml + String.format("  <id>%s</id>%n", getId()); //$NON-NLS-1$

		SortedSet<String> featureNames = new TreeSet<String>(
				getAllFeatureNames()); // list features in alpha order
		for (String feature : featureNames) {
			Object value = getFeature(feature);
			if (value != null) { // ignore null features
				if (value instanceof Boolean) { // booleans ignored if false,
					// shown as <XX/> if true
					boolean bvalue = ((Boolean) value).booleanValue();
					if (bvalue)
						xml = xml + String.format("  <%s/>%n", feature); //$NON-NLS-1$
				} else { // otherwise include feature and value
					xml = xml + String.format("  <%s>%s</%s>%n", feature, value //$NON-NLS-1$
							.toString(), feature);
				}
			}

		}
		xml = xml + String.format("</word>%n"); //$NON-NLS-1$
		return xml;
	}

	/**
	 * This method returns an empty <code>List</code> as words do not have child
	 * elements.
	 */
	@Override
	public List<NLGElement> getChildren() {
		return new ArrayList<NLGElement>();
	}

	@Override
	public String printTree(String indent) {
		StringBuffer print = new StringBuffer();
		print.append("WordElement: base=").append(getBaseForm()) //$NON-NLS-1$
				.append(", category=").append(getCategory().toString()) //$NON-NLS-1$
				.append(", ").append(super.toString()).append('\n'); //$NON-NLS-1$
		return print.toString();
	}
}
