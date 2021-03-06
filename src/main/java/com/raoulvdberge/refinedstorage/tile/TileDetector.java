package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeDetector;
import com.raoulvdberge.refinedstorage.gui.GuiDetector;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;

public class TileDetector extends TileNode<NetworkNodeDetector> {
    private static final String NBT_POWERED = "Powered";

    public static final TileDataParameter<Integer, TileDetector> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileDetector> TYPE = IType.createParameter();
    public static final TileDataParameter<Integer, TileDetector> MODE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getMode(), (t, v) -> {
        if (v == NetworkNodeDetector.MODE_UNDER || v == NetworkNodeDetector.MODE_EQUAL || v == NetworkNodeDetector.MODE_ABOVE || v == NetworkNodeDetector.MODE_AUTOCRAFTING) {
            t.getNode().setMode(v);
            t.getNode().markDirty();
        }
    });
    public static final TileDataParameter<Integer, TileDetector> AMOUNT = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getAmount(), (t, v) -> {
        t.getNode().setAmount(v);
        t.getNode().markDirty();
    }, p -> {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            GuiScreen gui = Minecraft.getMinecraft().currentScreen;

            if (gui instanceof GuiDetector) {
                GuiDetector.AMOUNT.setText(String.valueOf(p));
            }
        }
    });

    public TileDetector() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(AMOUNT);
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        getNode().setPowered(tag.getBoolean(NBT_POWERED));

        super.readUpdate(tag);
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setBoolean(NBT_POWERED, getNode().isPowered());

        return tag;
    }

    @Override
    @Nonnull
    public NetworkNodeDetector createNode(World world, BlockPos pos) {
        return new NetworkNodeDetector(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeDetector.ID;
    }
}
