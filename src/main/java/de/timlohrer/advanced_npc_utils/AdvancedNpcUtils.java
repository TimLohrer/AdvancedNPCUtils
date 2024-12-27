package de.timlohrer.advanced_npc_utils;

import de.timlohrer.advanced_npc_utils.registry.EntityRegistry;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class AdvancedNpcUtils implements ModInitializer {
    public static final String MOD_ID = "advanced_npc_utils";
    public static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(MOD_ID);
    public static boolean DEBUG = true;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Advanced NPC Utils");

        // Register entities
        EntityRegistry.registerEntities();
    }
}
