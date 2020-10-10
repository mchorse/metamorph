package mchorse.metamorph.capabilities.render;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.minecraft.nbt.JsonToNBT;

import java.lang.reflect.Type;

/**
 * Entity selector JSON adapter 
 */
public class EntitySelectorAdapter implements JsonDeserializer<EntitySelector>, JsonSerializer<EntitySelector>
{
    @Override
    public EntitySelector deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        if (!json.isJsonObject())
        {
            return null;
        }

        EntitySelector selector = new EntitySelector();
        JsonObject object = json.getAsJsonObject();

        if (object.has("name"))
        {
            selector.name = object.get("name").getAsString();
        }

        if (object.has("type"))
        {
            selector.type = object.get("type").getAsString();
        }

        if (object.has("enabled"))
        {
            selector.enabled = object.get("enabled").getAsBoolean();
        }

        if (object.has("match"))
        {
            try
            {
                selector.match = JsonToNBT.getTagFromJson(object.get("match").getAsString());
            }
            catch (Exception e)
            {}
        }

        if (object.has("morph"))
        {
            try
            {
                selector.morph = JsonToNBT.getTagFromJson(object.get("morph").getAsString());
            }
            catch (Exception e)
            {}
        }

        return selector;
    }

    @Override
    public JsonElement serialize(EntitySelector src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject object = new JsonObject();

        object.addProperty("name", src.name);
        object.addProperty("type", src.type);
        object.addProperty("enabled", src.enabled);

        if (src.match != null && !src.match.hasNoTags())
        {
            object.addProperty("match", src.match.toString());
        }

        if (src.morph != null)
        {
            object.addProperty("morph", src.morph.toString());
        }

        return object;
    }
}