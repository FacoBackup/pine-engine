package com.pine.common.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.Loggable;


public interface SerializableInstance extends Loggable {

    JsonElement serializeData();

    JsonObject serialize() ;

     void parse(JsonObject json);

}
