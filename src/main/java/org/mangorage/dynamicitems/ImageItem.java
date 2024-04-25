package org.mangorage.dynamicitems;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.mangorage.dynamicitems.generator.Startup;

import java.util.Random;
import java.util.UUID;

public interface ImageItem {

    /**
     * @param material -> Default Material used
     * @param imagePath -> Internal Path to the Image
     * @return
     */
    public static ImageItem CreateImageItem(String imagePath, Material material, String plugin, boolean isBlock) {

        final var path = imagePath;
        var item = new ImageItem() {
            private final UUID RANDOM_ID = UUID.randomUUID();
            private final int MODEL_DATA = new Random().nextInt(Integer.MAX_VALUE);
            private final String INTERNAL_IMAGE = path;

            @Override
            public ItemStack create() {
                var item = new ItemStack(getMaterial(), 1);
                item.editMeta(meta -> meta.setCustomModelData(MODEL_DATA));
                return item;
            }

            @Override
            public Material getMaterial() {
                return material;
            }

            @Override
            public int getModelData() {
                return MODEL_DATA;
            }

            @Override
            public String getInternalImage() {
                return INTERNAL_IMAGE;
            }

            @Override
            public UUID getID() {
                return RANDOM_ID;
            }

            @Override
            public Plugin getPlugin() {
                return Bukkit.getPluginManager().getPlugin(plugin);
            }

            @Override
            @Deprecated // TEMP FUNC
            public boolean isBlock() {
                return isBlock;
            }
        };

        Startup.loadModel(item);

        return item;
    }


    int getModelData();
    boolean isBlock();

    UUID getID();
    String getInternalImage();

    ItemStack create();
    Material getMaterial();
    Plugin getPlugin();
}
