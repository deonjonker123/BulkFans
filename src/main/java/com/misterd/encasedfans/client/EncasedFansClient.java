package com.misterd.encasedfans.client;

import com.misterd.encasedfans.EncasedFans;
import com.misterd.encasedfans.blockentity.EFBlockEntities;
import com.misterd.encasedfans.client.ber.EncasedFanBlockEntityRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.SimpleUnbakedStandaloneModel;

@EventBusSubscriber(modid = EncasedFans.MODID, value = Dist.CLIENT)
public class EncasedFansClient {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(EFBlockEntities.ENCASED_FAN.get(), EncasedFanBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerStandaloneModels(ModelEvent.RegisterStandalone event) {
        event.register(EncasedFanBlockEntityRenderer.PROPELLER_KEY,
                SimpleUnbakedStandaloneModel.simpleModelWrapper(EncasedFanBlockEntityRenderer.PROPELLER_MODEL));
    }
}
