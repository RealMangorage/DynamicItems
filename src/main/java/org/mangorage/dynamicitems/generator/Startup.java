package org.mangorage.dynamicitems.generator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.mangorage.dynamicitems.ImageItem;
import org.mangorage.dynamicitems.generator.records.CustomModelData;
import org.mangorage.dynamicitems.generator.records.ItemModelJson;
import org.mangorage.dynamicitems.generator.records.ItemOverride;
import org.mangorage.dynamicitems.generator.records.ItemTextureJson;
import org.mangorage.dynamicitems.generator.util.Util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Startup {
    private static final String GENERATED_PACK = "generatedpack";

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final Map<Material, ItemModelJson> ITEMS_MODELS_MINECRAFT = new HashMap<>();
    private static final List<ItemTextureJson> ITEM_TEXTURES = new ArrayList<>();


    private static byte[] hash = null;
    private static boolean cooked = false;
    private static ResourcePackInfo info;

    public static void loadModel(ImageItem item) {
        var data = ITEMS_MODELS_MINECRAFT.computeIfAbsent(item.getMaterial(), k -> new ItemModelJson(
                "item/generated",
                new HashMap<>(),
                new ArrayList<>()
        ));

        data.textures().put("layer0", "minecraft:%s/".formatted(item.isBlock() ? "block" : "item") + item.getMaterial().name().toLowerCase());
        data.overrides()
                .add(new ItemOverride(item, new CustomModelData(item.getModelData()), "dynamicitems:item/" +  item.getID()));

        var textures = new ItemTextureJson(item, "item/generated", new HashMap<>());
        textures.textures().put("layer0", "dynamicitems:item/" + item.getID());
        ITEM_TEXTURES.add(textures);
    }

    public static void cook() {
        Path root = Path.of(GENERATED_PACK);
        try {
            Util.deleteFolder(root.toFile());
            Util.writeStringToFile("""
                {
                  "pack": {
                    "pack_format": 7,
                    "description": "A generated Pack created by DynamicItems"
                  }
                }
                """, GENERATED_PACK + "/pack.mcmeta"
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        ITEMS_MODELS_MINECRAFT.forEach((k, v) -> {
            try {
                var data = GSON.toJson(v);
                Util.writeStringToFile(data, GENERATED_PACK + "/assets/minecraft/models/item/" + k.name().toLowerCase() + ".json");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        ITEM_TEXTURES.forEach(t -> {
            try {
                var data = GSON.toJson(t);
                var item = t.imageItem();

                Util.writeStringToFile(data, GENERATED_PACK + "/assets/dynamicitems/models/item/" + item.getID() + ".json");
                Util.copyFileFromJar(item.getPlugin(), item.getInternalImage(), GENERATED_PACK + "/assets/dynamicitems/textures/item/" + item.getID() + ".png");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Now ZIP it all up!


        try {
            Files.deleteIfExists(Path.of("files/pack.zip"));
            Util.zipDirectory(GENERATED_PACK, "files/pack.zip");
            hash = Util.getFileHash(Path.of("files/pack.zip").toFile());
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


        ResourcePackInfo.resourcePackInfo()
                .id(UUID.randomUUID())
                .uri(Util.buildURL("127.0.0.1", 1080, "/files/pack.zip"))
                .computeHashAndBuild()
                .thenAccept(i -> {
                    cooked = true;
                    info = i;
                });


        cooked = true;
    }

    public static boolean isCooked() {
        return cooked;
    }

    public static void sendPack(Plugin plugin, Player plr) {
        plr.sendResourcePacks(
                ResourcePackRequest.resourcePackRequest()
                        .packs(List.of(
                            info
                        ))
                        .prompt(Component.text("TESTING"))
                        .required(true)
                        .build()
        );
    }
}
