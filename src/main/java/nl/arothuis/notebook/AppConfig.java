package nl.arothuis.notebook;

import nl.arothuis.notebook.notes.application.NoteService;
import nl.arothuis.notebook.notes.domain.NoteRepository;
import nl.arothuis.notebook.notes.infrastructure.persistence.PostgresNoteRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@EnableAutoConfiguration
@Configuration
public class AppConfig {
    @PersistenceContext
    public EntityManager entityManager;

    @Bean
    public NoteService noteService(NoteRepository repository) {
        return new NoteService(repository);
    }

    @Bean
    public NoteRepository noteRepository(EntityManager entityManager) {
        return new PostgresNoteRepository(entityManager);
    }
}
