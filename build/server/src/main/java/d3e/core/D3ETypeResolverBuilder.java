package d3e.core;

import java.io.IOException;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.AsPropertyTypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;

public class D3ETypeResolverBuilder extends StdTypeResolverBuilder
		implements TypeResolverBuilder<StdTypeResolverBuilder> {

	public D3ETypeResolverBuilder() {
	}

	@Override
	public TypeDeserializer buildTypeDeserializer(DeserializationConfig config, JavaType baseType,
			Collection<NamedType> subtypes) {
		if (baseType.isPrimitive() || baseType instanceof MapType || baseType instanceof CollectionType) {
			return null;
		}
		if (!baseType.isAbstract()) {
			return null;
		}
		TypeIdResolver idRes = idResolver(config, baseType, subtypes, false, true);
		JavaType defaultImpl = defineDefaultImpl(config, baseType);
		return new AsPropertyTypeDeserializer(baseType, idRes, "__typename", _typeIdVisible, defaultImpl,
				As.EXISTING_PROPERTY) {
			private static final long serialVersionUID = 1L;

			@Override
			protected JavaType _handleMissingTypeId(DeserializationContext ctxt, String extraDesc) throws IOException {
				return _baseType;
			}
		};
	}

}
