package ru.tbcarus.spendingsb.controller.note;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.tbcarus.spendingsb.model.Note;
import ru.tbcarus.spendingsb.model.User;

import java.util.List;

@Controller
@RequestMapping("/profile")
public class NoteUIController extends AbstractNoteController{

    @RequestMapping("/notes")
    public String getAll(Model model, @AuthenticationPrincipal User user) {
        List<Note> notes = super.getAll(user);
        model.addAttribute("notes", notes);
        model.addAttribute("user", user);
        return "notes";
    }

    @RequestMapping("/invites")
    public String getInvites(Model model, @AuthenticationPrincipal User user) {
        List<Note> notes = super.getInvites(user);
        model.addAttribute("notes", notes);
        model.addAttribute("user", user);
        return "invites";
    }

    @RequestMapping("/notes/{id}")
    public String getById(Model model, @AuthenticationPrincipal User user, @PathVariable int id) {
        Note note = super.getNote(id, user.getEmail(), user);
        model.addAttribute("note", note);
        model.addAttribute("user", user);
        return "note";
    }

    @RequestMapping("/notes/{id}/delete")
    public String deleteNote(@PathVariable int id, @AuthenticationPrincipal User user, Model model) {
        super.deleteNote(id, user);
        return "redirect:/profile/notes";
    }

    @RequestMapping("/invites/{id}/delete")
    public String deleteOwnInvite(@PathVariable int id, @AuthenticationPrincipal User user, @RequestParam String email) {
        super.deleteOwnInvite(id, user.id(), email);
        return "redirect:/profile/invites";
    }

    @RequestMapping("/notes/{id}/accept")
    public String inviteAccept(@PathVariable int id, @AuthenticationPrincipal User user, Model model) {
        super.inviteAccept(id, user);
        return "redirect:/payments/profile/group";
    }
}
