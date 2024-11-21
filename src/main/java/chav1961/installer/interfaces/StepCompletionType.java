package chav1961.installer.interfaces;

import javax.swing.ImageIcon;
import javax.swing.Icon;

public enum StepCompletionType {
	WAITING("step.completion.type.waiting", new ImageIcon(StepCompletionType.class.getResource("waiting.png"))),
	PROCESSING("step.completion.type.processing", new ImageIcon(StepCompletionType.class.getResource("processing.png"))),
	SUCCESSFUL("step.completion.type.successful", new ImageIcon(StepCompletionType.class.getResource("successful.png"))),
	FAILED("step.completion.type.failed", new ImageIcon(StepCompletionType.class.getResource("failed.png"))),
	SKIPPED("step.completion.type.skipped", new ImageIcon(StepCompletionType.class.getResource("skipped.png"))),
	NOT_REQUIRED("step.completion.type.not.required", new ImageIcon(StepCompletionType.class.getResource("notRequired.png")));
	
	private final String	description;
	private final Icon		icon;
	
	private StepCompletionType(final String description, final Icon icon) {
		this.description = description;
		this.icon = icon;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Icon getIcon() {
		return icon;
	}
}
