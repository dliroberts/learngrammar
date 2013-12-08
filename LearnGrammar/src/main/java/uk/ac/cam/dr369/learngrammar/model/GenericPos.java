package uk.ac.cam.dr369.learngrammar.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import uk.ac.cam.dr369.learngrammar.util.UniqueLinkedQueue;

/**
 * POS tagsets should reference instances of this class as parent tags, so as to form a hierarchy.
 * 
 * @author duncan.roberts
 *
 */
public enum GenericPos implements Pos {
//	TOKEN("token"),
	
	ARGUMENT_GENERAL(		"argument", 	 .3d/*, TOKEN*/),
	
	NOUN_GENERAL(			"noun",          .5d, ARGUMENT_GENERAL),
	NOUN_COMMON_GENERAL(	"common noun",   .5d, NOUN_GENERAL),
	NOUN_PROPER_GENERAL(	"proper noun",   .5d, NOUN_GENERAL),
	NOUN_SINGULAR_GENERAL(	"singular noun", .5d, NOUN_GENERAL),
	NOUN_PLURAL_GENERAL(	"plural noun",   .5d, NOUN_GENERAL),
	
	COMPARATIVE_GENERAL(	"comparative",   .5d/*, TOKEN*/),
	CONJUNCTION_GENERAL(	"conjunction",   .5d/*, TOKEN*/),
	POSSESSIVE_GENERAL(		"possessive",    .5d/*, TOKEN*/),
	SUPERLATIVE_GENERAL(	"superlative",   .5d/*, TOKEN*/),
	
	ADJECTIVE_GENERAL(		"adjective",     .7d/*, TOKEN*/),
	ADVERB_GENERAL(			"adverb",        .7d/*, TOKEN*/),
	DETERMINER_GENERAL(		"determiner",    .7d/*, TOKEN*/),
	NUMERIC_GENERAL(		"numeric",       .7d/*, TOKEN*/),
	PREPOSITION_GENERAL(	"preposition",   .7d/*, TOKEN*/),
	PRONOUN_GENERAL(		"pronoun",       .7d, ARGUMENT_GENERAL),
	PUNCTUATION_GENERAL(	"punctuation",   .7d/*, TOKEN*/),
	SYMBOL_GENERAL(			"symbol",        .7d/*, TOKEN*/),
	
	VERB_GENERAL(			"verb",          .5d/*, TOKEN*/),
	VERB_PRESENT_GENERAL(	"present tense verb", .8d, VERB_GENERAL), // high score because 3p sing. present and !3p sing. present are v. similar
	VERB_PAST_GENERAL(		"past tense verb",    .6d, VERB_GENERAL); // lower score because past participle and simple past are distinct grammatically
	
	
	private final String description;
	private final double weight;
	private final Set<Pos> parents;
	
	private GenericPos(String description, double weight, Pos... parents) {
		this.description = description;
		this.weight = weight;
		this.parents = Collections.unmodifiableSet(new HashSet<Pos>(Arrays.asList(parents)));
	}
	@Override
	public String description() {
		return description;
	}
	@Override
	public String getLabel() {
		return "!"+name();
	}
	@Override
	public String toString() {
		return name().toLowerCase();
	}
	@Override
	public Set<Pos> parents() {
		return parents;
	}
	@Override
	public double weight() {
		return weight;
	}
	static boolean ancestorOf(Pos child, Pos parent) {
		Queue<Pos> toSearch = new UniqueLinkedQueue<Pos>();
		toSearch.offer(child);
		Pos current = null;
		do { // TODO junit this properly. does this work ok if there's a cycle?
			current = toSearch.poll();
			if (current.equals(parent))
				return true;
			toSearch.addAll(current.parents());
		} while (!toSearch.isEmpty());
		return false;
	}
	@Override
	public boolean descendentOf(Pos parent) {
		return ancestorOf(this, parent);
	}
	@Override
	public boolean ancestorOf(Pos child) {
		return ancestorOf(child, this);
	}
	@Override
	public Set<Pos> ancestors() {
		return ancestors(this);
	}
	static Set<Pos> ancestors(Pos pos) {
		Set<Pos> ancestors = new HashSet<Pos>();
		Queue<Pos> toSearch = new LinkedList<Pos>();
		toSearch.offer(pos);
		Pos current = null;
		do {
			current = toSearch.poll();
			ancestors.add(current);
			toSearch.addAll(current.parents());
		} while (!toSearch.isEmpty());
		return ancestors;
	}
}