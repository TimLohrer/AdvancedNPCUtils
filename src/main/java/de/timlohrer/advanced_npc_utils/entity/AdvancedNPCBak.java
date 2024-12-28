package de.timlohrer.advanced_npc_utils.entity;

import com.google.common.collect.ImmutableSet;
import de.timlohrer.advanced_npc_utils.AdvancedNpcUtils;
import de.timlohrer.advanced_npc_utils.mixin.EntityNavigationAccessor;
import de.timlohrer.advanced_npc_utils.mixin.PathAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AdvancedNPCBak extends PathAwareEntity implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int idleTicks = 0;
    private boolean didCollide = false;
    private float speed = 0.35F;
    private Vec3d lastPos = this.getPos();
    public boolean isIdle = true;
    @Nullable
    private Vec3d targetPos;
    @Nullable
    private Path currentPath;
    private List<DisplayEntity.BlockDisplayEntity> pathEntities = new ArrayList<>();

    private Vec3d posBeforeCollision = this.getPos();

    public AdvancedNPCBak(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void tick() {
//        AdvancedNpcUtils.LOGGER.info("Advanced NPC is ticking " + this.idleTicks);
        this.isIdle = this.targetPos == null || this.didCollide || this.getPos().distanceTo(this.targetPos) < 0.25 || (this.currentPath != null && this.currentPath.isFinished());
        this.targetPos = new Vec3d(0.5, 0, 0.5);

        if (this.isIdle) {
            if (this.idleTicks == 0 && this.currentPath != null && !this.pathEntities.isEmpty()) {
                AdvancedNpcUtils.LOGGER.error("Path is finished!");
                this.clearDebugPath();
                this.currentPath = null;
            }

            this.idleTicks++;

            // Handle collision
            if (this.didCollide && this.posBeforeCollision == null) {
                this.posBeforeCollision = this.getPos();
            } else if (this.didCollide && this.posBeforeCollision != null && idleTicks > 60) {
                this.didCollide = false;
                this.goTo(this.posBeforeCollision);
            }
        } else {
            this.idleTicks = 0;
        }


        super.tick();
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
//        AdvancedNpcUtils.LOGGER.info("Advanced NPC is colliding with player");
        this.didCollide = true;
        this.idleTicks = 0;
        this.isIdle = true;
        this.stopMovement();
        this.lookAtEntity(player, 90, 90);
        super.onPlayerCollision(player);
    }

    @Override
    protected void onKilledBy(@Nullable LivingEntity adversary) {
        this.clearDebugPath();
        super.onKilledBy(adversary);
    }

    private void goTo(Vec3d target) {
        clearDebugPath();

        /* -- range = 500 because of entity not being able to pathfind further correctly when the limit is reached. (Our bad + range resets when new node is set. If you set a node thats greater then 500 blocks go fuck yourself.) -- */
        Path path = ((EntityNavigationAccessor) this.navigation).invokeFindPathToAny(ImmutableSet.of(BlockPos.ofFloored(target.getX(), target.getY(), target.getZ())), 500, false, 0, 100);

        if (path == null) {
            AdvancedNpcUtils.LOGGER.error("Path is null");
            return;
        }

        if (AdvancedNpcUtils.DEBUG) {
            AdvancedNpcUtils.LOGGER.info("Placing debug path blocks");
            List<PathNode> nodes = ((PathAccessor) path).getNodes();

            Color randomColor = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));

            for (int i = 0; i < nodes.size(); i++) {
                if (i > 0) {
                    PathNode node = nodes.get(i);
                    Vec3d nodePos = node.getPos().add(0.4, 0, 0.4);
                    this.createDebugPathBlockDisplay(nodePos, Blocks.BLUE_TERRACOTTA, randomColor.getRGB(), false);

                    PathNode lastNode = nodes.get(i - 1);
                    Vec3d lastNodePos = lastNode.getPos().add(0.4f, 0, 0.4f);

                    String xDirection = "none";
                    String zDirection = "none";
                    String heightDirection = "none";

                    if(nodePos.x - lastNodePos.x > 0) {
                        xDirection = "x";
                    } else if(nodePos.x - lastNodePos.x < 0) {
                        xDirection = "-x";
                    }

                    if(nodePos.z - lastNodePos.z > 0) {
                        zDirection = "z";
                    } else if(nodePos.z - lastNodePos.z < 0) {
                        zDirection = "-z";
                    }

                    if(nodePos.y - lastNodePos.y > 0) {
                        heightDirection = "y";
                    } else if(nodePos.y - lastNodePos.y < 0) {
                        heightDirection = "-y";
                    }

                    double midX = lastNodePos.x;
                    double midY = lastNodePos.y;
                    double midZ = lastNodePos.z;

                    // Calculate the difference between node and lastNode
                    if(xDirection.equals("x") || xDirection.equals("-x")) {
                        double diffX = (nodePos.x - lastNodePos.x) / 2;
                        midX = lastNodePos.x + diffX;
                    }

                    if(!heightDirection.equals("none")) {
                        double diffY = (nodePos.y - lastNodePos.y) / 2;
                        midY = lastNodePos.y + diffY;
                    }

                    if(zDirection.equals("z") || zDirection.equals("-z")) {
                        double diffZ = (nodePos.z - lastNodePos.z) / 2;
                        midZ = lastNodePos.z + diffZ;
                    }

                    // The addition of another for example 0.05 depends on the size of the displayed point. Since our mid point has a size of 1 I add 0.05 to reach the mid of the nex block.
                    Vec3d midPos = new Vec3d(midX, midY, midZ);

                    this.createDebugPathBlockDisplay(midPos, Blocks.GLASS, Color.decode("0xffffff").getRGB(), false);
                }
            }

            // Start block
            this.createDebugPathBlockDisplay(this.getPos().add(-0.2, 0, -0.2), Blocks.RED_TERRACOTTA, Color.decode("0xFF0000").getRGB(), true);

            // End block
            this.createDebugPathBlockDisplay(this.targetPos.add(-0.2, 0, -0.2), Blocks.GREEN_TERRACOTTA, Color.decode("0x00FF00").getRGB(), true);
        }

        this.currentPath = path;
//        this.navigation.startMovingAlong(this.currentPath, this.speed);
    }

    private void createDebugPathBlockDisplay(Vec3d pos, Block block, int glowColor, boolean isStartEndPoint) {
        float scale = isStartEndPoint ? 0.4f : 0.2f;
        DisplayEntity.BlockDisplayEntity blockDisplay = EntityType.BLOCK_DISPLAY.create(this.getWorld(), SpawnReason.COMMAND);

        blockDisplay.refreshPositionAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0.0F, 0.0F);

//        new Runnable() {
//            @Override
//            public void run() {
//                blockDisplay.rotate((float) (Math.random() * 360), (float) (Math.random() * 360));
//            }
//        }.run();

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

        this.getWorld().spawnEntity(blockDisplay);
        this.pathEntities.add(blockDisplay);
    }

    private void clearDebugPath() {
        this.pathEntities.forEach(entity -> {
            entity.remove(RemovalReason.KILLED);
        });
        this.pathEntities.clear();
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 99999999);
    }

    private PlayState predicate(AnimationState<?> animationState) {
//        AdvancedNpcUtils.LOGGER.info("Walking predicate " + this.navigation.isIdle());
        if (!this.navigation.isIdle()) {
            animationState.getController().setAnimation(RawAnimation.begin().thenLoop("walk"));
        } else {
            animationState.getController().setAnimation(DefaultAnimations.IDLE);
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}