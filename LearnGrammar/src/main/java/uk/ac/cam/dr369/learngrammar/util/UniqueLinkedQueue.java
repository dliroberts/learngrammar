package uk.ac.cam.dr369.learngrammar.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A FIFO queue without duplicate elements. Performance of add(E) is O(n), so this
 * class is not recommended for large queues.
 * 
 * @author Duncan Roberts
 *
 * @param <E> the type of elements held in this collection
 */
public class UniqueLinkedQueue<E> implements Queue<E> {
	private final Queue<E> set = new LinkedList<E>();

	@Override
	public boolean add(E e) {
		if (set.contains(e))
			return false;
		return set.add(e);
	}

	@Override
	public E element() {
		return set.element();
	}

	@Override
	public boolean offer(E e) {
		return set.offer(e);
	}

	@Override
	public E peek() {
		return set.peek();
	}

	@Override
	public E poll() {
		return set.poll();
	}

	@Override
	public E remove() {
//		E e = element();
//		set.remove(e);
//		return e;
		return set.remove();
	}

	@Override
	public boolean addAll(Collection<? extends E> es) {
		boolean changed = false;
		for (E e : es) {
			changed |= add(e);
		}
		return changed;
	}

	@Override
	public void clear() {
		set.clear();
	}

	@Override
	public boolean contains(Object o) {
		return set.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> o) {
		return set.containsAll(o);
	}

	@Override
	public boolean isEmpty() {
		return set.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return set.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return set.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> o) {
		return set.removeAll(o);
	}

	@Override
	public boolean retainAll(Collection<?> o) {
		return set.retainAll(o);
	}

	@Override
	public int size() {
		return set.size();
	}

	@Override
	public Object[] toArray() {
		return set.toArray();
	}

	@Override
	public <T> T[] toArray(T[] t) {
		return set.toArray(t);
	}
	
	@Override
	public String toString() {
		return set.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((set == null) ? 0 : set.hashCode());
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
		UniqueLinkedQueue<?> other = (UniqueLinkedQueue<?>) obj;
		if (set == null) {
			if (other.set != null)
				return false;
		} else if (!set.equals(other.set))
			return false;
		return true;
	}
}