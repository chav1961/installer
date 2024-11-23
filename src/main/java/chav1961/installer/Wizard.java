package chav1961.installer;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
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
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import chav1961.installer.interfaces.ExitOptions;
import chav1961.installer.interfaces.InstallationService;
import chav1961.installer.interfaces.WizardAction;
import chav1961.installer.internal.ProductSelector;
import chav1961.installer.plugins.StandardUtilities;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacadeOwner;
import chav1961.purelib.model.FieldFormat;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;
import chav1961.purelib.i18n.interfaces.SupportedLanguages;
import chav1961.purelib.ui.swing.SwingUtils;
import chav1961.purelib.ui.swing.useful.JStateString;

public class Wizard extends JDialog implements LocaleChangeListener, LocalizerOwner, LoggerFacadeOwner {
	private static final long serialVersionUID = -7868719740809674349L;

	private static final String		APP_TITLE = "app.title";
	private static final String		APP_STEPS_TITLE = "app.steps.title";
	private static final String		APP_BUTTON_PREV = "app.button.prev";
	private static final String		APP_BUTTON_NEXT = "app.button.next";
	private static final String		APP_BUTTON_FINISH = "app.button.finish";
	private static final String		APP_BUTTON_CANCEL = "app.button.cancel";
	
	private final JButton			prevButton = new JButton();
	private final JButton			nextButton = new JButton();
	private final JButton			cancelButton = new JButton();
	private final JLabel			stepCaption = new JLabel();
	private final JLabel			stepsTitle = new JLabel((Icon)null, JLabel.CENTER);
	private final JLabel			avatar = new JLabel();
	private final JStateString		state;
	private final JComboBox<SupportedLanguages>	lang = prepareLangBox();
	private final JList<HistoryStack>		steps = prepareStepsList();
	private final Exchanger<WizardAction>	ex = new Exchanger<>();
	private List<Localizer>			localizer = new ArrayList<>();
	private String					caption = "*****";
	private Component				oldComponent = null;
	
