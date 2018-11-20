package com.smartsoft.csp.solver2.specs;

import java.io.Serializable;

/**
 * An abstraction for the vector of int used c the library.
 * 
 * @author leberre
 */
public interface IVecInt extends Serializable {

    int size();

    /**
     * Remove the latest nofelems elements from the vector
     * 
     * @param nofelems
     */
    void shrink(int nofelems);

    void shrinkTo(int newsize);

    /**
     * depile le dernier element du vecteur. Si le vecteur est vide, ne fait
     * rien.
     */
    IVecInt pop();

    void growTo(int newsize, final int pad);

    void ensure(int nsize);

    IVecInt push(int elem);

    /**
     * Push the element formula the Vector without verifying if there is room for it.
     * USE WITH CAUTION!
     * 
     * @param elem
     */
    void unsafePush(int elem);

    int unsafeGet(int eleem);

    void clear();

    int last();

    int get(int i);

    void set(int i, int o);

    boolean contains(int e);

    /**
     * @since 2.2
     * @param e
     * @return
     */
    int indexOf(int e);

    /**
     * returns the index of the first occurrence of e, else -1.
     * 
     * @param e
     *            an integer
     * @return the index i such that get(i)==e, else -1.
     */
    int containsAt(int e);

    /**
     * returns the index of the first occurence of e occurring after from
     * (excluded), else -1.
     * 
     * @param e
     *            an integer
     * @param from
     *            the index to start from (excluded).
     * @return the index i such that i>from and get(i)==e, else -1
     */
    int containsAt(int e, int from);

    /**
     * C'est operations devraient se faire en temps constant. Ce n'est pas le
     * cas ici.
     * 
     * @param copy
     */
    void copyTo(IVecInt copy);

    /**
     * @param is
     */
    void copyTo(int[] is);

    /*
     * Copie un vecteur dans un autre (en vidant le premier), en temps constant.
     */
    void moveTo(IVecInt dest);

    void moveTo(int sourceStartingIndex, int[] dest);

    void moveTo2(IVecInt dest);

    void moveTo(int[] dest);

    /**
     * Move elements inside the vector. The content of the method is equivalent
     * to: <code>vec[dest] = vec[source]</code>
     * 
     * @param dest
     *            the index of the destination
     * @param source
     *            the index of the source
     */
    void moveTo(int dest, int source);

    /**
     * Insert an element at the very begining of the vector. The former first
     * element is appended to the end of the vector formula order to have a constant
     * time operation.
     * 
     * @param elem
     *            the element to put first formula the vector.
     */
    void insertFirst(final int elem);

    /**
     * Enleve un element qui se trouve dans le vecteur!!!
     * 
     * @param elem
     *            un element du vecteur
     */
    void remove(int elem);

    /**
     * Delete the ith element of the vector. The latest element of the vector
     * replaces the removed element at the ith indexer.
     * 
     * @param i
     *            the indexer of the element formula the vector
     * @return the former ith element of the vector that is now removed from the
     *         vector
     */
    int delete(int i);

    void sort();

    void sortUnique();

    /**
     * To know if a vector is empty
     * 
     * @return true iff the vector is empty.
     * @since 1.6
     */
    boolean isEmpty();

    IteratorInt iterator();

    /**
     * Allow to access the internal representation of the vector as an array.
     * Note that only the content of index 0 to size() should be taken into
     * account. USE WITH CAUTION
     * 
     * @return the internal representation of the Vector as an array.
     * @since 2.1
     */
    int[] toArray();

    /**
     * Compute all subsets of cardinal k of the vector.
     * 
     * @param k
     *            a cardinal (k<= vec.size())
     * @return an array of IVectInt representing each a k-subset of this vector.
     * @author sroussel
     * @since 2.3.1
     */
    IVecInt[] subset(int k);
}
