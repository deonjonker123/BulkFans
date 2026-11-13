package com.misterd.encasedfans.item.custom;

import com.misterd.encasedfans.block.EFBlocks;
import com.misterd.encasedfans.block.custom.EncasedFanBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

public class SpannerItem extends Item {
    private static final Direction[] FACINGS = Direction.values();

    public SpannerItem(Properties properties) {
        super(properties);
    }

    private static Set<Block> pickupBlocks() {
        return Set.of(
                EFBlocks.ENCASED_FAN.get(),
                EFBlocks.TRAY.get(),
                EFBlocks.FIRE_CATALYST.get(),
                EFBlocks.SOUL_FIRE_CATALYST.get(),
                EFBlocks.LAVA_CATALYST.get(),
                EFBlocks.WATER_CATALYST.get());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        Player player = context.getPlayer();

        if (player != null && player.isSecondaryUseActive()) {
            if (!pickupBlocks().contains(state.getBlock())) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                level.removeBlock(pos, false);
                player.getInventory().placeItemBackInInventory(new ItemStack(state.getBlock()));
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1.0F);
            }
            return InteractionResult.SUCCESS;
        }

        if (!(state.getBlock() instanceof EncasedFanBlock)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            Direction current = state.getValue(EncasedFanBlock.FACING);
            Direction next = FACINGS[(current.ordinal() + 1) % FACINGS.length];
            level.setBlock(pos, state.setValue(EncasedFanBlock.FACING, next), Block.UPDATE_ALL);
            level.playSound(null, pos, state.getSoundType(level, pos, player).getPlaceSound(), SoundSource.BLOCKS, 0.5F, 1.2F);
        }
        return InteractionResult.SUCCESS;
    }
}