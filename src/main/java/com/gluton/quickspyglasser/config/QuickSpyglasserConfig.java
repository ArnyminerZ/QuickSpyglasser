package com.gluton.quickspyglasser.config;

import com.gluton.quickspyglasser.QuickSpyglasserClient;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Optional;

@Config(name = "quickspyglasser")
public class QuickSpyglasserConfig implements ConfigData {
    public boolean showSpyglassOverlay = true;
    public boolean playSpyglassSound = true;
    public boolean smoothZoom = false;
    @ConfigEntry.Gui.Tooltip(count = 3)
    public String quickSpyglassItemId = "minecraft:spyglass";

    public static ConfigHolder<QuickSpyglasserConfig> init() {
        ConfigHolder<QuickSpyglasserConfig> configHolder = AutoConfig.register(
                QuickSpyglasserConfig.class, JanksonConfigSerializer::new);
        AutoConfig.getConfigHolder(QuickSpyglasserConfig.class).registerSaveListener((holder, config) -> {
            Identifier itemId = Identifier.tryParse(config.quickSpyglassItemId);
            if (itemId != null) {
                Optional<Item> optionalItem = Registry.ITEM.getOrEmpty(itemId);
                if (optionalItem.isPresent()) {
                    QuickSpyglasserClient.quickSpyglassItem = optionalItem.get();
                    return ActionResult.SUCCESS;
                } else if (config.quickSpyglassItemId.isBlank()) {
                    QuickSpyglasserClient.quickSpyglassItem = Items.AIR;
                    return ActionResult.SUCCESS;
                }
            }

            config.quickSpyglassItemId = "minecraft:spyglass";
            QuickSpyglasserClient.quickSpyglassItem = Items.SPYGLASS;
            return ActionResult.SUCCESS;
        });
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(
                (s, m) -> AutoConfig.getConfigHolder(QuickSpyglasserConfig.class).load());
        return configHolder;
    }

    @Environment(EnvType.CLIENT)
    public static class QuickSpyglasserModMenu implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return screen -> AutoConfig.getConfigScreen(QuickSpyglasserConfig.class, screen).get();
        }
    }
}