package de.unipassau.im.ontoint.model;

/**
 * Any listener wanting to listen to loading and removing ontologies from
 * {@link WrappedOWLOntologyManager}s have to implement this interface.
 *
 * @author Philipp Nolte
 */
public interface IWrappedOWLOntologyManagerChangeListener {

    /**
     * Called by the event's source (most likely an
     * {@link WrappedOWLOntologyManager} instance) in the case of adding or
     * removing an object from the set of managed ontologies.
     *
     * @param event the change event describing the added and/or removed objects
     */
    void wrappedOWLOntologyManagerChanged(WrappedOWLOntologyManagerEvent event);

}
