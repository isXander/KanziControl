package dev.isxander.kanzicontrol.interactionarea;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.isxander.kanzicontrol.controls.TouchLook;
import dev.isxander.kanzicontrol.controls.TouchWalk;
import dev.isxander.kanzicontrol.interactionarea.reader.InteractionAreaReader;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import org.joml.Vector2fc;
import org.quiltmc.json5.JsonReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static dev.isxander.kanzicontrol.utils.RenderUtils.scaledFingerPosition;

public class InteractionAreaStorage extends AbstractInteractionAreaContainer<InteractionArea> {
    public static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("bonobo_interaction_area.json5");
    private static InteractionAreaStorage instance = new InteractionAreaStorage();

    public final TouchLook TOUCH_LOOK = insertBottom(new TouchLook());
    public final TouchWalk TOUCH_WALK = insertAbove(new TouchWalk(), TOUCH_LOOK);
//    public final ButtonInteractionArea TOUCH_JUMP_BUTTON = new ButtonInteractionArea(Lexigrams.JUMP, 30f, 30f, ButtonActions.JUMP);
//    public final ButtonInteractionArea TOUCH_MINE_BUTTON = new ButtonInteractionArea(Lexigrams.BREAK, 30f, 30f, ButtonActions.BREAK);
//    public final ButtonInteractionArea TOUCH_ATTACK_BUTTON = new ButtonInteractionArea(Lexigrams.FIGHT, 30f, 30f, ButtonActions.FIGHT);
//    public final ButtonInteractionArea TOUCH_USE_BUTTON = new ButtonInteractionArea(new SolidColorRenderer(0xFF00FF), 30f, 30f, ButtonActions.USE);
//    public final ButtonInteractionArea TOUCH_SWIM_DOWN = new ButtonInteractionArea(new SolidColorRenderer(0x0000FF), 30f, 30f, ButtonActions.TOGGLE_SWIM_DOWN);
//    public final RowInteractionArea BUTTON_ROW = insertAbove(
//            RowInteractionArea.builder()
//                    .element(TOUCH_JUMP_BUTTON)
//                    .element(TOUCH_MINE_BUTTON)
//                    .element(TOUCH_ATTACK_BUTTON)
//                    .element(TOUCH_USE_BUTTON)
//                    .element(TOUCH_SWIM_DOWN)
//                    .elementPadding(3f)
//                    .elementPosition(RowInteractionArea.ElementPosition.MIDDLE)
//                    .position(AnchorPoint.TOP_RIGHT, 0f, 0f, AnchorPoint.TOP_RIGHT)
//                    .build(),
//            TOUCH_WALK
//    );
//
//    public final RowInteractionArea HOTBAR_ROW = insertAbove(
//            RowInteractionArea.builder()
//                    .elements(Util.make(() -> {
//                        List<ButtonInteractionArea> list = new ArrayList<>();
//                        for (int i = 0; i < 9; i++) {
//                            int finalI = i;
//                            list.add(new ButtonInteractionArea(KanziControlDebug.DEBUG_HOTBAR_BUTTONS ? new SolidColorRenderer(0x20FFFFFF) : EmptyRenderer.INSTANCE, 16f, 16f, ButtonAction.down(() -> {
//                                var minecraft = Minecraft.getInstance();
//                                minecraft.player.getInventory().selected = finalI;
//                            })));
//                        }
//                        return list;
//                    }))
//                    .elementPadding(4f)
//                    .elementPosition(RowInteractionArea.ElementPosition.BOTTOM)
//                    .rowPadding(0, 0, 0, 2f)
//                    .position(AnchorPoint.BOTTOM_CENTER, 0f, 0f, AnchorPoint.BOTTOM_CENTER)
//                    .build(),
//            BUTTON_ROW
//    );

    public InteractionAreaStorage() {
        if (Files.notExists(PATH)) {
            try (var defaultAreaIn = InteractionAreaStorage.class.getResource("/bonobo_interaction_area.json5").openStream()) {
                Files.copy(defaultAreaIn, PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try(var reader = JsonReader.json5(PATH)) {
            for (var area : InteractionAreaReader.readChildren(reader)) {
                insertTop(area);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void render(PoseStack poseStack, float deltaTime) {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().render(poseStack, deltaTime, scaledFingerPosition(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos()), true);
    }

    public static void tick() {
        if (Minecraft.getInstance().screen != null) return;

        getInstance().tick(scaledFingerPosition(Minecraft.getInstance().mouseHandler.xpos(), Minecraft.getInstance().mouseHandler.ypos()), true);
    }

    public static InteractionAreaStorage getInstance() {
        return instance;
    }

    public static void regen() {
        instance = new InteractionAreaStorage();
    }
}
