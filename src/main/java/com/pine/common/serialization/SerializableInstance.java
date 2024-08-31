package com.pine.common.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.app.Loggable;


public interface SerializableInstance extends Loggable {

    JsonElement serializeData();

    JsonObject serialize() ;

     void parse(JsonObject json);

}
