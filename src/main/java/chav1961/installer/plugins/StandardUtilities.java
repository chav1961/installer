package chav1961.installer.plugins;


import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.function.Supplier;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import chav1961.installer.Application;
import chav1961.installer.Wizard;
import chav1961.installer.interfaces.ExitOptions;
import chav1961.installer.interfaces.InstallationService;
import chav1961.installer.interfaces.OptionsKeeper;
import chav1961.installer.internal.ContentKeeper;
import chav1961.installer.internal.InternalUtils;
import chav1961.installer.plugins.StandardUtilities.LastScreenContent.Options;
import chav1961.purelib.basic.NamedValue;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;
import chav1961.purelib.ui.swing.useful.LabelledLayout;


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

	private static final String		NAME_JDBC_SETTINGS_SCREEN_FIELD_DRIVER = "driver";
	private static final String		NAME_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING = "connString";
	private static final String		NAME_JDBC_SETTINGS_SCREEN_FIELD_USER = "user";
	private static final String		NAME_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD = "password";
	private static final String		STD_JDBC_SETTINGS_SCREEN_PREAMBLE = "std.jdbc.settings.screen.preamble";
	private static final String		STD_JDBC_SETTINGS_SCREEN_FIELD_DRIVER = "std.jdbc.settings.screen.field.driver";
	private static final String		STD_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING = "std.jdbc.settings.screen.field.conn.string";
	private static final String		STD_JDBC_SETTINGS_SCREEN_FIELD_USER = "std.jdbc.settings.screen.field.user";
	private static final String		STD_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD = "std.jdbc.settings.screen.field.password";
	private static final String		STD_JDBC_SETTINGS_SCREEN_BUTTON_TEST = "std.jdbc.settings.screen.field.password";
	
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

		extractValue2CheckBox(parm, NAME_LAST_SCREEN_FIELD_NEED_REBOOT, STD_LAST_SCREEN_FIELD_NEED_REBOOT, options);
//		if (parm.hasMember(NAME_LAST_SCREEN_FIELD_NEED_REBOOT)) {
//			options.add(new ContentKeeper<JCheckBox>(
//					NAME_LAST_SCREEN_FIELD_NEED_REBOOT,
//					new JCheckBox("",(Boolean)parm.getMember(NAME_LAST_SCREEN_FIELD_NEED_REBOOT)), 
//					Utils.mkMap(new NamedValue<String>(KEY_TEXT, STD_LAST_SCREEN_FIELD_NEED_REBOOT))
//					)
//			);
//		}
		extractValue2CheckBox(parm, NAME_LAST_SCREEN_FIELD_RUN_APPLICATION, STD_LAST_SCREEN_FIELD_RUN_APPLICATION, options);
//		if (parm.hasMember(NAME_LAST_SCREEN_FIELD_RUN_APPLICATION)) {
//			options.add(new ContentKeeper<JCheckBox>(
//					NAME_LAST_SCREEN_FIELD_RUN_APPLICATION,
//					new JCheckBox("",(Boolean)parm.getMember(NAME_LAST_SCREEN_FIELD_RUN_APPLICATION)), 
//					Utils.mkMap(new NamedValue<String>(KEY_TEXT, STD_LAST_SCREEN_FIELD_RUN_APPLICATION))
//					)
//			);
//		}
		extractValue2CheckBox(parm, NAME_LAST_SCREEN_FIELD_SHOW_README, STD_LAST_SCREEN_FIELD_SHOW_README, options);
//		if (parm.hasMember(NAME_LAST_SCREEN_FIELD_SHOW_README)) {
//			options.add(new ContentKeeper<JCheckBox>(
//					NAME_LAST_SCREEN_FIELD_SHOW_README,
//					new JCheckBox("",(Boolean)parm.getMember(NAME_LAST_SCREEN_FIELD_SHOW_README)), 
//					Utils.mkMap(new NamedValue<String>(KEY_TEXT, STD_LAST_SCREEN_FIELD_SHOW_README))
//					)
//			);
//		}
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
		final Wizard					w = (Wizard)Application.INSTALLATION_CONTEXT.get(Application.CTX_WIZARD);
		final InstallationService		service = (InstallationService)Application.INSTALLATION_CONTEXT.get(Application.CTX_SERVICE);
		final List<ContentKeeper<?>>	options = new ArrayList<>();
		
		extractValue2TextField(parm, NAME_JDBC_SETTINGS_SCREEN_FIELD_DRIVER, STD_JDBC_SETTINGS_SCREEN_FIELD_DRIVER, options);
