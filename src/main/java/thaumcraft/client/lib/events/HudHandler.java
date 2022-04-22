// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.client.lib.events;

import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.inventory.Slot;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import net.minecraft.client.gui.inventory.GuiContainer;
import java.util.Iterator;
import net.minecraft.client.renderer.RenderHelper;
import java.awt.Color;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.casters.ItemFocus;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.FXDispatcher;
import net.minecraft.util.math.MathHelper;
import java.util.Random;
import thaumcraft.client.lib.UtilsFX;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.common.items.tools.ItemSanityChecker;
import thaumcraft.common.items.tools.ItemThaumometer;
import thaumcraft.common.config.ModConfig;
import thaumcraft.api.casters.ICaster;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import java.text.DecimalFormat;
import thaumcraft.common.world.aura.AuraChunk;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.util.ResourceLocation;

public class HudHandler
{
    final ResourceLocation HUD;
    public LinkedBlockingQueue<KnowledgeGainTracker> knowledgeGainTrackers;
    public static final ResourceLocation BOOK;
    public static final ResourceLocation[] KNOW_TYPE;
    float kgFade;
    public static AuraChunk currentAura;
    private final float VISCON = 525.0f;
    long nextsync;
    DecimalFormat secondsFormatter;
    ItemStack lastItem;
    int lastCount;
    final ResourceLocation TAGBACK;
    
    public HudHandler() {
        this.HUD = new ResourceLocation("thaumcraft", "textures/gui/hud.png");
        this.knowledgeGainTrackers = new LinkedBlockingQueue<KnowledgeGainTracker>();
        this.kgFade = 0.0f;
        this.nextsync = 0L;
        this.secondsFormatter = new DecimalFormat("#######.#");
        this.lastItem = null;
        this.lastCount = 0;
        this.TAGBACK = new ResourceLocation("thaumcraft", "textures/aspects/_back.png");
    }
    
    @SideOnly(Side.CLIENT)
    void renderHuds(final Minecraft mc, final float renderTickTime, final EntityPlayer player, final long time) {
        GL11.glPushMatrix();
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -2000.0f);
        final int ww = sr.getScaledWidth();
        final int hh = sr.getScaledHeight();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        this.renderHudsInGUI(mc, renderTickTime, player, time, ww, hh);
        if (mc.inGameHasFocus && Minecraft.isGuiEnabled()) {
            mc.renderEngine.bindTexture(this.HUD);
            ItemStack handStack = player.getHeldItemMainhand();
            boolean rC = false;
            boolean rT = false;
            boolean rS = false;
            int start = 0;
            for (int a = 0; a < 2; ++a) {
                if (handStack != null && !handStack.isEmpty()) {
                    if (!rC && handStack.getItem() instanceof ICaster) {
                        this.renderCastingWandHud(mc, renderTickTime, player, time, handStack, start);
                        rC = true;
                        if (!ModConfig.CONFIG_GRAPHICS.dialBottom) {
                            start += 33;
                        }
                    }
                    else if (!rT && handStack.getItem() instanceof ItemThaumometer) {
                        this.renderThaumometerHud(mc, renderTickTime, player, time, ww, hh, start);
                        rT = true;
                        start += 80;
                    }
                    else if (!rS && handStack.getItem() instanceof ItemSanityChecker) {
                        this.renderSanityHud(mc, renderTickTime, player, time, start);
                        rS = true;
                        start += 75;
                    }
                }
                handStack = player.getHeldItemOffhand();
            }
        }
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    @SideOnly(Side.CLIENT)
    void renderHudsInGUI(final Minecraft mc, final float renderTickTime, final EntityPlayer player, final long time, final int ww, final int hh) {
        if (this.kgFade > 0.0f) {
            this.renderKnowledgeGains(mc, renderTickTime, player, time, ww, hh);
        }
    }
    
