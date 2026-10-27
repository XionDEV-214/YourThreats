package com.eisiadev.yourthreats.network;

import com.eisiadev.yourthreats.ThreatLevel;
import com.eisiadev.yourthreats.ThreatManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ThreatSetPacket(ThreatLevel level) {
    public static void encode(ThreatSetPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.level.ordinal());
    }

    public static ThreatSetPacket decode(FriendlyByteBuf buf) {
        return new ThreatSetPacket(ThreatLevel.byOrdinal(buf.readVarInt()));
    }

    public static void handle(ThreatSetPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer sender = ctx.get().getSender();
        if (sender != null) {
            MinecraftServer server = sender.server;
            if (canChange(sender)) {
                ThreatManager.setLevel(server, packet.level);
            } else {
                ThreatNetwork.sendTo(sender, ThreatManager.getLevel(server));
            }
        }
        ctx.get().setPacketHandled(true);
    }

    public static boolean canChange(ServerPlayer player) {
        return player.server.isSingleplayerOwner(player.getGameProfile()) || player.hasPermissions(2);
    }
}