//		if (parm.hasMember(NAME_JDBC_SETTINGS_SCREEN_FIELD_DRIVER)) {
//			options.add(new ContentKeeper<JTextField>(
//					NAME_JDBC_SETTINGS_SCREEN_FIELD_DRIVER,
//					new JTextField(parm.getMember(NAME_JDBC_SETTINGS_SCREEN_FIELD_DRIVER).toString()), 
//					Utils.mkMap(new NamedValue<String>(KEY_TEXT, STD_JDBC_SETTINGS_SCREEN_FIELD_DRIVER))
//					)
//			);
//		}
		extractValue2TextField(parm, NAME_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING, STD_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING, options);
//		if (parm.hasMember(NAME_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING)) {
//			options.add(new ContentKeeper<JTextField>(
//					NAME_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING,
//					new JTextField(parm.getMember(NAME_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING).toString()), 
//					Utils.mkMap(new NamedValue<String>(KEY_TEXT, STD_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING))
//					)
//			);
//		}
		extractValue2TextField(parm, NAME_JDBC_SETTINGS_SCREEN_FIELD_USER, STD_JDBC_SETTINGS_SCREEN_FIELD_USER, options);
//		if (parm.hasMember(NAME_JDBC_SETTINGS_SCREEN_FIELD_USER)) {
//			options.add(new ContentKeeper<JTextField>(
//					NAME_JDBC_SETTINGS_SCREEN_FIELD_USER,
//					new JTextField(parm.getMember(NAME_JDBC_SETTINGS_SCREEN_FIELD_USER).toString()), 
//					Utils.mkMap(new NamedValue<String>(KEY_TEXT, STD_JDBC_SETTINGS_SCREEN_FIELD_USER))
//					)
//			);
//		}
		extractValue2TextField(parm, NAME_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD, STD_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD, options);
