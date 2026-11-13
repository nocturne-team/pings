package com.github._0x00acab.callouts.client;

import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

// Built-in sounds first, then any .ogg in config/callouts/sounds. Clicking one picks it and plays it.
public final class SoundPickerScreen extends Screen {
	private final Screen parent;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private SoundList list;

	public SoundPickerScreen(Screen parent) {
		super(Component.translatable("callouts.config.sound"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		layout.addTitleHeader(title, font);
		list = layout.addToContents(new SoundList(minecraft));
		LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
		footer.addChild(Button.builder(Component.translatable("callouts.config.open_sounds_folder"),
			button -> Blaze3D.openPath(CustomSounds.directory())).build());
		footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).build());
		layout.visitWidgets(this::addRenderableWidget);
		repositionElements();
	}

	@Override
	protected void repositionElements() {
		layout.arrangeElements();
		list.updateSize(width, layout);
	}

	@Override
	public void onClose() {
		PingClient.config().save();
		minecraft.gui.setScreen(parent);
	}

	private final class SoundList extends ObjectSelectionList<SoundList.Entry> {
		SoundList(Minecraft minecraft) {
			super(minecraft, SoundPickerScreen.this.width, SoundPickerScreen.this.height - 64, 32, 18);
			String current = PingClient.config().sound;
			for (PingSound sound : PingSound.values()) {
				add(sound.id(), current);
			}
			for (String file : CustomSounds.list()) {
				add(CustomSounds.PREFIX + file, current);
			}
			if (getSelected() != null) {
				scrollToEntry(getSelected());
			}
		}

		private void add(String id, String current) {
			Entry entry = new Entry(id);
			addEntry(entry);
			if (id.equals(current)) {
				setSelected(entry);
			}
		}

		final class Entry extends ObjectSelectionList.Entry<Entry> {
			private final String id;
			private final Component label;

			Entry(String id) {
				this.id = id;
				Component name = PingSounds.label(id);
				this.label = id.startsWith(CustomSounds.PREFIX)
					? Component.translatable("callouts.config.custom_sound", name)
					: name;
			}

			@Override
			public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
				graphics.centeredText(font, label, getContentXMiddle(), getContentYMiddle() - font.lineHeight / 2, 0xFFFFFFFF);
			}

			@Override
			public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
				setSelected(this);
				ClientConfig config = PingClient.config();
				config.sound = id;
				PingSounds.preview(id, config.soundVolume);
				return true;
			}

			@Override
			public Component getNarration() {
				return Component.translatable("narrator.select", label);
			}
		}
	}
}
