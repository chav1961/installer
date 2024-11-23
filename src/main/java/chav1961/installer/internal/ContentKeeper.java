package chav1961.installer.internal;

import java.util.HashMap;
import java.util.Map;

import chav1961.purelib.basic.Utils;

public class ContentKeeper<T> {
	public final String					name;
	public final T						content;
	public final Map<String, Object>	attributes = new HashMap<>();

	public ContentKeeper(final String name, final T content, final Map<String, ?> attributes) {
		if (Utils.checkEmptyOrNullString(name)) {
			throw new IllegalArgumentException("Name can't be null or empty");
		}
		else if (content == null) {
			throw new NullPointerException("Content can't be null");
		}
		else if (attributes == null) {
			throw new NullPointerException("Attributes can't be null");
		}
		else {
			this.name = name;
			this.content = content;
			this.attributes.putAll(attributes);
		}
	}
}
