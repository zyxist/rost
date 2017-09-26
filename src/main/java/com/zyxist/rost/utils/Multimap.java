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

import java.util.*;

public class Multimap<K, V> {
	private Map<K, List<V>> storage = new LinkedHashMap<>();

	public static <K, V> Multimap<K, V> create() {
		return new Multimap<>();
	}

	public Collection<V> get(K key) {
		List<V> collection = storage.get(key);
		if (null == collection) {
			return Collections.EMPTY_LIST;
		}
		return collection;
	}

	public Multimap<K, V> put(K key, V value) {
		List<V> collection = storage.get(key);
		if (null == collection) {
			collection = new ReferencedList<>(key);
			storage.put(key, collection);
		}
		collection.add(value);
		return this;
	}

	public boolean containsValue(V value) {
		for (List<V> collection : storage.values()) {
			if (collection.contains(value)) {
				return true;
			}
		}
		return false;
	}

	public boolean isEmpty() {
		return storage.isEmpty();
	}

	public int size() {
		int total = 0;
		for (List<V> collection : storage.values()) {
			total += collection.size();
		}
		return total;
	}

	private class ReferencedList<T> extends LinkedList<T> {
		private final K parentRef;

		ReferencedList(K parentRef) {
			super();
			this.parentRef = parentRef;
		}

		@Override
		public Iterator<T> iterator() {
			return new ReferencedIterator<>(parentRef, this, super.iterator());
		}
	}

	private class ReferencedIterator<T> implements Iterator<T> {
		private final Iterator<T> delegate;
		private final List<T> parentList;
		private final K parentRef;

		ReferencedIterator(K parentRef, List<T> parentList, Iterator<T> delegate) {
			this.delegate = delegate;
			this.parentList = parentList;
			this.parentRef = parentRef;
		}

		@Override
		public boolean hasNext() {
			return delegate.hasNext();
		}

		@Override
		public T next() {
			return delegate.next();
		}

		@Override
		public void remove() {
			delegate.remove();
			if (parentList.isEmpty()) {
				storage.remove(parentRef);
			}
		}
	}
}
