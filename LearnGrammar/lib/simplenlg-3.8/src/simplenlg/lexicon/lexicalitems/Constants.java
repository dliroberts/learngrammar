package simplenlg.lexicon.lexicalitems;

import simplenlg.features.Case;
import simplenlg.features.ConjunctionType;
import simplenlg.features.Gender;
import simplenlg.features.NumberAgr;
import simplenlg.features.Person;

/**
 * This class consists entirely of static final fields, defining a small number
 * of function words that the {@link simplenlg.lexicon.Lexicon} loads at runtime
 * by default.
 * 
 * @author agatt
 * 
 */
public class Constants {

	public static final Conjunction AND = new Conjunction("and",
			ConjunctionType.COORDINATING);

	public static final Conjunction OR = new Conjunction("or",
			ConjunctionType.COORDINATING);

	public static final Conjunction BUT = new Conjunction("but",
			ConjunctionType.SUBORDINATING);

	public static final Conjunction BECAUSE = new Conjunction("because",
			ConjunctionType.SUBORDINATING);

	public static final Conjunction[] ALL_CONJUNCTIONS = new Conjunction[] {
			Constants.AND, Constants.OR, Constants.BUT, Constants.BECAUSE };

	public static final Pronoun PRO_1SG_NOM = new Pronoun("I", Case.NOMINATIVE,
			Person.FIRST, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun PRO_1SG_ACC = new Pronoun("me",
			Case.ACCUSATIVE, Person.FIRST, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun PRO_2SG_NOM = new Pronoun("you",
			Case.NOMINATIVE, Person.SECOND, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun PRO_2SG_ACC = new Pronoun("you",
			Case.ACCUSATIVE, Person.SECOND, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun PRO_3SGM_NOM = new Pronoun("he",
			Case.NOMINATIVE, Person.THIRD, NumberAgr.SINGULAR, Gender.MASCULINE);

	public static final Pronoun PRO_3SGM_ACC = new Pronoun("him",
			Case.ACCUSATIVE, Person.THIRD, NumberAgr.SINGULAR, Gender.MASCULINE);

	public static final Pronoun PRO_3SGF_NOM = new Pronoun("she",
			Case.NOMINATIVE, Person.THIRD, NumberAgr.SINGULAR, Gender.FEMININE);

	public static final Pronoun PRO_3SGF_ACC = new Pronoun("her",
			Case.ACCUSATIVE, Person.THIRD, NumberAgr.SINGULAR, Gender.FEMININE);

	public static final Pronoun PRO_1PL_NOM = new Pronoun("we",
			Case.NOMINATIVE, Person.FIRST, NumberAgr.PLURAL, Gender.NEUTER);

	public static final Pronoun PRO_1PL_ACC = new Pronoun("us",
			Case.ACCUSATIVE, Person.FIRST, NumberAgr.PLURAL, Gender.NEUTER);

	public static final Pronoun PRO_2PL = new Pronoun("you", Case.NOM_ACC,
			Person.SECOND, NumberAgr.PLURAL, Gender.NEUTER);

	public static final Pronoun PRO_3SG_NOM = new Pronoun("it",
			Case.NOMINATIVE, Person.THIRD, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun PRO_3SG_ACC = new Pronoun("it",
			Case.ACCUSATIVE, Person.THIRD, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun PRO_3PL_NOM = new Pronoun("they",
			Case.NOMINATIVE, Person.THIRD, NumberAgr.PLURAL, Gender.NEUTER);

	public static final Pronoun PRO_3PL_ACC = new Pronoun("them",
			Case.ACCUSATIVE, Person.THIRD, NumberAgr.PLURAL, Gender.NEUTER);

	public static final Pronoun POSS_1SG = new Pronoun("my", Case.NOMINATIVE,
			Person.FIRST, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun POSS_2SG = new Pronoun("your", Case.NOMINATIVE,
			Person.SECOND, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun POSS_3SGM = new Pronoun("his", Case.NOMINATIVE,
			Person.THIRD, NumberAgr.SINGULAR, Gender.MASCULINE);

	public static final Pronoun POSS_3SGF = new Pronoun("her", Case.NOMINATIVE,
			Person.THIRD, NumberAgr.SINGULAR, Gender.FEMININE);

	public static final Pronoun POSS_1PL = new Pronoun("our", Case.NOMINATIVE,
			Person.FIRST, NumberAgr.PLURAL, Gender.NEUTER);

	public static final Pronoun POSS_2PL = new Pronoun("your", Case.NOMINATIVE,
			Person.SECOND, NumberAgr.PLURAL, Gender.NEUTER);

	public static final Pronoun POSS_3PL = new Pronoun("their",
			Case.NOMINATIVE, Person.THIRD, NumberAgr.PLURAL, Gender.NEUTER);

	public static final Pronoun POSS_3SG = new Pronoun("its", Case.NOMINATIVE,
			Person.THIRD, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun POSS_1SG_NOM_ACC = new Pronoun("mine",
			Case.NOM_ACC, Person.FIRST, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun POSS_2SG_NOM_ACC = new Pronoun("yours",
			Case.NOM_ACC, Person.SECOND, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun POSS_3SGM_NOM_ACC = new Pronoun("his",
			Case.NOM_ACC, Person.THIRD, NumberAgr.SINGULAR, Gender.MASCULINE);

	public static final Pronoun POSS_3SGF_NOM_ACC = new Pronoun("hers",
			Case.NOM_ACC, Person.THIRD, NumberAgr.SINGULAR, Gender.FEMININE);

	public static final Pronoun POSS_1PL_NOM_ACC = new Pronoun("ours",
			Case.NOM_ACC, Person.FIRST, NumberAgr.PLURAL, Gender.NEUTER);

	public static final Pronoun POSS_2PL_NOM_ACC = new Pronoun("yours",
			Case.NOM_ACC, Person.SECOND, NumberAgr.PLURAL, Gender.NEUTER);

	public static final Pronoun POSS_3PL_NOM_ACC = new Pronoun("theirs",
			Case.NOM_ACC, Person.THIRD, NumberAgr.PLURAL, Gender.NEUTER);

	public static final Pronoun POSS_3SG_NOM_ACC = new Pronoun("its",
			Case.NOM_ACC, Person.THIRD, NumberAgr.SINGULAR, Gender.NEUTER);

	public static final Pronoun EXPLETIVE_THERE = new Pronoun("there",
			Person.THIRD, NumberAgr.SINGULAR, Gender.NEUTER, true);

	public static final Pronoun EXPLETIVE_IT = new Pronoun("it", Person.THIRD,
			NumberAgr.SINGULAR, Gender.NEUTER, true);

	public static final Pronoun[] PERSONAL_PRONOUNS = new Pronoun[] {
			Constants.PRO_1SG_NOM, Constants.PRO_1SG_ACC,
			Constants.PRO_2SG_NOM, Constants.PRO_2SG_ACC,
			Constants.PRO_3SG_NOM, Constants.PRO_3SG_ACC,
			Constants.PRO_3SGF_NOM, Constants.PRO_3SGF_ACC,
			Constants.PRO_3SGM_NOM, Constants.PRO_3SGM_ACC,
			Constants.PRO_1PL_NOM, Constants.PRO_1PL_ACC, Constants.PRO_2PL,
			Constants.PRO_3PL_NOM, Constants.PRO_3PL_ACC };

	public static final Pronoun[] POSSESSIVE_PRONOUNS = new Pronoun[] {
			Constants.POSS_1SG, Constants.POSS_2SG, Constants.POSS_3SG,
			Constants.POSS_3SGF, Constants.POSS_3SGM, Constants.POSS_1PL,
			Constants.POSS_2PL, Constants.POSS_3PL, Constants.POSS_1SG_NOM_ACC,
			Constants.POSS_2SG_NOM_ACC, Constants.POSS_3SG_NOM_ACC,
			Constants.POSS_3SGF_NOM_ACC, Constants.POSS_3SGM_NOM_ACC,
			Constants.POSS_1PL_NOM_ACC, Constants.POSS_2PL_NOM_ACC,
			Constants.POSS_3PL_NOM_ACC };

	public static final Pronoun[] ALL_PRONOUNS = new Pronoun[] {
			Constants.PRO_1SG_NOM, Constants.PRO_1SG_ACC,
			Constants.PRO_2SG_NOM, Constants.PRO_2SG_ACC,
			Constants.PRO_3SG_NOM, Constants.PRO_3SG_ACC,
			Constants.PRO_3SGF_NOM, Constants.PRO_3SGF_ACC,
			Constants.PRO_3SGM_NOM, Constants.PRO_3SGM_ACC,
			Constants.PRO_1PL_NOM, Constants.PRO_1PL_ACC, Constants.PRO_2PL,
			Constants.PRO_3PL_NOM, Constants.PRO_3PL_ACC, Constants.POSS_1SG,
			Constants.POSS_2SG, Constants.POSS_3SG, Constants.POSS_3SGF,
			Constants.POSS_3SGM, Constants.POSS_1PL, Constants.POSS_2PL,
			Constants.POSS_2SG_NOM_ACC, Constants.POSS_3SG_NOM_ACC,
			Constants.POSS_3SGF_NOM_ACC, Constants.POSS_3SGM_NOM_ACC,
			Constants.POSS_1PL_NOM_ACC, Constants.POSS_2PL_NOM_ACC,
			Constants.POSS_3PL_NOM_ACC, Constants.POSS_3PL,
			Constants.EXPLETIVE_THERE, Constants.EXPLETIVE_IT };

	public static Determiner DEFINITE = new Determiner("the");

	public static Determiner INDEFINITE_SG = new Determiner("a");

	public static Determiner INDEFINITE_PL = new Determiner("some");

	public static final Determiner[] ALL_DETERMINERS = new Determiner[] {
			Constants.DEFINITE, Constants.INDEFINITE_SG,
			Constants.INDEFINITE_PL };

	public static final Preposition IN = new Preposition("in");

	public static final Preposition ON = new Preposition("on");

	public static final Preposition INTO = new Preposition("into");

	public static final Preposition TO = new Preposition("to");

	public static final Preposition FROM = new Preposition("from");

	public static final Preposition WITH = new Preposition("with");

	public static final Preposition FOR = new Preposition("for");

	public static final Preposition[] ALL_PREPS = { Constants.IN, Constants.ON,
			Constants.INTO, Constants.TO, Constants.FROM, Constants.WITH,
			Constants.FOR };

	/**
	 * Utility method: Get the personal pronoun corresponding to a specific
	 * {@link simplenlg.features.Person}, {@link simplenlg.features.NumberAgr}
	 * and {@link simplenlg.features.Gender} combination. Nominative case is
	 * assumed. For case distinctions, see
	 * {@link #getPersonalPronoun(Person, NumberAgr, Gender, Case)}
	 * 
	 * @param p
	 *            A value of {@link simplenlg.features.Person}
	 * @param n
	 *            A value of {@link simplenlg.features.NumberAgr} (singular or
	 *            plural)
	 * @param g
	 *            A value of {@link simplenlg.features.Gender} (masc. or fem.)
	 * 
	 * @return The personal pronoun with these microplanner.features
	 */
	public static Pronoun getPersonalPronoun(Person p, NumberAgr n, Gender g) {

		for (Pronoun pro : Constants.ALL_PRONOUNS) {
			if (pro.getGender() == g
					&& pro.getNumber() == n
					&& pro.getPerson() == p
					&& (pro.getCaseValue() == Case.NOMINATIVE || pro
							.getCaseValue() == Case.NOM_ACC)) {
				return pro;
			}
		}

		return null;
	}

	/**
	 * Utility method: Get the personal pronoun corresponding to a specific
	 * {@link simplenlg.features.Person}, {@link simplenlg.features.NumberAgr},
	 * {@link simplenlg.features.Gender} and {@link simplenlg.features.Case}
	 * combination.
	 * 
	 * @param p
	 *            A value of {@link simplenlg.features.Person}
	 * @param n
	 *            A value of {@link simplenlg.features.NumberAgr} (singular or
	 *            plural)
	 * @param g
	 *            A value of {@link simplenlg.features.Gender} (masc. or fem.)
	 * 
	 * @return The personal pronoun with these microplanner.features
	 */
	public static Pronoun getPersonalPronoun(Person p, NumberAgr n, Gender g,
			Case c) {

		for (Pronoun pro : Constants.ALL_PRONOUNS) {
			if (pro.getGender() == g && pro.getNumber() == n
					&& pro.getPerson() == p && pro.getCaseValue() == c) {
				return pro;
			}
		}

		return null;
	}

	/**
	 * Gets the possessive pronoun with the given person, number and gender
	 * features.
	 * 
	 * @param p
	 *            the person feature
	 * @param n
	 *            the number feature
	 * @param g
	 *            the gender feature
	 * 
	 * @return the possessive pronoun, if one is found, <code>null</code>
	 *         otherwise.
	 */
	public static Pronoun getPossessivePronoun(Person p, NumberAgr n, Gender g) {

		for (Pronoun pro : Constants.POSSESSIVE_PRONOUNS) {
			if (pro.getPerson() == p && pro.getGender() == g
					&& pro.getNumber() == n) {
				return pro;
			}
		}

		return null;
	}

	/**
	 * Gets the possessive pronoun with the given person, number, gender and
	 * case features.
	 * 
	 * @param p
	 *            the person feature
	 * @param n
	 *            the number feature
	 * @param g
	 *            the gender feature
	 * 
	 * @return the possessive pronoun, if one is found, <code>null</code>
	 *         otherwise.
	 */
	public static Pronoun getPossessivePronoun(Person p, NumberAgr n, Gender g,
			Case c) {

		for (Pronoun pro : Constants.POSSESSIVE_PRONOUNS) {
			if (pro.getPerson() == p && pro.getGender() == g
					&& pro.getNumber() == n && pro.getCaseValue() == c) {
				return pro;
			}
		}

		return null;
	}

	/**
	 * Gets the pronoun with the given baseform
	 * 
	 * @param baseform
	 *            the baseform
	 * 
	 * @return the pronoun, if one is found, <code>null</code> otherwise.
	 */
	public static Pronoun getPronoun(String baseform) {

		if (baseform != null) {
			for (Pronoun p : Constants.ALL_PRONOUNS) {
				if (baseform.equalsIgnoreCase(p.baseForm)) {
					return p;
				}
			}
		}

		return null;

	}

	/**
	 * Get the pronoun with the given person, number, gender and case features,
	 * specifying whether it is possessive or not.
	 * 
	 * @param p
	 *            the person
	 * @param n
	 *            the number
	 * @param g
	 *            the gender
	 * @param c
	 *            the case value
	 * @param possessive
	 *            whether the pronoun required is possessive
	 * @return the pronoun, if one is found, <code>null</code> otherwise
	 */
	public static Pronoun getPronoun(Person p, NumberAgr n, Gender g, Case c,
			boolean possessive) {

		if (possessive) {
			if (c == Case.ACCUSATIVE) {
				return Constants.getPossessivePronoun(p, n, g, Case.NOM_ACC);
			} else {
				return Constants.getPossessivePronoun(p, n, g, c);
			}

		} else {
			return Constants.getPersonalPronoun(p, n, g, c);
		}

	}

	/**
	 * Get the <code>Conjunction</code> matching a <code>String</code>.
	 * 
	 * @param s
	 *            The <code>String</code>
	 * 
	 * @return The conjunction whose baseform is <code>s</code>, if one is
	 *         defined in this class, <code>null</code> otherwise.
	 * 
	 */
	public static Conjunction getConjunction(String s) {

		if (s.equalsIgnoreCase("and")) {
			return Constants.AND;
		} else if (s.equalsIgnoreCase("or")) {
			return Constants.OR;
		} else if (s.equalsIgnoreCase("but")) {
			return Constants.BUT;
		} else if (s.equalsIgnoreCase("because")) {
			return Constants.BECAUSE;
		}

		return null;
	}

	/**
	 * Gets the determiner with a specific baseform.
	 * 
	 * @param word
	 *            A <code>String</code>
	 * 
	 * @return The <code>Determiner</code> object which matches
	 *         <code>word</code>
	 */
	public static Determiner getDeterminer(String word) {

		if (word.equalsIgnoreCase("the")) {
			return Constants.DEFINITE;
		} else if (word.equalsIgnoreCase("a") || word.equalsIgnoreCase("an")) {
			return Constants.INDEFINITE_SG;
		} else if (word.equalsIgnoreCase("some")) {
			return Constants.INDEFINITE_PL;
		} else {
			return null;
		}

	}

	/**
	 * Gets a preposition which is predifined in the
	 * {@link simplenlg.lexicon.lexicalitems.Constants} class.
	 * 
	 * @param baseform
	 *            the baseform of the preposition
	 * 
	 * @return the preposition with the specified baseform
	 * 
	 */
	public static Preposition getPreposition(String baseform) {

		for (Preposition p : Constants.ALL_PREPS) {
			if (p.baseForm.equalsIgnoreCase(baseform)) {
				return p;
			}
		}

		return null;
	}

}
