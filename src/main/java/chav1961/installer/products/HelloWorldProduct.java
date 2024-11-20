package chav1961.installer.products;

import java.net.URI;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import chav1961.installer.interfaces.InstallationService;
import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.PureLibSettings.CurrentOS;
import chav1961.purelib.i18n.XMLLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;

public class HelloWorldProduct implements InstallationService {
	private static final Icon			SERVICE_ICON = new ImageIcon(HelloWorldProduct.class.getResource("hello-world.png"));
	private static final URI			SERVICE_SITE = URI.create("https://github.com/chav1961");
	private static final String			SERVICE_PRODUCT_NAME = "helloworld.product.name";
	private static final String			SERVICE_PRODUCT_DESCRIPTION = "helloworld.product.description";
	private static final String			SERVICE_PRODUCT_VENDOR = "helloworld.product.vendor";
	private static final DottedVersion	SERVICE_VERSION = new DottedVersion("1.0.0");

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
		return SERVICE_PRODUCT_NAME;
	}

	@Override
	public String getProductDescription() {
		return SERVICE_PRODUCT_DESCRIPTION;
	}

	@Override
	public DottedVersion getProductVersion() {
		return SERVICE_VERSION;
	}

	@Override
	public String getProductVendor() {
		return SERVICE_PRODUCT_VENDOR;
	}

	@Override
	public URI getProductSite() {
		return SERVICE_SITE;
	}

	@Override
	public Icon getProductIcon() {
		return SERVICE_ICON;
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
	public String getInstallationScript(final CurrentOS os) {
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
					return "";
				case WINDOWS	:
					return "";
				default:
					throw new UnsupportedOperationException("OS type ["+os+"] is not supported yet");
			}
		}
	}

}
