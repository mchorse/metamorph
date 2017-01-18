package mchorse.metamorph.api.json;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import mchorse.metamorph.api.models.Model;
import net.minecraft.util.ResourceLocation;

/**
 * Model JSON adapter
 * 
 * This adapter is responsible for only deserializing a {@link Model} instance.
 */
public class ModelAdapter implements JsonDeserializer<Model>
{
    private Gson gson = new GsonBuilder().create();

    /**
     * Deserializes {@link Model}
     * 
     * This method is responsible mainly from translating "default" field into 
     * {@link ResourceLocation}.
     */
    @Override
    public Model deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        Model model = gson.fromJson(json, Model.class);
        JsonObject object = json.getAsJsonObject();

        if (object.has("default"))
        {
            String type = object.get("default").getAsString();

            model.defaultTexture = new ResourceLocation(type);
        }

        return model;
    }
}