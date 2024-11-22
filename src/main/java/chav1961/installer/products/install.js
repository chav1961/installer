WIZARD.pushContent(
	"helloworld.last", 
	SERVICE.getLocalizer(), 
	SERVICE.getProductName(), 
	"helloworld.install.complete", 
	STD.getLastScreen({showReadme:true}), 
	true, 
	function(c){return true;}
);
