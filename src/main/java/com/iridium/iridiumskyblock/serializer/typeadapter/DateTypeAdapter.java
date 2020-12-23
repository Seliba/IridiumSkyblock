package com.iridium.iridiumskyblock.serializer.typeadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.iridium.iridiumskyblock.Utils;
import java.lang.reflect.Type;
import java.util.Date;

public class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    @Override
    public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(Utils.getCurrentTimeStamp(date, "MMM d, yyyy, hh:mm:ss aaa"));
    }


    @Override
    public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        return Utils.getLocalDateTime(jsonElement.getAsString(), "MMM d, yyyy, hh:mm:ss aaa");
    }
}