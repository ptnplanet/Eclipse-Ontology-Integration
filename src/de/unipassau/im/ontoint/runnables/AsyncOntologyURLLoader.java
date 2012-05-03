package de.unipassau.im.ontoint.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IWorkbench;
import org.semanticweb.owlapi.io.OWLOntologyInputSourceException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

/**
 * A threaded ontology loader for ontology URLs.
 */
public final class AsyncOntologyURLLoader extends AsyncOntologyLoader {

    /**
     * The iri to load from.
     */
    private IRI iri;

    /**
     * Creates a new asynchronic ontology loader for URLs. The URL has to
     * be converted to an OWL API IRI first.
     *
     * @param iriToLoad the document IRI to load
     */
    public AsyncOntologyURLLoader(final IRI iriToLoad) {
        this(null, iriToLoad);
    }

    /**
     * Creates a new asynchronic ontology loader for URLs. The URL has to
     * be converted to an OWL API IRI first.
     *
     * @param workbench the workbench to use when showing exception details
     * @param iriToLoad the document IRI to load
     */
    public AsyncOntologyURLLoader(final IWorkbench workbench,
            final IRI iriToLoad) {
        super(workbench);
        this.iri = iriToLoad;
    }

    /**
     * {@inheritDoc}
     */
    public void run(final IProgressMonitor monitor)
            throws InvocationTargetException {

        monitor.beginTask("Loading ontology from URL " + this.iri.toString(),
                IProgressMonitor.UNKNOWN);

        try {
            this.getManager().loadOntologyFromOntologyDocument(this.iri);
        } catch (OWLOntologyCreationException e) {
            this.handleException(e);
        } catch (OWLOntologyInputSourceException e) {
            this.handleException(e);
        }

        monitor.done();
    }

}
