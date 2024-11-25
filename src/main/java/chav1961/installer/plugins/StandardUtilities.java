package chav1961.installer.plugins;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import chav1961.installer.Application;
import chav1961.installer.Wizard;
import chav1961.installer.interfaces.ExitOptions;
import chav1961.installer.interfaces.InstallationService;
import chav1961.installer.interfaces.OptionsKeeper;
import chav1961.installer.internal.ContentKeeper;
import chav1961.installer.internal.InternalUtils;
import chav1961.purelib.basic.NamedValue;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;


// https://github.com/benjiman/nashorn-jsonparser/blob/master/src/main/resources/com/benjiweber/nashorn/jsonparser/jsonparser.js
public class StandardUtilities {
	private static final String		KEY_TEXT = "text";
	private static final String		KEY_PARAMS = "params";
	
	private static final String		STD_CONFIRM_EXIT_TITLE = "std.confirm.exit.title";
	private static final String		STD_CONFIRM_EXIT_MESSAGE = "std.confirm.exit.message";
	private static final String		STD_CONFIRM_EXIT_KEEP_SETTINGS = "std.confirm.exit.keep.settings";

	private static final String		NAME_LAST_SCREEN_FIELD_NEED_REBOOT = "needReboot";
	private static final String		NAME_LAST_SCREEN_FIELD_RUN_APPLICATION = "runApplication";
	private static final String		NAME_LAST_SCREEN_FIELD_SHOW_README = "showReadme";
	private static final String		STD_LAST_SCREEN_PREAMBLE = "std.last.screen.preamble";
	private static final String		STD_LAST_SCREEN_FIELD_NEED_REBOOT = "std.last.screen.field.need.reboot";
	private static final String		STD_LAST_SCREEN_FIELD_RUN_APPLICATION = "std.last.screen.field.run.application";
	private static final String		STD_LAST_SCREEN_FIELD_SHOW_README = "std.last.screen.field.show.readme";
	
	private static final StandardUtilities	instance = new StandardUtilities();
	
