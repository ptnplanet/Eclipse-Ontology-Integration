package de.unipassau.im.ontoint.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.unipassau.im.ontoint.model.IWrappedOWLOntologyManagerChangeListener;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManagerEvent;

/**
 * This content provider mediates between model (OntologyManager) and View
 * (OntologyManagerView).
 */
public final class OntologyManagerTableContentProvider
        implements IStructuredContentProvider,
        IWrappedOWLOntologyManagerChangeListener {

    /**
     * The <code>TableViewer</code> relying on this content provider.
     */
    private TableViewer tableViewer;

    /**
     * The manager providing input elements.
     */
    private WrappedOWLOntologyManager manager;

    /**
     * {@inheritDoc}
     */
    public void dispose() { }

    /**
     * {@inheritDoc}
     */
    public void inputChanged(final Viewer viewer, final Object oldInput,
            final Object newInput) {
        this.tableViewer = (TableViewer) viewer;

        if (this.manager != null) {
            this.manager.removeWrappedOWLOntologyManagerChangeListener(this);
        }
        this.manager = (WrappedOWLOntologyManager) newInput;
        if (this.manager != null) {
            this.manager.addWrappedOWLOntologyManagerChangeListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void wrappedOWLOntologyManagerChanged(
            final WrappedOWLOntologyManagerEvent event) {

        this.tableViewer.getTable().setRedraw(false);
        try {
            this.tableViewer.remove(event.getRemoved());
            this.tableViewer.add(event.getAdded());
        } finally {
            this.tableViewer.getTable().setRedraw(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getElements(final Object inputElement) {
        return this.manager.getWrappedOntologies();
    }

}
