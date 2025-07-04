package toufoumaster.btwaila.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScreenHudDesigner;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.phys.HitResult;
import toufoumaster.btwaila.BTWailaClient;
import toufoumaster.btwaila.demo.DemoManager;
import toufoumaster.btwaila.util.Colors;

import static toufoumaster.btwaila.BTWaila.translator;

public class BaseInfoComponent extends WailaTextComponent {
    private final int topPadding = 4;
    public BaseInfoComponent(String key, Layout layout) {
        super(key, 24, layout);
    }

    @Override
    public int getAnchorY(ComponentAnchor anchor) {
        if (anchor.yPosition == 0.0f && !(anchor == ComponentAnchor.TOP_CENTER)){
            return (int)(anchor.yPosition * getYSize(Minecraft.getMinecraft())) + topPadding;
        }
        return (int)(anchor.yPosition * getYSize(minecraft));
    }
    @Override
    public int getYSize(Minecraft mc) {
        if (!(mc.currentScreen instanceof ScreenHudDesigner) && !this.isVisible(mc)) {
            return 0;
        }
        return height();
    }
    @Override
    public boolean isVisible(Minecraft minecraft) {
        return minecraft.gameSettings.immersiveMode.drawHotbar();
    }

    @Override
    public void renderPost(Minecraft minecraft, HudIngame HudIngame, int xScreenSize, int yScreenSize, float f) {
        addOffY(topPadding);
        HitResult hitResult = minecraft.objectMouseOver;
        if (hitResult == null) {return;}
        if (hitResult.hitType == HitResult.HitType.TILE) {
            Block<?> block = minecraft.currentWorld.getBlock(hitResult.x, hitResult.y, hitResult.z);
            int meta = minecraft.currentWorld.getBlockMetadata(hitResult.x, hitResult.y, hitResult.z);
            ItemStack[] drops = null;
            if (block != null) {
                drops = block.getBreakResult(minecraft.currentWorld, EnumDropCause.PICK_BLOCK, hitResult.x, hitResult.y, hitResult.z, minecraft.currentWorld.getBlockMetadata(hitResult.x, hitResult.y, hitResult.z), null);
            }
            baseBlockInfo(block, meta, drops);
        } else if (hitResult.hitType == HitResult.HitType.ENTITY) {
            baseEntityInfo(hitResult.entity);
        }
    }

    @Override
    public void renderPreviewPost(Minecraft minecraft, Gui gui, Layout layout, int xScreenSize, int yScreenSize) {
        addOffY(topPadding);
        Block<?> block = DemoManager.getCurrentEntry().block;
        int meta = DemoManager.getCurrentEntry().meta;
        ItemStack[] drops = DemoManager.getCurrentEntry().drops;
        Entity entity = DemoManager.getCurrentEntry().entity;
        if (block != null){
            baseBlockInfo(block, meta, drops);
        } else if (entity != null) {
            baseEntityInfo(entity);
        }
    }
    protected void baseBlockInfo(Block<?> block, int blockMetadata, ItemStack[] blockDrops){
        if (!modSettings().bTWaila$getBlockTooltips().value) return;
        if (minecraft.font == null) return;

        ItemStack renderItem = new ItemStack(block, 1, blockMetadata);
        if (blockDrops != null && blockDrops.length > 0) renderItem = blockDrops[0];

        String languageKey = renderItem.getItemKey();

        String blockName = translator.translateNameKey(languageKey);
        String blockDesc = translator.translateDescKey(languageKey);
        String blockSource = "Minecraft";
        for (String modId: BTWailaClient.modIds.keySet()){
            if (languageKey.contains(modId)){
                blockSource = BTWailaClient.modIds.get(modId);
            }
        }
        String idString = block.id() + ":" + blockMetadata;
        if (modSettings().bTWaila$getShowBlockId().value){
            blockName += " " + idString;
        }

        drawStringJustified(blockName,0,getXSize(minecraft), Colors.WHITE);
        drawStringJustified(blockSource,0,getXSize(minecraft), Colors.BLUE);
        if (modSettings().bTWaila$getShowBlockDesc().value){
            drawStringJustified(blockDesc,0,getXSize(minecraft), Colors.LIGHT_GRAY);
        }
    }
    protected void baseEntityInfo(Entity entity){
        if (!modSettings().bTWaila$getEntityTooltips().value) return;
        boolean isLivingEntity = (entity instanceof Mob);
        Mob Mob = isLivingEntity ? (Mob) entity : null;

        int color = Colors.WHITE;
        if (isLivingEntity) {
            color = Colors.GREEN;
            if (entity instanceof MobMonster) {
                color = Colors.RED;
            }
            else if (entity instanceof Player) {
                color = Mob.chatColor;
            }
        }
        drawStringJustified(AdvancedInfoComponent.getEntityName(entity), 0, getXSize(minecraft), color);
    }

}
