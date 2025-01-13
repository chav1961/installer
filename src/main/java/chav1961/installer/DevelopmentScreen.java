package chav1961.installer;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EtchedBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import chav1961.installer.internal.ProjectInfo;
import chav1961.purelib.basic.CharUtils;
import chav1961.purelib.basic.PureLibSettings;
import chav1961.purelib.basic.exceptions.ContentException;
import chav1961.purelib.basic.interfaces.LoggerFacade;
import chav1961.purelib.basic.interfaces.LoggerFacade.Severity;
import chav1961.purelib.i18n.interfaces.Localizer;
import chav1961.purelib.model.ContentModelFactory;
import chav1961.purelib.model.interfaces.ContentMetadataInterface;
import chav1961.purelib.ui.HighlightItem;
import chav1961.purelib.ui.swing.AutoBuiltForm;
import chav1961.purelib.ui.swing.useful.JLocalizerContentEditor;
import chav1961.purelib.ui.swing.useful.JLocalizerContentEditor.ContentType;
import chav1961.purelib.ui.swing.useful.JStateString;
import chav1961.purelib.ui.swing.useful.JTextPaneHighlighter;

class DevelopmentScreen extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6391352835569485529L;

	private final JTabbedPane	pane = new JTabbedPane();
	private final JLocalizerContentEditor	editor;
	private final ProjectInfo	info; 
	private final JSHighlighter	script = new JSHighlighter();
	private final JStateString	state;
	
	public DevelopmentScreen(final Localizer localizer, final LoggerFacade logger) throws ContentException {
		super(new BorderLayout(5, 5));
		this.editor = new JLocalizerContentEditor(localizer, logger, (l,t)->{store(l, t);}, true, false);
		this.state = new JStateString(localizer);
		this.state.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
		this.info = new ProjectInfo(logger);

		final ContentMetadataInterface			mdi = ContentModelFactory.forAnnotatedClass(ProjectInfo.class);
		final AutoBuiltForm<ProjectInfo, Long>	form = new AutoBuiltForm<>(mdi, localizer, logger, PureLibSettings.INTERNAL_LOADER, info, info);

		form.setMaximumSize(new Dimension(250,300));
		pane.addTab("About", form);
		pane.addTab("I18N", editor);
		pane.addTab("Script", new JScrollPane(script));
		
		add(pane, BorderLayout.CENTER);
		add(state, BorderLayout.SOUTH);

		fillLocalizedStrings();
		state.message(Severity.info, "ready");
	}

	private void fillLocalizedStrings() {
		// TODO Auto-generated method stub
		
	}



	private void store(Localizer l, ContentType t) {
		// TODO Auto-generated method stub
	}
	
	private static enum LexemaType {
		Plain,
		Error,
		Number,
		String,
		Name,
		Keyword,
		EOF
	}

	private static class JSHighlighter extends JTextPaneHighlighter<LexemaType> {
		private static final long 			serialVersionUID = -8747874247856135024L;
		private static final Set<String> 	KEYWORDS = new HashSet<>(Arrays.asList("if", "else", "while", "do", "for", "in", "each", "switch", "case", "break", "continue", "return", "function", "delete", "default", "try", "catch", "throw", "var"));

		{
			SimpleAttributeSet	sas = new SimpleAttributeSet();
			
			StyleConstants.setForeground(sas, Color.BLACK);
			StyleConstants.setBold(sas, false);
			StyleConstants.setItalic(sas, false);
			characterStyles.put(LexemaType.Plain, sas);
			
			sas = new SimpleAttributeSet();

			StyleConstants.setForeground(sas, Color.RED);
			StyleConstants.setBold(sas, true);
			StyleConstants.setItalic(sas, false);
			characterStyles.put(LexemaType.Error, sas);

			sas = new SimpleAttributeSet();

			StyleConstants.setForeground(sas, Color.BLUE);
			StyleConstants.setBold(sas, true);
			StyleConstants.setItalic(sas, false);
			characterStyles.put(LexemaType.Number, sas);

			sas = new SimpleAttributeSet();

			StyleConstants.setForeground(sas, Color.BLUE);
			StyleConstants.setBold(sas, false);
			StyleConstants.setItalic(sas, false);
			characterStyles.put(LexemaType.String, sas);

			sas = new SimpleAttributeSet();

			StyleConstants.setForeground(sas, Color.GRAY);
			StyleConstants.setBold(sas, false);
			StyleConstants.setItalic(sas, true);
			characterStyles.put(LexemaType.Name, sas);

			sas = new SimpleAttributeSet();

			StyleConstants.setForeground(sas, Color.GREEN);
			StyleConstants.setBold(sas, true);
			StyleConstants.setItalic(sas, false);
			characterStyles.put(LexemaType.Keyword, sas);
		}
		
		
		public JSHighlighter() {
		}
		
		@Override
		protected HighlightItem<LexemaType>[] parseString(final String program) {
			final List<HighlightItem<LexemaType>>	result = new ArrayList<>();
			final char[]		content = CharUtils.terminateAndConvert2CharArray(program, '\0');
			final StringBuilder	sb = new StringBuilder();
			final long[]		forValue = new long[2];
			final int[]			forName = new int[2];
			int					from = 0, displ = 0, start, delta;

loop:		for(;;) {
				while (content[from] <= ' ' && content[from] != '\0') {
					from++;
					if (content[from] != '\r') {
						displ++;
					}
				}
				delta = from;
				start = displ;
				
				try {
					switch (content[from]) {
						case '\0' :
							result.add(new HighlightItem<LexemaType>(start, 0, LexemaType.Plain));
							break loop;
						case '0' : case '1' : case '2' : case '3' : case '4' : case '5' : case '6' : case '7' : case '8' : case '9' :
							from = CharUtils.parseNumber(content, from, forValue, CharUtils.PREF_ANY, true);
							result.add(new HighlightItem<LexemaType>(start, from - delta, LexemaType.Number));
							break;
						case '\"' :
							from = CharUtils.parseStringExtended(content, from + 1, '\"', sb);
							result.add(new HighlightItem<LexemaType>(start, from - delta, LexemaType.String));
							break;
						case '\'' :
							from = CharUtils.parseStringExtended(content, from + 1, '\'', sb);
							result.add(new HighlightItem<LexemaType>(start, from - delta, LexemaType.String));
							break;
						default :
							if (Character.isJavaIdentifierStart(content[from])) {
								from = CharUtils.parseName(content, from, forName);
								final String	name = new String(content, forName[0], forName[1]-forName[0] + 1);
								
								if (KEYWORDS.contains(name)) {
									result.add(new HighlightItem<LexemaType>(start, from - delta, LexemaType.Keyword));
								}
								else {
									result.add(new HighlightItem<LexemaType>(start, from - delta, LexemaType.Name));
								}
							}
							else {
								result.add(new HighlightItem<LexemaType>(start, 1, LexemaType.Plain));
								from++;
							}
					}
				} catch (Exception e) {
					result.add(new HighlightItem<LexemaType>(start, 1, LexemaType.Error));
					from++;
				}
				displ += (from - delta);
			}
			
			return result.toArray(new HighlightItem[result.size()]);
		}		
	}
}
