package com.skd.faunagarden.client;

import java.util.HashMap;
import java.util.Map;

/**
 * Vanilla spawn egg colors (primary and secondary) per entity id.
 * <p>
 * From Minecraft 26.1 on, spawn egg colors are baked into the egg textures and no longer exposed
 * through {@code SpawnEggItem}, so the values of the 1.21.1 spawn eggs are kept here. Entities that are
 * not in the table (modded mobs or mobs added after 1.21.1) report no color.
 */
public final class EggColors {
    private static final Map<String, int[]> COLORS = createTable();

    private EggColors() {
    }

    /**
     * Returns the opaque ARGB egg color of a vanilla entity.
     *
     * @param entityId  Full entity id such as {@code minecraft:cow}
     * @param tintIndex 0 for the primary color, 1 for the secondary color
     * @return the color, or -1 when the entity has no known egg color
     */
    public static int get(String entityId, int tintIndex) {
        if (entityId == null || !entityId.startsWith("minecraft:")) {
            return -1;
        }
        int[] pair = COLORS.get(entityId.substring("minecraft:".length()));
        if (pair == null || tintIndex < 0 || tintIndex > 1) {
            return -1;
        }
        return 0xFF000000 | pair[tintIndex];
    }

    private static Map<String, int[]> createTable() {
        Map<String, int[]> m = new HashMap<>();
        m.put("armadillo", new int[] {0xAD716D, 0x824848});
        m.put("allay", new int[] {0x00DAFF, 0x00ADFF});
        m.put("axolotl", new int[] {0xFBC1E3, 0xA62D74});
        m.put("bat", new int[] {0x4C3E30, 0x0F0F0F});
        m.put("bee", new int[] {0xEDC343, 0x43241B});
        m.put("blaze", new int[] {0xF6B201, 0xFFF87E});
        m.put("bogged", new int[] {0x8A9C72, 0x314D1B});
        m.put("breeze", new int[] {0xAF94DF, 0x9166DF});
        m.put("cat", new int[] {0xEFC88E, 0x957256});
        m.put("camel", new int[] {0xFCC369, 0xCB9337});
        m.put("cave_spider", new int[] {0x0C424E, 0xA80E0E});
        m.put("chicken", new int[] {0xA1A1A1, 0xFF0000});
        m.put("cod", new int[] {0xC1A76A, 0xE5C48B});
        m.put("cow", new int[] {0x443626, 0xA1A1A1});
        m.put("creeper", new int[] {0x0DA70B, 0x000000});
        m.put("dolphin", new int[] {0x223B4D, 0xF9F9F9});
        m.put("donkey", new int[] {0x534539, 0x867566});
        m.put("drowned", new int[] {0x8FF1D7, 0x799C65});
        m.put("elder_guardian", new int[] {0xCECCBA, 0x747693});
        m.put("ender_dragon", new int[] {0x1C1C1C, 0xE079FA});
        m.put("enderman", new int[] {0x161616, 0x000000});
        m.put("endermite", new int[] {0x161616, 0x6E6E6E});
        m.put("evoker", new int[] {0x959B9B, 0x1E1C1A});
        m.put("fox", new int[] {0xD5B69F, 0xCC6920});
        m.put("frog", new int[] {0xD07444, 0xFFC77C});
        m.put("ghast", new int[] {0xF9F9F9, 0xBCBCBC});
        m.put("glow_squid", new int[] {0x095656, 0x85F1BC});
        m.put("goat", new int[] {0xA5947C, 0x55493E});
        m.put("guardian", new int[] {0x5A8272, 0xF17D30});
        m.put("hoglin", new int[] {0xC66E55, 0x5F6464});
        m.put("horse", new int[] {0xC09E7D, 0xEEE500});
        m.put("husk", new int[] {0x797061, 0xE6CC94});
        m.put("iron_golem", new int[] {0xDBCDC2, 0x74A332});
        m.put("llama", new int[] {0xC09E7D, 0x995F40});
        m.put("magma_cube", new int[] {0x340000, 0xFCFC00});
        m.put("mooshroom", new int[] {0xA00F10, 0xB7B7B7});
        m.put("mule", new int[] {0x1B0200, 0x51331D});
        m.put("ocelot", new int[] {0xEFDE7D, 0x564434});
        m.put("panda", new int[] {0xE7E7E7, 0x1B1B22});
        m.put("parrot", new int[] {0x0DA70B, 0xFF0000});
        m.put("phantom", new int[] {0x43518A, 0x88FF00});
        m.put("pig", new int[] {0xF0A5A2, 0xDB635F});
        m.put("piglin", new int[] {0x995F40, 0xF9F3A4});
        m.put("piglin_brute", new int[] {0x592A10, 0xF9F3A4});
        m.put("pillager", new int[] {0x532F36, 0x959B9B});
        m.put("polar_bear", new int[] {0xEEEEDE, 0xD5D6CD});
        m.put("pufferfish", new int[] {0xF6B201, 0x37C3F2});
        m.put("rabbit", new int[] {0x995F40, 0x734831});
        m.put("ravager", new int[] {0x757470, 0x5B5049});
        m.put("salmon", new int[] {0xA00F10, 0x0E8474});
        m.put("sheep", new int[] {0xE7E7E7, 0xFFB5B5});
        m.put("shulker", new int[] {0x946794, 0x4D3852});
        m.put("silverfish", new int[] {0x6E6E6E, 0x303030});
        m.put("skeleton", new int[] {0xC1C1C1, 0x494949});
        m.put("skeleton_horse", new int[] {0x68684F, 0xE5E5D8});
        m.put("slime", new int[] {0x51A03E, 0x7EBF6E});
        m.put("sniffer", new int[] {0x871E09, 0x25AB70});
        m.put("snow_golem", new int[] {0xD9F2F2, 0x81A4A4});
        m.put("spider", new int[] {0x342D27, 0xA80E0E});
        m.put("squid", new int[] {0x223B4D, 0x708899});
        m.put("stray", new int[] {0x617677, 0xDDEAEA});
        m.put("strider", new int[] {0x9C3436, 0x4D494D});
        m.put("tadpole", new int[] {0x6D533D, 0x160A00});
        m.put("trader_llama", new int[] {0xEAA430, 0x456296});
        m.put("tropical_fish", new int[] {0xEF6915, 0xFFF9EF});
        m.put("turtle", new int[] {0xE7E7E7, 0x00AFAF});
        m.put("vex", new int[] {0x7A90A4, 0xE8EDF1});
        m.put("villager", new int[] {0x563C33, 0xBD8B72});
        m.put("vindicator", new int[] {0x959B9B, 0x275E61});
        m.put("wandering_trader", new int[] {0x456296, 0xEAA430});
        m.put("warden", new int[] {0x0F4649, 0x39D6E0});
        m.put("witch", new int[] {0x340000, 0x51A03E});
        m.put("wither", new int[] {0x141414, 0x4D72A0});
        m.put("wither_skeleton", new int[] {0x141414, 0x474D4D});
        m.put("wolf", new int[] {0xD7D3D3, 0xCEAF96});
        m.put("zoglin", new int[] {0xC66E55, 0xE6E6E6});
        m.put("zombie", new int[] {0x00AFAF, 0x799C65});
        m.put("zombie_horse", new int[] {0x315234, 0x97C284});
        m.put("zombie_villager", new int[] {0x563C33, 0x799C65});
        m.put("zombified_piglin", new int[] {0xEA9393, 0x4C7129});
        return m;
    }
}
