package uk.ac.cam.dr369.learngrammar.semantics;

import static uk.ac.cam.dr369.learngrammar.model.GenericPos.ADJECTIVE_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.NOUN_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GenericPos.ARGUMENT_GENERAL;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.AUXILIARY;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.CLAUSAL;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.CLAUSAL_COMPLEMENT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.COMPLEMENT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.DIRECT_OBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.INDIRECT_OBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.NON_CLAUSAL_MODIFIER;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.OBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.PREPOSITIONAL_COMPLEMENT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.SECOND_OBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.SUBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.X_COMPLEMENT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Slot.DEPENDENT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Slot.HEAD;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Slot.SUBTYPE;
import static uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser.POS_INF;
import static uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser.POS_ING;
import static uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser.POS_NON_ING_INF;
import static uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser.SemanticNounClass.SOMEBODY;
import static uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser.SemanticNounClass.SOMETHING;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation;
import uk.ac.cam.dr369.learngrammar.model.Pos;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.FlagSubtype;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Slot;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Subtype;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.TokenSubtype;
import uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser.SemanticNounClass;

//TODO lock down number - e.g. only one complement perhaps?
public enum WordnetVerbFrame implements VerbFrame {
	SOMETHING_VERBS_1(
			"Something ----s",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMETHING),
			new ConstraintImpl(COMPLEMENT, false)
			),
	SOMEBODY_VERBS_2(
			"Somebody ----s",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY),
			new ConstraintImpl(COMPLEMENT, false)
			),
	IT_IS_VERBING_3(
			"It is ----ing",
			POS_ING,
			new ConstraintImpl(SUBJECT, true, "it"),
			new ConstraintImpl(AUXILIARY, true, "be"),
			new ConstraintImpl(COMPLEMENT, false)
			),
	SOMETHING_IS_VERBING_PP_4(
			"Something is ----ing PP",
			POS_ING,
			new ConstraintImpl(SUBJECT, true, SOMETHING),
			new ConstraintImpl(AUXILIARY, true, "be"),
			new OrConstraint(
					new ConstraintImpl(CLAUSAL, true),
					new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, true)
					),
			new ConstraintImpl(X_COMPLEMENT, false, ADJECTIVE_GENERAL),
			new ConstraintImpl(OBJECT, true)
			),
	SOMETHING_VERBS_SOMETHING_ADJNOUN_5(
			"Something ----s something Adjective/Noun",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMETHING),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, true, ADJECTIVE_GENERAL, NOUN_GENERAL)
			),
	SOMETHING_VERBS_ADJNOUN_6(
			"Something ----s Adjective/Noun",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMETHING),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, ADJECTIVE_GENERAL, NOUN_GENERAL),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_ADJECTIVE_7(
			"Somebody ----s Adjective",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY),
			new ConstraintImpl(COMPLEMENT, true, ADJECTIVE_GENERAL),
			new ConstraintImpl(OBJECT, false)
			),
	SOMEBODY_VERBS_SOMETHING_8(
			"Somebody ----s something",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_SOMEBODY_9(
			"Somebody ----s somebody",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMETHING_VERBS_SOMEBODY_10(
			"Something ----s somebody",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMETHING),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMETHING_VERBS_SOMETHING_11(
			"Something ----s something",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMETHING),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMETHING_VERBS_TO_SOMEBODY_12(
			"Something ----s to somebody",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMETHING),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, false),
			new ConstraintImpl(INDIRECT_OBJECT, true, "to", new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL)),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_ON_SOMETHING_13(
			"Somebody ----s on something",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, false),
			new ConstraintImpl(INDIRECT_OBJECT, true, "on", new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/)),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_SOMEBODY_SOMETHING_14(
			"Somebody ----s somebody something",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, true)
			),
	SOMEBODY_VERBS_SOMETHING_TO_SOMEBODY_15(
			"Somebody ----s something to somebody",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING/*, WordnetSemanticAnalyser.POS_NP*/),
			new ConstraintImpl(INDIRECT_OBJECT, true, "to", new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL)),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_SOMETHING_FROM_SOMEBODY_16(
			"Somebody ----s something from somebody",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/),
			new ConstraintImpl(INDIRECT_OBJECT, true, "from", new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL)),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_SOMEBODY_WITH_SOMETHING_17(
			"Somebody ----s somebody with something",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(INDIRECT_OBJECT, true, "with", new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL)),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_SOMEBODY_OF_SOMETHING_18(
			"Somebody ----s somebody of something",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(INDIRECT_OBJECT, true, "of", new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/)),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_SOMETHING_ON_SOMEBODY_19(
			"Somebody ----s something on somebody",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/),
			new ConstraintImpl(INDIRECT_OBJECT, true, "on", new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL)),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_SOMEBODY_PP_20(
			"Somebody ----s somebody PP",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY),
			new OrConstraint(
					new ConstraintImpl(CLAUSAL, true),
					new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, true)
					),
			new ConstraintImpl(X_COMPLEMENT, false, ADJECTIVE_GENERAL),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_SOMETHING_PP_21(
			"Somebody ----s something PP",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY),
			new OrConstraint(
					new ConstraintImpl(CLAUSAL, true),
					new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, true)
					),
			new ConstraintImpl(X_COMPLEMENT, false, ADJECTIVE_GENERAL),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_PP_22(
			"Somebody ----s PP",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(COMPLEMENT, true),
			new ConstraintImpl(X_COMPLEMENT, false, ADJECTIVE_GENERAL),
			new ConstraintImpl(OBJECT, false)
			),
	SOMEBODYS_BODY_PART_VERBS_23(
			"Somebody's (body part) ----s",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, new ConstraintImpl(NON_CLAUSAL_MODIFIER, true, Slot.SUBTYPE, "poss")),
			new ConstraintImpl(COMPLEMENT, false)
			),
	SOMEBODY_VERBS_SOMEBODY_TO_INFINITIVE_24(
			"Somebody ----s somebody to INFINITIVE",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(X_COMPLEMENT, true, SUBTYPE, "to"),
			new ConstraintImpl(X_COMPLEMENT, true, POS_INF),
			new ConstraintImpl(CLAUSAL_COMPLEMENT, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_SOMEBODY_INFINITIVE_25(
			"Somebody ----s somebody INFINITIVE",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, true, POS_INF)
			),
	SOMEBODY_VERBS_THAT_CLAUSE_26(
			"Somebody ----s that CLAUSE",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL_COMPLEMENT, true),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(X_COMPLEMENT, false),
			new ConstraintImpl(OBJECT, false)
			),
	SOMEBODY_VERBS_TO_SOMEBODY_27(
			"Somebody ----s to somebody",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, false),
			new ConstraintImpl(INDIRECT_OBJECT, true, "to", new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL)),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_TO_INFINITIVE_28(
			"Somebody ----s to INFINITIVE",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL_COMPLEMENT, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(X_COMPLEMENT, true, WordnetSemanticAnalyser.POS_INF),
			new ConstraintImpl(X_COMPLEMENT, true, Slot.SUBTYPE, "to"),
			new ConstraintImpl(OBJECT, false)
			),
	SOMEBODY_VERBS_WHETHER_INFINITIVE_29(
			"Somebody ----s whether INFINITIVE",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, false),
			new ConstraintImpl(INDIRECT_OBJECT, true, "whether", new ConstraintImpl(DIRECT_OBJECT, true, WordnetSemanticAnalyser.POS_INF)),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_SOMEBODY_INTO_VERBING_SOMETHING_30(
			"Somebody ----s somebody into V-ing something",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(INDIRECT_OBJECT, true, "into", new ConstraintImpl(
					DIRECT_OBJECT, true, new ConstraintImpl(
							DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL), POS_ING)),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_SOMETHING_WITH_SOMETHING_31(
			"Somebody ----s something with something",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL),
			new ConstraintImpl(INDIRECT_OBJECT, true, "with", new ConstraintImpl(DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL)),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_INFINITIVE_32(
			"Somebody ----s INFINITIVE",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, POS_INF),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	SOMEBODY_VERBS_VERBING_33(
			"Somebody ----s VERB-ing",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, POS_ING),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, false)
			),
	IT_VERBS_THAT_CLAUSE_34(
			"It ----s that CLAUSE",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, "it"),
			new ConstraintImpl(CLAUSAL_COMPLEMENT, true),
			new ConstraintImpl(X_COMPLEMENT, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(OBJECT, false)
			),
	SOMETHING_VERBS_INFINITIVE_35(
			"Something ----s INFINITIVE",
			POS_NON_ING_INF,
			new ConstraintImpl(SUBJECT, true, SOMETHING),
			new ConstraintImpl(CLAUSAL, false),
			new ConstraintImpl(PREPOSITIONAL_COMPLEMENT, false),
			new ConstraintImpl(DIRECT_OBJECT, true, POS_INF),
			new ConstraintImpl(INDIRECT_OBJECT, false),
			new ConstraintImpl(SECOND_OBJECT, false)
			);
	
	private final String description;
	private final Set<Pos> acceptedVerbTypes;
	private final AndConstraint constraints;
	
	WordnetVerbFrame(String description, Pos[] acceptedVerbTypes, Constraint... constraints) {
		this.description = description;
		this.acceptedVerbTypes = Collections.unmodifiableSet(new HashSet<Pos>(Arrays.asList(acceptedVerbTypes)));
		this.constraints = new AndConstraint(constraints);
	}
	
	public static Set<VerbFrame> getAcceptingFrames(Token verb) {
		Set<VerbFrame> frames = new HashSet<VerbFrame>();
		for (VerbFrame frame : values()) {
			if (frame.accept(verb))
				frames.add(frame);
		}
		return frames;
	}

	@Override
	public boolean accept(Token token) {
		return acceptedVerbTypes.contains(token.pos()) && constraints.accept(token);
	}

	public String getDescription() {
		return description;
	}
	
	public String toString() {
		return description;
	}

	public Set<Pos> getAcceptedVerbTypes() {
		return Collections.unmodifiableSet(acceptedVerbTypes);
	}

	public AndConstraint getConstraints() {
		return constraints;
	}
}

