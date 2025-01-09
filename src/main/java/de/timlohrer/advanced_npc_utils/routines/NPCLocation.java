package de.timlohrer.advanced_npc_utils.routines;

import net.minecraft.util.math.BlockPos;

import java.util.List;

public record NPCLocation(BlockPos pos, List<NPCRoutineFeatures> features) {
}
