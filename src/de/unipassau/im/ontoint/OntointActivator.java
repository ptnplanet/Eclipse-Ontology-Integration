package de.unipassau.im.ontoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.semanticweb.owlapi.model.IRI;

import de.unipassau.im.ontoint.jobs.ImportOntologyFileJob;
import de.unipassau.im.ontoint.jobs.ImportOntologyURLJob;
import de.unipassau.im.ontoint.model.WrappedOWLOntology;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;
import de.unipassau.im.ontoint.proposals.BayesClassifier;
import de.unipassau.im.ontoint.proposals.Classifier;
import de.unipassau.im.ontoint.proposals.ContextFeature;

/**
 * The activator class controls the plug-in life cycle.
 */
public final class OntointActivator extends AbstractUIPlugin {

    /**
     * Memento tags for saving global information.
     */
    private static final String MEMENTO_TAG_ONTOLOGIES = "Ontologies";
    private static final String MEMENTO_TAG_ONTOLOGY = "Ontology";
    private static final String MEMENTO_TAG_ISFILE = "isFile";
    private static final String MEMENTO_TAG_LOCATION = "location";

    /**
     * Plugin file names.
     */
    private static final String FILE_CLASSIFIER = "classifier.serialized";
    private static final String FILE_RECENT = "recent.xml";

    /**
     * The unique plug-in ID.
     */
    public static final String PLUGIN_ID = "de.unipassau.im.ontoint";

    /**
     * The image cache for lazy-loading any icons.
     */
    private ImageCache imageCache;

    /**
     * The shared instance.
     */
    private static OntointActivator plugin;

    /**
     * The ontology manager to use throughout this plugin's model.
     */
    private WrappedOWLOntologyManager manager;

    /**
     * The classifier used for relevance computation of template proposals.
     */
    private Classifier<ContextFeature, String> classifier;

    /**
     * For testing purposes.
     */
    private boolean deserialize;

    /**
     * The constructor.
     */
    public OntointActivator() {
        this(true);
    }

    /**
     * The constructor.
     *
     * @param loadSerializedClassifier if <code>true</code>, then the serialized
     *  classifier will be loaded
     */
    public OntointActivator(final boolean loadSerializedClassifier) {
        this.deserialize = loadSerializedClassifier;
        this.imageCache = new ImageCache();
        this.manager = new WrappedOWLOntologyManager();
    }

    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        OntointActivator.plugin = this;

        // Register an icon for this plugin's jobs.
        this.getWorkbench().getProgressService().registerIconForFamily(
                OntointActivator.imageDescriptorFromPlugin(
                        OntointActivator.PLUGIN_ID, "/icons/import.gif"),
                OntointActivator.PLUGIN_ID);

        // Load the serialized classifier or fall back to an empty one.
        if (!this.deserialize || !this.loadClassifier()) {
            this.classifier = new BayesClassifier<ContextFeature, String>();
        }

