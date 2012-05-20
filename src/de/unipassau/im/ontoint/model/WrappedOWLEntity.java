package de.unipassau.im.ontoint.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Not really a wrapper but a data holding class that represents the basic
 * information from the original OWL API {@link OWLEntity}. Because this entity
 * will be serialized together with the {@link TemplateProposalTrie}, it is
 * more practically to just save any necessary information.
 *
 * @author Philipp Nolte
 */
public final class WrappedOWLEntity {

    /**
     * Instead of relying on the OWL API {@link EntityType} implementation, a
     * simple and easy to use enum will do here.
     */
    public static enum Types {

        /**
         * An OWL class.
         */
        OWLClass,

        /**
         * An OWL individual.
         */
        OWLNamedIndividual,

        /**
         * An OWL data property.
         */
        OWLDataProperty,

        /**
         * An OWL data type.
         */
        OWLDataType,

        /**
         * An OWL object property.
         */
        OWLObjectProperty,

        /**
         * An OWL annotation property.
         */
        OWLAnnotationProperty,

        /**
         * An OWL ontology.
         */
        OWLOntology,

        /**
         * Other type or unknown.
         */
        Other
    }

    /**
     * Caching {@link WrappedOWLEntity} singletons will ensure, that instances
     * can be accessed from multiple parts. Of course it is always possible
     * to create new, extra instances from the public constructors.
     */
    private static Map<String, WrappedOWLEntity> cache =
            new HashMap<String, WrappedOWLEntity>();

    /**
     * The full length ID of this entity.
     */
    private String longID;

    /**
     * The shortened ID of this entity.
     */
    private String shortID;

    /**
     * The type of this entity.
     */
    private WrappedOWLEntity.Types type;

    /**
     * Creates a new instance with the ID and type given.  The short ID will be
     * created on instanciation.
     *
     * @param entityID the entity ID
     * @param entityType the entity type
     */
    public WrappedOWLEntity(final String entityID,
            final WrappedOWLEntity.Types entityType) {
        this.longID = entityID;
        this.shortID = this.generateShortID(this.longID);
        this.type = entityType;
    }

    /**
     * Creates a new instance from the {@link OWLEntity} given.
     *
     * @param entity the entity to create a wrapped version of
     */
    public WrappedOWLEntity(final OWLEntity entity) {
        this.longID = entity.toStringID();
        this.shortID = this.generateShortID(this.longID);
        if (entity.isOWLClass()) {
            this.type = WrappedOWLEntity.Types.OWLClass;
        } else if (entity.isOWLDataProperty()) {
            this.type = WrappedOWLEntity.Types.OWLDataProperty;
        } else if (entity.isOWLDatatype()) {
            this.type = WrappedOWLEntity.Types.OWLDataType;
        } else if (entity.isOWLNamedIndividual()) {
            this.type = WrappedOWLEntity.Types.OWLNamedIndividual;
        } else if (entity.isOWLObjectProperty()) {
            this.type = WrappedOWLEntity.Types.OWLObjectProperty;
        } else if (entity.isOWLAnnotationProperty()) {
            this.type = WrappedOWLEntity.Types.OWLAnnotationProperty;
        } else {
            this.type = WrappedOWLEntity.Types.Other;
        }
    }

    /**
     * Creates a new instance from the {@link OWLOntology} given.
     *
     * @param ontology the ontology to create a wrapped version of
     */
    public WrappedOWLEntity(final OWLOntology ontology) {
        this.longID = ontology.getOntologyID().toString();
        this.shortID = this.longID.substring(this.longID.lastIndexOf('/') + 1,
                this.longID.length() - 1);
        this.type = WrappedOWLEntity.Types.OWLOntology;
    }

    /**
     * Generates a short version of the given entity ID.
     *
     * @param id the long id
     * @return the short version
     */
    public String generateShortID(final String id) {
        Assert.isNotNull(id);
        if (id.contains("#"))
            return id.substring(id.lastIndexOf('#') + 1);
        return id;
    }

    /**
     * Retrieves the entity's ID.
     *
     * @return the ID
     */
    public String getID() {
        return this.longID;
    }

    /**
     * Retrieves the entity's short ID.
     *
     * @return the short ID
     */
    public String getShortID() {
        return this.shortID;
    }

    /**
     * Retrieves the entity's type.
     *
     * @return the type
     */
    public WrappedOWLEntity.Types getType() {
        return this.type;
    }

    /**
     * Returns true if the entity is of the given type.
     *
     * @param entityType the type to check against
     * @return <code>true</code> if the entity is of this type
     */
    public boolean isType(final WrappedOWLEntity.Types entityType) {
        return this.type.equals(entityType);
    }

    /**
     * Returns wrapped entity instances for all entities in the given ontology.
     * The returned instances may be cached instances eg. singleton instances.
     *
     * @param ontology entities from this ontology will be wrapped
     * @return the wrapped entity instances
     */
    public static Set<WrappedOWLEntity> getEntitiesFrom(
            final OWLOntology ontology) {
        Set<WrappedOWLEntity> toReturn = new HashSet<WrappedOWLEntity>();

        toReturn.add(WrappedOWLEntity.getEntityFor(ontology));

        for (OWLClass c : ontology.getClassesInSignature(false))
            toReturn.add(WrappedOWLEntity.getEntityFor(c));

        for (OWLNamedIndividual i : ontology.getIndividualsInSignature(false))
            toReturn.add(WrappedOWLEntity.getEntityFor(i));

        for (OWLDataProperty d : ontology.getDataPropertiesInSignature(false))
            toReturn.add(WrappedOWLEntity.getEntityFor(d));

        for (OWLDatatype t : ontology.getDatatypesInSignature(false))
            toReturn.add(WrappedOWLEntity.getEntityFor(t));

        return toReturn;
    }

    /**
     * Retrieves an cached instance (if it exists) for the entity given or
     * creates a new instance if no cached singleton instance was found.
     *
     * @param entity the entity to wrap
     * @return the wrapped entity instance
     */
    public static WrappedOWLEntity getEntityFor(final OWLEntity entity) {
        WrappedOWLEntity toReturn =
                WrappedOWLEntity.cache.get(entity.toStringID());
        if (toReturn == null) {
            toReturn = new WrappedOWLEntity(entity);
            WrappedOWLEntity.cache.put(entity.toStringID(), toReturn);
        }
        return toReturn;
    }

    /**
     * Retrieves an cached instance (if it exists) for the ontology given or
     * creates a new instance if no cached singleton instance was found.
     *
     * @param ontology the ontology to wrap
     * @return the wrapped entity instance
     */
    public static WrappedOWLEntity getEntityFor(final OWLOntology ontology) {
        String id = ontology.getOntologyID().toString();
        WrappedOWLEntity toReturn =
                WrappedOWLEntity.cache.get(id);
        if (toReturn == null) {
            toReturn = new WrappedOWLEntity(ontology);
            WrappedOWLEntity.cache.put(id, toReturn);
        }
        return toReturn;
    }

    /**
     * Clears the cache.  This method should be called before the model is
     * serialized.
     */
    public static void clearCache() {
        WrappedOWLEntity.cache.clear();
    }

}
