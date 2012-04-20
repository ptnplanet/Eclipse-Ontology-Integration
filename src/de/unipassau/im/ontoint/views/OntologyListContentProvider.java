package de.unipassau.im.ontoint.views;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.semanticweb.owlapi.model.OWLOntology;

import de.unipassau.im.ontoint.model.IOntologyManagerListener;
import de.unipassau.im.ontoint.model.Ontology;
import de.unipassau.im.ontoint.model.OntologyManager;

public class OntologyListContentProvider implements ITreeContentProvider,
        IOntologyManagerListener {

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO Auto-generated method stub

    }

    @Override
    public void ontologiesChanged() {
        // TODO Auto-generated method stub

    }

    @Override
    public Object[] getElements(Object inputElement) {
        return this.getChildren(inputElement);
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof OntologyManager) {
            return ((OntologyManager) parentElement)
                    .getTopLevelOntologies().toArray();
        } else if (parentElement instanceof OWLOntology) {
            return ((OWLOntology) parentElement)
                    .getDirectImports().toArray();
        }
        return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return (this.getChildren(element).length > 0);
    }

}
