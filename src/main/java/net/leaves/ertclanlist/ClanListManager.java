package net.leaves.ertclanlist;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public final class ClanListManager {
    private static final List<String> CODE_NAMES = List.of(
            // A
            "ABIBRO", "adilisole", "Airforce", "Ahmedamage", "aksu2", "Angeloqt",
            "AaradhyaProBro", "areyou", "Assasin_String", "Asze1556", "awa_nb",
            "Axolote_boy", "Anam_ji",
            // B
            "Ballman69", "blaze9", "boviii", "Bread_", "Bunnyxzy",
            // C
            "cagri_0", "caliberc12345342", "CameronKai_", "ChaosCrusher", "chickenooge",
            "CG_PRIMES", "ConradIsDUmb", "CriticoFiscal_23",
            // D
            "DoubleTree", "daijin_ryz", "DanielXmiL", "DemilXGod", "Devilbhaiya5566",
            "dhruvisdream", "D1im", "Dkiller004", "Dpmo", "Dracogamer5678", "dwerpy440",
            // E
            "EBK007", "EmeraldNova520", "exabyte", "EvilStar", "exploded",
            // F
            "FemaleKnight", "fxddup", "Frostbang",
            // G
            "Goliver22", "Galaxyfan", "GenesisLB12", "Genteel_RebesX5", "GGboi2000",
            // H
            "Hasnain176_", "hasyyTheGoat", "Higoshi", "HITPVPP", "hotiv",
            "herobrine998877", "hrishabh_warrior",
            // I
            "Insight_World", "Isabelle_", "isimyokki", "Its_Aza09", "itsProBix",
            "ItzRynzoGOAT", "Invictus_Fish", "Izuna_Uchiha140",
            // J
            "John_Cena5876",
            // K
            "KARASTAR95A", "KD_GAMER_83", "Kinaicsaszar", "Kivren", "Klorna_Fa",
            // L
            "LtraDude", "Leaves56_", "lil_lion0110", "Lord479", "LorD_AUysh123",
            "Lowlife", "Lqlac_", "Ltx1w", "Lostbylife", "LynxBear_",
            // M
            "Magma_Ranger", "Manas", "Mander", "MarcusIndigo", "ME_UKR7", "Mimaklima007",
            "Mingling", "ming13285", "mirkoprofesi", "mixer567", "Mqstics", "MineMaster102",
            "mohamedamin2048", "Mohib8c",
            // N
            "nathanlabz", "NotRare123", "NotXufflux", "Nowace", "Nor",
            // O
            "ODAyal", "ocommon", "oCxlpped", "OmarPastaLatas", "Olympia", "onepiece22", "Owl",
            // P
            "P3W_", "palasam", "PapaLowlife", "ParaMgaming07", "Peitoxz", "Pixo_xD",
            "piyushindian", "Proximity101", "PuruBhai12",
            // Q
            "Quite_one",
            // R
            "ramu123", "Reachh_", "Regalll", "rizze_", "Roco_Spea", "ROYALWHITE444",
            "RoshanTiwari", "RubyDaCherry00",
            // S
            "Sahann1", "SaudyaGamer", "Serjali", "ShafayGOAT", "shake3b", "shadowofightz",
            "ShadowEmblem", "Slexzx", "soham_hehe", "Spy16", "SS_34", "strqm", "Stret_MD",
            "StretMD", "StrykingMaterial", "Subbiplays", "SubZero2807",
            // T
            "Terror_Rox", "thatskibidimatt", "Thegreatboi", "TheIronGystum007", "thvamo08",
            "thunder9795", "Tobi___YT", "Tomioka_giyu69", "toji_fr", "therushingdragon",
            // U
            "u_could",
            // V
            "vikbhy89", "Viaanpromax", "Vaelqorx", "Velphinx_", "Ventakura", "Vesynn",
            "VibeJoy49", "vxrioq",
            // X
            "XBeasTTyX", "Xi3moreeeX", "XMishere", "XxBossGamerxX", "XxLuffySanxX",
            // Y
            "YFsimo", "ylmz2023", "yosoycraftyt",
            // Z
            "zirox_", "Zyphronix"

    );

    private static final int DEFAULT_COLOR = 0x55FF55;

    private static final Pattern VALID_NAME = Pattern.compile("^[A-Za-z0-9_]{3,16}$");
    private static final Pattern VALID_HEX = Pattern.compile("^#?[0-9A-Fa-f]{6}$");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    private static final Map<String, String> NAMES = new LinkedHashMap<>();
    private static int color = DEFAULT_COLOR;

    private static class SaveData {
        String color;
        List<String> players;
    }

    private ClanListManager() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("ertclanlist.json");
    }

    public static synchronized void load() {
        NAMES.clear();
        color = DEFAULT_COLOR;
        Path path = file();
        boolean fileExists = Files.exists(path);

        if (fileExists) {
            try (Reader reader = Files.newBufferedReader(path)) {
                JsonElement root = JsonParser.parseReader(reader);
                if (root.isJsonArray()) {

                    for (JsonElement element : root.getAsJsonArray()) {
                        String name = element.getAsString();
                        NAMES.put(name.toLowerCase(Locale.ROOT), name);
                    }
                } else if (root.isJsonObject()) {
                    SaveData data = GSON.fromJson(root, SaveData.class);
                    if (data.players != null) {
                        for (String name : data.players) {
                            NAMES.put(name.toLowerCase(Locale.ROOT), name);
                        }
                    }
                    if (data.color != null && isValidHex(data.color)) {
                        color = parseHex(data.color);
                    }
                }
            } catch (Exception e) {
                ErtClanList.LOGGER.error("Could not read clan list", e);
            }
        }


        boolean changed = !fileExists;
        for (String name : CODE_NAMES) {
            if (NAMES.putIfAbsent(name.toLowerCase(Locale.ROOT), name) == null) {
                changed = true;
            }
        }
        if (changed) save();
    }

    private static void save() {
        SaveData data = new SaveData();
        data.color = getColorHex();
        data.players = new ArrayList<>(NAMES.values());
        try {
            Files.createDirectories(file().getParent());
            try (Writer writer = Files.newBufferedWriter(file())) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            ErtClanList.LOGGER.error("Could not save clan list", e);
        }
    }


    public static boolean isValidName(String name) {
        return name != null && VALID_NAME.matcher(name).matches();
    }

    public static synchronized boolean contains(String name) {
        return NAMES.containsKey(name.toLowerCase(Locale.ROOT));
    }

    public static synchronized boolean add(String name) {
        String key = name.toLowerCase(Locale.ROOT);
        if (NAMES.containsKey(key)) return false;
        NAMES.put(key, name);
        save();
        return true;
    }

    public static synchronized boolean remove(String name) {
        boolean removed = NAMES.remove(name.toLowerCase(Locale.ROOT)) != null;
        if (removed) save();
        return removed;
    }

    public static synchronized List<String> getAll() {
        return new ArrayList<>(NAMES.values());
    }


    public static boolean isValidHex(String text) {
        return text != null && VALID_HEX.matcher(text.trim()).matches();
    }

    private static int parseHex(String text) {
        String hex = text.trim();
        if (hex.startsWith("#")) hex = hex.substring(1);
        return Integer.parseInt(hex, 16);
    }

    public static synchronized int getColor() {
        return color;
    }

    public static synchronized String getColorHex() {
        return String.format("#%06X", color);
    }

    public static synchronized boolean setColorFromHex(String text) {
        if (!isValidHex(text)) return false;
        color = parseHex(text);
        save();
        return true;
    }
}