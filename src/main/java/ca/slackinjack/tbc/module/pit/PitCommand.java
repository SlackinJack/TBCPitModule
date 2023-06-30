package ca.slackinjack.tbc.module.pit;

import ca.slackinjack.tbc.TBC;
import ca.slackinjack.tbc.utils.TBCExternalModulesEnum;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class PitCommand extends CommandBase {

    private final TBC INSTANCE;
    private final TBCPitModule MODULE_INSTANCE;
    private final Pit PIT;
    private final String usageText = "Usage: /pit [pants/aura (test)]";

    public PitCommand(TBC tbcIn, TBCPitModule modIn, Pit pitIn) {
        INSTANCE = tbcIn;
        MODULE_INSTANCE = modIn;
        PIT = pitIn;
    }

    @Override
    public String getCommandName() {
        return TBCExternalModulesEnum.getCommandNameFor(TBCExternalModulesEnum.PIT);
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        switch (args.length) {
            case 1:
                if (args[0].equals("aura")) {
                    PIT.setOpenGuiAura();
                } else if (args[0].equals("pants")) {
                    PIT.setOpenGuiPants();
                } else {
                    INSTANCE.getUtilsPublic().addUnformattedChatMessage(this.usageText, 1);
                }
                break;
            case 2:
                if (args[0].equals("aura")) {
                    if (args[1].equals("test")) {
                        PIT.getPitAuraTimer().resetTime();
                    } else {
                        INSTANCE.getUtilsPublic().addUnformattedChatMessage(this.usageText, 1);
                    }
                } else {
                    INSTANCE.getUtilsPublic().addUnformattedChatMessage(this.usageText, 1);
                }
                break;
            default:
                INSTANCE.getUtilsPublic().addUnformattedChatMessage(this.usageText, 1);
                break;
        }
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
