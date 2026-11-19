package com.github._0x00acab.callouts.client;

// A note block instrument and note (0-24, like right-clicking a note block), stored as "note:<instrument>:<note>".
public record NoteSound(String instrument, int note) {
	public static final String PREFIX = "note:";
	public static final int MAX_NOTE = 24;
	public static final int DEFAULT_NOTE = 12;
	private static final String[] NAMES = {"F♯", "G", "G♯", "A", "A♯", "B", "C", "C♯", "D", "D♯", "E", "F"};

	public static NoteSound parse(String id) {
		if (!id.startsWith(PREFIX)) {
			return null;
		}
		String[] parts = id.substring(PREFIX.length()).split(":");
		if (parts.length != 2 || parts[0].isEmpty()) {
			return null;
		}
		try {
			int note = Integer.parseInt(parts[1]);
			return note >= 0 && note <= MAX_NOTE ? new NoteSound(parts[0], note) : null;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public String id() {
		return PREFIX + instrument + ":" + note;
	}

	public NoteSound withNote(int newNote) {
		return new NoteSound(instrument, newNote);
	}

	// Same formula as NoteBlock.getPitchFromNote.
	public float pitch() {
		return (float) Math.pow(2.0, (note - 12) / 12.0);
	}

	public String noteName() {
		return NAMES[note % 12];
	}
}
