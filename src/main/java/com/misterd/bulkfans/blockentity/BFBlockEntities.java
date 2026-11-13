package com.misterd.bulkfans.blockentity;

import com.misterd.bulkfans.BulkFans;
import com.misterd.bulkfans.block.BFBlocks;
import com.misterd.bulkfans.blockentity.custom.IndustrialFanBlockEntity;
import com.misterd.bulkfans.blockentity.custom.TrayBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BFBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, BulkFans.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<IndustrialFanBlockEntity>> INDUSTRIAL_FAN =
            BLOCK_ENTITIES.register("industrial_fan",
                    () -> new BlockEntityType<>(IndustrialFanBlockEntity::new, BFBlocks.INDUSTRIAL_FAN.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrayBlockEntity>> TRAY =
            BLOCK_ENTITIES.register("tray",
                    () -> new BlockEntityType<>(TrayBlockEntity::new, BFBlocks.TRAY.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
        eventBus.addListener(BFBlockEntities::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.Item.BLOCK, TRAY.get(), (tray, side) -> tray.getItemHandler());
    }
}