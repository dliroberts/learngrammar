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
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Slot.SUBTYPE;
import static uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser.POS_INF;
import static uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser.POS_ING;
import static uk.ac.cam.dr369.learngrammar.semantics.WordnetSemanticAnalyser.POS_NON_ING_INF;
import static uk.ac.cam.dr369.learngrammar.semantics.SemanticNounClass.SOMEBODY;
import static uk.ac.cam.dr369.learngrammar.semantics.SemanticNounClass.SOMETHING;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import uk.ac.cam.dr369.learngrammar.model.Pos;
import uk.ac.cam.dr369.learngrammar.model.Token;
import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.Slot;

//TODO lock down number - e.g. only one complement perhaps?

/**
 * WordNet's implementation of verb frames.
 * 
 * @author duncan.roberts
 */
public enum WordnetVerbFrame implements VerbFrame {
	// IDs at ends of names tie in to WordNet's IDs.
	
	SOMETHING_VERBS_1(
			"Something ----s",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMETHING),
			new ConcreteVerbFrameConstraint(COMPLEMENT, false)
		),
	SOMEBODY_VERBS_2(
			"Somebody ----s",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY),
			new ConcreteVerbFrameConstraint(COMPLEMENT, false)
		),
	IT_IS_VERBING_3(
			"It is ----ing",
			POS_ING,
			new ConcreteVerbFrameConstraint(SUBJECT, true, "it"),
			new ConcreteVerbFrameConstraint(AUXILIARY, true, "be"),
			new ConcreteVerbFrameConstraint(COMPLEMENT, false)
		),
	SOMETHING_IS_VERBING_PP_4(
			"Something is ----ing PP",
			POS_ING,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMETHING),
			new ConcreteVerbFrameConstraint(AUXILIARY, true, "be"),
			new OrVerbFrameConstraint(
				new ConcreteVerbFrameConstraint(CLAUSAL, true),
				new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, true)
			),
			new ConcreteVerbFrameConstraint(X_COMPLEMENT, false, ADJECTIVE_GENERAL),
			new ConcreteVerbFrameConstraint(OBJECT, true)
		),
	SOMETHING_VERBS_SOMETHING_ADJNOUN_5(
			"Something ----s something Adjective/Noun",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMETHING),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, true, ADJECTIVE_GENERAL, NOUN_GENERAL)
		),
	SOMETHING_VERBS_ADJNOUN_6(
			"Something ----s Adjective/Noun",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMETHING),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, ADJECTIVE_GENERAL, NOUN_GENERAL),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_ADJECTIVE_7(
			"Somebody ----s Adjective",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY),
			new ConcreteVerbFrameConstraint(COMPLEMENT, true, ADJECTIVE_GENERAL),
			new ConcreteVerbFrameConstraint(OBJECT, false)
		),
	SOMEBODY_VERBS_SOMETHING_8(
			"Somebody ----s something",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_SOMEBODY_9(
			"Somebody ----s somebody",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMETHING_VERBS_SOMEBODY_10(
			"Something ----s somebody",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMETHING),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMETHING_VERBS_SOMETHING_11(
			"Something ----s something",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMETHING),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMETHING_VERBS_TO_SOMEBODY_12(
			"Something ----s to somebody",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMETHING),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, true, "to", new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL)),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_ON_SOMETHING_13(
			"Somebody ----s on something",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, true, "on", new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/)),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_SOMEBODY_SOMETHING_14(
			"Somebody ----s somebody something",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, true)
		),
	SOMEBODY_VERBS_SOMETHING_TO_SOMEBODY_15(
			"Somebody ----s something to somebody",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING/*, WordnetSemanticAnalyser.POS_NP*/),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, true, "to", new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL)),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_SOMETHING_FROM_SOMEBODY_16(
			"Somebody ----s something from somebody",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, true, "from", new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL)),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_SOMEBODY_WITH_SOMETHING_17(
			"Somebody ----s somebody with something",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, true, "with", new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL)),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_SOMEBODY_OF_SOMETHING_18(
			"Somebody ----s somebody of something",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, true, "of", new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/)),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_SOMETHING_ON_SOMEBODY_19(
			"Somebody ----s something on somebody",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, true, "on", new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL)),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_SOMEBODY_PP_20(
			"Somebody ----s somebody PP",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY),
			new OrVerbFrameConstraint(
				new ConcreteVerbFrameConstraint(CLAUSAL, true),
				new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, true)
			),
			new ConcreteVerbFrameConstraint(X_COMPLEMENT, false, ADJECTIVE_GENERAL),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_SOMETHING_PP_21(
			"Somebody ----s something PP",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY),
			new OrVerbFrameConstraint(
				new ConcreteVerbFrameConstraint(CLAUSAL, true),
				new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, true)
			),
			new ConcreteVerbFrameConstraint(X_COMPLEMENT, false, ADJECTIVE_GENERAL),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING/*, NOUN_PHRASE*/),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_PP_22(
			"Somebody ----s PP",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(COMPLEMENT, true),
			new ConcreteVerbFrameConstraint(X_COMPLEMENT, false, ADJECTIVE_GENERAL),
			new ConcreteVerbFrameConstraint(OBJECT, false)
		),
	SOMEBODYS_BODY_PART_VERBS_23(
			"Somebody's (body part) ----s",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, new ConcreteVerbFrameConstraint(NON_CLAUSAL_MODIFIER, true, Slot.SUBTYPE, "poss")),
			new ConcreteVerbFrameConstraint(COMPLEMENT, false)
		),
	SOMEBODY_VERBS_SOMEBODY_TO_INFINITIVE_24(
			"Somebody ----s somebody to INFINITIVE",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(X_COMPLEMENT, true, SUBTYPE, "to"),
			new ConcreteVerbFrameConstraint(X_COMPLEMENT, true, POS_INF),
			new ConcreteVerbFrameConstraint(CLAUSAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_SOMEBODY_INFINITIVE_25(
			"Somebody ----s somebody INFINITIVE",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, true, POS_INF)
		),
	SOMEBODY_VERBS_THAT_CLAUSE_26(
			"Somebody ----s that CLAUSE",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL_COMPLEMENT, true),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(X_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(OBJECT, false)
		),
	SOMEBODY_VERBS_TO_SOMEBODY_27(
			"Somebody ----s to somebody",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, true, "to", new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL)),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_TO_INFINITIVE_28(
			"Somebody ----s to INFINITIVE",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(X_COMPLEMENT, true, WordnetSemanticAnalyser.POS_INF),
			new ConcreteVerbFrameConstraint(X_COMPLEMENT, true, Slot.SUBTYPE, "to"),
			new ConcreteVerbFrameConstraint(OBJECT, false)
		),
	SOMEBODY_VERBS_WHETHER_INFINITIVE_29(
			"Somebody ----s whether INFINITIVE",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, true, "whether", new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, WordnetSemanticAnalyser.POS_INF)),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_SOMEBODY_INTO_VERBING_SOMETHING_30(
			"Somebody ----s somebody into V-ing something",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, true, "into", new ConcreteVerbFrameConstraint(
				DIRECT_OBJECT, true, new ConcreteVerbFrameConstraint(
						DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL), POS_ING)),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_SOMETHING_WITH_SOMETHING_31(
			"Somebody ----s something with something",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, true, "with", new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, SOMETHING, ARGUMENT_GENERAL)),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_INFINITIVE_32(
			"Somebody ----s INFINITIVE",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, POS_INF),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	SOMEBODY_VERBS_VERBING_33(
			"Somebody ----s VERB-ing",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMEBODY, ARGUMENT_GENERAL),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, POS_ING),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		),
	IT_VERBS_THAT_CLAUSE_34(
			"It ----s that CLAUSE",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, "it"),
			new ConcreteVerbFrameConstraint(CLAUSAL_COMPLEMENT, true),
			new ConcreteVerbFrameConstraint(X_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(OBJECT, false)
		),
	SOMETHING_VERBS_INFINITIVE_35(
			"Something ----s INFINITIVE",
			POS_NON_ING_INF,
			new ConcreteVerbFrameConstraint(SUBJECT, true, SOMETHING),
			new ConcreteVerbFrameConstraint(CLAUSAL, false),
			new ConcreteVerbFrameConstraint(PREPOSITIONAL_COMPLEMENT, false),
			new ConcreteVerbFrameConstraint(DIRECT_OBJECT, true, POS_INF),
			new ConcreteVerbFrameConstraint(INDIRECT_OBJECT, false),
			new ConcreteVerbFrameConstraint(SECOND_OBJECT, false)
		);
	
	private final String description;
	private final Set<Pos> acceptedVerbTypes;
	private final AndVerbFrameConstraint constraints;
	
	WordnetVerbFrame(String description, Collection<? extends Pos> acceptedVerbTypes, VerbFrameConstraint... constraints) {
		this.description = description;
		this.acceptedVerbTypes = ImmutableSet.copyOf(acceptedVerbTypes);
		this.constraints = new AndVerbFrameConstraint(constraints);
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

	public AndVerbFrameConstraint getConstraints() {
		return constraints;
	}
}