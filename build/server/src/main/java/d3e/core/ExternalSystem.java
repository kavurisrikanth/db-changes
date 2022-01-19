package d3e.core;

import models.CreatableObject;

public interface ExternalSystem {
	void save(CreatableObject obj, boolean internal);
	void delete(CreatableObject obj, boolean internal);
}
