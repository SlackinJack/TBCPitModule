package ca.slackinjack.tbc.module.pit;

import ca.slackinjack.tbc.TBC;
import ca.slackinjack.tbc.server.Minigame;
import ca.slackinjack.tbc.server.MinigameModuleBase;
import ca.slackinjack.tbc.utils.packethandler.InboundPacketHandlerBase;
import net.minecraft.command.CommandBase;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = TBCPitModule.MODID, version = TBCPitModule.VERSION, dependencies = TBCPitModule.DEPENDENCIES, acceptedMinecraftVersions = TBCPitModule.MCVERSION)
public class TBCPitModule implements MinigameModuleBase {

    // Forge
    public static final String MODID = ",";
    public static final String VERSION = "1.0.5";
    public static final String DEPENDENCIES = "required-after:.@[3.0.4,)";
    public static final String MCVERSION = "1.8.9";

    // Module
    private static TBCPitModule MODULE_INSTANCE;
    private PitInboundPacketHandler packetHandler;
    private Pit PIT;
    private PitCommand command;

    public TBCPitModule() {
        MODULE_INSTANCE = this;
    }

    @Override
    public MinigameModuleBase getModuleInstance() {
        return MODULE_INSTANCE;
    }

    @Override
    public Minigame getModuleMinigame(int dataIn) {
        if (PIT == null) {
            PIT = new Pit(TBC.getTBC(), this);
        }
        return PIT;
    }

    @Override
    public CommandBase getCommand() {
        if (this.command == null) {
            this.command = new PitCommand(TBC.getTBC(), this, PIT);
        }
        return this.command;
    }

    @Override
    public InboundPacketHandlerBase getInboundPacketHandler() {
        if (this.packetHandler == null) {
            this.packetHandler = new PitInboundPacketHandler(TBC.getTBC(), PIT);
        }

        return this.packetHandler;
    }
}
