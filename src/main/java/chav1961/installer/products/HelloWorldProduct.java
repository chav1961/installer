package chav1961.installer.products;

import java.net.URI;

import chav1961.installer.interfaces.InstallationService;
import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.PureLibSettings.CurrentOS;
import chav1961.purelib.i18n.XMLLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;

public class HelloWorldProduct implements InstallationService {
	private final Localizer		current;
	
	public HelloWorldProduct() {
		this.current = Localizer.Factory.newInstance(URI.create(XMLLocalizer.LOCALIZER_SCHEME+":xml:root://"+getClass().getName()+"/"+getClass().getPackageName().replace('.', '/')+"/i18n.xml"));
	}
	
	@Override
	public Localizer getLocalizer() {
		return current;
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
		return new DottedVersion("1.0.0");
	}

	@Override
	public String getProductVendor() {
		return "aka chav1961";
	}

	@Override
	public URI getProductSite() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isCurrentOsSupported(final CurrentOS os) {
		if (os == null) {
			throw new NullPointerException("OS to test can't be null");
		}
		else {
			switch (os) {
				case BSD		:
				case LINUX		:
				case MACOS		:
				case OPEN_VMS	:
				case OS2		:
				case OSX		:
				case SUN_OS		:
				case UNKNOWN	:
					return false;
				case WINDOWS	:
					return true;
				default:
					throw new UnsupportedOperationException("OS type ["+os+"] is not supported yet");
			}
		}
	}

	@Override
	public String getInstallationScript(CurrentOS os) {
		// TODO Auto-generated method stub
		return null;
	}

}
