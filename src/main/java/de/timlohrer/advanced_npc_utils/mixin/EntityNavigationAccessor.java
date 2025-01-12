package de.timlohrer.advanced_npc_utils.mixin;

import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Set;

@Mixin(EntityNavigation.class)
public interface EntityNavigationAccessor {
    @Invoker("findPathToAny")
    Path invokeFindPathToAny(Set<BlockPos> positions, int range, boolean useHeadPos, int distance, float followRange);
}
