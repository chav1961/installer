module chav1961.installer {
	requires transitive chav1961.purelib;
	requires java.base;
	requires java.scripting;
	requires java.desktop;
	requires transitive org.openjdk.nashorn;
	requires java.sql;
	requires java.xml;
	
	exports chav1961.installer;
	exports chav1961.installer.interfaces;
	opens chav1961.installer.plugins;
	opens chav1961.installer to chav1961.purelib;
	
	uses chav1961.installer.interfaces.InstallationPlugin;
	uses chav1961.installer.interfaces.InstallationService;

	provides chav1961.installer.interfaces.InstallationPlugin with chav1961.installer.plugins.StandardPlugin;
	provides chav1961.installer.interfaces.InstallationService with chav1961.installer.products.HelloWorldProduct;
}
