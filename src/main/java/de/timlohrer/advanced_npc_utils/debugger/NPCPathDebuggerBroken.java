package de.timlohrer.advanced_npc_utils.debugger;

import de.timlohrer.advanced_npc_utils.AdvancedNpcUtils;
import de.timlohrer.advanced_npc_utils.mixin.PathAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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

public interface NPCPathDebuggerBroken {
    List<DisplayEntity.BlockDisplayEntity> debugPathEntities = new ArrayList<>();

    BlockPos getTargetBlockPos();

    default void generateDebugPath(Path path) {
        PathAwareEntity entity = (PathAwareEntity) this;
        AdvancedNpcUtils.LOGGER.info("Placing debug path blocks");
        List<PathNode> nodes = ((PathAccessor) path).getNodes();

        Color randomColor = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));

        List<Vec3d> corners = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) {
            Vec3d nodePos = nodes.get(i).getPos().add(0.4, 0, 0.4);

            if (i == 0) {
                corners.add(nodePos);
            } else if (i == nodes.size() - 1) {
                corners.add(nodePos);
            } else {
                Vec3d lastCornerPos = corners.getLast();
                Vec3d nextNodePos = nodes.get(i + 1).getPos().add(0.4, 0, 0.4);

                HashMap<String, String> diffLast = calcPathNodePosDiff(nodePos, lastCornerPos); // +x none +z
                HashMap<String, String> diffNext = calcPathNodePosDiff(nextNodePos, nodePos); // +x none +z

                if (!Objects.equals(diffLast, diffNext)) {
                    corners.add(nodePos);
                }
            }
        }

        for (int i = 0; i < corners.size(); i++) {
            if (i == 0) continue;

            Vec3d corner = corners.get(i);
            Vec3d lastCornerPos = corners.get(i - 1);

            HashMap<String, String> diffLast = calcPathNodePosDiff(corner, lastCornerPos);

            float xDiff = (float) Math.abs(corner.x - lastCornerPos.x);
            float yDiff = (float) Math.abs(corner.y - lastCornerPos.y);
            float zDiff = (float) Math.abs(corner.z - lastCornerPos.z);

            String xDirection = diffLast.get("xDirection");
            String yDirection = diffLast.get("yDirection");
            String zDirection = diffLast.get("zDirection");

            Vector3f scaleModifier = new Vector3f(0.0f, 0.0f, 0.0f);
            Vec2f rotationModifier = new Vec2f(0.0f, 0.0f);

            float lineWidth = getDebugPathBlockDisplayBaseScale(false);

            // Draw Corner
            float cornerScale = 2f;
            Vector3f cornerScaleModifier = new Vector3f(lineWidth, lineWidth, lineWidth).mul(cornerScale);
            this.createDebugPathBlockDisplay(entity.getWorld(), corner.add(-lineWidth / 2, 0, -lineWidth / 2), cornerScaleModifier, rotationModifier, Blocks.LIGHT_BLUE_CONCRETE, new Color(100, 100, 255).getRGB(), false);

            System.out.println("xDiff" + xDiff * -1);
            System.out.println("zDiff" + zDiff * -1);

            // Draw Line
            if ((xDirection.equals("none") || zDirection.equals("none"))
                    && yDirection.equals("none")) {
                switch ((xDirection + zDirection).replace("none", "")) {
                    case "x": {
                        scaleModifier.add(xDiff, 0, 0);
                    }
                    case "-x": {
                        scaleModifier.add(xDiff * -1, 0, 0);
                    }
                    case "z": {
                        scaleModifier.add(0, 0, zDiff);
                    }
                    case "-z": {
                        scaleModifier.add(0, 0, zDiff * -1);
                    }

                }
            } else if (!(xDirection.equals("none")) && !(zDirection.equals("none"))) {

                float diagonalLength = (float) Math.sqrt(xDiff * xDiff + zDiff * zDiff);

                // IDEE: Fix weird translations by scaling z axis and altering rotation
                switch (xDirection + zDirection) {
                    case "xz": {
                        corner = corner.add(1.5 * lineWidth, 0, 0.75 * lineWidth);
                        scaleModifier.add(diagonalLength, 0, 0);
                        rotationModifier = new Vec2f(45.0f, 0.0f);
                        break;
                    }
                    case "x-z": {
                        corner = corner.add(0.25 * lineWidth, 0, lineWidth);
                        scaleModifier.add(diagonalLength, 0, 0);
                        rotationModifier = new Vec2f(-45.0f, 0.0f);
                        break;
                    }
                    case "-xz": {
                        corner = corner.add(0.75 * lineWidth, 0, 0.5 * lineWidth);
                        scaleModifier.add(0, 0, diagonalLength);
                        rotationModifier = new Vec2f(45.0f, 0.0f);
                        break;
                    }
                    case "-x-z": {
                        corner = corner.add(1.25 * lineWidth, 0, 0.5 * lineWidth);
                        scaleModifier.add(0, 0, diagonalLength);
                        rotationModifier = new Vec2f(135.0f, 0.0f);
                        break;
                    }
                }

                switch (yDirection) {
                    case "y":
                        rotationModifier = new Vec2f(0.0f, 45.0f);
                        break;
                    case "-y":
                        rotationModifier = new Vec2f(0.0f, -45.0f);
                        break;
                }
            } else if (!xDirection.equals("none") || !zDirection.equals("none")) {
                // Check if step down either x and y or z and y
                float diagonalLengthX = (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
                float diagonalLengthZ = (float) Math.sqrt(zDiff * zDiff + yDiff * yDiff);

                switch ((xDirection + zDirection).replace("none", "")) {
                    case "x":
                        scaleModifier.add(diagonalLengthX, 0, 0);
                        break;
                    case "-x":
                        scaleModifier.add(-diagonalLengthX + lineWidth, 0, 0);
                        break;
                    case "z":
                        scaleModifier.add(0, 0, diagonalLengthZ);
                        break;
                    case "-z":
                        scaleModifier.add(0, 0, -diagonalLengthZ + lineWidth);
                        break;
                }

                switch (yDirection) {
                    case "y":
                        rotationModifier = new Vec2f(0.0f, 45.0f);
                        break;
                    case "-y":
                        rotationModifier = new Vec2f(0.0f, -45.0f);
                        break;
                }
            }

            this.createDebugPathBlockDisplay(entity.getWorld(), corner, scaleModifier, rotationModifier, Blocks.WHITE_CONCRETE, randomColor.getRGB(), false);
        }
        ;

        // Start block
        this.createDebugPathBlockDisplay(entity.getWorld(), entity.getPos().add(-0.2, 0, -0.2), new Vector3f(0, 0, 0), new Vec2f(0.0f, 0.0f), Blocks.RED_WOOL, Color.decode("0xff0000").getRGB(), true);

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
        return isStartEndPoint ? 0.4f : 0.1f;
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
//        debugPathEntities.forEach(entity -> {
//            entity.remove(Entity.RemovalReason.KILLED);
//        });
//        debugPathEntities.clear();
    }
}
