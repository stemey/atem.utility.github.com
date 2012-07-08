package org.atemsource.atem.impl.json;

import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.impl.json.type.JsonSchemaRef;
import org.atemsource.atem.impl.json.type.JsonSchemaUri;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.UniConverter;

public class JsonSchemaUriConverter {

	public String converterBA(JsonSchemaUri ref) {
		return ((JsonSchemaUri)ref).getUri().toString();
	}

}
