package de.timlohrer.advanced_npc_utils.entity;

import com.google.common.collect.ImmutableSet;
import de.timlohrer.advanced_npc_utils.AdvancedNpcUtils;
import de.timlohrer.advanced_npc_utils.debugger.NPCPathDebugger;
import de.timlohrer.advanced_npc_utils.mixin.EntityNavigationAccessor;
import de.timlohrer.advanced_npc_utils.routines.NPCRoutine;
import de.timlohrer.advanced_npc_utils.routines.NPCRoutineFeatures;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class AdvancedNPC extends PathAwareEntity implements NPCPathDebugger {
    private int idleTicks = 0;
    private final List<NPCRoutine> routines = this.getRoutines();

    @Nullable
    private NPCRoutine currentRoutine;
    @Nullable
    private Path currentPath;

    public AdvancedNPC(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public BlockPos getTargetBlockPos() {
        if (this.currentRoutine == null) return null;
        return this.currentRoutine.getCurrentLocation().pos();
    }

    @Override
    public void tick() {
        if (this.routines.isEmpty()) return;
        if (this.idleTicks > 0) {
            if (this.currentRoutine == null) {
                this.currentRoutine = this.routines.getFirst();
//                this.navigateTo();
            } else if (this.currentPath.isFinished()) {
                if (!this.currentRoutine.getCurrentLocation().features().isEmpty() && this.idleTicks > 0) {
                    this.currentRoutine.getCurrentLocation().features().forEach(feature -> {
                        if (feature instanceof NPCRoutineFeatures.Pause) {
                            this.idleTicks = -((NPCRoutineFeatures.Pause) feature).pauseTicks;
                        } else if (feature instanceof NPCRoutineFeatures.Custom) {
                            ((NPCRoutineFeatures.Custom) feature).action.apply(this);
                        }
                    });
                } else {
                    idleTicks++;
                }
            }

//            if (this.)
        }
        super.tick();
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        super.onPlayerCollision(player);
    }

    @Override
    protected void onKilledBy(@Nullable LivingEntity adversary) {
        this.clearDebugPath();
        super.onKilledBy(adversary);
    }

    /**
     * @return The routines of the NPC
     */
    public abstract List<NPCRoutine> getRoutines();

    /**
     * @return The speed of the NPC
     */
    public abstract float getSpeed();

    /**
     * @param pos The position to navigate to
     */
    protected void navigateTo(BlockPos pos) {
        Path path = ((EntityNavigationAccessor) this.navigation).invokeFindPathToAny(ImmutableSet.of(pos), 500, false, 0, 100);

        if (path == null) {
            AdvancedNpcUtils.LOGGER.error("Path is null");
            return;
        }

        if (AdvancedNpcUtils.DEBUG) {
            this.generateDebugPath(path);
        }
        this.currentPath = path;
        this.idleTicks = 0;
        this.navigation.startMovingAlong(this.currentPath, this.getSpeed());
    }

    /**
     * @return The amount of ticks the NPC has been idle for
     */
    public int getIdleTicks() {
        return idleTicks;
    }
}