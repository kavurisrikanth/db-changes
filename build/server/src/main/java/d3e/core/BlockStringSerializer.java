package d3e.core;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class BlockStringSerializer extends StdSerializer<BlockString> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BlockStringSerializer() {
		this(null);
	}

	public BlockStringSerializer(Class<BlockString> t) {
		super(t);
	}

	@Override
	public void serialize(BlockString value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		jgen.writeString(value.getContent());
	}
}