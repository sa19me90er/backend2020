package nl.arothuis.notebook;

import nl.arothuis.notebook.notes.application.NoteService;
import nl.arothuis.notebook.notes.domain.NoteRepository;
import nl.arothuis.notebook.notes.infrastructure.persistence.PostgresNoteRepository;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class AppConfig {
    public static final String DEFAULT_DB_URL = "postgres://dev:dev@localhost:54321/notebook";

    @Bean
    public DataSource getDataSource() throws URISyntaxException {
        String dbUrl = System.getenv("DATABASE_URL");
        if (dbUrl == null) {
            dbUrl = DEFAULT_DB_URL;
        }

        // Heroku does not set JDBC_DATABASE_URL
        // when used in conjunction with containers
        // However, DATABASE_URL will always be set.
        // Otherwise, we could have used the settings in application.properties.
        // See: https://devcenter.heroku.com/articles/connecting-to-relational-databases-on-heroku-with-java#using-the-jdbc_database_url
        URI dbUri = new URI(dbUrl);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String jdbcUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        return DataSourceBuilder
                .create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .build();
    }

    @Bean
    public NoteService noteService(NoteRepository repository) {
        return new NoteService(repository);
    }

    @Bean
    public NoteRepository noteRepository(EntityManager entityManager) {
        return new PostgresNoteRepository(entityManager);
    }
}
