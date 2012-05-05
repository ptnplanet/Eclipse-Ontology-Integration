package de.unipassau.im.ontoint.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.semanticweb.owlapi.model.OWLClass;

public class ClassHierarchyTreeLabelProvider extends LabelProvider {

    public String getText(Object element) {
        OWLClass node = (OWLClass) element;
        return node.toStringID();
    }

}
