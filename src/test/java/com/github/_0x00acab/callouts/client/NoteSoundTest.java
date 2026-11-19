package com.github._0x00acab.callouts.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NoteSoundTest {

	@Test
	void idRoundTrips() {
		NoteSound sound = new NoteSound("harp", 7);

		assertEquals("note:harp:7", sound.id());
		assertEquals(sound, NoteSound.parse(sound.id()));
	}

	@Test
	void rejectsMalformedIds() {
		assertNull(NoteSound.parse("click_soft"));
		assertNull(NoteSound.parse("note:harp"));
		assertNull(NoteSound.parse("note::3"));
		assertNull(NoteSound.parse("note:harp:25"));
		assertNull(NoteSound.parse("note:harp:-1"));
		assertNull(NoteSound.parse("note:harp:high"));
	}

	@Test
	void pitchMatchesNoteBlocks() {
		assertEquals(0.5f, new NoteSound("harp", 0).pitch(), 1e-6);
		assertEquals(1.0f, new NoteSound("harp", 12).pitch(), 1e-6);
		assertEquals(2.0f, new NoteSound("harp", 24).pitch(), 1e-6);
	}

	@Test
	void noteNamesFollowNoteBlockOrder() {
		assertEquals("F♯", new NoteSound("harp", 0).noteName());
		assertEquals("C", new NoteSound("harp", 6).noteName());
		assertEquals("F♯", new NoteSound("harp", 12).noteName());
	}
}
