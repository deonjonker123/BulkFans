package com.misterd.encasedfans.blockentity.custom;

import com.misterd.encasedfans.blockentity.EFBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class EncasedFanBlockEntity extends BlockEntity {
    public EncasedFanBlockEntity(BlockPos pos, BlockState state) {
        super(EFBlockEntities.ENCASED_FAN.get(), pos, state);
    }
}
