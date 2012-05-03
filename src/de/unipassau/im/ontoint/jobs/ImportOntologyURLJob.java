package de.unipassau.im.ontoint.jobs;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import de.unipassau.im.ontoint.OntointActivator;

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
    protected OWLOntology loadOntology(final IProgressMonitor monitor) {
        monitor.subTask("Loading File from " + this.iri.toString());

        OWLOntology loadedOntology = null;
        try {
            loadedOntology = OntointActivator.getDefault().getManager()
                    .getWrappedManager()
                    .loadOntologyFromOntologyDocument(this.iri);
        } catch (OWLOntologyCreationException e) {
            this.appendException(e);
        }

        return loadedOntology;
    }

}
