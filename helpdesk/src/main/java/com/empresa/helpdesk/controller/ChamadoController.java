package com.empresa.helpdesk.controller;

import com.empresa.helpdesk.model.Chamado;
import com.empresa.helpdesk.model.Perfil;
import com.empresa.helpdesk.model.Usuario;
import com.empresa.helpdesk.service.ChamadoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/chamados")
public class ChamadoController {

    @Autowired
    private ChamadoService chamadoService;

    @PostMapping("/novo")
    public String abrirChamado(@ModelAttribute Chamado chamado, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario clienteLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (clienteLogado == null) return "redirect:/";

        try {
            chamadoService.criarChamado(chamado, clienteLogado);
            redirectAttributes.addFlashAttribute("sucesso", "Chamado aberto com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao abrir chamado: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/tecnico/assumir/{id}")
    public String assumirChamado(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario tecnicoLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (tecnicoLogado == null || !Perfil.TECNICO.equals(tecnicoLogado.getPerfil()) && !Perfil.ADMIN.equals(tecnicoLogado.getPerfil())) {
            return "redirect:/";
        }

        try {
            chamadoService.assumirChamado(id, tecnicoLogado);
            redirectAttributes.addFlashAttribute("sucesso", "Chamado assumido com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao assumir chamado: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/tecnico/responder/{id}")
    public String responderChamado(@PathVariable Long id,
                                   @RequestParam("respostaTecnica") String respostaTecnica,
                                   @RequestParam(value = "status", required = false) String status,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Usuario tecnicoLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (tecnicoLogado == null) return "redirect:/";

        try {
            chamadoService.adicionarResposta(id, respostaTecnica, status, tecnicoLogado);
            redirectAttributes.addFlashAttribute("sucesso", "Resposta enviada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao responder: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/cliente/responder/{id}")
    public String clienteResponder(@PathVariable Long id,
                                   @RequestParam("mensagem") String mensagem,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        Usuario clienteLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (clienteLogado == null) return "redirect:/";

        try {
            chamadoService.clienteResponder(id, mensagem, clienteLogado);
            redirectAttributes.addFlashAttribute("sucesso", "Mensagem enviada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao enviar mensagem: " + e.getMessage());
        }
        return "redirect:/dashboard";
    }
}