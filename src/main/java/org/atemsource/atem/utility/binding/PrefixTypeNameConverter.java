package org.atemsource.atem.utility.binding;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.api.TypeNameConverter;

public class PrefixTypeNameConverter implements TypeNameConverter {
	public PrefixTypeNameConverter() {
		super();
	}

	private String prefix;

	public PrefixTypeNameConverter(String prefix) {
		super();
		this.prefix = prefix;
	}

	@Override
	public String convert(EntityType<?> type) {
		return prefix + ":" + type.getCode();
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