        // Load all recently opened ontologies
        this.importRecentOntologies();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(final BundleContext context) throws Exception {

        this.saveClassifier();
        this.saveRecentOntologies();

        OntointActivator.plugin = null;
        this.imageCache.dispose();
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     *
     * @return the shared instance
     */
    public static OntointActivator getDefault() {
        return OntointActivator.plugin;
    }

    /**
     * Retrieves this plugin instance's ontology manager.
     *
     * @return this instance's owl manager
     */
    public WrappedOWLOntologyManager getManager() {
        return this.manager;
    }

    /**
     * Retrieves this plugin's image cache.
     *
     * @return the image cache
     */
    public ImageCache getImageCache() {
        return this.imageCache;
    }

    /**
     * Shortcut for
     * {@link OntointActivator#imageDescriptorFromPlugin(String, String)}.
     *
     * @param imageFilePath the image path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(
            final String imageFilePath) {
        return OntointActivator.imageDescriptorFromPlugin(
                OntointActivator.PLUGIN_ID, imageFilePath);
    }

    /**
     * Gets the classifier.
     *
     * @return the classifier or <code>null</code> if no classifier has been
     *  loaded (yet)
     */
    public Classifier<ContextFeature, String> getClassifier() {
        return this.classifier;
    }

    /**
     * Load the serialized classifier.
     *
     * @return <code>true</code> if the classifier was loaded
     */
    @SuppressWarnings("unchecked")
    private boolean loadClassifier() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(
                    this.getPluginFile("classifier.serialized"));
            ObjectInputStream o = new ObjectInputStream(fis);
            Object obj = o.readObject();
            this.classifier = (Classifier<ContextFeature, String>) obj;
        } catch (FileNotFoundException e) {
            OntointLog.logError(e);
            return false;
        } catch (IOException e) {
            OntointLog.logError(e);
            return false;
        } catch (ClassNotFoundException e) {
            OntointLog.logError(e);
            return false;
        } catch (ClassCastException e) {
            OntointLog.logError(e);
            return false;
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                OntointLog.logError(e);
            }
        }
        return true;
    }

    /**
     * Serialize the classifier.
     *
     * @return <code>true</code> if the classifier was successfully serialized
     */
    private boolean saveClassifier() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(
                    this.getPluginFile("classifier.serialized"));
            ObjectOutputStream o = new ObjectOutputStream(fos);
            o.writeObject(this.classifier);
        } catch (IOException e) {
            OntointLog.logError(e);
            return false;
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                OntointLog.logError(e);
            }
        }
        return true;
    }

    /**
     * Load recently opened ontologies.
     */
    private void importRecentOntologies() {
        FileReader reader = null;
        try {
            reader = new FileReader(
                    this.getPluginFile(OntointActivator.FILE_RECENT));
            this.loadRecentOntologies(XMLMemento.createReadRoot(reader));
        } catch (FileNotFoundException e) {
            // ignore, file has not been created yet
        } catch (Exception e) {
            OntointLog.logError(e);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                OntointLog.logError(e);
            }
        }
    }

    /**
     * Load recent ontology from the memento provided.
     *
     * @param memento the xml mememto to use
     */
    private void loadRecentOntologies(final XMLMemento memento) {
        IMemento[] children = memento.getChildren(
                OntointActivator.MEMENTO_TAG_ONTOLOGY);

        for (IMemento child : children) {
            final boolean isFile = child.getBoolean(
                    OntointActivator.MEMENTO_TAG_ISFILE);
            final String iri = child.getString(
                    OntointActivator.MEMENTO_TAG_LOCATION);

            if (isFile)
                new ImportOntologyFileJob("Importing recently opened ontology",
                        new File(iri.replaceAll("^file:", ""))).schedule();
            else
                new ImportOntologyURLJob("Importing recently opened ontology",
                        IRI.create(iri)).schedule();
        }
    }

    /**
     * Save all opened ontologies.
     */
    private void saveRecentOntologies() {
        if (this.getManager().getWrappedOntologies().length < 1)
            return;

        XMLMemento memento = XMLMemento.createWriteRoot(
                OntointActivator.MEMENTO_TAG_ONTOLOGIES);
        for (WrappedOWLOntology o : this.getManager().getWrappedOntologies())
            this.saveRecentOntology(o, memento);

        FileWriter writer = null;
        try {
            writer = new FileWriter(
                    this.getPluginFile(OntointActivator.FILE_RECENT));
            memento.save(writer);
        } catch (IOException e) {
            OntointLog.logError(e);
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                OntointLog.logError(e);
            }
        }
    }

    /**
     * Save a single ontology to the recent file list.
     *
     * @param ontology the ontology to save
     * @param memento the memento instance to use
     */
    private void saveRecentOntology(final WrappedOWLOntology ontology,
            final XMLMemento memento) {

        if (ontology.isImported())
            return;

        String iri = ontology.getDocumentIRI().toString();

        IMemento child = memento.createChild(
                OntointActivator.MEMENTO_TAG_ONTOLOGY);
        child.putBoolean(OntointActivator.MEMENTO_TAG_ISFILE,
                iri.startsWith("file"));
        child.putString(OntointActivator.MEMENTO_TAG_LOCATION, iri);
    }

    /**
     * Gets a file from the plugin directory.
     *
     * @param fileName the file to load
     * @return the file with the serialized classifier
     */
    private File getPluginFile(final String fileName) {
        return OntointActivator
                .getDefault()
                .getStateLocation()
                .append(fileName)
                .toFile();
    }

}
