package rest.ws;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class TemplateManager {

	private Map<String, Template> templates = new HashMap<>();

	public boolean hasTemplate(String templateId) {
		return templates.containsKey(templateId);
	}

	public void addTemplate(Template template) {
		templates.put(template.getHash(), template);
	}

	public Template getTemplate(String templateId) {
		return templates.get(templateId);
	}
}
