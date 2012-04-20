package de.unipassau.im.ontoint;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

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
     * The constructor.
     */
    public OntointActivator() { }

    /**
     * {@inheritDoc}
     */
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        OntointActivator.plugin = this;
    }

    /**
     * {@inheritDoc}
     */
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
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

}
