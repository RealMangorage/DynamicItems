package org.mangorage.dynamicitems.generator.records;

import com.google.gson.annotations.Expose;
import org.mangorage.dynamicitems.ImageItem;

public record ItemOverride(
        @Expose(serialize = false, deserialize = false) ImageItem imageItem,
        @Expose CustomModelData predicate,
        @Expose String model
) {}
