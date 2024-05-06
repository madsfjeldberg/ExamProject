package dev.examproject.controller;

import dev.examproject.model.Project;
import dev.examproject.model.User;
import dev.examproject.service.ProjectService;
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
    private final ProjectService projectService;

    public WebController(UserService userService, ProjectService projectService) {
        this.userService = userService;
        this.projectService = projectService;
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

    @GetMapping("/About")
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
            System.out.println(authenticatedUser);
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

    @GetMapping(path = "/{username}/overview")
    public String overview(@PathVariable("username") String username, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        Project selectedProject = (Project) session.getAttribute("selectedProject");
        if (selectedProject != null) {
            Project project = projectService.getProject(selectedProject.getName());
            model.addAttribute("project", project);
        }
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            model.addAttribute("user", authenticatedUser);
            return "overview";
        }
        return "redirect:/login";
    }

    @GetMapping(path = "/{username}/projects")
    public String projects(@PathVariable("username") String username, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            model.addAttribute("user", authenticatedUser);
            int userId = userService.getUserId(username);
            model.addAttribute("projects", projectService.getProjectsForUser(userId, username));
            return "projects";
        }
        return "redirect:/login";
    }

    @GetMapping(path = "/{username}/addProject")
    public String addProject(@PathVariable("username") String username, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            model.addAttribute("user", authenticatedUser);
            model.addAttribute("project", new Project());
            return "addProject";
        }
        return "redirect:/login";
    }

    @PostMapping(path = "/{username}/addProject")
    public String addProject(@PathVariable("username") String username, @ModelAttribute("project") Project project, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {

            projectService.addProject(project, username);
            System.out.println(projectService.getProjectId(project.getName()));
            userService.setUserToAdmin(username, projectService.getProjectId(project.getName()));

            System.out.println("projekt tilføjet:" + project);
            return "redirect:/" + username + "/projects";
        }
        return "redirect:/login";
    }

    @GetMapping(path = "/selectProject/{projectName}")
    public String selectProject(@PathVariable("projectName") String projectName, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null) {
            Project project = projectService.getProject(projectName);
            session.setAttribute("selectedProject", project);
            return "redirect:/" + authenticatedUser.getUsername() + "/overview";
        }
        return "redirect:/login";
    }

}
