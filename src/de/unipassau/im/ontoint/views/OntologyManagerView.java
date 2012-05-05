package de.unipassau.im.ontoint.views;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchActionConstants;
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
     * The view's id.
     */
    public static final String ID =
            "de.unipassau.im.ontoint.views.ontologyManager";

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
     * Creates a new view instance.
     */
    public OntologyManagerView() {
        super();
        this.manager = OntointActivator.getDefault().getManager();
    }

    /**
     * {@inheritDoc}
     */
    public void createPartControl(final Composite parent) {
        this.setupLayout(parent);

        this.createLabel(parent);
        this.createViewer(parent);

        this.viewer.setContentProvider(
                new OntologyManagerTableContentProvider());
        this.viewer.setLabelProvider(
                new OntologyManagerTableLabelProvider());
        this.viewer.setInput(this.manager);

        this.manager.addWrappedOWLOntologyManagerChangeListener(this);

        this.createContextMenu();
    }

    /**
     * Setup the view's layout.
     *
     * @param parent the viewer's composite
     */
    private void setupLayout(final Composite parent) {
        final GridLayout layout = new GridLayout();
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
    private void createViewer(final Composite parent) {
        this.viewer = new TableViewer(parent, SWT.FULL_SELECTION | SWT.MULTI);
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

        this.getSite().setSelectionProvider(this.viewer);
    }

    /**
     * Create the label showing the ontology count.
     *
     * @param parent the parent composite
     */
    private void createLabel(final Composite parent) {
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

    /**
     * Update the label count.
     */
    private void updateLabelCount() {
        this.label.setText(
                this.manager.getWrappedOntologies().length
                + " ontologies");
    }

    /**
     * Creates the empty context menu, that allows the plugin to add
     * contributions.
     */
    private void createContextMenu() {
        final MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(final IMenuManager menuManager) {
                menuMgr.add(
                        new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
        });
        final Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
        this.viewer.getControl().setMenu(menu);
        this.getSite().registerContextMenu(menuMgr, this.viewer);
    }

}
