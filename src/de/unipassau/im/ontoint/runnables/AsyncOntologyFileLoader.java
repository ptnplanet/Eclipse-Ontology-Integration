package de.unipassau.im.ontoint.runnables;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbench;
import org.semanticweb.owlapi.io.OWLOntologyInputSourceException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * A threaded ontology loader for ontology files.
 */
public final class AsyncOntologyFileLoader extends AsyncOntologyLoader {

    /**
     * The file to laod from.
     */
    private File file;

    /**
     * Creates a new asynchronic ontology loader for files.
     *
     * @param fileToLoad the file to load
     */
    public AsyncOntologyFileLoader(final File fileToLoad) {
        this(null, fileToLoad);
    }

    /**
     * Creates a new asynchronic ontology loader for files.
     *
     * @param workbench the workbench to use when showing exception details
     * @param fileToLoad the file to load
     */
    public AsyncOntologyFileLoader(final IWorkbench workbench,
            final File fileToLoad) {
        super(workbench);
        this.file = fileToLoad;
    }

    /**
     * {@inheritDoc}
     */
    public void run(final IProgressMonitor monitor)
            throws InvocationTargetException {

        monitor.beginTask("Loading ontology from file " + this.file.getName(),
                IProgressMonitor.UNKNOWN);

        try {
            this.getManager().loadOntologyFromOntologyDocument(file);
        } catch (OWLOntologyCreationException e) {
            this.handleException(e);
        } catch (OWLOntologyInputSourceException e) {
            this.handleException(e);
        }

        monitor.done();
    }

}
