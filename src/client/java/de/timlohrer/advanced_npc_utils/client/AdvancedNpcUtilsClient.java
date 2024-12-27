package de.timlohrer.advanced_npc_utils.client;

import de.timlohrer.advanced_npc_utils.client.renderer.AdvancedNPCRenderer;
import de.timlohrer.advanced_npc_utils.registry.EntityRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;

public class AdvancedNpcUtilsClient implements ClientModInitializer {
    public static final MinecraftClient client = MinecraftClient.getInstance();


    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(EntityRegistry.ADVANCED_NPC, AdvancedNPCRenderer::new);
        SharedConstants.isDevelopment = true;
    }
}
