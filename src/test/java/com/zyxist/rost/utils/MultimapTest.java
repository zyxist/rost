/*
 * Copyright (C) 2017 The Rost Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zyxist.rost.utils;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MultimapTest {
	@Test
	public void shouldInsertMultipleItemsUnderTheSameKey() {
		// Given
		Multimap<String, Integer> mm = Multimap.create();

		// When
		mm.put("a", 1);
		mm.put("a", 2);
		mm.put("b", 3);

		// Then
		assertEquals(3, mm.size());
		assertEquals(List.of(1, 2), mm.get("a"));
		assertEquals(List.of(3), mm.get("b"));
	}

	@Test
	public void shouldCheckWhetherValueExists() {
		// Given
		Multimap<String, Integer> mm = Multimap.create();
		mm.put("a", 1);
		mm.put("a", 2);
		mm.put("b", 3);

		// When
		boolean existsOne = mm.containsValue(1);
		boolean existsThree = mm.containsValue(3);
		boolean existsFour = mm.containsValue(4);

		// Then
		assertTrue(existsOne);
		assertTrue(existsThree);
		assertFalse(existsFour);
	}

	@Test
	public void shouldRemoveValuesThroughIterator() {
		// Given
		Multimap<String, Integer> mm = Multimap.create();
		mm.put("a", 1);
		mm.put("a", 2);
		mm.put("b", 3);

		// When
		removeThroughIterator(mm, "a");

		// Then
		assertEquals(1, mm.size());
		assertEquals(0, mm.get("a").size());
	}

	@Test
	public void shouldReportEmptyMultimapCorrectlyAfterRemoving() {
		// Given
		Multimap<String, Integer> mm = Multimap.create();
		mm.put("a", 1);
		mm.put("a", 2);
		mm.put("a", 3);

		// When
		removeThroughIterator(mm, "a");

		// Then
		assertEquals(0, mm.size());
		assertTrue(mm.isEmpty());
	}

	private void removeThroughIterator(Multimap<String, Integer> mm, String key) {
		Iterator<Integer> it = mm.get(key).iterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}
}
