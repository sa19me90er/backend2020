package nl.arothuis.notebook.notes.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

// Note that the @Entity couples the domain to persistence infrastructure
// This is not uncommon for data-driven APIs,
// but should be discouraged for more domain-driven applications
@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue
    private int id;

    private String title;
    private String author;
    private String contents;
    private LocalDateTime creationDate;

    public Note() {}
    public Note(String title, String author, String contents, LocalDateTime creationDate) {
        this.title = title;
        this.author = author;
        this.contents = contents;
        this.creationDate = creationDate;
    }
    public Note(int id, String title, String author, String contents, LocalDateTime creationDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.contents = contents;
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getContents() {
        return contents;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
