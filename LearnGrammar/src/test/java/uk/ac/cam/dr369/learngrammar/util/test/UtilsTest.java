package uk.ac.cam.dr369.learngrammar.util.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Test;

import uk.ac.cam.dr369.learngrammar.util.Utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

public class UtilsTest {
	private static final ImmutableBiMap<String, String> DETOK_TOK_MAP =
		new ImmutableBiMap.Builder<String, String>()
			.put("I'm a cat!", "I 'm a cat !")
			.put("It's fairly 'important', isn't it?",
				"It 's fairly ' important ' , is n't it ?")
			.build();
	
	@Test
	public void testDetokenise() {
		for (Entry<String, String> e : DETOK_TOK_MAP.entrySet()) {
			assertEquals(e.getKey(), Utils.detokenise(e.getKey()));
		}
	}

	@Test
	public void testTokenise() {
		for (Entry<String, String> e : DETOK_TOK_MAP.inverse().entrySet()) {
			assertEquals(e.getKey(), Utils.tokenise(e.getKey()));
		}
	}

	@Test
	public void testTokeniseSentences() {
		Set<String> expected = ImmutableSet.of("This is a sentence.", "Here is another!",
				"Where will it end?", "I don't know... it's hard to say.");
		Set<String> actual = ImmutableSet.copyOf(Utils.tokeniseSentences("This is a sentence. Here is another! "
				+"Where will it end? I don't know... it's hard to say."));
		assertEquals(expected, actual);
	}

	@Test
	public void testSublistSafe() {
		List<Integer> expected = ImmutableList.of(1, 3, 8, 12);
		List<Integer> actual = Utils.sublistSafe(ImmutableList.copyOf(expected), -20, 300);
		assertEquals(expected, actual);
		
		actual = Utils.sublistSafe(ImmutableList.of(1, 2, 3, 1, 3, 8, 12, 13), 3, 7);
		assertEquals(expected, actual);
	}

	@Test
	public void testEstablishList() {
		Map<String, List<Integer>> foo = Maps.newHashMap();
		Utils.establishList(foo, "hello world").add(3);
		assertEquals(foo.get("hello world"), ImmutableList.of(3));
	}

	@Test
	public void testEstablishSet() {
		Map<String, Set<Integer>> foo = Maps.newHashMap();
		Utils.establishSet(foo, "hello world").add(33);
		assertEquals(foo.get("hello world"), ImmutableSet.of(33));
	}

	@Test
	public void testEstablishMap() {
		Map<String, Map<Integer, String>> foo = Maps.newHashMap();
		
		Utils.establishMap(foo, "hello").put(333, "world");
		assertEquals(foo.get("hello").get(333), "world");
	}

	@Test
	public void testEstablishSetInTable() {
		Table<String, Integer, Set<Integer>> foo = HashBasedTable.create();
		Utils.establishSetInTable(foo, "hello world", 3333).add(-22);
		assertEquals(foo.get("hello world", 3333), ImmutableSet.of(-22));
	}

	@Test
	public void testEstablishListInTable() {
		Table<String, Integer, List<Integer>> foo = HashBasedTable.create();
		Utils.establishListInTable(foo, "hello world", 3333).addAll(ImmutableList.of(4243, 32));
		assertEquals(foo.get("hello world", 3333), ImmutableList.of(4243, 32));
	}

	@Test
	public void testPutSameValue() {
		fail("Not yet implemented");
	}

	@Test
	public void testPutValueInListForKeys() {
		fail("Not yet implemented");
	}

	@Test
	public void testPutValueInSetForKeys() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveFromSetForKeys() {
		fail("Not yet implemented");
	}

	@Test
	public void testList() {
		fail("Not yet implemented");
	}

	@Test
	public void testOneElem() {
		fail("Not yet implemented");
	}

	@Test
	public void testCombine() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnion() {
		fail("Not yet implemented");
	}

	@Test
	public void testIntersection() {
		fail("Not yet implemented");
	}

	@Test
	public void testComplement() {
		fail("Not yet implemented");
	}

	@Test
	public void testPruneMultilevelMap() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeepCopyListOfT() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeepCopySortedSetOfT() {
		fail("Not yet implemented");
	}

	@Test
	public void testInitialCapitalOnly() {
		fail("Not yet implemented");
	}

	@Test
	public void testAllCapitals() {
		fail("Not yet implemented");
	}

	@Test
	public void testAllLowerCase() {
		fail("Not yet implemented");
	}

	@Test
	public void testToInitialCapitalOnly() {
		fail("Not yet implemented");
	}

	@Test
	public void testFormatForPrintingCollectionOfS() {
		fail("Not yet implemented");
	}

	@Test
	public void testFormatForPrintingCollectionOfSInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetGrTypes() {
		fail("Not yet implemented");
	}

	@Test
	public void testUnzip() {
		fail("Not yet implemented");
	}

}
