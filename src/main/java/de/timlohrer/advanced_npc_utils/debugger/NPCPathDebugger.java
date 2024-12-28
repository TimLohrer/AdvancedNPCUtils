package de.timlohrer.advanced_npc_utils.debugger;

import de.timlohrer.advanced_npc_utils.AdvancedNpcUtils;
import de.timlohrer.advanced_npc_utils.mixin.PathAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public interface NPCPathDebugger {
    List<DisplayEntity.BlockDisplayEntity> debugPathEntities = new ArrayList<>();

    BlockPos getTargetBlockPos();

    default void generateDebugPath(Path path) {
        PathAwareEntity entity = (PathAwareEntity)this;
        AdvancedNpcUtils.LOGGER.info("Placing debug path blocks");
        List<PathNode> nodes = ((PathAccessor) path).getNodes();

        Color randomColor = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));

        for (int i = 0; i < nodes.size(); i++) {
            if (i > 0) {
                PathNode node = nodes.get(i);
                Vec3d nodePos = node.getPos().add(0.4, 0, 0.4);

                // don't render last node since it is inside of the target node
                if (i != nodes.size() - 1) {
                    this.createDebugPathBlockDisplay(entity.getWorld(), nodePos, Blocks.BLUE_WOOL, new Color(0, 0, 255).getRGB(), false);
                }

                PathNode lastNode = nodes.get(i - 1);
                Vec3d lastNodePos = lastNode.getPos().add(0.4f, 0, 0.4f);

                String xDirection = "none";
                String zDirection = "none";
                String heightDirection = "none";

                if (nodePos.x - lastNodePos.x > 0) {
                    xDirection = "x";
                } else if (nodePos.x - lastNodePos.x < 0) {
                    xDirection = "-x";
                }

                if (nodePos.z - lastNodePos.z > 0) {
                    zDirection = "z";
                } else if (nodePos.z - lastNodePos.z < 0) {
                    zDirection = "-z";
                }

                if (nodePos.y - lastNodePos.y > 0) {
                    heightDirection = "y";
                } else if (nodePos.y - lastNodePos.y < 0) {
                    heightDirection = "-y";
                }

                double midX = lastNodePos.x;
                double midY = lastNodePos.y;
                double midZ = lastNodePos.z;

                // Calculate the difference between node and lastNode
                if (xDirection.equals("x") || xDirection.equals("-x")) {
                    double diffX = (nodePos.x - lastNodePos.x) / 2;
                    midX = lastNodePos.x + diffX;
                }

                if (!heightDirection.equals("none")) {
                    double diffY = (nodePos.y - lastNodePos.y) / 2;
                    midY = lastNodePos.y + diffY;
                }

                if (zDirection.equals("z") || zDirection.equals("-z")) {
                    double diffZ = (nodePos.z - lastNodePos.z) / 2;
                    midZ = lastNodePos.z + diffZ;
                }

                // The addition of another for example 0.05 depends on the size of the displayed point. Since our mid point has a size of 1 I add 0.05 to reach the mid of the nex block.
                Vec3d midPos = new Vec3d(midX, midY, midZ);

                this.createDebugPathBlockDisplay(entity.getWorld(), midPos, Blocks.GLASS, new Color(255, 255, 255).getRGB(), false);
            }
        }

        // Start block
        this.createDebugPathBlockDisplay(entity.getWorld(), entity.getPos().add(-0.2, 0, -0.2), Blocks.RED_WOOL, Color.decode("0xff0000").getRGB(), true);

        // End block
        this.createDebugPathBlockDisplay(entity.getWorld(), Vec3d.of(this.getTargetBlockPos()).add(-0.2, 0, -0.2).add(0.5, 0, 0.5), Blocks.GREEN_WOOL, Color.decode("0x00FF00").getRGB(), true);
    }

    private void createDebugPathBlockDisplay(World world, Vec3d pos, Block block, int glowColor, boolean isStartEndPoint) {
        float scale = isStartEndPoint ? 0.4f : 0.2f;
        DisplayEntity.BlockDisplayEntity blockDisplay = EntityType.BLOCK_DISPLAY.create(world, SpawnReason.COMMAND);

        blockDisplay.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);

        blockDisplay.setGlowing(true);
        blockDisplay.setGlowColorOverride(glowColor);
        blockDisplay.setBlockState(block.getDefaultState());

        blockDisplay.setTransformation(
                new AffineTransformation(
                        new Vector3f(0f, 0f, 0f),
                        new Quaternionf(0f, 0f, 0f, 1f),
                        new Vector3f(scale, scale, scale),
                        new Quaternionf(0f, 0f, 0f, 1f)
                )
        );

        blockDisplay.setBillboardMode(DisplayEntity.BillboardMode.FIXED);

        debugPathEntities.add(blockDisplay);
        world.spawnEntity(blockDisplay);
    }

    default void clearDebugPath() {
        debugPathEntities.forEach(entity -> {
            entity.remove(Entity.RemovalReason.KILLED);
        });
        debugPathEntities.clear();
    }
}
