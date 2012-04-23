package de.unipassau.im.ontoint.model;

import java.util.EventObject;

public class WrappedOWLOntologyManagerEvent extends EventObject {

    private Object[] added;

    private Object[] removed;

    private static final long serialVersionUID = 4107406540475979175L;

    public WrappedOWLOntologyManagerEvent(Object source) {
        super(source);
    }

    public WrappedOWLOntologyManagerEvent(Object source, Object[] addedObjects,
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

    public Object[] getAdded() {
        return this.added;
    }

    public Object[] getRemoved() {
        return this.removed;
    }

}
