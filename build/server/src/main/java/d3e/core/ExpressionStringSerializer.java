package d3e.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class ExpressionStringSerializer extends StdSerializer<ExpressionString> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExpressionStringSerializer() {
		this(null);
	}

	public ExpressionStringSerializer(Class<ExpressionString> t) {
		super(t);
	}

	@Override
	public void serialize(ExpressionString value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jgen.writeString(value.getContent());
	}
}