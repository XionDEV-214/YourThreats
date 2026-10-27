package com.eisiadev.yourthreats.network;

import com.eisiadev.yourthreats.ClientThreatState;
import com.eisiadev.yourthreats.ThreatLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ThreatSyncPacket(ThreatLevel level) {
    public static void encode(ThreatSyncPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.level.ordinal());
    }

    public static ThreatSyncPacket decode(FriendlyByteBuf buf) {
        return new ThreatSyncPacket(ThreatLevel.byOrdinal(buf.readVarInt()));
    }

    public static void handle(ThreatSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ClientThreatState.level = packet.level;
        ctx.get().setPacketHandled(true);
    }
}
