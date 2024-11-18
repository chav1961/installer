module chav1961.installer {
	requires transitive chav1961.purelib;
	requires java.base;
	requires java.desktop;
	
	exports chav1961.installer;
	opens chav1961.installer to chav1961.purelib;
}
