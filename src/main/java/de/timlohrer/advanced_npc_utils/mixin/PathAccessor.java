package de.timlohrer.advanced_npc_utils.mixin;

import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Path.class)
public interface PathAccessor {
    @Accessor("nodes")
    List<PathNode> getNodes();
}
