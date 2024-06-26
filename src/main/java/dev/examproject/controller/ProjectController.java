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

import java.util.List;

@Controller
@RequestMapping(path = "")
public class ProjectController {

    private final UserService userService;
    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectController(UserService userService, ProjectService projectService, TaskService taskService) {
        this.userService = userService;
        this.projectService = projectService;
        this.taskService = taskService;
    }

    public boolean isLoggedIn(HttpSession session, String username) {
        return session.getAttribute("user") != null && ((User) session.getAttribute("user")).getUsername().equals(username);
    }

    @GetMapping(path = "/{username}/overview")
    public String overview(@PathVariable("username") String username, Model model, HttpSession session) {
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
            model.addAttribute("user", session.getAttribute("user"));
            return "overview";
        }
        return "redirect:/login";
    }

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
        int userId = userService.getUserId(username);
        List<Project> projects = projectService.getProjectsForUser(userId);
        boolean isAdmin = projects.stream().anyMatch(p -> p.getAdmin().equals(username));
        if (isLoggedIn(session, username)) {
            model.addAttribute("user", authenticatedUser);
            model.addAttribute("projects", projects);
            model.addAttribute("isAdmin", isAdmin);
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

    // har ændret den her til at være get i stedet for post
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
