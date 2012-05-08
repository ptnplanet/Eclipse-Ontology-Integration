package de.unipassau.im.ontoint.views;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.model.IWrappedOWLOntologyManagerChangeListener;
import de.unipassau.im.ontoint.model.WrappedOWLOntology;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManagerEvent;

public class ClassHierarchyTreeContentProvider
        implements ITreeContentProvider,
        IWrappedOWLOntologyManagerChangeListener {

    private WrappedOWLOntology input;

    private TreeViewer treeViewer;

    public void dispose() { }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.treeViewer = (TreeViewer) viewer;
        this.input = (WrappedOWLOntology) newInput;
        this.treeViewer.expandToLevel(2);
    }

    public Object[] getElements(Object inputElement) {

        // Retrieve the OWL root: OWLThing.
        return new Object[] {
                OntointActivator.getDefault().getManager().getWrappedManager()
                .getOWLDataFactory().getOWLClass(
                        OWLRDFVocabulary.OWL_THING.getIRI())
        };
    }

    public Object[] getChildren(Object parentElement) {
        if (this.input == null) {
            return new Object[0];
        }
        OWLClass node = (OWLClass) parentElement;

        Set<OWLClass> children = this.input.getReasoner().getSubClasses(
                node, true).getFlattened();
        Set<OWLClass> toReturn = new HashSet<OWLClass>();
        for (Iterator<OWLClass> it = children.iterator(); it.hasNext();) {
            OWLClass child = it.next();
            if (this.input.getReasoner().isSatisfiable(child)) {
                toReturn.add(child);
            }
        }
        return toReturn.toArray();
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object element) {
        final Object[] children = this.getChildren(element);
        return children.length > 0;
    }

    @Override
    public void wrappedOWLOntologyManagerChanged(
            WrappedOWLOntologyManagerEvent event) {
        WrappedOWLOntology[] removed =
                (WrappedOWLOntology[]) event.getRemoved();
        for (int i = 0; i < removed.length; i++) {
            if (removed[i].equals(this.input)) {
                this.treeViewer.setInput(null);
            }
        }
    }

    

}
