package de.timlohrer.advanced_npc_utils.client.renderer;

import de.timlohrer.advanced_npc_utils.client.model.TestNPCModel;
import de.timlohrer.advanced_npc_utils.entity.TestNPCEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TestNPCRenderer extends GeoEntityRenderer<TestNPCEntity> {
    public TestNPCRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new TestNPCModel());
    }
}
