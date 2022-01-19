package gqltosql.schema;

import java.util.List;

public interface IModelSchema {

	public List<DModel<?>> getAllTypes();

	public DModel<?> getType(String type);

	public DModel<?> getType(int index);

	public List<DClazz> getAllChannels();

	public DClazz getChannel(String name);

	public List<DClazz> getAllRPCs();

	public DClazz getRPC(String name);

}
