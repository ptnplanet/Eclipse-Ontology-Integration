package de.unipassau.im.ontoint.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.unipassau.im.ontoint.OntointLog;

/**
 * The main entry point to the model.  The {@link WrappedOWLOntologyManager}
 * wrapps an OWL API {@link OWLOntologyManager} and handles the loading and
 * removing of {@link WrappedOWLOntology}s.
 */
public final class WrappedOWLOntologyManager
        implements OWLOntologyLoaderListener {

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
     * A {@link Map} mapping wrapped ontologies to their original.
     */
    private Map<OWLOntology, WrappedOWLOntology> wrappedOntologies;

    /**
     * Creates a new {@link WrappedOWLOntologyManager} instance that will use
     * the default {@link OWLOntologyManager}.
     */
    public WrappedOWLOntologyManager() {
        this.wrappedManager = OWLManager.createOWLOntologyManager();
        this.wrappedManager.addOntologyLoaderListener(this);
        this.listeners =
                new LinkedList<IWrappedOWLOntologyManagerChangeListener>();
        this.wrappedOntologies =
                new Hashtable<OWLOntology, WrappedOWLOntology>();
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
     * Notifies the listeners listening about the event given.
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
     * Notifies the listeners listening about the added and removed ontologies.
     *
     * @param added the added ontologies
     * @param removed the removed ontologies
     */
    public void notifyListeners(final Object[] added, final Object[] removed) {
        WrappedOWLOntologyManagerEvent event =
                new WrappedOWLOntologyManagerEvent(this, added, removed);
        this.notifyListeners(event);
    }

    /**
     * Retrieves the wrapped {@link OWLOntologyManager}.
     *
     * @return the manager
     */
    public OWLOntologyManager getWrappedManager() {
        return this.wrappedManager;
    }

    /**
     * {@inheritDoc}
     */
    public void startedLoadingOntology(final LoadingStartedEvent event) { }

    /**
     * {@inheritDoc}
     */
    public void finishedLoadingOntology(final LoadingFinishedEvent event) {
        if (event.isSuccessful()) {

            // Map the original Ontology to the wrapped ontology.
            final OWLOntology original =
                    this.wrappedManager.getOntology(event.getOntologyID());
            this.wrappedOntologies.put(original,
                    new WrappedOWLOntology(original, event.isImported(),
                            event.getDocumentIRI()));
        }
    }

    /**
     * Retrieve the wrapped ontology for the original ontology given.
     *
     * @param original the original ontology
     * @return the wrapped wontology
     */
    public WrappedOWLOntology getWrappedOntology(final OWLOntology original) {
        return this.wrappedOntologies.get(original);
    }

    /**
     * Retrieves the wrapped ontologies.
     *
     * @return the wrapped ontologies
     */
    public WrappedOWLOntology[] getWrappedOntologies() {
        Collection<WrappedOWLOntology> toReturn =
                this.wrappedOntologies.values();
        return toReturn.toArray(new WrappedOWLOntology[toReturn.size()]);
    }

    /**
     * Remove an ontology from the loaded set.
     *
     * @param array an array of ontologies to remove
     */
    public void removeOntologies(final Object[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] instanceof WrappedOWLOntology) {
                WrappedOWLOntology ontology = (WrappedOWLOntology) array[i];
                OWLOntology original = ontology.getWrappedOntology();
                this.wrappedManager.removeOntology(original);
                this.wrappedOntologies.remove(original);
                this.notifyListeners(null, new Object[] {ontology});
            }
        }
    }

}
