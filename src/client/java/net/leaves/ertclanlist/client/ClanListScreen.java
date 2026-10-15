package net.leaves.ertclanlist.client;

import net.leaves.ertclanlist.ClanListManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClanListScreen extends Screen {
    private EditBox input;
    private StringWidget status;
    private EditBox colorInput;
    private StringWidget colorPreview;

    public ClanListScreen() {
        super(Component.translatable("screen.ertclanlist.title"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;

        addRenderableWidget(new StringWidget(cx - 100, 30, 200, 9, this.title, this.font));

        input = new EditBox(this.font, cx - 100, 60, 200, 20, Component.translatable("screen.ertclanlist.input"));
        input.setMaxLength(16);
        input.setHint(Component.translatable("screen.ertclanlist.input_hint"));
        addRenderableWidget(input);
        setInitialFocus(input);

        addRenderableWidget(Button.builder(Component.translatable("screen.ertclanlist.add"), b -> addPlayer())
                .bounds(cx - 100, 90, 98, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("screen.ertclanlist.list"),
                        b -> this.minecraft.setScreen(new ClanListViewScreen(this)))
                .bounds(cx + 2, 90, 98, 20).build());

        status = new StringWidget(cx - 100, 122, 200, 9, Component.empty(), this.font);
        addRenderableWidget(status);

        addRenderableWidget(new StringWidget(cx - 100, 150, 200, 9,
                Component.translatable("screen.ertclanlist.color_label"), this.font));

        colorInput = new EditBox(this.font, cx - 100, 164, 130, 20, Component.translatable("screen.ertclanlist.color_label"));
        colorInput.setMaxLength(7);
        colorInput.setValue(ClanListManager.getColorHex());
        addRenderableWidget(colorInput);

        addRenderableWidget(Button.builder(Component.translatable("screen.ertclanlist.color_apply"), b -> applyColor())
                .bounds(cx + 34, 164, 66, 20).build());

        colorPreview = new StringWidget(cx - 100, 192, 200, 9, Component.empty(), this.font);
        addRenderableWidget(colorPreview);
        updateColorPreview();
    }

    private void addPlayer() {
        String name = input.getValue().trim();
        if (!ClanListManager.isValidName(name)) {
            status.setMessage(Component.translatable("screen.ertclanlist.invalid"));
        } else if (ClanListManager.add(name)) {
            status.setMessage(Component.translatable("screen.ertclanlist.added", name));
            input.setValue("");
        } else {
            status.setMessage(Component.translatable("screen.ertclanlist.exists", name));
        }
    }

    private void applyColor() {
        if (ClanListManager.setColorFromHex(colorInput.getValue())) {
            colorInput.setValue(ClanListManager.getColorHex());
            updateColorPreview();
        } else {
            colorPreview.setMessage(Component.translatable("screen.ertclanlist.color_invalid"));
        }
    }

    private void updateColorPreview() {
        int rgb = ClanListManager.getColor();
        colorPreview.setMessage(
                Component.translatable("screen.ertclanlist.color_current", ClanListManager.getColorHex())
                        .withStyle(style -> style.withColor(rgb))
        );
    }
}