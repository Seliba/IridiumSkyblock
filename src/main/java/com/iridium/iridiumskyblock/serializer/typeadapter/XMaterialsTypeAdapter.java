package com.iridium.iridiumskyblock.serializer.typeadapter;

import com.cryptomorin.xseries.XMaterial;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public class XMaterialsTypeAdapter implements JsonSerializer<XMaterial>, JsonDeserializer<XMaterial> {

    @Override
    public JsonElement serialize(XMaterial material, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(material.name());
    }


    @Override
    public XMaterial deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        return XMaterial.valueOf(jsonElement.getAsString().replace("LEGACY_", ""));
    }
}