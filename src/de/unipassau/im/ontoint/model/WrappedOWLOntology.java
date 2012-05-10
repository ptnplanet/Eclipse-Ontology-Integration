package de.unipassau.im.ontoint.model;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

/**
 * A wrapped {@link OWLOntology} extending the OWLOntology functionality.  The
 * wrapper is necessary to retrieve extra information such as if the ontology
 * was imported by an other ontology and the original document ID. Each
 * ontology also needs a reasoner for structural (class hierarchy) reasoning.
 */
public final class WrappedOWLOntology implements IAdaptable {

    /**
     * The wrapped ontology.
     */
    private OWLOntology wrappedOntology;

    /**
     * The reasoner used for class hierarchy building.
     */
    private OWLReasoner reasoner;

    /**
     * <code>true</code> if the ontology was imported by an other ontology.
     */
    private boolean isImported;

    /**
     * The ontology document IRI.
     */
    private IRI documentIRI;

    /**
     * Creates a new {@link WrappedOWLOntology} instance with the data given.
     *
     * @param ontology the original ontology to wrap
     * @param imported <code>true</code> if the ontology was imported by an
     *    other ontology
     * @param iri the document IRI
     */
    public WrappedOWLOntology(final OWLOntology ontology,
            final boolean imported, final IRI iri) {
        this.wrappedOntology = ontology;
        this.isImported = imported;
        this.documentIRI = iri;
        this.reasoner =
                (new StructuralReasonerFactory()).createReasoner(ontology);
    }

    /**
     * Gets the wrapped ontology.
     *
     * @return the wrapped ontology
     */
    public OWLOntology getWrappedOntology() {
        return wrappedOntology;
    }

    /**
     * Returns <code>true</code> if the ontology was imported by an other one.
     *
     * @return if the ontology was imported
     */
    public boolean isImported() {
        return isImported;
    }

    /**
     * Retrieves the document IRI of this ontology.
     *
     * @return the document IRI
     */
    public IRI getDocumentIRI() {
        return documentIRI;
    }

    /**
     * Retrieves the reasoner used for class hierarchy building.
     *
     * @return the reasoner associated with this ontology
     */
    public OWLReasoner getReasoner() {
        return this.reasoner;
    }

    /**
     * Retrieves the {@link OWLOntologyID} of the wrapped ontology.
     *
     * @return the ontology ID
     */
    public OWLOntologyID getOntologyID() {
        return this.wrappedOntology.getOntologyID();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    public Object getAdapter(final Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        final String iri = this.documentIRI.toString();
        return iri.substring(iri.lastIndexOf('/') + 1) + " <" + iri + ">";
    }

}
