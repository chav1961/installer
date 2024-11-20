package chav1961.installer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import chav1961.installer.interfaces.InstallationPlugin;
import chav1961.installer.interfaces.InstallationService;
import chav1961.purelib.basic.ArgParser;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.PureLibSettings.CurrentOS;
import chav1961.purelib.basic.exceptions.CommandLineParametersException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.interfaces.PureLibStandardIcons;
import chav1961.purelib.ui.swing.useful.JSimpleSplash;

// https://nsis.sourceforge.io/Simple_tutorials
public class Application {
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
					
					final Wizard	w = new Wizard(localizer);
					final Thread	show = new Thread(()->{w.setVisible(true);});
					
					show.setDaemon(true);
					show.start();
					final InstallationService	selected = installations.size() == 1 ? installations.get(0) : w.selectProduct2Install(installations);
					
					if (selected != null) {
						bindings.put("CURRENT", selected);						
						engine.eval(selected.getInstallationScript(PureLibSettings.CURRENT_OS));
					}
					else {
						w.cancel();
					}
				}
				else {
					throw new CommandLineParametersException("No any installation services found - nothing to install");
				}
			}
			else {
				throw new CommandLineParametersException("Nashorn JS engine not found");
			}
		} catch (ScriptException e) {
			e.printStackTrace();
			System.exit(129);
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
