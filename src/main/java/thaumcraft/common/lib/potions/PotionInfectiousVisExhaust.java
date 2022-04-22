// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.potions;

import java.util.Iterator;
import java.util.List;
import thaumcraft.api.potions.PotionVisExhaust;
import net.minecraft.potion.PotionEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.potion.Potion;

public class PotionInfectiousVisExhaust extends Potion
{
    public static Potion instance;
    private int statusIconIndex;
    static final ResourceLocation rl;
    
    public PotionInfectiousVisExhaust(final boolean par2, final int par3) {
        super(par2, par3);
        this.statusIconIndex = -1;
        this.setIconIndex(0, 0);
        this.setPotionName("potion.infvisexhaust");
        this.setIconIndex(6, 1);
        this.setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(PotionInfectiousVisExhaust.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(final EntityLivingBase target, final int par2) {
        final List<EntityLivingBase> targets = target.world.getEntitiesWithinAABB((Class)EntityLivingBase.class, target.getEntityBoundingBox().grow(4.0, 4.0, 4.0));
        if (targets.size() > 0) {
            for (final EntityLivingBase e : targets) {
                if (!e.isPotionActive(PotionInfectiousVisExhaust.instance)) {
                    if (par2 > 0) {
                        e.addPotionEffect(new PotionEffect(PotionInfectiousVisExhaust.instance, 6000, par2 - 1, false, true));
                    }
                    else {
                        e.addPotionEffect(new PotionEffect(PotionVisExhaust.instance, 6000, 0, false, true));
                    }
                }
            }
        }
    }
    
    public boolean isReady(final int par1, final int par2) {
        return par1 % 40 == 0;
    }
    
    static {
        PotionInfectiousVisExhaust.instance = null;
        rl = new ResourceLocation("thaumcraft", "textures/misc/potions.png");
    }
}