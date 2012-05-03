package de.unipassau.im.ontoint.jobs;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import de.unipassau.im.ontoint.OntointActivator;


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
    protected OWLOntology loadOntology(final IProgressMonitor monitor) {
        monitor.subTask("Loading File " + this.file.getName());

        OWLOntology loadedOntology = null;
        try {
            loadedOntology = OntointActivator.getDefault().getManager()
                    .getWrappedManager()
                    .loadOntologyFromOntologyDocument(this.file);
        } catch (OWLOntologyCreationException e) {
            this.appendException(e);
        }

        return loadedOntology;
    }

}
