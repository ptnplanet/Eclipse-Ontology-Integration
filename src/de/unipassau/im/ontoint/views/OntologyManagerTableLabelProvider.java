package de.unipassau.im.ontoint.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.semanticweb.owlapi.model.OWLOntology;

import de.unipassau.im.ontoint.OntointActivator;
import de.unipassau.im.ontoint.model.WrappedOWLOntology;

/**
 * A content provider for the ontology manager table.
 */
public final class OntologyManagerTableLabelProvider extends LabelProvider
        implements ITableLabelProvider {

    /**
     * {@inheritDoc}
     */
    public Image getColumnImage(final Object element, final int columnIndex) {
        WrappedOWLOntology ontology = (WrappedOWLOntology) element;
        final String documentIRI = ontology.getDocumentIRI().toString();
        ImageDescriptor descriptor = null;

        switch (columnIndex) {
        case 0:
            if (ontology.isImported()) {
                descriptor = OntointActivator.imageDescriptorFromPlugin(
                        OntointActivator.PLUGIN_ID, "/icons/importedOnt.gif");
            } else {
                descriptor = OntointActivator.imageDescriptorFromPlugin(
                        OntointActivator.PLUGIN_ID, "/icons/topLevelOnt.gif");
            }
            break;
        case 1:
            if (documentIRI.startsWith("file:")) {
                descriptor = OntointActivator.imageDescriptorFromPlugin(
                        OntointActivator.PLUGIN_ID, "/icons/ontFile.gif");
            } else {
                descriptor = OntointActivator.imageDescriptorFromPlugin(
                        OntointActivator.PLUGIN_ID, "/icons/ontWeb.gif");
            }
            break;
        default:
            return null;
        }

        return OntointActivator.getDefault().getImageCache()
                .getImage(descriptor);
    }

    /**
     * {@inheritDoc}
     */
    public String getColumnText(final Object element, final int columnIndex) {
        WrappedOWLOntology ontology = (WrappedOWLOntology) element;
        final String documentIRI = ontology.getDocumentIRI().toString();

        switch (columnIndex) {
        case 0:
            return documentIRI.substring(documentIRI.lastIndexOf('/') + 1);
        case 1:
            return documentIRI;
        default:
            return ontology.getOntologyID().toString();
        }
    }

}
