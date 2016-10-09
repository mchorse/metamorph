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
            this.models.put("Sheep", Model.parse(loader.getResourceAsStream(path + "sheep.json")));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}