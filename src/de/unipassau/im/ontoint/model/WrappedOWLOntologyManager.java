package de.unipassau.im.ontoint.model;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyLoaderListener;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * The main entry point to the model.  The {@link WrappedOWLOntologyManager}
 * wraps an OWL API {@link OWLOntologyManager} and handles the loading and
 * removing of {@link WrappedOWLOntology}s.
 *
 * @author Philipp Nolte
 */
public final class WrappedOWLOntologyManager
        implements OWLOntologyLoaderListener {

    /**
     * The wrapped {@link OWLOntologyManager}.
     */
    private OWLOntologyManager wrappedManager;

    /**
     * A list of listeners listening for
     * {@link WrappedOWLOntologyManagerEvent}s.
     */
    private Collection<IWrappedOWLOntologyManagerChangeListener> listeners =
            new LinkedList<IWrappedOWLOntologyManagerChangeListener>();

    /**
     * A {@link Map} mapping wrapped ontologies to their original.  This will
     * allow the retrieval of WrappedOWLOntologies from given
     * {@link OWLOntology}s.
     */
    private Map<OWLOntology, WrappedOWLOntology> wrappedOntologies =
            new Hashtable<OWLOntology, WrappedOWLOntology>();

    /**
     * The Trie containing all the string templates available for autocomplete
     * from the managed ontologies.
     */
    private TemplateProposalTrie autocompleteTrie = new TemplateProposalTrie();

    /**
     * Creates a new {@link WrappedOWLOntologyManager} instance that will use
     * a default {@link OWLOntologyManager}.
     */
    public WrappedOWLOntologyManager() {

        /*
         * Setup the wrapped ontology manager. This WrappedOWLOntologyManager
         * instance will be associated with the plugin's single activator
         * instance and create a new OWLOntologyManager on plugin startup.
         */
        this.wrappedManager = OWLManager.createOWLOntologyManager();

        /*
         * To make this WrappedOWLOntologyManager communicate load and remove
         * events to listeners in the view, it has to listen to the wrapped
         * manager first.
         */
        this.wrappedManager.addOntologyLoaderListener(this);
    }

    /**
     * Add a new listener to this {@link WrappedOWLOntologyManager}
     * listening to any {@link WrappedOWLOntologyManagerEvent}s.
     *
     * @param listener the listener to add
     * @return <code>true</code> if the listener collection changed
     */
    public boolean addWrappedOWLOntologyManagerChangeListener(
            final IWrappedOWLOntologyManagerChangeListener listener) {
        return this.listeners.add(listener);
    }

    /**
     * Remove a previous added listener.
     *
     * @param listener the listener to remove
     * @return <code>true</code> if a listener was removed
     */
    public boolean removeWrappedOWLOntologyManagerChangeListener(
            final IWrappedOWLOntologyManagerChangeListener listener) {
        return this.listeners.remove(listener);
    }

    /**
     * Notifies the listeners listening about the event given.
     *
     * @param event the event to notify the listeners about
     */
    public void notifyListeners(final WrappedOWLOntologyManagerEvent event) {
        for (IWrappedOWLOntologyManagerChangeListener listener : this.listeners)
            listener.wrappedOWLOntologyManagerChanged(event);
    }

    /**
     * Notifies the listeners listening about the added and removed ontologies.
     *
     * @param added the added ontologies
     * @param removed the removed ontologies
     */
    public void notifyListeners(final Object[] added, final Object[] removed) {
        WrappedOWLOntologyManagerEvent event =
                new WrappedOWLOntologyManagerEvent(this, added, removed);
        this.notifyListeners(event);
    }

    /**
     * Retrieves the wrapped {@link OWLOntologyManager}.
     *
     * @return the manager
     */
    public OWLOntologyManager getWrappedManager() {
        return this.wrappedManager;
    }

    /**
     * {@inheritDoc}
     */
    public void startedLoadingOntology(final LoadingStartedEvent event) {

        /*
         * There is nothing to do here.  The start of the loading process will
         * trigger the view's progress monitor to show in the controller. The
         * model does not have anything to do with that.
         */
    }

    /**
     * {@inheritDoc}
     */
    public void finishedLoadingOntology(final LoadingFinishedEvent event) {
        if (!event.isSuccessful())
            return;

        /*
         * Map the original Ontology to the wrapped ontology.  Calling
         * WrappedOWLOntologyManager#getWrappedOntologie will allow the
         * retrieval of the successfully loaded ontology handed over by the
         * LoadingFinishedEvent.
         */
        final OWLOntology original = this.wrappedManager.getOntology(
                event.getOntologyID());
        final WrappedOWLOntology wrappedOriginal = new WrappedOWLOntology(
                original,
                event.isImported(),
                event.getDocumentIRI());
        this.wrappedOntologies.put(original, wrappedOriginal);

        /*
         * All the ontology's relevant entities have to be added to the
         * autocomplete template trie.
         */
        this.fillTree(original);
    }

    /**
     * Retrieve the wrapped ontology for the original ontology given.
     *
     * @param original the original ontology
     * @return the wrapped wontology
     */
    public WrappedOWLOntology getWrappedOntology(final OWLOntology original) {
        return this.wrappedOntologies.get(original);
    }

    /**
     * Retrieves the wrapped ontologies.
     *
     * @return the wrapped ontologies
     */
    public WrappedOWLOntology[] getWrappedOntologies() {
        Collection<WrappedOWLOntology> toReturn =
                this.wrappedOntologies.values();
        return toReturn.toArray(new WrappedOWLOntology[toReturn.size()]);
    }

    /**
     * Remove an ontology from the loaded set.
     *
     * @param array an array of ontologies to remove
     */
    public void removeOntologies(final Object[] array) {
        Assert.isNotNull(array);

        for (Object element : array) {
            if (element instanceof WrappedOWLOntology) {

                /*
                 * For each WrappedOWLOntology to remove, the listeners have to
                 * be informed and the mapping has to be updated.
                 */
                final WrappedOWLOntology wrappedOntology =
                        (WrappedOWLOntology) element;
                final OWLOntology original =
                        wrappedOntology.getWrappedOntology();

                this.wrappedManager.removeOntology(original);
                this.wrappedOntologies.remove(original);

                this.notifyListeners(null, new Object[] {wrappedOntology});
            }
        }
    }

    /**
     * Retrieves a Set of {@link OWLEntity}s for autocompletion available from
     * the managed ontologies.
     *
     * @return the available templates
     */
    public Set<OWLEntity> getAutocompleteTemplates() {
        return this.autocompleteTrie.postfixes();
    }

    /**
     * Retrieves a Set of {@link OWLEntity}s for autocompletion available from
     * the managed ontologies relative to the prefix given.
     *
     * @param prefix the prefix to search for
     * @return the available templates
     */
    public Set<OWLEntity> getAutocompleteTemplates(final String prefix) {
        return this.autocompleteTrie.postfixes(prefix);
    }

    /**
     * Fill the autocomplete template {@link TemplateProposalTrie} with all relevant
     * template strings.
     *
     * @param ontology the ontology to fill the Trie with
     */
    private void fillTree(final OWLOntology ontology) {
        for (OWLClass c : ontology.getClassesInSignature(false))
            this.autocompleteTrie.add(c);

        for (OWLNamedIndividual i : ontology.getIndividualsInSignature(false))
            this.autocompleteTrie.add(i);

        for (OWLDataProperty d : ontology.getDataPropertiesInSignature(false))
            this.autocompleteTrie.add(d);

        for (OWLDatatype t : ontology.getDatatypesInSignature(false))
            this.autocompleteTrie.add(t);
    }

}
