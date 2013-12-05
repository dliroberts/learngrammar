/* ==================================================
 * SimpleNLG: An API for Natural Language Generation
 * ==================================================
 *
 * Copyright (c) 2007, the University of Aberdeen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted FOR RESEARCH PURPOSES ONLY, provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, 
 * 		this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation and/or 
 *    other materials provided with the distribution.
 * 3. Neither the name of the University of Aberdeen nor the names of its contributors 
 * 	  may be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *    
 *    THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 *    AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 *    THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 *    ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
 *    FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 *    (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *     LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND 
 *     ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 *     (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 *     EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *     
 *  Redistribution and use for purposes other than research requires special permission by the
 *  copyright holders and contributors. Please contact Ehud Reiter (ereiter@csd.abdn.ac.uk) for
 *  more information.
 *     
 *	   =================    
 *     Acknowledgements:
 *     =================
 *     This library contains a re-implementation of some rules derived from the MorphG package
 *     by Guido Minnen, John Carroll and Darren Pearce. You can find more information about MorphG
 *     in the following reference:
 *     	Minnen, G., Carroll, J., and Pearce, D. (2001). Applied Morphological Processing of English.
 *     		Natural Language Engineering 7(3): 207--223.
 *     Thanks to John Carroll (University of Sussex) for permission to re-use the MorphG rules. 
 */

package simplenlg.lexicon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import simplenlg.exception.LexiconException;
import simplenlg.features.Category;
import simplenlg.features.InflectionType;
import simplenlg.features.NumberAgr;
import simplenlg.features.Person;
import simplenlg.features.Quantification;
import simplenlg.features.Tense;
import simplenlg.features.VerbType;
import simplenlg.lexicon.lexicalitems.Adjective;
import simplenlg.lexicon.lexicalitems.Conjunction;
import simplenlg.lexicon.lexicalitems.Constants;
import simplenlg.lexicon.lexicalitems.Determiner;
import simplenlg.lexicon.lexicalitems.LexicalItem;
import simplenlg.lexicon.lexicalitems.Noun;
import simplenlg.lexicon.lexicalitems.Preposition;
import simplenlg.lexicon.lexicalitems.Pronoun;
import simplenlg.lexicon.lexicalitems.Symbol;
import simplenlg.lexicon.lexicalitems.Verb;
import simplenlg.lexicon.morph.AdjectiveInflection;
import simplenlg.lexicon.morph.MorphologicalRule;
import simplenlg.lexicon.morph.NounInflection;
import simplenlg.lexicon.morph.VerbInflection;
import simplenlg.lexicon.morph.WordLists;

/**
 * This class implements the <code>LexiconInterface</code> interface, providing
 * a number of additional methods which operate either directly on strings, or
 * on <code>LexicalItem</code>s. The <code>Lexicon</code> is therefore both a
 * repository of lexical information, and the source from which a finite set of
 * morphological rules can be applied to strings or lexical items.
 * 
 * <p>
 * The <code>Lexicon</code> comes pre-loaded with the following information:
 * 
 * <OL>
 * <LI>inflectional exception lists of verbs and nouns defined in
 * {@link simplenlg.lexicon.morph.WordLists};</LI>
 * <LI>basic function words, namely determiners, pronouns, prepositions and
 * conjunctions, defined in {@link simplenlg.lexicon.lexicalitems.Constants};</LI>
 * <LI>inflectional rules for nouns, verbs and adjectives, defined in the
 * {@link simplenlg.lexicon.morph} package.
 * </OL>
 * 
 * <P>
 * For all other exceptions to the morphological rules defined here, it relies
 * on the predefined pattern-action rules in the package
 * {@link simplenlg.lexicon.morph}.
 * 
 * <p>
 * Internally, the items in the <code>Lexicon</code> are organised using three
 * orthogonal classifications:
 * 
 * <OL>
 * <LI>by ID: all lexical items have a unique ID; if one is not defined for an
 * item, it is added automatically by the lexicon when the item is added to it;
 * <LI>by Category: all classes implementing the <code>LexicalItem</code>
 * interface are assigned a {@link simplenlg.features.Category};
 * <LI>by baseform: all lexical items are assumed to have a baseform. Note that,
 * unlike for the case of ID, the baseform is not assumed to uniquely specify a
 * lexical item (i.e. several lexical items may have the same baseform).
 * </OL>
 * 
 * @author albert gatt
 */
public class Lexicon implements LexiconInterface {

