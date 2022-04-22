// 
// Decompiled by Procyon v0.6.0
// 

package thaumcraft.common.lib.network.playerdata;

import net.minecraft.util.IThreadListener;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketFocusNameToServer implements IMessage, IMessageHandler<PacketFocusNameToServer, IMessage>
{
    private long loc;
    private String name;
    
    public PacketFocusNameToServer() {
    }
    
    public PacketFocusNameToServer(final BlockPos pos, final String name) {
        this.loc = pos.toLong();
        this.name = name;
    }
    
    public void toBytes(final ByteBuf buffer) {
        buffer.writeLong(this.loc);
        ByteBufUtils.writeUTF8String(buffer, this.name);
    }
    
    public void fromBytes(final ByteBuf buffer) {
        this.loc = buffer.readLong();
        this.name = ByteBufUtils.readUTF8String(buffer);
    }
    
    public IMessage onMessage(final PacketFocusNameToServer message, final MessageContext ctx) {
        final IThreadListener mainThread = ctx.getServerHandler().player.getServerWorld();
        mainThread.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                if (ctx.getServerHandler().player == null) {
                    return;
                }
                final BlockPos pos = BlockPos.fromLong(message.loc);
                final TileEntity rt = ctx.getServerHandler().player.world.getTileEntity(pos);
                if (rt != null && rt instanceof TileFocalManipulator) {
                    ((TileFocalManipulator)rt).focusName = message.name;
                    rt.markDirty();
                }
            }
        });
        return null;
    }
}