package ru.tbcarus.spendingsb.controller.note;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.model.User;

import java.util.List;

@Controller
@RequestMapping("/profile/notes")
public class NoteUIController extends AbstractNoteController{

    @RequestMapping("")
    public String getAll(Model model, @AuthenticationPrincipal User user) {
        List<Note> notes = super.getAll(user);
        model.addAttribute("notes", notes);
        model.addAttribute("user", user);
        return "notes";
    }

    @RequestMapping("/{id}")
    public String getById(Model model, @AuthenticationPrincipal User user, @PathVariable int id) {
        Note note = super.getNote(id, user.getEmail(), user);
        model.addAttribute("note", note);
        model.addAttribute("user", user);
        return "note";
    }

    @RequestMapping("/delete")
    public String delete(@RequestParam int id, @AuthenticationPrincipal User user) {

        return "notes";
    }

}
