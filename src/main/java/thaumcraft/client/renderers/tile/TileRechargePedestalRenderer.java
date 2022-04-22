// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.renderers.tile;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.entity.item.EntityItem;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.tiles.devices.TileRechargePedestal;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

@SideOnly(Side.CLIENT)
public class TileRechargePedestalRenderer extends TileEntitySpecialRenderer<TileRechargePedestal>
{
    public void render(final TileRechargePedestal ped, final double x, final double y, final double z, final float partialTicks, final int destroyStage, final float alpha) {
        super.render(ped, x, y, z, partialTicks, destroyStage, alpha);
        if (ped != null && !ped.getSyncedStackInSlot(0).isEmpty()) {
            EntityItem entityitem = null;
            final float ticks = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + partialTicks;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x + 0.5f, (float)y + 0.75f, (float)z + 0.5f);
            GL11.glScaled(1.5, 1.5, 1.5);
            GL11.glRotatef(ticks % 360.0f, 0.0f, 1.0f, 0.0f);
            final ItemStack is = ped.getSyncedStackInSlot(0).copy();
            is.setCount(1);
            entityitem = new EntityItem(Minecraft.getMinecraft().world, 0.0, 0.0, 0.0, is);
            entityitem.hoverStart = 0.0f;
            final RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
            rendermanager.renderEntity(entityitem, 0.0, 0.0, 0.0, 0.0f, 0.0f, false);
            GL11.glPopMatrix();
        }
    }
}