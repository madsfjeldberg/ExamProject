package dev.examproject.controller;

import dev.examproject.model.Project;
import dev.examproject.model.Task;
import dev.examproject.model.User;
import dev.examproject.service.TaskService;
import dev.examproject.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequestMapping(path = "")
@Controller
public class TaskController {

    private final UserService userService;
    private final TaskService taskService;

    public TaskController(UserService userService, TaskService taskService) {
        this.userService = userService;
        this.taskService = taskService;
    }

    // checker at der er en bruger logget ind og at det er den rigtige bruger.
    public boolean isLoggedIn(HttpSession session, String username) {
        return session.getAttribute("user") != null && ((User) session.getAttribute("user")).getUsername().equals(username);
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

    @GetMapping(path = "/{username}/assignselftotask/{taskId}")
    public String assignSelfToTask(@PathVariable("username") String username,
                                   @PathVariable("taskId") int taskId,
                                   @ModelAttribute("user") User user,
                                   HttpSession session, RedirectAttributes attributes) {
        Project mainProject = (Project) session.getAttribute("selectedProject");
        Project subProject = (Project) session.getAttribute("selectedSubProject");
        User authenticatedUser = (User) session.getAttribute("user");
        if (isLoggedIn(session, username)) {
            int userId = userService.getUserId(user.getUsername());
            // check at bruger er medlem af main project og subprojekt
            if (mainProject.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(authenticatedUser.getUsername()))
                    && subProject.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(authenticatedUser.getUsername()))) {
                // check at bruger ikke allerede er assigned til tasken.
                Task task = taskService.getTask(taskId);
                if (task.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(authenticatedUser.getUsername()))) {
                    attributes.addFlashAttribute("errorMessage", "Brugeren er allerede tildelt opgaven.");
                    return "redirect:/" + username + "/subprojectoverview";
                }
                taskService.assignSelfToTask(taskId, userId);
            } else {
                attributes.addFlashAttribute("errorMessage", "Brugeren er ikke en del af projektet.");
            }
            return "redirect:/" + username + "/subprojectoverview";
        }
        return "redirect:/login";
    }

    @PostMapping(path = "/{username}/assignusertotask/{taskId}")
    public String assignUserToTask(@PathVariable("username") String username,
                                   @PathVariable("taskId") int taskId,
                                   @ModelAttribute("user") User user,
                                   @RequestParam("assignedUsername") String assignedUsername, HttpSession session,
                                   RedirectAttributes attributes) {
        Project mainProject = (Project) session.getAttribute("selectedProject");
        Project subProject = (Project) session.getAttribute("selectedSubProject");
        if (!isLoggedIn(session, username)) {
            return "redirect:/login";
        }
        int userId = userService.getUserId(assignedUsername);
        // check at bruger er medlem af main project og subprojekt
        if (mainProject.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(assignedUsername))
                && subProject.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(assignedUsername))) {
            // check at bruger ikke allerede er assigned til tasken.
            Task task = taskService.getTask(taskId);
            if (task.getAssignedUsers().stream().anyMatch(u -> u.getUsername().equals(assignedUsername))) {
                attributes.addFlashAttribute("errorMessage", "Brugeren er allerede tildelt opgaven.");
                return "redirect:/" + username + "/subprojectoverview";
            }
            taskService.assignUserToTask(taskId, userId);
            attributes.addFlashAttribute("successMessage", "Brugeren er blevet tildelt opgaven.");
            return "redirect:/" + username + "/subprojectoverview";
        } else {
            attributes.addFlashAttribute("errorMessage", "Brugeren er ikke en del af projektet.");
        }
        attributes.addFlashAttribute("errorMessage", "Brugeren er ikke en del af projektet.");
        return "redirect:/" + username + "/subprojectoverview";
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


    @PostMapping("/{username}/deletetask/{taskId}")
    public String deleteTask(@PathVariable("username") String username, @PathVariable("taskId") int taskId, HttpSession session) {
        if (isLoggedIn(session, username)) {
            taskService.removeTaskUsers(taskId);
            taskService.deleteTask(taskId);
            return "redirect:/" + username + "/subprojectoverview";
        }
        return "redirect:/login";
    }


}
