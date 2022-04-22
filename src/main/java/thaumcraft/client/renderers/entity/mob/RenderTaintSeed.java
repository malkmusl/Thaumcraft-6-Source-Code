// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.entity.mob;

import net.minecraft.entity.EntityLiving;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.client.model.ModelBase;
import thaumcraft.client.renderers.models.entity.ModelTaintSeed;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;
import net.minecraft.client.renderer.entity.RenderLiving;

@SideOnly(Side.CLIENT)
public class RenderTaintSeed extends RenderLiving<EntityTaintSeed>
{
    private static final ResourceLocation rl;
    
    public RenderTaintSeed(final RenderManager rm) {
        super(rm, new ModelTaintSeed(), 0.4f);
    }
    
    public RenderTaintSeed(final RenderManager rm, final ModelBase modelbase, final float sz) {
        super(rm, modelbase, sz);
    }
    
    protected ResourceLocation getEntityTexture(final EntityTaintSeed entity) {
        return RenderTaintSeed.rl;
    }
    
    public void doRender(final EntityTaintSeed entity, final double x, final double y, final double z, final float entityYaw, final float partialTicks) {
        if (MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Pre(entity, this, x, y, z))) {
            return;
        }
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
        final boolean shouldSit = entity.isRiding() && entity.getRidingEntity() != null && entity.getRidingEntity().shouldRiderSit();
        this.mainModel.isRiding = shouldSit;
        this.mainModel.isChild = entity.isChild();
        try {
            GlStateManager.pushMatrix();
            final float f = 0.0f;
            final float f2 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
            this.renderLivingAt(entity, x, y, z);
            final float f3 = this.handleRotationFloat(entity, partialTicks);
            this.applyRotations(entity, f3, f, partialTicks);
            final float f4 = this.prepareScale(entity, partialTicks);
            float f5 = 0.0f;
            float f6 = 0.0f;
            f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            f6 = entity.limbSwing - entity.limbSwingAmount * (1.0f - partialTicks);
            if (f5 > 1.0f) {
                f5 = 1.0f;
            }
            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
            this.mainModel.setRotationAngles(f6, f5, f3, f, f2, f4, entity);
            if (this.renderOutlines) {
                final boolean flag1 = this.setScoreTeamColor(entity);
                GlStateManager.enableColorMaterial();
                GlStateManager.enableOutlineMode(this.getTeamColor(entity));
                if (!this.renderMarker) {
                    this.renderModel(entity, f6, f5, f3, f, f2, f4);
                }
                this.renderLayers(entity, f6, f5, partialTicks, f3, f, f2, f4);
                GlStateManager.disableOutlineMode();
                GlStateManager.disableColorMaterial();
                if (flag1) {
                    this.unsetScoreTeamColor();
                }
            }
            else {
                final boolean flag2 = this.setDoRenderBrightness(entity, partialTicks);
                this.renderModel(entity, f6, f5, f3, f, f2, f4);
                if (flag2) {
                    this.unsetBrightness();
                }
                GlStateManager.depthMask(true);
                this.renderLayers(entity, f6, f5, partialTicks, f3, f, f2, f4);
            }
            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
        }
        catch (final Exception ex) {}
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        if (!this.renderOutlines) {
            this.renderName(entity, x, y, z);
        }
        MinecraftForge.EVENT_BUS.post(new RenderLivingEvent.Post(entity, this, x, y, z));
    }
    
    static {
        rl = new ResourceLocation("thaumcraft", "textures/entity/taintseed.png");
    }
}