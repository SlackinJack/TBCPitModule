package ca.slackinjack.tbc.module.pit.mods;

import ca.slackinjack.tbc.TBC;
import ca.slackinjack.tbc.module.pit.Pit;
import ca.slackinjack.tbc.module.pit.TBCPitModule;
import ca.slackinjack.tbc.utils.chat.TextFormattingEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class PitAuraTimer extends Gui {

    private final TBC INSTANCE;
    private final TBCPitModule MODULE_INSTANCE;
    private final Pit PIT;
    private final Minecraft MINECRAFT = Minecraft.getMinecraft();
    private final String configKey = "Aura Timer Coordinates";

    private int auraTimeRemaining = 0;
    private boolean auraTimeRenderable = true;
    private int auraTimeExpiryTime = 100;

    public PitAuraTimer(TBC tbcIn, TBCPitModule modIn, Pit pitIn) {
        INSTANCE = tbcIn;
        MODULE_INSTANCE = modIn;
        PIT = pitIn;
    }

    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Pre e) {
        this.renderAuraTimer(e.resolution);
    }

    public void onClientTickEvent(TickEvent.ClientTickEvent e) {
        if (e.phase == Phase.START) {
            if (this.auraTimeRemaining > 0) {
                --this.auraTimeRemaining;
                this.auraTimeRenderable = true;
            } else {
                if (this.auraTimeExpiryTime < 100) {
                    ++this.auraTimeExpiryTime;
                }

                if (this.auraTimeExpiryTime >= 100) {
                    this.auraTimeRenderable = false;
                }
            }
        }
    }

    public void handleSetSlot(int windowId, int slot, ItemStack theItemStack) {
        if (windowId == 0 && slot >= 36 && slot < 45) {
            if (theItemStack == null) {
                slot = slot - 36;
                ItemStack currentStack = MINECRAFT.thePlayer.inventory.getStackInSlot(slot);
                if (currentStack != null && currentStack.getItem() == Items.slime_ball) {
                    this.resetTime();
                }
            } else if (theItemStack.getItem() == Items.slime_ball) {
                slot = slot - 36;
                ItemStack currentStack = MINECRAFT.thePlayer.inventory.getStackInSlot(slot);
                if (currentStack != null && currentStack.getItem() == Items.slime_ball) {
                    if (theItemStack.stackSize < currentStack.stackSize) {
                        this.resetTime();
                    }
                }
            }
        }
    }

    private void renderAuraTimer(ScaledResolution resIn) {
        if (this.auraTimeRenderable) {
            int backgroundLeft = INSTANCE.getUtilsPublic().getConfigLoader().getCoordinatesForKey(this.configKey)[0];
            int backgroundTop = INSTANCE.getUtilsPublic().getConfigLoader().getCoordinatesForKey(this.configKey)[1];
            int backgroundRight = backgroundLeft + 128;
            int backgroundBottom = backgroundTop + 32;
            double barFillLength = this.auraTimeRemaining / 300.0D;
            int barTop = backgroundTop + 8;
            int barBottom = backgroundBottom - 8;
            int healthBarLeft = backgroundLeft + 32;
            int healthBarRightMax = backgroundRight - 8;
            int playerHealthBar = new Double(barFillLength * (healthBarRightMax - healthBarLeft)).intValue();
            int healthBarRight = healthBarLeft + playerHealthBar;

            int backgroundColor = Integer.MIN_VALUE;
            int trackColor = Integer.MIN_VALUE;
            int fillColor = 0x9055FF55;
            int textColor = TextFormattingEnum.WHITE.getRGBValue();

            if (this.auraTimeRemaining <= 80.0D && this.auraTimeRemaining > 0.0D) {
                backgroundColor = 0x90FF5555;
                trackColor = Integer.MIN_VALUE;
                fillColor = 0x90AA0000;
                textColor = TextFormattingEnum.DARK_RED.getRGBValue();
            } else if (this.auraTimeRemaining <= 0.0D) {
                backgroundColor = 0x90AA0000;
                trackColor = 0x90000000;
                fillColor = 0x90AA0000;
                textColor = TextFormattingEnum.DARK_RED.getRGBValue();
            }

            INSTANCE.getUtilsPublic().drawBoxOnScreen(backgroundLeft, backgroundTop, backgroundRight, backgroundBottom, backgroundColor);
            if (this.auraTimeRemaining > 195) {
                MINECRAFT.fontRendererObj.drawStringWithShadow(String.format("%.0f", (this.auraTimeRemaining / 20.0D)) + "s", backgroundLeft + 8, backgroundTop + 12, textColor);
            } else {
                MINECRAFT.fontRendererObj.drawStringWithShadow(String.format("%.1f", (this.auraTimeRemaining / 20.0D)) + "s", backgroundLeft + 8, backgroundTop + 12, textColor);
            }
            INSTANCE.getUtilsPublic().drawBoxOnScreen(healthBarLeft, barTop, healthBarRightMax, barBottom, trackColor);
            INSTANCE.getUtilsPublic().drawBoxOnScreen(healthBarLeft, barTop, healthBarRight, barBottom, fillColor);
        }
    }

    public void resetTime() {
        this.auraTimeExpiryTime = 0;
        this.auraTimeRemaining = 300;
        this.auraTimeRenderable = true;
    }
}
