package ca.slackinjack.tbc.module.pit;

import ca.slackinjack.tbc.TBC;
import ca.slackinjack.tbc.server.hypixel.Hypixel;
import ca.slackinjack.tbc.server.hypixel.HypixelInboundPacketHandler;
import ca.slackinjack.tbc.utils.packethandler.PacketEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2FPacketSetSlot;

public class PitInboundPacketHandler extends HypixelInboundPacketHandler {

    private final TBC INSTANCE;
    private final Pit PIT;
    private final Minecraft MINECRAFT = Minecraft.getMinecraft();

    public PitInboundPacketHandler(TBC tbcIn, Pit pIn) {
        super(tbcIn, (Hypixel) tbcIn.getUtilsPublic().getCurrentServer());
        INSTANCE = tbcIn;
        PIT = pIn;
    }

    @Override
    public boolean processPacket(Packet thePacket) {
        PacketEnum packetType = null;

        for (PacketEnum p : PacketEnum.values()) {
            if (p.getPacketClass() == thePacket.getClass()) {
                packetType = p;
                break;
            }
        }

        if (packetType != null) {
            switch (packetType) {
                case S2F:
                    S2FPacketSetSlot s2fPacket = (S2FPacketSetSlot) thePacket;
                    PIT.getPitAuraTimer().handleSetSlot(s2fPacket.func_149175_c(), s2fPacket.func_149173_d(), s2fPacket.func_149174_e());
                    break;
            }
        }

        return super.processPacket(thePacket);
    }
}
