package rest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import d3e.core.DFile;
import graphql.language.Argument;
import graphql.language.BooleanValue;
import graphql.language.EnumValue;
import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.NullValue;
import graphql.language.ObjectValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.language.VariableReference;
import store.EntityHelperService;

public class ArgumentInputContext extends GraphQLInputContext {

	private List<Argument> arguments;
	JSONObject variables;

	public ArgumentInputContext(List<Argument> arguments, EntityHelperService helperService,
			Map<Long, Object> inputObjectCache, Map<String, DFile> files, JSONObject variables) {
		super(helperService, inputObjectCache, files);
		this.arguments = arguments;
		this.variables = variables;
	}

	Object readAny(String field) {
		for (Argument a : arguments) {
			if (a.getName().equals(field)) {
				Value value = a.getValue();
				if (value instanceof VariableReference) {
					try {
						return variables.get(((VariableReference) value).getName());
					} catch (JSONException e) {
						throw new RuntimeException(e);
					}
				} else {
					return value;
				}
			}
		}
		return null;
	}

	public boolean has(String field) {
		for (Argument a : arguments) {
			if (a.getName().equals(field)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <T> T readRef(String field, String type) {
		Object obj = readAny(field);
		if (obj == null) {
			return null;
		}
		if (obj instanceof ObjectValue) {
			GraphQLInputContext ctx = createContext(field);
			return ctx.readObject(type, false);
		} else {
			return (T) readRef(helperService.get(type), readLong(field));
		}
	}

	@Override
	public <T> T readUnion(String field, String type) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public <T> List<T> readUnionColl(String field, String type) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public <T> T readChild(String field, String type) {
		GraphQLInputContext ctx = createContext(field);
		if (ctx == null) {
			return null;
		}
		return ctx.readObject(type, true);
	}

	protected GraphQLInputContext createContext(String field) {
		Object any = readAny(field);
		if (any == null || any instanceof NullValue) {
			return null;
		}
		if (any instanceof ObjectValue) {
			return new ObjectValueInputContext((ObjectValue) any, helperService, inputObjectCache, files, variables);
		} else if (any instanceof JSONObject) {
			return new JSONInputContext((JSONObject) any, helperService, inputObjectCache, files, variables);
		} else {
			throw new RuntimeException("Unknown value");
		}
	}

	public long readLong(String field) {
		Object any = readAny(field);
		if (any instanceof IntValue) {
			return ((IntValue) any).getValue().longValue();
		}
		if (any instanceof Integer) {
			return ((Integer) any).longValue();
		}
		if (any == null) {
			return 0;
		}
		return (long) any;
	}

	public String readString(String field) {
		Object any = readAny(field);
		if(JSONObject.NULL.equals(any)) {
			return null;
		}
		if (any instanceof StringValue) {
			return ((StringValue) any).getValue();
		} else if(any instanceof EnumValue) {
			return ((EnumValue) any).getName();
		} 
		return (String) any;
	}

	public long readInteger(String field) {
		Object any = readAny(field);
		if (any instanceof IntValue) {
			return ((IntValue) any).getValue().longValue();
		}
		if (any instanceof Integer) {
			return ((Integer) any).longValue();
		}
		return 0;
	}

	public boolean readBoolean(String field) {
		Object any = readAny(field);
		if (any == null) {
			return false;
		}
		if (any instanceof BooleanValue) {
			return ((BooleanValue) any).isValue();
		}
		return (boolean) any;
	}

	public double readDouble(String field) {
		Object any = readAny(field);
		if (any == null) {
			return 0.0;
		}
		if (any instanceof FloatValue) {
			return ((FloatValue) any).getValue().doubleValue();
		}
		if (any instanceof Float) {
			return ((Float) any).doubleValue();
		}
		return (double) any;
	}

	public Duration readDuration(String field) {
		throw new RuntimeException("Not Implemented");
	}

	public LocalDateTime readDateTime(String field) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public LocalDate readDate(String field) {
		throw new RuntimeException("Unsupported");
	}

	public LocalTime readTime(String field) {
		throw new RuntimeException("Not Implemented");
	}

	@Override
	public List<Long> readLongColl(String field) {
		Object any = readAny(field);
		if (any instanceof JSONArray) {
			try {
				JSONArray array = (JSONArray) any;
				int length = array.length();
				List<Long> res = new ArrayList<>();
				for (int i = 0; i < length; i++) {
					res.add(array.getLong(i));
				}
				return res;
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("Unsupported");
	}
	
	@Override
	public List<Long> readIntegerColl(String field) {
		Object any = readAny(field);
		if (any instanceof JSONArray) {
			try {
				JSONArray array = (JSONArray) any;
				int length = array.length();
				List<Long> res = new ArrayList<>();
				for (int i = 0; i < length; i++) {
					res.add(array.getLong(i));
				}
				return res;
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("Unsupported");
	}

	@Override
	public List<String> readStringColl(String field) {
		Object any = readAny(field);
		if (any instanceof JSONArray) {
			try {
				JSONArray array = (JSONArray) any;
				int length = array.length();
				List<String> res = new ArrayList<>();
				for (int i = 0; i < length; i++) {
					res.add(array.getString(i));
				}
				return res;
			} catch (JSONException e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("Unsupported");
	}

	@Override
	public <T> List<T> readChildColl(String field, String type) {
		throw new RuntimeException("Unsupported");
	}

	@Override
	public <T> List<T> readRefColl(String field, String type) {
		throw new RuntimeException("Unsupported");
	}

	@Override
	public <T extends Enum<?>> List<T> readEnumColl(String field, Class<T> cls) {
		throw new RuntimeException("Unsupported");
	}

	@Override
	public List<DFile> readDFileColl(String field) {
		throw new RuntimeException("Unsupported");
	}
}
