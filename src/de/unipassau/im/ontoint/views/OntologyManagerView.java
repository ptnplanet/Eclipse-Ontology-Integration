package de.unipassau.im.ontoint.views;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;

import de.unipassau.im.ontoint.OntointActivator;

/**
 * The manage ontology view.
 */
public final class OntologyManagerView extends ViewPart {

    /**
     * The <code>TableViewer</code> displaying the ontology tree.
     */
    private TableViewer viewer;

    /**
     * {@inheritDoc}
     */
    public void createPartControl(final Composite parent) {
        this.viewer = OntologyManagerView.createViewer(parent);
        this.viewer.setContentProvider(
                new OntologyManagerTableContentProvider());
        this.viewer.setLabelProvider(
                new OntologyManagerTableLabelProvider(
                        OntointActivator.getDefault().getManager()));
        this.viewer.setInput(OntointActivator.getDefault().getManager());
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() { }

    /**
     * Create a <code>TableViewer</code> instance to display the ontology tree.
     *
     * @param parent the parent composite
     * @return the <code>TableViewer</code> instance
     */
    protected static TableViewer createViewer(final Composite parent) {
        final TableViewer tableViewer = new TableViewer(parent,
                SWT.SINGLE | SWT.FULL_SELECTION);
        final Table table = tableViewer.getTable();

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

        return tableViewer;
    }

}
