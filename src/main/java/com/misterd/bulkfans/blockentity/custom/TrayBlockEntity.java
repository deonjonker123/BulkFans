package com.misterd.bulkfans.blockentity.custom;

import com.misterd.bulkfans.blockentity.BFBlockEntities;
import com.misterd.bulkfans.processing.BFProcessingTypes;
import com.misterd.bulkfans.processing.FanProcessing;
import com.misterd.bulkfans.processing.ProcessingType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import java.util.List;
import java.util.Optional;

public class TrayBlockEntity extends BlockEntity {
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOTS = 4;
    private static final double SURFACE_HEIGHT = 10.0 / 16.0;

    private final TrayItemHandler items = new TrayItemHandler();
    private Optional<ResourceKey<ProcessingType>> progressType = Optional.empty();
    private int progressTicks;

    public TrayBlockEntity(BlockPos pos, BlockState state) {
        super(BFBlockEntities.TRAY.get(), pos, state);
    }

    public ItemStacksResourceHandler getItemHandler() {
        return items;
    }

    public ItemStack getStack(int slot) {
        return items.getResource(slot).toStack(items.getAmountAsInt(slot));
    }

    private void setStack(int slot, ItemStack stack) {
        items.set(slot, ItemResource.of(stack), stack.getCount());
    }

    public boolean isEmpty() {
        for (int slot = 0; slot < items.size(); slot++) {
            if (items.getAmountAsInt(slot) > 0) {
                return false;
            }
        }
        return true;
    }

    public boolean canInsert(ItemStack stack) {
        ItemStack input = getStack(INPUT_SLOT);
        return input.isEmpty() || (ItemStack.isSameItemSameComponents(input, stack) && input.getCount() < input.getMaxStackSize());
    }

    public void insertFromHand(ItemStack held, Player player) {
        ItemStack input = getStack(INPUT_SLOT);
        int room = input.isEmpty() ? held.getMaxStackSize() : input.getMaxStackSize() - input.getCount();
        int moved = Math.min(room, held.getCount());
        if (moved <= 0) {
            return;
        }
        ItemStack added = held.copyWithCount(moved);
        held.consume(moved, player);
        setStack(INPUT_SLOT, input.isEmpty() ? added : input.copyWithCount(input.getCount() + moved));
    }

    public void takeAll(Player player) {
        for (int slot = items.size() - 1; slot >= 0; slot--) {
            ItemStack stack = getStack(slot);
            if (!stack.isEmpty()) {
                setStack(slot, ItemStack.EMPTY);
                player.getInventory().placeItemBackInInventory(stack);
            }
        }
    }

    public void process(ServerLevel level, Holder<ProcessingType> type, int elapsed) {
        ItemStack input = getStack(INPUT_SLOT);
        Optional<ResourceKey<ProcessingType>> key = type.unwrapKey();
        if (input.isEmpty() || key.isEmpty() || !FanProcessing.canProcess(level, input, type)) {
            resetProgress();
            return;
        }

        int ticks = progressType.equals(key) ? progressTicks + elapsed : elapsed;
        if (ticks < FanProcessing.requiredTicks(input.getCount())) {
            progressType = key;
            progressTicks = ticks;
            setChanged();
            FanProcessing.spawnProcessingParticles(level, worldPosition.getX() + 0.5, worldPosition.getY() + SURFACE_HEIGHT, worldPosition.getZ() + 0.5);
            return;
        }

        List<ItemStack> results = FanProcessing.process(level, input, type);
        setStack(INPUT_SLOT, ItemStack.EMPTY);
        for (ItemStack result : results) {
            ItemStack leftover = storeOutput(result);
            if (!leftover.isEmpty()) {
                popOut(level, leftover);
            }
        }
    }

    private ItemStack storeOutput(ItemStack result) {
        ItemStack remaining = result.copy();
        for (int slot = INPUT_SLOT + 1; slot <= OUTPUT_SLOTS && !remaining.isEmpty(); slot++) {
            ItemStack existing = getStack(slot);
            if (existing.isEmpty()) {
                setStack(slot, remaining);
                return ItemStack.EMPTY;
            }
            if (ItemStack.isSameItemSameComponents(existing, remaining)) {
                int moved = Math.min(existing.getMaxStackSize() - existing.getCount(), remaining.getCount());
                if (moved > 0) {
                    setStack(slot, existing.copyWithCount(existing.getCount() + moved));
                    remaining.shrink(moved);
                }
            }
        }
        return remaining;
    }

    private void popOut(ServerLevel level, ItemStack stack) {
        ItemEntity entity = new ItemEntity(level, worldPosition.getX() + 0.5, worldPosition.getY() + SURFACE_HEIGHT + 0.1, worldPosition.getZ() + 0.5, stack);
        entity.setDeltaMovement(0, 0.2, 0);
        level.addFreshEntity(entity);
    }

    private void resetProgress() {
        if (progressType.isPresent() || progressTicks != 0) {
            progressType = Optional.empty();
            progressTicks = 0;
            setChanged();
        }
    }

    private void contentsChanged(int slot) {
        if (slot == INPUT_SLOT && items.getAmountAsInt(INPUT_SLOT) == 0) {
            progressType = Optional.empty();
            progressTicks = 0;
        }
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        items.serialize(output.child("Inventory"));
        output.storeNullable("ProgressType", ResourceKey.codec(BFProcessingTypes.REGISTRY), progressType.orElse(null));
        output.putInt("ProgressTicks", progressTicks);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("Inventory").ifPresent(items::deserialize);
        progressType = input.read("ProgressType", ResourceKey.codec(BFProcessingTypes.REGISTRY));
        progressTicks = input.getIntOr("ProgressTicks", 0);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveCustomOnly(registries);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            NonNullList<ItemStack> contents = NonNullList.create();
            for (int slot = 0; slot < items.size(); slot++) {
                contents.add(getStack(slot));
            }
            Containers.dropContents(level, pos, contents);
        }
    }

    private class TrayItemHandler extends ItemStacksResourceHandler {
        TrayItemHandler() {
            super(1 + OUTPUT_SLOTS);
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return index == INPUT_SLOT;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            return index == INPUT_SLOT ? 0 : super.extract(index, resource, amount, transaction);
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            contentsChanged(index);
        }
    }
}