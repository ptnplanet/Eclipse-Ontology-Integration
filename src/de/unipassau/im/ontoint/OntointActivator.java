package de.unipassau.im.ontoint;

import java.io.File;

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
        this.classifier = new BayesClassifier<ContextFeature, String>();
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

        new ImportOntologyFileJob("Importing Ontology", new File("/Library/WebServer/Documents/Astronomy.owl"))
        .schedule();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(final BundleContext context) throws Exception {
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
    public static ImageDescriptor getImageDescriptor(String imageFilePath) {
        return OntointActivator.imageDescriptorFromPlugin(
                OntointActivator.PLUGIN_ID, imageFilePath);
    }

    public Classifier<ContextFeature, String> getClassifier() {
        return this.classifier;
    }

}
