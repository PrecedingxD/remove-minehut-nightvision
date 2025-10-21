package me.preceding.nightvision;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import me.preceding.nightvision.manager.ConfigManager;
import me.preceding.nightvision.config.NightvisionConfig;
import me.preceding.nightvision.manager.MinehutManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.logging.Logger;

public class RemoveNightvisionClient implements ClientModInitializer {

    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .setPrettyPrinting()
            .create();
    public static final Logger LOGGER = Logger.getLogger(RemoveNightvisionClient.class.getSimpleName());

    private static RemoveNightvisionClient instance;

    private MinehutManager minehutManager;
    private ConfigManager configManager;

    @Override
    public void onInitializeClient() {
        instance = this;

        this.minehutManager = new MinehutManager();
        this.configManager = new ConfigManager();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("toggleminehutnightvision")
                            .executes((ctx) -> {
                                final NightvisionConfig config = configManager.getConfig();
                                final ClientPlayerEntity player = MinecraftClient.getInstance().player;

                                if (player == null) {
                                    return -1;
                                }

                                config.setDisableNightvision(!config.isDisableNightvision());
                                configManager.saveConfig();

                                if (minehutManager.isOnMinehutLobby()) {
                                    if (config.isDisableNightvision()) {
                                        player.removeStatusEffect(StatusEffects.NIGHT_VISION);
                                    } else {
                                        player.addStatusEffect(
                                                new StatusEffectInstance(
                                                        StatusEffects.NIGHT_VISION,
                                                        Integer.MAX_VALUE,
                                                        1,
                                                        true,
                                                        false
                                                )
                                        );
                                    }
                                }

                                player.sendMessage(
                                        Text.literal("You have ")
                                                .append(
                                                        !config.isDisableNightvision() ? Text.literal("enabled").styled((styler) -> styler.withColor(Formatting.GREEN)) : Text.literal("disabled").styled((styler) -> styler.withColor(Formatting.RED))
                                                )
                                                .append(
                                                        Text.literal(" Minehut lobby night vision.")
                                                )
                                        ,
                                        false
                                );

                                if (!minehutManager.isOnMinehutLobby()) {
                                    if(config.isDisableNightvision()) {
                                        player.sendMessage(
                                                Text.literal("You aren't currently in the lobby, so night vision has not been removed.")
                                                        .styled((styler) -> styler.withColor(Formatting.GRAY).withItalic(true)),
                                                false
                                        );
                                    } else {
                                        player.sendMessage(
                                                Text.literal("You aren't currently in the lobby, so night vision has not been added.")
                                                        .styled((styler) -> styler.withColor(Formatting.GRAY).withItalic(true)),
                                                false
                                        );
                                    }
                                }

                                return 1;
                            })
            );
        });
    }

    public MinehutManager getMinehutManager() {
        return minehutManager;
    }

    public static RemoveNightvisionClient getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

}