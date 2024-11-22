package chav1961.installer.internal;

import java.io.IOException;

import chav1961.purelib.basic.MimeType;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.i18n.interfaces.Localizer;

public class InternalUtils {
	public static String loadHtml(final Localizer localizer, final String key) {
		try{
			final String	html = Utils.fromResource(localizer.getContent(key, MimeType.MIME_CREOLE_TEXT, MimeType.MIME_HTML_TEXT));

			return html.substring(html.indexOf("<html>"));
		} catch (LocalizationException | IOException e) {
			return e.getLocalizedMessage();
		} 
	}
}
