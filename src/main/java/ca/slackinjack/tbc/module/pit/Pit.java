package ca.slackinjack.tbc.module.pit;

import ca.slackinjack.tbc.TBC;
import ca.slackinjack.tbc.module.pit.mods.PitDarkPantsFinder;
import ca.slackinjack.tbc.module.pit.mods.PitAuraTimer;
import ca.slackinjack.tbc.server.Minigame;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Pit extends Minigame {

    private final TBCPitModule MODULE_INSTANCE;

    private final PitDarkPantsFinder df;
    private final PitAuraTimer at;
    private boolean openGuiAura;
    private boolean openGuiPants;
    private int ticksSinceLastUpdate = 0;

    public Pit(TBC tbcIn, TBCPitModule modIn) {
        super(tbcIn);
        MODULE_INSTANCE = modIn;

        this.df = new PitDarkPantsFinder(INSTANCE, MODULE_INSTANCE, this);
        this.at = new PitAuraTimer(INSTANCE, MODULE_INSTANCE, this);
    }

    @Override
    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Pre e) {
        switch (e.type) {
            case HOTBAR:
                this.df.onRenderGameOverlayEvent(e);
                this.at.onRenderGameOverlayEvent(e);
                break;
        }

    }

    @Override
    public void onClientTickEvent(TickEvent.ClientTickEvent e) {
        if (this.openGuiAura) {
            this.openGuiAura = !this.openGuiAura;
            MINECRAFT.displayGuiScreen(INSTANCE.getUtilsPublic().new UtilsEditGui("pit", "Aura Timer Coordinates", INSTANCE.getUtilsPublic().getConfigLoader().getCoordinatesForKey("Aura Timer Coordinates")));
        }
        if (this.openGuiPants) {
            this.openGuiPants = !this.openGuiPants;
            MINECRAFT.displayGuiScreen(INSTANCE.getUtilsPublic().new UtilsEditGui("pit", "Dark Pants Finder Coordinates", INSTANCE.getUtilsPublic().getConfigLoader().getCoordinatesForKey("Dark Pants Finder Coordinates")));
        }

        this.at.onClientTickEvent(e);

        if (MINECRAFT.theWorld != null && !MINECRAFT.theWorld.playerEntities.isEmpty()) {
            this.ticksSinceLastUpdate++;

            if (MINECRAFT.theWorld.playerEntities.size() > 40) {
                if (this.ticksSinceLastUpdate % 60 == 0) {
                    this.ticksSinceLastUpdate = 0;
                    this.df.updateDarkPantsPlayers(MINECRAFT.theWorld.playerEntities);
                }
            } else {
                if (this.ticksSinceLastUpdate % 30 == 0) {
                    this.ticksSinceLastUpdate = 0;
                    this.df.updateDarkPantsPlayers(MINECRAFT.theWorld.playerEntities);
                }
            }
        }
    }

    public void setOpenGuiAura() {
        this.openGuiAura = true;
    }

    public void setOpenGuiPants() {
        this.openGuiPants = true;
    }

    public PitDarkPantsFinder getPitDPF() {
        return this.df;
    }

    public PitAuraTimer getPitAuraTimer() {
        return this.at;
    }
}
