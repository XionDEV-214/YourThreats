package com.eisiadev.yourthreats;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

public class ThreatSavedData extends SavedData {
    private static final String FILE_ID = YourThreats.MODID;
    private static final String KEY = "ThreatLevel";

    private ThreatLevel level = ThreatLevel.DEFAULT;

    public static ThreatSavedData get(MinecraftServer server) {
        return server.overworld().getDataStorage()
                .computeIfAbsent(ThreatSavedData::load, ThreatSavedData::new, FILE_ID);
    }

    private static ThreatSavedData load(CompoundTag tag) {
        ThreatSavedData data = new ThreatSavedData();
        ThreatLevel parsed = ThreatLevel.byName(tag.getString(KEY));
        data.level = parsed != null ? parsed : ThreatLevel.DEFAULT;
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putString(KEY, level.name());
        return tag;
    }

    public ThreatLevel getLevel() {
        return level;
    }

    public void setLevel(ThreatLevel level) {
        this.level = level;
        setDirty();
    }
}
