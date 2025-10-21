package me.preceding.nightvision.mixin;

import me.preceding.nightvision.RemoveNightvisionClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListHeaderS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onPlayerListHeader", at = @At("HEAD"))
    public void onPlayerListHeader(PlayerListHeaderS2CPacket packet, CallbackInfo ci) {
        final String header = packet.header().getString();
        final String footer = packet.footer().getString();

        RemoveNightvisionClient.getInstance().getMinehutManager().handleTabUpdate(header, footer);
    }

    @Inject(method = "onEntityStatusEffect", at = @At("HEAD"))
    public void onEntityStatusEffect(EntityStatusEffectS2CPacket packet, CallbackInfo ci) {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        final int entityId = packet.getEntityId();

        if (player == null || player.getId() != entityId) {
            return;
        }

        MinecraftClient.getInstance().send(() -> {
            if (RemoveNightvisionClient.getInstance().getMinehutManager().isOnMinehutLobby()
                    && RemoveNightvisionClient.getInstance().getConfigManager().getConfig().isDisableNightvision()
                    && player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
                player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }
        });

    }

}
