package de.unipassau.im.ontoint.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * A simple but powerful StringTrie implementation.  {@link WrappedOWLEntity}s
 * are stored in the nodes. The edges are labeled by a single char of the
 * entity's String ID. Following the chars of an ID down from the root will
 * result in the node containing the entity with that specific ID.
 *
 * @author Philipp Nolte
 */
public final class TemplateProposalTrie implements Set<WrappedOWLEntity> {

    /**
     * This node's value.
     */
    private Set<WrappedOWLEntity> values;

    /**
     * A dictionary table containing references to the child nodes according to
     * the values of the edges connecting them.
     */
    private Map<Character, TemplateProposalTrie> children =
            new Hashtable<Character, TemplateProposalTrie>();

    /**
     * Are there any values stored in this node?
     *
     * @return <code>true</code> if there are values
     */
    private boolean flagged() {
        return (this.values != null) && (this.values.size() > 0);
    }

    /**
     * {@inheritDoc}
     */
    public boolean add(final WrappedOWLEntity e) {
        final String eID = e.getShortID().toLowerCase();

        /*
         * This Trie implementation can not allow any null values. The
         * array that is going to be added can have a length of zero and will
         * only have to set the flag accordingly. Return false if the zero
         * length element was already added.
         */
        if (eID == null)
            throw new NullPointerException();

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

        if (node.values == null)
            node.values = new HashSet<WrappedOWLEntity>();

        return node.values.contains(e) ? false : node.values.add(e);
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
    public boolean addAll(final Collection<? extends WrappedOWLEntity> c) {
        boolean toReturn = false;
        for (WrappedOWLEntity e : c)
            toReturn = this.add(e) || toReturn;
        return toReturn;
    }

    /**
     * {@inheritDoc}
     */
    public void clear() {
        this.values = null;
        for (TemplateProposalTrie child : this.children.values())
            child.clear();
        this.children.clear();
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(final Object o) {
        if (o == null)
            throw new NullPointerException();
        if (!(o instanceof WrappedOWLEntity))
            return false;
        char[] array = ((WrappedOWLEntity) o).getID().toCharArray();

        // Try finding the array by traversing the edges one element at a time.
        TemplateProposalTrie node = this;
        for (char element : array) {
            if (!node.children.containsKey(element))
                return false;
            node = node.children.get(element);
        }
        return node.flagged() ? node.values.contains(o) : false;
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
        return this.children.isEmpty()
                && ((this.values == null) || (this.values.size() == 0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<WrappedOWLEntity> iterator() {
        return new Iterator<WrappedOWLEntity>() {

            /**
             * Use the iterator from the set returned by the postfixes helper
             * method.
             */
            private Iterator<WrappedOWLEntity> it =
                    TemplateProposalTrie.this.postfixes().iterator();

            /**
             * {@inheritDoc}
             */
            public boolean hasNext() {
                return this.it.hasNext();
            }

            /**
             * {@inheritDoc}
             */
            public WrappedOWLEntity next() {
                return this.it.next();
            }

            /**
             * Not supported, because removing from the underlying Set will not
             * remove the object from the Trie structure. Use the Trie's remove
             * function instead.
             */
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
        if (!(o instanceof WrappedOWLEntity))
            return false;
        char[] array = ((WrappedOWLEntity) o).getShortID()
                .toLowerCase().toCharArray();

        /*
         * Zero length arrays can only be removed, when this root node contains
         * the zero length value.
         */
        if ((array.length == 0) && this.flagged())
            return this.values.remove(o);

        TemplateProposalTrie node = this;
        TemplateProposalTrie lastEndNode = this;
        int lastEndIndex = 0;
        for (int i = 0; i < array.length; i++) {

            // Remember the last end node.
            if (node.flagged() && (i < (array.length - 1))) {
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
        if (node.flagged()) {
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
            toReturn = this.remove(o) || toReturn;
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
    public Set<WrappedOWLEntity> postfixes() {
        final Set<WrappedOWLEntity> toReturn = new HashSet<WrappedOWLEntity>();
        if (this.flagged()) {
            toReturn.addAll(this.values);
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
    public Set<WrappedOWLEntity> postfixes(final String prefix) {
        TemplateProposalTrie currentNode = this;
        for (char element : prefix.toCharArray()) {
            if (!currentNode.children.containsKey(element)) {
                return new HashSet<WrappedOWLEntity>();
            }
            currentNode = currentNode.children.get(element);
        }
        return currentNode.postfixes();
    }

}
