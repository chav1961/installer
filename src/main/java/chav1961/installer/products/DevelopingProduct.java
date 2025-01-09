package chav1961.installer.products;

import java.net.URI;

import javax.swing.Icon;

import chav1961.installer.interfaces.InstallationService;
import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.PureLibSettings.CurrentOS;
import chav1961.purelib.i18n.interfaces.Localizer;

public class DevelopingProduct implements InstallationService {

	@Override
	public Localizer getLocalizer() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProductName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProductDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DottedVersion getProductVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProductVendor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public URI getProductSite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getProductIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getAvatar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCurrentOsSupported(CurrentOS os) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getInstallationScript(CurrentOS os) {
		// TODO Auto-generated method stub
		return null;
	}

}
