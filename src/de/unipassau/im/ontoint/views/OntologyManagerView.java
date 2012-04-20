package de.unipassau.im.ontoint.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;

import de.unipassau.im.ontoint.model.OntologyManager;

public class OntologyManagerView extends ViewPart {

    public OntologyManagerView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(Composite parent) {
        Tree ontologyTree = new Tree(parent,
                SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        TreeViewer viewer = new TreeViewer(ontologyTree);

        viewer.setContentProvider(new OntologyListContentProvider());
        viewer.setInput(OntologyManager.getInstance());
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

}
