package chav1961.installer.internal;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent.EventType;

import chav1961.purelib.basic.MimeType;
import chav1961.purelib.basic.Utils;
import chav1961.purelib.basic.exceptions.LocalizationException;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.SwingUtils;

public class InternalUtils {
	public static String loadHtml(final Localizer localizer, final String key) {
		try{
			final String	html = Utils.fromResource(localizer.getContent(key, MimeType.MIME_CREOLE_TEXT, MimeType.MIME_HTML_TEXT));

			return html.substring(html.indexOf("<html>"));
		} catch (LocalizationException | IOException e) {
			return e.getLocalizedMessage();
		} 
	}
	
	public static JEditorPane createEditorPane() {
		final JEditorPane	pane = new JEditorPane("text/html","");
		
		pane.setEditable(false);
		pane.setOpaque(false);
		pane.addHyperlinkListener((l)->{
			if (l.getEventType() == EventType.ACTIVATED) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(l.getURL().toURI());
					} catch (IOException | URISyntaxException e) {
						SwingUtils.getNearestLogger(pane).message(Severity.warning, e, e.getLocalizedMessage());
					}
				}
			}
		});
		return pane;
	}
}
