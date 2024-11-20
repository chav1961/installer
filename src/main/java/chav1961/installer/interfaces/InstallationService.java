package chav1961.installer.interfaces;

import java.net.URI;

import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.PureLibSettings.CurrentOS;
import chav1961.purelib.i18n.interfaces.Localizer;

public interface InstallationService {
	Localizer getLocalizer();
	String getProductName();
	String getProductDescription();
	DottedVersion getProductVersion();
	String getProductVendor();
	URI getProductSite();
	boolean isCurrentOsSupported(CurrentOS os);
	String getInstallationScript(CurrentOS os);
}