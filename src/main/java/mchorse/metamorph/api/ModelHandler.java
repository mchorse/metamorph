package mchorse.metamorph.api;

import java.util.HashMap;
import java.util.Map;

/**
 * This class responsible for storing domain custom models and sending models to
 * players who are logged in.
 */
public class ModelHandler
{
    /**
     * Cached models, they're loaded from stuffs
     */
    public Map<String, Model> models = new HashMap<String, Model>();

    /**
     * Load default provided models into model map
     */
    public void loadModels()
    {
        try
        {
            String path = "assets/metamorph/models/entity/";
            ClassLoader loader = this.getClass().getClassLoader();

            this.models.put("Chicken", Model.parse(loader.getResourceAsStream(path + "chicken.json")));
            this.models.put("Cow", Model.parse(loader.getResourceAsStream(path + "cow.json")));
            this.models.put("Creeper", Model.parse(loader.getResourceAsStream(path + "creeper.json")));
            this.models.put("MushroomCow", Model.parse(loader.getResourceAsStream(path + "mooshroom.json")));
            this.models.put("Ozelot", Model.parse(loader.getResourceAsStream(path + "ocelot.json")));
            this.models.put("Pig", Model.parse(loader.getResourceAsStream(path + "pig.json")));
            this.models.put("Rabbit", Model.parse(loader.getResourceAsStream(path + "rabbit.json")));
            this.models.put("Sheep", Model.parse(loader.getResourceAsStream(path + "sheep.json")));
            this.models.put("Squid", Model.parse(loader.getResourceAsStream(path + "squid.json")));
            this.models.put("Wolf", Model.parse(loader.getResourceAsStream(path + "wolf.json")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}