	// map from unique ID to a lexical item
	TreeMap<String, LexicalItem> itemsByID;

	// map from baseform to set of lexical items
	TreeMap<String, Set<String>> itemsByBaseform;

	// map from category to set of lexical items
	TreeMap<Category, Set<String>> itemsByCategory;

	// morphological rules
	TreeMap<String, MorphologicalRule<?>> rules;

	// lexical classes
	TreeMap<String, LexicalClass<?>> lexicalClasses;

	// prefix to use for self-generated IDs
	String idPrefix;

	/**
	 * Creates a new instance of <code>Lexicon</code>. The constructor loads a
	 * list of verb exceptions and symbols at construction time. These are found
	 * in {@link simplenlg.lexicon.morph.WordLists}.
	 */
	public Lexicon() {
		this.itemsByID = new TreeMap<String, LexicalItem>();
		this.itemsByBaseform = new TreeMap<String, Set<String>>();
		this.itemsByCategory = new TreeMap<Category, Set<String>>();
		this.rules = new TreeMap<String, MorphologicalRule<?>>();
		this.lexicalClasses = new TreeMap<String, LexicalClass<?>>();
		this.idPrefix = "I";

		// now, add stuff
		addAuxVerbs();
		addModals();
		addConsDoublingVerbs();
		addSymbols();
		addRules();
		addInvariantNouns();
		addDeterminers();
		addConjunctions();
		addPronouns();
		addPrepositions();
	}

	// ****************************************************************
	// METHODS TO CREATE LEXICAL ITEMS
	// ****************************************************************
	/**
	 * Factory method to construct a lexical item.
	 * 
	 * @param c
	 *            the desired Category
	 * @param w
	 *            the w baseform
	 * 
	 * @return the lexical item
	 */
	public static LexicalItem makeLexicalItem(Category c, String w) {

		switch (c) {
		case NOUN:
			return new Noun(w);

		case VERB:
			return new Verb(w);

		case PRONOUN:
			return new Pronoun(w);

		case PREPOSITION:
			return new Preposition(w);

		case CONJUNCTION:
			return new Conjunction(w);

		case DETERMINER:
			return new Determiner(w);

		case SYMBOL:
			return new Symbol(w);

		default:
			return null;
		}
	}

