package nl.arothuis.notebook.notes.infrastructure.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class NoteRequest {
    @NotNull
    @NotBlank
    public String title;

    @NotNull
    @NotBlank
    public String author;

    @NotNull
    @NotBlank
    public String contents;
}