    @SideOnly(Side.CLIENT)
    void renderKnowledgeGains(final Minecraft mc, final float renderTickTime, final EntityPlayer player, final long time, final int ww, final int hh) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, this.kgFade / 40.0f);
        mc.renderEngine.bindTexture(HudHandler.BOOK);
        UtilsFX.drawTexturedQuadFull((float)(ww - 17), (float)(hh - 17), -90.0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final LinkedBlockingQueue<KnowledgeGainTracker> temp = new LinkedBlockingQueue<KnowledgeGainTracker>();
        int a = 0;
        while (!this.knowledgeGainTrackers.isEmpty()) {
            final KnowledgeGainTracker current = this.knowledgeGainTrackers.poll();
            if (current != null) {
                mc.renderEngine.bindTexture(HudHandler.KNOW_TYPE[current.type.ordinal()]);
                final Random rand = new Random(current.seed);
                GL11.glPushMatrix();
                float s = 16.0f;
                float x = (float)(ww / 4 + rand.nextInt(32));
                float y = (float)(hh / 3 + rand.nextInt(32));
                float wot = 0.0f;
                if (current.progress < current.max * 0.66f) {
                    final float q = (current.progress - renderTickTime) / (current.max * 0.66f);
                    s *= q;
                    final float m = (float)Math.sin(q * 3.141592653589793 - 1.5707963267948966) * 0.5f + 0.5f;
                    y *= m;
                    final float d = (float)Math.sin(m * 3.141592653589793 * 0.5);
                    x *= d;
                }
                else {
                    wot = current.max - current.progress + renderTickTime;
                    final float wot2 = wot / (current.max * 0.33f);
                    final float m = (float)Math.sin(wot2 * 3.141592653589793 * 2.0 - 1.5707963267948966) * 0.5f + 1.5f;
                    if (wot2 < 0.5) {
                        s *= wot2 * 2.0f;
                    }
                    s *= m;
                }
                final float xx = ww - 12 + rand.nextInt(8) - x;
                final float yy = hh - 12 + rand.nextInt(8) - y;
                if (current.sparks && player.getRNG().nextInt((int)(1.0f + current.progress / (float)current.max * 10.0f)) == 0) {
                    final float r = MathHelper.getInt(player.world.rand, 255, 255) / 255.0f;
                    final float g = MathHelper.getInt(player.world.rand, 189, 255) / 255.0f;
                    final float b = MathHelper.getInt(player.world.rand, 64, 255) / 255.0f;
                    FXDispatcher.INSTANCE.drawSimpleSparkleGui(player.world.rand, xx + player.world.rand.nextGaussian() * 5.0, yy + player.world.rand.nextGaussian() * 5.0, player.world.rand.nextGaussian(), player.world.rand.nextGaussian(), 24.0f, r, g, b, player.world.rand.nextInt(5), 0.9f, -1.0f);
                }
                GL11.glTranslatef(xx, yy, (float)(-80 + a));
                GL11.glRotatef((float)(84 + rand.nextInt(12)), 0.0f, 0.0f, -1.0f);
                UtilsFX.renderQuadCentered(1, 1, 0, s, 1.0f, 1.0f, 1.0f, 200, 771, 1.0f);
                if (current.category != null) {
                    mc.renderEngine.bindTexture(current.category.icon);
                    GL11.glTranslatef(0.0f, 0.0f, 1.0f);
                    UtilsFX.renderQuadCentered(1, 1, 0, s * 0.75f, 1.0f, 1.0f, 1.0f, 200, 771, 1.0f);
                }
                if (current.progress > current.max * 0.9f) {
                    final float wot3 = wot / (current.max * 0.1f);
                    final float m2 = (float)Math.sin(wot3 * 3.141592653589793 * 2.0 - 1.5707963267948966) * 0.25f + 0.25f;
                    final float size = 64.0f * m2;
                    GL11.glRotatef((float)rand.nextInt(360), 0.0f, 0.0f, -1.0f);
                    mc.renderEngine.bindTexture(ParticleEngine.particleTexture);
                    final float r2 = MathHelper.getInt(rand, 255, 255) / 255.0f;
                    final float g2 = MathHelper.getInt(rand, 189, 255) / 255.0f;
                    final float b2 = MathHelper.getInt(rand, 64, 255) / 255.0f;
                    UtilsFX.renderQuadCentered(64, 64, 320 + rand.nextInt(16), size, r2, g2, b2, 200, 1, 1.0f);
                }
                if (current.progress < current.max * 0.1f) {
                    final float wot3 = 1.0f - (current.progress - renderTickTime) / (current.max * 0.1f);
                    final float m2 = (float)Math.sin(wot3 * 3.141592653589793 * 2.0 - 1.5707963267948966) * 0.25f + 0.25f;
                    final float size = 32.0f * m2;
                    GL11.glRotatef((float)rand.nextInt(360), 0.0f, 0.0f, -1.0f);
                    mc.renderEngine.bindTexture(ParticleEngine.particleTexture);
                    final float r2 = MathHelper.getInt(rand, 255, 255) / 255.0f;
                    final float g2 = MathHelper.getInt(rand, 189, 255) / 255.0f;
                    final float b2 = MathHelper.getInt(rand, 64, 255) / 255.0f;
                    UtilsFX.renderQuadCentered(64, 64, 320 + rand.nextInt(16), size, r2, g2, b2, 200, 1, 1.0f);
                }
                temp.offer(current);
                GL11.glPopMatrix();
            }
            ++a;
        }
        while (!temp.isEmpty()) {
            this.knowledgeGainTrackers.offer(temp.poll());
        }
    }
    
    @SideOnly(Side.CLIENT)
    void renderThaumometerHud(final Minecraft mc, final float partialTicks, final EntityPlayer player, final long time, final int ww, final int hh, final int shifty) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        float base = MathHelper.clamp(HudHandler.currentAura.getBase() / 525.0f, 0.0f, 1.0f);
        float vis = MathHelper.clamp(HudHandler.currentAura.getVis() / 525.0f, 0.0f, 1.0f);
        float flux = MathHelper.clamp(HudHandler.currentAura.getFlux() / 525.0f, 0.0f, 1.0f);
        final float count = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted + partialTicks;
        final float count2 = Minecraft.getMinecraft().getRenderViewEntity().ticksExisted / 3.0f + partialTicks;
        if (flux + vis > 1.0f) {
            final float m = 1.0f / (flux + vis);
            base *= m;
            vis *= m;
            flux *= m;
        }
        float start = 10.0f + (1.0f - vis) * 64.0f;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glTranslated(2.0, shifty, 0.0);
        if (vis > 0.0f) {
            GL11.glPushMatrix();
            GL11.glColor4f(0.7f, 0.4f, 0.9f, 1.0f);
            GL11.glTranslated(5.0, start, 0.0);
            GL11.glScaled(1.0, vis, 1.0);
            UtilsFX.drawTexturedQuad(0.0f, 0.0f, 88.0f, 56.0f, 8.0f, 64.0f, -90.0);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glBlendFunc(770, 1);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
            GL11.glTranslated(5.0, start, 0.0);
            UtilsFX.drawTexturedQuad(0.0f, 0.0f, 96.0f, 56.0f + count % 64.0f, 8.0f, vis * 64.0f, -90.0);
            GL11.glBlendFunc(770, 771);
            GL11.glPopMatrix();
            if (player.isSneaking()) {
                GL11.glPushMatrix();
                GL11.glTranslated(16.0, start, 0.0);
                GL11.glScaled(0.5, 0.5, 0.5);
                final String msg = this.secondsFormatter.format(HudHandler.currentAura.getVis());
                mc.ingameGUI.drawString(mc.fontRenderer, msg, 0, 0, 15641343);
                GL11.glPopMatrix();
                mc.renderEngine.bindTexture(this.HUD);
            }
        }
        if (flux > 0.0f) {
            start = 10.0f + (1.0f - flux - vis) * 64.0f;
            GL11.glPushMatrix();
            GL11.glColor4f(0.25f, 0.1f, 0.3f, 1.0f);
            GL11.glTranslated(5.0, start, 0.0);
            GL11.glScaled(1.0, flux, 1.0);
            UtilsFX.drawTexturedQuad(0.0f, 0.0f, 88.0f, 56.0f, 8.0f, 64.0f, -90.0);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glBlendFunc(770, 1);
            GL11.glColor4f(0.7f, 0.4f, 1.0f, 0.5f);
            GL11.glTranslated(5.0, start, 0.0);
            UtilsFX.drawTexturedQuad(0.0f, 0.0f, 104.0f, 120.0f - count2 % 64.0f, 8.0f, flux * 64.0f, -90.0);
            GL11.glBlendFunc(770, 771);
            GL11.glPopMatrix();
            if (player.isSneaking()) {
                GL11.glPushMatrix();
                GL11.glTranslated(16.0, start - 4.0f, 0.0);
                GL11.glScaled(0.5, 0.5, 0.5);
                final String msg = this.secondsFormatter.format(HudHandler.currentAura.getFlux());
                mc.ingameGUI.drawString(mc.fontRenderer, msg, 0, 0, 11145659);
                GL11.glPopMatrix();
                mc.renderEngine.bindTexture(this.HUD);
            }
        }
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        UtilsFX.drawTexturedQuad(1.0f, 1.0f, 72.0f, 48.0f, 16.0f, 80.0f, -90.0);
        GL11.glPopMatrix();
        start = 8.0f + (1.0f - base) * 64.0f;
        GL11.glPushMatrix();
        UtilsFX.drawTexturedQuad(2.0f, start, 117.0f, 61.0f, 14.0f, 5.0f, -90.0);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
    
    @SideOnly(Side.CLIENT)
    void renderSanityHud(final Minecraft mc, final Float partialTicks, final EntityPlayer player, final long time, final int shifty) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glTranslated(0.0, shifty, 0.0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        UtilsFX.drawTexturedQuad(1.0f, 1.0f, 152.0f, 0.0f, 20.0f, 76.0f, -90.0);
        final int p = ThaumcraftCapabilities.getWarp(player).get(IPlayerWarp.EnumWarpType.PERMANENT);
        final int s = ThaumcraftCapabilities.getWarp(player).get(IPlayerWarp.EnumWarpType.NORMAL);
        final int t = ThaumcraftCapabilities.getWarp(player).get(IPlayerWarp.EnumWarpType.TEMPORARY);
        float tw = (float)(p + s + t);
        float mod = 1.0f;
        if (tw > 100.0f) {
            mod = 100.0f / tw;
            tw = 100.0f;
        }
        final int gap = (int)((100.0f - tw) / 100.0f * 48.0f);
        final int wt = (int)(t / 100.0f * 48.0f * mod);
        final int ws = (int)(s / 100.0f * 48.0f * mod);
        if (t > 0) {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0f, 0.5f, 1.0f, 1.0f);
            UtilsFX.drawTexturedQuad(7.0f, (float)(21 + gap), 200.0f, (float)gap, 8.0f, (float)(wt + gap), -90.0);
            GL11.glPopMatrix();
        }
        if (s > 0) {
            GL11.glPushMatrix();
            GL11.glColor4f(0.75f, 0.0f, 0.75f, 1.0f);
            UtilsFX.drawTexturedQuad(7.0f, (float)(21 + wt + gap), 200.0f, (float)(wt + gap), 8.0f, (float)(wt + ws + gap), -90.0);
            GL11.glPopMatrix();
        }
        if (p > 0) {
            GL11.glPushMatrix();
            GL11.glColor4f(0.5f, 0.0f, 0.5f, 1.0f);
            UtilsFX.drawTexturedQuad(7.0f, (float)(21 + wt + ws + gap), 200.0f, (float)(wt + ws + gap), 8.0f, 48.0f, -90.0);
            GL11.glPopMatrix();
        }
        GL11.glPushMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        UtilsFX.drawTexturedQuad(1.0f, 1.0f, 176.0f, 0.0f, 20.0f, 76.0f, -90.0);
        GL11.glPopMatrix();
        if (tw >= 100.0f) {
            GL11.glPushMatrix();
            GL11.glScaled(0.75, 0.75, 1.0);
            GL11.glTranslated(mc.player.getRNG().nextInt(2), mc.player.getRNG().nextInt(2), 0.0);
            UtilsFX.drawTexturedQuad(3.0f, 3.0f, 216.0f, 0.0f, 20.0f, 16.0f, -90.0);
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }
    
    @SideOnly(Side.CLIENT)
    void renderChargeMeters(final Minecraft mc, final float renderTickTime, final EntityPlayer player, final long time, final int ww, final int hh) {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int start = 0;
        final int total = 0;
    }
    
    @SideOnly(Side.CLIENT)
    void renderCastingWandHud(final Minecraft mc, final float partialTicks, final EntityPlayer player, final long time, final ItemStack wandstack, final int shifty) {
        final ICaster wand = (ICaster)wandstack.getItem();
        final short short1 = 240;
        final short short2 = 240;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, short1 / 1.0f, short2 / 1.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, (float)shifty, 0.0f);
        final ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, sr.getScaledWidth_double(), sr.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        final int l = sr.getScaledHeight();
        final int dailLocation = ModConfig.CONFIG_GRAPHICS.dialBottom ? (l - 32) : 0;
        GL11.glTranslatef(0.0f, (float)dailLocation, -2000.0f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        mc.renderEngine.bindTexture(this.HUD);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 0.5);
        UtilsFX.drawTexturedQuad(0.0f, 0.0f, 0.0f, 0.0f, 64.0f, 64.0f, -90.0);
        GL11.glPopMatrix();
        GL11.glTranslatef(16.0f, 16.0f, 0.0f);
        final int max = HudHandler.currentAura.getBase();
        final int amt = (int)HudHandler.currentAura.getVis();
        final ItemFocus focus = (ItemFocus)wand.getFocus(wandstack);
        final ItemStack focusStack = wand.getFocusStack(wandstack);
        GL11.glPushMatrix();
        GL11.glTranslatef(16.0f, -10.0f, 0.0f);
        GL11.glScaled(0.5, 0.5, 0.5);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int loc = (int)(30.0f * amt / max);
        GL11.glPushMatrix();
        final Color ac = new Color(Aspect.ENERGY.getColor());
        GL11.glColor4f(ac.getRed() / 255.0f, ac.getGreen() / 255.0f, ac.getBlue() / 255.0f, 0.8f);
        UtilsFX.drawTexturedQuad(-4.0f, (float)(35 - loc), 104.0f, 0.0f, 8.0f, (float)loc, -90.0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        UtilsFX.drawTexturedQuad(-8.0f, -3.0f, 72.0f, 0.0f, 16.0f, 42.0f, -90.0);
        GL11.glPopMatrix();
        final int sh = 0;
        if (player.isSneaking()) {
            GL11.glPushMatrix();
            GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
            String msg = this.secondsFormatter.format(amt);
            mc.ingameGUI.drawString(mc.fontRenderer, msg, -32, -4, 16777215);
            GL11.glPopMatrix();
            if (focus != null && focus.getVisCost(focusStack) > 0.0f) {
                final float mod = wand.getConsumptionModifier(wandstack, player, false);
                GL11.glPushMatrix();
                msg = this.secondsFormatter.format(focus.getVisCost(focusStack) * mod);
                mc.ingameGUI.drawString(mc.fontRenderer, msg, -32 - mc.ingameGUI.getFontRenderer().getStringWidth(msg) / 2, 32, 16777215);
                GL11.glPopMatrix();
            }
            mc.renderEngine.bindTexture(this.HUD);
        }
        GL11.glPopMatrix();
        if (focus != null) {
            final ItemStack pickedStack = wand.getPickedBlock(player.inventory.getCurrentItem());
            if (pickedStack != null && !pickedStack.isEmpty()) {
                this.renderWandTradeHud(partialTicks, player, time, pickedStack);
            }
            else {
                GL11.glPushMatrix();
                GL11.glTranslatef(-24.0f, -24.0f, 90.0f);
                RenderHelper.enableGUIStandardItemLighting();
                GL11.glDisable(2896);
                GL11.glEnable(32826);
                GL11.glEnable(2903);
                GL11.glEnable(2896);
                try {
                    mc.getRenderItem().renderItemAndEffectIntoGUI(wand.getFocusStack(wandstack), 16, 16);
                }
                catch (final Exception ex) {}
                GL11.glDisable(2896);
                GL11.glPopMatrix();
            }
        }
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
    
    @SideOnly(Side.CLIENT)
    public void renderWandTradeHud(final float partialTicks, final EntityPlayer player, final long time, final ItemStack picked) {
        if (picked == null) {
            return;
        }
        final Minecraft mc = Minecraft.getMinecraft();
        int amount = this.lastCount;
        if (this.lastItem == null || this.lastItem.isEmpty() || player.inventory.getTimesChanged() > 0 || !picked.isItemEqual(this.lastItem)) {
            amount = 0;
            for (final ItemStack is : player.inventory.mainInventory) {
                if (is != null && !is.isEmpty() && is.isItemEqual(picked)) {
                    amount += is.getCount();
                }
            }
            this.lastItem = picked;
            player.inventory.markDirty();
        }
        this.lastCount = amount;
        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glEnable(2896);
        try {
            mc.getRenderItem().renderItemAndEffectIntoGUI(picked, -8, -8);
        }
        catch (final Exception ex) {}
        GL11.glDisable(2896);
        GL11.glPushMatrix();
        final String am = "" + amount;
        final int sw = mc.fontRenderer.getStringWidth(am);
        GL11.glTranslatef(0.0f, (float)(-mc.fontRenderer.FONT_HEIGHT), 500.0f);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        for (int a = -1; a <= 1; ++a) {
            for (int b = -1; b <= 1; ++b) {
                if ((a == 0 || b == 0) && (a != 0 || b != 0)) {
                    mc.fontRenderer.drawString(am, a + 16 - sw, b + 24, 0);
                }
            }
        }
        mc.fontRenderer.drawString(am, 16 - sw, 24, 16777215);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
    
    public void renderAspectsInGui(final GuiContainer gui, final EntityPlayer player, final ItemStack stack, final int sd, final int sx, final int sy) {
        final AspectList tags = ThaumcraftCraftingManager.getObjectTags(stack);
        if (tags == null) {
            return;
        }
        GL11.glPushMatrix();
        int x = 0;
        int y = 0;
        int index = 0;
        if (tags.size() > 0) {
            for (final Aspect tag : tags.getAspectsSortedByAmount()) {
                if (tag != null) {
                    x = sx + index * 18;
                    y = sy + sd - 16;
                    UtilsFX.drawTag(x, y, tag, (float)tags.getAmount(tag), 0, gui.zLevel);
                    ++index;
                }
            }
        }
        GL11.glPopMatrix();
    }
    
    private boolean isMouseOverSlot(final Slot par1Slot, int par2, int par3, final int par4, final int par5) {
        final int var4 = par4;
        final int var5 = par5;
        par2 -= var4;
        par3 -= var5;
        return par2 >= par1Slot.xPos - 1 && par2 < par1Slot.xPos + 16 + 1 && par3 >= par1Slot.yPos - 1 && par3 < par1Slot.yPos + 16 + 1;
    }
    
    static {
        BOOK = new ResourceLocation("thaumcraft", "textures/items/thaumonomicon.png");
        KNOW_TYPE = new ResourceLocation[] { new ResourceLocation("thaumcraft", "textures/research/knowledge_theory.png"), new ResourceLocation("thaumcraft", "textures/research/knowledge_observation.png") };
        HudHandler.currentAura = new AuraChunk(null, (short)0, 0.0f, 0.0f);
    }
    
    public static class KnowledgeGainTracker
    {
        IPlayerKnowledge.EnumKnowledgeType type;
        ResearchCategory category;
        int progress;
        int max;
        long seed;
        boolean sparks;
        
        public KnowledgeGainTracker(final IPlayerKnowledge.EnumKnowledgeType type, final ResearchCategory category, int progress, final long seed) {
            this.sparks = false;
            this.type = type;
            this.category = category;
            if (type == IPlayerKnowledge.EnumKnowledgeType.THEORY) {
                progress += 10;
            }
            this.progress = progress;
            this.max = progress;
            this.seed = seed;
        }
    }
}