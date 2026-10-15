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
    //names go here!
    private static final List<String> CODE_NAMES = List.of();

    private static final int DEFAULT_COLOR = 0x55FF55; // green

    private static final Pattern VALID_NAME = Pattern.compile("^[A-Za-z0-9_]{3,16}$");
    private static final Pattern VALID_HEX = Pattern.compile("^#?[0-9A-Fa-f]{6}$");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    private static final Map<String, String> NAMES = new LinkedHashMap<>();
    private static int color = DEFAULT_COLOR;

    /** What gets written to the JSON file. */
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
                    // old format: just a list of names
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

        // names from the code are always added
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

    /** @return true if added, false if it was already in the list */
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