package de.unipassau.im.ontoint.model;

import java.util.EventObject;

/**
 * An event, that will be handed over to any
 * {@link IWrappedOWLOntologyManagerChangeListener} as soon as ontologies are
 * added or removed from the {@link WrappedOWLOntologyManager} source.
 *
 * @author Philipp Nolte
 */
public final class WrappedOWLOntologyManagerEvent extends EventObject {

    /**
     * The array of added ontologies.
     */
    private Object[] added;

    /**
     * The array of removed ontologies.
     */
    private Object[] removed;

    /**
     * {@link EventObject}s have to be able to be serialized.
     */
    private static final long serialVersionUID = 4107406540475979175L;

    /**
     * Creates a new, empty event.
     *
     * @param source the source of this event
     */
    public WrappedOWLOntologyManagerEvent(final Object source) {
        super(source);
        this.added = new Object[0];
        this.removed = new Object[0];
    }

    /**
     * Creates a new event with the added and removed objects.
     *
     * @param source the source of this event
     * @param addedObjects the added objects
     * @param removedObjects the removed objects
     */
    public WrappedOWLOntologyManagerEvent(final Object source,
            Object[] addedObjects,
            Object[] removedObjects) {
        this(source);

        if (addedObjects == null) {
            addedObjects = new Object[0];
        }
        this.added = addedObjects;

        if (removedObjects == null) {
            removedObjects = new Object[0];
        }
        this.removed = removedObjects;
    }

    /**
     * Gets the added objects.
     *
     * @return the added objects
     */
    public Object[] getAdded() {
        return this.added;
    }

    /**
     * Gets the removed objects.
     *
     * @return the removed objects
     */
    public Object[] getRemoved() {
        return this.removed;
    }

}
