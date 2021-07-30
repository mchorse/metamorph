package mchorse.metamorph.capabilities.render;

import mchorse.metamorph.api.MorphManager;
import mchorse.metamorph.api.MorphUtils;
import mchorse.metamorph.api.morphs.AbstractMorph;
import mchorse.metamorph.client.EntityModelHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelRenderer implements IModelRenderer
{
    public static long selectorsUpdate = System.currentTimeMillis();

    public EntitySelector selector;
    public AbstractMorph morph;
    public long lastUpdate = -1;

    private int timer;
    private float lastW = -1;
    private float lastH = -1;

    public static IModelRenderer get(Entity entity)
    {
        return entity.getCapability(ModelProvider.MODEL, null);
    }

    @Override
    public void update(EntityLivingBase target)
    {
        if (this.lastUpdate < selectorsUpdate || this.IsNotMatchedAnymore(target))
        {
            this.lastUpdate = selectorsUpdate;

            this.updateSelector(target);
        }

        if (this.selector != null && this.morph != null)
        {
            this.morph.update(target);
        }
    }

    private boolean IsNotMatchedAnymore(EntityLivingBase target)
    {
        this.timer += 1;

        if (this.timer > 10)
        {
            this.timer = 0;

            if (this.selector == null)
            {
                return true;
            }

            return !this.selector.matches(target);
        }

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateSelector(EntityLivingBase target)
    {
        this.selector = null;
        this.morph = null;

        for (EntitySelector selector : EntityModelHandler.selectors)
        {
            if (selector.matches(target))
            {
                this.selector = selector;
                this.morph = MorphManager.INSTANCE.morphFromNBT(this.selector.morph);

                if (this.lastW < 0)
                {
                    this.lastW = target.width;
                    this.lastH = target.height;
                }

                return;
            }
        }

        if (this.selector == null && this.lastW > 0 && this.lastH > 0)
        {
            target.width = this.lastW;
            target.height = this.lastH;

            this.lastW = -1;
            this.lastH = -1;
        }
    }

    @Override
    public boolean canRender()
    {
        return this.selector != null;
    }

    /**
     * Render the animator controller based on given entity
     */
    @Override
    public boolean render(EntityLivingBase entity, double x, double y, double z, float partialTicks)
    {
        return this.selector != null && MorphUtils.render(this.morph, entity, x, y, z, 0, partialTicks);
    }
}