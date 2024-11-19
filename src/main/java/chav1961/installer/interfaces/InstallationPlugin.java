package chav1961.installer.interfaces;

import java.net.URI;

import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.PureLibSettings.CurrentOS;

public interface InstallationPlugin {
	String getPluginName();
	String getVendor();
	String getDescription();
	DottedVersion getVersion();
	URI getPluginSite();
	String getPluginVariable();
	boolean isCurrentOsSupported(CurrentOS os);
	Object getInterface(CurrentOS os);
}
