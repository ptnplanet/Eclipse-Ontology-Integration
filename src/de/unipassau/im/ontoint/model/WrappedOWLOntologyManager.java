package de.unipassau.im.ontoint.model;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public final class WrappedOWLOntologyManager {

    private OWLOntologyManager wrappedManager;

    private Collection<IWrappedOWLOntologyManagerChangeListener> listeners;

    public WrappedOWLOntologyManager() {
        this.wrappedManager = OWLManager.createOWLOntologyManager();
        this.listeners =
                new LinkedList<IWrappedOWLOntologyManagerChangeListener>();
    }

    public boolean addWrappedOWLOntologyManagerChangeListener(
            IWrappedOWLOntologyManagerChangeListener listener) {
        return this.listeners.add(listener);
    }

    public boolean removeWrappedOWLOntologyManagerChangeListener(
            IWrappedOWLOntologyManagerChangeListener listener) {
        return this.listeners.remove(listener);
    }

    public boolean loadOntologyFromFile(File file)
            throws OWLOntologyCreationException {

        // Try loading the ontology file.
        OWLOntology loadedOntology =
                this.wrappedManager.loadOntologyFromOntologyDocument(file);

        // On success, notify the listeners.
        if (loadedOntology instanceof OWLOntology) {
            this.notifyListeners(new WrappedOWLOntologyManagerEvent(this,
                    new OWLOntology[] {loadedOntology}, null));
            return true;
        }
        return false;
    }

    public boolean loadOntologyFromURL(URL url)
            throws OWLOntologyCreationException, URISyntaxException {

        // Try loading the ontology url.
        OWLOntology loadedOntology =
                this.wrappedManager.loadOntologyFromOntologyDocument(
                        IRI.create(url));

        // On success, notify the listeners.
        if (loadedOntology instanceof OWLOntology) {
            this.notifyListeners(new WrappedOWLOntologyManagerEvent(this,
                    new OWLOntology[] {loadedOntology}, null));
            return true;
        }
        return false;
    }

    private void notifyListeners(final WrappedOWLOntologyManagerEvent event) {
        for (Iterator<IWrappedOWLOntologyManagerChangeListener> it =
                this.listeners.iterator(); it.hasNext();) {

            it.next().wrappedOWLOntologyManagerChanged(event);
        }
    }
}
