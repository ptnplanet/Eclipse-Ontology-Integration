package de.unipassau.im.ontoint.jobs;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * An {@link ImportOntologyJob} subclass implementing the loadOntology method
 * for loading ontologies from Files.
 */
public final class ImportOntologyFileJob extends ImportOntologyJob {

    /**
     * The file to import.
     */
    private File file;

    /**
     * Creates a new instance with the given {@link File} to load.
     *
     * @param name the name of the Job
     * @param fileToImport the file to import
     *
     * @see Job
     */
    public ImportOntologyFileJob(final String name, final File fileToImport) {
        super(name);
        this.file = fileToImport;
    }

    /**
     * {@inheritDoc}
     */
    protected void loadOntology(final IProgressMonitor monitor) {
        monitor.subTask("Loading File " + this.file.getName());

        try {
            this.getManager().getWrappedManager()
                    .loadOntologyFromOntologyDocument(this.file);
        } catch (OWLOntologyCreationException e) {
            // ignored
        }
    }

}
