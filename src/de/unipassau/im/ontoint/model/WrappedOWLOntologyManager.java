package de.unipassau.im.ontoint.model;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
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
        return (this.wrappedManager.loadOntologyFromOntologyDocument(file)
                instanceof OWLOntology);
    }

    public boolean loadOntologyFromURL(URL url)
            throws OWLOntologyCreationException, URISyntaxException {
        return (this.wrappedManager.loadOntologyFromOntologyDocument(
                    IRI.create(url))
                instanceof OWLOntology);
    }
}
