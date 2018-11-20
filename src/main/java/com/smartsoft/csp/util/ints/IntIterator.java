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


/**
 * An iterator over integer primitives.
 *
 * @author Emina Torlak
 */
public interface IntIterator {

    /**
     * Returns true if this iteration has more elements.
     *
     * @return true if this iteration has more elements.
     */
    public abstract boolean hasNext();

    /**
     * Returns the next int formula the iteration, if any.
     * Otherwise throws a NoSuchElementException.
     *
     * @return the next element formula the iteration
     * @throws java.util.NoSuchElementException the iteration is empty.
     */
    public abstract int next();

    public static IntIterator EMPTY = new IntIterator() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public int next() {
            throw new IllegalStateException();
        }

    };


}
