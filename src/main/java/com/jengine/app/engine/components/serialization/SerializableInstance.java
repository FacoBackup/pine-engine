package com.jengine.app.engine.components.serialization;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jengine.app.Loggable;


public interface SerializableInstance extends Loggable {

    JsonElement serializeData();

    JsonObject serialize() ;

     void parse(JsonObject json);

}
