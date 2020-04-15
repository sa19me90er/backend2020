package nl.arothuis.notebook.notes.domain;

import java.util.List;
import java.util.Optional;

public interface NoteRepository {
    List<Note> list();
    Optional<Note> findById(int id);
    void save(Note note);
}
