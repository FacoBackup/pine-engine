package com.pine.common;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pine.common.serialization.SerializableRepository;
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

    public void parseAll(String serialized){
        JsonArray repos = new Gson().fromJson(serialized, JsonArray.class);
        for(JsonElement repoDump : repos){
            JsonObject dump = repoDump.getAsJsonObject();
            for(var repo : repositories){
                if(repo.isCompatible(dump)){
                    repo.parse(dump);
                }
            }
        }
    }
}
