package uk.ac.cam.dr369.learngrammar.commonality;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.Pos;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Subtype;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.TokenSubtype;
import uk.ac.cam.dr369.learngrammar.parsing.DependencyStructure;
import uk.ac.cam.dr369.learngrammar.util.Utils;

public class Twig extends DependencyStructure implements Iterable<PathItem> {
	private static final long serialVersionUID = -4862089177272984180L;
	private final FeatureProfile profile;
	private final double weight;
	private final Token leaf;
	
	public Twig(List<GrammaticalRelation> grs, List<Token> tokens, FeatureProfile profile) {
		this(grs, tokens, true, profile);
	}
	public Twig(List<GrammaticalRelation> grs, List<Token> tokens, boolean initialise, FeatureProfile profile) {
		super(grs, tokens, initialise);
		this.profile = profile;
		if (initialise) {
			double weightTmp = 1d;
			for (GrammaticalRelation gr : grs) {
				GrType grt = gr.type();
				weightTmp *= grt == null ? 1d : grt.weight();
			}
			for (Token tok : tokens) {
				Pos p = tok.pos();
				weightTmp *= p == null ? 1d : p.weight();
			}
			// weight should be with respect to 'genericness' of constituents, not to its size. Features determine relationship between
			// size of a twig and its score.
			weight = weightTmp;// / ((double) grs.size() + tokens.size());
			leaf = initLeaf(getTokens(false));
		}
		else {
			leaf = null;
			weight = -1;
		}
	}
	public FeatureProfile profile() {
		return profile;
	}
	@Override
	public int compareTo(DependencyStructure ds) {
		if (ds instanceof Twig) {
			Twig dpds = (Twig) ds;
			return toString().compareTo(dpds.toString());
		}
		else
			return super.compareTo(ds);
	}
	public boolean subsumes(Twig other) {
		if (other == null)
			return false;
		// read only, so safe to not take copies
		List<Token> otherToks = other.getTokens(false);
		List<GrammaticalRelation> otherGrs = other.getGrs(false);
		List<Token> thisToks = getTokens(false);
		List<GrammaticalRelation> thisGrs = getGrs(false);
		
		if (otherToks.size() != thisToks.size())
			return false;
		if (otherGrs.size() != thisGrs.size())
			return false;

		Iterator<PathItem> thIt = iterator();
		Iterator<PathItem> otIt = other.iterator();
		
		for (int i = 1; thIt.hasNext(); i++) {
			if (i % 2 == 0) { // GR level
				GrammaticalRelation thisGr = thIt.next().gr();
				GrammaticalRelation otherGr = otIt.next().gr();
				GrType thisGrType = thisGr.type();
				GrType otherGrType = otherGr.type();
				if (!(thisGrType == null && otherGrType == null)
						&& (thisGrType == null || otherGrType == null || !thisGrType.ancestorOf(otherGrType)))
					return false;
			}
			else { // token level
				Token thisToken = thIt.next().token();
				Token otherToken = otIt.next().token();
				Pos thisPos = thisToken.pos();
				Pos otherPos = otherToken.pos();
				if (!(thisPos == null && otherPos == null)
						&& (thisPos == null || otherPos == null || !thisPos.ancestorOf(otherPos)))
					return false;
				String thisLemma = thisToken.getLemma();
				String otherLemma = otherToken.getLemma();
				if (thisLemma == null && otherLemma != null)
					return false;
				if (thisLemma != null && otherLemma == null)
					return false;
				if (thisLemma != null && otherLemma != null && !thisLemma.equals(otherLemma))
					return false;
				String thisSupertag = thisToken.getSupertag();
				String otherSupertag = otherToken.getSupertag();
				if (thisSupertag == null && otherSupertag != null)
					return false;
				if (thisSupertag != null && otherSupertag == null)
					return false;
				if (thisSupertag != null && otherSupertag != null && !thisSupertag.equals(otherSupertag))
					return false;
			}
		}
		
		return true;
	}
	public String toString() {
		return describe(true); //+ " ("+describe(false)+")";
	}
	public String describe(boolean terse) {
		boolean grs = profile == null ? true : profile.grs();
		boolean tokens = profile == null ? true : profile.tokens();
		StringBuilder sb = new StringBuilder();
		Token leaf;
		if (getGrs().isEmpty() && getTokens().isEmpty())
			return "...empty...";
		try {
			leaf = getLeaf();
		} catch (NullPointerException e) { // occurs for uninitialised structs only - which we only call toString() on in debug. Only solution: initialise.
			return new Twig(getGrs(), getTokens(), profile).toString();
		}
		int height = getGrs().size() + getTokens().size();
		
		Token lastToken = leaf;
		GrammaticalRelation lastGr = null;
		
		if (tokens && lastToken != null) {
			describeToken(terse, sb, lastToken);
		}
		
		boolean containsNull = false;
		if (grs && !terse) {
			for (GrammaticalRelation gr : getGrs(false)) {
				if (gr.type() == null) {
					containsNull = true;
					break;
				}
			}
		}
		if (tokens && !terse) {
			for (Token tok : getTokens(false)) {
				if (tok.describe(true) == null || tok.describe(true).equalsIgnoreCase("NULL")) {
					containsNull = true;
					break;
				}
			}
		}
		
		for (int i = 0; i < height-1; i++) {
			if (i % 2 == 0) { // even, so add GR
				List<GrammaticalRelation> l = lastToken.childOf(); // FIXME can trigger null ptr exceptions...
				if (l.size() > 1)
					throw new IllegalArgumentException(lastToken+" has more than 1 parent GR. Method called on invalid data structure.");
				if (l.size() == 0)
					throw new IllegalArgumentException(lastToken+" has 0 parent GRs. Height set too high.");
				lastGr = l.get(0);
				if (grs) {
					if (!terse) {
						if (i < height - 2 || !containsNull) {
							if (tokens)
								sb.append(" which is a ");
							else {
								if (i > 0) {
									sb.append(" grammatically linked to ");
								}
								sb.append("a ");
							}
						}
						else {
							if (tokens)
								sb.append(" which is the head of the sentence");
							else
								sb.append(" whose head is the head of the sentence");
						}
					}
					
					if (terse || i < height - 2 || !containsNull)
						describeGr(terse, sb, lastGr, lastToken);
				}
			}
			else { // odd, so add token
				lastToken = lastGr.getHead();
				if (tokens && lastToken != null) {
					if (!terse) {
						if (i < height - 2 || !containsNull) {
							if (grs)
								sb.append(" of ");
							else
								sb.append(" grammatically linked to ");
						}
						else {
							sb.append(" which is the head of the sentence");
						}
					}
					if (terse || i < height - 2 || !containsNull)
						describeToken(terse, sb, lastToken);
				}
			}
		}
		return sb.toString();
	}
	private static void describeToken(boolean terse, StringBuilder target, Token token) {
		if (terse)
			target.append("<");
		target.append(token.describe(terse).replace("NULL", "null"));
		if (terse)
			target.append(">");
	}
	private static void describeGr(boolean terse, StringBuilder target, GrammaticalRelation gr, Token lastToken) {
		if (terse) {
			if (gr.getSubtype() instanceof TokenSubtype && ((TokenSubtype) gr.getSubtype()).token().equals(lastToken))
				target.append("s");
			target.append("(");
				target.append(gr.type());
			target.append(")");
		}
		else if (gr.type() != null) {
			target.append(gr.type().description());
		}
		else {
			// TODO what to put here?
		}
	}
	public double getScore() {
		return profile.score(this);
	}
//	public static List<Twig> substructuresOf(List<Twig> dses, int height) {
//		List<Twig> subs = new ArrayList<Twig>();
//		for (Twig ds : dses) {
//			subs.add(ds.substructure(height));
//		}
//		return subs;
//	}
//	/** Returns graph equivalent of a sublist by finding lowest (not head of any GR) token node (must be exactly 1 such node!),
//	 *  and constructing a DependencyStructure that contains that node, and the height-1 (GRs+tokens) above it. For instance,
//	 *  with a height of 3, one is returned the lowest token, the GR that it's a dependent/subtype of, and the token that is the
//	 *  head of that GR.
//	 *  @throws IllegalArgumentException if <code>height</code> is larger than height of ds.
//	 */
//	public Twig substructure(int height) {
//		Token leaf = getLeaf();
//	
//		Token lastToken = leaf;
//		GrammaticalRelation lastGr = null;
//		List<Token> tokens = new ArrayList<Token>();
//		List<GrammaticalRelation> grs = new ArrayList<GrammaticalRelation>();
//		tokens.add(leaf);
//		
//		for (int i = 0; i < height - 1; i++) { // height-1 because we start with 1 token already
//			if (i % 2 == 0) { // even, so add GR
//				List<GrammaticalRelation> l = lastToken.childOf();
//				if (l.size() > 1)
//					throw new IllegalArgumentException(lastToken+" has more than 1 parent GR. Method called on invalid data structure.");
//				if (l.size() == 0)
//					throw new IllegalArgumentException(lastToken+" has 0 parent GRs. Height set too high.");
//				lastGr = l.get(0);
//				grs.add(lastGr);
//			}
//			else { // odd, so add token
//				lastToken = lastGr.getHead();
//				tokens.add(lastToken);
//			}
//		}
//		if (height % 2 == 0) { // where height is even, link top GR to a null token
//			int lastIdx = grs.size() - 1;
//			GrammaticalRelation top = grs.get(lastIdx);
//			grs.set(lastIdx, new GrammaticalRelation(top.type(), top.getSubtype(), top.getInitialGrValue(), null, top.getDependent()));
//		}
//		return new Twig(Utils.deepCopy(grs), Utils.deepCopy(tokens), profile());
//	}
	private Token initLeaf(List<Token> tokens) {
		Token leaf = null;
		for (Token candidate : getTokens()) {
			if (candidate.headOf().isEmpty()) {
				if (leaf == null)
					leaf = candidate;
				else
					throw new IllegalArgumentException("ds has more than 1 leaf token.");
			}
		}
		if (leaf == null) {
			return tokens.iterator().next(); // Must contain a cycle. In which case, any token is just as valid
		}
		return leaf;
	}
	public Token getLeaf() {
		return leaf;
	}
	private static List<Twig> supertypesOf(Twig struct, FeatureProfile profile, int height) {
		List<Twig> variants = new ArrayList<Twig>();
		variants.add(struct);
		if (profile.grTypes()) {
			for (GrammaticalRelation gr : struct.getGrs()) {
				GrType t = gr.type();
				if (t != null) {
					List<Twig> v2 = new ArrayList<Twig>();
					Set<GrType> ancestorGrTypes = t.ancestors();
					for (GrType ancestorGrType : ancestorGrTypes) {
						GrammaticalRelation amendedGr =
							new GrammaticalRelation(ancestorGrType, gr.getSubtype(), gr.getInitialGrValue(), gr.getHead(), gr.getDependent());
						for (Twig variant : variants) {
							List<GrammaticalRelation> amendedGrs = variant.getGrs();
							amendedGrs.remove(gr);
							amendedGrs.add(amendedGr);
							Twig amendedDs = new Twig(amendedGrs, variant.getTokens(), false, profile);
							v2.add(amendedDs);
						}
					}
					variants = v2;
				}
			}
		} // TODO remove 'dependent' GR type instances? a bit pointless...
		if (profile.pos()) {
			for (Token tok : struct.getTokens()) {
				Pos p = tok.pos();
				if (p != null) {
					List<Twig> v2 = new ArrayList<Twig>();
					Set<Pos> ancestors = p.ancestors();
					for (Pos ancestor : ancestors) {
						Token amendedToken =
							new Token(tok.getLemma(), tok.getSuffix(), tok.getIndex(), ancestor, tok.getSupertag(), tok.getWord(), tok.getVerbFrame());
						for (Twig variant : variants) {
							List<GrammaticalRelation> amendedGrs = variant.getGrs();
							List<Token> amendedTokens = variant.getTokens();
							amendedTokens.remove(tok);
							amendedTokens.add(amendedToken);
							for (ListIterator<GrammaticalRelation> it = amendedGrs.listIterator(); it.hasNext();) { // update refs
								GrammaticalRelation gr = it.next();
								Token h = gr.getHead();
								Token d = gr.getDependent();
								Subtype s = gr.getSubtype();
								if (h != null && h.equals(tok)) {
									it.set(new GrammaticalRelation(gr.type(), s, gr.getInitialGrValue(), amendedToken, gr.getDependent()));
								}
								else if (d != null && d.equals(tok)) {
									it.set(new GrammaticalRelation(gr.type(), s, gr.getInitialGrValue(), gr.getHead(), amendedToken));
								}
								else if (s instanceof TokenSubtype) {
									Token st = ((TokenSubtype) s).token();
									if (st != null && st.equals(tok)) {
										it.set(new GrammaticalRelation(gr.type(),
												new TokenSubtype(amendedToken), gr.getInitialGrValue(), gr.getHead(), gr.getDependent()));
									}
								}
							}
							Twig amendedDs = new Twig(amendedGrs, amendedTokens, false, profile);
							v2.add(amendedDs);
						}
					}
					variants = v2;
				}
			}
		}
		return variants;
	}
	public static List<Twig> getPartialStructures(Token token, FeatureProfile profile, int height) {
		return getPartialStructures(token, profile, height, true);
	}
	public static List<Twig> getPartialStructures(Token token, FeatureProfile profile, int height, boolean supertypes) {
		Token usChild = underspecify(token, profile, -height);
		
		List<Twig> dses =
			getPartialStructureForToken(token, usChild, profile, height - 1, height); // -1 to discount current token
		
		List<Twig> ds2 = new ArrayList<Twig>();
		if (supertypes && profile.recurseHierarchy()) {
			for (Twig dpds : dses) {
				ds2.addAll(supertypesOf(dpds, profile, height));
			}
		}
		else {
			ds2 = dses;
		}
		
		// Each GR and token must be a clone so they can be attached together in the DS constructor
		for (ListIterator<Twig> li = ds2.listIterator(); li.hasNext();) {
			Twig ds = li.next();
			ds = new Twig(ds.getGrs(), ds.getTokens(), profile);
			li.set(ds);
		}
		return ds2;
	}
	private static List<Twig> getPartialStructureForToken(Token token, Token usChild, FeatureProfile profile, int height, int origHeight) {
		if (height < 0)
			throw new IllegalArgumentException("Height must be greater than zero.");
		
		List<Twig> dses = new ArrayList<Twig>();
		if (height == 0) {
			Twig ds = new Twig(new ArrayList<GrammaticalRelation>(), Utils.oneElem(usChild), false, profile);
			dses.add(ds);
			return dses;
		}
		List<GrammaticalRelation> dependentOf = token.dependentOf();
		for (GrammaticalRelation parentGr : dependentOf) {
			getPartialStructureForGr(dses, usChild, parentGr, profile, height, origHeight, false);
		}
		List<GrammaticalRelation> subtypeOf = token.subtypeOf();
		for (GrammaticalRelation parentGr : subtypeOf) {
			getPartialStructureForGr(dses, usChild, parentGr, profile, height, origHeight, true);
		}
		if ((Utils.union(subtypeOf, dependentOf)).isEmpty()) {
			// no parents, but still want a deeper structure as height > 0. So link together some spacer tokens.
			if (height == 1 && profile.grs() && !token.headOf().isEmpty() &&
					((!profile.tokens() && origHeight > 2) || profile.tokens())) { // generate GR (top=GR)
				GrammaticalRelation gr = new GrammaticalRelation(null, null, "Null-"+height, null, usChild);
				List<Token> t = Utils.oneElem(usChild);
				List<GrammaticalRelation> g = Utils.oneElem(gr);
				dses.add(new Twig(g, t, false, profile));
			}
			// generate GR and its parent token (top=token). Skip over tokens not included in GR graph such as punctuation.
			else if (height == 2 && profile.tokens() && !profile.grs() && !token.getGrs().isEmpty()) {
				Token tok = new Token("NULL", null, -height, null, null, null);
				GrammaticalRelation gr = new GrammaticalRelation(null, null, "Null-"+height, tok, usChild);
				List<Token> t = Utils.list(usChild, tok);
				List<GrammaticalRelation> g = Utils.oneElem(gr);
				dses.add(new Twig(g, t, false, profile));
			}
			// If neither of these conditions is met, this is fine: when we iterate using a lower height, we'll hit one or the other.
		}
		return dses;
	}
	private static void getPartialStructureForGr(List<Twig> dses, Token usChild, GrammaticalRelation parentGr,
			FeatureProfile profile, int height, int origHeight, boolean subtype) {
		if (height == 1) {
			GrammaticalRelation usGr = underspecify(parentGr, profile, null, subtype ? new TokenSubtype(usChild) : null, subtype ? null : usChild);
			Twig ds = new Twig(Utils.oneElem(usGr), Utils.oneElem(usChild), false, profile);
			dses.add(ds);
		}
		else {
			Token parentTok = parentGr.getHead();
			Token usParent = underspecify(parentTok, profile, -height);
			GrammaticalRelation usGr = underspecify(parentGr, profile, usParent, subtype ? new TokenSubtype(usChild) : null, subtype ? null : usChild);
			if (height == 2) {
				Twig ds = new Twig(Utils.oneElem(usGr), Utils.list(usChild, usParent), false, profile);
				dses.add(ds);
			}
			else if (height > 2) { // recurse!
				List<Twig> parentDses = getPartialStructureForToken(parentTok, usParent, profile, height - 2, origHeight);
				for (Twig pds : parentDses) {
					List<Token> t = Utils.combine(pds.getTokens(), Utils.oneElem(usChild));
					List<GrammaticalRelation> g = Utils.combine(pds.getGrs(), Utils.oneElem(usGr));
					Twig n = new Twig(g, t, false, profile);
					dses.add(n);
				}
			} // TODO see if this breaks for height==7, with multiple heads at every level
		}
	}
	public double weight() {
		return weight;
	}
	public Iterator<PathItem> iterator() {
		return new PathIterator();
	}
	public PathItem get(int i) {
		return new PathIterator(i).next();
	}
	private static Token underspecify(Token fullToken, FeatureProfile profile, int index) {
		return new Token(profile.lemmas() ? fullToken.getLemma() : null, null, index,
						profile.pos() ? fullToken.pos() : null, profile.supertags() ? fullToken.getSupertag() : null, null);
	}
	private static GrammaticalRelation underspecify(
			GrammaticalRelation fullGr, FeatureProfile profile, Token usHead, Subtype usSubtype, Token usDependent) {
		return new GrammaticalRelation(
				profile.grTypes() ? fullGr.type() : null,
				usSubtype,
				profile.initialGrValues() ? fullGr.getInitialGrValue() : null,
				usHead,
				usDependent);
	}
	
	class PathIterator implements Iterator<PathItem> {
		private Token token;
		private GrammaticalRelation gr;
		private int idx;
		public PathIterator(int idx) {
			this.idx = 1;
			for (int i = 1; i < idx; i++) {
				next();
			}
		}
		public PathIterator() {
			this(1);
		}
		@Override
		public boolean hasNext() {
			if (idx % 2 == 0) { // GR level
				return token != null && token.childOf() != null && !token.childOf().isEmpty();
			}
			else { // token level
				return idx > 1 ? (gr != null && gr.getHead() != null) : getLeaf() != null;
			}
		}
		@Override
		public PathItem next() {
			int i = idx++;
			if (i % 2 == 0) { // GR level
				gr = token.childOf().get(0);
				return new PathItem(gr);
			}
			else { // token level
				token = i > 1 ? gr.getHead() : getLeaf();
				return new PathItem(token);
			}
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}