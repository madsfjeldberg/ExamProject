package dev.examproject.controller;

import dev.examproject.model.User;
import dev.examproject.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "")
public class WebController {

    private final UserService userService;

    public WebController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String redirectHome() {
        return "redirect:/home";
    }

    @GetMapping(path = "home")
    public String home(Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        boolean isLoggedIn = authenticatedUser != null;
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("user", authenticatedUser);
        return "home";
    }

    @GetMapping(path = "login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping(path = "login")
    public String login(@ModelAttribute("user") User user, HttpSession session, RedirectAttributes attributes) {
        User authenticatedUser = userService.authenticateUser(user.getUsername(), user.getPassword());
        if (authenticatedUser != null) {
            session.setAttribute("user", authenticatedUser);
            return "redirect:/" + authenticatedUser.getUsername() + "/overview";
        } else {
            attributes.addFlashAttribute("errorMessage", "Incorrect username or password");
            return "redirect:/login";
        }
    }

    @GetMapping(path = "logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

    @GetMapping(path = "/{username}/overview")
    public String overview(@PathVariable("username") String username, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            model.addAttribute("user", authenticatedUser);
            return "overview";
        }
        return "redirect:/login";
    }


}
