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
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public interface NPCPathDebugger {
    List<DisplayEntity.BlockDisplayEntity> debugPathEntities = new ArrayList<>();

    BlockPos getTargetBlockPos();

    default void generateDebugPath(Path path) {
        PathAwareEntity entity = (PathAwareEntity) this;
        AdvancedNpcUtils.LOGGER.info("Placing debug path blocks");
        List<Vec3d> nodes = ((PathAccessor) path).getNodes().stream().map(PathNode::getPos).toList();
        List<Vec3d> corners = new ArrayList<>();

        corners.add(nodes.getFirst());
        corners.add(nodes.getLast());

        // All nodes except start and end
        for (int i = 1; i < nodes.size() - 1; i++) {
            Vec3d nodePos = nodes.get(i).add(0.4, 0, 0.4);
            Vec3d lastCornerPos = corners.getLast();
            Vec3d nextNodePos = nodes.get(i + 1).add(0.4, 0, 0.4);

            HashMap<String, String> diffLast = calcPathNodePosDiff(nodePos, lastCornerPos);
            HashMap<String, String> diffNext = calcPathNodePosDiff(nextNodePos, nodePos);

            Vector3f scaleModifier = new Vector3f(0f, 0f, 0f);
            Color glowColor = Color.decode("0xF0F0FF");
            Block block = Blocks.LIGHT_BLUE_CONCRETE;

            if (!Objects.equals(diffLast, diffNext)) {
                corners.add(nodePos);
                scaleModifier.add(0.05f, 0.05f, 0.05f);
                glowColor = Color.decode("0x7070FF");
                block = Blocks.BLUE_CONCRETE;
            }
            createDebugPathBlockDisplay(entity.getWorld(), nodePos, scaleModifier, new Vec2f(0.0f, 0.0f), block, glowColor.getRGB(), false);
        }

        // Start block
        this.createDebugPathBlockDisplay(entity.getWorld(), entity.getPos().add(-0.2, 0, -0.2), new Vector3f(0, 0, 0), new Vec2f(0.0f, 0.0f), Blocks.RED_WOOL, Color.decode("0xFF0000").getRGB(), true);

        // End block
        this.createDebugPathBlockDisplay(entity.getWorld(), Vec3d.of(this.getTargetBlockPos()).add(-0.2, 0, -0.2).add(0.5, 0, 0.5), new Vector3f(0, 0, 0), new Vec2f(0.0f, 0.0f), Blocks.GREEN_WOOL, Color.decode("0x00FF00").getRGB(), true);
    }

    private HashMap<String, String> calcPathNodePosDiff(Vec3d nodePos, Vec3d lastNodePos) {
        String xDirection = "none";
        String yDirection = "none";
        String zDirection = "none";

        if (nodePos.x > lastNodePos.x) {
            xDirection = "-x";
        } else if (nodePos.x < lastNodePos.x) {
            xDirection = "x";
        }

        if (nodePos.z > lastNodePos.z) {
            zDirection = "-z";
        } else if (nodePos.z < lastNodePos.z) {
            zDirection = "z";
        }

        if (nodePos.y > lastNodePos.y) {
            yDirection = "-y";
        } else if (nodePos.y < lastNodePos.y) {
            yDirection = "y";
        }

        HashMap<String, String> returnValues = new HashMap<>();
        returnValues.put("xDirection", xDirection);
        returnValues.put("yDirection", yDirection);
        returnValues.put("zDirection", zDirection);

        return returnValues;
    }

    private float getDebugPathBlockDisplayBaseScale(boolean isStartEndPoint) {
        return isStartEndPoint ? 0.4f : 0.2f;
    }

    private void createDebugPathBlockDisplay(World world, Vec3d pos, Vector3f scaleModifier, Vec2f rotation, Block block, int glowColor, boolean isStartEndPoint) {
        float scale = this.getDebugPathBlockDisplayBaseScale(isStartEndPoint);
        DisplayEntity.BlockDisplayEntity blockDisplay = EntityType.BLOCK_DISPLAY.create(world, SpawnReason.COMMAND);

        blockDisplay.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), rotation.x, rotation.y);

        blockDisplay.setGlowing(true);
        blockDisplay.setGlowColorOverride(glowColor);
        blockDisplay.setBlockState(block.getDefaultState());

        blockDisplay.setTransformation(
                new AffineTransformation(
                        new Vector3f(0f, 0f, 0f),
                        new Quaternionf(0f, 0f, 0f, 1f),
                        new Vector3f(scale, scale, scale).add(scaleModifier),
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
