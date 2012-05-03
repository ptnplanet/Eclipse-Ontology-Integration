package de.unipassau.im.ontoint;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.unipassau.im.ontoint.model.WrappedOWLOntologyManager;

/**
 * The activator class controls the plug-in life cycle.
 */
public final class OntointActivator extends AbstractUIPlugin {

    /**
     * The unique plug-in ID.
     */
    public static final String PLUGIN_ID = "de.unipassau.im.ontoint";

    /**
     * The shared instance.
     */
    private static OntointActivator plugin;

    /**
     * The ontology manager to use throughout this plugin's model.
     */
    private WrappedOWLOntologyManager manager;

    /**
     * The constructor.
     */
    public OntointActivator() {
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
    }

    /**
     * {@inheritDoc}
     */
    public void stop(final BundleContext context) throws Exception {
        OntointActivator.plugin = null;
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

}
