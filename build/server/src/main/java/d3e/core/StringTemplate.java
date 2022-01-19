package d3e.core;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;

public class StringTemplate {
	private VelocityContext context = new VelocityContext();
	private Template template;

	public static StringTemplate fromString(String text) {
		// Added to fix error in InviteUserRequestUtil (which is not used anywhere).
		return fromString(text, null);
	}

	public static StringTemplate fromString(String text, String name) {
		// The name parameter is not used. Consider using it or removing it.
		RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
		StringReader reader = new StringReader(text);
		Template template = new Template();
		template.setRuntimeServices(runtimeServices);

		try {
			template.setData(runtimeServices.parse(reader, name));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		template.initDocument();
		return new StringTemplate(template);
	}

	private StringTemplate(Template template) {
		this.template = template;
	}

	public void put(String key, Object value) {
		this.context.put(key, value);
	}

	public String merge() {
		StringWriter sw = new StringWriter();
		this.template.merge(context, sw);
		return sw.toString();
	}
}
