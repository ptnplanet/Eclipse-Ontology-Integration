package de.unipassau.im.ontoint.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.model.IWrappedOWLOntologyManagerChangeListener;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManagerEvent;

/**
 * The ontology class hierarchy view.
 */
public class ClassHierarchyView extends ViewPart
        implements IWrappedOWLOntologyManagerChangeListener  {

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
     * Creates a new view instance.
     */
    public ClassHierarchyView() {
        super();
        this.manager = OntointActivator.getDefault().getManager();
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

    private void createTreeViewer(Composite parent) {
        this.viewer = new TreeViewer(parent);
        final Tree tree = viewer.getTree();

        final GridData layoutData =
                new GridData(SWT.FILL, SWT.FILL, true, true);
        layoutData.horizontalSpan = 2;
        tree.setLayoutData(layoutData);
    }

    private void createDropDown(final Composite parent) {
        final Label label = new Label(parent, SWT.HORIZONTAL);
        label.setText("Select ontology: ");
        label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        this.dropdown = new Combo(parent, SWT.DROP_DOWN);
        this.dropdown.setLayoutData(
                new GridData(SWT.FILL, SWT.CENTER, true, false));
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

    @Override
    public void wrappedOWLOntologyManagerChanged(
            WrappedOWLOntologyManagerEvent event) {
        Object[] added = event.getAdded();
        this.viewer.setInput(added[0]);
    }

}
