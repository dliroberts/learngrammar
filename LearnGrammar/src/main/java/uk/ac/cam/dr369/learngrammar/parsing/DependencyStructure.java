package uk.ac.cam.dr369.learngrammar.parsing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Subtype;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.TokenSubtype;
import uk.ac.cam.dr369.learngrammar.util.Utils;
import uk.ac.cam.dr369.learngrammar.util.Utils.VeryCloneable;

/** Parsed representation of a sentence. */
public class DependencyStructure implements Comparable<DependencyStructure>, VeryCloneable<DependencyStructure>, Serializable {
	private static final long serialVersionUID = -5058097886775902483L;
	private final List<GrammaticalRelation> grs;
	private final List<Token> tokens;
	
	public DependencyStructure(List<GrammaticalRelation> grs, List<Token> tokens) {
		this(grs, tokens, true);
	}
	protected DependencyStructure(List<GrammaticalRelation> grs, List<Token> tokens, boolean initialise) {
		this.grs = grs;
		this.tokens = tokens;
		
		if (initialise) { // Initialise each token with references to its associated GRs.
			
			// 1: ensure all tokens referenced in each GR is an element of 'tokens'.
			for (ListIterator<GrammaticalRelation> it = grs.listIterator(); it.hasNext();) {
				GrammaticalRelation gr = it.next();
				Token dependent = gr.getDependent();
				Token head = gr.getHead();
				Subtype subtype = gr.getSubtype();
				
				if (dependent != null) {
					for (Token tok : tokens) {
						if (tok.equals(dependent)) {
							dependent = tok; // use version in 'tokens'
							break;
						}
					}
				}
				if (head != null) { // When building subgraphs of DependencyStructures, headless GRs are acceptable
					for (Token tok : tokens) {
						if (tok.equals(head)) {
							head = tok; // use version in 'tokens'
							break;
						}
					}
				}
				if (subtype instanceof TokenSubtype) {
					Token grsu = ((TokenSubtype) subtype).token();
					for (Token tok : tokens) {
						if (tok.equals(grsu)) {
							subtype = new TokenSubtype(tok); // use version in 'tokens'
							break;
						}
					}
				}
				it.set(new GrammaticalRelation(gr.type(), subtype, gr.getInitialGrValue(), head, dependent));
			}
			
			// 2: Initialise map structure used to find all GRs relating to each token
			Map<Token, List<GrammaticalRelation>> grAssocs = new HashMap<Token, List<GrammaticalRelation>>();
			for (Token tok : tokens) {
				grAssocs.put(tok, new ArrayList<GrammaticalRelation>());
			}
			
			// 3: Place GRs in appropriate token-specific lists
			for (GrammaticalRelation gr : grs) {
				if (gr.getDependent() != null)
					grAssocs.get(gr.getDependent()).add(gr);
				if (gr.getHead() != null) // When building subgraphs of DependencyStructures, headless GRs are acceptable
					grAssocs.get(gr.getHead()).add(gr);
				if (gr.getSubtype() instanceof TokenSubtype)
					grAssocs.get(((TokenSubtype) gr.getSubtype()).token()).add(gr);
			}
			
			// 4: link to tokens
			for (Token tok : tokens) {
				tok.initialiseGrs(grAssocs.get(tok));
			}
		}
	}
	public DependencyStructure substitute(GrammaticalRelation from, GrammaticalRelation to) {
		if (this.grs.indexOf(from) != -1) {
			List<Token> tokens = getTokens();
			List<GrammaticalRelation> grs = getGrs();
			// Save min index of tokens under 'from' GR (crash out for the moment if not contiguous!)
			SortedSet<Token> fromDependentTokens = from.getDependentTokens(true);
			int firstDepIdx = fromDependentTokens.first().getIndex();
			int lastDepIdx = firstDepIdx - 1;
			for (Token depTok : fromDependentTokens) {
				int depIdx = depTok.getIndex();
				if (depIdx != lastDepIdx + 1)
					throw new IllegalStateException("Removing non-contiguous token set not supported");
				lastDepIdx = depIdx;
			}
			// Remove 'from' token dependencies
			tokens.removeAll(fromDependentTokens);
			// Make list of GRs ONLY referenced by subtree headed by 'from'. Remove all such tokens and GRs from DS.
			Set<GrammaticalRelation> fromDependentGrs = from.getDependentGrs(true);
			grs.removeAll(fromDependentGrs);
			// Find all tokens and GRs that sit underneath 'to'.
			SortedSet<Token> toDependents = to.getDependentTokens(true);
			Set<GrammaticalRelation> toDepGrs = to.getDependentGrs(true);
			// Insert these tokens into the token list, starting at min index.
			//tokens.addAll(firstDepIdx, toDependents);
			int i = firstDepIdx;
			for (Token toDepTok : toDependents) {
				tokens.add(i++, toDepTok.clone());
			}
			for (GrammaticalRelation toDepGr : toDepGrs) {
				grs.add(toDepGr.clone());
			}
			// Rewire 'to' point to head of 'from'
			grs.remove(to);
			to = new GrammaticalRelation(
					to.type(),
					to.getSubtype() == null ? null : to.getSubtype().clone(),
					to.getInitialGrValue(),
					from.getHead() == null ? null : from.getHead().clone(),
					to.getDependent() == null ? null : to.getDependent().clone());
			grs.add(to);
			// Fix indices by iterating over tokens and setting their indices to be their list positions.
			// Also have an attempt at fixing up capitalisation.
			Map<Token,Integer> oldTokenToNewIndices = new HashMap<Token,Integer>();
			for (ListIterator<Token> li = tokens.listIterator(firstDepIdx); li.hasNext();) {
				int idx = li.nextIndex();
				Token t = li.next();
				Token tOld = t;
				int oldIdx = t.getIndex();
				if (oldIdx != idx) {
					t = new Token(t.getLemma(), t.getSuffix(), idx, t.pos(), t.getSupertag(), t.getWord()).normaliseCapitalisation();
					li.set(t);
				}
				oldTokenToNewIndices.put(tOld, idx);
			}
			// Refresh references in GRs to new token set
			for (ListIterator<GrammaticalRelation> li = grs.listIterator(); li.hasNext();) {
				GrammaticalRelation gr = li.next();
				for (Token tok : tokens) {
					GrammaticalRelation.Subtype sub = gr.getSubtype();
					if (sub != null && oldTokenToNewIndices.containsKey(sub)) {
						int expectedIdx = oldTokenToNewIndices.get(sub);
						sub = equalsExceptIndex(sub, tok, expectedIdx) ? new TokenSubtype(tok) : sub;
					}
					Token head = gr.getHead();
					if (oldTokenToNewIndices.containsKey(head)) {
						int expectedIdx = oldTokenToNewIndices.get(head);
						head = equalsExceptIndex(head, tok, expectedIdx) ? tok : head;
					}
					Token dep = gr.getDependent();
					if (dep != null && oldTokenToNewIndices.containsKey(dep)) {
						int expectedIdx = oldTokenToNewIndices.get(dep);
						dep = equalsExceptIndex(dep, tok, expectedIdx) ? tok : dep;
					}
					gr = new GrammaticalRelation(gr.type(), sub, gr.getInitialGrValue(), head, dep);
					li.set(gr);
				}
				
				// Filter out 'dead' GRs that refer to some part of the source structure not extant in the target.
				List<Token> tl = new ArrayList<Token>();
				tl.add(gr.getHead());
				if (gr.getDependent() != null)
					tl.add(gr.getDependent());
				if (gr.getSubtype() instanceof TokenSubtype)
					tl.add(((TokenSubtype) gr.getSubtype()).token());
				
				if (!tokens.containsAll(tl))
					li.remove();
				
				// TODO note that the reverse problem also exists: the target structure might require dependencies not found in the source sentence.
				// Only obvious resolution: pass modified sentence back to parser to get refreshed GR set.
			}
			return new DependencyStructure(grs, tokens);
		}
		else
			return this;
	}
	private static boolean equalsExceptIndex(GrammaticalRelation.Subtype old, Token updated, int expectedIndex) {
		if (old instanceof TokenSubtype) {
			Token oldT = ((TokenSubtype) old).token();
			return equalsExceptIndex(oldT, updated, expectedIndex);
		}
		else {
			return old.equals(updated);
		}
	}
	private static boolean equalsExceptIndex(Token old, Token updated, int expectedIndex) {
		return new Token(old.getLemma(), old.getSuffix(), expectedIndex, old.pos(), old.getSupertag(), old.getWord()).equals(updated);
	}

