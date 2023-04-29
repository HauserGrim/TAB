package me.neznamy.tab.shared.features.layout.skin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.NonNull;
import me.neznamy.tab.shared.config.file.ConfigurationFile;
import me.neznamy.tab.shared.config.file.YamlConfigurationFile;
import me.neznamy.tab.shared.TAB;
import me.neznamy.tab.shared.platform.TabList;
import org.jetbrains.annotations.Nullable;

public class SkinManager {

    private final List<String> invalidSkins = new ArrayList<>();
    @Getter private TabList.Skin defaultSkin;
    private final Map<String, SkinSource> sources = new HashMap<>();

    public SkinManager(@NonNull String defaultSkin) {
        try {
            File f = new File(TAB.getInstance().getDataFolder(), "skincache.yml");
            if (f.exists() || f.createNewFile()) {
                ConfigurationFile cache = new YamlConfigurationFile(null, f);
                sources.put("player", new PlayerSkin(cache));
                sources.put("mineskin", new MineSkin(cache));
                sources.put("texture", new Texture(cache));
                this.defaultSkin = getSkin(defaultSkin);
            } else {
                TAB.getInstance().getErrorManager().criticalError("Failed to load skin cache", null);
            }
        } catch (IOException e) {
            TAB.getInstance().getErrorManager().criticalError("Failed to load skin cache", e);
        }
    }

    public @Nullable TabList.Skin getSkin(@NonNull String skin) {
        if (invalidSkins.contains(skin)) return defaultSkin;
        for (Entry<String, SkinSource> entry : sources.entrySet()) {
            if (skin.startsWith(entry.getKey() + ":")) {
                List<String> value = entry.getValue().getSkin(skin.substring(entry.getKey().length()+1));
                if (value.isEmpty()) {
                    invalidSkins.add(skin);
                    return defaultSkin;
                }
                return new TabList.Skin(value.get(0), value.get(1));
            }
        }
        TAB.getInstance().getMisconfigurationHelper().invalidLayoutSkinDefinition(skin);
        return null;
    }
}