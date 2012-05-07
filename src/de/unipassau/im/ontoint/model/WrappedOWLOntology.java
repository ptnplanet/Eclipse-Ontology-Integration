package de.unipassau.im.ontoint.model;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

public class WrappedOWLOntology implements IAdaptable {

    private OWLOntology wrappedOntology;

    private OWLReasoner reasoner;

    private boolean isImported;

    private IRI documentIRI;

    public WrappedOWLOntology(final OWLOntology ontology,
            final boolean imported, final IRI iri) {
        this.wrappedOntology = ontology;
        this.isImported = imported;
        this.documentIRI = iri;
        this.reasoner =
                (new StructuralReasonerFactory()).createReasoner(ontology);
    }

    public OWLOntology getWrappedOntology() {
        return wrappedOntology;
    }

    public boolean isImported() {
        return isImported;
    }

    public IRI getDocumentIRI() {
        return documentIRI;
    }

    public OWLReasoner getReasoner() {
        return this.reasoner;
    }

    public OWLOntologyID getOntologyID() {
        return this.wrappedOntology.getOntologyID();
    }

    @Override
    public Object getAdapter(final Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    public String toString() {
        final String iri = this.documentIRI.toString();
        return iri.substring(iri.lastIndexOf('/') + 1) + " <" + iri + ">";
    }

}
