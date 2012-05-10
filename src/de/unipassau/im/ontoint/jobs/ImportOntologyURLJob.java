package de.unipassau.im.ontoint.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * An {@link ImportOntologyJob} subclass implementing the loadOntology method
 * for loading ontologies from URLs.
 */
public final class ImportOntologyURLJob extends ImportOntologyJob {

    /**
     * The ontology iri (location) to load from.
     */
    private IRI iri;

    /**
     * Creates a new instance with the given {@link File} to load.
     *
     * @param name the name of the Job
     * @param iriToLoad the file location to import
     *
     * @see Job
     */
    public ImportOntologyURLJob(final String name, final IRI iriToLoad) {
        super(name);
        this.iri = iriToLoad;
    }

    /**
     * {@inheritDoc}
     */
    protected void loadOntology(final IProgressMonitor monitor) {
        monitor.subTask("Loading File from " + this.iri.toString());

        try {
            this.getManager().getWrappedManager()
                    .loadOntologyFromOntologyDocument(this.iri);
        } catch (OWLOntologyCreationException e) {
            // ignored
        }
    }

}
