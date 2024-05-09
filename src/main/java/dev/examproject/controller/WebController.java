package dev.examproject.controller;

import dev.examproject.model.Project;
import dev.examproject.model.Task;
import dev.examproject.model.User;
import dev.examproject.service.ProjectService;
import dev.examproject.service.TaskService;
import dev.examproject.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping(path = "")
public class WebController {

    private final UserService userService;
    private final ProjectService projectService;

    private final TaskService taskService;

    public WebController(UserService userService, ProjectService projectService, TaskService taskService) {
        this.userService = userService;
        this.projectService = projectService;
        this.taskService = taskService;
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
         //   List<Task> tasks = taskService.getProjectTasks(selectedProject.getProjectId());
            List<Project> subProjects = projectService.getSubProjectsForProject(selectedProject.getProjectId());

            model.addAttribute("project", project);
           // model.addAttribute("tasks", tasks);
            model.addAttribute("subProjects", subProjects);
            model.addAttribute("totalHoursForProject", projectService.getTotalRequiredHoursForAllSubProjects(selectedProject.getProjectId()));

        }
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            model.addAttribute("user", authenticatedUser);
            return "overview";
        }
        return "redirect:/login";
    }

    @PostMapping(path = "/{username}/addUser")
    public String addUserToProject(@PathVariable("username") String username, @ModelAttribute("user") User user, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        Project selectedProject = (Project) session.getAttribute("selectedProject");
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            userService.addUserToProject(user, selectedProject.getProjectId());
            System.out.println("bruger tilføjet:" + user);
            return "redirect:/" + username + "/overview";
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

            projectService.addProject(project);
            System.out.println(projectService.getProjectId(project.getName()));
            userService.addUserToProject(authenticatedUser, project.getProjectId());
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
  /*  @GetMapping(path = "/{username}/addTask")
    public String addTask(@PathVariable("username") String username, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            model.addAttribute("user", authenticatedUser);
            model.addAttribute("task", new Task());
            model.addAttribute("project", session.getAttribute("selectedProject"));
            return "addTask";
        }
        return "redirect:/login";
    }*/
    @GetMapping(path = "/{username}/addSubProjectTask")
    public String addSubProjectTask(@PathVariable("username") String username, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            model.addAttribute("user", authenticatedUser);
            model.addAttribute("task", new Task());
            model.addAttribute("project", session.getAttribute("selectedProject"));
            return "addSubProjectTask";
        }
        return "redirect:/login";
    }
   /* @PostMapping(path = "/{username}/addTask")
    public String addTask(@PathVariable("username") String username, @ModelAttribute("task") Task task, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            Project selectedProject = (Project) session.getAttribute("selectedProject");
            if (selectedProject != null) {
                task.setProjectId(selectedProject.getProjectId());
                taskService.addTask(task);
            }
            System.out.println("task tilføjet:" + task);
            return "redirect:/" + username + "/overview";
        }
        return "redirect:/login";
    }*/
    @PostMapping(path = "/{username}/addSubProjectTask")
    public String addSubProjectTask(@PathVariable("username") String username, @ModelAttribute("task") Task task, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            Project selectedSubProject = (Project) session.getAttribute("selectedSubProject");
            if (selectedSubProject != null) {
                task.setProjectId(selectedSubProject.getProjectId());
                taskService.addTask(task);
            }
            System.out.println("task tilføjet:" + task);
            return "redirect:/subProjectOverview";

        }
        return "redirect:/login";
    }
    @GetMapping(path = "/{username}/addSubProject")
    public String addSubProject(@PathVariable("username") String username, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            model.addAttribute("user", authenticatedUser);
            model.addAttribute("subProject", new Project());
            return "addSubProject";
        }
        return "redirect:/login";
    }

    @PostMapping(path = "/{username}/addSubProject")
    public String addSubProject(@PathVariable("username") String username, @ModelAttribute("subProject") Project subProject, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            Project selectedProject = (Project) session.getAttribute("selectedProject");
            if (selectedProject != null) {
                subProject.setParentProjectID(selectedProject.getProjectId());
                projectService.addSubProject(subProject);
            }
            return "redirect:/" + username + "/overview";
        }
        return "redirect:/login";
    }
    @GetMapping(path = "/selectSubProject/{subProjectName}")
    public String selectSubProject(@PathVariable("subProjectName") String subProjectName, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null) {
            Project subProject = projectService.getSubProject(subProjectName);
            session.setAttribute("selectedSubProject", subProject);
            return "redirect:/subProjectOverview";
        }
        return "redirect:/login";
    }
    @GetMapping(path = "/subProjectOverview")
    public String subProjectOverview(HttpSession session, Model model) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (authenticatedUser != null) {
            Project selectedSubProject = (Project) session.getAttribute("selectedSubProject");
            if (selectedSubProject != null) {
                List<Task> tasks = taskService.getProjectTasks(selectedSubProject.getProjectId());
                model.addAttribute("tasks", tasks);
                model.addAttribute("selectedSubProject", selectedSubProject);
                model.addAttribute("user", authenticatedUser);
                model.addAttribute("totalHoursForProject", taskService.getTotalRequiredHoursForProject(selectedSubProject.getProjectId()));
                return "subProjectOverview";
            }
        }
        return "redirect:/login";
    }

}
