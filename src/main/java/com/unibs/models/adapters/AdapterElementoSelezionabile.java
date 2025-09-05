package com.unibs.models.adapters;

import com.unibs.utils.ElementoSelezionabile;

import java.util.function.Function;

public class AdapterElementoSelezionabile<T> implements ElementoSelezionabile {
    private final T value;
    private final Function<T, String> placeholderFunction;

    public AdapterElementoSelezionabile(T value, Function<T, String> placeholderFunction) {
        this.value = value;
        this.placeholderFunction = placeholderFunction;
    }

    @Override
    public String getPlaceHolder() {
        return placeholderFunction.apply(value);
    }

    public T getValue() {
        return value;
    }
}