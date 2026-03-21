package com.competitive.games.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GameUIController {

    @GetMapping("/math-arena")
    public String showMathArena(HttpSession session, Model model) {
        return checkAuth(session, model, "math-arena");
    }

    @GetMapping("/code-breaker")
    public String showCodeBreaker(HttpSession session, Model model) {
        return checkAuth(session, model, "code-breaker");
    }

    @GetMapping("/react-predict")
    public String showReactPredict(HttpSession session, Model model) {
        return checkAuth(session, model, "react-predict");
    }

    @GetMapping("/memory-grid")
    public String showMemoryGrid(HttpSession session, Model model) {
        return checkAuth(session, model, "memory-grid");
    }

    @GetMapping("/equation-builder")
    public String showEquationBuilder(HttpSession session, Model model) {
        return checkAuth(session, model, "equation-builder");
    }

    @GetMapping("/bluff-detect")
    public String showBluffDetect(HttpSession session, Model model) {
        return checkAuth(session, model, "bluff-detect");
    }

    @GetMapping("/graph-domain")
    public String showGraphDomain(HttpSession session, Model model) {
        return checkAuth(session, model, "graph-domain");
    }

    @GetMapping("/ai-logic")
    public String showAILogic(HttpSession session, Model model) {
        return checkAuth(session, model, "ai-logic");
    }

    private String checkAuth(HttpSession session, Model model, String template) {
        if (session.getAttribute("userId") == null) {
            return "redirect:/login";
        }
        model.addAttribute("username", session.getAttribute("username"));
        return template;
    }
}
