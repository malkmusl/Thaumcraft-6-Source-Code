// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.items.casters.foci;

import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.particle.Particle;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import net.minecraft.world.World;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.common.lib.events.ServerEvents;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.casters.Trajectory;
import net.minecraft.util.math.RayTraceResult;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;

public class FocusEffectBreak extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSBREAK";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.BREAK";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.ENTROPY;
    }
    
    @Override
    public int getComplexity() {
        return this.getSettingValue("power") * 3 + this.getSettingValue("silk") * 4 + ((this.getSettingValue("fortune") == 0) ? 0 : ((this.getSettingValue("fortune") + 1) * 3));
    }
    
    @Override
    public boolean execute(final RayTraceResult target, final Trajectory trajectory, final float finalPower, final int num) {
        if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
            PacketHandler.INSTANCE.sendToAllAround(new PacketFXFocusPartImpact(target.getBlockPos().getX() + 0.5, target.getBlockPos().getY() + 0.5, target.getBlockPos().getZ() + 0.5, new String[] { this.getKey() }), new NetworkRegistry.TargetPoint(this.getPackage().world.provider.getDimension(), target.hitVec.x, target.hitVec.y, target.hitVec.z, 64.0));
            final boolean silk = this.getSettingValue("silk") > 0;
            final int fortune = this.getSettingValue("fortune");
            final float strength = this.getSettingValue("power") * finalPower;
            float dur = this.getPackage().world.getBlockState(target.getBlockPos()).getBlockHardness(this.getPackage().world, target.getBlockPos()) * 100.0f;
            dur = (float)Math.sqrt(dur);
            if (this.getPackage().getCaster() instanceof EntityPlayer) {
                ServerEvents.addBreaker(this.getPackage().world, target.getBlockPos(), this.getPackage().world.getBlockState(target.getBlockPos()), (EntityPlayer)this.getPackage().getCaster(), true, silk, fortune, strength, dur, dur, (int)(dur / strength / 3.0f * num), 0.25f + (silk ? 0.25f : 0.0f) + fortune * 0.1f, null);
            }
            return true;
        }
        return true;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        final int[] silk = { 0, 1 };
        final String[] silkDesc = { "focus.common.no", "focus.common.yes" };
        final int[] fortune = { 0, 1, 2, 3, 4 };
        final String[] fortuneDesc = { "focus.common.no", "I", "II", "III", "IV" };
        return new NodeSetting[] { new NodeSetting("power", "focus.break.power", new NodeSetting.NodeSettingIntRange(1, 5)), new NodeSetting("fortune", "focus.common.fortune", new NodeSetting.NodeSettingIntList(fortune, fortuneDesc)), new NodeSetting("silk", "focus.common.silk", new NodeSetting.NodeSettingIntList(silk, silkDesc)) };
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void renderParticleFX(final World world, final double posX, final double posY, final double posZ, final double motionX, final double motionY, final double motionZ) {
        final FXGeneric fb = new FXGeneric(world, posX, posY, posZ, motionX, motionY, motionZ);
        fb.setMaxAge(6 + world.rand.nextInt(6));
        final int q = world.rand.nextInt(4);
        fb.setParticles(704 + q * 3, 3, 1);
        fb.setSlowDown(0.8);
        fb.setScale((float)(1.7000000476837158 + world.rand.nextGaussian() * 0.30000001192092896));
        ParticleEngine.addEffect(world, fb);
    }
    
    @Override
    public void onCast(final Entity caster) {
        caster.world.playSound(null, caster.getPosition().up(), SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.PLAYERS, 0.1f, 2.0f + (float)(caster.world.rand.nextGaussian() * 0.05000000074505806));
    }
}