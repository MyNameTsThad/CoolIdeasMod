package com.thaddev.coolideas.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.thaddev.coolideas.CoolIdeasMod;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@OnlyIn(Dist.CLIENT)
public class ModMismatchScreen extends Screen {
    public static final String MESSAGE_1 = "menu.coolideas.modmismatchmessage.1";
    public static final String MESSAGE_2 = "menu.coolideas.modmismatchmessage.2";
    public static final String MESSAGE_DISCREPANCY_LIST = "menu.coolideas.modmismatchmessage.discrepancylist";
    public static final String MESSAGE_MODLOADER = "menu.coolideas.modmismatchmessage.modloader";
    public static final String MESSAGE_MODVERSION = "menu.coolideas.modmismatchmessage.modversion";
    public static final String CLIENT = "menu.coolideas.modmismatchmessage.client";
    public static final String SERVER = "menu.coolideas.modmismatchmessage.server";

    public static final String NO_DOWNLOAD = "menu.coolideas.modmismatchmessage.no_download";
    public static final String OPEN_CURSEFORGE = "menu.coolideas.modmismatchmessage.open_curseforge";
    public static final String OPEN_MODRINTH = "menu.coolideas.modmismatchmessage.open_modrinth";
    public static final String OPEN_GITHUB = "menu.coolideas.modmismatchmessage.open_github";
    public static final String DISCONNECT = "menu.coolideas.modmismatchmessage.disconnect";
    public static final String IGNORE = "menu.coolideas.modmismatchmessage.ignore";
    public static final String IGNORE_CONFIRM = "menu.coolideas.modmismatchmessage.ignore_confirm";

    private final String clientModVersion, serverModVersion;
    private final String clientModLoader, serverModLoader;

    public ModMismatchScreen(String clientModVersion, String serverModVersion, String clientModLoader, String serverModLoader) {
        super(Component.translatable(CoolIdeasMod.SCREEN_VERSION_MISMATCH));
        this.clientModVersion = clientModVersion;
        this.serverModVersion = serverModVersion;
        this.clientModLoader = clientModLoader;
        this.serverModLoader = serverModLoader;
    }

