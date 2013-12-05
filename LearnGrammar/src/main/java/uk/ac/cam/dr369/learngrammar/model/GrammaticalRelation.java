package uk.ac.cam.dr369.learngrammar.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.ac.cam.dr369.learngrammar.util.Utils.VeryCloneable;
import uk.ac.cam.dr369.learngrammar.util.Utils.Wrapper;

public class GrammaticalRelation implements VeryCloneable<GrammaticalRelation>, Comparable<GrammaticalRelation>, Serializable {
	private static final long serialVersionUID = -7401562800190577461L;

	private static final List<String> SUBTYPEABLE_GRS = Arrays.asList(new String[] {"cmod", "ncmod", "xmod", "xcomp", "pcomp", "ta"});

	private final GrType type;
	private final Subtype subtype;
	private final Token head;
	private final Token dependent;
	private final String initialGrValue;
	public GrammaticalRelation(GrType type, Subtype subtype,
			String initialGrValue, Token head, Token dependent) {
		super();
		this.type = type;
		this.subtype = subtype;
		this.initialGrValue = initialGrValue == null ? "" : initialGrValue;
		this.head = head;
		this.dependent = dependent;
	}
	public GrType type() {
		return type;
	}
	public Subtype getSubtype() {
		return subtype;
	}
	public String getInitialGrValue() {
		return initialGrValue;
	}
	public Token getHead() {
		return head;
	}
	public Token getDependent() {
		return dependent;
	}
	/**
	 * Depth-first search for all dependent tokens.
	 * 
	 * @param includeSubtypeTokens
	 * @return
	 */
	public SortedSet<Token> getDependentTokens(boolean includeSubtypeTokens) {
		Wrapper<SortedSet<Token>> wrapper = new Wrapper<SortedSet<Token>>();
		getDependents(includeSubtypeTokens, wrapper, null);
		return wrapper.wrapped;
	}
	/**
	 * Depth-first search for all dependent tokens.
	 * 
	 * @param includeSubtypeTokens
	 * @return
	 */
	public Set<GrammaticalRelation> getDependentGrs(boolean includeSubtypeTokens) {
		Wrapper<Set<GrammaticalRelation>> wrapper = new Wrapper<Set<GrammaticalRelation>>();
		getDependents(includeSubtypeTokens, null, wrapper);
		return wrapper.wrapped;
	}
	private void getDependents(boolean includeSubtypes, Wrapper<SortedSet<Token>> returnTokens, Wrapper<Set<GrammaticalRelation>> returnGrs) {
		Set<GrammaticalRelation> searchedGrs = new HashSet<GrammaticalRelation>();
		SortedSet<Token> searchedTokens = new TreeSet<Token>();
		Queue<Token> toSearch = new LinkedList<Token>();
		toSearch.offer(dependent);
		if (includeSubtypes && subtype instanceof TokenSubtype)
			toSearch.offer(((TokenSubtype) subtype).token());
		do {
			Token t = toSearch.poll();
			searchedTokens.add(t);
			List<GrammaticalRelation> grs = t.getGrs();
			for (GrammaticalRelation gr : grs) {
				if (!searchedGrs.contains(gr)) {
					searchedGrs.add(gr);
					if (!searchedTokens.contains(gr.dependent) && !toSearch.contains(gr.dependent)) {
						toSearch.offer(gr.dependent);
					}
					if (includeSubtypes && gr.subtype instanceof TokenSubtype
							&& !searchedTokens.contains(((TokenSubtype) gr.subtype).token()) && !toSearch.contains(((TokenSubtype) gr.subtype).token())) {
						Token st = ((TokenSubtype) gr.subtype).token();
						toSearch.offer(st);
					}
				}
			}
		} while (!toSearch.isEmpty());
		if (returnTokens != null)
			returnTokens.wrapped = searchedTokens;
		if (returnGrs != null)
			returnGrs.wrapped = searchedGrs;
	}
	@Override
	public GrammaticalRelation clone() {
		return new GrammaticalRelation(
			type,
			(subtype == null ? null : subtype.clone()),
			initialGrValue,
			(head == null ? null : head.clone()),
			(dependent == null ? null : dependent.clone()));
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dependent == null) ? 0 : dependent.hashCode());
		result = prime * result + ((head == null) ? 0 : head.hashCode());
		result = prime * result
				+ ((initialGrValue == null) ? 0 : initialGrValue.hashCode());
		result = prime * result + ((subtype == null) ? 0 : subtype.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		GrammaticalRelation other = (GrammaticalRelation) obj;
		if (dependent == null) {
			if (other.dependent != null)
				return false;
		} else if (!dependent.equals(other.dependent))
			return false;
		if (head == null) {
			if (other.head != null)
				return false;
		} else if (!head.equals(other.head))
			return false;
		if (initialGrValue == null) {
			if (other.initialGrValue != null)
				return false;
		} else if (!initialGrValue.equals(other.initialGrValue))
			return false;
		if (subtype == null) {
			if (other.subtype != null)
				return false;
		} else if (!subtype.equals(other.subtype))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
	@Override
	public String toString() {
		if (type == null)
			return '('+initialGrValue
			+ ' ' + head + ' '
			+ (subtype == null ? "" : "s//"+subtype)
			+ (dependent == null ? "" : dependent)
			+ ')';
		String subtypeStr;
		if (subtype == null)
			subtypeStr = SUBTYPEABLE_GRS.contains(type) ? " _" : "";
		else
			subtypeStr = ' ' + subtype.toString();
		return '('
				+ (type == null ? null : type.getLabel())
				+ subtypeStr
				+ ' ' + head
				+ ' ' + dependent
				+ (initialGrValue != null && initialGrValue.length() > 0 ? ' ' + initialGrValue : "")
				+ ')'/*+"@"+Integer.toHexString(super.hashCode())*/;
	}
	public interface Subtype extends VeryCloneable<Subtype>, Serializable {
		
	}
	public static class TokenSubtype implements Subtype {
		private static final long serialVersionUID = 7979620473425271144L;
		private final Token token;
		public TokenSubtype(Token token) {
			super();
			this.token = token;
		}
		@Override
		public String toString() {
			return token.toString();
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((token == null) ? 0 : token.hashCode());
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
			TokenSubtype other = (TokenSubtype) obj;
			if (token == null) {
				if (other.token != null)
					return false;
			} else if (!token.equals(other.token))
				return false;
			return true;
		}
		public Token token() {
			return token;
		}
		@Override
		public TokenSubtype clone() {
			return new TokenSubtype(token.clone());
		}
	}
	public static class FlagSubtype implements Subtype {
		private static final long serialVersionUID = -8489235381709790828L;
		private final String flag;
		public FlagSubtype(String flag) {
			this.flag = flag;
		}
		public String flag() {
			return flag;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((flag == null) ? 0 : flag.hashCode());
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
			FlagSubtype other = (FlagSubtype) obj;
			if (flag == null) {
				if (other.flag != null)
					return false;
			} else if (!flag.equals(other.flag))
				return false;
			return true;
		}
		@Override
		public String toString() {
			return flag;
		}
		@Override
		public FlagSubtype clone() {
			return new FlagSubtype(flag);
		}
	}
	
	/* Used in compareTo */
	private List<Integer> getTokenIndices() {
		List<Integer> l = new ArrayList<Integer>();
		if (subtype != null && subtype instanceof TokenSubtype) {
			Token st = ((TokenSubtype) subtype).token();
			l.add(st.getIndex());
		}
		if (head != null)
			l.add(head.getIndex());
		if (dependent != null)
			l.add(dependent.getIndex());
		Collections.sort(l);
		return l;
	}
	@Override
	public int compareTo(GrammaticalRelation o) {
		List<Integer> tList = getTokenIndices();
		List<Integer> oList = o.getTokenIndices();
		for (ListIterator<Integer> tLi = tList.listIterator(); tLi.hasNext();) {
			int tMin = tLi.next();
			for (ListIterator<Integer> oLi = oList.listIterator(); oLi.hasNext();) {
				int oMin = oLi.next();
				if (tMin != oMin) {
					return tMin - oMin;
				}
				tLi.remove();
				oLi.remove();
			}
		}
		return 0;
	}
	public enum Slot {
		HEAD, DEPENDENT, SUBTYPE
	}
	
	public enum GrType {
//		DEPENDENT               ("dependent"), // programmatically, what's the use of DEPENDENT?
		TEXT_ADJUNCT            ("ta",        1,   "text adjunct (e.g. punctuation)"/*,   DEPENDENT*/),
		ARGUMENT_MODIFIER       ("arg_mod",   .5d, "argument or modifier"/*, DEPENDENT*/),
		DETERMINER              ("det",       1,   "determiner"/*,   DEPENDENT*/),
		AUXILIARY               ("aux",       1,   "auxiliary"/*,   DEPENDENT*/),
		CONJUNCTION             ("conj",      1,   "conjunction"/*,   DEPENDENT*/),
		MODIFIER                ("mod",       .8d, "modifier", ARGUMENT_MODIFIER),
		ARGUMENT                ("arg",       .6d, "argument", ARGUMENT_MODIFIER),
		NON_CLAUSAL_MODIFIER    ("ncmod",     1,   "non-clausal modifier", MODIFIER),
		X_MODIFIER              ("xmod",      1,   "unsaturated predicative modifier", MODIFIER),
		CLAUSAL_MODIFIER        ("cmod",      1,   "clausal modifier", MODIFIER),
		PREPOSITIONAL_MODIFIER  ("pmod",      1,   "prepositional phrase modifier", MODIFIER),
		SUBJECT_DIRECT_OBJECT   ("subj_dobj", .8d, "subject or direct object", ARGUMENT),
		SUBJECT                 ("subj",      .8d, "subject", ARGUMENT, SUBJECT_DIRECT_OBJECT),
		COMPLEMENT              ("comp",      .7d, "complement", ARGUMENT),
		NON_CLAUSAL_SUBJECT     ("ncsubj",    1,   "non-clausal subject", SUBJECT),
		X_SUBJECT               ("xsubj",     1,   "unsaturated predicative subject", SUBJECT),
		CLAUSAL_SUBJECT         ("csubj",     1,   "clausal subject", SUBJECT),
		OBJECT                  ("obj",       .8d, "object", COMPLEMENT),
		PREPOSITIONAL_COMPLEMENT("pcomp",     1,   "prepositional phrase complement", COMPLEMENT),
		CLAUSAL                 ("clausal",   .8d, "clausal or verb phrase complement", COMPLEMENT),
		DIRECT_OBJECT           ("dobj",      1,   "direct object", OBJECT, SUBJECT_DIRECT_OBJECT),
		SECOND_OBJECT           ("obj2",      1,   "second object", OBJECT),
		INDIRECT_OBJECT         ("iobj",      1,   "indirect object", OBJECT),
		X_COMPLEMENT            ("xcomp",     1,   "unsaturated verb phrase complement", CLAUSAL),
		CLAUSAL_COMPLEMENT      ("ccomp",     1,   "clausal complement", CLAUSAL),
		PASSIVE                 ("passive",   1,   "passive"); // not used by C&C...
		
		private final String label;
		private final double weight;
		private final GrType[] parents;
		private final String description;
		
		GrType(String label, double weight, String description, GrType... parents) {
			this.parents = parents;
			this.label = label;
			this.weight = weight;
			this.description = description;
		}
		public boolean descendentOf(GrType parent) {
			Queue<GrType> toSearch = new LinkedList<GrType>();
			toSearch.offer(this);
			GrType current = null;
			do {
				current = toSearch.poll();
				if (current.equals(parent))
					return true;
				toSearch.addAll(Arrays.asList(current.parents));
			} while (!toSearch.isEmpty());
			return false;
		}
		public boolean ancestorOf(GrType child) {
			return child.descendentOf(this);
		}
		public Set<GrType> ancestors() {
			Set<GrType> ancestors = new HashSet<GrType>();
			Queue<GrType> toSearch = new LinkedList<GrType>();
			toSearch.offer(this);
			GrType current = null;
			do {
				current = toSearch.poll();
				ancestors.add(current);
				toSearch.addAll(Arrays.asList(current.parents));
			} while (!toSearch.isEmpty());
			return ancestors;
		}
		public String getLabel() {
			return label;
		}
		public String description() {
			return description;
		}
		public static GrType valueOfByLabel(String label) {
			for (GrType grt : values()) {
				if (grt.label.equals(label))
					return grt;
			}
			throw new IllegalArgumentException("No enum const class "+GrType.class+" instance with label="+label);
		}
		public double weight() {
			return weight;
		}
		public List<GrType> parents() {
			return Collections.unmodifiableList(Arrays.asList(parents));
		}
		@Override
		public String toString() {
			return label;
		}
	}
}