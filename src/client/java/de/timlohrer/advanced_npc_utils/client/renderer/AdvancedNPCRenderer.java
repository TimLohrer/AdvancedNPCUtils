package de.timlohrer.advanced_npc_utils.client.renderer;

import de.timlohrer.advanced_npc_utils.client.model.AdvancedNPCModel;
import de.timlohrer.advanced_npc_utils.entity.AdvancedNPC;
import net.minecraft.client.render.entity.EntityRendererFactory;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AdvancedNPCRenderer extends GeoEntityRenderer<AdvancedNPC> {
    public AdvancedNPCRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new AdvancedNPCModel());
    }
}
