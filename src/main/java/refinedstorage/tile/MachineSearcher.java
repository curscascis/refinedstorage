package refinedstorage.tile;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import refinedstorage.RefinedStorageBlocks;

import java.util.List;
import java.util.Set;

public class MachineSearcher {
    public static void search(TileController controller, BlockPos current, Set<String> visited, List<TileMachine> machines) {
        if (visited.contains(current.getX() + "," + current.getY() + "," + current.getZ()) || controller.getPos().equals(current)) {
            return;
        }

        visited.add(current.getX() + "," + current.getY() + "," + current.getZ());

        Block block = controller.getWorld().getBlockState(current).getBlock();
        TileEntity tile = controller.getWorld().getTileEntity(current);

        if (tile instanceof TileController) {
            if (!tile.getPos().equals(controller.getPos())) {
                controller.getWorld().createExplosion(null, tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 4.5f, true);
            }
        }

        if (tile instanceof TileMachine) {
            TileMachine machine = (TileMachine) tile;

            if (machine.getRedstoneMode().isEnabled(controller.getWorld(), tile.getPos())) {
                machines.add(machine);
            } else if (machine instanceof TileRelay) {
                // If the relay is disabled we can't search any further
                return;
            }
        }

        if (tile instanceof TileMachine || block == RefinedStorageBlocks.CABLE) {
            for (EnumFacing dir : EnumFacing.VALUES) {
                search(controller, current.offset(dir), visited, machines);
            }
        }
    }
}
