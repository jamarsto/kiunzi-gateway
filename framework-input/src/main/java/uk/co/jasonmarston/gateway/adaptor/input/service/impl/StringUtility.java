package uk.co.jasonmarston.gateway.adaptor.input.service.impl;

final class StringUtility {
	public static final String unquoteString(
		final String value
	) {
		if(!value.startsWith("\"")) {
			return value;
		}
		return value
			.substring(1, value.length() - 1);
	}
}
	