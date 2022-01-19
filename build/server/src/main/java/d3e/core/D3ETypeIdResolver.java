package d3e.core;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

import classes.ClassUtils;

public class D3ETypeIdResolver extends TypeIdResolverBase {

	@Override
	public JavaType typeFromId(DatabindContext context, String id) throws IOException {
		Class<?> cls = null;
		try {
			cls = Class.forName("models." + id);
		} catch (Exception e1) {
			try {
				cls = Class.forName("classes." + id);
			} catch (Exception e2) {
				try {
					cls = Class.forName("lists." + id);
				} catch (Exception e3) {
					e3.printStackTrace();
				}
			}
		}
		return context.getTypeFactory().constructType(cls);
	}

	@Override
	public String idFromValue(Object value) {
		return ClassUtils.getClass(value).getSimpleName();
	}

	@Override
	public String idFromValueAndType(Object value, Class<?> suggestedType) {
		return ClassUtils.getClass(value).getSimpleName();
	}

	@Override
	public Id getMechanism() {
		return Id.NAME;
	}
}
