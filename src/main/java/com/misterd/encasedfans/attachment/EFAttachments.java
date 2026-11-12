package com.misterd.encasedfans.attachment;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.processing.ProcessingProgress;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class EFAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, EncasedFans.MODID);

    public static final Supplier<AttachmentType<ProcessingProgress>> PROCESSING_PROGRESS = ATTACHMENT_TYPES.register("processing_progress",
            () -> AttachmentType.builder(() -> ProcessingProgress.EMPTY).serialize(ProcessingProgress.CODEC).build());

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}