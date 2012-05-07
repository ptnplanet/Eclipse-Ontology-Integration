package de.unipassau.im.ontoint.views;

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.model.IWrappedOWLOntologyManagerChangeListener;
import de.unipassau.im.ontoint.model.WrappedOWLOntology;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManagerEvent;

/**
 * The ontology class hierarchy view.
 */
public final class ClassHierarchyView extends ViewPart
        implements IWrappedOWLOntologyManagerChangeListener {

    /**
     * The view's id.
     */
    public static final String ID =
            "de.unipassau.im.ontoint.views.classHierarchy";

    /**
     * The manager associated with this view.
     */
    private WrappedOWLOntologyManager manager;

    /**
     * The dropdown list for the user to select an ontology.
     */
    private Combo dropdown;

    /**
     * This view's viewer.
     */
    private TreeViewer viewer;

    /**
     * A map mapping dropdown index and ontology.
     */
    private Map<String, WrappedOWLOntology> availableItems;

    /**
     * Creates a new view instance.
     */
    public ClassHierarchyView() {
        super();
        this.manager = OntointActivator.getDefault().getManager();
        this.availableItems = new Hashtable<String, WrappedOWLOntology>();
    }

    /**
     * {@inheritDoc}
     */
    public void createPartControl(final Composite parent) {
        this.setupLayout(parent);

        this.createDropDown(parent);
        this.createTreeViewer(parent);

        this.viewer.setContentProvider(new ClassHierarchyTreeContentProvider());
        this.viewer.setLabelProvider(new ClassHierarchyTreeLabelProvider());

        this.manager.addWrappedOWLOntologyManagerChangeListener(this);
    }

    /**
     * Creates the tree viewer.
     *
     * @param parent the viewer's parent composite to use
     */
    private void createTreeViewer(final Composite parent) {
        this.viewer = new TreeViewer(parent);
        final Tree tree = viewer.getTree();

        final GridData layoutData =
                new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.horizontalSpan = 2;
        tree.setLayoutData(layoutData);
    }

    /**
     * Creates the dropdown.
     *
     * @param parent the dropdown's parent composite to use
     */
    private void createDropDown(final Composite parent) {
        final Label label = new Label(parent, SWT.HORIZONTAL);
        label.setText("Select ontology: ");
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        this.dropdown = new Combo(parent, SWT.DROP_DOWN);
        this.dropdown.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false));

        this.fillDropDown();
        this.dropdown.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent e) {
                ClassHierarchyView.this.show(
                        ClassHierarchyView.this.dropdown.getSelectionIndex());
            }
        });
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
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        parent.setLayout(layout);
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() { }

    /**
     * {@inheritDoc}
     */
    public void wrappedOWLOntologyManagerChanged(
            final WrappedOWLOntologyManagerEvent event) {
        final Object[] added =  event.getAdded();
        for (int i = 0; i < added.length; i++) {
            if (added[i] instanceof WrappedOWLOntology) {
                final WrappedOWLOntology toAdd =
                        (WrappedOWLOntology) added[i];
                this.dropdown.add(toAdd.toString());
                this.availableItems.put(toAdd.toString(), toAdd);
            }
        }
        final Object[] removed = event.getRemoved();
        for (int i = 0; i < removed.length; i++) {
            if (removed[i] instanceof WrappedOWLOntology) {
                final WrappedOWLOntology toRemove =
                        (WrappedOWLOntology) removed[i];
                this.dropdown.remove(toRemove.toString());
                this.availableItems.remove(toRemove.toString());

                if (this.dropdown.indexOf(toRemove.toString())
                        == this.dropdown.getSelectionIndex()) {
                    this.dropdown.deselectAll();
                }
            }
        }
    }

    /**
     * Fills the dropdown with the initial values.
     */
    private void fillDropDown() {
        this.dropdown.removeAll();
        final WrappedOWLOntology[] ontologies =
                this.manager.getWrappedOntologies();
        for (int i = 0; i < ontologies.length; i++) {
            this.dropdown.add(ontologies[i].toString());
            this.availableItems.put(ontologies[i].toString(), ontologies[i]);
        }
    }

    /**
     * Switches the view's data to the object to select.
     *
     * @param toSelect the object to show in the view's tree and dropdown.
     */
    public void show(final WrappedOWLOntology toSelect) {
        final int index = this.dropdown.indexOf(toSelect.toString());
        this.show(index);
    }

    /**
     * Switches the view's data to the object's index to select.
     *
     * @param selectionIndex the object index in the dropdown to show in the
     *   view's tree and dropdown.
     */
    public void show(final int selectionIndex) {
        if ((selectionIndex < 0)
                || (selectionIndex >= this.dropdown.getItemCount())) {
            return;
        }

        this.dropdown.select(selectionIndex);
        this.viewer.setInput(this.availableItems.get(
                this.dropdown.getItem(selectionIndex)));
    }

}
