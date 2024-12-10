package chav1961.installer.products;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import chav1961.installer.interfaces.InstallationService;
import chav1961.purelib.basic.DottedVersion;
import chav1961.purelib.basic.PureLibSettings.CurrentOS;
import chav1961.purelib.basic.URIUtils;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.XMLUtils;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.exceptions.EnvironmentException;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.enumerations.ContinueMode;
import chav1961.purelib.enumerations.NodeEnterMode;
import chav1961.purelib.i18n.AbstractLocalizer;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.i18n.interfaces.SupportedLanguages;

/*
 * <product name="product name" version="v.v.v" vendor="vendor" site="href">
 * 		<description>
 * 			description
 * 		</description>
 * 		<avatar>
 * 			base64content
 * 		</avatar>
 * 		<icon>
 * 			base64content
 * 		</icon>
 * 		<script osType="">
 * 			script
 *		</script>
 *		</localization>
 *			xmlLocalizer-styled content
 *		</localization>
 *		<advanced>
 *			<lang name="">
 *				<key name="name>content</key>
 *			</lang>
 *		<advanced>
 * </product>
 */

public class XMLBasedProduct implements InstallationService {
	private final String		name;
	private final String		description;
	private final String		vendor;
	private final DottedVersion	version;
	private final URI			site;
	private final Icon			avatar;
	private final Icon			icon;
	private final EnumMap<CurrentOS, String>	map = new EnumMap<>(CurrentOS.class);
	private final Localizer		localizer;
	
	public XMLBasedProduct(final InputStream is) throws IOException {
		try {
			final DocumentBuilder	builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			final Document 			doc = builder.parse(is);
			
			doc.getDocumentElement().normalize();
			final NamedNodeMap		nnMap = doc.getElementsByTagName("product").item(0).getAttributes();
			
			this.name = nnMap.getNamedItem("name").getTextContent().trim();
			this.vendor = nnMap.getNamedItem("vendor").getTextContent().trim();
			this.version = new DottedVersion(nnMap.getNamedItem("vendor").getTextContent().trim());
			this.site = URI.create(nnMap.getNamedItem("vendor").getTextContent().trim());
			this.description = doc.getElementsByTagName("description").item(0).getTextContent().trim();
			this.avatar = new ImageIcon(URI.create("self:/#")+doc.getElementsByTagName("icon").item(0).getTextContent().trim());
			this.icon = new ImageIcon(URI.create("self:/#")+doc.getElementsByTagName("avatar").item(0).getTextContent().trim());
			this.localizer = new InternalLocalizer((Element)doc.getElementsByTagName("localization").item(0), (Element)doc.getElementsByTagName("advanced").item(0));
		} catch (ParserConfigurationException | SAXException | ContentException e) {
			throw new IOException(e.getLocalizedMessage(), e);
		}
	}

	@Override
	public Localizer getLocalizer() {
		return localizer;
	}

	@Override
	public String getProductName() {
		return name;
	}

	@Override
	public String getProductDescription() {
		return description;
	}

	@Override
	public DottedVersion getProductVersion() {
		return version;
	}

	@Override
	public String getProductVendor() {
		return vendor;
	}

	@Override
	public URI getProductSite() {
		return site;
	}

	@Override
	public Icon getProductIcon() {
		return icon;
	}

	@Override
	public Icon getAvatar() {
		return avatar;
	}

	@Override
	public boolean isCurrentOsSupported(CurrentOS os) {
		if (os == null) {
			throw new NullPointerException("OS can't be null");
		}
		else {
			return map.containsKey(os);
		}
	}

	@Override
	public String getInstallationScript(final CurrentOS os) {
		if (os == null) {
			throw new NullPointerException("OS can't be null");
		}
		else if (!isCurrentOsSupported(os)) {
			throw new UnsupportedOperationException("Current os ["+os+"] is not supported yet");
		}
		else {
			return map.get(os);
		}
	}
	
	private static class InternalLocalizer extends AbstractLocalizer {
		private static final URI	INTERNAL_URI = URI.create(Localizer.LOCALIZER_SCHEME+":internal:/");

		private final Set<String>	keys = new HashSet<>();
		private final EnumMap<SupportedLanguages, Map<String, String>>	pairs = new EnumMap<>(SupportedLanguages.class);
		private final EnumMap<SupportedLanguages, Map<String, String>>	helps = new EnumMap<>(SupportedLanguages.class);

		InternalLocalizer(final Element keys, final Element helps) throws ContentException, NullPointerException {
			XMLUtils.walkDownXML(keys, (mode, node)->{
				if (mode == NodeEnterMode.ENTER) {
					switch (node.getTagName()) {
						case "lang"	: 
						case "key"	:
						default :
					}
				}
				return ContinueMode.CONTINUE;
			});
			XMLUtils.walkDownXML(helps, (mode, node)->{
				if (mode == NodeEnterMode.ENTER) {
					switch (node.getTagName()) {
						case "lang"	: 
						case "key"	:
						default :
					}
				}
				return ContinueMode.CONTINUE;
			});
		}
		
		@Override
		public URI getLocalizerId() {
			return INTERNAL_URI;
		}

		@Override
		public boolean canServe(final URI resource) throws NullPointerException {
			if (resource == null) {
				throw new NullPointerException("Resource can't be null");
			}
			else {
				return URIUtils.canServeURI(resource, INTERNAL_URI);
			}
		}

		@Override
		public Localizer newInstance(final URI resource) throws EnvironmentException, NullPointerException, IllegalArgumentException {
			if (resource == null) {
				throw new NullPointerException("Resource can't be null");
			}
			else if (!canServe(resource)){
				throw new EnvironmentException("Can't serve ["+resource+"] resource"); 
			}
			else {
				return this;
			}
		}

		@Override
		public Iterable<String> localKeys() {
			return keys;
		}

		@Override
		public String getLocalValue(final String key) throws LocalizationException, IllegalArgumentException {
			return getLocalValue(key, currentLocale().getLocale());
		}

		@Override
		public String getLocalValue(final String key, final Locale locale) throws LocalizationException, IllegalArgumentException {
			if (Utils.checkEmptyOrNullString(key)) {
				throw new IllegalArgumentException("Key string can't be null or empty");
			}
			else if (locale == null) {
				throw new NullPointerException("Locale can't be null");
			}
			else {
				final SupportedLanguages	lang = SupportedLanguages.of(locale);
				
				if (!pairs.containsKey(lang)) {
					throw new LocalizationException("Language ["+lang+"] is not supported with the localizer");
				}
				else if (!pairs.get(lang).containsKey(key)) {
					throw new LocalizationException("Localization key ["+key+"] not found anywhere");
				}
				else {
					return pairs.get(lang).get(key);
				}
			}
		}

		@Override
		protected boolean isLocaleSupported(final String key, final Locale locale) throws LocalizationException, IllegalArgumentException {
			final SupportedLanguages	lang = SupportedLanguages.of(locale);
			
			if (!pairs.containsKey(lang)) {
				return false;
			}
			else if (!pairs.get(lang).containsKey(key)) {
				return false;
			}
			else {
				return true;
			}
		}

		@Override
		protected void loadResource(final Locale newLocale) throws LocalizationException, NullPointerException {
		}

		@Override
		protected String getHelp(final String helpId, final Locale locale, final String encoding) throws LocalizationException, IllegalArgumentException {
			final SupportedLanguages	lang = SupportedLanguages.of(locale);
			
			return helps.get(lang).get(helpId);
		}
	}
}
