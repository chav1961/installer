package chav1961.installer.plugins;

import java.net.URI;

import chav1961.installer.interfaces.InstallationPlugin;
import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.PureLibSettings.CurrentOS;

public class StandardPlugin implements InstallationPlugin {
	private static final URI			SERVICE_SITE = URI.create("https://github.com/chav1961");
	private static final DottedVersion	SERVICE_VERSION = new DottedVersion("1.0.0");

	@Override
	public String getPluginName() {
		return "Standard plugin";
	}

	@Override
	public String getVendor() {
		return "aka";
	}

	@Override
	public String getDescription() {
		return "Standard plugin contains most often used methods";
	}

	@Override
	public DottedVersion getVersion() {
		return SERVICE_VERSION;
	}

	@Override
	public URI getPluginSite() {
		return SERVICE_SITE;
	}

	@Override
	public String getPluginVariable() {
		return "STD";
	}

	@Override
	public boolean isCurrentOsSupported(final CurrentOS os) {
		if (os == null) {
			throw new NullPointerException("Os can't be null");
		}
		else {
			return true;
		}
	}

	@Override
	public Object getInterface(final CurrentOS os) {
		if (os == null) {
			throw new NullPointerException("Os can't be null");
		}
		else {
			return new StandardUtilities();
		}
	}
}
