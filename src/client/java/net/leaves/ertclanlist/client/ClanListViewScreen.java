package net.leaves.ertclanlist.client;

import net.leaves.ertclanlist.ClanListManager;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.Locale;

public class ClanListViewScreen extends Screen {
    private static final int PER_PAGE = 6;
    private static final int LIST_Y = 78;

    private final Screen parent;
    private int page = 0;
    private boolean searchOpen = false;
    private String searchText = "";

    private EditBox searchBox;
    private final Button[] nameButtons = new Button[PER_PAGE];
    private final String[] shown = new String[PER_PAGE];
    private Button prev;
    private Button next;
    private StringWidget pageLabel;

    public ClanListViewScreen(Screen parent) {
        super(Component.translatable("screen.ertclanlist.list_title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;

        addRenderableWidget(new StringWidget(cx - 100, 15, 200, 9, this.title, this.font));

        addRenderableWidget(Button.builder(Component.literal(searchOpen ? "Close search" : "Search"), b -> {
            searchOpen = !searchOpen;
            searchText = "";
            page = 0;
            this.rebuildWidgets();
        }).bounds(cx - 100, 30, 200, 20).build());

        if (searchOpen) {
            searchBox = new EditBox(this.font, cx - 100, 54, 200, 20, Component.literal("Search"));
            searchBox.setMaxLength(16);
            searchBox.setHint(Component.literal("Search name..."));
            searchBox.setValue(searchText);
            searchBox.setResponder(text -> {
                searchText = text;
                page = 0;
                refreshList();
            });
            addRenderableWidget(searchBox);
            setInitialFocus(searchBox);
        }

        for (int i = 0; i < PER_PAGE; i++) {
            final int index = i;
            nameButtons[i] = Button.builder(Component.empty(), b -> {
                if (shown[index] != null) {
                    ClanListManager.remove(shown[index]);
                    refreshList();
                }
            }).bounds(cx - 100, LIST_Y + i * 21, 200, 20).build();
            addRenderableWidget(nameButtons[i]);
        }

        int y = LIST_Y + PER_PAGE * 21 + 5;

        prev = Button.builder(Component.literal("<"), b -> {
            page--;
            refreshList();
        }).bounds(cx - 100, y, 40, 20).build();
        addRenderableWidget(prev);

        pageLabel = new StringWidget(cx - 20, y + 6, 100, 9, Component.empty(), this.font);
        addRenderableWidget(pageLabel);

        next = Button.builder(Component.literal(">"), b -> {
            page++;
            refreshList();
        }).bounds(cx + 60, y, 40, 20).build();
        addRenderableWidget(next);

        addRenderableWidget(Button.builder(Component.translatable("gui.back"),
                b -> this.minecraft.setScreen(parent)).bounds(cx - 100, y + 26, 200, 20).build());

        refreshList();
    }

    private List<String> filteredNames() {
        String query = searchText.trim().toLowerCase(Locale.ROOT);
        return ClanListManager.getAll().stream()
                .filter(name -> name.toLowerCase(Locale.ROOT).startsWith(query))
                .toList();
    }

    private void refreshList() {
        List<String> filtered = filteredNames();
        int maxPage = Math.max(0, (filtered.size() - 1) / PER_PAGE);
        page = Math.min(page, maxPage);

        for (int i = 0; i < PER_PAGE; i++) {
            int index = page * PER_PAGE + i;
            if (index < filtered.size()) {
                shown[i] = filtered.get(index);
                nameButtons[i].setMessage(Component.literal(shown[i] + "  (click to remove)"));
                nameButtons[i].visible = true;
            } else {
                shown[i] = null;
                nameButtons[i].visible = false;
            }
        }

        prev.active = page > 0;
        next.active = page < maxPage;
        pageLabel.setMessage(filtered.isEmpty()
                ? Component.literal("No results")
                : Component.literal((page + 1) + " / " + (maxPage + 1)));
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}