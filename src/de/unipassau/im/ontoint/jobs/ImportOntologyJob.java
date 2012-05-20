package de.unipassau.im.ontoint.jobs;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.statushandlers.StatusManager;
import org.semanticweb.owlapi.model.OWLOntologyLoaderListener;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.OntointLog;
import de.unipassau.im.ontoint.model.WrappedOWLOntology;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;

/**
 * Abstract class for importing ontologies as a background task visible to the
 * user. Implementations must provide
 * {@link ImportOntologyJob#loadOntology(IProgressMonitor)} method and append
 * occurred exceptions via the
 * {@link ImportOntologyJob#appendException(Throwable)} method.
 */
public abstract class ImportOntologyJob extends Job
        implements OWLOntologyLoaderListener {

    /**
     * A list of exceptions that occurred during the import of the ontology.
     * These exceptions will be displayed to the user after canceling the
     * import.
     */
    private List<IStatus> exceptions;

    /**
     * The manager to load the ontologies into.
     */
    private WrappedOWLOntologyManager manager;

    /**
     * Creates a new Job instance with the given name.
     *
     * @param name the name of the job
     *
     * @see Job
     */
    public ImportOntologyJob(final String name) {
        super(name);

        // Make the job execution visible to the user.
        this.setUser(true);
        this.manager = OntointActivator.getDefault().getManager();
    }

    /**
     * Retrieves the ontology manager.
     *
     * @return the manager used.
     */
    protected final WrappedOWLOntologyManager getManager() {
        return this.manager;
    }

    /**
     * {@inheritDoc}
     */
    protected final IStatus run(final IProgressMonitor monitor) {
        this.manager.getWrappedManager().addOntologyLoaderListener(this);

        monitor.beginTask("Importing Ontology", IProgressMonitor.UNKNOWN);

        this.loadOntology(monitor);

        monitor.done();

        if (this.exceptions != null) {
            this.reportExceptions();
        }

        this.manager.getWrappedManager().removeOntologyLoaderListener(this);
        return Status.OK_STATUS;
    }

    /**
     * Report exceptions back to the user.
     */
    private void reportExceptions() {
        if (this.exceptions != null) {
            for (IStatus e : this.exceptions) {
                StatusManager.getManager().handle(e, StatusManager.SHOW);
            }
        }
    }

    /**
     * Notify the {@link WrappedOWLOntologyManager} listeners about the
     * newly added ontology.
     *
     * @param loadedOntology the ontology to hand over to the UI
     */
    private void syncWithUI(final WrappedOWLOntology loadedOntology) {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {

                OntointActivator.getDefault().getManager().notifyListeners(
                        new WrappedOWLOntology[] {loadedOntology}, null);
            }
        });
    }

    /**
     * Imports an ontology into the eclipse environment.
     *
     * @param monitor the monitor to report the subtask to
     */
    protected abstract void loadOntology(IProgressMonitor monitor);

    /**
     * Appends an exception that occurred during the import of the ontology to
     * the list of exceptions presented to the user after the import finished.
     *
     * @param exception the exception to append
     *
     * @return <code>true</code> if the exception was appended
     */
    public final boolean appendException(final Throwable exception) {
        if (this.exceptions == null) {
            this.exceptions = new LinkedList<IStatus>();
        }

        return this.exceptions.add(OntointLog.createStatus(IStatus.ERROR, 0,
                "Error while importing ontology. Please make sure the ontology"
                + " document is readable and contains a parsable ontology."
                + " Caused by:\n"
                + exception.getClass().getSimpleName()
                , exception));
    }

    /**
     * {@inheritDoc}
     */
    public final boolean belongsTo(final Object family) {

        // Show a custom icon for this job.
        return (family.equals(OntointActivator.PLUGIN_ID)
                || super.belongsTo(family));
    }


    /**
     * {@inheritDoc}
     */
    public final void startedLoadingOntology(
            final LoadingStartedEvent event) { }

    /**
     * {@inheritDoc}
     */
    public final void finishedLoadingOntology(
            final LoadingFinishedEvent event) {
        if (event.isSuccessful()) {
            this.syncWithUI(this.manager.getWrappedOntology(
                    this.manager.getWrappedManager().getOntology(
                            event.getOntologyID())));
        } else {
            this.appendException(event.getException());
        }
    }

}
