package com.competitive.games.controller;

import com.competitive.games.model.User;
import com.competitive.games.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

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
        try {
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                model.addAttribute("error", "All fields are required");
                return "register";
            }

            if (userRepository.findByEmail(email).isPresent()) {
                model.addAttribute("error", "Email already exists");
                return "register";
            }
            if (userRepository.findByUsername(username).isPresent()) {
                model.addAttribute("error", "Username already exists");
                return "register";
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password);

            User savedUser = userRepository.save(newUser);
            session.setAttribute("userId", savedUser.getId());
            session.setAttribute("username", savedUser.getUsername());
            
            return "redirect:/dashboard";
        } catch (Exception e) {
            try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("c:/Users/acer/OneDrive/Desktop/game/debug.log", true))) {
                pw.println("ERROR at Register: " + new java.util.Date() + " - " + e.toString());
                e.printStackTrace(pw);
            } catch (java.io.IOException ioe) {}
            model.addAttribute("error", "Server Error: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(HttpSession session) {
        if (session.getAttribute("userId") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam("username") String username, @RequestParam("password") String password, Model model,
            HttpSession session) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            session.setAttribute("userId", userOpt.get().getId());
            session.setAttribute("username", userOpt.get().getUsername());
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
