package com.github._0x00acab.pings;

import java.util.List;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PingCategoryTest {

    @Test
    void wheelHasTheFiveSpecCategoriesInOrder() {
        assertEquals(
            List.of(PingCategory.DANGER, PingCategory.LOOK_HERE, PingCategory.COME_HERE, PingCategory.NEED_HELP, PingCategory.MINING),
            PingCategory.WHEEL);
    }

    @Test
    void byIdRoundTripsEveryCategory() {
        for (PingCategory category : PingCategory.values()) {
            assertEquals(category, PingCategory.byId(category.ordinal()));
        }
    }

    @Test
    void byIdRejectsUnknownIds() {
        assertNull(PingCategory.byId(-1));
        assertNull(PingCategory.byId(PingCategory.values().length));
        assertNull(PingCategory.byId(Integer.MAX_VALUE));
    }

    @Test
    void spritesAndLangKeysUseTheModNamespace() {
        assertEquals("pings.category.look_here", PingCategory.LOOK_HERE.translationKey());
        assertEquals("pings:category/look_here", PingCategory.LOOK_HERE.sprite().toString());
    }
}
