/*
 * MorphologyRules.java - a helper class to the morphology processor for 
 * controlling the morphology rules.
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
package simplenlg.morphology.english;

import simplenlg.features.Feature;
import simplenlg.features.DiscourseFunction;
import simplenlg.features.Form;
import simplenlg.features.Gender;
import simplenlg.features.InternalFeature;
import simplenlg.features.LexicalFeature;
import simplenlg.features.NumberAgreement;
import simplenlg.features.Pattern;
import simplenlg.features.Person;
import simplenlg.features.Tense;
import simplenlg.framework.InflectedWordElement;
import simplenlg.framework.NLGElement;
import simplenlg.framework.StringElement;
import simplenlg.framework.WordElement;

/**
 * <p>
 * This abstract class contains a number of rules for doing simple inflection.
 * </p>
 * 
 * <p>
 * As a matter of course, the processor will first use any user-defined
 * inflection for the world. If no inflection is provided then the lexicon, if
 * it exists, will be examined for the correct inflection. Failing this a set of
 * very basic rules will be examined to inflect the word.
 * </p>
 * 
 * <p>
 * All processing modules perform realisation on a tree of
 * <code>NLGElement</code>s. The modules can alter the tree in whichever way
 * they wish. For example, the syntax processor replaces phrase elements with
 * list elements consisting of inflected words while the morphology processor
 * replaces inflected words with string elements.
 * </p>
 * 
 * <p>
 * <b>N.B.</b> the use of <em>module</em>, <em>processing module</em> and
 * <em>processor</em> is interchangeable. They all mean an instance of this
 * class.
 * </p>
 * 
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
 * @author D. Westwater, University of Aberdeen.
 * @version 4.0
 */
public abstract class MorphologyRules {

	/**
	 * A triple array of Pronouns organised by singular/plural,
	 * possessive/reflexive/subjective/objective and by gender/person.
	 */
	@SuppressWarnings("nls")
	private static final String[][][] PRONOUNS = {
			{ { "I", "you", "he", "she", "it" },
					{ "me", "you", "him", "her", "it" },
					{ "myself", "yourself", "himself", "herself", "itself" },
					{ "mine", "yours", "his", "hers", "its" },
					{ "my", "your", "his", "her", "its" } },
			{
					{ "we", "you", "they", "they", "they" },
					{ "us", "you", "them", "them", "them" },
					{ "ourselves", "yourselves", "themselves", "themselves",
							"themselves" },
					{ "ours", "yours", "theirs", "theirs", "theirs" },
					{ "our", "your", "their", "their", "their" } } };

