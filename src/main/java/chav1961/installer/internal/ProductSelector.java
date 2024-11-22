package chav1961.installer.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import chav1961.installer.interfaces.InstallationService;
import chav1961.purelib.basic.MimeType;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;
import chav1961.purelib.ui.swing.SwingUtils;

public class ProductSelector extends JPanel implements LocaleChangeListener, LocalizerOwner {
	private static final long serialVersionUID = -8602909912476623182L;
	
	public static final String		SEL_STEP = "sel.step";
	public static final String		SEL_TITLE = "sel.title";
	private static final String		SEL_PREAMBLE = "sel.preamble";
	private static final String		SEL_NO_SELECTION = "sel.no.selection";
	private static final Icon		AVATAR = new ImageIcon(ProductSelector.class.getResource("avatar.png"));

	private final Localizer			localizer;
	private final JList<InstallationService>	toSelect;
	private final JLabel			description = new JLabel();

	public ProductSelector(final Localizer localizer, final List<InstallationService> products) {
		super(new BorderLayout(10, 10));
		
		if (localizer == null) {
			throw new NullPointerException("Localizer can't be null");
		}
		else if (products == null || products.isEmpty()) {
			throw new IllegalArgumentException("Products list can't be null or empty");
		}
		else {
			final JPanel	inside = new JPanel();
			
			this.localizer = localizer;
			this.toSelect = prepareProductList(products);
			
			inside.add(new JScrollPane(toSelect));
			add(description, BorderLayout.NORTH);
			add(inside, BorderLayout.CENTER);
			
			fillLocalizedStrings();
		}
	}

	@Override
	public Localizer getLocalizer() {
		return localizer;
	}
	
	public Icon getAvatar() {
		return AVATAR;
	}
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		final DefaultListModel<InstallationService>	model = (DefaultListModel<InstallationService>)toSelect.getModel();
		
		for(int index = 0; index < model.getSize(); index++) {
			model.get(index).getLocalizer().setCurrentLocale(newLocale);
		}
		fillLocalizedStrings();
	}

	public InstallationService getServiceSelected() {
		return toSelect.getSelectedValue();
	}
	
	public boolean validateContent() {
		if (getServiceSelected() != null) {
			return true;
		}
		else {
			SwingUtils.getNearestLogger(this).message(Severity.warning, getLocalizer().getValue(SEL_NO_SELECTION));
			return false;
		}
	}
	
	private JList<InstallationService> prepareProductList(final List<InstallationService> products) {
		final DefaultListModel<InstallationService>	model = new DefaultListModel<InstallationService>();
		final JList<InstallationService>			result = new JList<>(model);
		
		model.addAll(products);
		result.setMinimumSize(new Dimension(400,200));
		result.setPreferredSize(new Dimension(400,200));
		result.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 8780607307703911934L;

			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				final InstallationService	service = (InstallationService)value;
				final JLabel	label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

				label.setIcon(service.getProductIcon());
				label.setText(service.getLocalizer().getValue(service.getProductName()));
				label.setToolTipText(service.getLocalizer().getValue(service.getProductDescription()));
				return label;
			}
		});
		return result;
	}
	
	private void fillLocalizedStrings() {
		try {
			final String	html = Utils.fromResource(getLocalizer().getContent(SEL_PREAMBLE, MimeType.MIME_CREOLE_TEXT, MimeType.MIME_HTML_TEXT)); 
			
			description.setText(html.substring(html.indexOf("<html>")));
		} catch (LocalizationException | IOException e) {
			description.setText(SEL_PREAMBLE);
		}
		toSelect.setModel(toSelect.getModel());
	}
}