	// ****************************************************************
	// GETTERS, SETTERS, RETRIEVAL
	// ****************************************************************

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.LexiconInterface#addItem(simplenlg.features.Category,
	 * java.lang.String)
	 */
	public void addItem(Category cat, String word) {
		LexicalItem lex = Lexicon.makeLexicalItem(cat, word);
		addItem(lex);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.LexiconInterface#addItem(simplenlg.lexicon.lexicalitems
	 * .LexicalItem)
	 */
	public void addItem(LexicalItem lex) {
		String word = lex.getBaseForm();
		String id = lex.getID();

		if (id == null) {
			id = makeID();
			lex.setID(id);
		}

		// add to items by id map
		this.itemsByID.put(id, lex);

		// add to items by baseform map
		if (this.itemsByBaseform.containsKey(word)) {
			this.itemsByBaseform.get(word).add(id);
		} else {
			Set<String> ids = new HashSet<String>();
			ids.add(id);
			this.itemsByBaseform.put(word, ids);
		}

		// add to items by category map
		Category cat = lex.getCategory();

		if (this.itemsByCategory.containsKey(cat)) {
			this.itemsByCategory.get(lex.getCategory()).add(id);
		} else {
			Set<String> items = new HashSet<String>();
			items.add(id);
			this.itemsByCategory.put(cat, items);
		}

	}

	/**
	 * An alternative way to add a noun to the lexicon, together with its plural
	 * form. This is especially useful for nouns which have an irregular plural,
	 * although the morphological rules incorporated in the <code>Lexicon</code>
	 * are fairly robust and should handle most irregular forms.
	 * 
	 * <P>
	 * The plural parameter can be <code>null</code> if a noun needs to be
	 * added, but not its plural.
	 * 
	 * <P>
	 * If the <code>Lexicon</code> already contains a noun with the same
	 * baseform, then if the plural form is also specified in the method call,
	 * the noun's plural form will be set.
	 * 
	 * @param noun
	 *            The noun's base form
	 * @param plural
	 *            The plural form of this noun or <code>null</code>
	 */
	public void addNoun(String noun, String plural) {

		if (hasItem(Category.NOUN, noun)) {
			Noun n = (Noun) getItem(Category.NOUN, noun);
			n.setPlural(plural);

		} else {
			Noun n = new Noun(noun);
			n.setPlural(plural);
			addItem(n);
		}
	}

	/**
	 * An alternative way to add a verb to the lexicon, together with its
	 * inflectional forms. This is especially useful if these forms are
	 * irregular, although the morphological rules incorporated in the
	 * <code>Lexicon</code> are fairly robust and should handle most irregular
	 * forms.
	 * 
	 * <P>
	 * Any of the parameters to this method, except for the baseform itself, can
	 * be <code>null</code>.
	 * 
	 * <P>
	 * If the <code>Lexicon</code> already contains a verb with the same
	 * baseform, then if the some or all of the forms are also specified in the
	 * method call, they will be set in the existing lexical entry.
	 * 
	 * @param verb
	 *            The verb's base form
	 * @param past
	 *            The past tense form of this verb or <code>null</code>
	 * @param pastParticiple
	 *            The past participle tense form of this verb or
	 *            <code>null</code>
	 * @param presentParticiple
	 *            The present participle tense form of this verb or
	 *            <code>null</code>
	 */
	public void addVerb(String verb, String past, String pastParticiple,
			String presentParticiple) {

		if (hasItem(Category.VERB, verb)) {
			Verb v = (Verb) getItem(Category.VERB, verb);
			v.setPast(past);
			v.setPastParticiple(pastParticiple);
			v.setPresentParticiple(presentParticiple);

		} else {
			Verb v = new Verb(verb);
			v.setPast(past);
			v.setPastParticiple(pastParticiple);
			v.setPresentParticiple(presentParticiple);
			addItem(v);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.LexiconInterface#getItems(java.lang.String)
	 */
	public Collection<LexicalItem> getItems(String base) {
		List<LexicalItem> items = new ArrayList<LexicalItem>();

		if (this.itemsByBaseform.containsKey(base)) {
			for (String id : this.itemsByBaseform.get(base)) {
				items.add(this.itemsByID.get(id));
			}
		}

		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.LexiconInterface#getItem(simplenlg.features.Category,
	 * java.lang.String)
	 */
	public LexicalItem getItem(Category cat, String base) {

		if (cat == null || base == null) {
			return null;
		}

		if (this.itemsByBaseform.containsKey(base)) {
			for (String id : this.itemsByBaseform.get(base)) {
				LexicalItem lex = this.itemsByID.get(id);

				if (lex.getCategory().equals(cat)) {
					return lex;
				}
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.LexiconInterface#getItems(simplenlg.features.Category)
	 */
	public List<LexicalItem> getItems(Category cat) {
		List<LexicalItem> items = new ArrayList<LexicalItem>();

		if (this.itemsByCategory.containsKey(cat)) {
			for (String id : this.itemsByCategory.get(cat)) {
				items.add(this.itemsByID.get(id));
			}
		}

		return items;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.LexiconInterface#getItem(java.lang.String)
	 */
	public LexicalItem getItemByID(String id) {
		return this.itemsByID.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.LexiconInterface#hasItem(simplenlg.features.Category,
	 * java.lang.String)
	 */
	public boolean hasItem(Category cat, String word) {
		return getItem(cat, word) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.LexiconInterface#hasItem(java.lang.String)
	 */
	public boolean hasItem(String word) {

		for (String baseform : this.itemsByBaseform.keySet()) {
			if (baseform.equalsIgnoreCase(word)) {
				return true;
			}
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.LexiconInterface#hasItemID(java.lang.String)
	 */
	public boolean hasItemID(String id) {
		return this.itemsByID.containsKey(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.LexiconInterface#getNumberOfItems()
	 */
	public int getNumberOfItems() {
		return this.itemsByID.keySet().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * simplenlg.lexicon.LexiconInterface#getNumberOfItems(simplenlg.features
	 * .Category)
	 */
	public int getNumberOfItems(Category cat) {

		if (this.itemsByCategory.containsKey(cat)) {
			return this.itemsByCategory.get(cat).size();
		}

		return 0;
	}

	/**
	 * Gets the number of entries stored in this lexicon.
	 * 
	 * @param baseform
	 *            the baseform
	 * 
	 * @return the number of entries
	 */
	public int getNumberOfEntries(String baseform) {

		if (this.itemsByBaseform.containsKey(baseform)) {
			return this.itemsByBaseform.get(baseform).size();
		}

		return 0;
	}

	// ****************************************************************
	// MORPHOLOGY: INFLECTION
	// ****************************************************************

	/**
	 * Gets the plural form of a word. In actual fact, the <code>Lexicon</code>
	 * checks whether it already contains a <code>Noun</code> whose baseform is
	 * <code>word</code>. If that is the case, it returns the plural form by
	 * calling {@link simplenlg.lexicon.lexicalitems.Noun#getPlural()} from the
	 * <code>Noun</code>. If not, it creates a new instance of <code>Noun</code>
	 * , which gets stored in this instance of <code>Lexicon</code>, returning
	 * the plural form.
	 * 
	 * @param word
	 *            The baseform of some word that needs to be pluralised
	 * 
	 * @return A <code>String</code> which is the plural of <code>word</code>
	 */
	public String getPlural(String word) {
		Noun n = (Noun) getItem(Category.NOUN, word);

		if (n == null) {
			n = new Noun(word);
			addItem(n);
		}

		return n.getPlural();
	}

	/**
	 * Gets the comparative form of an adjective. In actual fact, the
	 * <code>Lexicon</code> checks whether it already contains a
	 * <code>Adjective</code> whose baseform is <code>word</code>. If that is
	 * the case, it returns the comparative form by calling
	 * {@link simplenlg.lexicon.lexicalitems.Adjective#getComparative()} from
	 * the <code>Adjective</code>. If not, it creates a new instance of
	 * <code>Adjective</code>, which gets stored in this instance of
	 * <code>Lexicon</code>, returning the comparative form.
	 * 
	 * <br>
	 * 
	 * <strong>Note</strong> This feature is experimental and is not guaranteed
	 * to return correct results. It is expected to improve in future releases.
	 * 
	 * @param word
	 *            - The baseform of some adjective
	 * 
	 * @return A <code>String</code> which is the comparative form of
	 *         <code>word</code>
	 */
	public String getComparative(String word) {
		Adjective a = (Adjective) getItem(Category.ADJECTIVE, word);

		if (a == null) {
			a = new Adjective(word);
			addItem(a);
		}

		return a.getComparative();
	}

	/**
	 * Gets the superlative form of an adjective. In actual fact, the
	 * <code>Lexicon</code> checks whether it already contains a
	 * <code>Adjective</code> whose baseform is <code>word</code>. If that is
	 * the case, it returns the superlative form by calling
	 * {@link simplenlg.lexicon.lexicalitems.Adjective#getSuperlative()} from
	 * the <code>Adjective</code>. If not, it creates a new instance of
	 * <code>Adjective</code>, which gets stored in this instance of
	 * <code>Lexicon</code>, returning the superlative form.
	 * 
	 * <BR>
	 * 
	 * <strong>Note</strong> This feature is experimental and is not guaranteed
	 * to return correct results. It is expected to improve in future releases.
	 * 
	 * @param word
	 *            - The baseform of some adjective
	 * 
	 * @return A <code>String</code> which is the comparative form of
	 *         <code>word</code>
	 */
	public String getSuperlative(String word) {
		Adjective a = (Adjective) getItem(Category.ADJECTIVE, word);

		if (a == null) {
			a = new Adjective(word);
			addItem(a);
		}

		return a.getSuperlative();
	}

	/**
	 * Gets the past tense form of a verb. Whenever called, <code>Lexicon</code>
	 * checks whether it already contains a <code>Verb</code> whose baseform is
	 * <code>word</code>. If that is the case, it returns the past tense form by
	 * calling {@link simplenlg.lexicon.lexicalitems.Verb#getPast()} from the
	 * <code>Verb</code>. If not, it creates a new instance of <code>Verb</code>
	 * , which gets stored in this instance of <code>Lexicon</code>, and returns
	 * the past tense form form.
	 * 
	 * @param word
	 *            The baseform of some verb
	 * 
	 * @return A <code>String</code> which is the past tense form of
	 *         <code>word</code>
	 */
	public String getPast(String word) {
		Verb v = (Verb) getItem(Category.VERB, word);

		if (v == null) {
			v = new Verb(word);
			addItem(v);
		}

		return v.getPast();
	}

	/**
	 * Gets the past participle form of a verb. Whenever called,
	 * <code>Lexicon</code> checks whether it already contains a
	 * <code>Verb</code> whose baseform is <code>word</code>. If that is the
	 * case, it returns the past participle by calling
	 * {@link simplenlg.lexicon.lexicalitems.Verb#getPastParticiple()} from the
	 * <code>Verb</code>. If not, it creates a new instance of <code>Verb</code>
	 * , which gets stored in this instance of <code>Lexicon</code>, and returns
	 * the past participle form.
	 * 
	 * @param word
	 *            The baseform of some verb
	 * 
	 * @return A <code>String</code> which is the past participle form of
	 *         <code>word</code>
	 */
	public String getPastParticiple(String word) {
		Verb v = (Verb) getItem(Category.VERB, word);

		if (v == null) {
			v = new Verb(word);
			addItem(v);
		}

		return v.getPastParticiple();

	}

	/**
	 * Gets the 3rd person present tense form of a verb. Whenever called,
	 * <code>Lexicon</code> checks whether it already contains a
	 * <code>Verb</code> whose baseform is <code>word</code>. If that is the
	 * case, it returns the 3rd person present by calling
	 * {@link simplenlg.lexicon.lexicalitems.Verb#getPresent3SG()} from the
	 * <code>Verb</code>. If not, it creates a new instance of <code>Verb</code>
	 * , which gets stored in this instance of <code>Lexicon</code>, and returns
	 * the present tense form.
	 * 
	 * @param word
	 *            The baseform of some verb
	 * 
	 * @return A <code>String</code> which is the 3rd person singular present
	 *         tense form of <code>word</code>
	 */
	public String getPresent3SG(String word) {
		Verb v = (Verb) getItem(Category.VERB, word);

		if (v == null) {
			v = new Verb(word);
			addItem(v);
		}

		return v.getPresent3SG();
	}

	/**
	 * Gets the "ing" (present participle or continuous) form of a verb.
	 * Whenever called, <code>Lexicon</code> checks whether it already contains
	 * a <code>Verb</code> whose baseform is <code>word</code>. If that is the
	 * case, it returns the "ing" form by calling
	 * {@link simplenlg.lexicon.lexicalitems.Verb#getPresentParticiple()} from
	 * the <code>Verb</code>. If not, it creates a new instance of
	 * <code>Verb</code>, which gets stored in this instance of
	 * <code>Lexicon</code>, and returns the "ing" form.
	 * 
	 * @param word
	 *            The baseform of some verb
	 * 
	 * @return A <code>String</code> which is the "ing" form of
	 *         <code>word</code>
	 */
	public String getPresentParticiple(String word) {
		Verb v = (Verb) getItem(Category.VERB, word);

		if (v == null) {
			v = new Verb(word);
			addItem(v);
		}

		return v.getPresentParticiple();
	}

	/**
	 * An alias for {@link #getPresentParticiple(String)}
	 * 
	 * @param word
	 *            The baseform of some verb
	 * 
	 * @return The present participle ("ing") form.
	 */
	public String getContinuous(String word) {
		return getPresentParticiple(word);
	}

	/**
	 * An alias for {@link #getPresentParticiple(String)}
	 * 
	 * @param word
	 *            The baseform of some verb
	 * 
	 * @return The "ing" form.
	 */
	public String getIngForm(String word) {
		return getPresentParticiple(word);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seesimplenlg.lexicon.LexiconInterface#addRule(simplenlg.lexicon.morph.
	 * MorphologicalRule)
	 */
	public <T extends LexicalItem> void addRule(MorphologicalRule<T> rule) {

		if (!rule.hasName()) {
			throw new LexiconException(
					"Cannot add unnamed morphological rule to the Lexicon.");
		}

		if (this.rules == null) {
			this.rules = new TreeMap<String, MorphologicalRule<?>>();
		}

		this.rules.put(rule.getName(), rule);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.LexiconInterface#getRules()
	 */
	public Collection<MorphologicalRule<?>> getRules() {
		return this.rules.values();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.LexiconInterface#applyRule(java.lang.String,
	 * java.lang.String)
	 */
	public String applyRule(String ruleName, String word) {

		try {
			return this.rules.get(ruleName).apply(word);

		} catch (NullPointerException npe) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.LexiconInterface#applyRule(java.lang.String,
	 * simplenlg.lexicon.lexicalitems.LexicalItem)
	 */
	@SuppressWarnings("unchecked")
	public <T extends LexicalItem> String applyRule(String ruleName, T lex) {

		if (this.rules.containsKey(ruleName)) {
			try {
				return ((MorphologicalRule<T>) this.rules.get(ruleName))
						.apply(lex);

			} catch (Exception e) {
				return null;
			}
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.LexiconInterface#getVerbForm(java.lang.String,
	 * simplenlg.features.Tense, simplenlg.features.Person,
	 * simplenlg.features.NumberAgr)
	 */
	public String getVerbForm(String v, Tense t, Person p, NumberAgr n) {
		Verb verb;

		if (hasItem(Category.VERB, v)) {
			verb = (Verb) getItem(Category.VERB, v);
		} else {
			verb = new Verb(v);
			addItem(verb);
		}

		switch (t) {

		case PRESENT:
			return verb.getPresent(p, n);
		case PAST:
			return verb.getPast(p, n);
		default:
			return verb.getBaseForm();
		}
	}

	/**
	 * Gets the be subjunctive.
	 * 
	 * @return Utility method: The form of the verb "to be" used in subjunctive
	 *         mood (conditionals), i.e. "were"
	 */
	public String getBeSubjunctive() {
		return "were";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see simplenlg.lexicon.LexiconInterface#reset()
	 */
	public void reset() {
		this.itemsByBaseform.clear();
		this.itemsByID.clear();
		this.itemsByCategory.clear();
	}

	public void addLexicalClass(LexicalClass<?> lexClass) {
		lexClass.setParentLexicon(this);
		this.lexicalClasses.put(lexClass.getID(), lexClass);
	}

	public boolean hasLexicalClass(String classID) {
		return this.lexicalClasses.containsKey(classID);
	}

	public LexicalClass<?> getLexicalClass(String classID) {
		return this.lexicalClasses.get(classID);
	}

	// ****************************************************************
	// UTILS
	// ****************************************************************

	/*
	 * Adds the symbols.
	 */
	private void addSymbols() {

		for (String symb : WordLists.SYMBOLS) {
			addItem(Category.SYMBOL, symb);
		}

	}

	/*
	 * Adds the aux verbs.
	 */
	private void addAuxVerbs() {

		for (String v : WordLists.AUXILIARIES) {
			Verb verb = new Verb(v);
			verb.setVerbType(VerbType.AUX);
			addItem(verb);
		}
	}

	private void addModals() {

		for (String v : WordLists.MODALS) {
			Verb verb = new Verb(v);
			verb.setVerbType(VerbType.MODAL);
			verb.setInflectionType(InflectionType.INVARIANT);
			addItem(verb);
		}
	}

	/*
	 * adds verbs that take cons doubling
	 */
	private void addConsDoublingVerbs() {

		for (String v : WordLists.VERB_CONS_DOUBLING) {
			Verb verb = new Verb(v);
			verb.setVerbType(VerbType.MAIN);
			// verb.setInflectionType(InflectionType.REG_DOUBLING);
			addItem(verb);
		}
	}

	/*
	 * Add nouns which do not take a plural
	 */
	private void addInvariantNouns() {

		for (String n : WordLists.NULL_PLURAL) {
			Noun noun = new Noun(n);
			noun.setInflectionType(InflectionType.INVARIANT);
			addItem(noun);
		}
	}

	/*
	 * Add morphological rules to the lexicon
	 */
	private void addRules() {
		this.rules.put("plural", NounInflection.PLURAL);
		this.rules.put("comparative", AdjectiveInflection.COMPARATIVE);
		this.rules.put("superlative", AdjectiveInflection.SUPERLATIVE);
		this.rules.put("presentTense", VerbInflection.PRESENT_TENSE);
		this.rules.put("pastTense", VerbInflection.PAST_TENSE);
		this.rules.put("pastParticiple", VerbInflection.PAST_PARTICIPLE);
		this.rules.put("presentParticiple", VerbInflection.ING_FORM);
	}

	/*
	 * Add determiners
	 */
	private void addDeterminers() {

		for (Determiner d : Constants.ALL_DETERMINERS) {
			if (d == Constants.DEFINITE) {
				d.setQuantification(Quantification.DEFINITE);
			} else {
				d.setQuantification(Quantification.INDEF_ASSERT);
			}
		}
	}

	/*
	 * Add conjunctions
	 */
	private void addConjunctions() {
		for (Conjunction c : Constants.ALL_CONJUNCTIONS) {
			addItem(c);
		}
	}

	/*
	 * add pronouns
	 */
	private void addPronouns() {
		for (Pronoun p : Constants.ALL_PRONOUNS) {
			addItem(p);
		}
	}

	/*
	 * add prepositions
	 */
	private void addPrepositions() {
		for (Preposition p : Constants.ALL_PREPS) {
			addItem(p);
		}
	}

	/*
	 * Constructs an ID for a lexical entry, just in case it doesn't have one.
	 */
	private String makeID() {
		String number = String.valueOf(getNumberOfItems());
		return this.idPrefix + number;
	}

}
