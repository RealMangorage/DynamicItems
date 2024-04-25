package org.mangorage.dynamicitems.generator.records;

import com.google.gson.annotations.Expose;
import org.mangorage.dynamicitems.ImageItem;

import java.util.Map;

public record ItemTextureJson(
        @Expose(deserialize = false, serialize = false) ImageItem imageItem,
        @Expose String parent,
        @Expose Map<String, String> textures
) {}
