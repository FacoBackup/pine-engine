package com.pine.service.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.Loggable;


public interface SerializableInstance extends Loggable {

    JsonElement serializeData();

    JsonObject serialize() ;

     void parse(JsonObject json);

}