//		if (parm.hasMember(NAME_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD)) {
//			options.add(new ContentKeeper<JTextField>(
//					NAME_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD,
//					new JPasswordField(parm.getMember(NAME_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD).toString()), 
//					Utils.mkMap(new NamedValue<String>(KEY_TEXT, STD_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD))
//					)
//			);
//		}
		return new JdbcSettingsContent(w.getLocalizer(),
				new ContentKeeper<JEditorPane>(
					"text",
					InternalUtils.createEditorPane(),
					Utils.mkMap(new NamedValue(KEY_TEXT, STD_JDBC_SETTINGS_SCREEN_PREAMBLE),
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
	 * {"driver":"path","connString":"connectionString","user":"name","password":"password","schema":"defaultName","scripts":"URI"}
	 */
	public JComponent getSchemaSettingsScreen(final ScriptObjectMirror parm) {
		final Wizard					w = (Wizard)Application.INSTALLATION_CONTEXT.get(Application.CTX_WIZARD);
		final InstallationService		service = (InstallationService)Application.INSTALLATION_CONTEXT.get(Application.CTX_SERVICE);
		final List<ContentKeeper<?>>	options = new ArrayList<>();

		extractValue2TextField(parm, NAME_JDBC_SETTINGS_SCREEN_FIELD_DRIVER, STD_JDBC_SETTINGS_SCREEN_FIELD_DRIVER, options);
		extractValue2TextField(parm, NAME_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING, STD_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING, options);
		extractValue2TextField(parm, NAME_JDBC_SETTINGS_SCREEN_FIELD_USER, STD_JDBC_SETTINGS_SCREEN_FIELD_USER, options);
		extractValue2TextField(parm, NAME_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD, STD_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD, options);
		
		return null;
	}

	private static void extractValue2CheckBox(final ScriptObjectMirror parm, final String key, final String title, final List<ContentKeeper<?>> options) {
		if (parm.hasMember(key)) {
			options.add(new ContentKeeper<JCheckBox>(
					key,
					new JCheckBox("",(Boolean)parm.getMember(key)), 
					Utils.mkMap(new NamedValue<String>(KEY_TEXT, title))
					)
			);
		}
	}
	
	private static void extractValue2TextField(final ScriptObjectMirror parm, final String key, final String title, final List<ContentKeeper<?>> options) {
		if (parm.hasMember(key)) {
			options.add(new ContentKeeper<JTextField>(
					key,
					new JTextField(parm.getMember(key).toString()), 
					Utils.mkMap(new NamedValue<String>(KEY_TEXT, title))
					)
			);
		}
	}

	private static void extractValue2PasswordField(final ScriptObjectMirror parm, final String key, final String title, final List<ContentKeeper<?>> options) {
		if (parm.hasMember(key)) {
			options.add(new ContentKeeper<JPasswordField>(
					key,
					new JPasswordField(parm.getMember(key).toString()), 
					Utils.mkMap(new NamedValue<String>(KEY_TEXT, title))
					)
			);
		}
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

	public static class JdbcSettingsContent extends JPanel implements LocaleChangeListener, OptionsKeeper<JdbcSettingsContent.Options> {
		private static final long serialVersionUID = -5453784299596229643L;
		private static final Icon				SUCCESS_ICON = new ImageIcon(JdbcSettingsContent.class.getResource(""));
		private static final Icon				FAILED_ICON = new ImageIcon(JdbcSettingsContent.class.getResource(""));

		private final Localizer					localizer;
		private final ContentKeeper<?>			preamble;
		private final List<JLabel>				labels = new ArrayList<>();
		private final List<ContentKeeper<?>>	options;
		private final JButton					test = new JButton();
		
		private JdbcSettingsContent(final Localizer localizer, final ContentKeeper<?> preamble, final List<ContentKeeper<?>> options) {
			super(new BorderLayout(10, 10));
			this.localizer = localizer;
			this.preamble = preamble;
			this.options = options;
			final JPanel	optList = new JPanel(new LabelledLayout());
			
			add((JComponent)preamble.content, BorderLayout.NORTH);
			for(ContentKeeper<?> item : options) {
				final JLabel		label = new JLabel();
				final JComponent	component = (JComponent)item.content;
				
				component.setInputVerifier(new InputVerifier() {
					@Override
					public boolean verify(final JComponent input) {
						return verifyOptions(item.name, input);
					}
				});
				optList.add(label, LabelledLayout.LABEL_AREA);
				optList.add(component, LabelledLayout.CONTENT_AREA);
				labels.add(label);
			}
			add(optList, BorderLayout.CENTER);
			add(test, BorderLayout.SOUTH);
			test.addActionListener((e)->test.setIcon(testConnection() ? SUCCESS_ICON : FAILED_ICON));
			fillLocalizedStrings();
		}
		
		@Override
		public Options getOptions() {
			String	driverFile = "";
			String	connString = "jdbc:";
			String	user = "";
			char[]	password = new char[0];
			
			for (ContentKeeper<?> item : options) {
				switch (item.name) {
					case NAME_JDBC_SETTINGS_SCREEN_FIELD_DRIVER			:
						driverFile = ((JTextField)item.content).getText();
						break;
					case NAME_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING	:
						connString = ((JTextField)item.content).getText();
						break;
					case NAME_JDBC_SETTINGS_SCREEN_FIELD_USER			:
						user = ((JTextField)item.content).getText();
						break;
					case NAME_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD		:
						password = ((JPasswordField)item.content).getPassword();
						break;
					default :
						throw new UnsupportedOperationException("Item name ["+item.name+"] is not supported yet");
				}
			}
			return new Options(new File(driverFile), URI.create(connString), user, password);
		}

		@Override
		public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
			fillLocalizedStrings();
		}

		public static class Options {
			public final File 	driver;
			public final URI 	connString;
			public final String	user;
			public final char[]	password;

			public Options(File driver, URI connString, String user, char[] password) {
				this.driver = driver;
				this.connString = connString;
				this.user = user;
				this.password = password;
			}

			@Override
			public String toString() {
				return "Options [driver=" + driver + ", connString=" + connString + ", user=" + user + "]";
			}
		}

		private void fillLocalizedStrings() {
			final Supplier<String>[]	values = (Supplier<String>[])preamble.attributes.get(KEY_PARAMS);
			final Object[]				parms = new Object[values.length];
			
			for(int index = 0; index < parms.length; index++) {
				parms[index] = values[index].get();
			}
			((JEditorPane)preamble.content).setText(String.format(InternalUtils.loadHtml(localizer, preamble.attributes.get(KEY_TEXT).toString()), parms));
			for (int index = 0; index < options.size(); index++) {
				labels.get(index).setText(localizer.getValue(localizer.getValue(options.get(index).attributes.get(KEY_TEXT).toString())));
			}
			test.setText(localizer.getValue(STD_JDBC_SETTINGS_SCREEN_BUTTON_TEST));
		}
		
		private boolean verifyOptions(final String currentOption, final JComponent input) {
			String	value = "";
			
			switch (currentOption) {
				case NAME_JDBC_SETTINGS_SCREEN_FIELD_DRIVER			:
					value = ((JTextComponent)input).getText().trim();
					
					if (Utils.checkEmptyOrNullString(value)) {
						SwingUtils.getNearestLogger(input).message(Severity.warning, "empty driver name!");
						return false;
					}
					else {
						final File	f = new File(value);
						
						if (!f.exists() || !f.isFile() || !f.canRead()) {
							SwingUtils.getNearestLogger(input).message(Severity.warning, "driver name [] not exists!");
							return false;
						}
						else {
							try(final URLClassLoader	loader = new URLClassLoader(new URL[] {f.toURI().toURL()})) {
								
								for(Driver item : ServiceLoader.load(java.sql.Driver.class, loader)) {
									return true;
								}
								SwingUtils.getNearestLogger(input).message(Severity.warning, "driver name [] not exists!");
								return false;
							} catch (IOException e) {
								SwingUtils.getNearestLogger(input).message(Severity.warning, "driver name [] not exists!");
								return false;
							}
						}
					}
				case NAME_JDBC_SETTINGS_SCREEN_FIELD_CONN_STRING	:
					value = ((JTextComponent)input).getText().trim();
					
					if (Utils.checkEmptyOrNullString(value)) {
						SwingUtils.getNearestLogger(input).message(Severity.warning, "empty driver name!");
						return false;
					}
					else {
						try{final URI	conn = URI.create(value);
							if (!"jdbc".equalsIgnoreCase(conn.getScheme())) {
								SwingUtils.getNearestLogger(input).message(Severity.warning, "driver name [] not exists!");
								return false;
							}
							else {
								return true;
							}
						} catch (IllegalArgumentException exc) {
							SwingUtils.getNearestLogger(input).message(Severity.warning, "driver name [] not exists!");
							return false;
						}
					}
				case NAME_JDBC_SETTINGS_SCREEN_FIELD_USER			:
					value = ((JTextComponent)input).getText().trim();
					
					if (Utils.checkEmptyOrNullString(value)) {
						SwingUtils.getNearestLogger(input).message(Severity.warning, "empty driver name!");
						return false;
					}
					else {
						return true;
					}
				case NAME_JDBC_SETTINGS_SCREEN_FIELD_PASSWORD		:
					final char[]	passwd = ((JPasswordField)input).getPassword();
					
					if (passwd.length == 0) {
						SwingUtils.getNearestLogger(input).message(Severity.warning, "empty driver name!");
						return false;
					}
					else {
						return true;
					}
				default :
					throw new UnsupportedOperationException("Option ["+currentOption+"] is not supported yet");
			}
		}
		
		private boolean testConnection() {
			final Options	opts = getOptions();
			
			try(final URLClassLoader	loader = new URLClassLoader(new URL[] {opts.driver.toURI().toURL()})) {
				for(Driver item : ServiceLoader.load(java.sql.Driver.class, loader)) {
					try(final Connection	conn = item.connect(opts.connString.toString(), Utils.mkProps("user", opts.user, "password", new String(opts.password)))) {
						return true;
					} catch (SQLException exc) {
						SwingUtils.getNearestLogger(this).message(Severity.warning, "empty driver name!");
						return false;
					}
				}
				SwingUtils.getNearestLogger(this).message(Severity.warning, "empty driver name!");
				return false;
			} catch (IOException exc) {
				SwingUtils.getNearestLogger(this).message(Severity.warning, "empty driver name!");
				return false;
			}
		}
	}	
	
	public static StandardUtilities getInstance() {
		return instance;
	}
}
