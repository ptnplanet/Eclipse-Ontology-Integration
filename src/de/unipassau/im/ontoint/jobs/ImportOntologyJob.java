package de.unipassau.im.ontoint.jobs;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.semanticweb.owlapi.model.OWLOntology;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManagerEvent;

public abstract class ImportOntologyJob extends Job {

    /**
     * A list of exceptions that occurred during the import of the ontology.
     * These exceptions will be displayed to the user after canceling the
     * import.
     */
    private List<Throwable> exceptions;

    /**
     * The ontology that was loaded.
     */
    private OWLOntology loadedOntology;

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
    }

    /**
     * {@inheritDoc}
     */
    protected final IStatus run(final IProgressMonitor monitor) {

        monitor.beginTask("Importing Ontology", IProgressMonitor.UNKNOWN);

        this.loadedOntology = this.loadOntology(monitor);
        if (this.loadedOntology == null) {
            monitor.setCanceled(true);
            this.reportExceptions();
            return Status.CANCEL_STATUS;
        }

        monitor.done();
        this.syncWithUI();
        return Status.OK_STATUS;
    }

    private void reportExceptions() {
        // TODO Auto-generated method stub
        
    }

    private void syncWithUI() {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {

                WrappedOWLOntologyManager manager =
                        OntointActivator.getDefault().getManager();
                manager.notifyListeners(new WrappedOWLOntologyManagerEvent(
                        manager,
                        new OWLOntology[] {
                            ImportOntologyJob.this.loadedOntology
                        },
                        new OWLOntology[0]));
            }
        });
    }

    /**
     * Imports an ontology into the eclipse environment.
     *
     * @param monitor the monitor to report the subtask to
     *
     * @return the loaded ontology
     */
    protected abstract OWLOntology loadOntology(IProgressMonitor monitor);

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
            this.exceptions = new LinkedList<Throwable>();
        }

        return this.exceptions.add(exception);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean belongsTo(final Object family) {
        return (family.equals(OntointActivator.PLUGIN_ID)
                || super.belongsTo(family));
    }

}
