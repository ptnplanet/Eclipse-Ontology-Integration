package de.unipassau.im.ontoint.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntologyLoaderListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * The main entry point to the model.  The {@link WrappedOWLOntologyManager}
 * wrapps an OWL API {@link OWLOntologyManager} and handles the loading and
 * removing of {@link WrappedOWLOntology}s.
 */
public final class WrappedOWLOntologyManager {

    /**
     * The wrapped {@link OWLOntologyManager}.
     */
    private OWLOntologyManager wrappedManager;

    /**
     * A list of listeners listening for
     * {@link WrappedOWLOntologyManagerEvent}s.
     */
    private Collection<IWrappedOWLOntologyManagerChangeListener> listeners;

    /**
     * Creates a new {@link WrappedOWLOntologyManager} instance that will use
     * the default {@link OWLOntologyManager}.
     */
    public WrappedOWLOntologyManager() {
        this.wrappedManager = OWLManager.createOWLOntologyManager();
        this.listeners =
                new LinkedList<IWrappedOWLOntologyManagerChangeListener>();
    }

    /**
     * Add a new listener to this {@link WrappedOWLOntologyManager}
     * listening to any {@link WrappedOWLOntologyManagerEvent}s.
     *
     * @param listener the listener to add
     * @return <code>true</code> if the listener collection changed
     */
    public boolean addWrappedOWLOntologyManagerChangeListener(
            final IWrappedOWLOntologyManagerChangeListener listener) {
        return this.listeners.add(listener);
    }

    /**
     * Remove a previous added listener.
     *
     * @param listener the listener to remove
     * @return <code>true</code> if a listener was removed
     */
    public boolean removeWrappedOWLOntologyManagerChangeListener(
            final IWrappedOWLOntologyManagerChangeListener listener) {
        return this.listeners.remove(listener);
    }

    /**
     * Notifies the listeners listening.
     *
     * @param event the event to notify the listeners about
     */
    public void notifyListeners(final WrappedOWLOntologyManagerEvent event) {
        for (Iterator<IWrappedOWLOntologyManagerChangeListener> it =
                this.listeners.iterator(); it.hasNext();) {

            it.next().wrappedOWLOntologyManagerChanged(event);
        }
    }

    /**
     * Retrieves the wrapped {@link OWLOntologyManager}.
     *
     * @return the manager
     */
    public OWLOntologyManager getWrappedManager() {
        return this.wrappedManager;
    }

}
