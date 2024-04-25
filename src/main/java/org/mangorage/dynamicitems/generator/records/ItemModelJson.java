package org.mangorage.dynamicitems.generator.records;

import java.util.List;
import java.util.Map;

public record ItemJson(String parent, Map<String, String> textures, List<ItemOverride> overrideList) {
}
