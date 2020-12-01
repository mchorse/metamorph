package mchorse.metamorph.api.json;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphSettings;
import mchorse.metamorph.api.abilities.IAbility;

/**
 * Morph settings adapter
 * 
 * This class is responsible for deserializing {@link MorphSettings}.
 */
public class MorphSettingsAdapter implements JsonDeserializer<MorphSettings>
{
    @Override
    public MorphSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
    {
        JsonObject object = json.getAsJsonObject();

        MorphSettings morph = new MorphSettings();
        MorphManager manager = MorphManager.INSTANCE;

        if (object.has("health") && object.get("health").isJsonPrimitive())
        {
            morph.health = object.get("health").getAsInt();
        }

        if (object.has("speed") && object.get("speed").isJsonPrimitive())
        {
            morph.speed = object.get("speed").getAsFloat();
        }

        if (object.has("hostile") && object.get("hostile").isJsonPrimitive())
        {
            morph.hostile = object.get("hostile").getAsBoolean();
        }

        if (object.has("hands") && object.get("hands").isJsonPrimitive())
        {
            morph.hands = object.get("hands").getAsBoolean();
        }

        if (object.has("abilities") && object.get("abilities").isJsonArray())
        {
            morph.abilities.clear();

            for (JsonElement ability : object.get("abilities").getAsJsonArray())
            {
                if (!ability.isJsonPrimitive())
                {
                    continue;
                }

                IAbility iability = manager.abilities.get(ability.getAsString());

                if (iability != null)
                {
                    morph.abilities.add(iability);
                }
            }
        }

        if (object.has("action") && object.get("action").isJsonPrimitive())
        {
            morph.action = manager.actions.get(object.get("action").getAsString());
        }

        if (object.has("attack") && object.get("attack").isJsonPrimitive())
        {
            morph.attack = manager.attacks.get(object.get("attack").getAsString());
        }

        if (object.has("updates") && object.get("updates").isJsonPrimitive())
        {
            morph.updates = object.get("updates").getAsBoolean();
        }

        return morph;
    }
}