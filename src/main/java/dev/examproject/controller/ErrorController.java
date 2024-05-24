package dev.examproject.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController{
    @GetMapping("/error")
    public String handlerError(HttpServletRequest request) {
        String error = "error";

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                error = "Fejl/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                error = "Fejl/500";
            }
        }
        return error;
    }
}
