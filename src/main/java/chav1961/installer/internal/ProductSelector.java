package chav1961.installer.internal;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import chav1961.installer.interfaces.InstallationService;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.Localizer.LocaleChangeListener;
import chav1961.purelib.i18n.interfaces.LocalizerOwner;

public class ProductSelector extends JPanel implements LocaleChangeListener, LocalizerOwner {
	private static final long serialVersionUID = -8602909912476623182L;
	
	private static final String		SEL_PREAMBLE = "sel.preamble";
	private static final String		SEL_SELECT = "sel.select";

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
	
	@Override
	public void localeChanged(final Locale oldLocale, final Locale newLocale) throws LocalizationException {
		fillLocalizedStrings();
	}

	private JList<InstallationService> prepareProductList(final List<InstallationService> products) {
		final JList<InstallationService>		result = new JList<>(products.toArray(new InstallationService[products.size()]));
		
		return result;
	}
	
	private void fillLocalizedStrings() {
		// TODO Auto-generated method stub
		
		description.setText(getLocalizer().getValue(SEL_PREAMBLE));
		toSelect.setModel(toSelect.getModel());
	}

}
