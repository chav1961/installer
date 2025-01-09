package chav1961.installer;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.ui.swing.useful.JLocalizerContentEditor;
import chav1961.purelib.ui.swing.useful.JLocalizerContentEditor.ContentType;

public class DevelopmentScreen extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6391352835569485529L;

	private final JTabbedPane	pane = new JTabbedPane();
	private final JLocalizerContentEditor	editor;
	
	public DevelopmentScreen(final Localizer localizer, final LoggerFacade logger) throws ContentException {
		super(new BorderLayout(5, 5));
		this.editor = new JLocalizerContentEditor(localizer, logger, (l,t)->{store(l, t);}, true, false);
		add(editor, BorderLayout.CENTER);
	}

	private void store(Localizer l, ContentType t) {
		// TODO Auto-generated method stub
	}
}
