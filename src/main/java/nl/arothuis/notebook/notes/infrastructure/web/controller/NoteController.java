package nl.arothuis.notebook.notes.infrastructure.web.controller;

import nl.arothuis.notebook.notes.application.NoteService;
import nl.arothuis.notebook.notes.domain.Note;
import nl.arothuis.notebook.notes.infrastructure.web.dto.NoteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("notes")
public class NoteController {
    @Autowired
    private NoteService noteService;

    @GetMapping(produces = "application/json")
    public List<Note> listNotes() {
        return this.noteService.listNotes();
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    public Note findNote(@PathVariable int id) {
        return this.noteService
                .findNoteById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public void writeNote(@RequestBody @Valid NoteRequest noteRequest) {
        this.noteService.writeNote(
                noteRequest.title,
                noteRequest.author,
                noteRequest.contents
        );
    }
}