	/**
	 * This method performs the morphology for nouns.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	protected static StringElement doNounMorphology(
			InflectedWordElement element, WordElement baseWord) {
		StringBuffer realised = new StringBuffer();

		if (element.isPlural()
				&& !element.getFeatureAsBoolean(LexicalFeature.PROPER).booleanValue()) {

			String pluralForm = null;

			if (element.getFeatureAsBoolean(LexicalFeature.NON_COUNT).booleanValue()) {
				pluralForm = element.getBaseForm();
			} else {
				pluralForm = element.getFeatureAsString(LexicalFeature.PLURAL);
			}
			if (pluralForm == null && baseWord != null) {
				if (baseWord.getFeatureAsBoolean(LexicalFeature.NON_COUNT)
						.booleanValue()) {
					pluralForm = baseWord.getBaseForm();
				} else {
					pluralForm = baseWord.getFeatureAsString(LexicalFeature.PLURAL);
				}
			}
			if (pluralForm == null) {
				Object pattern = element.getFeature(Feature.PATTERN);
				if (Pattern.GRECO_LATIN_REGULAR.equals(pattern)) {
					pluralForm = buildGrecoLatinPluralNoun(element
							.getBaseForm());
				} else {
					pluralForm = buildRegularPluralNoun(element.getBaseForm());
				}
			}
			realised.append(pluralForm);
		} else {
			realised.append(element.getBaseForm());
		}
		checkPossessive(element, realised);
		StringElement realisedElement = new StringElement(realised.toString());
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, element
				.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * Builds a plural for regular nouns. The rules are performed in this order:
	 * <ul>
	 * <li>For nouns ending <em>-Cy</em>, where C is any consonant, the ending
	 * becomes <em>-ies</em>. For example, <em>fly</em> becomes <em>flies</em>.</li>
	 * <li>For nouns ending <em>-ch</em>, <em>-s</em>, <em>-sh</em>, <em>-x</em>
	 * or <em>-z</em> the ending becomes <em>-es</em>. For example, <em>box</em>
	 * becomes <em>boxes</em>.</li>
	 * <li>All other nouns have <em>-s</em> appended the other end. For example,
	 * <em>dog</em> becomes <em>dogs</em>.</li>
	 * </ul>
	 * 
	 * @param baseForm
	 *            the base form of the word.
	 * @return the inflected word.
	 */
	private static String buildRegularPluralNoun(String baseForm) {
		String plural = null;
		if (baseForm != null) {
			if (baseForm.matches(".*[b-z&&[^eiou]]y\\b")) { //$NON-NLS-1$
				plural = baseForm.replaceAll("y\\b", "ies"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (baseForm.matches(".*[szx(ch)(sh)]\\b")) { //$NON-NLS-1$
				plural = baseForm + "es"; //$NON-NLS-1$
			} else {
				plural = baseForm + "s"; //$NON-NLS-1$
			}
		}
		return plural;
	}

	/**
	 * Builds a plural for Greco-Latin regular nouns. The rules are performed in
	 * this order:
	 * <ul>
	 * <li>For nouns ending <em>-us</em> the ending becomes <em>-i</em>. For
	 * example, <em>focus</em> becomes <em>foci</em>.</li>
	 * <li>For nouns ending <em>-ma</em> the ending becomes <em>-mata</em>. For
	 * example, <em>trauma</em> becomes <em>traumata</em>.</li>
	 * <li>For nouns ending <em>-a</em> the ending becomes <em>-ae</em>. For
	 * example, <em>larva</em> becomes <em>larvae</em>.</li>
	 * <li>For nouns ending <em>-um</em> or <em>-on</em> the ending becomes
	 * <em>-a</em>. For example, <em>taxon</em> becomes <em>taxa</em>.</li>
	 * <li>For nouns ending <em>-sis</em> the ending becomes <em>-ses</em>. For
	 * example, <em>analysis</em> becomes <em>analyses</em>.</li>
	 * <li>For nouns ending <em>-is</em> the ending becomes <em>-ides</em>. For
	 * example, <em>cystis</em> becomes <em>cystides</em>.</li>
	 * <li>For nouns ending <em>-men</em> the ending becomes <em>-mina</em>. For
	 * example, <em>foramen</em> becomes <em>foramina</em>.</li>
	 * <li>For nouns ending <em>-ex</em> the ending becomes <em>-ices</em>. For
	 * example, <em>index</em> becomes <em>indices</em>.</li>
	 * <li>For nouns ending <em>-x</em> the ending becomes <em>-ces</em>. For
	 * example, <em>matrix</em> becomes <em>matrices</em>.</li>
	 * </ul>
	 * 
	 * @param baseForm
	 *            the base form of the word.
	 * @return the inflected word.
	 */
	private static String buildGrecoLatinPluralNoun(String baseForm) {
		String plural = null;
		if (baseForm != null) {
			if (baseForm.endsWith("us")) { //$NON-NLS-1$
				plural = baseForm.replaceAll("us\\b", "i"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (baseForm.endsWith("ma")) { //$NON-NLS-1$
				plural = baseForm + "ta"; //$NON-NLS-1$
			} else if (baseForm.endsWith("a")) { //$NON-NLS-1$
				plural = baseForm + "e"; //$NON-NLS-1$
			} else if (baseForm.matches(".*[(um)(on)]\\b")) { //$NON-NLS-1$
				plural = baseForm.replaceAll("[(um)(on)]\\b", "a"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (baseForm.endsWith("sis")) { //$NON-NLS-1$
				plural = baseForm.replaceAll("sis\\b", "ses"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (baseForm.endsWith("is")) { //$NON-NLS-1$
				plural = baseForm.replaceAll("is\\b", "ides"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (baseForm.endsWith("men")) { //$NON-NLS-1$
				plural = baseForm.replaceAll("men\\b", "mina"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (baseForm.endsWith("ex")) { //$NON-NLS-1$
				plural = baseForm.replaceAll("ex\\b", "ices"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (baseForm.endsWith("x")) { //$NON-NLS-1$
				plural = baseForm.replaceAll("x\\b", "ces"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				plural = baseForm;
			}
		}
		return plural;
	}

	/**
	 * This method performs the morphology for verbs.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	protected static NLGElement doVerbMorphology(InflectedWordElement element,
			WordElement baseWord) {

		String realised = null;
		Object numberValue = element.getFeature(Feature.NUMBER);
		Object personValue = element.getFeature(Feature.PERSON);
		Tense tenseValue = element.getTense();
		Object formValue = element.getFeature(Feature.FORM);
		Object patternValue = element.getFeature(Feature.PATTERN);

		if (element.isNegated() || Form.BARE_INFINITIVE.equals(formValue)) {
			realised = element.getBaseForm();
		} else if (Form.PRESENT_PARTICIPLE.equals(formValue)) {
			realised = element.getFeatureAsString(LexicalFeature.PRESENT_PARTICIPLE);

			if (realised == null && baseWord != null) {
				realised = baseWord
						.getFeatureAsString(LexicalFeature.PRESENT_PARTICIPLE);
			}
			if (realised == null) {
				if (Pattern.REGULAR_DOUBLE.equals(patternValue)) {
					realised = buildDoublePresPartVerb(element.getBaseForm());
				} else {
					realised = buildRegularPresPartVerb(element.getBaseForm());
				}
			}
		} else if (Tense.PAST.equals(tenseValue)
				|| Form.PAST_PARTICIPLE.equals(formValue)) {
			if (Form.PAST_PARTICIPLE.equals(formValue)) {
				realised = element.getFeatureAsString(LexicalFeature.PAST_PARTICIPLE);

				if (realised == null && baseWord != null) {
					realised = baseWord
							.getFeatureAsString(LexicalFeature.PAST_PARTICIPLE);
				}
				if (realised == null) {
					if ("be".equalsIgnoreCase(element.getBaseForm())) { //$NON-NLS-1$
						realised = "been"; //$NON-NLS-1$
					} else if (Pattern.REGULAR_DOUBLE.equals(patternValue)) {
						realised = buildDoublePastVerb(element.getBaseForm());
					} else {
						realised = buildRegularPastVerb(element.getBaseForm(),
								numberValue);
					}
				}
			} else {
				realised = element.getFeatureAsString(LexicalFeature.PAST);

				if (realised == null && baseWord != null) {
					realised = baseWord.getFeatureAsString(LexicalFeature.PAST);
				}
				if (realised == null) {
					if (Pattern.REGULAR_DOUBLE.equals(patternValue)) {
						realised = buildDoublePastVerb(element.getBaseForm());
					} else {
						realised = buildRegularPastVerb(element.getBaseForm(),
								numberValue);
					}
				}
			}
		} else if ((numberValue == null || NumberAgreement.SINGULAR
				.equals(numberValue))
				&& (personValue == null || Person.THIRD.equals(personValue))
				&& (tenseValue == null || Tense.PRESENT.equals(tenseValue))) {

			realised = element.getFeatureAsString(LexicalFeature.PRESENT3S);

			if (realised == null && baseWord != null
					&& !"be".equalsIgnoreCase(element.getBaseForm())) { //$NON-NLS-1$
				realised = baseWord.getFeatureAsString(LexicalFeature.PRESENT3S);
			}
			if (realised == null) {
				realised = buildPresent3SVerb(element.getBaseForm());
			}
		} else {
			if ("be".equalsIgnoreCase(element.getBaseForm())) { //$NON-NLS-1$
				if (Person.FIRST.equals(personValue)
						&& (NumberAgreement.SINGULAR.equals(numberValue) || numberValue == null)) {
					realised = "am"; //$NON-NLS-1$
				} else {
					realised = "are"; //$NON-NLS-1$
				}
			} else {
				realised = element.getBaseForm();
			}
		}
		StringElement realisedElement = new StringElement(realised);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, element
				.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * Checks to see if the noun is possessive. If it is then nouns in ending in
	 * <em>-s</em> become <em>-s'</em> while every other noun has <em>-'s</em> appended to
	 * the end.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>
	 * @param realised
	 *            the realisation of the word.
	 */
	private static void checkPossessive(InflectedWordElement element,
			StringBuffer realised) {

		if (element.getFeatureAsBoolean(Feature.POSSESSIVE).booleanValue()) {
			if (realised.charAt(realised.length() - 1) == 's') {
				realised.append('\'');

			} else {
				realised.append("'s"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Builds the third-person singular form for regular verbs. The rules are
	 * performed in this order:
	 * <ul>
	 * <li>If the verb is <em>be</em> the realised form is <em>is</em>.</li>
	 * <li>For verbs ending <em>-ch</em>, <em>-s</em>, <em>-sh</em>, <em>-x</em>
	 * or <em>-z</em> the ending becomes <em>-es</em>. For example,
	 * <em>preach</em> becomes <em>preaches</em>.</li>
	 * <li>For verbs ending <em>-y</em> the ending becomes <em>-ies</em>. For
	 * example, <em>fly</em> becomes <em>flies</em>.</li>
	 * <li>For every other verb, <em>-s</em> is added to the end of the word.</li>
	 * </ul>
	 * 
	 * @param baseForm
	 *            the base form of the word.
	 * @return the inflected word.
	 */
	private static String buildPresent3SVerb(String baseForm) {
		String morphology = null;
		if (baseForm != null) {
			if (baseForm.equalsIgnoreCase("be")) { //$NON-NLS-1$
				morphology = "is"; //$NON-NLS-1$
			} else if (baseForm.matches(".*[szx(ch)(sh)]\\b")) { //$NON-NLS-1$
				morphology = baseForm + "es"; //$NON-NLS-1$
			} else if (baseForm.matches(".*[b-z&&[^eiou]]y\\b")) { //$NON-NLS-1$
				morphology = baseForm.replaceAll("y\\b", "ies"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				morphology = baseForm + "s"; //$NON-NLS-1$
			}
		}
		return morphology;
	}

	/**
	 * Builds the past-tense form for regular verbs. The rules are performed in
	 * this order:
	 * <ul>
	 * <li>If the verb is <em>be</em> and the number agreement is plural then
	 * the realised form is <em>were</em>.</li>
	 * <li>If the verb is <em>be</em> and the number agreement is singular then
	 * the realised form is <em>was</em>.</li>
	 * <li>For verbs ending <em>-e</em> the ending becomes <em>-ed</em>. For
	 * example, <em>chased</em> becomes <em>chased</em>.</li>
	 * <li>For verbs ending <em>-Cy</em>, where C is any consonant, the ending
	 * becomes <em>-ied</em>. For example, <em>dry</em> becomes <em>dried</em>.</li>
	 * <li>For every other verb, <em>-ed</em> is added to the end of the word.</li>
	 * </ul>
	 * 
	 * @param baseForm
	 *            the base form of the word.
	 * @param number
	 *            the number agreement for the word.
	 * @return the inflected word.
	 */
	private static String buildRegularPastVerb(String baseForm, Object number) {
		String morphology = null;
		if (baseForm != null) {
			if (baseForm.equalsIgnoreCase("be")) { //$NON-NLS-1$
				if (NumberAgreement.PLURAL.equals(number)) {
					morphology = "were"; //$NON-NLS-1$
				} else {
					morphology = "was"; //$NON-NLS-1$
				}
			} else if (baseForm.endsWith("e")) { //$NON-NLS-1$
				morphology = baseForm + "d"; //$NON-NLS-1$
			} else if (baseForm.matches(".*[b-z&&[^eiou]]y\\b")) { //$NON-NLS-1$
				morphology = baseForm.replaceAll("y\\b", "ied"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				morphology = baseForm + "ed"; //$NON-NLS-1$
			}
		}
		return morphology;
	}

	/**
	 * Builds the past-tense form for verbs that follow the doubling form of the
	 * last consonant. <em>-ed</em> is added to the end after the last consonant
	 * is doubled. For example, <em>tug</em> becomes <em>tugged</em>.
	 * 
	 * @param baseForm
	 *            the base form of the word.
	 * @return the inflected word.
	 */
	private static String buildDoublePastVerb(String baseForm) {
		String morphology = null;
		if (baseForm != null) {
			morphology = baseForm + baseForm.charAt(baseForm.length() - 1)
					+ "ed"; //$NON-NLS-1$
		}
		return morphology;
	}

	/**
	 * Builds the present participle form for regular verbs. The rules are
	 * performed in this order:
	 * <ul>
	 * <li>If the verb is <em>be</em> then the realised form is <em>being</em>.</li>
	 * <li>For verbs ending <em>-ie</em> the ending becomes <em>-ying</em>. For
	 * example, <em>tie</em> becomes <em>tying</em>.</li>
	 * <li>For verbs ending <em>-ee</em>, <em>-oe</em> or <em>-ye</em> then
	 * <em>-ing</em> is added to the end. For example, <em>canoe</em> becomes
	 * <em>canoeing</em>.</li>
	 * <li>For other verbs ending in <em>-e</em> the ending becomes
	 * <em>-ing</em>. For example, <em>chase</em> becomes <em>chasing</em>.</li>
	 * <li>For all other verbs, <em>-ing</em> is added to the end. For example,
	 * <em>dry</em> becomes <em>drying</em>.</li>
	 * </ul>
	 * 
	 * @param baseForm
	 *            the base form of the word.
	 * @param number
	 *            the number agreement for the word.
	 * @return the inflected word.
	 */
	private static String buildRegularPresPartVerb(String baseForm) {
		String morphology = null;
		if (baseForm != null) {
			if (baseForm.equalsIgnoreCase("be")) { //$NON-NLS-1$
				morphology = "being"; //$NON-NLS-1$
			} else if (baseForm.endsWith("ie")) { //$NON-NLS-1$
				morphology = baseForm.replaceAll("ie\\b", "ying"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (baseForm.matches(".*[^iyeo]e\\b")) { //$NON-NLS-1$
				morphology = baseForm.replaceAll("e\\b", "ing"); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				morphology = baseForm + "ing"; //$NON-NLS-1$
			}
		}
		return morphology;
	}

	/**
	 * Builds the present participle form for verbs that follow the doubling
	 * form of the last consonant. <em>-ing</em> is added to the end after the
	 * last consonant is doubled. For example, <em>tug</em> becomes
	 * <em>tugging</em>.
	 * 
	 * @param baseForm
	 *            the base form of the word.
	 * @return the inflected word.
	 */
	private static String buildDoublePresPartVerb(String baseForm) {
		String morphology = null;
		if (baseForm != null) {
			morphology = baseForm + baseForm.charAt(baseForm.length() - 1)
					+ "ing"; //$NON-NLS-1$
		}
		return morphology;
	}

	/**
	 * This method performs the morphology for adjectives.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public static NLGElement doAdjectiveMorphology(
			InflectedWordElement element, WordElement baseWord) {

		String realised = null;
		Object patternValue = element.getFeature(Feature.PATTERN);

		if (element.getFeatureAsBoolean(Feature.IS_COMPARATIVE).booleanValue()) {
			realised = element.getFeatureAsString(LexicalFeature.COMPARATIVE);

			if (realised == null && baseWord != null) {
				realised = baseWord.getFeatureAsString(LexicalFeature.COMPARATIVE);
			}
			if (realised == null) {
				if (Pattern.REGULAR_DOUBLE.equals(patternValue)) {
					realised = buildDoubleCompAdjective(element.getBaseForm());
				} else {
					realised = buildRegularComparative(element.getBaseForm());
				}
			}
		} else if (element.getFeatureAsBoolean(Feature.IS_SUPERLATIVE)
				.booleanValue()) {

			realised = element.getFeatureAsString(LexicalFeature.SUPERLATIVE);

			if (realised == null && baseWord != null) {
				realised = baseWord.getFeatureAsString(LexicalFeature.SUPERLATIVE);
			}
			if (realised == null) {
				if (Pattern.REGULAR_DOUBLE.equals(patternValue)) {
					realised = buildDoubleSuperAdjective(element.getBaseForm());
				} else {
					realised = buildRegularSuperlative(element.getBaseForm());
				}
			}
		} else {
			realised = element.getBaseForm();
		}
		StringElement realisedElement = new StringElement(realised);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, element
				.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * Builds the comparative form for adjectives that follow the doubling form
	 * of the last consonant. <em>-er</em> is added to the end after the last
	 * consonant is doubled. For example, <em>fat</em> becomes <em>fatter</em>.
	 * 
	 * @param baseForm
	 *            the base form of the word.
	 * @return the inflected word.
	 */
	private static String buildDoubleCompAdjective(String baseForm) {
		String morphology = null;
		if (baseForm != null) {
			morphology = baseForm + baseForm.charAt(baseForm.length() - 1)
					+ "er"; //$NON-NLS-1$
		}
		return morphology;
	}

	/**
	 * Builds the comparative form for regular adjectives. The rules are
	 * performed in this order:
	 * <ul>
	 * <li>For verbs ending <em>-Cy</em>, where C is any consonant, the ending
	 * becomes <em>-ier</em>. For example, <em>brainy</em> becomes
	 * <em>brainier</em>.</li>
	 * <li>For verbs ending <em>-e</em> the ending becomes <em>-er</em>. For
	 * example, <em>fine</em> becomes <em>finer</em>.</li>
	 * <li>For all other verbs, <em>-er</em> is added to the end. For example,
	 * <em>clear</em> becomes <em>clearer</em>.</li>
	 * </ul>
	 * 
	 * @param baseForm
	 *            the base form of the word.
	 * @param number
	 *            the number agreement for the word.
	 * @return the inflected word.
	 */
	private static String buildRegularComparative(String baseForm) {
		String morphology = null;
		if (baseForm != null) {
			if (baseForm.matches(".*[b-z&&[^eiou]]y\\b")) { //$NON-NLS-1$
				morphology = baseForm.replaceAll("y\\b", "ier"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (baseForm.endsWith("e")) { //$NON-NLS-1$
				morphology = baseForm + "r"; //$NON-NLS-1$
			} else {
				morphology = baseForm + "er"; //$NON-NLS-1$
			}
		}
		return morphology;
	}

	/**
	 * Builds the superlative form for adjectives that follow the doubling form
	 * of the last consonant. <em>-est</em> is added to the end after the last
	 * consonant is doubled. For example, <em>fat</em> becomes <em>fattest</em>.
	 * 
	 * @param baseForm
	 *            the base form of the word.
	 * @return the inflected word.
	 */
	private static String buildDoubleSuperAdjective(String baseForm) {
		String morphology = null;
		if (baseForm != null) {
			morphology = baseForm + baseForm.charAt(baseForm.length() - 1)
					+ "est"; //$NON-NLS-1$
		}
		return morphology;
	}

	/**
	 * Builds the superlative form for regular adjectives. The rules are
	 * performed in this order:
	 * <ul>
	 * <li>For verbs ending <em>-Cy</em>, where C is any consonant, the ending
	 * becomes <em>-iest</em>. For example, <em>brainy</em> becomes
	 * <em>brainiest</em>.</li>
	 * <li>For verbs ending <em>-e</em> the ending becomes <em>-est</em>. For
	 * example, <em>fine</em> becomes <em>finest</em>.</li>
	 * <li>For all other verbs, <em>-est</em> is added to the end. For example,
	 * <em>clear</em> becomes <em>clearest</em>.</li>
	 * </ul>
	 * 
	 * @param baseForm
	 *            the base form of the word.
	 * @param number
	 *            the number agreement for the word.
	 * @return the inflected word.
	 */
	private static String buildRegularSuperlative(String baseForm) {
		String morphology = null;
		if (baseForm != null) {
			if (baseForm.matches(".*[b-z&&[^eiou]]y\\b")) { //$NON-NLS-1$
				morphology = baseForm.replaceAll("y\\b", "iest"); //$NON-NLS-1$ //$NON-NLS-2$
			} else if (baseForm.endsWith("e")) { //$NON-NLS-1$
				morphology = baseForm + "st"; //$NON-NLS-1$
			} else {
				morphology = baseForm + "est"; //$NON-NLS-1$
			}
		}
		return morphology;
	}

	/**
	 * This method performs the morphology for adverbs.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @param baseWord
	 *            the <code>WordElement</code> as created from the lexicon
	 *            entry.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public static NLGElement doAdverbMorphology(InflectedWordElement element,
			WordElement baseWord) {

		String realised = null;

		if (element.getFeatureAsBoolean(Feature.IS_COMPARATIVE).booleanValue()) {
			realised = element.getFeatureAsString(LexicalFeature.COMPARATIVE);

			if (realised == null && baseWord != null) {
				realised = baseWord.getFeatureAsString(LexicalFeature.COMPARATIVE);
			}
			if (realised == null) {
				realised = buildRegularComparative(element.getBaseForm());
			}
		} else if (element.getFeatureAsBoolean(Feature.IS_SUPERLATIVE)
				.booleanValue()) {

			realised = element.getFeatureAsString(LexicalFeature.SUPERLATIVE);

			if (realised == null && baseWord != null) {
				realised = baseWord.getFeatureAsString(LexicalFeature.SUPERLATIVE);
			}
			if (realised == null) {
				realised = buildRegularSuperlative(element.getBaseForm());
			}
		} else {
			realised = element.getBaseForm();
		}
		StringElement realisedElement = new StringElement(realised);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, element
				.getFeature(InternalFeature.DISCOURSE_FUNCTION));
		return realisedElement;
	}

	/**
	 * This method performs the morphology for pronouns.
	 * 
	 * @param element
	 *            the <code>InflectedWordElement</code>.
	 * @return a <code>StringElement</code> representing the word after
	 *         inflection.
	 */
	public static NLGElement doPronounMorphology(InflectedWordElement element) {
		String realised = null;

		if (!element.getFeatureAsBoolean(InternalFeature.NON_MORPH).booleanValue()) {
			Object genderValue = element.getFeature(LexicalFeature.GENDER);
			Object personValue = element.getFeature(Feature.PERSON);
			Object discourseValue = element
					.getFeature(InternalFeature.DISCOURSE_FUNCTION);

			int numberIndex = element.isPlural() ? 1 : 0;
			int genderIndex = (genderValue instanceof Gender) ? ((Gender) genderValue)
					.ordinal()
					: 2;

			int personIndex = (personValue instanceof Person) ? ((Person) personValue)
					.ordinal()
					: 2;

			if (personIndex == 2) {
				personIndex += genderIndex;
			}

			int positionIndex = 0;

			if (element.getFeatureAsBoolean(LexicalFeature.REFLEXIVE).booleanValue()) {
				positionIndex = 2;
			} else if (element.getFeatureAsBoolean(Feature.POSSESSIVE)
					.booleanValue()) {
				positionIndex = 3;
				if (DiscourseFunction.SPECIFIER.equals(discourseValue)) {
					positionIndex++;
				}
			} else {
				positionIndex = (DiscourseFunction.SUBJECT
						.equals(discourseValue) && !element
						.getFeatureAsBoolean(Feature.PASSIVE).booleanValue())
						|| DiscourseFunction.SPECIFIER.equals(discourseValue)
						|| (DiscourseFunction.COMPLEMENT.equals(discourseValue) && element
								.getFeatureAsBoolean(Feature.PASSIVE)
								.booleanValue()) ? 0 : 1;
			}
			realised = PRONOUNS[numberIndex][positionIndex][personIndex];
		} else {
			realised = element.getBaseForm();
		}
		StringElement realisedElement = new StringElement(realised);
		realisedElement.setFeature(InternalFeature.DISCOURSE_FUNCTION, element
				.getFeature(InternalFeature.DISCOURSE_FUNCTION));

		return realisedElement;
	}

	/**
	 * This method performs the morphology for determiners.
	 * 
	 * @param determiner
	 *            the <code>InflectedWordElement</code>.
	 * @param realisation
	 *            the current realisation of the determiner.
	 */
	public static void doDeterminerMorphology(NLGElement determiner,
			String realisation) {

		if (realisation != null) {
			if (determiner.getRealisation().equals("a")) { //$NON-NLS-1$

				if (determiner.isPlural()) {
					determiner.setRealisation("some"); //$NON-NLS-1$
				} else if (realisation.matches("\\A(a|e|i|o|u).*")) { //$NON-NLS-1$
					determiner.setRealisation("an"); //$NON-NLS-1$
				}
			}
		}
	}
}
