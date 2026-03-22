package com.competitive.games.controller;

import com.competitive.games.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class UserController {

    // 🔥 Temporary in-memory storage
    private Map<String, User> usersByUsername = new HashMap<>();
    private Map<String, User> usersByEmail = new HashMap<>();

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model, HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam("username") String username,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password,
                               Model model, HttpSession session) {

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "All fields are required");
            return "register";
        }

        if (usersByEmail.containsKey(email)) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }

        if (usersByUsername.containsKey(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        // Create user
        User newUser = new User();
        newUser.setId((long) (usersByUsername.size() + 1)); // fake ID
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password);

        // Save in memory
        usersByUsername.put(username, newUser);
        usersByEmail.put(email, newUser);

        session.setAttribute("userId", newUser.getId());
        session.setAttribute("username", newUser.getUsername());

        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username,
                            @RequestParam("password") String password,
                            Model model,
                            HttpSession session) {

        User user = usersByUsername.get(username);

        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            return "redirect:/dashboard";
        }

        model.addAttribute("error", "Invalid username or password");
        return "login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", session.getAttribute("username"));
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
