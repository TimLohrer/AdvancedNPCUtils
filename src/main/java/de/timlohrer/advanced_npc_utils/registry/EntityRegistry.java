package de.timlohrer.advanced_npc_utils.registry;

import de.timlohrer.advanced_npc_utils.AdvancedNpcUtils;
import de.timlohrer.advanced_npc_utils.entity.TestNPCEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class EntityRegistry {
    public static final EntityType<TestNPCEntity> ADVANCED_NPC = Registry.register(
            Registries.ENTITY_TYPE,
            Identifier.of(AdvancedNpcUtils.MOD_ID, "advanced_npc"),
            EntityType.Builder.create(TestNPCEntity::new, SpawnGroup.CREATURE).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE,Identifier.of(AdvancedNpcUtils.MOD_ID,"advanced_npc")))
    );

    public static void registerEntities() {
        AdvancedNpcUtils.LOGGER.info("Registering entities");

        FabricDefaultAttributeRegistry.register(EntityRegistry.ADVANCED_NPC, TestNPCEntity.createAttributes());
    }
}
