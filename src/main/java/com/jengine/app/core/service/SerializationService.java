package com.jengine.app.core.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jengine.app.core.service.serialization.SerializableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SerializationService {
    @Autowired
    private List<SerializableRepository> repositories;

    public String serializeAll() {
        JsonArray serializedRepositories = new JsonArray();
        repositories.forEach(r -> serializedRepositories.add(r.serialize()));
        return serializedRepositories.toString();
    }

    public void parseAll(JsonArray serializedRepositories){
        for(JsonElement repoDump : serializedRepositories){
            JsonObject dump = repoDump.getAsJsonObject();
            for(var repo : repositories){
                if(repo.isCompatible(dump)){
                    repo.parse(dump);
                }
            }
        }
    }
}
