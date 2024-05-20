package dev.examproject.controller;

import dev.examproject.model.Project;
import dev.examproject.model.Task;
import dev.examproject.model.User;
import dev.examproject.repository.util.TurboLogger;
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

    private static final TurboLogger log = new TurboLogger(WebController.class);

    private final UserService userService;
    private final ProjectService projectService;
    private final TaskService taskService;

    public WebController(UserService userService, ProjectService projectService, TaskService taskService) {
        this.userService = userService;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    // checker at der er en bruger logget ind og at det er den rigtige bruger.
    public boolean isLoggedIn(HttpSession session, String username) {
        return session.getAttribute("user") != null
                && ((User) session.getAttribute("user")).getUsername().equals(username);
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
        Project mainProject = (Project) session.getAttribute("selectedProject");
        if (mainProject != null) {
            // TODO: det her skal skrives om når task og project er merget i samme repo.
            List<Project> subProjects = projectService.getSubProjectsForProject(mainProject.getProjectId());
            for (Project subProject : subProjects) {
                subProject.setTasks(taskService.getProjectTasks(subProject.getProjectId()));
            }
            model.addAttribute("project", mainProject);
            model.addAttribute("subProjects", subProjects);
            model.addAttribute("totalHoursForProject", projectService.getTotalRequiredHoursForAllSubProjects(mainProject.getProjectId()));
        }
        if (isLoggedIn(session, username)) {
            model.addAttribute("user", authenticatedUser);
            return "overview";
        }
        return "redirect:/login";
    }

    // Metode til både at adde en bruger til project eller subproject
    // alt efter hvilken type der er valgt.
    // hold kæft hvor er den lang. ser senere om den kan forkortes lidt :)
    // mads
    @PostMapping(path = "/{username}/assignuserto{type}")
    public String addUserToProject(@PathVariable("username") String username,
                                   @PathVariable("type") String type,
                                   @ModelAttribute("user") User user, HttpSession session) {
        if (isLoggedIn(session, username)) {
            Project selectedProject;
            // check hvilken "type" der er valgt
            if ("Project".equalsIgnoreCase(type)) {
                selectedProject = (Project) session.getAttribute("selectedProject");
            } else if ("SubProject".equalsIgnoreCase(type)) {
                selectedProject = (Project) session.getAttribute("selectedSubProject");
                Project parentProject = projectService.getProject(selectedProject.getParentProjectID());
                if (parentProject.getAssignedUsers().stream().noneMatch(u -> u.getUsername().equals(user.getUsername()))) { // check om bruger er medlem af main project
                    log.info("User is not a member of main project.");
                    return "redirect:/" + username + "/subprojectoverview";
                }
            } else {
                return "redirect:/login";
            }
            userService.addUserToProject(user, selectedProject.getProjectId());

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

    // samme som ovenover.
    // mads
    @GetMapping(path = "/{username}/select{type}/{projectId}")
    public String selectProject(@PathVariable("type") String type,
                                @PathVariable("username") String username,
                                @PathVariable("projectId") int projectId,
                                HttpSession session) {
        if (isLoggedIn(session, username)) {
            Project project;
            if ("Project".equalsIgnoreCase(type)) {
                project = projectService.getProject(projectId);
                session.setAttribute("selectedProject", project);
            } else if ("SubProject".equalsIgnoreCase(type)) {
                project = projectService.getProject(projectId);
                session.setAttribute("selectedSubProject", project);
            }
            return "redirect:/" + username + (type.equalsIgnoreCase("Project") ? "/overview" : "/subprojectoverview");
        }
        return "redirect:/login";
    }

    // mads
    @GetMapping(path = "/{username}/projects")
    public String projects(@PathVariable("username") String username, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (isLoggedIn(session, username)) {
            model.addAttribute("user", authenticatedUser);
            int userId = userService.getUserId(username);
            model.addAttribute("projects", projectService.getProjectsForUser(userId));
            return "projects";
        }
        return "redirect:/login";
    }

    @GetMapping(path = "/{username}/add{type}project")
    public String addProject(@PathVariable("username") String username,
                             @PathVariable("type") String type,
                             Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (isLoggedIn(session, username)) {
            model.addAttribute("user", authenticatedUser);
            if ("sub".equalsIgnoreCase(type)) {
                model.addAttribute("subProject", new Project());
            } else {
                model.addAttribute("project", new Project());
            }
            return "add" + type + "project";
        }
        return "redirect:/login";
    }


    @PostMapping(path = "/{username}/add{type}project")
    public String addProject(@PathVariable("username") String username,
                             @PathVariable("type") String type,
                             @ModelAttribute("project") Project project, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (isLoggedIn(session, username)) {
            if ("Sub".equalsIgnoreCase(type)) {
                Project selectedProject = (Project) session.getAttribute("selectedProject");
                if (selectedProject != null) {
                    project.setParentProjectID(selectedProject.getProjectId());
                    projectService.addProject(project);
                    return "redirect:/" + username + "/overview";
                }
            } else {
                projectService.addProject(project);
            }
            userService.addUserToProject(authenticatedUser, project.getProjectId());
            userService.setUserToAdmin(username, project.getProjectId());
            return "redirect:/" + username + "/projects";
        }
        return "redirect:/login";
    }

    @GetMapping(path = "/{username}/addtask")
    public String addTask(@PathVariable("username") String username, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (isLoggedIn(session, username)) {
            model.addAttribute("user", authenticatedUser);
            model.addAttribute("task", new Task());
            model.addAttribute("project", session.getAttribute("selectedProject"));
            return "addtask";
        }
        return "redirect:/login";
    }

    @PostMapping(path = "/{username}/addtask")
    public String addTask(@PathVariable("username") String username,
                          @ModelAttribute("task") Task task, HttpSession session) {
        if (isLoggedIn(session, username)) {
            Project selectedSubProject = (Project) session.getAttribute("selectedSubProject");
            if (selectedSubProject != null) {
                task.setProjectId(selectedSubProject.getProjectId());
                taskService.addTask(task);
            }
            System.out.println("task tilføjet:" + task);
            return "redirect:/" + username + "/subprojectoverview";
        }
        return "redirect:/login";
    }

    @GetMapping(path = "/{username}/subprojectoverview")
    public String subProjectOverview(@PathVariable("username") String username,
                                     HttpSession session, Model model) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (isLoggedIn(session, username)) {
            Project selectedSubProject = (Project) session.getAttribute("selectedSubProject");
            Project selectedProject = (Project) session.getAttribute("selectedProject");
            if (selectedSubProject != null) {
                List<Task> tasks = taskService.getProjectTasks(selectedSubProject.getProjectId());
                model.addAttribute("selectedProject", selectedProject);
                model.addAttribute("tasks", tasks);
                model.addAttribute("project", selectedSubProject);
                model.addAttribute("user", authenticatedUser);
                model.addAttribute("totalHoursForProject", taskService.getTotalRequiredHoursForProject(selectedSubProject.getProjectId()));
                return "subprojectoverview";
            }
        }
        return "redirect:/login";
    }

    @GetMapping(path = "/{username}/assignselftotask/{taskId}")
    public String assignSelfToTask(@PathVariable("username") String username,
                                   @PathVariable("taskId") int taskId,
                                   @ModelAttribute("user") User user, HttpSession session) {
        Project mainProject = (Project) session.getAttribute("selectedProject");
        Project subProject = (Project) session.getAttribute("selectedSubProject");
        User authenticatedUser = (User) session.getAttribute("user");
        if (isLoggedIn(session, username)) {
            int userId = userService.getUserId(user.getUsername());
            // check at bruger er medlem af main project og subprojekt
            if (mainProject.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(authenticatedUser.getUsername()))
                    && subProject.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(authenticatedUser.getUsername()))) {
                taskService.assignSelfToTask(taskId, userId);
            } else log.info("User not assigned to project.");
            return "redirect:/" + username + "/subprojectoverview";
        }
        return "redirect:/login";
    }

    @PostMapping(path = "/{username}/assignusertotask/{taskId}")
    public String assignUserToTask(@PathVariable("username") String username,
                                   @PathVariable("taskId") int taskId,
                                   @ModelAttribute("user") User user,
                                   @RequestParam("assignedUsername") String assignedUsername, HttpSession session) {
        Project mainProject = (Project) session.getAttribute("selectedProject");
        Project subProject = (Project) session.getAttribute("selectedSubProject");
        if (isLoggedIn(session, username)) {
            int userId = userService.getUserId(assignedUsername);
            // check at bruger er medlem af main project og subprojekt
            if (mainProject.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(assignedUsername))
                    && subProject.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(assignedUsername))) {
                taskService.assignUserToTask(taskId, userId);
            } else log.info("User not assigned to project.");
            return "redirect:/" + username + "/subprojectoverview";
        }
        return "redirect:/login";
    }

    @GetMapping("/{username}/editproject/{projectId}")
    public String showEditProjectForm(@PathVariable("username") String username,
                                      @PathVariable("projectId") int projectId, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        System.out.println("Attempting to edit project with ID: " + projectId + " for user: " + username);
        if (isLoggedIn(session, username)) {
            Project project = projectService.getProject(projectId);
            if (project != null) {
                model.addAttribute("project", project);
                model.addAttribute("user", authenticatedUser);
                return "editproject";
            } else {
                return "redirect:/" + username + "/projects";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/{username}/updateproject/{projectId}")
    public String updateProject(@PathVariable("username") String username,
                                @PathVariable("projectId") int projectId,
                                @ModelAttribute("project") Project project, HttpSession session) {
        if (isLoggedIn(session, username)) {
            int success = projectService.updateProject(project);
            if (success == 1) {
                Project updatedProject = projectService.getProject(projectId);
                if (project.getParentProjectID() == 0) {
                    session.setAttribute("selectedProject", updatedProject);
                    return "redirect:/" + username + "/projects";
                } else {
                    session.setAttribute("selectedSubProject", updatedProject);
                    return "redirect:/" + username + "/subprojectoverview";
                }
            } else {
                return "redirect:/" + username + "/editproject" + projectId;
            }
        }
        return "redirect:/login";
    }

    @GetMapping("/{username}/edittask/{taskId}")
    public String showEditTaskForm(@PathVariable("username") String username, @PathVariable("taskId") int taskId, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (isLoggedIn(session, username)) {
            Task task = taskService.getTask(taskId);
            if (task != null) {
                model.addAttribute("task", task);
                model.addAttribute("user", authenticatedUser);
                return "edittask";
            } else {
                return "redirect:/" + username + "/subprojectoverview";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/{username}/updatetask/{taskId}")
    public String updateTask(@PathVariable("username") String username, @PathVariable("taskId") int taskId, @ModelAttribute("task") Task task, RedirectAttributes redirectAttributes, HttpSession session) {
        if (isLoggedIn(session, username)) {
            task.setTaskId(taskId);
            int updateResult = taskService.updateTask(task);
            if (updateResult == 1) {
                redirectAttributes.addFlashAttribute("successMessage", "Task updated successfully");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Error updating task");
            }
            return "redirect:/" + username + "/subprojectoverview";
        }
        return "redirect:/login";
    }

    //-----------------------------------------------------------------Delete--------------------------
    @GetMapping("/{username}/confirmdeletetask/{taskId}")
    public String confirmDeleteTask(@PathVariable("username") String username, @PathVariable("taskId") int taskId, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (isLoggedIn(session, username)) {
            Task task = taskService.getTask(taskId);
            if (task != null) {
                model.addAttribute("task", task);
                model.addAttribute("user", authenticatedUser);
                return "confirmdeletetask";
            }
        }
        return "redirect:/login";
    }

    @PostMapping("/{username}/deletetask/{taskId}")
    public String deleteTask(@PathVariable("username") String username, @PathVariable("taskId") int taskId, HttpSession session) {
        if (isLoggedIn(session, username)) {
            taskService.deleteTask(taskId);
            return "redirect:/" + username + "/subprojectoverview";
        }
        return "redirect:/login";
    }
    @GetMapping("/{username}/confirmdeleteproject/{projectId}")
    public String confirmDeleteProject(@PathVariable("username") String username, @PathVariable("projectId") int projectId, Model model, HttpSession session) {
        User authenticatedUser = (User) session.getAttribute("user");
        if (isLoggedIn(session, username)) {
            Project project = projectService.getProject(projectId);
            if (project != null) {
                model.addAttribute("project", project);
                model.addAttribute("user", authenticatedUser);
                if (project.getParentProjectID() > 0) {
                    return "confirmdeleteproject";
                } else {
                    return "confirmdeletesubproject";
                }
            }
        }
        return "redirect:/login";

    }

    // måske ikke den mest optimale måde at gøre det her på. ikke helt sikker.
    // men tænker ikke det er så tit man sletter et projekt, så hvis det tager et sekund
    // eller to længere er det nok ikke det største performance hit.
    // /mads
    @PostMapping("/{username}/deleteproject/{projectId}")
    public String deleteProject(@PathVariable("username") String username,
                                @PathVariable("projectId") int projectId, HttpSession session) {
        if (isLoggedIn(session, username)) {
            taskService.removeTaskUsersForProject(projectId); // removes assigned users from tasks
            taskService.deleteTasksForProject(projectId); // removes tasks
            userService.removeUsersFromProject(projectId); // removes users from project
            projectService.deleteSubProjects(projectId); // deletes subprojects
            projectService.deleteProject(projectId); // deletes the main project
            return "redirect:/" + username + "/projects";
        }
        return "redirect:/login";
    }


    @PostMapping("/{username}/deletesubproject/{projectId}")
    public String deleteSubProject(@PathVariable("username") String username,
                                   @PathVariable("projectId") int projectId, HttpSession session) {
        if (isLoggedIn(session, username)) {
            taskService.removeTaskUsersForProject(projectId); // removes assigned users from tasks
            taskService.deleteTasksForProject(projectId); // removes tasks
            userService.removeUsersFromProject(projectId); // removes users from project
            projectService.deleteProject(projectId); // deletes the subproject
            return "redirect:/" + username + "/overview";
        }
        return "redirect:/login";
    }


}



