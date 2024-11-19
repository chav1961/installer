package chav1961.installer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Wizard extends JDialog {
	private static final long serialVersionUID = -7868719740809674349L;

	private Component				oldComponent = null;
	private final JButton			prevButton = new JButton("<<");
	private final JButton			nextButton = new JButton(">>");
	private final JButton			cancelButton = new JButton("Cancel");
	private final JLabel			stepCaption = new JLabel("?????");
	private final JComboBox<String>	lang = new JComboBox<String>();
	private final JList<String>		steps = new JList<>(new String[] {"sdds"});
	
	public Wizard() {
		super(null, ModalityType.APPLICATION_MODAL);
		final JPanel	header = new JPanel(new BorderLayout());
		final JPanel	left = new JPanel();
		final JPanel	separator = new JPanel(new BorderLayout());
		final JPanel	bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		stepCaption.setHorizontalAlignment(JLabel.CENTER);
		header.add(stepCaption, BorderLayout.CENTER); 
		header.add(lang, BorderLayout.EAST); 

		getContentPane().add(header, BorderLayout.NORTH);
		
		steps.setPreferredSize(new Dimension(250, 250));
		left.add(steps);
		getContentPane().add(steps, BorderLayout.WEST);
		
		bottom.add(prevButton);
		bottom.add(nextButton);
		bottom.add(cancelButton);
		
		separator.add(new JSeparator(), BorderLayout.NORTH);
		separator.add(bottom, BorderLayout.CENTER);
		
		getContentPane().add(separator, BorderLayout.SOUTH);
		setSize(1024, 768);
		setLocationRelativeTo(null);
	}
	
	public void setContent(final JComponent content) {
		if (oldComponent != null) {
			remove(oldComponent);
		}
		add(content, BorderLayout.CENTER);
		oldComponent = content;
	}
}
