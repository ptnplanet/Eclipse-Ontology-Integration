package de.unipassau.im.ontoint.runnables;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.OntointLog;
import de.unipassau.im.ontoint.views.ExceptionDetailsDialog;

/**
 * Abstract base of any ontology loader. It will handle exceptions during
 * ontology loading.
 */
public abstract class AsyncOntologyLoader implements IRunnableWithProgress {

    /**
     * The manager to use for loading. Make sure that there are listeners
     * listening to any <code>OWLOntologyLoaderListenerEvents</code>.
     */
    private OWLOntologyManager manager;

    /**
     * The eclipse workbench to use.
     */
    private IWorkbench workbench;

    /**
     * Creates a new asynchronic ontology loader.  The workbench given will be
     * used to visibly show exceptions occurring during the load process.
     *
     * @param workbenchToUse the workbench to use when showing exception details
     */
    public AsyncOntologyLoader(final IWorkbench workbenchToUse) {
        this.workbench = workbenchToUse;
        if (this.workbench == null) {
            this.workbench = PlatformUI.getWorkbench();
        }
        this.manager = OntointActivator.getDefault().getManager()
                .getWrappedManager();
    }

    /**
     * Creates a new asynchronic ontology loader.
     */
    public AsyncOntologyLoader() {
        this(null);
    }

    /**
     * Retrieves the manager used.
     *
     * @return the manager used
     */
    protected final OWLOntologyManager getManager() {
        return this.manager;
    }

    /**
     * Handles any exception thrown while loading ontologies.
     *
     * @param e the exception thrown
     */
    protected final void handleException(final Throwable e) {
        if (this.workbench == null) {
            OntointLog.logError(e);
            return;
        }

        final Shell parent = this.workbench.getActiveWorkbenchWindow()
                .getShell();
        final String title = "Error loading ontology";

        new ExceptionDetailsDialog(parent, title, null, null, e, null).open();
    }

    /**
     * Start running with the workbench's progress service defined.
     */
    public final void start() {
        if (this.workbench == null) {
            return;
        }

        /*
         * The eclipse runnable API will want us to throw exceptions.
         * The runnable implementations presented here will not, because
         * the exceptions will be handled within the controller.
         */
        try {
            this.workbench.getProgressService().run(false, false, this);
        } catch (InvocationTargetException e) {

            OntointLog.logError(e);
        } catch (InterruptedException e) {

            // Should never occur.
            OntointLog.logError(e);
        }
    }

}
