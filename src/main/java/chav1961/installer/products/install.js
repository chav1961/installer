var screen = STD.getLastScreen({showReadme:true});
var result; 

switch (
	WIZARD.pushContent(
		"helloworld.last", 
		SERVICE.getLocalizer(), 
		SERVICE.getProductName(), 
		"helloworld.install.complete", 
		screen, 
		true, 
		function(c){return true;}).name()
	) {
		case 'PREVIOUS':
			WIZARD.popContent('_sp_');		
			result = false;
			break;		
		case 'COMPLETE':
			print(screen.getOptions());
			result = true;
			break;		
		case 'CANCEL':
			print("Cancel");
			result = true;
			break;		
		case 'CANCEL_WITH_KEEP_SETTINGS':
			print("Save and Cancel");
			result = true;
			break;		
}
result;