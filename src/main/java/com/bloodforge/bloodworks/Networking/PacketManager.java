package com.bloodforge.bloodworks.Networking;

import com.bloodforge.bloodworks.Globals;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

@SuppressWarnings("unused")
public class PacketManager
{
    private static SimpleChannel INSTANCE;
    /* Stupid fucking names */
    private static final NetworkDirection PAYLOAD_TO_SERVER = NetworkDirection.PLAY_TO_SERVER;
    private static final NetworkDirection PAYLOAD_TO_CLIENT = NetworkDirection.PLAY_TO_CLIENT;
    private static final NetworkDirection QUERY_TO_SERVER = NetworkDirection.LOGIN_TO_SERVER;
    private static final NetworkDirection QUERY_TO_CLIENT = NetworkDirection.LOGIN_TO_CLIENT;

    private static int packetId = 0;

    private static int id()
    {
        return packetId++;
    }

    public static void register()
    {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Globals.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;

        net.messageBuilder(FluidSyncS2CPacket.class, id(), PAYLOAD_TO_CLIENT)
                .decoder(FluidSyncS2CPacket::new)
                .encoder(FluidSyncS2CPacket::toBytes)
                .consumerMainThread(FluidSyncS2CPacket::handle)
                .add();

        net.messageBuilder(TankSyncS2CPacket.class, id(), PAYLOAD_TO_CLIENT)
                .decoder(TankSyncS2CPacket::new)
                .encoder(TankSyncS2CPacket::toBytes)
                .consumerMainThread(TankSyncS2CPacket::handle)
                .add();

        net.messageBuilder(TankNameSyncS2CPacket.class, id(), PAYLOAD_TO_CLIENT)
                .decoder(TankNameSyncS2CPacket::new)
                .encoder(TankNameSyncS2CPacket::toBytes)
                .consumerMainThread(TankNameSyncS2CPacket::handle)
                .add();

        net.messageBuilder(UpdateTankS2CPacket.class, id(), PAYLOAD_TO_CLIENT)
                .decoder(UpdateTankS2CPacket::new)
                .encoder(UpdateTankS2CPacket::toBytes)
                .consumerMainThread(UpdateTankS2CPacket::handle)
                .add();

        net.messageBuilder(NeuronSyncS2CPacket.class, id(), PAYLOAD_TO_CLIENT)
                .decoder(NeuronSyncS2CPacket::new)
                .encoder(NeuronSyncS2CPacket::toBytes)
                .consumerMainThread(NeuronSyncS2CPacket::handle)
                .add();

        net.messageBuilder(MessageS2CPacket.class, id(), PAYLOAD_TO_CLIENT)
                .decoder(MessageS2CPacket::new)
                .encoder(MessageS2CPacket::toBytes)
                .consumerMainThread(MessageS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message)
    {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player)
    {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToClients(MSG message)
    {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}