	public SortedSet<Token> findDependencySubset(String word, String gr) {
		for (Token tok : getTokens()) {
			if (tok.getWord().equals(word)) {
				for (GrammaticalRelation grel : tok.getGrs()) {
					if (grel.type().getLabel().equals(gr)) {
						return grel.getDependentTokens(true);
					}
				}
			}
		}
		throw new IllegalArgumentException("Unable to find " + word + ">" + gr + " for " + this);
	}
	
	public DependencyStructure replaceStructure(String word, String gr, GrammaticalRelation toGr) {
		for (Token tok : getTokens()) {
			if (tok.getWord().equals(word)) {
				for (GrammaticalRelation fromGr : tok.getGrs()) {
					if (fromGr.type().getLabel().equals(gr)) {
						return substitute(fromGr, toGr);
					}
				}
			}
		}
		throw new IllegalArgumentException("Unable to find " + word + ">" + gr + " for " + this);
	}
	
	public DependencyStructure substitute(Token from, Token to) {
		to = new Token(to.getLemma(), to.getSuffix(), from.getIndex(), to.pos(), to.getSupertag(), to.getWord()); // note that index comes from 'from' not 'to'
		List<Token> tokens = getTokens();
		int tokIdx;
		if ((tokIdx = tokens.indexOf(from)) != -1) {
			List<GrammaticalRelation> grs = getGrs();
			tokens.set(tokIdx, to);
			for (ListIterator<GrammaticalRelation> li = grs.listIterator(); li.hasNext();) {
				GrammaticalRelation gr = li.next();
				if (gr.getHead().equals(from)) {
					GrammaticalRelation replacement = new GrammaticalRelation(gr.type(), gr.getSubtype(), gr.getInitialGrValue(), to, gr.getDependent());
					li.set(replacement);
				}
				else if (gr.getDependent() != null && gr.getDependent().equals(from)) {
					GrammaticalRelation replacement = new GrammaticalRelation(gr.type(), gr.getSubtype(), gr.getInitialGrValue(), gr.getHead(), to);
					li.set(replacement);
				}
				else if (gr.getSubtype() != null && gr.getSubtype().equals(from)) {
					GrammaticalRelation replacement = new GrammaticalRelation(
							gr.type(), new TokenSubtype(to), gr.getInitialGrValue(), gr.getHead(), gr.getDependent());
					li.set(replacement);
				}
			}
			return new DependencyStructure(grs, tokens);
		}
		else
			return this;
	}
	public String getSentence() {
		StringBuilder sb = new StringBuilder();
		for (Token token : tokens) {
			sb.append(token.getWord());
			sb.append(' ');
		}
		String detokenised = Utils.detokenise(sb.toString().trim());
		return detokenised;
	}
	public List<Token> getTokens() {
		return Utils.deepCopy(tokens);
	}
	protected List<Token> getTokens(boolean copy) {
		return copy ? getTokens() : tokens;
	}
	public List<GrammaticalRelation> getGrs() {
		return Utils.deepCopy(grs);
	}
	protected List<GrammaticalRelation> getGrs(boolean copy) {
		return copy ? getGrs() : grs;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((grs == null) ? 0 : grs.hashCode());
		result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DependencyStructure other = (DependencyStructure) obj;
		if (grs == null) {
			if (other.grs != null)
				return false;
		} else if (!grs.equals(other.grs))
			return false;
		if (tokens == null) {
			if (other.tokens != null)
				return false;
		} else if (!tokens.equals(other.tokens))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Sentence: \"" + getSentence() + "\"; " + "GRs: " + new TreeSet<GrammaticalRelation>(grs) + "; tokens: " + tokens;
	}
	@Override
	public int compareTo(DependencyStructure o) {
		return getSentence().compareTo(o.getSentence());
	}
	@Override
	public DependencyStructure clone() {
		return new DependencyStructure(getGrs(), getTokens());
	}
}