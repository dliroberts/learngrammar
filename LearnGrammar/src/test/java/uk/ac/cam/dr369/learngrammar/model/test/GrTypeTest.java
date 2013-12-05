package uk.ac.cam.dr369.learngrammar.model.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.ARGUMENT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.ARGUMENT_MODIFIER;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.CLAUSAL;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.CLAUSAL_MODIFIER;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.CLAUSAL_SUBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.COMPLEMENT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.DETERMINER;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.DIRECT_OBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.INDIRECT_OBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.MODIFIER;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.NON_CLAUSAL_MODIFIER;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.NON_CLAUSAL_SUBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.OBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.PREPOSITIONAL_COMPLEMENT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.SUBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.SUBJECT_DIRECT_OBJECT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.X_COMPLEMENT;
import static uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType.X_MODIFIER;

import java.util.Set;

import org.junit.Test;

import uk.ac.cam.dr369.learngrammar.model.GrammaticalRelation.GrType;

import com.google.common.collect.ImmutableSet;

public class GrTypeTest {
	@Test
	public void testDescendentOf() {
		assertFalse(NON_CLAUSAL_SUBJECT.descendentOf(COMPLEMENT));

		assertTrue(PREPOSITIONAL_COMPLEMENT.descendentOf(COMPLEMENT));
		assertFalse(PREPOSITIONAL_COMPLEMENT.ancestorOf(GrType.CLAUSAL));

		assertTrue(NON_CLAUSAL_MODIFIER.descendentOf(ARGUMENT_MODIFIER));
		assertTrue(CLAUSAL_MODIFIER.descendentOf(MODIFIER));

		assertTrue(OBJECT.descendentOf(COMPLEMENT));
		assertTrue(INDIRECT_OBJECT.descendentOf(ARGUMENT));
		assertFalse(DIRECT_OBJECT.ancestorOf(INDIRECT_OBJECT));
		assertFalse(DIRECT_OBJECT.descendentOf(INDIRECT_OBJECT));
		assertFalse(INDIRECT_OBJECT.ancestorOf(DIRECT_OBJECT));
		assertFalse(INDIRECT_OBJECT.descendentOf(DIRECT_OBJECT));
		assertTrue(DIRECT_OBJECT.descendentOf(SUBJECT_DIRECT_OBJECT));
		assertTrue(DIRECT_OBJECT.descendentOf(ARGUMENT_MODIFIER));
		assertFalse(INDIRECT_OBJECT.descendentOf(SUBJECT_DIRECT_OBJECT));
	}

	@Test
	public void testAncestorOf() {
		assertTrue(ARGUMENT_MODIFIER.ancestorOf(ARGUMENT));
		assertFalse(NON_CLAUSAL_SUBJECT.ancestorOf(COMPLEMENT));
		assertTrue(SUBJECT.ancestorOf(NON_CLAUSAL_SUBJECT));
		assertTrue(ARGUMENT.ancestorOf(SUBJECT));
		assertFalse(INDIRECT_OBJECT.ancestorOf(DIRECT_OBJECT));
	}

	@Test
	public void testAncestors() {
		Set<GrType> a = ImmutableSet.of(SUBJECT_DIRECT_OBJECT, ARGUMENT,
				ARGUMENT_MODIFIER, SUBJECT);
		Set<GrType> b = SUBJECT.ancestors();
		assertEquals(a, b);

		a = ImmutableSet.of(CLAUSAL, COMPLEMENT, ARGUMENT, ARGUMENT_MODIFIER,
				X_COMPLEMENT);
		b = X_COMPLEMENT.ancestors();
		assertEquals(a, b);

		a = ImmutableSet.of(OBJECT, COMPLEMENT, ARGUMENT, ARGUMENT_MODIFIER,
				SUBJECT_DIRECT_OBJECT, DIRECT_OBJECT);
		b = DIRECT_OBJECT.ancestors();
		assertEquals(a, b);

		a = ImmutableSet.of(SUBJECT, SUBJECT_DIRECT_OBJECT, ARGUMENT,
				ARGUMENT_MODIFIER, CLAUSAL_SUBJECT);
		b = CLAUSAL_SUBJECT.ancestors();
		assertEquals(a, b);

	}

	@Test
	public void testGetLabel() {
		assertEquals(SUBJECT.getLabel(), "subj");
		assertEquals(NON_CLAUSAL_SUBJECT.getLabel(), "ncsubj");
		assertEquals(CLAUSAL_MODIFIER.getLabel(), "cmod");
	}

	@Test
	public void testValueOfByLabel() {
		assertEquals(GrType.valueOfByLabel("det"), DETERMINER);
		assertEquals(GrType.valueOfByLabel("comp"), COMPLEMENT);
		assertEquals(GrType.valueOfByLabel("iobj"), INDIRECT_OBJECT);
		assertEquals(GrType.valueOfByLabel("xmod"), X_MODIFIER);
	}

	@Test
	public void testWeight() {
		assertEquals(DETERMINER.weight(), 1d, 0d);
		assertEquals(NON_CLAUSAL_MODIFIER.weight(), 1d, 0d);
		assertNotSame(CLAUSAL, 1d);
	}

	@Test
	public void testParents() {
		Set<GrType> a = ImmutableSet.of(SUBJECT_DIRECT_OBJECT, ARGUMENT);
		Set<GrType> b = ImmutableSet.copyOf(SUBJECT.parents());
		assertEquals(a, b);

		a = ImmutableSet.of(CLAUSAL);
		b = ImmutableSet.copyOf(X_COMPLEMENT.parents());
		assertEquals(a, b);

		a = ImmutableSet.of(OBJECT, SUBJECT_DIRECT_OBJECT);
		b = ImmutableSet.copyOf(DIRECT_OBJECT.parents());
		assertEquals(a, b);

		a = ImmutableSet.of(SUBJECT);
		b = ImmutableSet.copyOf(CLAUSAL_SUBJECT.parents());
		assertEquals(a, b);
	}

	@Test
	public void testToString() {
		assertEquals(SUBJECT.toString(), "subj");
		assertEquals(NON_CLAUSAL_SUBJECT.toString(), "ncsubj");
		assertEquals(CLAUSAL_MODIFIER.toString(), "cmod");
	}
}