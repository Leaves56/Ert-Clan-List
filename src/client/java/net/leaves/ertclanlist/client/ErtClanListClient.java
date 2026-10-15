package net.leaves.ertclanlist.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.leaves.ertclanlist.ErtClanList;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class ErtClanListClient implements ClientModInitializer {
    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(ErtClanList.id("main"));

        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.ertclanlist.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new ClanListScreen());
                }
            }
        });
    }
}