package com.misterd.encasedfans.block;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.block.custom.EncasedFanBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EFBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EncasedFans.MODID);

    public static final DeferredBlock<EncasedFanBlock> ENCASED_FAN = BLOCKS.registerBlock("fan",
            EncasedFanBlock::new,
            props -> props
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops());

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