    @Override
    protected void init() {
        int pos = this.height / 2 + 30;
        if (!serverModLoader.equals(clientModLoader)) pos += 20;
        this.addRenderableWidget(drawCenteredButton((this.width / 2) - 160, pos + 25, 150, 20, false, Component.translatable(OPEN_CURSEFORGE),
                (pButton) ->
                    this.minecraft.setScreen(new ConfirmLinkScreen((p_169337_) -> {
                        if (p_169337_) {
                            Util.getPlatform().openUri("");
                        }

                        this.minecraft.setScreen(this);
                    }, "", false)),
                (pButton, pPoseStack, pMouseX, pMouseY) ->
                    renderTooltip(pPoseStack, Component.translatable(NO_DOWNLOAD), pMouseX, pMouseY)
            )
        );
        this.addRenderableWidget(drawCenteredButton((this.width / 2), pos + 25, 150, 20, false, Component.translatable(OPEN_MODRINTH),
                (pButton) ->
                    this.minecraft.setScreen(new ConfirmLinkScreen((p_169337_) -> {
                        if (p_169337_) {
                            Util.getPlatform().openUri("");
                        }

                        this.minecraft.setScreen(this);
                    }, "", false)),
                (pButton, pPoseStack, pMouseX, pMouseY) ->
                    renderTooltip(pPoseStack, Component.translatable(NO_DOWNLOAD), pMouseX, pMouseY)
            )
        );
        this.addRenderableWidget(drawCenteredButton((this.width / 2) + 160, pos + 25, 150, 20, true, Component.translatable(OPEN_GITHUB),
                (pButton) ->
                    this.minecraft.setScreen(new ConfirmLinkScreen((p_169337_) -> {
                        if (p_169337_) {
                            Util.getPlatform().openUri("https://github.com/MyNameTsThad/CoolIdeasMod");
                            Util.getPlatform().openFile(new File(this.minecraft.gameDirectory.getAbsolutePath() + "/mods"));
                            this.minecraft.stop();
                        }

                        this.minecraft.setScreen(this);
                    }, "https://github.com/MyNameTsThad/CoolIdeasMod", false)),
                (pButton, pPoseStack, pMouseX, pMouseY) ->
                    renderTooltip(pPoseStack, Component.translatable(OPEN_GITHUB), pMouseX, pMouseY)
            )
        );
        this.addRenderableWidget(drawCenteredButton((this.width / 2) - 105, pos + 55, 200, 20, true, Component.translatable(DISCONNECT),
                (pButton) -> {
                    if (this.minecraft != null && this.minecraft.level != null) {
                        this.minecraft.level.disconnect();
                    }
                }
            )
        );
        this.addRenderableWidget(drawCenteredButton((this.width / 2) + 105, pos + 55, 200, 20, true, Component.translatable(IGNORE),
                (pButton) -> {
                    if (pButton.getMessage().getString().equals(Component.translatable(IGNORE).getString())) {
                        pButton.setMessage(Component.translatable(IGNORE_CONFIRM).withStyle(ChatFormatting.RED));
                    }else{
                        this.onClose();
                    }
                },
                (pButton, pPoseStack, pMouseX, pMouseY) ->
                    renderTooltip(pPoseStack, Component.translatable(IGNORE_CONFIRM), pMouseX, pMouseY)
            )
        );
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int p_96563_, int p_96564_, float p_96565_) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, p_96563_, p_96564_, p_96565_);
        drawCenteredString(matrixStack, this.font, this.title.copy().withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.UNDERLINE), this.width / 2, this.height / 2 - 65, 16777215);
        drawCenteredString(matrixStack, this.font, Component.translatable(MESSAGE_1), this.width / 2, this.height / 2 - 45, 16777215);
        drawCenteredString(matrixStack, this.font, Component.translatable(MESSAGE_2), this.width / 2, this.height / 2 - 25, 16777215);
        drawCenteredString(matrixStack, this.font, Component.translatable(MESSAGE_DISCREPANCY_LIST), this.width / 2, this.height / 2 - 5, 16777215);
        int pos = this.height / 2 + 15;
        if (!serverModLoader.equals(clientModLoader)) {
            MutableComponent toShow = Component.translatable(MESSAGE_MODLOADER)
                .append("  ")
                .append(
                    Component.translatable(CLIENT)
                        .append(" (")
                        .append(serverModVersion)
                        .append(")")
                        .withStyle(ChatFormatting.RED)
                )
                .append(" -> ")
                .append(
                    Component.translatable(SERVER)
                        .append(" (")
                        .append(serverModLoader)
                        .append(")")
                        .withStyle(ChatFormatting.GREEN)
                );
            drawCenteredString(matrixStack, this.font, toShow, this.width / 2, pos, 16777215);
            pos += 20;
        }
        if (!serverModVersion.equals(clientModVersion)) {
            MutableComponent toShow = Component.translatable(MESSAGE_MODVERSION)
                .append("  ")
                .append(
                    Component.translatable(CLIENT)
                        .append(" (")
                        .append(clientModVersion)
                        .append(")")
                        .withStyle(ChatFormatting.RED)
                )
                .append(" -> ")
                .append(
                    Component.translatable(SERVER)
                        .append(" (")
                        .append(serverModVersion)
                        .append(")")
                        .withStyle(ChatFormatting.GREEN)
                );
            drawCenteredString(matrixStack, this.font, toShow, this.width / 2, pos, 16777215);
        }
    }

    @Override
    public void onClose() {
        CoolIdeasMod.instance.isMismatching = true;
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void openLinkAndCloseGame(String link) {

    }

    private Button drawCenteredButton(int x, int y, int width, int height, boolean active, MutableComponent text, Button.OnPress press, Button.OnTooltip tooltip) {
        Button button = new Button(x - (width / 2), y - (height / 2), width, height, text, press, tooltip);
        button.active = active;
        return button;
    }

    private Button drawCenteredButton(int x, int y, int width, int height, boolean active, MutableComponent text, Button.OnPress press) {
        Button button = new Button(x - (width / 2), y - (height / 2), width, height, text, press);
        button.active = active;
        return button;
    }
}
