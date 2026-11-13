package com.misterd.encasedfans.block;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.block.custom.EncasedFanBlock;
import com.misterd.encasedfans.block.custom.TrayBlock;
import net.minecraft.world.level.block.Block;
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

    public static final DeferredBlock<Block> FIRE_CATALYST = catalyst("fire_catalyst", 15);
    public static final DeferredBlock<Block> SOUL_FIRE_CATALYST = catalyst("soul_fire_catalyst", 10);
    public static final DeferredBlock<Block> LAVA_CATALYST = catalyst("lava_catalyst", 15);
    public static final DeferredBlock<Block> WATER_CATALYST = catalyst("water_catalyst", 0);

    public static final DeferredBlock<TrayBlock> TRAY = BLOCKS.registerBlock("tray",
            TrayBlock::new,
            props -> props
                    .mapColor(MapColor.METAL)
                    .strength(3.5F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops());

    private static DeferredBlock<Block> catalyst(String name, int light) {
        return BLOCKS.registerBlock(name,
                Block::new,
                props -> props
                        .mapColor(MapColor.METAL)
                        .strength(3.5F)
                        .sound(SoundType.METAL)
                        .requiresCorrectToolForDrops()
                        .lightLevel(state -> light)
                        .noOcclusion());
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}