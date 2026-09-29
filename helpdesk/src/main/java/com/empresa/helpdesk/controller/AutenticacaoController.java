package com.empresa.helpdesk.controller;

import com.empresa.helpdesk.model.Usuario;
import com.empresa.helpdesk.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AutenticacaoController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/")
    public String telaAcesso(Model model, HttpSession session) {
        // Se o usuário já estiver autenticado na sessão, redireciona para o dashboard
        if (session.getAttribute("usuarioLogado") != null) {
            return "redirect:/dashboard";
        }
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new Usuario());
        }
        return "acesso";
    }

    @PostMapping("/cadastrar")
    public String cadastrar(@ModelAttribute("usuario") Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            // Chama o método que força o perfil CLIENTE para cadastros públicos
            usuarioService.cadastrarCliente(usuario);
            redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso! Faça seu login.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cadastrar: " + e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String senha,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.autenticar(email, senha);
            session.setAttribute("usuarioLogado", usuario);
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}