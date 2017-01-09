package mchorse.metamorph.api.models;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * This class responsible for storing domain custom models and sending models to
 * players who are logged in.
 */
public class ModelManager
{
    /**
     * Cached models, they're loaded from stuffs
     */
    public Map<String, Model> models = new HashMap<String, Model>();

    /**
     * Load a custom model with name and lowercase'd filename generated from 
     * name. 
     */
    public void load(String name) throws Exception
    {
        this.load(name, name.toLowerCase());
    }

    /**
     * Load a custom model with name and lowercase'd filename generated from 
     * name. 
     */
    public void load(String name, String filename) throws Exception
    {
        this.load(name, filename, "metamorph");
    }

    /**
     * Load a custom model with name and lowercase'd filename generated from 
     * name. 
     */
    public void load(String name, String filename, String modId) throws Exception
    {
        String path = "assets/" + modId + "/models/entity/";
        ClassLoader loader = this.getClass().getClassLoader();

        this.load(name, loader.getResourceAsStream(path + filename + ".json"));
    }

    /**
     * Load a custom model with name and filename
     */
    public void load(String name, InputStream stream) throws Exception
    {
        this.models.put(name, Model.parse(stream));
    }
}