package chav1961.installer;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import chav1961.installer.interfaces.InstallationPlugin;
import chav1961.installer.interfaces.InstallationService;
import chav1961.installer.internal.ProductSelector;
import chav1961.purelib.basic.ArgParser;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.CommandLineParametersException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.useful.JSimpleSplash;

// https://nsis.sourceforge.io/Simple_tutorials
public class Application {
	public static final Map<String, Object>	INSTALLATION_CONTEXT = new HashMap<>();
	public static final String	CTX_SERVICE = "__service__";
	public static final String	CTX_WIZARD = "__wizadr__";
	
	private static final String	ARG_DEBUG = "d";
	private static final URI	LOCALIZER_URI = URI.create(Localizer.LOCALIZER_SCHEME+":xml:root://"+Application.class.getName()+"/i18n.xml");
	
	public static void main(final String[] args) {
		final ArgParser	parser = new ApplicationArgParser();
		
		try(final JSimpleSplash	jss = new JSimpleSplash();
			final Localizer		localizer = Localizer.Factory.newInstance(LOCALIZER_URI)) {
			jss.start("Prepare installer", 3);
			
			final ArgParser				parsed = parser.parse(args);
			final ScriptEngineManager 	mgr = new ScriptEngineManager();
			final ScriptEngine 			engine = mgr.getEngineByName("nashorn");
			
			if (engine != null) {
				jss.processed(1);
				
				final Bindings	bindings = engine.createBindings();
		
				for(InstallationPlugin plugin : ServiceLoader.load(InstallationPlugin.class)) {
					if (plugin.isCurrentOsSupported(PureLibSettings.CURRENT_OS)) {
						bindings.put(plugin.getPluginVariable(), plugin.getInterface(PureLibSettings.CURRENT_OS));
					}
				}
				engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
				jss.processed(2);
				
				final List<InstallationService>	installations = new ArrayList<>();
				
				for(InstallationService service : ServiceLoader.load(InstallationService.class)) {
					if (service.isCurrentOsSupported(PureLibSettings.CURRENT_OS)) {
						installations.add(service);
					}
				}
				if (!installations.isEmpty()) {
					PureLibSettings.PURELIB_LOCALIZER.push(localizer);
					final Wizard			w = new Wizard(localizer);
					final ProductSelector	ps = new ProductSelector(localizer, installations);

					bindings.put("WIZARD", w);
					INSTALLATION_CONTEXT.put(CTX_WIZARD, w);
					try {
						w.setVisible(true);
						switch (w.pushContent("_sp_", localizer, ProductSelector.SEL_TITLE, ps, false, (c)->ps.validateContent())) {
							case CANCEL : case COMPLETE : case PREVIOUS : case CANCEL_WITH_KEEP_SETTINGS :
								break;
							case NEXT	:
								final InstallationService	service = ps.getServiceSelected();

								bindings.put("SERVICE", service);
								INSTALLATION_CONTEXT.put(CTX_SERVICE, service);
								
								localizer.push(service.getLocalizer());
								engine.eval(service.getInstallationScript(PureLibSettings.CURRENT_OS));
								break;
							default:
								throw new UnsupportedOperationException("Wizard action is not supported yet");
						}
					} finally {
						w.setVisible(false);
						w.dispose();
					}
				}
				else {
					throw new CommandLineParametersException("No any installation services found - nothing to install");
				}
			}
			else {
				throw new CommandLineParametersException("Nashorn JS engine not found");
			}
		} catch (CommandLineParametersException e) {
			System.err.println(e.getLocalizedMessage());
			System.err.println(parser.getUsage("installer"));
			System.exit(128);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(130);
		}		
	}
	
	static class ApplicationArgParser extends ArgParser {
		private static final ArgParser.AbstractArg[]	KEYS = {
			new BooleanArg(ARG_DEBUG, false, "Turn on debug trace", false)
		};
		
		private ApplicationArgParser() {
			super(KEYS);
		}
	}
}
