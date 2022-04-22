// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.entities.ai.combat;

import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;

public class EntityCritterAIAttackMelee extends EntityAIAttackMelee
{
    public EntityCritterAIAttackMelee(final EntityCreature creature, final double speedIn, final boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);
    }
    
    protected void checkAndPerformAttack(final EntityLivingBase target, final double range) {
        final double d0 = this.getAttackReachSqr(target);
        if (range <= d0 && this.attackTick <= 0) {
            this.attackTick = 20;
            this.attacker.swingArm(EnumHand.MAIN_HAND);
            this.attackEntityAsMob(this.attacker, target);
        }
    }
    
    protected boolean attackEntityAsMob(final EntityLiving attacker, final Entity target) {
        float f = Math.max(2.0f, (attacker.height + attacker.width) * 2.0f);
        if (attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null) {
            f = (float)attacker.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        }
        int i = 0;
        if (target instanceof EntityLivingBase) {
            f += EnchantmentHelper.getModifierForCreature(attacker.getHeldItemMainhand(), ((EntityLivingBase)target).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(attacker);
        }
        final boolean flag = target.attackEntityFrom(DamageSource.causeMobDamage(attacker), f);
        if (flag) {
            if (i > 0 && target instanceof EntityLivingBase) {
                ((EntityLivingBase)target).knockBack(attacker, i * 0.5f, MathHelper.sin(attacker.rotationYaw * 0.017453292f), -MathHelper.cos(attacker.rotationYaw * 0.017453292f));
                attacker.motionX *= 0.6;
                attacker.motionZ *= 0.6;
            }
            final int j = EnchantmentHelper.getFireAspectModifier(attacker);
            if (j > 0) {
                target.setFire(j * 4);
            }
            if (target instanceof EntityPlayer) {
                final EntityPlayer entityplayer = (EntityPlayer)target;
                final ItemStack itemstack = attacker.getHeldItemMainhand();
                final ItemStack itemstack2 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;
                if (!itemstack.isEmpty() && !itemstack2.isEmpty() && itemstack.getItem().canDisableShield(itemstack, itemstack2, entityplayer, attacker) && itemstack2.getItem().isShield(itemstack2, entityplayer)) {
                    final float f2 = 0.25f + EnchantmentHelper.getEfficiencyModifier(attacker) * 0.05f;
                    if (attacker.getRNG().nextFloat() < f2) {
                        entityplayer.getCooldownTracker().setCooldown(itemstack2.getItem(), 100);
                        attacker.world.setEntityState(entityplayer, (byte)30);
                    }
                }
            }
            if (target instanceof EntityLivingBase) {
                EnchantmentHelper.applyThornEnchantments((EntityLivingBase)target, attacker);
            }
            EnchantmentHelper.applyArthropodEnchantments(attacker, target);
        }
        return flag;
    }
}