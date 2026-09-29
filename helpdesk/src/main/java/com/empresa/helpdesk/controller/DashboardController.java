package com.empresa.helpdesk.controller;

import com.empresa.helpdesk.model.Perfil;
import com.empresa.helpdesk.model.Usuario;
import com.empresa.helpdesk.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            return "redirect:/";
        }

        model.addAttribute("usuarioLogado", usuarioLogado);

        // Se for ADMINISTRADOR, carrega a lista de todos os usuários para o painel de gestão
        if (usuarioLogado.getPerfil() == Perfil.ADMIN) {
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("novoTecnico", new Usuario());
        }

        return "dashboard";
    }

    // Rota para o ADMIN cadastrar um novo Técnico ou Admin
    @PostMapping("/admin/cadastrar-tecnico")
    public String cadastrarTecnico(@ModelAttribute("novoTecnico") Usuario novoTecnico,
                                   @RequestParam("perfilDesejado") Perfil perfilDesejado,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null || usuarioLogado.getPerfil() != Perfil.ADMIN) {
            return "redirect:/dashboard";
        }

        try {
            usuarioService.cadastrarPorAdmin(novoTecnico, perfilDesejado);
            redirectAttributes.addFlashAttribute("sucesso", "Técnico/Usuário cadastrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao cadastrar: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }

    // Rota para o ADMIN excluir um usuário (com proteção contra auto-exclusão)
    @PostMapping("/admin/excluir/{id}")
    public String excluirUsuario(@PathVariable Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null || usuarioLogado.getPerfil() != Perfil.ADMIN) {
            return "redirect:/dashboard";
        }

        try {
            usuarioService.excluirUsuario(id, usuarioLogado.getId());
            redirectAttributes.addFlashAttribute("sucesso", "Usuário removido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        return "redirect:/dashboard";
    }

    // Rota para resetar/alterar a própria senha
    @PostMapping("/alterar-senha")
    public String alterarSenha(@RequestParam("novaSenha") String novaSenha,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            return "redirect:/";
        }

        try {
            // Atualiza a senha na entidade e salva via service
            usuarioService.alterarSenha(usuarioLogado.getId(), novaSenha);

            // Atualiza a senha no objeto da sessão
            usuarioLogado.setSenha(novaSenha);
            session.setAttribute("usuarioLogado", usuarioLogado);

            redirectAttributes.addFlashAttribute("sucesso", "Senha alterada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao alterar senha: " + e.getMessage());
        }

        return "redirect:/dashboard";
    }
}