function install() {
	var properties = STD.loadOptions("c:/tmp/assa.props", false);
	var jdbcOptions = {driver:"",connString:"",user:"",password:""};
	
	STD.extractOptions(properties, jdbcOptions);
	var screen = STD.getJdbcSettingsScreen(jdbcOptions);
	var result; 
	
	switch (
		WIZARD.pushContent(
			"helloworld.jdbc", 
			SERVICE.getLocalizer(), 
			SERVICE.getProductName(), 
			"helloworld.jdbc.settings", 
			screen, 
			false, 
			function(c){return screen.checkOptions();},
			function(c){return screen.isNextActionAvailable();}).name()
		) {
			case 'PREVIOUS':
				WIZARD.popContent('_sp_');		
				result = false;
				return result;		
			case 'NEXT':
				print(jdbcOptions = screen.getOptions());
				break;		
			case 'CANCEL':
				print("Cancel");
				result = true;
				return result;		
			case 'CANCEL_WITH_KEEP_SETTINGS':
				print("Save and Cancel");
				STD.saveOptions("c:/tmp/assa.props", screen.getOptions());
				
				result = true;
				return result;		
	}

	screen = STD.getSelectTargetDirScreen({targetDir:"",freeMemoryRequired:1000,dirMustBeEmpty:true,dirWillBeCleaned:true});
	var targetDirOptions;

	switch (
		WIZARD.pushContent(
			"helloworld.last", 
			SERVICE.getLocalizer(), 
			SERVICE.getProductName(), 
			"helloworld.select.target", 
			screen, 
			false, 
			function(c){return screen.checkOptions();},
			function(c){return screen.isNextActionAvailable();}).name()
		) {
			case 'PREVIOUS':
				WIZARD.popContent('_sp_');		
				result = false;
				break;		
			case 'COMPLETE':
				print(targetDirOptions = screen.getOptions());
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
		
	screen = STD.getLastScreen({showReadme:true});
	var lastScreenOptions;
	
	switch (
		WIZARD.pushContent(
			"helloworld.last", 
			SERVICE.getLocalizer(), 
			SERVICE.getProductName(), 
			"helloworld.install.complete", 
			screen, 
			true, 
			function(c){return screen.checkOptions();}).name()
		) {
			case 'PREVIOUS':
				WIZARD.popContent('_sp_');		
				result = false;
				break;		
			case 'COMPLETE':
				print(lastScreenOptions = screen.getOptions());
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
	return result;
}

install();