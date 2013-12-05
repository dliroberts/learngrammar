package uk.ac.cam.dr369.learngrammar.model;

import static uk.ac.cam.dr369.learngrammar.model.GenericPos.ADJECTIVE_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.ADVERB_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.NOUN_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.VERB_GENERAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Set;

import com.google.common.collect.Lists;

import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Slot;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Subtype;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.TokenSubtype;
import uk.ac.cam.dr369.learngrammar.semantics.VerbFrame;
import uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser;
import uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser.SemanticNounClass;
import uk.ac.cam.dr369.learngrammar.util.Utils;
import uk.ac.cam.dr369.learngrammar.util.Utils.VeryCloneable;

public class Token implements VeryCloneable<Token>, Comparable<Token> {
	private static WordnetSemanticAnalyser semanticAnalyser;
	
	private final String lemma;
	private final String suffix;
	private final int index;
	private final Pos posTag;
	private final NamedEntityClass namedEntityClass;
	private final String word;
	private final String supertag;
	private final VerbFrame verbFrame; // only applicable if this is a verb
	private List<GrammaticalRelation> grs;
	/** Once 'attached' to a dependency structure, this Token becomes immutable (GRs cannot be changed). Clones are 'detached'. */
	private boolean attached;
	
	public Token(String lemma, String suffix, int index, Pos posTag, String supertag, String word) {
		this(lemma, suffix, index, posTag, supertag, word, null, null);
	}
	public Token(String lemma, String suffix, int index, Pos posTag, String supertag, String word, NamedEntityClass ner) {
		this(lemma, suffix, index, posTag, supertag, word, null, ner);
	}
	public Token(String lemma, String suffix, int index, Pos posTag, String supertag, String word, VerbFrame verbFrame) {
		this(lemma, suffix, index, posTag, supertag, word, verbFrame, null);
	}
	public Token(String lemma, String suffix, int index, Pos posTag, String supertag, String word, VerbFrame verbFrame, NamedEntityClass ner) {
		this.suffix = suffix == null ? "" : suffix;
		this.index = index;
		this.posTag = posTag;
		this.word = word;
		this.supertag = supertag;
		this.namedEntityClass = ner;
		attached = false;
		if (semanticAnalyser == null)
			semanticAnalyser = WordnetSemanticAnalyser.getInstance(null);
		this.lemma = lemma == null && index >= 0 ? semanticAnalyser.firstLemma(this) : lemma; // index<0: special null token
		this.verbFrame = verbFrame;
	}
	private Token(String lemma, String suffix, int index, Pos posTag, String supertag, String word, boolean attached) {
		this(lemma, suffix, index, posTag, supertag, word);
		this.attached = attached;
	}
	public String getSupertag() {
		return supertag;
	}
	public String getWord() {
		return word;
	}
	public String getLemma() {
		return lemma;
	}
	public Pos pos() {
		return posTag;
	}
	public NamedEntityClass getNamedEntityClass() {
		return namedEntityClass;
	}
	public String getSuffix() {
		return suffix;
	}
	public int getIndex() {
		return index;
	}
	public List<GrammaticalRelation> getGrs() {
		return grs;
	}
	public VerbFrame getVerbFrame() {
		return verbFrame;
	}
	public void initialiseGrs(List<GrammaticalRelation> grs) {
		if (attached)
			throw new IllegalStateException("GR list cannot be modified once set.");
		attached = true;
		this.grs = grs;
	}
	/** Almost inherently fallible stab at normalising capitalisation (for when a word has been moved to a different part of a sentence, e.g. becomes the first word). */
	public Token normaliseCapitalisation() {
		List<String> exceptions = Arrays.asList(new String[] { // TODO largely replaceable by referencing proper noun POS tag, no?
				"I", "French", "English", "German", "Italian", "Spanish", "Chinese", "Italian", "Hindi", "Japanese", "Thai", "Greek", "Portuguese", "Russian", "Polish", "Swiss", "Belgian",
				"Irish", "American", "Canadian", "Mexican", "Iranian", "African", "European" // Only needed for CLAWS2 where these are marked as JJ (adjective)...
		});
		List<String> initialCapitalClaws2Tags = Arrays.asList(new String[] {
				"NP", "NP1", "NP2", "NPD1", "NPD2", "NPM1", "NPM2", "NNSB1", "NNSB2", "NNL1"}); // FIXME hardcoded tagset dependency!!
		List<Pos> initialCapitalPtbTags = Arrays.asList(new Pos[] {CandcPtbPos.NOUN_PLURAL_PROPER, CandcPtbPos.NOUN_SINGULAR_PROPER});
		String modWord = word;
		if (initialCapitalClaws2Tags.contains(posTag) || initialCapitalPtbTags.contains(posTag) || exceptions.contains(word)) {
			// do nowt
		}
		else if (index == 0) {
			if (Utils.allLowerCase(word)) {
				modWord = Utils.toInitialCapitalOnly(word);
			}
		}
		else {
			if (Utils.initialCapitalOnly(word)) {
				modWord = word.toLowerCase();
			}
		}
		
		return new Token(lemma, suffix, index, posTag, supertag, modWord, false);
	}
	public boolean isHeadOf(GrammaticalRelation.GrType grType) {
		return isXOf(grType, Slot.HEAD).size() > 0;
	}
	public boolean isDependentOf(GrammaticalRelation.GrType grType) {
		return isXOf(grType, Slot.DEPENDENT).size() > 0;
	}
	public boolean isSubtypeOf(GrammaticalRelation.GrType grType) {
		return isXOf(grType, Slot.SUBTYPE).size() > 0;
	}
	public List<GrammaticalRelation> headOf(GrammaticalRelation.GrType grType) {
		return isXOf(grType, Slot.HEAD);
	}
	public List<GrammaticalRelation> dependentOf(GrammaticalRelation.GrType grType) {
		return isXOf(grType, Slot.DEPENDENT);
	}
	public List<GrammaticalRelation> subtypeOf(GrammaticalRelation.GrType grType) {
		return isXOf(grType, Slot.SUBTYPE);
	}
	public List<GrammaticalRelation> headOf() {
		return isXOf(null, Slot.HEAD);
	}
	public List<GrammaticalRelation> dependentOf() {
		return isXOf(null, Slot.DEPENDENT);
	}
	public List<GrammaticalRelation> subtypeOf() {
		return isXOf(null, Slot.SUBTYPE);
	}
	public List<GrammaticalRelation> childOf() {
		return new ArrayList<GrammaticalRelation>(Utils.union(isXOf(null, Slot.SUBTYPE), isXOf(null, Slot.DEPENDENT)));
	}
	public boolean xOf(GrammaticalRelation.GrType grType, Slot pos) {
		return isXOf(grType, pos).size() > 0;
	}
	public List<GrammaticalRelation> isXOf(GrammaticalRelation.GrType grType, Slot pos) {
		List<GrammaticalRelation> headedBys = new ArrayList<GrammaticalRelation>();
		for (GrammaticalRelation gr : getGrs()) {
			boolean tokEq;
			switch (pos) {
			case HEAD:
				tokEq = this.equals(gr.getHead());
				break;
			case DEPENDENT:
				Token dependent = gr.getDependent();
				tokEq = dependent != null && dependent.equals(this);
				break;
			case SUBTYPE:
				Subtype subtype = gr.getSubtype();
				if (subtype instanceof TokenSubtype) {
					Token subtypeToken = ((TokenSubtype) subtype).token();
					tokEq = subtypeToken.equals(this);
				}
				else
					tokEq = false;
				break;
			default:
				throw new IllegalArgumentException();
			}
			
			if (tokEq && (grType == null || gr.type().descendentOf(grType)))
				headedBys.add(gr);
		}
		return headedBys;
	}
	public List<Token> getAncestorTokens() {
		List<Token> ancestorTokens = new ArrayList<Token>();
		getAncestors(ancestorTokens, null);
		return ancestorTokens;
	}
	public List<GrammaticalRelation> getAncestorGrs() {
		List<GrammaticalRelation> ancestorGrs = new ArrayList<GrammaticalRelation>();
		getAncestors(null, ancestorGrs);
		return ancestorGrs;
	}
	private List<List<GrammaticalRelation>> getAncestorGrHierarchy(List<GrammaticalRelation> currentGrHierarchy, Set<Token> seen) {
		List<List<GrammaticalRelation>> ancestorGrHierarchy = new ArrayList<List<GrammaticalRelation>>();
		List<GrammaticalRelation> parents = subtypeOf();
		parents.addAll(dependentOf());
		for (GrammaticalRelation gr : parents) {
			List<GrammaticalRelation> dupGrHier = new ArrayList<GrammaticalRelation>();
			dupGrHier.addAll(currentGrHierarchy);
			dupGrHier.add(gr);
			
			Token head = gr.getHead();
			if (!seen.contains(head)) {
				// TODO untested: copying of seen into seenCp. Only want to exclude toks that've occurred in this specific hierarchy
				Set<Token> seenCp = new HashSet<Token>(seen);
				seenCp.add(head);
				List<List<GrammaticalRelation>> h = head.getAncestorGrHierarchy(dupGrHier, seenCp);
				ancestorGrHierarchy.addAll(h);
			}
		}
		if (parents.isEmpty())
			ancestorGrHierarchy.add(currentGrHierarchy);
		return ancestorGrHierarchy;
	}
	
