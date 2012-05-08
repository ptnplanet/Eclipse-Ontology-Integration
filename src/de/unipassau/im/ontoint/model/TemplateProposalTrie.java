package de.unipassau.im.ontoint.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLEntity;

/**
 * A simple but powerful StringTrie implementation.
 *
 * @author Philipp Nolte
 */
public final class TemplateProposalTrie implements Set<OWLEntity> {

    /**
     * This node's value.
     */
    private OWLEntity value;

    /**
     * A dictionary table containing references to the child nodes according to
     * the values of the edges connecting them.
     */
    private Map<Character, TemplateProposalTrie> children =
            new Hashtable<Character, TemplateProposalTrie>();

    /**
     * Flag indicating if an array sequence ends in this node.  This is crucial
     * to know, because the sequence "hello" can contain both the words "hell"
     * and "hello". The nodes holding these values will have this flag set to
     * <code>true</code>.
     */
    private boolean flag = false;
//
//    /**
//     * Creates a new Trie instance with an empty root node.
//     */
//    public TemplateProposalTrie() {
//        this("");
//    }
//
//    /**
//     * Creates a new Trie instance with the given array as root node's value.
//     * @param initalValue
//     */
//    public TemplateProposalTrie(final String initialValue) {
//        this.value = initialValue;
//    }

    /**
     * {@inheritDoc}
     */
    public boolean add(final OWLEntity e) {
        final String eID = e.toStringID();

        /*
         * This Trie implementation can not allow any null values. The
         * array that is going to be added can have a length of zero and will
         * only have to set the flag accordingly. Return false if the zero
         * length element was already added.
         */
        if (eID == null)
            throw new NullPointerException();
        if (eID.length() == 0)
            return this.flag ? false : (this.flag = true);

        /*
         * Walk through the array to add and make sure an edge for every array
         * element is present. If the last child node already had the end flag
         * set to true, then the value was already present: return false.
         */
        TemplateProposalTrie node = this;
        for (char element : eID.toCharArray()) {
            if (!node.children.containsKey(element))
                node.addEdge(element);
            node = node.children.get(element);
        }
        node.value = e;
        return node.flag ? false : (node.flag = true);
    }

    /**
     * Helper method adding a new child and setting its value accordingly.
     *
     * @param e The edge to add.
     */
    private void addEdge(final char e) {
        this.children.put(e, new TemplateProposalTrie());
    }

    /**
     * {@inheritDoc}
     */
    public boolean addAll(final Collection<? extends OWLEntity> c) {
        boolean toReturn = false;
        for (OWLEntity e : c)
            toReturn = toReturn || this.add(e);
        return toReturn;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        this.value = null;
        this.children.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(final Object o) {
        if (o == null)
            throw new NullPointerException();
        if (!(o instanceof OWLEntity))
            return false;
        char[] array = ((OWLEntity) o).toStringID().toCharArray();

        // Try finding the array by traversing the edges one element at a time.
        TemplateProposalTrie node = this;
        for (char element : array) {
            if (!node.children.containsKey(element))
                return false;
            node = node.children.get(element);
        }
        return node.flag;
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsAll(final Collection<?> c) {
        boolean toReturn = true;
        for (Object e : c) {
            toReturn = toReturn && this.contains(e);
            if (!toReturn)
                return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty() {
        return this.children.isEmpty() && !this.flag;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<OWLEntity> iterator() {
        return new Iterator<OWLEntity>() {

            /**
             * Use the iterator from the set returned by the postfixes helper
             * method.
             */
            private Iterator<OWLEntity> it =
                    TemplateProposalTrie.this.postfixes().iterator();

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean hasNext() {
                return this.it.hasNext();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public OWLEntity next() {
                return this.it.next();
            }

            /**
             * Not supported, because removing from the underlying Set will not
             * remove the object from the Trie structure. Use the Trie's remove
             * function instead.
             */
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(final Object o) {
        if (o == null)
            throw new NullPointerException();
        if (!(o instanceof OWLEntity))
            return false;
        char[] array = ((OWLEntity) o).toStringID().toCharArray();

        /*
         * Zero length arrays can only be removed, when this root node contains
         * the zero length value.
         */
        if ((array.length == 0) && (this.value == null))
            return this.flag ? (this.flag = false) : false;

        TemplateProposalTrie node = this;
        TemplateProposalTrie lastEndNode = this;
        int lastEndIndex = 0;
        for (int i = 0; i < array.length; i++) {

            // Remember the last end node.
            if ((node.flag) && (i < (array.length - 1))) {
                lastEndNode = node;
                lastEndIndex = i;
            }
            if (!node.children.containsKey(array[i]))
                return false;
            node = node.children.get(array[i]);
        }

        /*
         * Delete all subtrees leading from the last end node to the end of the
         * array to delete.
         */
        if (node.flag) {
            node = lastEndNode;
            for (int i = lastEndIndex; i < array.length; i++) {
                lastEndNode = node.children.get(array[i]);
                node.children.remove(array[i]);
                node = lastEndNode;
            }
            return true;
        } else
            return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean removeAll(final Collection<?> c) {
        boolean toReturn = false;
        for (Object o : c)
            toReturn = toReturn || this.remove(o);
        return toReturn;
    }

    /**
     * Not supported.
     *
     * @throws UnsupportedOperationException
     */
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public int size() {
        return this.postfixes().size();
    }

    /**
     * {@inheritDoc}
     */
    public Object[] toArray() {
        return this.postfixes().toArray();
    }

    /**
     * {@inheritDoc}
     */
    public <U> U[] toArray(final U[] a) {
        return this.postfixes().toArray(a);
    }

    /**
     * Retrieves all arrays down the Trie from this root node.
     *
     * @return A <code>Set</code> of all arrays in this (sub)tree.
     */
    public Set<OWLEntity> postfixes() {
        final Set<OWLEntity> toReturn = new HashSet<OWLEntity>();
        if (this.flag) {
            toReturn.add(this.value);
        }

        for (TemplateProposalTrie t : this.children.values())
            toReturn.addAll(t.postfixes());

        return toReturn;
    }

    /**
     * Retrieves all arrays down the Trie with the given prefix.
     *
     * @param prefix The array prefix to use.
     * @return A <code>Set</code> of all arrays in this (sub)tree with the given
     *  prefix. Note, that the prefix will be returned as part of the set's
     *  elements.
     */
    public Set<OWLEntity> postfixes(final String prefix) {
        TemplateProposalTrie currentNode = this;
        for (char element : prefix.toCharArray()) {
            if (!currentNode.children.containsKey(element)) {
                return new HashSet<OWLEntity>();
            }
            currentNode = currentNode.children.get(element);
        }
        return currentNode.postfixes();
    }

}
