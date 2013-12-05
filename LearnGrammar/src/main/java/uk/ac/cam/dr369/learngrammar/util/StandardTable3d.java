package uk.ac.cam.dr369.learngrammar.util;
/*
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.UnmodifiableIterator;
*/
public class StandardTable3d<R, C, D, V> implements Table3d<R, C, D, V> {
	
/*	final Map<D, Table<R, C, V>> backingMap;
	final Supplier<? extends Table<R, C, V>> factory;

	StandardTable3d(Map<D, Table<R, C, V>> backingMap,
			Supplier<? extends Table<R, C, V>> factory) {
		this.backingMap = backingMap;
		this.factory = factory;
	}

	@Override
	public boolean contains(Object rowKey, Object columnKey, Object depthKey) {
		if ((rowKey == null) || (columnKey == null) || (depthKey == null)) {
			return false;
		}
		Table<C, D, V> map = safeGet(backingMap, rowKey, depthKey);
		return map != null && safeContainsKey(map, columnKey, depthKey);
	}

	@Override
	public boolean containsColumn(Object columnKey, Object depthKey) {
		if (columnKey == null || depthKey == null) {
			return false;
		}
		for (Table<C, D, V> map : backingMap.values()) {
			if (safeContainsKey(map, columnKey, depthKey)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsRow(Object rowKey, Object depthKey) {
		return rowKey != null && safeContainsKey(backingMap, rowKey, depthKey);
	}
	
//	@Override
//	public boolean containsDepth(Object rowKey, Object columnKey) {
//		return columnKey != null && safeContainsKey(backingMap, rowKey, depthKey);
//	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null) {
			return false;
		}
		for (Table<C, D, V> map : backingMap.values()) {
			if (map.containsValue(value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public V get(Object rowKey, Object columnKey, Object depthKey) {
		if ((rowKey == null) || (columnKey == null) || (depthKey == null)) {
			return null;
		}
		Table<C, D, V> map = safeGet(backingMap, columnKey);
		return map == null ? null : safeGet(map, depthKey);
	}

	@Override
	public boolean isEmpty() {
		return backingMap.isEmpty();
	}

	@Override
	public int size() {
		int size = 0;
		for (Table<C, D, V> map : backingMap.values()) {
			size += map.size();
		}
		return size;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Table3d) {
			Table3d<?, ?, ?> other = (Table3d<?, ?, ?>) obj;
			return cellSet().equals(other.cellSet());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return cellSet().hashCode();
	}

	/**
	 * Returns the string representation {@code rowMap().toString()}.
	 
	@Override
	public String toString() {
		return rowMap().toString();
	}

	// Mutators

	@Override
	public void clear() {
		backingMap.clear();
	}

	private Table<C, D, V> getOrCreate(R rowKey) {
		Table<C, D, V> map = backingMap.get(rowKey);
		if (map == null) {
			map = factory.get();
			backingMap.put(depthKey, map);
		}
		return map;
	}

	@Override
	public V put(R rowKey, C columnKey, D depthKey, V value) {
		checkNotNull(rowKey);
		checkNotNull(columnKey);
		checkNotNull(depthKey);
		checkNotNull(value);
		return getOrCreate(rowKey).put(columnKey, value);
	}

	@Override
	public void putAll(Table3d<? extends R, ? extends C, ? extends D, ? extends V> table) {
		for (Cell<? extends R, ? extends C, ? extends D, ? extends V> cell : table.cellSet()) {
			put(cell.getRowKey(), cell.getColumnKey(), cell.getDepthKey(), cell.getValue());
		}
	}

	@Override
	public V remove(Object rowKey, Object columnKey, Object depthKey) {
		if ((rowKey == null) || (columnKey == null) || (depthKey == null)) {
			return null;
		}
		Table<C, D, V> map = safeGet(backingMap, depthKey);
		if (map == null) {
			return null;
		}
		V value = map.remove(rowKey, columnKey);
		if (map.isEmpty()) {
			backingMap.remove(depthKey);
		}
		return value;
	}

	private Table<R, D, V> removeDepth(Object column) {
		Table<R, C, V> output = new LinkedHashBasedTable<R, C, V>();
		Iterator<Entry<D, Table<C, R, V>>> iterator = backingMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<D, Table<R, C, V>> entry = iterator.next();
			V value = entry.getValue().remove(column);
			if (value != null) {
				output.put(entry.getKey(), value);
				if (entry.getValue().isEmpty()) {
					iterator.remove();
				}
			}
		}
		return output;
	}

	private boolean containsMapping(Object rowKey, Object columnKey,
			Object value) {
		return value != null && value.equals(get(rowKey, columnKey));
	}

	*//** Remove a row key / column key / value mapping, if present. *//*
	private boolean removeMapping(Object rowKey, Object columnKey, Object value) {
		if (containsMapping(rowKey, columnKey, value)) {
			remove(rowKey, columnKey);
			return true;
		}
		return false;
	}

	// Views

	*//**
	 * Abstract collection whose {@code isEmpty()} returns whether the table is
	 * empty and whose {@code clear()} clears all table mappings.
	 *//*
	private abstract class Table3dCollection<T> extends AbstractCollection<T> {
		@Override
		public boolean isEmpty() {
			return backingMap.isEmpty();
		}

		@Override
		public void clear() {
			backingMap.clear();
		}
	}

	*//**
	 * Abstract set whose {@code isEmpty()} returns whether the table is empty
	 * and whose {@code clear()} clears all table mappings.
	 *//*
	private abstract class Table3dSet<T> extends AbstractSet<T> {
		@Override
		public boolean isEmpty() {
			return backingMap.isEmpty();
		}

		@Override
		public void clear() {
			backingMap.clear();
		}
	}

	private transient CellSet cellSet;

	*//**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The set's iterator traverses the mappings for the first row, the mappings
	 * for the second row, and so on.
	 * 
	 * <p>
	 * Each cell is an immutable snapshot of a row key / column key / value
	 * mapping, taken at the time the cell is returned by a method call to the
	 * set or its iterator.
	 *//*
	@Override
	public Set<Cell<R, C, D, V>> cellSet() {
		CellSet result = cellSet;
		return (result == null) ? cellSet = new CellSet() : result;
	}

	private class CellSet extends Table3dSet<Cell<R, C, D, V>> {
		@Override
		public Iterator<Cell<R, C, D, V>> iterator() {
			return new CellIterator();
		}

		@Override
		public int size() {
			return StandardTable3d.this.size();
		}

		@Override
		public boolean contains(Object obj) {
			if (obj instanceof Cell) {
				Cell<?, ?, ?, ?> cell = (Cell<?, ?, ?, ?>) obj;
				return containsMapping(cell.getRowKey(), cell.getColumnKey(), cell.getDepthKey(), cell.getValue());
			}
			return false;
		}

		@Override
		public boolean remove(Object obj) {
			if (obj instanceof Cell) {
				Cell<?, ?, ?> cell = (Cell<?, ?, ?>) obj;
				return removeMapping(cell.getRowKey(), cell.getColumnKey(),
						cell.getValue());
			}
			return false;
		}
	}

	private class CellIterator implements Iterator<Cell<R, C, D, V>> {
		final Iterator<Entry<D, Table<R, C, V>>> rowIterator = backingMap.entrySet().iterator();
		Entry<D, Table<R, C, V>> rowEntry;
		Iterator<Entry<R, C, V>> columnIterator = EMPTY_MODIFIABLE_ITERATOR;

		@Override
		public boolean hasNext() {
			return rowIterator.hasNext() || columnIterator.hasNext() || depthIterator.hasNext();
		}

		@Override
		public Cell<R, C, D, V> next() {
			if (!columnIterator.hasNext()) {
				rowEntry = rowIterator.next();
				columnIterator = rowEntry.getValue().entrySet().iterator();
			}
			Entry<C, V> columnEntry = columnIterator.next();
			return Tables.immutableCell(rowEntry.getKey(),
					columnEntry.getKey(), columnEntry.getValue());
		}

		@Override
		public void remove() {
			columnIterator.remove();
			if (rowEntry.getValue().isEmpty()) {
				rowIterator.remove();
			}
		}
	}

	@Override
	public Table<C, D, V> row(R rowKey) {
		return new Row(rowKey);
	}

	private class Row extends ImprovedAbstractMap<C, V> {		 

		final R rowKey;

		Row(R rowKey) {
			this.rowKey = checkNotNull(rowKey);
		}

//		@Override
		protected Set<Entry<C, V>> createEntrySet() {
			return new RowEntrySet();
		}

//		@Override
		public boolean containsKey(Object key) {
			return contains(rowKey, key);
		}

//		@Override
		public V get(Object key) {
			return StandardTable3d.this.get(rowKey, key);
		}

//		@Override
		public V put(C key, V value) {
			return StandardTable3d.this.put(rowKey, key, value);
		}

//		@Override
		public V remove(Object key) {
			return StandardTable3d.this.remove(rowKey, key);
		}

		private class RowEntrySet extends AbstractSet<Entry<C, V>> {
			@Override
			public void clear() {
				backingMap.remove(rowKey);
			}

			@Override
			public boolean contains(Object o) {
				if (o instanceof Entry) {
					Entry<?, ?> entry = (Entry<?, ?>) o;
					return containsMapping(rowKey, entry.getKey(), entry
							.getValue());
				}
				return false;
			}

			@Override
			public boolean remove(Object o) {
				if (o instanceof Entry) {
					Entry<?, ?> entry = (Entry<?, ?>) o;
					return removeMapping(rowKey, entry.getKey(), entry
							.getValue());
				}
				return false;
			}

			@Override
			public int size() {
				Map<C, V> map = backingMap.get(rowKey);
				return (map == null) ? 0 : map.size();
			}

			@Override
			public Iterator<Entry<C, V>> iterator() {
				final Map<C, V> map = backingMap.get(rowKey);
				if (map == null) {
					return Iterators.emptyModifiableIterator();
				}
				final Iterator<Entry<C, V>> iterator = map.entrySet()
						.iterator();
				return new Iterator<Entry<C, V>>() {
					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public Entry<C, V> next() {
						final Entry<C, V> entry = iterator.next();
						return new ForwardingMapEntry<C, V>() {
							@Override
							protected Entry<C, V> delegate() {
								return entry;
							}

							@Override
							public V setValue(V value) {
								return super.setValue(checkNotNull(value));
							}
						};
					}

					@Override
					public void remove() {
						iterator.remove();
						if (map.isEmpty()) {
							backingMap.remove(rowKey);
						}
					}
				};
			}
		}
	}

	*//**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The returned map's views have iterators that don't support {@code
	 * remove()}.
	 *//*
	@Override
	public Map<R, V> column(C columnKey) {
		return new Column(columnKey);
	}
	  abstract static class ImprovedAbstractMap<K, V> extends AbstractMap<K, V> {
		    *//**
		     * Creates the entry set to be returned by {@link #entrySet()}. This method
		     * is invoked at most once on a given map, at the time when {@code entrySet}
		     * is first called.
		     *//*
		    protected abstract Set<Entry<K, V>> createEntrySet();

		    private Set<Entry<K, V>> entrySet;

		    @Override public Set<Entry<K, V>> entrySet() {
		      Set<Entry<K, V>> result = entrySet;
		      if (result == null) {
		        entrySet = result = createEntrySet();
		      }
		      return result;
		    }

		    private Set<K> keySet;

		    @Override public Set<K> keySet() {
		      Set<K> result = keySet;
		      if (result == null) {
		        final Set<K> delegate = super.keySet();
		        keySet = result = new ForwardingSet<K>() {
		          @Override protected Set<K> delegate() {
		            return delegate;
		          }

		          @Override public boolean isEmpty() {
		            return ImprovedAbstractMap.this.isEmpty();
		          }

		          @Override public boolean remove(Object object) {
		            if (contains(object)) {
		              ImprovedAbstractMap.this.remove(object);
		              return true;
		            }
		            return false;
		          }
		        };
		      }
		      return result;
		    }

		    private Collection<V> values;

		    @Override public Collection<V> values() {
		      Collection<V> result = values;
		      if (result == null) {
		        final Collection<V> delegate = super.values();
		        values = result = new ForwardingCollection<V>() {
		          @Override protected Collection<V> delegate() {
		            return delegate;
		          }

		          @Override public boolean isEmpty() {
		            return ImprovedAbstractMap.this.isEmpty();
		          }
		        };
		      }
		      return result;
		    }

		    *//**
		     * Returns {@code true} if this map contains no key-value mappings.
		     *
		     * <p>The implementation returns {@code entrySet().isEmpty()}.
		     *
		     * @return {@code true} if this map contains no key-value mappings
		     *//*
		    @Override public boolean isEmpty() {
		      return entrySet().isEmpty();
		    }
		  }

	private class Column extends ImprovedAbstractMap<R, V> {
		final C columnKey;

		Column(C columnKey) {
			this.columnKey = checkNotNull(columnKey);
		}

		@Override
		public V put(R key, V value) {
			return StandardTable3d.this.put(key, columnKey, value);
		}

		@Override
		public V get(Object key) {
			return StandardTable3d.this.get(key, columnKey);
		}

		@Override
		public boolean containsKey(Object key) {
			return StandardTable3d.this.contains(key, columnKey);
		}

		@Override
		public V remove(Object key) {
			return StandardTable3d.this.remove(key, columnKey);
		}

		@Override
		public Set<Entry<R, V>> createEntrySet() {
			return new EntrySet();
		}

		Values columnValues;

//		@Override
		public Collection<V> values() {
			Values result = columnValues;
			return (result == null) ? columnValues = new Values() : result;
		}

		*//**
		 * Removes all {@code Column} mappings whose row key and value satisfy
		 * the given predicate.
		 *//*
		boolean removePredicate(Predicate<? super Entry<R, V>> predicate) {
			boolean changed = false;
			Iterator<Entry<D, Table<R, C, V>>> iterator = backingMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<R, Map<C, V>> entry = iterator.next();
				Map<C, V> map = entry.getValue();
				V value = map.get(columnKey);
				if (value != null
						&& predicate.apply(new ImmutableEntry<R, V>(entry
								.getKey(), value))) {
					map.remove(columnKey);
					changed = true;
					if (map.isEmpty()) {
						iterator.remove();
					}
				}
			}
			return changed;
		}

		class EntrySet extends AbstractSet<Entry<R, V>> {
			@Override
			public Iterator<Entry<R, V>> iterator() {
				return new EntrySetIterator();
			}

			@Override
			public int size() {
				int size = 0;
				for (Table<R, C, V> map : backingMap.values()) {
					if (map.containsKey(columnKey)) {
						size++;
					}
				}
				return size;
			}

			@Override
			public boolean isEmpty() {
				return !containsColumn(columnKey);
			}

			@Override
			public void clear() {
				Predicate<Entry<R, V>> predicate = Predicates.alwaysTrue();
				removePredicate(predicate);
			}

			@Override
			public boolean contains(Object o) {
				if (o instanceof Entry) {
					Entry<?, ?> entry = (Entry<?, ?>) o;
					return containsMapping(entry.getKey(), columnKey, entry
							.getValue());
				}
				return false;
			}

			@Override
			public boolean remove(Object obj) {
				if (obj instanceof Entry) {
					Entry<?, ?> entry = (Entry<?, ?>) obj;
					return removeMapping(entry.getKey(), columnKey, entry.getValue());
				}
				return false;
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				boolean changed = false;
				for (Object obj : c) {
					changed |= remove(obj);
				}
				return changed;
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				return removePredicate(Predicates.not(Predicates.in(c)));
			}
		}

		class EntrySetIterator extends AbstractIterator<Entry<D, V>> {
			final Iterator<Entry<D, Table<R, C, V>>> iterator = backingMap.entrySet().iterator();

			@Override
			protected Entry<R, V> computeNext() {
				while (iterator.hasNext()) {
					final Entry<R, Map<C, V>> entry = iterator.next();
					if (entry.getValue().containsKey(columnKey)) {
						return new AbstractMapEntry<R, V>() {
							@Override
							public R getKey() {
								return entry.getKey();
							}

							@Override
							public V getValue() {
								return entry.getValue().get(columnKey);
							}

							@Override
							public V setValue(V value) {
								return entry.getValue().put(columnKey,
										checkNotNull(value));
							}
						};
					}
				}
				return endOfData();
			}
		}

		KeySet keySet;

		@Override
		public Set<R> keySet() {
			KeySet result = keySet;
			return result == null ? keySet = new KeySet() : result;
		}

		class KeySet extends AbstractSet<R> {
			@Override
			public Iterator<R> iterator() {
				return keyIteratorImpl(Column.this);
			}

			@Override
			public int size() {
				return entrySet().size();
			}

			@Override
			public boolean isEmpty() {
				return !containsColumn(columnKey);
			}

			@Override
			public boolean contains(Object obj) {
				return StandardTable3d.this.contains(obj, columnKey);
			}

			@Override
			public boolean remove(Object obj) {
				return StandardTable3d.this.remove(obj, columnKey) != null;
			}

			@Override
			public void clear() {
				entrySet().clear();
			}

			@Override
			public boolean removeAll(final Collection<?> c) {
				boolean changed = false;
				for (Object obj : c) {
					changed |= remove(obj);
				}
				return changed;
			}

			@Override
			public boolean retainAll(final Collection<?> c) {
				checkNotNull(c);
				Predicate<Entry<R, V>> predicate = new Predicate<Entry<R, V>>() {
					@Override
					public boolean apply(Entry<R, V> entry) {
						return !c.contains(entry.getKey());
					}
				};
				return removePredicate(predicate);
			}
		}

		class Values extends AbstractCollection<V> {
			@Override
			public Iterator<V> iterator() {
				return valueIteratorImpl(Column.this);
			}

			@Override
			public int size() {
				return entrySet().size();
			}

			@Override
			public boolean isEmpty() {
				return !containsColumn(columnKey);
			}

			@Override
			public void clear() {
				entrySet().clear();
			}

			@Override
			public boolean remove(Object obj) {
				if (obj == null) {
					return false;
				}
				Iterator<Map<C, V>> iterator = backingMap.values().iterator();
				while (iterator.hasNext()) {
					Map<C, V> map = iterator.next();
					if (map.entrySet().remove(
							new ImmutableEntry<C, Object>(columnKey, obj))) {
						if (map.isEmpty()) {
							iterator.remove();
						}
						return true;
					}
				}
				return false;
			}

			@Override
			public boolean removeAll(final Collection<?> c) {
				checkNotNull(c);
				Predicate<Entry<R, V>> predicate = new Predicate<Entry<R, V>>() {
					@Override
					public boolean apply(Entry<R, V> entry) {
						return c.contains(entry.getValue());
					}
				};
				return removePredicate(predicate);
			}

			@Override
			public boolean retainAll(final Collection<?> c) {
				checkNotNull(c);
				Predicate<Entry<R, V>> predicate = new Predicate<Entry<R, V>>() {
					@Override
					public boolean apply(Entry<R, V> entry) {
						return !c.contains(entry.getValue());
					}
				};
				return removePredicate(predicate);
			}
		}
	}

	private transient RowKeySet rowKeySet;

	@Override
	public Set<R> rowKeySet() {
		Set<R> result = rowKeySet;
		return (result == null) ? rowKeySet = new RowKeySet() : result;
	}

	class DepthKeySet extends Table3dSet<D> {
		@Override
		public Iterator<D> iterator() {
			return keyIteratorImpl(depthMap());
		}

		@Override
		public int size() {
			return backingMap.size();
		}

		@Override
		public boolean contains(Object obj) {
			return containsDepth(obj);
		}

		@Override
		public boolean remove(Object obj) {
			return (obj != null) && backingMap.remove(obj) != null;
		}
	}

	private transient Set<C> columnKeySet;

	*//**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The returned set has an iterator that does not support {@code remove()}.
	 * 
	 * <p>
	 * The set's iterator traverses the columns of the first row, the columns of
	 * the second row, etc., skipping any columns that have appeared previously.
	 *//*
	@Override
	public Set<C> columnKeySet() {
		Set<C> result = columnKeySet;
		return (result == null) ? columnKeySet = new DepthKeySet() : result;
	}

	private class ColumnKeySet extends Table3dSet<C> {
		@Override
		public Iterator<C> iterator() {
			return createColumnKeyIterator();
		}

		@Override
		public int size() {
			return Iterators.size(iterator());
		}

		@Override
		public boolean remove(Object obj) {
			if (obj == null) {
				return false;
			}
			boolean changed = false;
			Iterator<Map<C, V>> iterator = backingMap.values().iterator();
			while (iterator.hasNext()) {
				Map<C, V> map = iterator.next();
				if (map.keySet().remove(obj)) {
					changed = true;
					if (map.isEmpty()) {
						iterator.remove();
					}
				}
			}
			return changed;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			checkNotNull(c);
			boolean changed = false;
			Iterator<Map<C, V>> iterator = backingMap.values().iterator();
			while (iterator.hasNext()) {
				Map<C, V> map = iterator.next();
				// map.keySet().removeAll(c) can throw a NPE when map is a
				// TreeMap with
				// natural ordering and c contains a null.
				if (Iterators.removeAll(map.keySet().iterator(), c)) {
					changed = true;
					if (map.isEmpty()) {
						iterator.remove();
					}
				}
			}
			return changed;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			checkNotNull(c);
			boolean changed = false;
			Iterator<Map<C, V>> iterator = backingMap.values().iterator();
			while (iterator.hasNext()) {
				Map<C, V> map = iterator.next();
				if (map.keySet().retainAll(c)) {
					changed = true;
					if (map.isEmpty()) {
						iterator.remove();
					}
				}
			}
			return changed;
		}

		@Override
		public boolean contains(Object obj) {
			if (obj == null) {
				return false;
			}
			for (Map<C, V> map : backingMap.values()) {
				if (map.containsKey(obj)) {
					return true;
				}
			}
			return false;
		}
	}

	*//**
	 * Creates an iterator that returns each column value with duplicates
	 * omitted.
	 *//*
	Iterator<C> createColumnKeyIterator() {
		return new ColumnKeyIterator();
	}

	private class ColumnKeyIterator extends AbstractIterator<C> {
		// Use the same map type to support TreeMaps with comparators that
		// aren't consistent with equals().
		final Map<C, V> seen = factory.get();
		final Iterator<Map<C, V>> mapIterator = backingMap.values().iterator();
		Iterator<Entry<C, V>> entryIterator = Iterators.emptyIterator();

		@Override
		protected C computeNext() {
			while (true) {
				if (entryIterator.hasNext()) {
					Entry<C, V> entry = entryIterator.next();
					if (!seen.containsKey(entry.getKey())) {
						seen.put(entry.getKey(), entry.getValue());
						return entry.getKey();
					}
				} else if (mapIterator.hasNext()) {
					entryIterator = mapIterator.next().entrySet().iterator();
				} else {
					return endOfData();
				}
			}
		}
	}

	private transient Values values;

	*//**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The collection's iterator traverses the values for the first row, the
	 * values for the second row, and so on.
	 *//*
	@Override
	public Collection<V> values() {
		Values result = values;
		return (result == null) ? values = new Values() : result;
	}

	private class Values extends Table3dCollection<V> {
		@Override
		public Iterator<V> iterator() {
			final Iterator<Cell<R, C, V>> cellIterator = cellSet().iterator();
			return new Iterator<V>() {
				@Override
				public boolean hasNext() {
					return cellIterator.hasNext();
				}

				@Override
				public V next() {
					return cellIterator.next().getValue();
				}

				@Override
				public void remove() {
					cellIterator.remove();
				}
			};
		}

		@Override
		public int size() {
			return StandardTable3d.this.size();
		}
	}

	private transient RowMap rowMap;

	@Override
	public Map<D, Table<R, C, V>> rowMap() {
		RowMap result = rowMap;
		return (result == null) ? rowMap = new RowMap() : result;
	}

	class RowMap extends ImprovedAbstractMap<R, Map<C, V>> {
		@Override
		public boolean containsKey(Object key) {
			return containsRow(key);
		}

		// performing cast only when key is in backing map and has the correct
		// type
		@SuppressWarnings("unchecked")
		@Override
		public Map<C, V> get(Object key) {
			return containsRow(key) ? row((R) key) : null;
		}

		@Override
		public Set<R> keySet() {
			return rowKeySet();
		}

		@Override
		public Map<C, V> remove(Object key) {
			return (key == null) ? null : backingMap.remove(key);
		}

		@Override
		protected Set<Entry<R, Map<C, V>>> createEntrySet() {
			return new EntrySet();
		}

		class EntrySet extends Table3dSet<Entry<D, Table<R, C, V>>> {
			@Override
			public Iterator<Entry<D, Table<R, C, V>>> iterator() {
				return new EntryIterator();
			}

			@Override
			public int size() {
				return backingMap.size();
			}

			@Override
			public boolean contains(Object obj) {
				if (obj instanceof Entry) {
					Entry<?, ?> entry = (Entry<?, ?>) obj;
					return entry.getKey() != null
							&& entry.getValue() instanceof Map
							&& Collections2.safeContains(backingMap.entrySet(), entry);
				}
				return false;
			}

			@Override
			public boolean remove(Object obj) {
				if (obj instanceof Entry) {
					Entry<?, ?> entry = (Entry<?, ?>) obj;
					return entry.getKey() != null
							&& entry.getValue() instanceof Map
							&& backingMap.entrySet().remove(entry);
				}
				return false;
			}
		}

		class EntryIterator implements Iterator<Entry<D, Table<R, C, V>>> {
			final Iterator<D> delegate = backingMap.keySet().iterator();

			@Override
			public boolean hasNext() {
				return delegate.hasNext();
			}

			@Override
			public Entry<D, Table<R, C, V>> next() {
				D depthKey = delegate.next();
				return new ImmutableEntry<D, Table<R, C, V>>(depthKey, depth(depthKey));
			}

			@Override
			public void remove() {
				delegate.remove();
			}
		}
	}

	private transient ColumnMap columnMap;

	@Override
	public Map<D, Table<R, C, V>> depthMap() {
		ColumnMap result = columnMap;
		return (result == null) ? columnMap = new ColumnMap() : result;
	}

	private class ColumnMap extends ImprovedAbstractMap<C, Map<R, V>> {
		// The cast to C occurs only when the key is in the map, implying that
		// it
		// has the correct type.
		@SuppressWarnings("unchecked")
		@Override
		public Map<R, V> get(Object key) {
			return containsColumn(key) ? column((C) key) : null;
		}

		@Override
		public boolean containsKey(Object key) {
			return containsColumn(key);
		}

		@Override
		public Map<R, V> remove(Object key) {
			return containsColumn(key) ? removeColumn(key) : null;
		}

		@Override
		public Set<Entry<C, Map<R, V>>> createEntrySet() {
			return new ColumnMapEntrySet();
		}

		@Override
		public Set<C> keySet() {
			return columnKeySet();
		}

		ColumnMapValues columnMapValues;

		@Override
		public Collection<Map<R, V>> values() {
			ColumnMapValues result = columnMapValues;
			return (result == null) ? columnMapValues = new ColumnMapValues()
					: result;
		}

		class ColumnMapEntrySet extends Table3dSet<Entry<C, Map<R, V>>> {
			@Override
			public Iterator<Entry<C, Map<R, V>>> iterator() {
				final Iterator<C> columnIterator = columnKeySet().iterator();
				return new UnmodifiableIterator<Entry<C, Map<R, V>>>() {
					@Override
					public boolean hasNext() {
						return columnIterator.hasNext();
					}

					@Override
					public Entry<C, Map<R, V>> next() {
						C columnKey = columnIterator.next();
						return new ImmutableEntry<C, Map<R, V>>(columnKey,
								column(columnKey));
					}
				};
			}

			@Override
			public int size() {
				return columnKeySet().size();
			}

			@Override
			public boolean contains(Object obj) {
				if (obj instanceof Entry) {
					Entry<?, ?> entry = (Entry<?, ?>) obj;
					if (containsColumn(entry.getKey())) {
						// The cast to C occurs only when the key is in the map,
						// implying
						// that it has the correct type.
						@SuppressWarnings("unchecked")
						C columnKey = (C) entry.getKey();
						return get(columnKey).equals(entry.getValue());
					}
				}
				return false;
			}

			@Override
			public boolean remove(Object obj) {
				if (contains(obj)) {
					Entry<?, ?> entry = (Entry<?, ?>) obj;
					removeColumn(entry.getKey());
					return true;
				}
				return false;
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				boolean changed = false;
				for (Object obj : c) {
					changed |= remove(obj);
				}
				return changed;
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				boolean changed = false;
				for (C columnKey : Lists.newArrayList(columnKeySet().iterator())) {
					if (!c.contains(new ImmutableEntry<C, Map<R, V>>(columnKey,
							column(columnKey)))) {
						removeColumn(columnKey);
						changed = true;
					}
				}
				return changed;
			}
		}

		private class ColumnMapValues extends Table3dCollection<Table<R, C, V>> {
			@Override
			public Iterator<Table<R, C, V>> iterator() {
				return valueIteratorImpl(DepthMap.this);
			}

			@Override
			public boolean remove(Object obj) {
				for (Entry<C, Map<R, V>> entry : ColumnMap.this.entrySet()) {
					if (entry.getValue().equals(obj)) {
						removeColumn(entry.getKey());
						return true;
					}
				}
				return false;
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				checkNotNull(c);
				boolean changed = false;
				for (C columnKey : Lists
						.newArrayList(columnKeySet().iterator())) {
					if (c.contains(column(columnKey))) {
						removeColumn(columnKey);
						changed = true;
					}
				}
				return changed;
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				checkNotNull(c);
				boolean changed = false;
				for (C columnKey : Lists
						.newArrayList(columnKeySet().iterator())) {
					if (!c.contains(column(columnKey))) {
						removeColumn(columnKey);
						changed = true;
					}
				}
				return changed;
			}

			@Override
			public int size() {
				return columnKeySet().size();
			}
		}
	}

	private static final long serialVersionUID = 0;

	*//**
	 * Generates the iterator of a map's key set from the map's entry set
	 * iterator.
	 *//*
	static <K, V> Iterator<K> keyIteratorImpl(Map<K, V> map) {
		final Iterator<Entry<K, V>> entryIterator = map.entrySet().iterator();
		return new Iterator<K>() {
			@Override
			public boolean hasNext() {
				return entryIterator.hasNext();
			}

			@Override
			public K next() {
				return entryIterator.next().getKey();
			}

			@Override
			public void remove() {
				entryIterator.remove();
			}
		};
	}

	*//**
	 * Generates the iterator of a map's value collection from the map's entry
	 * set iterator.
	 *//*
	static <K, V> Iterator<V> valueIteratorImpl(Map<K, V> map) {
		final Iterator<Entry<K, V>> entryIterator = map.entrySet().iterator();
		return new Iterator<V>() {
			@Override
			public boolean hasNext() {
				return entryIterator.hasNext();
			}

			@Override
			public V next() {
				return entryIterator.next().getValue();
			}

			@Override
			public void remove() {
				entryIterator.remove();
			}
		};
	}

	private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR = new Iterator<Object>() {
		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Object next() {
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new IllegalStateException();
		}
	};

	class ImmutableEntry<K, V> extends AbstractMapEntry<K, V> implements
			Serializable {
		private final K key;
		private final V value;

		ImmutableEntry(K key, V value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public final V setValue(V value) {
			throw new UnsupportedOperationException();
		}

		private static final long serialVersionUID = 0;
	}
	abstract class AbstractMapEntry<K, V> implements Entry<K, V> {

		  @Override
		  public abstract K getKey();

		  @Override
		  public abstract V getValue();

		  @Override
		  public V setValue(V value) {
		    throw new UnsupportedOperationException();
		  }

		  @Override public boolean equals(Object object) {
		    if (object instanceof Entry) {
		      Entry<?, ?> that = (Entry<?, ?>) object;
		      return Objects.equal(this.getKey(), that.getKey())
		          && Objects.equal(this.getValue(), that.getValue());
		    }
		    return false;
		  }

		  @Override public int hashCode() {
		    K k = getKey();
		    V v = getValue();
		    return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
		  }

		  *//**
		   * Returns a string representation of the form <code>{key}={value}</code>.
		   *//*
		  @Override public String toString() {
		    return getKey() + "=" + getValue();
		  }
	}
	static <V> V safeGet(Map<?, V> map, Object key) {
		try {
			return map.get(key);
		} catch (ClassCastException e) {
			return null;
		}
	}

	static boolean safeContainsKey(Map<?, ?> map, Object key) {
		try {
			return map.containsKey(key);
		} catch (ClassCastException e) {
			return false;
		}
	}*/
}