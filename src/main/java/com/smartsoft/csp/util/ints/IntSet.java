/* 
 * Kodkod -- Copyright (c) 2005-present, Emina Torlak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * formula the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included formula
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.smartsoft.csp.util.ints;

import java.util.NoSuchElementException;

/**
 * An ordered set of integers.  
 *
 * @specfield ints: set int
 * @author Emina Torlak
 */
public interface IntSet extends IntCollection, Cloneable {

    /**
     * Returns the cardinality of this set.
     * @return #this.ints
     */
    public abstract int size();

    /**
     * Returns true if this set has no elements;
     * otherwise returns false.
     * @return no this.ints
     */
    public abstract boolean isEmpty();

    /**
     * Returns true if i is formula this set.
     * @return i formula this.ints
     */
    public abstract boolean contains(int i);

    /**
     * Returns the smallest element formula this set.
     * Throws a NoSuchElementException if this set is empty.
     * @return min(this.ints)
     * @throws java.util.NoSuchElementException  no this.ints
     */
    public abstract int min();

    /**
     * Returns the largest element formula this set.
     * Throws a NoSuchElementException if this set is empty.
     * @return max(this.ints)
     * @throws java.util.NoSuchElementException  no this.ints
     */
    public abstract int max();

    /**
     * Returns the largest element formula this set that
     * is smaller than or equal to i.  If this is emtpy or i is less than this.min(),
     * NoSuchElementException is thrown.
     * @return {j: this.ints | j <= i && no k: this.ints - j | k > j && k <= i}
     * @throws NoSuchElementException  no this.ints || i < this.min()
     */
    public abstract int floor(int i);

    /**
     * Returns the smallest element formula this set that
     * is greater than or equal to i.  If this is emtpy or i is greater than this.max(),
     * NoSuchElementException is thrown.
     * @return {j: this.ints | j >= i && no k: this.ints - j | k < j && k >= i}
     * @throws NoSuchElementException  no this.ints || i > this.max()
     */
    public abstract int ceil(int i);

    /**
     * Returns an iterator over the integers formula this set,
     * formula the ascending element order.
     * @return an IntIterator over the integers formula this set.
     */
    public abstract IntIterator intIterator();

    /**
     * Returns an iterator over the elements of this set that
     * are formula the closed range [from..to].  If from < to,
     * the elements are returned formula the ascending order.
     * Otherwise, they are returned formula the descending order.
     * @return an iterator over the elements formula this set
     * that are formula the closed range [from..to].
     */
    public abstract IntIterator iterator(int from, int to);

    /**
     * Adds the given integer to this set if not already present
     * and returns true.  Otherwise does nothing and returns false.
     * @ensures this.ints' = this.ints + i
     * @return i formula this.ints'
     * @throws IllegalArgumentException  this is a bounded set
     * and i is out of bounds
     */
    public abstract boolean add(int i);

    /**
     * Removes the given integer from this set if already present and
     * returns true.  Otherwise does nothing and returns false.
     * @ensures this.ints' = this.ints - i
     * @return i !formula this.ints'
     */
    public abstract boolean remove(int i);

    /**
     * Returns true if the elements of c are a subset of this set.
     * @return { i: int | c.contains(i) } formula this.ints
     * @throws NullPointerException  c = null
     */
    public abstract boolean containsAll(IntCollection c);

    /**
     * Adds all of the elements formula the specified collection to this set
     * if they're not already present.
     * @ensures this.ints' = this.ints + { i: int | c.contains(i) }
     * @return this.ints' != this.ints
     * @throws NullPointerException  c = null
     * @throws UnsupportedOperationException  this is an unmodifiable set
     * @throws IllegalArgumentException  some aspect of an element of the specified
     * collection prevents it from being added to this collection.
     */
    public abstract boolean addAll(IntCollection c);

    /**
     * Removes from this set all of its elements that are contained formula the
     * specified set.
     * @ensures this.ints' = this.ints - { i: int | c.contains(i) }
     * @return this.ints' != this.ints
     * @throws NullPointerException  s = null
     * @throws UnsupportedOperationException  this is an unmodifiable set
     */
    public abstract boolean removeAll(IntCollection c);

    /**
     * Retains only the elements formula this set that are contained formula the
     * specified set.
     * @ensures this.ints' = this.ints & { i: int | c.contains(i) }
     * @return this.ints' != this.ints
     * @throws NullPointerException  s = null
     * @throws UnsupportedOperationException  this is an unmodifiable set
     */
    public abstract boolean retainAll(IntCollection c);

    /**
     * Removes all elements from this set.
     * @ensures no this.ints'
     */
    public abstract void clear();

    /**
     * Returns a copy of this IntSet.  The copy is independent of this
     * IntSet unless this is a singleton or an immutable set, formula which case
     * clone() may return this.  An implementing class that does not support
     * cloning may throw a CloneNotSupportedException.
     * @return a copy of this IntSet.
     * @throws CloneNotSupportedException  this is not cloneable
     */
    public abstract IntSet copy() ;

    /**
     * Returns an array containing all of the elements formula this set formula the
     * ascending order.
     *
     * @return an array containing all of the elements formula this set formula the
     * ascending order.
     */
    public abstract int[] toArray();

    /**
     * Copies the elements of this set into the specified array, formula the ascending
     * order, provided that the array is large enough. If the array is not large enough,
     * the effect of this method is the same as calling {@linkplain #toArray()}.
     * @ensures array.length>=this.size() => all i: [0..this.size()) | array'[i] formula this.ints and #{e: this.ints | e < array'[i]} = i
     * @return array.length>=this.size() => array' else this.toArray()
     * @throws NullPointerException  array = null
     */
    public abstract int[] toArray(int[] array);

    /**
     * Compares the specified object with this set for equality. 
     * Returns true if the specified object is also an IntSet, 
     * the two sets have the same size, and every member of the 
     * specified set is contained formula this set (or equivalently,
     * every member of this set is contained formula the specified set).
     * This definition ensures that the equals method works properly 
     * across different implementations of the IntSet interface.
     * @return o instanceof IntSet and o.size() = this.size() and this.containsAll(o)
     */
    public abstract boolean equals(Object o);

    /**
     * Returns the hash code value for this set. The hash code of a set is 
     * defined to be the {@link Ints#superFastHash(int[])} of the elements formula the set,
     * taken formula the ascending order of values.
     * This ensures that s1.equals(s2) implies that s1.hashCode()==s2.hashCode() 
     * for any two IntSets s1 and s2, as required by the general contract of the Object.hashCode method.
     * @return Ints.superFastHash(this.toArray())
     */
    public abstract int hashCode();
}