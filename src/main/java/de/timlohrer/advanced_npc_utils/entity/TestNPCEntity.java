package de.timlohrer.advanced_npc_utils.entity;

import de.timlohrer.advanced_npc_utils.routines.NPCLocation;
import de.timlohrer.advanced_npc_utils.routines.NPCRoutine;
import de.timlohrer.advanced_npc_utils.routines.NPCRoutineType;
import de.timlohrer.advanced_npc_utils.routines.NPCRoutineFeatures;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.time.Duration;
import java.util.List;

public class TestNPCEntity extends AdvancedNPC implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    List<NPCLocation> locations = List.of(
            new NPCLocation(new BlockPos(10, 0, 10), List.of()),
            new NPCLocation(new BlockPos(0, 0, 10), List.of(new NPCRoutineFeatures.Pause(Duration.ofSeconds(5)))),
            new NPCLocation(new BlockPos(5, 0, 3), List.of(new NPCRoutineFeatures.Custom((npc) -> {
                npc.getEntityWorld().setBlockState(npc.getBlockPos(), Blocks.ACACIA_LOG.getDefaultState());
                return null;
            })))
    );
    List<NPCRoutine> routines = List.of(
            new NPCRoutine(locations, NPCRoutineType.FIXED)
    );

    public TestNPCEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public List<NPCRoutine> getRoutines() { return routines; }

    @Override
    public float getSpeed() { return 0.35f; }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return PathAwareEntity.createMobAttributes().add(EntityAttributes.MAX_HEALTH, 99999999);
    }

    private PlayState predicate(AnimationState<?> animationState) {
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
