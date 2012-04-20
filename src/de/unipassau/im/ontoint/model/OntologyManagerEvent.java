package de.unipassau.im.ontoint.model;

import java.util.EventObject;

import org.semanticweb.owlapi.model.OWLOntology;

public final class OntologyManagerEvent extends EventObject {

    private static final long serialVersionUID = -6587999390572350031L;

    private final Ontology[] added;

    private final Ontology[] removed;

    public OntologyManagerEvent(final OntologyManager source,
            final Ontology[] itemsAdded, final Ontology[] itemsRemoved) {
        super(source);
        this.added = itemsAdded;
        this.removed = itemsRemoved;
    }

    public Ontology[] getItemsAdded() {
        return this.added;
    }

    public Ontology[] getItemsRemoved() {
        return this.removed;
    }

}
