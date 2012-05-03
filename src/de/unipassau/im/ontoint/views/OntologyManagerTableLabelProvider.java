package de.unipassau.im.ontoint.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLOntology;

import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;

public final class OntologyManagerTableLabelProvider extends LabelProvider
        implements ITableLabelProvider {

    private WrappedOWLOntologyManager ontologyManager;

    public OntologyManagerTableLabelProvider(
            final WrappedOWLOntologyManager manager) {
        super();
        this.ontologyManager = manager;
    }

    /**
     * {@inheritDoc}
     */
    public Image getColumnImage(final Object element, final int columnIndex) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String getColumnText(final Object element, final int columnIndex) {
        OWLOntology ontology = (OWLOntology) element;

        switch (columnIndex) {
        case 0:
//            String documentIRI = ontology.getDocumentIRI().toString();
//            return documentIRI.substring(documentIRI.lastIndexOf('/') + 1);
            return "laala";
        case 1:
//            return ontology.getWrappedOntology().getOntologyID().toString();
            return "blabla";
        default:
            return "";
        }
    }

}
