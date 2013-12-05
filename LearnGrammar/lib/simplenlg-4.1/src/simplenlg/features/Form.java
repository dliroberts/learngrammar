/*
 * Form.java - A representation of the different forms a verb and its associated 
 * clause can take.
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

package simplenlg.features;

/**
 * <p>
 * An enumeration representing the different forms a verb and its associated
 * phrase can take. The form is recorded under the {@code Feature.FORM} feature
 * and applies to verbs and verb phrases.
 * </p>
 * <hr>
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
 * @author A. Gatt and D. Westwater, University of Aberdeen.
 * @version 4.0
 * 
 */
public enum Form {
	
	/**
	 * The bare infinitive is the base form of the verb.
	 */
	BARE_INFINITIVE,
	
	/**
	 * In English, the gerund form refers to the usage of a verb as a noun. For
	 * example, <em>I like <b>swimming</b></em>. In more general terms, gerunds
	 * are usually formed from the base word with <em>-ing</em> added to the
	 * end.
	 */
	GERUND,

	/**
	 * The imperative form of a verb is the one used when the grammatical 
	 * mood is one of expressing a command or giving a direct request. For example, 
	 * <em><b>Close</b> the door.</em>
	 */
	IMPERATIVE, 
	
	/**
	 * The infinitive form represents the base form of the verb, with our
	 * without the particle <em>to</em>. For example, <em>do</em> and
	 * <em>to do</em> are both infinitive forms of <em>do</em>.
	 */
	INFINITIVE,

	/**
	 * Normal form represents the base verb. For example, <em>kiss</em>,
	 * <em>walk</em>, <em>bark</em>, <em>eat</em>.
	 */
	NORMAL,

	/**
	 * Most verbs will have only a single form for the past tense. However, some
	 * verbs will have two forms, one for the simple past tense and one for the
	 * past participle (also knowns as passive participle or perfect
	 * participle). The part participle represents the second of these two
	 * forms. For example, the verb <em>eat</em> has the simple past form of
	 * <em>ate</em> and also the past participle form of <em>eaten</em>. Another
	 * example, is <em>write</em>, <em>wrote</em> and <em>written</em>.
	 */
	PAST_PARTICIPLE,

	/**
	 * The present participle is identical in form to the gerund and is normally
	 * used in the active voice. However, the gerund is meant to highlight a
	 * verb being used as a noun. The present participle remains as a verb. For 
	 * example, <em>Jim was <b>sleeping</b></em>.
	 */
	PRESENT_PARTICIPLE;
}
