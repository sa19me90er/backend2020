package nl.arothuis.notebook.notes.infrastructure.persistence;

import nl.arothuis.notebook.notes.domain.Note;
import nl.arothuis.notebook.notes.domain.NoteRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public class PostgresNoteRepository implements NoteRepository  {
    private final EntityManager entityManager;

    public PostgresNoteRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Note> list() {
        return this.entityManager
                // Hibernate Query Language (HQL)
                .createQuery("SELECT n FROM Note n", Note.class)
                .getResultList();
    }

    @Override
    public Optional<Note> findById(int id) {
        Note note = this.entityManager.find(Note.class, id);
        return Optional.ofNullable(note);
    }

    @Override
    public void save(Note note) {
        this.entityManager.persist(note);
    }
}
