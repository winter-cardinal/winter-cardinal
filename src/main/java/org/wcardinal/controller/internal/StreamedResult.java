package org.wcardinal.controller.internal;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize( using=StreamedResultSerializer.class )
public class StreamedResult {
	private final String value;

	public StreamedResult( final String value ) {
		this.value = value;
	}

	public void serialize( final JsonGenerator gen, final SerializerProvider serializers ) throws IOException, JsonProcessingException {
		gen.writeRawValue( this.value );
	}
}
