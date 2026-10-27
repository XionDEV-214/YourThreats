package com.eisiadev.yourthreats.network;

import com.eisiadev.yourthreats.ThreatLevel;
import com.eisiadev.yourthreats.YourThreats;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ThreatNetwork {
    private static final String VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(YourThreats.MODID, "main"),
            () -> VERSION, VERSION::equals, VERSION::equals);

    private ThreatNetwork() {}

    public static void register() {
        CHANNEL.messageBuilder(ThreatSyncPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ThreatSyncPacket::encode)
                .decoder(ThreatSyncPacket::decode)
                .consumerMainThread(ThreatSyncPacket::handle)
                .add();
        CHANNEL.messageBuilder(ThreatSetPacket.class, 1, NetworkDirection.PLAY_TO_SERVER)
                .encoder(ThreatSetPacket::encode)
                .decoder(ThreatSetPacket::decode)
                .consumerMainThread(ThreatSetPacket::handle)
                .add();
    }

    public static void sendTo(ServerPlayer player, ThreatLevel level) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ThreatSyncPacket(level));
    }

    public static void sendToAll(ThreatLevel level) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), new ThreatSyncPacket(level));
    }

    public static void sendToServer(ThreatLevel level) {
        CHANNEL.sendToServer(new ThreatSetPacket(level));
    }
}
