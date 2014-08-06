package com.tyler.gson.optional;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

public class GuavaOptionalTypeFactory extends OptionalTypeFactory {    
	
	@Override
	public <T> boolean isOptionalType(TypeToken<T> typeToken) {
		return typeToken.getRawType() != Optional.class && ( typeToken.getType() instanceof ParameterizedType );			
	}
	
	@SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
       Type type = typeToken.getType();
       if (typeToken.getRawType() != Optional.class || !(type instanceof ParameterizedType)) {
          return null;
       }

       Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
       TypeAdapter<?> elementAdapter = gson.getAdapter(TypeToken.get(elementType));
       return (TypeAdapter<T>) newOptionalAdapter(elementAdapter);
    }

	@SuppressWarnings("unchecked")
	protected <E,T> OptionalTypeAdapter<E,T> newOptionalAdapter( final TypeAdapter<T> elementAdapter ) {
		return (OptionalTypeAdapter<E, T>) new GuavaOptionalAdapter<T>(elementAdapter);		
	}
    
    private static class GuavaOptionalAdapter<T> extends OptionalTypeAdapter<Optional<T>,T> {
		
		protected GuavaOptionalAdapter( final TypeAdapter<T> elementAdapter ) {
			super(elementAdapter);			
		}

		@Override
		protected boolean isPresent( final Optional<T> e ) {
			return e.isPresent();
		}
		
		@Override
		protected T getValue( final Optional<T> e ) {
			return e.get();
		}

		@Override
		protected Optional<T> presentOptional( final T t ) {
			return Optional.of( t );
		}

		@Override
		protected Optional<T> emptyOptional() {
			return Optional.absent();
		}
	}
}