package nl.arothuis.notebook.notes.application;

import nl.arothuis.notebook.notes.domain.Note;
import nl.arothuis.notebook.notes.domain.NoteRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class NoteService {
    private NoteRepository notes;

    public NoteService(NoteRepository notes) {
        this.notes = notes;
    }

    public List<Note> listNotes() {
        return this.notes.list();
    }

    public Optional<Note> findNoteById(int id) {
        return this.notes.findById(id);
    }

    public void writeNote(String title, String author, String contents) {
        this.notes.save(
                new Note(title, author, contents, LocalDateTime.now())
        );
    }
}
