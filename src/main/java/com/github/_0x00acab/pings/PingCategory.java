package com.github._0x00acab.pings;

import java.util.List;
import net.minecraft.resources.Identifier;

public enum PingCategory {
    PLAIN("plain"),
    DANGER("danger"),
    LOOK_HERE("look_here"),
    COME_HERE("come_here"),
    NEED_HELP("need_help"),
    MINING("mining");

    public static final List<PingCategory> WHEEL = List.of(DANGER, LOOK_HERE, COME_HERE, NEED_HELP, MINING);
    private static final PingCategory[] BY_ID = values();

    private final String key;

    PingCategory(String key) {
        this.key = key;
    }

    public static PingCategory byId(int id) {
        return id >= 0 && id < BY_ID.length ? BY_ID[id] : null;
    }

    public String translationKey() {
        return "pings.category." + key;
    }

    public Identifier sprite() {
        return PingsMod.id("category/" + key);
    }
}
