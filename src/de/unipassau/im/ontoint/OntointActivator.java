package de.unipassau.im.ontoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.unipassau.im.ontoint.jobs.ImportOntologyFileJob;
import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;
import de.unipassau.im.ontoint.proposals.BayesClassifier;
import de.unipassau.im.ontoint.proposals.Classifier;
import de.unipassau.im.ontoint.proposals.ContextFeature;

/**
 * The activator class controls the plug-in life cycle.
 */
public final class OntointActivator extends AbstractUIPlugin {

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
     * The constructor.
     */
    public OntointActivator() {
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
        if (!this.loadClassifier()) {
            this.classifier = new BayesClassifier<ContextFeature, String>();
        }

        new ImportOntologyFileJob("Importing Ontology", new File("/Library/WebServer/Documents/Astronomy.owl"))
        .schedule();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(final BundleContext context) throws Exception {

        this.saveClassifier();

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
        System.out.println(this.getClasifierFile());
        try {
            fis = new FileInputStream(this.getClasifierFile());
            ObjectInputStream o = new ObjectInputStream(fis);
            Object obj = o.readObject();
            this.classifier = (Classifier<ContextFeature, String>) obj;
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        } catch (ClassCastException e) {
            return false;
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                // ignore
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
            fos = new FileOutputStream(this.getClasifierFile());
            ObjectOutputStream o = new ObjectOutputStream(fos);
            o.writeObject(this.classifier);
        } catch (IOException e) {
            return false;
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                // ignore
            }
        }
        return true;
    }

    /**
     * Gets the serialized classifier file.
     *
     * @return the file with the serialized classifier
     */
    private File getClasifierFile() {
        return OntointActivator
                .getDefault()
                .getStateLocation()
                .append("classifier.serialized")
                .toFile();
    }

}
