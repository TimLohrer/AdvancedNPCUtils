package de.timlohrer.advanced_npc_utils.client.model;

import de.timlohrer.advanced_npc_utils.AdvancedNpcUtils;
import de.timlohrer.advanced_npc_utils.entity.AdvancedNPC;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class AdvancedNPCModel extends GeoModel<AdvancedNPC> {
    @Override
    public Identifier getModelResource(AdvancedNPC advancedNPC, @Nullable GeoRenderer<AdvancedNPC> geoRenderer) {
        return Identifier.of(AdvancedNpcUtils.MOD_ID, "geo/advanced_npc.geo.json");
    }

    @Override
    public Identifier getTextureResource(AdvancedNPC advancedNPC, @Nullable GeoRenderer<AdvancedNPC> geoRenderer) {
        return Identifier.of(AdvancedNpcUtils.MOD_ID, "textures/entity/advanced_npc.png");
    }

    @Override
    public Identifier getAnimationResource(AdvancedNPC geoAnimatable) {
        return Identifier.of(AdvancedNpcUtils.MOD_ID, "animations/advanced_npc.animation.json");
    }
}
