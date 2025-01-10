package chav1961.installer.products;

import java.net.URI;
import java.util.EnumMap;

import javax.swing.Icon;

import chav1961.installer.interfaces.InstallationService;
import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.PureLibSettings.CurrentOS;
import chav1961.purelib.i18n.MutableJsonLocalizer;

public class DevelopingProduct implements InstallationService {
	private final DottedVersion	version = new DottedVersion();
	private final EnumMap<CurrentOS, String>	scripts = new EnumMap<>(CurrentOS.class);
	private final MutableJsonLocalizer			localizer = new MutableJsonLocalizer();
	private String				productName = "<new>";
	private String				productDescription = "<new>";
	private String				productVendor = "<new>";
	private URI					productSite = URI.create("http://localhost");
	private Icon				productIcon = null;
	private Icon				productAvatar = null;

	public DevelopingProduct() {
		
	}
	
	@Override
	public MutableJsonLocalizer getLocalizer() {
		return localizer;
	}

	@Override
	public String getProductName() {
		return productName;
	}

	@Override
	public String getProductDescription() {
		return productDescription;
	}

	@Override
	public DottedVersion getProductVersion() {
		return version;
	}

	@Override
	public String getProductVendor() {
		return productVendor;
	}

	@Override
	public URI getProductSite() {
		return productSite;
	}

	@Override
	public Icon getProductIcon() {
		return productIcon;
	}

	@Override
	public Icon getAvatar() {
		return productAvatar;
	}

	@Override
	public boolean isCurrentOsSupported(final CurrentOS os) {
		return scripts.containsKey(os);
	}

	@Override
	public String getInstallationScript(final CurrentOS os) {
		return scripts.get(os);
	}

	public EnumMap<CurrentOS, String> getScripts() {
		return scripts;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public void setProductVendor(String productVendor) {
		this.productVendor = productVendor;
	}

	public void setProductSite(URI productSite) {
		this.productSite = productSite;
	}

	public void setProductIcon(Icon productIcon) {
		this.productIcon = productIcon;
	}

	public void setProductAvatar(Icon productAvatar) {
		this.productAvatar = productAvatar;
	}
	
	public void setProductVersion(final DottedVersion version) {
		this.version.assign(version);
	}
}
