package ca.slackinjack.tbc.module.pit.mods;

import ca.slackinjack.tbc.TBC;
import ca.slackinjack.tbc.module.pit.Pit;
import ca.slackinjack.tbc.module.pit.TBCPitModule;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class PitDarkPantsFinder extends Gui {

    private final TBC INSTANCE;
    private final TBCPitModule MODULE_INSTANCE;
    private final Pit PIT;
    private final Minecraft mc = Minecraft.getMinecraft();
    private final ConcurrentMap<EntityPlayer, String> darkPantsPlayers = new ConcurrentHashMap<>();
    private final Pattern nameTrailingPattern = Pattern.compile("(?i)\\ \u00a7[0-9a-zA-Z](.*)");
    private final String configKey = "Dark Pants Finder Coordinates";

    public PitDarkPantsFinder(TBC tbcIn, TBCPitModule modIn, Pit pitIn) {
        INSTANCE = tbcIn;
        MODULE_INSTANCE = modIn;
        PIT = pitIn;
    }

    public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Pre e) {
        if (e.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
            this.renderDarkPantsFinder();
        }
    }

    public void updateDarkPantsPlayers(List<EntityPlayer> playerList) {
        for (EntityPlayer ep : playerList) {
            this.checkDarkPantsForPlayer(ep);
        }
    }

    private boolean checkDarkPantsForPlayer(EntityPlayer thePlayer) {
        boolean removePlayer = false;
        if (thePlayer != null && thePlayer != this.mc.thePlayer && thePlayer.inventory != null) {
            if (thePlayer.inventory.armorItemInSlot(1) != null && thePlayer.inventory.armorItemInSlot(1).getItem() == Items.leather_leggings) {
                ItemStack is = thePlayer.inventory.armorItemInSlot(1);
                ItemArmor ia = (ItemArmor) is.getItem();
                if (ia.hasColor(is) && ia.getColor(is) == 0) {
                    String enchantment = this.getEnchantment(is);

                    if (enchantment != null) {
                        this.darkPantsPlayers.put(thePlayer, enchantment);
                    }
                } else {
                    removePlayer = true;
                }
            } else {
                removePlayer = true;
            }
        } else {
            removePlayer = true;
        }

        if (removePlayer && this.darkPantsPlayers.containsKey(thePlayer)) {
            this.darkPantsPlayers.remove(thePlayer);
        }

        return !removePlayer;
    }

    private void renderDarkPantsFinder() {
        if (!this.darkPantsPlayers.isEmpty()) {
            int newBackgroundTop = 0 + INSTANCE.getUtilsPublic().getConfigLoader().getCoordinatesForKey(this.configKey)[1];
            int newBackgroundRight = INSTANCE.getUtilsPublic().getConfigLoader().getCoordinatesForKey(this.configKey)[0] + 280;
            int newBackgroundLeft = 0 + INSTANCE.getUtilsPublic().getConfigLoader().getCoordinatesForKey(this.configKey)[0];
            int newBackgroundBottom = INSTANCE.getUtilsPublic().getConfigLoader().getCoordinatesForKey(this.configKey)[1] + 24 + (this.darkPantsPlayers.size() * 16);

            GlStateManager.pushMatrix();

            drawRect(newBackgroundLeft, newBackgroundTop, newBackgroundRight, newBackgroundBottom, Integer.MIN_VALUE);

            int drawn = 0;
            for (Entry<EntityPlayer, String> entry : this.darkPantsPlayers.entrySet()) {
                if (this.checkDarkPantsForPlayer(entry.getKey())) {
                    if (this.mc.theWorld.getPlayerEntityByUUID(entry.getKey().getUniqueID()) != null) {
                        String enchant = "\u00a7d(" + entry.getValue() + "\u00a7d)";
                        boolean playerInSpawn = true;
                        if (entry.getKey().posX > 30.0D || entry.getKey().posX < -30.0D || entry.getKey().posZ > 30.0D || entry.getKey().posZ < -30.0D) {
                            playerInSpawn = false;
                        } else if (entry.getKey().posY < 65.0D) {
                            playerInSpawn = false;
                        }

                        String location = playerInSpawn ? "\u00a7a[S]" : "\u00a7c[D]";
                        String name = this.nameTrailingPattern.matcher(entry.getKey().getDisplayName().getFormattedText()).replaceAll("");
                        String distance = "\u00a76" + new DecimalFormat("0.0").format(entry.getKey().getDistance(this.mc.thePlayer.posX, this.mc.thePlayer.posY, this.mc.thePlayer.posZ));
                        String health = "\u00a7c" + new DecimalFormat("0").format(entry.getKey().getHealth()) + "\u2764";

                        this.mc.getRenderItem().renderItemAndEffectIntoGUI(entry.getKey().inventory.armorItemInSlot(1), newBackgroundLeft + 8, newBackgroundTop + 12 + (drawn * 16));

                        this.mc.fontRendererObj.drawStringWithShadow(enchant, newBackgroundLeft + 26, newBackgroundTop + 18 + (drawn * 16), 0xFFFFFF);
                        this.mc.fontRendererObj.drawStringWithShadow(location, newBackgroundLeft + 28 + this.mc.fontRendererObj.getStringWidth(enchant), newBackgroundTop + 18 + (drawn * 16), 0xFFFFFF);
                        this.mc.fontRendererObj.drawStringWithShadow(name, newBackgroundLeft + 30 + this.mc.fontRendererObj.getStringWidth(enchant) + this.mc.fontRendererObj.getStringWidth(location), newBackgroundTop + 18 + (drawn * 16), 0xFFFFFF);
                        this.mc.fontRendererObj.drawStringWithShadow(distance, newBackgroundRight - 12 - this.mc.fontRendererObj.getStringWidth(health) - this.mc.fontRendererObj.getStringWidth(distance), newBackgroundTop + 18 + (drawn * 16), 0xFFFFFF);
                        this.mc.fontRendererObj.drawStringWithShadow(health, newBackgroundRight - 10 - this.mc.fontRendererObj.getStringWidth(health), newBackgroundTop + 18 + (drawn * 16), 0xFFFFFF);
                        drawn++;
                    } else {
                        this.darkPantsPlayers.remove(entry.getKey());
                    }
                } else {
                    this.darkPantsPlayers.remove(entry.getKey());
                }
            }

            GlStateManager.popMatrix();
        }
    }

    private String getEnchantment(ItemStack itemStackIn) {
        if (itemStackIn != null) {
            NBTTagCompound tag = itemStackIn.getTagCompound();
            if (tag != null && tag.hasKey("ExtraAttributes")) {
                NBTTagCompound extraAttributesTag = tag.getCompoundTag("ExtraAttributes");
                if (extraAttributesTag != null) {
                    NBTTagList customEnchantsList = extraAttributesTag.getTagList("CustomEnchants", 10);
                    if (customEnchantsList != null && !customEnchantsList.hasNoTags()) {
                        NBTTagCompound lastEnchantTag = customEnchantsList.getCompoundTagAt(customEnchantsList.tagCount() - 1);
                        String lastEnchantName = lastEnchantTag.getString("Key").toLowerCase();
                        if (lastEnchantName.equals("golden_handcuffs")) {
                            return "Handcuffs";
                        }
                        if (lastEnchantName.equals("mind_assault")) {
                            return "MA";
                        }
                        if (lastEnchantName.equals("hedge_fund")) {
                            return "Hedge";
                        }
                        if (lastEnchantName.equals("needless_suffering")) {
                            return "30 Hearts";
                        }
                        if (lastEnchantName.equals("grim_reaper")) {
                            return "Grim";
                        }
                        if (lastEnchantName.equals("sanguisuge")) {
                            return "Sang";
                        }
                        if (lastEnchantName.equals("venom")) {
                            return "\u00a7b\u00a7lVenom";
                        }

                        return lastEnchantName.substring(0, 1).toUpperCase() + lastEnchantName.substring(1).toLowerCase();
                    }
                }
            }
        }
        return null;
    }
}
