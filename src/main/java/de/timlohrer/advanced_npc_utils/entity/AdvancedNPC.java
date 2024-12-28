package de.timlohrer.advanced_npc_utils.entity;

import com.google.common.collect.ImmutableSet;
import de.timlohrer.advanced_npc_utils.AdvancedNpcUtils;
import de.timlohrer.advanced_npc_utils.debugger.NPCPathDebugger;
import de.timlohrer.advanced_npc_utils.mixin.EntityNavigationAccessor;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AdvancedNPC extends PathAwareEntity implements NPCPathDebugger {
    private int idleTicks = 0;
    private boolean didCollide = false;
    public boolean isIdle = true;

    @Nullable
    private BlockPos targetPos;
    private Vec3d posBeforeCollision = this.getPos();

    @Nullable
    private Path currentPath;

    public AdvancedNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public BlockPos getTargetBlockPos() {
        return targetPos;
    }

    @Override
    public void tick() {
        this.isIdle = this.targetPos == null || this.didCollide || this.getPos().distanceTo(Vec3d.of(this.targetPos)) < 0.25 || (this.currentPath != null && this.currentPath.isFinished());
        this.targetPos = new BlockPos(0, 0, 0);

        if (this.isIdle) {
            if (this.idleTicks == 0 && this.currentPath != null) {
                AdvancedNpcUtils.LOGGER.error("Path is finished!");
                this.currentPath = null;
                this.clearDebugPath();
            }

            this.idleTicks++;

            if (this.didCollide && this.posBeforeCollision == null) {
                this.posBeforeCollision = this.getPos();
            } else if (this.didCollide && this.posBeforeCollision != null && idleTicks > 20) {
                this.didCollide = false;
                this.navigateTo(this.posBeforeCollision);
            }
        } else {
            this.idleTicks = 0;
        }

        super.tick();
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        this.idleTicks = 0;

        this.didCollide = true;
        this.isIdle = true;

        this.stopMovement();
        this.clearDebugPath();

        this.lookAtEntity(player, 90, 90);
        super.onPlayerCollision(player);
    }

    @Override
    protected void onKilledBy(@Nullable LivingEntity adversary) {
        this.clearDebugPath();
        super.onKilledBy(adversary);
    }

    public abstract float getSpeed();

    protected void navigateTo(Vec3d target) {
        Path path = ((EntityNavigationAccessor) this.navigation).invokeFindPathToAny(ImmutableSet.of(BlockPos.ofFloored(target.getX(), target.getY(), target.getZ())), 500, false, 0, 100);

        if (path == null) {
            AdvancedNpcUtils.LOGGER.error("Path is null");
            return;
        }

        if (AdvancedNpcUtils.DEBUG) {
            this.generateDebugPath(path);
        }
        this.currentPath = path;
        this.navigation.startMovingAlong(this.currentPath, this.getSpeed());
    }
}