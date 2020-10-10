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

        if (object.has("health"))
        {
            morph.health = object.get("health").getAsInt();
        }

        if (object.has("speed"))
        {
            morph.speed = object.get("speed").getAsFloat();
        }

        if (object.has("hostile"))
        {
            morph.hostile = object.get("hostile").getAsBoolean();
        }

        if (object.has("hands"))
        {
            morph.hands = object.get("hands").getAsBoolean();
        }

        if (object.has("abilities"))
        {
            morph.abilities.clear();

            for (JsonElement ability : object.get("abilities").getAsJsonArray())
            {
                IAbility iability = manager.abilities.get(ability.getAsString());

                if (iability != null)
                {
                    morph.abilities.add(iability);
                }
            }
        }

        if (object.has("action"))
        {
            morph.action = manager.actions.get(object.get("action").getAsString());
        }

        if (object.has("attack"))
        {
            morph.attack = manager.attacks.get(object.get("attack").getAsString());
        }

        if (object.has("updates"))
        {
            morph.updates = object.get("updates").getAsBoolean();
        }

        return morph;
    }
}