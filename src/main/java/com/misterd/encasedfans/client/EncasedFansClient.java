package com.misterd.encasedfans.client;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.block.EFBlocks;
import com.misterd.encasedfans.blockentity.EFBlockEntities;
import com.misterd.encasedfans.client.ber.EncasedFanBlockEntityRenderer;
import com.misterd.encasedfans.client.ber.TrayBlockEntityRenderer;
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

@Mod(value = EncasedFans.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = EncasedFans.MODID, value = Dist.CLIENT)
public class EncasedFansClient {
    public EncasedFansClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EFBlockEntities.ENCASED_FAN.get(), EncasedFanBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(EFBlockEntities.TRAY.get(), TrayBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerBlockTints(RegisterColorHandlersEvent.BlockTintSources event) {
        event.register(List.of(BlockTintSources.water()), EFBlocks.WATER_CATALYST.get());
    }

    @SubscribeEvent
    public static void registerStandaloneModels(ModelEvent.RegisterStandalone event) {
        event.register(EncasedFanBlockEntityRenderer.PROPELLER_KEY,
                SimpleUnbakedStandaloneModel.simpleModelWrapper(EncasedFanBlockEntityRenderer.PROPELLER_MODEL));
    }
}