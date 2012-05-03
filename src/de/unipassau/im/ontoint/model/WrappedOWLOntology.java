package de.unipassau.im.ontoint.model;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;

public class WrappedOWLOntology {

    private OWLOntology wrappedOntology;

    private boolean isImported;

    private IRI documentIRI;

    public WrappedOWLOntology(final OWLOntology ontology,
            final boolean imported, final IRI iri) {
        this.wrappedOntology = ontology;
        this.isImported = imported;
        this.documentIRI = iri;
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

}
