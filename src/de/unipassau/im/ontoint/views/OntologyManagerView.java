package de.unipassau.im.ontoint.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.model.IWrappedOWLOntologyManagerChangeListener;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManagerEvent;

/**
 * The manage ontology view.
 */
public final class OntologyManagerView extends ViewPart
        implements IWrappedOWLOntologyManagerChangeListener {

    /**
     * The {@link TableViewer} displaying the ontology tree.
     */
    private TableViewer viewer;

    /**
     * The {@link Label} displaying the ontology count.
     */
    private Label label;

    /**
     * The manager associated with this view.
     */
    private WrappedOWLOntologyManager manager;

    /**
     * {@inheritDoc}
     */
    public void createPartControl(final Composite parent) {
        this.setupLayout(parent);
        this.manager = OntointActivator.getDefault().getManager();

        this.createLabel(parent);
        this.createViewer(parent);

        this.viewer.setContentProvider(
                new OntologyManagerTableContentProvider());
        this.viewer.setLabelProvider(
                new OntologyManagerTableLabelProvider());
        this.viewer.setInput(this.manager);

        this.manager.addWrappedOWLOntologyManagerChangeListener(this);
    }

    /**
     * Setup the viewer's layout.
     *
     * @param parent the viewer's composite
     */
    private void setupLayout(final Composite parent) {
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 1;
        layout.horizontalSpacing = 1;
        layout.numColumns = 1;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        parent.setLayout(layout);
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() { }

    /**
     * Create a {@link TableViewer} instance to display the ontology tree.
     *
     * @param parent the parent composite
     */
    protected void createViewer(final Composite parent) {
        this.viewer = new TableViewer(parent, SWT.SINGLE
                | SWT.FULL_SELECTION);
        final Table table = this.viewer.getTable();
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        final String[] columnNames =
                new String[] {"Name", "Document", "Ontology IRI"};
        final int[] columnWidths = new int[] {100, 150, 250};
        final int[] columnAlignments = new int[] {SWT.LEFT, SWT.LEFT, SWT.LEFT};

        for (int i = 0; i <  columnNames.length; i++) {
            TableColumn column = new TableColumn(table, columnAlignments[i]);
            column.setText(columnNames[i]);
            column.setWidth(columnWidths[i]);
        }
    }

    /**
     * Create the label showing the ontology count.
     *
     * @param parent the parent composite
     */
    protected void createLabel(final Composite parent) {
        this.label = new Label(parent, SWT.VERTICAL);
        this.label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        this.updateLabelCount();
    }

    /**
     * {@inheritDoc}
     */
    public void wrappedOWLOntologyManagerChanged(
            final WrappedOWLOntologyManagerEvent event) {
        this.updateLabelCount();
    }

    private void updateLabelCount() {
        this.label.setText(
                this.manager.getWrappedManager().getOntologies().size()
                + " ontologies");
    }

}
