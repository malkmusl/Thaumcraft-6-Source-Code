// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import thaumcraft.client.lib.UtilsFX;
import net.minecraft.init.Blocks;
import net.minecraft.client.Minecraft;
import thaumcraft.common.lib.utils.BlockStateUtils;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;
import thaumcraft.client.lib.obj.AdvancedModelLoader;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;
import thaumcraft.client.lib.obj.IModelCustom;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

@SideOnly(Side.CLIENT)
public class TileGolemBuilderRenderer extends TileEntitySpecialRenderer
{
    private IModelCustom model;
    private static final ResourceLocation TM;
    private static final ResourceLocation TEX;
    EntityItem entityitem;
    
    public TileGolemBuilderRenderer() {
        this.entityitem = null;
        this.model = AdvancedModelLoader.loadModel(TileGolemBuilderRenderer.TM);
    }
    
    public void renderTileEntityAt(final TileGolemBuilder tile, final double par2, final double par4, final double par6, final float pt, final int destroyStage) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float)par2 + 0.5f, (float)par4, (float)par6 + 0.5f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.bindTexture(TileGolemBuilderRenderer.TEX);
        if (destroyStage >= 0) {
            this.bindTexture(TileGolemBuilderRenderer.DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(5.0f, 5.0f, 2.0f);
            GlStateManager.translate(0.0625f, 0.0625f, 0.0625f);
            GlStateManager.matrixMode(5888);
        }
        else {
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            GL11.glScalef(1.0f, -1.0f, 1.0f);
            GL11.glMatrixMode(5888);
        }
        final EnumFacing facing = BlockStateUtils.getFacing(tile.getBlockMetadata());
        if (tile.getWorld() != null) {
            switch (facing.ordinal()) {
                case 5: {
                    GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 4: {
                    GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
                case 3: {
                    GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                    break;
                }
            }
        }
        this.model.renderAllExcept("press");
        GL11.glPushMatrix();
        final float h = (float)tile.press;
        final double s = Math.sin(Math.toRadians(h)) * 0.625;
        GL11.glTranslated(0.0, -s, 0.0);
        this.model.renderPart("press");
        GL11.glPopMatrix();
        if (destroyStage >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
        else {
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glMatrixMode(5888);
        }
        GL11.glTranslatef(-0.3125f, 0.625f, 1.3125f);
        GL11.glRotatef(90.0f, -1.0f, 0.0f, 0.0f);
        final TextureAtlasSprite icon = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.LAVA.getDefaultState());
        UtilsFX.renderQuadFromIcon(icon, 0.625f, 1.0f, 1.0f, 1.0f, 200, 771, 1.0f);
        GL11.glPopMatrix();
    }
    
    public void render(final TileEntity te, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        this.renderTileEntityAt((TileGolemBuilder)te, x, y, z, partialTicks, destroyStage);
    }
    
    static {
        TM = new ResourceLocation("thaumcraft", "models/block/golembuilder.obj");
        TEX = new ResourceLocation("thaumcraft", "textures/blocks/golembuilder.png");
    }
}