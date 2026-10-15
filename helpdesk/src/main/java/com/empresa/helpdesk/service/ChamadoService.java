package com.empresa.helpdesk.service;

import com.empresa.helpdesk.model.Chamado;
import com.empresa.helpdesk.model.Usuario;
import com.empresa.helpdesk.repository.ChamadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChamadoService {

    @Autowired
    private ChamadoRepository chamadoRepository;

    public List<Chamado> listarTodos() {
        return chamadoRepository.findAll();
    }

    public Chamado buscarPorId(Long id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado com ID: " + id));
    }

    public List<Chamado> listarPorCliente(Long clienteId) {
        return chamadoRepository.findAll().stream()
                .filter(c -> c.getCliente() != null && c.getCliente().getId().equals(clienteId))
                .toList();
    }

    public List<Chamado> listarPorCliente(Usuario cliente) {
        if (cliente == null) return List.of();
        return listarPorCliente(cliente.getId());
    }

    public Chamado salvar(Chamado chamado) {
        return chamadoRepository.save(chamado);
    }

    public Chamado criarChamado(Chamado chamado, Usuario cliente) {
        chamado.setCliente(cliente);
        chamado.setStatus("ABERTO");
        return chamadoRepository.save(chamado);
    }

    public Chamado assumirChamado(Long chamadoId, Usuario tecnico) {
        Chamado chamado = buscarPorId(chamadoId);
        chamado.setTecnico(tecnico);
        chamado.setStatus("EM_ANDAMENTO");
        return chamadoRepository.save(chamado);
    }

    public Chamado adicionarResposta(Long chamadoId, String resposta, String status, Usuario tecnico) {
        Chamado chamado = buscarPorId(chamadoId);

        String historicoAtual = chamado.getRespostaTecnica() != null ? chamado.getRespostaTecnica() : "";
        if (!historicoAtual.isEmpty()) {
            historicoAtual += "\n\n";
        }

        String novaInteracao = "[" + tecnico.getNome() + " (Técnico)]: " + resposta;
        chamado.setRespostaTecnica(historicoAtual + novaInteracao);

        if (status != null && !status.isEmpty()) {
            chamado.setStatus(status);
        }

        return chamadoRepository.save(chamado);
    }

    public Chamado clienteResponder(Long chamadoId, String novaMensagem, Usuario cliente) {
        Chamado chamado = buscarPorId(chamadoId);

        String historicoAtual = chamado.getRespostaTecnica() != null ? chamado.getRespostaTecnica() : "";
        if (!historicoAtual.isEmpty()) {
            historicoAtual += "\n\n";
        }

        String novaInteracao = "[" + cliente.getNome() + " (Cliente)]: " + novaMensagem;
        chamado.setRespostaTecnica(historicoAtual + novaInteracao);

        if ("CONCLUIDO".equals(chamado.getStatus())) {
            chamado.setStatus("EM_ANDAMENTO");
        }

        return chamadoRepository.save(chamado);
    }
}