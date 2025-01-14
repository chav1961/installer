var screen = STD.getJdbcSettingsScreen({driver:"C:/svns/dev/trunk/postgresql-42.4.1.jar",connString:"jdbc:postgresql://localhost:5432/postgres",user:"postgres",password:"postgres"});
var result; 

switch (
	WIZARD.pushContent(
		"helloworld.jdbc", 
		SERVICE.getLocalizer(), 
		SERVICE.getProductName(), 
		"helloworld.jdbc.settings", 
		screen, 
		false, 
		function(c){return false;}).name()
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

screen = STD.getLastScreen({showReadme:true});

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