interface Constraint {
	public boolean accept(Token token);
}

class OrConstraint implements Constraint {
	private Constraint[] constraints;
	public OrConstraint(Constraint... constraints) {
		this.constraints = constraints;
	}
	public boolean accept(Token token) {
		for (Constraint constraint : constraints)
			if (constraint.accept(token))
				return true;
		return false;
	}
	public String toString() {
		return "Or"+constraints;
	}
}

class AndConstraint implements Constraint {
	private Constraint[] constraints;
	public AndConstraint(Constraint... constraints) {
		this.constraints = constraints;
	}
	public boolean accept(Token token) {
		for (Constraint constraint : constraints)
			if (!constraint.accept(token))
				return false;
		return true;
	}
	public String toString() {
		return "And"+constraints;
	}
}

class ConstraintImpl implements Constraint {
	GrType type;
	Slot slot;
	boolean desired;
	Slot secondaryReq;
	Set<Pos> secondaryPos;
	String secondaryLemma;
	ConstraintImpl secondarySpec;
	private SemanticNounClass semanticNounClass;
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (!desired)
			sb.append("!");
		if (type != null) {
			sb.append("(");
			sb.append(type);
			sb.append(")");
		}
		if (slot != null || secondaryReq != null) {
			if (slot != null)
				sb.append(slot);
			sb.append("->");
			if (secondaryReq != null)
				sb.append(secondaryReq);
		}
		if (secondaryLemma != null) {
			sb.append("<");
			sb.append(secondaryLemma);
			sb.append(">");
		}
		if (semanticNounClass != null) {
			sb.append("/");
			sb.append(semanticNounClass);
		}
		if (secondaryPos != null && !secondaryPos.isEmpty()) {
			sb.append("|");
			sb.append(secondaryPos);
		}
		if (secondarySpec != null) {
			sb.append("{");
			sb.append(secondarySpec.toString());
			sb.append("}");
		}
		return sb.toString();
	}
	public boolean accept(Token token) {
		List<GrammaticalRelation> grs = token.getGrs();
		for (GrammaticalRelation gr : grs) {
			if (!gr.type().descendentOf(type))
				continue;
			if (	(slot.equals(HEAD)      && gr.getHead().equals(token)     ) ||
					(slot.equals(DEPENDENT) && gr.getDependent().equals(token)) ||
					(slot.equals(SUBTYPE)   && gr.getSubtype().equals(token)  )
			   ) {
				if (desired) {
					if (secondaryReq != null) {
						Subtype linkedSubtype; // actually populated with heads and dependents as well...
						if (secondaryReq.equals(HEAD))
							linkedSubtype = new TokenSubtype(gr.getHead());
						else if (secondaryReq.equals(DEPENDENT))
							linkedSubtype = new TokenSubtype(gr.getDependent());
						else if (secondaryReq.equals(SUBTYPE))
							linkedSubtype = gr.getSubtype();
						else
							throw new Error();
						if (linkedSubtype instanceof TokenSubtype) {
							Token linkedToken = ((TokenSubtype) linkedSubtype).token();
							if (semanticNounClass != null) {
								SemanticNounClass actualSemNc = SemanticNounClass.getSemanticNounClass(linkedToken);
								if (!semanticNounClass.equals(actualSemNc))
									return false;
							}
							if (secondaryPos != null && !secondaryPos.isEmpty()) {// && !secondaryPos.contains(linkedToken.getPosTag()))
								boolean found = false;
								for (Pos spos : secondaryPos) {
									if (spos.ancestorOf(linkedToken.pos())) {
										found = true;
										break;
									}
								}
								if (!found)
									return false;
							}
							if (secondaryLemma != null && !secondaryLemma.equals(linkedToken.getLemma()))
								return false;
							if (secondarySpec != null && !secondarySpec.accept(linkedToken))
								return false;
						}
						else { // instanceof Flag
							FlagSubtype linkedFlag = (FlagSubtype) linkedSubtype;
							if (secondaryLemma != null && linkedFlag != null && !secondaryLemma.equals(linkedFlag.flag()))
								return false;
						}
					}
					return true;
				}
				else
					return false;
			}
		}
		return !desired;
	}
	public ConstraintImpl(GrType type, Slot slot, boolean desired, SemanticNounClass semNc) {
		this.type = type;
		this.slot = slot;
		this.desired = desired;
		this.semanticNounClass = semNc;
		this.secondaryReq = DEPENDENT;
	}
	public ConstraintImpl(GrType type, boolean desired) {
		this.type = type;
		this.slot = HEAD;
		this.desired = desired;
	}
	public ConstraintImpl(GrType type, boolean desired, SemanticNounClass semNc) {
		this(type, HEAD, desired, semNc);
	}
	public ConstraintImpl(GrType type, Slot slot, boolean desired, Slot secondaryReq, Pos... secondaryPos) {
		this(type, slot, desired, null);
		this.secondaryReq = secondaryReq;
		this.secondaryPos = new HashSet<Pos>(Arrays.asList(secondaryPos));
	}
	public ConstraintImpl(GrType type, Slot slot, boolean desired, Slot secondaryReq, SemanticNounClass semNc, Pos... secondaryPos) {
		this(type, slot, desired, semNc);
		this.secondaryReq = secondaryReq;
		this.secondaryPos = new HashSet<Pos>(Arrays.asList(secondaryPos));
	}
	public ConstraintImpl(GrType type, Slot slot, boolean desired, Slot secondaryReq, String secondaryLemma, Pos... secondaryPos) {
		this(type, slot, desired, secondaryReq, secondaryPos);
		this.secondaryLemma = secondaryLemma;
	}
	public ConstraintImpl(GrType type, boolean desired, Slot secondaryReq, String secondaryLemma) {
		this(type, HEAD, desired, secondaryReq, secondaryLemma);
	}
	public ConstraintImpl(GrType type, boolean desired, String secondaryLemma) {
		this(type, HEAD, desired, DEPENDENT, secondaryLemma);
	}
	public ConstraintImpl(GrType type, boolean desired, Pos... secondaryPos) {
		this(type, HEAD, desired, DEPENDENT, secondaryPos);
	}
	public ConstraintImpl(GrType type, boolean desired, SemanticNounClass semNc, Pos... secondaryPos) {
		this(type, HEAD, desired, DEPENDENT, semNc, secondaryPos);
	}
	public ConstraintImpl(GrType type, boolean desired, String secondaryLemma, ConstraintImpl secondarySpec) {
		this(type, HEAD, desired, DEPENDENT, secondaryLemma);
		this.secondarySpec = secondarySpec;
	}
	public ConstraintImpl(GrType type, boolean desired, ConstraintImpl secondarySpec, Pos... secondaryPos) {
		this(type, HEAD, desired, DEPENDENT, secondaryPos);
		this.secondarySpec = secondarySpec;
		this.secondaryPos = new HashSet<Pos>(Arrays.asList(secondaryPos));
	}
	public ConstraintImpl(GrType type, boolean desired, ConstraintImpl secondarySpec) {
		this(type, HEAD, desired, DEPENDENT);
		this.secondarySpec = secondarySpec;
	}
	public ConstraintImpl(GrType type, boolean desired, SemanticNounClass semNc, ConstraintImpl secondarySpec) {
		this(type, HEAD, desired, DEPENDENT, semNc);
		this.secondarySpec = secondarySpec;
	}
}