module chav1961.installer {
	requires transitive chav1961.purelib;
	requires java.base;
	requires java.desktop;
	
	exports chav1961.installer;
	exports chav1961.installer.interfaces;
	opens chav1961.installer to chav1961.purelib;
	
	uses chav1961.installer.interfaces.InstallationPlugin;
	uses chav1961.installer.interfaces.InstallationService;

	provides chav1961.installer.interfaces.InstallationService with chav1961.installer.products.HelloWorldProduct;
}
