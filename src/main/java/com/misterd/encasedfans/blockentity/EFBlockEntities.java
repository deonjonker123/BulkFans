package com.misterd.encasedfans.blockentity;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.block.EFBlocks;
import com.misterd.encasedfans.blockentity.custom.EncasedFanBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EFBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EncasedFans.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EncasedFanBlockEntity>> ENCASED_FAN =
            BLOCK_ENTITIES.register("fan",
                    () -> new BlockEntityType<>(EncasedFanBlockEntity::new, EFBlocks.ENCASED_FAN.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
