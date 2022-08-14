package com.thaddev.coolideas.client.gui;

import com.thaddev.coolideas.CoolIdeasMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmChatLinkScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.io.File;

@Environment(EnvType.CLIENT)
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
        super(Text.translatable(CoolIdeasMod.SCREEN_VERSION_MISMATCH));
        this.clientModVersion = clientModVersion;
        this.serverModVersion = serverModVersion;
        this.clientModLoader = clientModLoader;
        this.serverModLoader = serverModLoader;
    }

    @Override
    protected void init() {
        int pos = this.height / 2 + 30;
        if (!serverModLoader.equals(clientModLoader)) pos += 20;
        this.addDrawableChild(drawCenteredButtonWidget((this.width / 2) - 160, pos + 25, 150, 20, false, Text.translatable(OPEN_CURSEFORGE),
                (pButtonWidget) ->
                    this.client.setScreen(new ConfirmChatLinkScreen((confirmed) -> {
                        if (confirmed) {
                            Util.getOperatingSystem().open("");
                        }

                        this.client.setScreen(this);
                    }, "", false)),
                (pButtonWidget, pPoseStack, pMouseX, pMouseY) ->
                    renderTooltip(pPoseStack, Text.translatable(NO_DOWNLOAD), pMouseX, pMouseY)
            )
        );
        this.addDrawableChild(drawCenteredButtonWidget((this.width / 2), pos + 25, 150, 20, false, Text.translatable(OPEN_MODRINTH),
                (pButtonWidget) ->
                    this.client.setScreen(new ConfirmChatLinkScreen((confirmed) -> {
                        if (confirmed) {
                            Util.getOperatingSystem().open("");
                        }

                        this.client.setScreen(this);
                    }, "", false)),
                (pButtonWidget, pPoseStack, pMouseX, pMouseY) ->
                    renderTooltip(pPoseStack, Text.translatable(NO_DOWNLOAD), pMouseX, pMouseY)
            )
        );
        this.addDrawableChild(drawCenteredButtonWidget((this.width / 2) + 160, pos + 25, 150, 20, true, Text.translatable(OPEN_GITHUB),
                (pButtonWidget) ->
                    this.client.setScreen(new ConfirmChatLinkScreen((confirmed) -> {
                        if (confirmed) {
                            Util.getOperatingSystem().open("https://github.com/MyNameTsThad/CoolIdeasMod");
                            Util.getOperatingSystem().open(new File(this.client.runDirectory.getAbsolutePath() + "/mods"));
                            this.client.stop();
                        }

                        this.client.setScreen(this);
                    }, "https://github.com/MyNameTsThad/CoolIdeasMod", false)),
                (pButtonWidget, pPoseStack, pMouseX, pMouseY) ->
                    renderTooltip(pPoseStack, Text.translatable(OPEN_GITHUB), pMouseX, pMouseY)
            )
        );
        this.addDrawableChild(drawCenteredButtonWidget((this.width / 2) - 105, pos + 55, 200, 20, true, Text.translatable(DISCONNECT),
                (pButtonWidget) -> {
                    if (this.client != null && this.client.world != null) {
                        this.client.world.disconnect();
                    }
                }
            )
        );
        this.addDrawableChild(drawCenteredButtonWidget((this.width / 2) + 105, pos + 55, 200, 20, true, Text.translatable(IGNORE),
                (pButtonWidget) -> {
                    if (pButtonWidget.getMessage().getString().equals(Text.translatable(IGNORE).getString())) {
                        pButtonWidget.setMessage(Text.translatable(IGNORE_CONFIRM).formatted(Formatting.RED));
                    } else {
                        this.close();
                    }
                },
                (pButtonWidget, pPoseStack, pMouseX, pMouseY) ->
                    renderTooltip(pPoseStack, Text.translatable(IGNORE_CONFIRM), pMouseX, pMouseY)
            )
        );
    }

    @Override
    public void render(@NotNull MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, delta);
        drawCenteredText(matrixStack, this.textRenderer, this.title.copy().formatted(Formatting.BOLD).formatted(Formatting.UNDERLINE), this.width / 2, this.height / 2 - 65, 16777215);
        drawCenteredText(matrixStack, this.textRenderer, Text.translatable(MESSAGE_1), this.width / 2, this.height / 2 - 45, 16777215);
        drawCenteredText(matrixStack, this.textRenderer, Text.translatable(MESSAGE_2), this.width / 2, this.height / 2 - 25, 16777215);
        drawCenteredText(matrixStack, this.textRenderer, Text.translatable(MESSAGE_DISCREPANCY_LIST), this.width / 2, this.height / 2 - 5, 16777215);
        int pos = this.height / 2 + 15;
        if (!serverModLoader.equals(clientModLoader)) {
            MutableText toShow = Text.translatable(MESSAGE_MODLOADER)
                .append("  ")
                .append(
                    Text.translatable(CLIENT)
                        .append(" (")
                        .append(serverModVersion)
                        .append(")")
                        .formatted(Formatting.RED)
                )
                .append(" -> ")
                .append(
                    Text.translatable(SERVER)
                        .append(" (")
                        .append(serverModLoader)
                        .append(")")
                        .formatted(Formatting.GREEN)
                );
            drawCenteredText(matrixStack, this.textRenderer, toShow, this.width / 2, pos, 16777215);
            pos += 20;
        }
        if (!serverModVersion.equals(clientModVersion)) {
            MutableText toShow = Text.translatable(MESSAGE_MODVERSION)
                .append("  ")
                .append(
                    Text.translatable(CLIENT)
                        .append(" (")
                        .append(clientModVersion)
                        .append(")")
                        .formatted(Formatting.RED)
                )
                .append(" -> ")
                .append(
                    Text.translatable(SERVER)
                        .append(" (")
                        .append(serverModVersion)
                        .append(")")
                        .formatted(Formatting.GREEN)
                );
            drawCenteredText(matrixStack, this.textRenderer, toShow, this.width / 2, pos, 16777215);
        }
    }

    @Override
    public void close() {
        CoolIdeasMod.instance.isMismatching = true;
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private ButtonWidget drawCenteredButtonWidget(int x, int y, int width, int height, boolean active, MutableText text, ButtonWidget.PressAction press, ButtonWidget.TooltipSupplier tooltip) {
        ButtonWidget button = new ButtonWidget(x - (width / 2), y - (height / 2), width, height, text, press, tooltip);
        button.active = active;
        return button;
    }

    private ButtonWidget drawCenteredButtonWidget(int x, int y, int width, int height, boolean active, MutableText text, ButtonWidget.PressAction press) {
        ButtonWidget button = new ButtonWidget(x - (width / 2), y - (height / 2), width, height, text, press);
        button.active = active;
        return button;
    }
}