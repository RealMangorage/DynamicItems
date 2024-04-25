package org.mangorage.dynamicitems.generator.records;

import com.google.gson.annotations.Expose;

import java.util.List;
import java.util.Map;

public record ItemModelJson(
        @Expose String parent,
        @Expose Map<String, String> textures,
        @Expose List<ItemOverride> overrides
) { }
