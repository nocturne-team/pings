package com.github._0x00acab.callouts.client;

import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

// Built-in sounds, note block instruments, then any .ogg in config/callouts/sounds. Clicking one picks it and plays it.
public final class SoundPickerScreen extends Screen {
	private final Screen parent;
	private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
	private SoundList list;
	private PitchSlider pitch;

	public SoundPickerScreen(Screen parent) {
		super(Component.translatable("callouts.config.sound"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		layout.addTitleHeader(title, font);
		pitch = new PitchSlider();
		list = layout.addToContents(new SoundList(minecraft));
		LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
		footer.addChild(pitch);
		footer.addChild(Button.builder(Component.translatable("callouts.config.open_sounds_folder"),
			button -> Blaze3D.openPath(CustomSounds.directory())).width(120).build());
		footer.addChild(Button.builder(CommonComponents.GUI_DONE, button -> onClose()).width(80).build());
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

	private static NoteSound currentNote() {
		return NoteSound.parse(PingClient.config().sound);
	}

	private static void choose(String id) {
		ClientConfig config = PingClient.config();
		config.sound = id;
		PingSounds.preview(id, config.soundVolume);
	}

	// Only usable while a note block sound is picked; keeps its note when switching instruments.
	private static final class PitchSlider extends AbstractSliderButton {
		private int note;

		PitchSlider() {
			super(0, 0, 120, 20, Component.empty(), 0);
			NoteSound current = currentNote();
			note = current == null ? NoteSound.DEFAULT_NOTE : current.note();
			value = note / (double) NoteSound.MAX_NOTE;
			refresh();
		}

		int note() {
			return note;
		}

		void refresh() {
			active = currentNote() != null;
			updateMessage();
		}

		@Override
		protected void updateMessage() {
			setMessage(Component.translatable("callouts.config.pitch", new NoteSound("", note).noteName(), note));
		}

		@Override
		protected void applyValue() {
			int newNote = (int) Math.round(value * NoteSound.MAX_NOTE);
			if (newNote == note) {
				return;
			}
			note = newNote;
			NoteSound current = currentNote();
			if (current != null) {
				choose(current.withNote(note).id());
			}
		}
	}

	private final class SoundList extends ObjectSelectionList<SoundList.Entry> {
		SoundList(Minecraft minecraft) {
			super(minecraft, SoundPickerScreen.this.width, SoundPickerScreen.this.height - 64, 32, 18);
			for (PingSound sound : PingSound.values()) {
				add(new Entry(sound.id(), null, PingSounds.label(sound.id())));
			}
			for (NoteBlockInstrument instrument : PingSounds.noteInstruments()) {
				add(new Entry(null, instrument.getSerializedName(),
					Component.translatable("callouts.sound.note_block", PingSounds.instrumentName(instrument))));
			}
			for (String file : CustomSounds.list()) {
				String id = CustomSounds.PREFIX + file;
				add(new Entry(id, null, Component.translatable("callouts.config.custom_sound", PingSounds.label(id))));
			}
			if (getSelected() != null) {
				scrollToEntry(getSelected());
			}
		}

		private void add(Entry entry) {
			addEntry(entry);
			if (entry.isCurrent()) {
				setSelected(entry);
			}
		}

		final class Entry extends ObjectSelectionList.Entry<Entry> {
			// Exactly one of id and instrument is set; a note block's id also depends on the pitch slider.
			private final String id;
			private final String instrument;
			private final Component label;

			Entry(String id, String instrument, Component label) {
				this.id = id;
				this.instrument = instrument;
				this.label = label;
			}

			boolean isCurrent() {
				if (instrument != null) {
					NoteSound current = currentNote();
					return current != null && current.instrument().equals(instrument);
				}
				return id.equals(PingClient.config().sound);
			}

			@Override
			public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float partialTick) {
				graphics.centeredText(font, label, getContentXMiddle(), getContentYMiddle() - font.lineHeight / 2, 0xFFFFFFFF);
			}

			@Override
			public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
				setSelected(this);
				choose(instrument != null ? new NoteSound(instrument, pitch.note()).id() : id);
				pitch.refresh();
				return true;
			}

			@Override
			public Component getNarration() {
				return Component.translatable("narrator.select", label);
			}
		}
	}
}
