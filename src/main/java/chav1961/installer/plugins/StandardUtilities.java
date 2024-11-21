package chav1961.installer.plugins;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import chav1961.installer.Application;
import chav1961.installer.Wizard;
import chav1961.installer.interfaces.ExitOptions;
import chav1961.purelib.ui.swing.useful.JLocalizedOptionPane;

public class StandardUtilities {
	private static final String		STD_CONFIRM_EXIT_TITLE = "std.confirm.exit.title";
	private static final String		STD_CONFIRM_EXIT_MESSAGE = "std.confirm.exit.message";
	private static final String		STD_CONFIRM_EXIT_KEEP_SETTINGS = "std.confirm.exit.keep.settings";
	
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

	public static StandardUtilities getInstance() {
		return instance;
	}
}