	private List<List<Token>> getAncestorTokenHierarchy(List<Token> currentTokenHierarchy) {
		List<List<Token>> ancestorTokenHierarchy = new ArrayList<List<Token>>();
		List<GrammaticalRelation> parents = subtypeOf();
		parents.addAll(dependentOf());
		currentTokenHierarchy.add(this);
		for (GrammaticalRelation gr : parents) {
			List<Token> dupTokHier = new ArrayList<Token>();
			dupTokHier.addAll(currentTokenHierarchy);
//			dupTokHier.add(this);
			
			Token head = gr.getHead();
			List<List<Token>> h = head.getAncestorTokenHierarchy(dupTokHier);
			ancestorTokenHierarchy.addAll(h);
		}
		if (parents.isEmpty())
			ancestorTokenHierarchy.add(currentTokenHierarchy);
		return ancestorTokenHierarchy;
	}
	public List<List<GrammaticalRelation>> getAncestorGrHierarchies() {
		List<List<GrammaticalRelation>> l = new ArrayList<List<GrammaticalRelation>>();
		getAncestorHierarchies(null, l);
		return l;
	}
	public List<List<Token>> getAncestorTokenHierarchies() {
		List<List<Token>> l = new ArrayList<List<Token>>();
		getAncestorHierarchies(l, null);
		return l;
	}
	public void getAncestorHierarchies(List<List<Token>> ancestorTokens, List<List<GrammaticalRelation>> ancestorGrs) {
		if (ancestorTokens != null)
			ancestorTokens.addAll(getAncestorTokenHierarchy(new ArrayList<Token>()));
		if (ancestorGrs != null) {
			ancestorGrs.addAll(getAncestorGrHierarchy(new ArrayList<GrammaticalRelation>(), new HashSet<Token>()));
		}
	}
	public void getAncestors(List<Token> ancestorTokens, List<GrammaticalRelation> ancestorGrammaticalRelations) {
		List<Token> tokenHierarchy = new ArrayList<Token>();
		List<GrammaticalRelation> grHierarchy = new ArrayList<GrammaticalRelation>();
		Queue<Token> toSearch = new LinkedList<Token>();
		toSearch.add(this);
		do {
			Token next = toSearch.poll();
			tokenHierarchy.add(next);
			List<GrammaticalRelation> deps = next.subtypeOf();
			deps.addAll(next.dependentOf());
			for (GrammaticalRelation gr : deps) {
				if (grHierarchy.contains(gr))
					continue;
				grHierarchy.add(gr);
				Token head = gr.getHead();
				if (!toSearch.contains(head) && !tokenHierarchy.contains(head)) {
					toSearch.offer(head);
				}
			}
			
		} while (!toSearch.isEmpty());
		if (ancestorTokens != null)
			ancestorTokens.addAll(tokenHierarchy);
		if (ancestorGrammaticalRelations != null)
			ancestorGrammaticalRelations.addAll(grHierarchy);
	}
	@Override
	public Token clone() {
		Token c = new Token(lemma, suffix, index, posTag, supertag, word, false); // use 'non-attaching' constructor
		c.grs = grs; // FIXME this should be a clone... but how to avoid an infinite loop? Maybe revise so that Tokens only reference GRs where they are the head?
		return c;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + index;
		result = prime * result + ((lemma == null) ? 0 : lemma.hashCode());
		result = prime * result + ((posTag == null) ? 0 : posTag.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		result = prime * result
				+ ((supertag == null) ? 0 : supertag.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
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
		Token other = (Token) obj;
		if (index != other.index/* && index >= 0*/) // second condition required due to use of dummy negative indexes on null tokens
			return false;
		if (lemma == null) {
			if (other.lemma != null)
				return false;
		} else if (!lemma.equals(other.lemma))
			return false;
		if (posTag == null) {
			if (other.posTag != null)
				return false;
		} else if (!posTag.equals(other.posTag))
			return false;
		if (suffix == null) {
			if (other.suffix != null)
				return false;
		} else if (!suffix.equals(other.suffix))
			return false;
		if (supertag == null) {
			if (other.supertag != null)
				return false;
		} else if (!supertag.equals(other.supertag))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean s = suffix != null && suffix.length() > 0;
		boolean p = posTag != null;
		if (lemma != null && s) {
			sb.append(lemma);
			sb.append('+');
			sb.append(suffix);
		}
		else if (word != null) {
			sb.append(word);
		}
		else if (lemma != null) {
			sb.append(lemma);
		}
		else if (index < 0 && !p) {
			sb.append("NULL");
		}
		
		if (index >= 0) {
			sb.append(':');
			sb.append(index);
		}
		if (p) {
			sb.append('|');
			sb.append(posTag);
		}
		return sb.toString();
	}
	public String describe(boolean terse) { // more verbose than toString - describes supertag if present.
		StringBuilder sb = new StringBuilder();
		boolean s = suffix != null && suffix.length() > 0;
		boolean p = posTag != null;
		boolean u = supertag != null;
		
		boolean described = true; // in the following if-else sequence, do we actually provide real info, or just 'NULL'?
		if (lemma != null && s) {
			if (!terse)
				sb.append("a token with the lemma '");
			sb.append(lemma);
			if (!terse)
				sb.append("' and the suffix '");
			else
				sb.append('+');
			sb.append(suffix);
			if (!terse)
				sb.append("'");
		}
		else if (word != null) {
			if (!terse)
				sb.append("the word '");
			sb.append(word);
			if (!terse)
				sb.append("'");
		}
		else if (lemma != null) {
			if (!terse)
				sb.append("a token with the lemma '");
			sb.append(lemma);
			if (!terse)
				sb.append("'");
		}
		else if (index < 0 && !p && !u) {
			if (terse)
				sb.append("NULL");
			else
				sb.append("a token");
			described = false;
		}
		else if (!terse) {
			sb.append("a token");
			described = false;
		}
		
		List<String> descriptiveItems = Lists.newArrayList();
		
		if (index >= 0) {
			StringBuilder sb2 = new StringBuilder();
			if (terse) {
				sb2.append(':');
				sb2.append(index);
			}
			else {
				sb2.append("occurring as word #");
				sb2.append(index + 1); // 1-index it for normal humans...
			}
			if (!terse)
				sb2.append(" in the sentence");
			descriptiveItems.add(sb2.toString());
		}
		if (p) {
			StringBuilder sb2 = new StringBuilder();
			if (terse)
				sb2.append('|');
			else
				sb2.append("POS tag '");
			sb2.append(posTag);
			if (!terse) {
				sb2.append("' (");
				sb2.append(posTag.description());
				sb2.append(")");
			}
			descriptiveItems.add(sb2.toString());
		}
		if (u) {
			StringBuilder sb2 = new StringBuilder();
			if (terse)
				sb2.append("#");
			else
				sb2.append("C&C supertag '");
			sb2.append(supertag);
			if (!terse)
				sb2.append("'");
			descriptiveItems.add(sb2.toString());
		}
		
		boolean lastIsOccurring = false;
		for (ListIterator<String> li = descriptiveItems.listIterator(); li.hasNext();) {
			boolean prev = li.hasPrevious();
			String item = li.next();
			if (!terse) {
				if (!prev) { // i.e. first element
					if (!item.startsWith("occurring")) {
						if (descriptiveItems.size() > 1) {
							sb.append(", ");
						}
						else {
							sb.append(described ? " and " : " with ");
						}
					}
					else {
						sb.append(", ");
						lastIsOccurring = true;
					}
				}
				else if (lastIsOccurring) {
					sb.append(", with ");
					lastIsOccurring = false;
				}
				else if (!li.hasNext()) { // i.e. last element
					sb.append(" and ");
				}
				else {
					sb.append(", ");
				}
			}
			sb.append(item);
		}
		
		return sb.toString();
	}
	@Override
	public int compareTo(Token t) {
		return index - t.index;
	}
	
	public boolean isVerb() {
		return posTag.descendentOf(VERB_GENERAL);
	}
	
	public boolean isAdjective() {
		return posTag.descendentOf(ADJECTIVE_GENERAL);
	}
	
	public boolean isAdverb() {
		return posTag.descendentOf(ADVERB_GENERAL);
	}
	
	public boolean isNoun() {
		return posTag.descendentOf(NOUN_GENERAL);
	}
	
	public boolean isOpenClass() {
		return isNoun() || isVerb() || isAdjective() || isAdverb();
	}
	
	public boolean isSomebody() {
		return SemanticNounClass.getSemanticNounClass(this).equals(SemanticNounClass.SOMEBODY);
	}
	
	public boolean isSomething() {
		return SemanticNounClass.getSemanticNounClass(this).equals(SemanticNounClass.SOMETHING);
	}
}