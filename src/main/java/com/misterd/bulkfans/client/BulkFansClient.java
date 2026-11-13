package com.misterd.bulkfans.client;

import com.misterd.bulkfans.BulkFans;
import com.misterd.bulkfans.block.BFBlocks;
import com.misterd.bulkfans.blockentity.BFBlockEntities;
import com.misterd.bulkfans.client.ber.IndustrialFanBlockEntityRenderer;
import com.misterd.bulkfans.client.ber.TrayBlockEntityRenderer;
import net.minecraft.client.color.block.BlockTintSources;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;

import java.util.List;

@Mod(value = BulkFans.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = BulkFans.MODID, value = Dist.CLIENT)
public class BulkFansClient {
    public BulkFansClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BFBlockEntities.INDUSTRIAL_FAN.get(), IndustrialFanBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(BFBlockEntities.TRAY.get(), TrayBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerBlockTints(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(BlockTintSources.water()), BFBlocks.WATER_CATALYST.get());
    }

    @SubscribeEvent
    public static void registerStandaloneModels(ModelEvent.RegisterStandalone event) {
        event.register(IndustrialFanBlockEntityRenderer.PROPELLER_KEY,
                SimpleUnbakedStandaloneModel.simpleModelWrapper(IndustrialFanBlockEntityRenderer.PROPELLER_MODEL));
    }
}