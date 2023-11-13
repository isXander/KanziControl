package dev.isxander.kanzicontrol.interactionarea;

import dev.isxander.kanzicontrol.config.KanziConfig;
import dev.isxander.kanzicontrol.debug.KanziControlDebug;
import dev.isxander.kanzicontrol.interactionarea.elements.*;
import dev.isxander.kanzicontrol.interactionarea.button.*;
import dev.isxander.kanzicontrol.utils.InventoryUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Vector2fc;

import java.util.ArrayList;
import java.util.List;

import static dev.isxander.kanzicontrol.utils.RenderUtils.scaledFingerPosition;

public class RootInteractionArea extends AbstractInteractionAreaContainer<InteractionArea> {
    private static RootInteractionArea instance = new RootInteractionArea();

    public final TouchLook TOUCH_LOOK = insertBottom(new TouchLook());
    public final TouchWalk TOUCH_WALK = insertAbove(new TouchWalk(), TOUCH_LOOK);
    public final ScreenDamageFlashArea SCREEN_DAMAGE_FLASH = insertTop(new ScreenDamageFlashArea());
    public final IndicatorTextureArea INDICATOR_TEXTURE = insertTop(new IndicatorTextureArea());
    public final CursorDisplayArea CURSOR_DISPLAY = insertTop(new CursorDisplayArea());
    public final TouchEntity TOUCH_ENTITY = insertTop(new TouchEntity());

    private RootInteractionArea() {
        // BUTTON ROW
        insertBelow(
                RowInteractionArea.builder()
                        /* JMP */ //.element(new ButtonInteractionArea(Lexigrams.JUMP, 32f, 32f, ButtonActions.JUMP, ButtonRenderPredicates.ALWAYS))
                        /* BRK */ //.element(new ButtonInteractionArea(Lexigrams.BREAK, 32f, 32f, ButtonActions.BREAK, ButtonRenderPredicates.ALWAYS))
                        /* ATK */ //.element(new ButtonInteractionArea(Lexigrams.FIGHT, 32f, 32f, ButtonActions.FIGHT, ButtonRenderPredicates.ALWAYS))
                        /* USE */ //.element(new ButtonInteractionArea(Lexigrams.USE, 32f, 32f, ButtonActions.USE, ButtonRenderPredicates.ALWAYS))
                        /* SWM */ //.element(new ButtonInteractionArea(Lexigrams.TOGGLE_SWIM_DOWN, 32f, 32f, ButtonActions.TOGGLE_SWIM_DOWN, ButtonRenderPredicates.ALWAYS))
                        /* EAT */ .element(new EatButtonArea(32f, 32f))
                        .elementIf(KanziConfig.INSTANCE.instance().shareButton, () -> new ButtonInteractionArea(Lexigrams.GIVE, 32f, 32f, ButtonActions.SHARE, ButtonRenderPredicates.PLAYER_NEARBY))
                        .element(new ButtonInteractionArea(new SolidColorRenderer(-1), 32f, 32f, ButtonActions.WATER_BUCKET_CLUTCH, ButtonRenderPredicates.FALLING))
                        .elementPadding(10f)
                        .elementPosition(RowInteractionArea.ElementPosition.MIDDLE)
                        .position(AnchorPoint.TOP_CENTER, 0f, -19f, AnchorPoint.TOP_CENTER)
                        .build(),
                CURSOR_DISPLAY
        );

        // HOTBAR
        if (false) // disabled
        insertTop(
                RowInteractionArea.builder()
                        .elements(Util.make(() -> {
                            List<ButtonInteractionArea> list = new ArrayList<>();
                            for (int i = 0; i < 9; i++) {
                                int finalI = i;
                                list.add(new ButtonInteractionArea(KanziControlDebug.DEBUG_HOTBAR_BUTTONS ? new SolidColorRenderer(0x20FFFFFF) : EmptyRenderer.INSTANCE, 16f, 16f, ButtonAction.down(() -> {
                                    var minecraft = Minecraft.getInstance();
                                    minecraft.player.getInventory().selected = finalI;
                                }), ButtonRenderPredicates.ALWAYS));
                            }
                            return list;
                        }))
                        .elementPadding(4f)
                        .elementPosition(RowInteractionArea.ElementPosition.BOTTOM)
                        .rowPadding(0, 0, 0, 2f)
                        .position(AnchorPoint.BOTTOM_CENTER, 0f, 0f, AnchorPoint.BOTTOM_CENTER)
                        .build()
        );
    }

    @Override
    public boolean isInBounds(Vector2fc position) {
        return true;
    }

    public static void onMouseDown(float fingerScreenX, float fingerScreenY) {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().fingerDown(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void onMouseMove(float fingerScreenX, float fingerScreenY) {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().fingerMove(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void onMouseUp(float fingerScreenX, float fingerScreenY) {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().fingerUp(scaledFingerPosition(fingerScreenX, fingerScreenY));
    }

    public static void render(GuiGraphics graphics, float deltaTime) {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().render(graphics, deltaTime, scaledFingerPosition(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos()), true);
    }

    public static void tick() {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().tick(scaledFingerPosition(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos()), true);
    }

    public static RootInteractionArea getInstance() {
        return instance;
    }

    public static void regen() {
        instance = new RootInteractionArea();
    }
}
