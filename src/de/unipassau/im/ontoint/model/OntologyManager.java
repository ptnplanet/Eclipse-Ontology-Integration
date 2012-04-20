package de.unipassau.im.ontoint.model;

import java.io.File;
import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.unipassau.im.ontoint.OntointLog;

/**
 * The ontology manager -- it acts as a wrapper for the OWLManager class.
 */
public final class OntologyManager {

    /**
     * Singleton.
     */
    private static OntologyManager instance = new OntologyManager();

    /**
     * The OWL Ontology Manager managing.
     */
    private OWLOntologyManager manager;

    /**
     * A list of listeners for this manager instance.
     */
    private List<IOntologyManagerListener> listeners;

    /**
     * Retrieve the singleton instance.
     *
     * @return the singleton
     */
    public static OntologyManager getInstance() {
        return OntologyManager.instance;
    }

    /**
     * Private singleton constructor.
     */
    private OntologyManager() {

        /*
         * Create a new OWL Ontology Manager - main access point for any
         * ontologies associated with this OntologyManager.
         */
        this.manager = OWLManager.createOWLOntologyManager();

        // Initialize all remaining private fields.
        this.listeners = new LinkedList<IOntologyManagerListener>();

        // TODO: remove this temporary stuff
        try {
            this.manager.loadOntologyFromOntologyDocument(
                    IRI.create("http://localhost/pizza.owl"));
        } catch (OWLOntologyCreationException e) {
            OntointLog.logError("Could not load ontology.", e);
        }
    }

    public boolean loadOntologyFromURI(final URI uri) {
        return false;
    }

    public boolean loadOntologyFromFile(final File file) {
        return false;
    }

    public Set<OWLOntology> getOntologies() {
        return null;
    }

    public Set<OWLOntology> getTopLevelOntologies() {
        Set<OWLOntology> toReturn = this.manager.getOntologies();
        for (OWLOntology ont : this.manager.getOntologies()) {
            toReturn.removeAll(ont.getDirectImports());
        }
        return toReturn;
    }

    /**
     * Adds the given listener to the list of listeners.
     *
     * @param listener the listener to add
     */
    public void addOntologyManagerListener(
            final IOntologyManagerListener listener) {
        this.listeners.add(listener);
    }

    /**
     * Removes the given listener from the listeners list.
     *
     * @param listener the listener to remove
     */
    public void removeOntologyManagerListener(
            final IOntologyManagerListener listener) {
        this.listeners.remove(listener);
    }

}
