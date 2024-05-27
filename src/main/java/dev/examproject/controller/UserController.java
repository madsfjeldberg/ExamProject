package dev.examproject.controller;

import dev.examproject.model.Project;
import dev.examproject.model.User;
import dev.examproject.repository.util.TurboLogger;
import dev.examproject.service.ProjectService;
import dev.examproject.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(path = "")
public class UserController {

    private static final TurboLogger log = TurboLogger.getInstance(UserController.class);

    private final UserService userService;
    private final ProjectService projectService;

    public UserController(UserService userService, ProjectService projectService) {
        this.userService = userService;
        this.projectService = projectService;
    }

    boolean isLoggedIn(HttpSession session, String username) {
        return session.getAttribute("user") != null && ((User) session.getAttribute("user")).getUsername().equals(username);
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

    @GetMapping("about")
    public String about(Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        boolean isLoggedIn = authenticatedUser != null;
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("user", authenticatedUser);
        return "about";
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
            log.info("User logged in: " + user);
            return "redirect:/" + authenticatedUser.getUsername() + "/projects";
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

    @GetMapping(path = "register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping(path = "register")
    public String register(@ModelAttribute("user") User user, HttpSession session, RedirectAttributes attributes) {
        User existingUser = userService.getUser(user.getUsername());
        if (existingUser != null) {
            attributes.addFlashAttribute("errorMessage", "Username already exists");
            return "redirect:/register";
        } else {
            userService.addUser(user);
            session.setAttribute("user", user);
            attributes.addFlashAttribute("successMessage", "User registered successfully");
            return "redirect:/" + user.getUsername() + "/overview";
        }
    }

    // Metode til både at adde en bruger til project eller subproject
    // alt efter hvilken type der er valgt.
    // hold kæft hvor er den lang. ser senere om den kan forkortes lidt :)
    // mads
    @PostMapping(path = "/{username}/assignuserto{type}")
    public String addUserToProject(@PathVariable("username") String username,
                                   @PathVariable("type") String type,
                                   @ModelAttribute("user") User user, HttpSession session,
                                   RedirectAttributes attributes) {
        if (isLoggedIn(session, username)) {
            Project selectedProject;

            // check hvilken "type" der er valgt
            if ("Project".equalsIgnoreCase(type)) {
                selectedProject = (Project) session.getAttribute("selectedProject");
                if (userService.getUser(user.getUsername()) == null) {
                    attributes.addFlashAttribute("errorMessage", "Brugeren eksisterer ikke.");
                    return "redirect:/" + username + "/overview";
                }
                if (selectedProject.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
                    attributes.addFlashAttribute("errorMessage", "Brugeren er allerede en del af projektet.");
                    return "redirect:/" + username + "/overview";
                }

            } else if ("SubProject".equalsIgnoreCase(type)) {
                selectedProject = (Project) session.getAttribute("selectedSubProject");
                Project parentProject = projectService.getProject(selectedProject.getParentProjectID());
                if (parentProject.getAssignedUsers().stream().noneMatch(u -> u.getUsername().equals(user.getUsername()))) { // check om bruger er medlem af main project
                    attributes.addFlashAttribute("errorMessage", "Brugeren er ikke en del af hoved-projektet.");
                    return "redirect:/" + username + "/subprojectoverview";
                } if (selectedProject.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(user.getUsername()))) {
                    attributes.addFlashAttribute("errorMessage", "Brugeren er allerede en del af projektet.");
                    return "redirect:/" + username + "/subprojectoverview";
                }

            } else {
                return "redirect:/" + username + (type.equalsIgnoreCase("Project") ? "/overview" : "/subprojectoverview");
            }

            //tilføj bruger til projekt eller subprojekt
            userService.addUserToProject(user, selectedProject.getProjectId());
            attributes.addFlashAttribute("successMessage", "Brugeren er blevet tilføjet til projektet.");

            // Hent opdateret projekt eller subprojekt
            if ("Project".equalsIgnoreCase(type)) {
                selectedProject = projectService.getProject(selectedProject.getProjectId());
            } else if ("SubProject".equalsIgnoreCase(type)) {
                selectedProject = projectService.getProject(selectedProject.getProjectId());
            }

            // Opdater session med det nye projekt eller subprojekt
            if ("Project".equalsIgnoreCase(type)) {
                session.setAttribute("selectedProject", selectedProject);
            } else if ("SubProject".equalsIgnoreCase(type)) {
                session.setAttribute("selectedSubProject", selectedProject);
            }

            return "redirect:/" + username + (type.equalsIgnoreCase("Project") ? "/overview" : "/subprojectoverview"); // redirecter til den korrekte "type"'s side.
        }
        return "redirect:/login";
    }

}
