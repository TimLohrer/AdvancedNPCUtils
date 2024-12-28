package de.timlohrer.advanced_npc_utils.client.model;

import de.timlohrer.advanced_npc_utils.AdvancedNpcUtils;
import de.timlohrer.advanced_npc_utils.entity.TestNPCEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class TestNPCModel extends GeoModel<TestNPCEntity> {
    @Override
    public Identifier getModelResource(TestNPCEntity testNPCEntity, @Nullable GeoRenderer<TestNPCEntity> geoRenderer) {
        return Identifier.of(AdvancedNpcUtils.MOD_ID, "geo/advanced_npc.geo.json");
    }

    @Override
    public Identifier getTextureResource(TestNPCEntity testNPCEntity, @Nullable GeoRenderer<TestNPCEntity> geoRenderer) {
        return Identifier.of(AdvancedNpcUtils.MOD_ID, "textures/entity/advanced_npc.png");
    }

    @Override
    public Identifier getAnimationResource(TestNPCEntity testNPCEntity) {
        return Identifier.of(AdvancedNpcUtils.MOD_ID, "animations/advanced_npc.animation.json");
    }
}
