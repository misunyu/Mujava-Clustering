package mujava.plugin;

import kaist.selab.util.MuJavaLogger;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class MuJavaPlugin extends AbstractUIPlugin {

	// The shared instance.
	private static MuJavaPlugin plugin;

	// The plug-in ID
	public static final String PLUGIN_ID = "mujava.plugin";

	/**
	 * The constructor.
	 */
	public MuJavaPlugin() {
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		MuJavaLogger.getLogger().info("");
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static MuJavaPlugin getDefault() {
		return plugin;
	}
}