	public ExitOptions confirmExit() {
		final Wizard	w = (Wizard)Application.INSTALLATION_CONTEXT.get(Application.CTX_WIZARD);
		final JPanel	panel = new JPanel(new BorderLayout(5, 5));
		final JCheckBox	keepSettings = new JCheckBox(w.getLocalizer().getValue(STD_CONFIRM_EXIT_KEEP_SETTINGS));
		
		panel.add(new JLabel(w.getLocalizer().getValue(STD_CONFIRM_EXIT_MESSAGE)), BorderLayout.CENTER);
		panel.add(keepSettings, BorderLayout.SOUTH);
		
		if (new JLocalizedOptionPane(w.getLocalizer()).confirm(w, panel, STD_CONFIRM_EXIT_TITLE, JOptionPane.WARNING_MESSAGE, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
			return keepSettings.isSelected() ? ExitOptions.EXIT_AND_SAVE : ExitOptions.EXIT;
		}
		else {
			return ExitOptions.CANCEL; 
		}
	}

	/*
	 * {"needReboot":true/false,"runApplication":true/false,"showReadMe":true/false}
	 */
	public JComponent getLastScreen(final ScriptObjectMirror parm) {
		final Wizard					w = (Wizard)Application.INSTALLATION_CONTEXT.get(Application.CTX_WIZARD);
		final InstallationService		service = (InstallationService)Application.INSTALLATION_CONTEXT.get(Application.CTX_SERVICE);
		final List<ContentKeeper<?>>	options = new ArrayList<>();
		
		if (parm.hasMember(NAME_LAST_SCREEN_FIELD_NEED_REBOOT)) {
			options.add(new ContentKeeper<JCheckBox>(
					NAME_LAST_SCREEN_FIELD_NEED_REBOOT,
					new JCheckBox("",(Boolean)parm.getMember(NAME_LAST_SCREEN_FIELD_NEED_REBOOT)), 
					Utils.mkMap(new NamedValue<String>(KEY_TEXT, STD_LAST_SCREEN_FIELD_NEED_REBOOT))
					)
			);
		}
		if (parm.hasMember(NAME_LAST_SCREEN_FIELD_RUN_APPLICATION)) {
			options.add(new ContentKeeper<JCheckBox>(
					NAME_LAST_SCREEN_FIELD_RUN_APPLICATION,
					new JCheckBox("",(Boolean)parm.getMember(NAME_LAST_SCREEN_FIELD_RUN_APPLICATION)), 
					Utils.mkMap(new NamedValue<String>(KEY_TEXT, STD_LAST_SCREEN_FIELD_RUN_APPLICATION))
					)
			);
		}
		if (parm.hasMember(NAME_LAST_SCREEN_FIELD_SHOW_README)) {
			options.add(new ContentKeeper<JCheckBox>(
					NAME_LAST_SCREEN_FIELD_SHOW_README,
					new JCheckBox("",(Boolean)parm.getMember(NAME_LAST_SCREEN_FIELD_SHOW_README)), 
					Utils.mkMap(new NamedValue<String>(KEY_TEXT, STD_LAST_SCREEN_FIELD_SHOW_README))
					)
			);
		}
		return new LastScreenContent(w.getLocalizer(),
				new ContentKeeper<JEditorPane>(
					"text",
					InternalUtils.createEditorPane(),
					Utils.mkMap(new NamedValue(KEY_TEXT, STD_LAST_SCREEN_PREAMBLE),
						new NamedValue(KEY_PARAMS, new Supplier[] {
								()->service.getLocalizer().getValue(service.getProductName()),
								()->service.getLocalizer().getValue(service.getProductDescription()),
								()->service.getProductVersion().toString(),
								()->service.getLocalizer().getValue(service.getProductVendor().toString()),
								()->service.getProductSite()
							})
					)
				),
				options
		);
	}

	/*
	 * {"driver":"path","connString":"connectionString","user":"name","password":"password"}
	 */
	public JComponent getJdbcSettingsScreen(final ScriptObjectMirror parm) {
		return null;
	}	
	
	public static class LastScreenContent extends JPanel implements LocaleChangeListener, OptionsKeeper<LastScreenContent.Options> {
		private static final long serialVersionUID = 7203785897240864944L;

		private final Localizer					localizer;
		private final ContentKeeper<?>			preamble;
		private final List<ContentKeeper<?>>	options;
		
		private LastScreenContent(final Localizer localizer, final ContentKeeper<?> preamble, final List<ContentKeeper<?>> options) {
			super(new BorderLayout(10, 10));
			this.localizer = localizer;
			this.preamble = preamble;
			this.options = options;
			final JPanel	optList = new JPanel(new GridLayout(options.size(), 1));
			
			add((JComponent)preamble.content, BorderLayout.NORTH);
			for(ContentKeeper<?> item : options) {
				optList.add((JComponent)item.content);
			}
			add(optList, BorderLayout.CENTER);
		}

		@Override
		public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
			fillLocalizedStrings();
		}
		
		@Override
		public Options getOptions() {
			boolean	needReboot = false;
			boolean	runApplication = false;
			boolean	showReadme = false;
			
			for (ContentKeeper<?> item : options) {
				switch (item.name) {
					case NAME_LAST_SCREEN_FIELD_NEED_REBOOT		:
						needReboot = ((JCheckBox)item.content).isSelected();
						break;
					case NAME_LAST_SCREEN_FIELD_RUN_APPLICATION	:
						runApplication = ((JCheckBox)item.content).isSelected();
						break;
					case NAME_LAST_SCREEN_FIELD_SHOW_README		:
						showReadme = ((JCheckBox)item.content).isSelected();
						break;
					default :
						throw new UnsupportedOperationException("Item name ["+item.name+"] is not supported yet");
				}
			}
			return new Options(needReboot, runApplication, showReadme);
		}

		public static class Options {
			public final boolean needReboot;
			public final boolean runApplication;
			public final boolean showReadme;
			
			private Options(final boolean needReboot, final boolean runApplication, final boolean showReadme) {
				this.needReboot = needReboot;
				this.runApplication = runApplication;
				this.showReadme = showReadme;
			}

			@Override
			public String toString() {
				return "Options [needReboot=" + needReboot + ", runApplication=" + runApplication + ", showReadme=" + showReadme + "]";
			}
		}
		
		private void fillLocalizedStrings() {
			final Supplier<String>[]	values = (Supplier<String>[])preamble.attributes.get(KEY_PARAMS);
			final Object[]				parms = new Object[values.length];
			
			for(int index = 0; index < parms.length; index++) {
				parms[index] = values[index].get();
			}
			((JEditorPane)preamble.content).setText(String.format(InternalUtils.loadHtml(localizer, preamble.attributes.get(KEY_TEXT).toString()), parms));
			for (ContentKeeper<?> item : options) {
				((JCheckBox)item.content).setText(localizer.getValue(item.attributes.get(KEY_TEXT).toString()));
			}
		}
	}

	public static StandardUtilities getInstance() {
		return instance;
	}
}
