package chav1961.installer.internal;

import java.util.HashMap;
import java.util.Map;

public class ContentKeeper<T> {
	public final T						content;
	public final Map<String, Object>	attributes = new HashMap<>();

	public ContentKeeper(final T content, final Map<String, ?> attributes) {
		if (content == null) {
			throw new NullPointerException("Content can't be null");
		}
		else if (attributes == null) {
			throw new NullPointerException("Attributes can't be null");
		}
		else {
			this.content = content;
			this.attributes.putAll(attributes);
		}
	}
}