	public Wizard(final Localizer localizer) {
		super(null, ModalityType.MODELESS);
		this.localizer.add(localizer);		
		this.state = new JStateString(localizer);

		getContentPane().setLayout(new BorderLayout(10, 10));
		
		localizer.addLocaleChangeListener(this);
		
		final JPanel	header = new JPanel(new BorderLayout());
		final JPanel	left = new JPanel(new BorderLayout(10, 10));
		final JPanel	separator = new JPanel(new BorderLayout());
		final JPanel	bottom = new JPanel(new BorderLayout());
		final JPanel	buttons = new JPanel(new FlowLayout());
		final Font		f = (Font)UIManager.get("Label.font");
		
		stepCaption.setHorizontalAlignment(JLabel.CENTER);
		stepCaption.setFont(f.deriveFont(Font.BOLD, 1.5f * f.getSize()));
		header.add(stepCaption, BorderLayout.CENTER); 
		header.add(lang, BorderLayout.EAST); 

		getContentPane().add(header, BorderLayout.NORTH);
		
		steps.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		steps.setPreferredSize(new Dimension(250, 250));
		avatar.setHorizontalAlignment(JLabel.CENTER);
		left.add(stepsTitle, BorderLayout.NORTH);
		left.add(steps, BorderLayout.CENTER);
		left.add(new JLabel(" "), BorderLayout.WEST);
		left.add(new JLabel(" "), BorderLayout.EAST);
		left.add(avatar, BorderLayout.SOUTH);
		getContentPane().add(left, BorderLayout.WEST);
		
		prevButton.addActionListener((e)->press(WizardAction.PREVIOUS));
		buttons.add(prevButton);
		nextButton.addActionListener((e)->press(WizardAction.NEXT));
		buttons.add(nextButton);
		cancelButton.addActionListener((e)->press(WizardAction.CANCEL));
		buttons.add(cancelButton);
		bottom.add(state, BorderLayout.CENTER);
		bottom.add(buttons, BorderLayout.EAST);
		
		separator.add(new JSeparator(), BorderLayout.NORTH);
		separator.add(bottom, BorderLayout.CENTER);
		
		getContentPane().add(separator, BorderLayout.SOUTH);
		setSize(1024, 768);
		setLocationRelativeTo(null);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				press(WizardAction.CANCEL);
			}
		});
		
		fillLocalizedStrings();
	}

	@Override
	public Localizer getLocalizer() {
		return localizer.get(0);
	}

	@Override
	public LoggerFacade getLogger() {
		return state;
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings();
		if (oldComponent != null) {
			SwingUtils.refreshLocale(oldComponent, oldLocale, newLocale);
		}
	}
	
	public void setAvatar(final Icon avatar) {
		this.avatar.setIcon(avatar);
	}
	
	public void setContent(final JComponent content) {
		if (content == null) {
			throw new NullPointerException("Content to set cna't be null");
		}
		else {
			if (oldComponent != null) {
				getContentPane().remove(oldComponent);
			}
			getContentPane().add(content, BorderLayout.CENTER);
			getContentPane().repaint();
			SwingUtils.refreshLocale(content, getLocalizer().currentLocale().getLocale(), getLocalizer().currentLocale().getLocale());
			oldComponent = content;
		}
	}
	
	public WizardAction pushContent(final String stepId, final Localizer localizer, final String stepName, final String stepTitle, final JComponent component, final boolean isTerminalNode, final Predicate<JComponent> validator) {
		if (Utils.checkEmptyOrNullString(stepId)) {
			throw new IllegalArgumentException("Step ID can't be null or empty");
		}
		else if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (Utils.checkEmptyOrNullString(stepName)) {
			throw new IllegalArgumentException("Step name can't be null or empty");
		}
		else if (component == null) {
			throw new NullPointerException("Component can't be null");
		}
		else {
			final HistoryStack	item = new HistoryStack(stepId, localizer, stepName, component, isTerminalNode);
			
			((DefaultListModel<HistoryStack>)steps.getModel()).addElement(item);
			this.localizer.add(0, localizer);
			setContent(component);
			setStepCaption(stepTitle);
			prevButton.setEnabled(steps.getModel().getSize() > 1);
			nextButton.setText(getLocalizer().getValue(isTerminalNode ? APP_BUTTON_FINISH : APP_BUTTON_NEXT));
			
			try {
				WizardAction	action;
				
				for(;;) {
					switch (action = ex.exchange(null)) {
						case CANCEL		:
							final ExitOptions	options;
							
							switch (options = StandardUtilities.getInstance().confirmExit()) {
								case CANCEL			:
									break;
								case EXIT			:
									return WizardAction.CANCEL;
								case EXIT_AND_SAVE	:
									return WizardAction.CANCEL_WITH_KEEP_SETTINGS;
								default :
									throw new UnsupportedOperationException("ExitAction type ["+options+"] is not supported yet");
							}
							break;
						case NEXT		:
							if (validator.test(component)) {
								return isTerminalNode ? WizardAction.COMPLETE : WizardAction.NEXT;
							}
							break;
						case PREVIOUS	:
							popContent();
							return WizardAction.PREVIOUS;
						default :
							throw new UnsupportedOperationException("WizardAction type ["+action+"] is not supported yet");
					}
				}
			} catch (InterruptedException e) {
				return WizardAction.CANCEL;
			}
		}
	}
	
	public void popContent() {
		final HistoryStack obj = ((DefaultListModel<HistoryStack>)steps.getModel()).remove(steps.getModel().getSize()-1);
		
		setContent(obj.component);
		this.localizer.remove(0);
	}

	public void popContent(final String stepId) {
		if (Utils.checkEmptyOrNullString(stepId)) {
			throw new IllegalArgumentException("Step ID can't be null or empty");
		}
		else {
			int	found = -1;
			
			for(int index = steps.getModel().getSize()-1; index >= 0; index--) {
				if (steps.getModel().getElementAt(index).stepId.equals(stepId)) {
					found = index;
					break;
				}
			}
			if (found == -1) {
				throw new IllegalArgumentException("Step ID ["+stepId+"] is missing in the history stack");
			}
			else {
				while (steps.getModel().getSize() > found) {
					popContent();
				}
			}
		}
	}
	
	public void complete() {
		
	}

	public void cancel() {
		setVisible(false);
		dispose();
	}

	private void setStepCaption(final String caption) {
		this.caption = caption;
		try{
			this.stepCaption.setText(getLocalizer().getValue(this.caption));
		} catch (LocalizationException exc) {
			this.stepCaption.setText(caption);
		}
	}
	
	private void press(final WizardAction action) {
		try {
			ex.exchange(action, 100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException | TimeoutException e) {
		}
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

	private JList<HistoryStack> prepareStepsList() {
		final ListModel<HistoryStack>	model = new DefaultListModel<>();
		final JList<HistoryStack>		result = new JList<>(model);
		
		result.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = -7533988324827608667L;
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				final JLabel		label = (JLabel)super.getListCellRendererComponent(list, value, index, false, false);
				final HistoryStack	item = (HistoryStack)value;
				
				label.setText(item.localizer.getValue(item.stepName));
				return label;
			}
		});
		result.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		result.setPreferredSize(new Dimension(250, 250));
		return result;
	}
	
	private void fillLocalizedStrings() {
		final DefaultListModel<HistoryStack>	model = (DefaultListModel<HistoryStack>)steps.getModel();
		
		setTitle(getLocalizer().getValue(APP_TITLE));
		prevButton.setText(getLocalizer().getValue(APP_BUTTON_PREV));
		nextButton.setText(getLocalizer().getValue(model.getSize() > 0 && model.getElementAt(model.getSize()-1).terminal ? APP_BUTTON_FINISH : APP_BUTTON_NEXT));
		cancelButton.setText(getLocalizer().getValue(APP_BUTTON_CANCEL));
		stepsTitle.setText(getLocalizer().getValue(APP_STEPS_TITLE));
		setStepCaption( caption);
	}

	private static class HistoryStack {
		private final String		stepId;
		private final Localizer		localizer;
		private final String		stepName;
		private final JComponent	component;
		private final boolean		terminal;
		
		private HistoryStack(final String stepId, final Localizer localizer, final String stepName, final JComponent component, final boolean terminal) {
			this.stepId = stepId;
			this.localizer = localizer;
			this.stepName = stepName;
			this.component = component;
			this.terminal = terminal;
		}
	}
}
