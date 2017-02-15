package mchorse.metamorph.config;

import mchorse.metamorph.Metamorph;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Metamorph config class
 * 
 * Intance of this class is responsible for storing configuration for Metamorph 
 * mod.
 */
public class MetamorphConfig
{
    /* Config options */

    /**
     * Prevents ghosts from spawning if the player has already a currently 
     * killed mob's morph 
     */
    public boolean prevent_ghosts;

    /**
     * Retain morphs when player died 
     */
    public boolean keep_morphs;

    /**
     * Hide username in the survival morphing menu. Added just because, for 
     * no reason, if you're asking 
     */
    public boolean hide_username;

    /**
     * Prevent acquiring morphs by killing morphs 
     */
    public boolean prevent_kill_acquire;

    /**
     * Show demorph as an option in survival morph menu 
     */
    public boolean show_demorph;

    /* End of config options */

    /**
     * Forge-provided configuration object class instance stuff...
     */
    private Configuration config;

    public MetamorphConfig(Configuration config)
    {
        this.config = config;
        this.reload();
    }

    /**
     * Reload config values
     */
    public void reload()
    {
        String cat = Configuration.CATEGORY_GENERAL;
        String lang = "metamorph.config.";

        this.prevent_ghosts = this.config.getBoolean("prevent_ghosts", cat, true, "Prevent ghosts from spawning if player has morph of mob already?", lang + "prevent_ghosts");
        this.keep_morphs = this.config.getBoolean("keep_morphs", cat, true, "Retain morphs when player dies?", lang + "keep_morphs");
        this.hide_username = this.config.getBoolean("hide_username", cat, false, "Hide username in survival morphing menu", lang + "hide_username");
        this.prevent_kill_acquire = this.config.getBoolean("prevent_kill_acquire", cat, false, "Prevent morph acquiring by killing a mob (or specifically prevent ghost spawning in any case)?", lang + "prevent_kill_acquire");
        this.show_demorph = this.config.getBoolean("show_demorph", cat, true, "Show demorph as an option in survival morph menu", lang + "show_demorph");

        this.config.getCategory(cat).setComment("General configuration of Metamorph mod");

        if (this.config.hasChanged())
        {
            this.config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(Metamorph.MODID) && this.config.hasChanged())
        {
            this.reload();
        }
    }
}