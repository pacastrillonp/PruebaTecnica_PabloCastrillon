package com.pruebatecnica.pablocastrillon.pruebatecnica_pablocastrillon.core.utils;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;

import java.lang.reflect.Type;

public class SerializationTool {

    public static String serializeToJson(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static <T> T deserializeFromJson(String jsonString, Class<T> classOfT) {
        Gson gson = new Gson();
        Object object = gson.fromJson(jsonString, (Type) classOfT);
        return Primitives.wrap(classOfT).cast(object);
    }

}