package chav1961.installer;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Locale;
import java.util.UUID;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.SpringLayout;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import chav1961.installer.interfaces.WizardAction;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;
import chav1961.purelib.i18n.interfaces.SupportedLanguages;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JStateString;

public class Wizard extends JDialog implements LocaleChangeListener, LocalizerOwner {
	private static final long serialVersionUID = -7868719740809674349L;

	private static final String		APP_TITLE = "app.title";
	private static final String		APP_STEPS_TITLE = "app.steps.title";
	private static final String		APP_BUTTON_PREV = "app.button.prev";
	private static final String		APP_BUTTON_NEXT = "app.button.next";
	private static final String		APP_BUTTON_FINISH = "app.button.finish";
	private static final String		APP_BUTTON_CANCEL = "app.button.cancel";
	
	private final Localizer			localizer;
	private final JButton			prevButton = new JButton();
	private final JButton			nextButton = new JButton();
	private final JButton			cancelButton = new JButton();
	private final JLabel			stepCaption = new JLabel("?????");
	private final JLabel			stepsTitle = new JLabel((Icon)null, JLabel.CENTER);
	private final JStateString		state;
	private final JComboBox<SupportedLanguages>	lang = prepareLangBox();
	private final JList<String>		steps = prepareStepsList();
	private Component				oldComponent = null;
	
	public Wizard(final Localizer localizer) {
		super(null, ModalityType.APPLICATION_MODAL);
		this.localizer = localizer;		
		this.state = new JStateString(localizer);

		localizer.addLocaleChangeListener(this);
		
		final JPanel		header = new JPanel(new BorderLayout());
		final JPanel		left = new JPanel(new BorderLayout(10, 10));
		final JPanel		separator = new JPanel(new BorderLayout());
		final JPanel		bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		stepCaption.setHorizontalAlignment(JLabel.CENTER);
		header.add(stepCaption, BorderLayout.CENTER); 
		header.add(lang, BorderLayout.EAST); 

		getContentPane().add(header, BorderLayout.NORTH);
		
		steps.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		steps.setPreferredSize(new Dimension(250, 250));
		left.add(stepsTitle, BorderLayout.NORTH);
		left.add(steps, BorderLayout.CENTER);
		left.add(new JLabel(" "), BorderLayout.WEST);
		left.add(new JLabel(" "), BorderLayout.EAST);
		left.add(new JLabel(" "), BorderLayout.SOUTH);
		getContentPane().add(left, BorderLayout.WEST);
		
		bottom.add(prevButton);
		bottom.add(nextButton);
		bottom.add(cancelButton);
		
		separator.add(new JSeparator(), BorderLayout.NORTH);
		separator.add(bottom, BorderLayout.CENTER);
		
		getContentPane().add(separator, BorderLayout.SOUTH);
		setSize(1024, 768);
		setLocationRelativeTo(null);
		
		fillLocalizedStrings();
	}

	@Override
	public Localizer getLocalizer() {
		return localizer;
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings();
		if (oldComponent != null) {
			SwingUtils.refreshLocale(oldComponent, oldLocale, newLocale);
		}
	}
	
	public void setContent(final JComponent content) {
		if (content == null) {
			throw new NullPointerException("Content to set cna't be null");
		}
		else {
			if (oldComponent != null) {
				remove(oldComponent);
			}
			add(content, BorderLayout.CENTER);
			SwingUtils.refreshLocale(content, localizer.currentLocale().getLocale(), localizer.currentLocale().getLocale());
			oldComponent = content;
		}
	}
	
	public WizardAction pushContent(final String stepId, final String localizer, final String stepName, final JComponent component, final boolean isTerminalNode) {
		return null;
	}
	
	public WizardAction popContent() {
		return null;
	}

	public WizardAction popContent(final String stepId) {
		return null;
	}
	
	public void complete() {
		
	}

	public void cancel() {
		
	}
	
	private JComboBox<SupportedLanguages> prepareLangBox() {
		final ComboBoxModel<SupportedLanguages>	model = new DefaultComboBoxModel<SupportedLanguages>(SupportedLanguages.values()); 
		final JComboBox<SupportedLanguages>		result = new JComboBox<SupportedLanguages>(model);

		result.addActionListener((e)->{
			final SupportedLanguages	selected = (SupportedLanguages) result.getSelectedItem();
			
			PureLibSettings.PURELIB_LOCALIZER.setCurrentLocale(selected.getLocale());
		});
		result.setRenderer(SwingUtils.getCellRenderer(SupportedLanguages.class, new FieldFormat(SupportedLanguages.class), ListCellRenderer.class));
		return result;
	}

	private JList<String> prepareStepsList() {
		final ListModel<String>	model = new DefaultListModel<>();
		final JList<String>		result = new JList<>(model);
		
		steps.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		steps.setPreferredSize(new Dimension(250, 250));
		return result;
	}
	
	private void fillLocalizedStrings() {
		setTitle(getLocalizer().getValue(APP_TITLE));
		prevButton.setText(getLocalizer().getValue(APP_BUTTON_PREV));
		nextButton.setText(getLocalizer().getValue(APP_BUTTON_NEXT));
		cancelButton.setText(getLocalizer().getValue(APP_BUTTON_CANCEL));
		stepsTitle.setText(getLocalizer().getValue(APP_STEPS_TITLE));
	}
}
