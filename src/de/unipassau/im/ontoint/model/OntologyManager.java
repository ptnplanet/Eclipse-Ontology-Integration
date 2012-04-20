package de.unipassau.im.ontoint.model;

/**
 * The ontology manager.
 */
public final class OntologyManager {

    /**
     * Singleton.
     */
    private static OntologyManager manager = new OntologyManager();

    /**
     * Private singleton constructor.
     */
    private OntologyManager() { }

    /**
     * Retrieve the singleton instance.
     *
     * @return The singleton.
     */
    public static OntologyManager getManager() {
        return OntologyManager.manager;
    }

}
