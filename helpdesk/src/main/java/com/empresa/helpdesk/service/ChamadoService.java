
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

    public Chamado criarChamado(Chamado chamado, Usuario cliente) {
        chamado.setCliente(cliente);
        chamado.setStatus("ABERTO");
        return chamadoRepository.save(chamado);
    }

    public List<Chamado> listarTodos() {
        return chamadoRepository.findAll();
    }

    public List<Chamado> listarPorCliente(Long clienteId) {
        return chamadoRepository.findByClienteId(clienteId);
    }

    public Chamado buscarPorId(Long id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chamado não encontrado."));
    }

    public Chamado assumirChamado(Long chamadoId, Usuario tecnico) {
        Chamado chamado = buscarPorId(chamadoId);
        chamado.setTecnico(tecnico);
        chamado.setStatus("EM_ANDAMENTO");
        return chamadoRepository.save(chamado);
    }

    // Resposta enviada pelo Técnico (com nome real)
    public Chamado adicionarResposta(Long chamadoId, String respostaTecnica, String novoStatus, Usuario tecnico) {
        Chamado chamado = buscarPorId(chamadoId);

        String historicoAtual = chamado.getRespostaTecnica() != null ? chamado.getRespostaTecnica() : "";
        if (!historicoAtual.isEmpty()) {
            historicoAtual += "\n\n";
        }

        String novaInteracao = "[" + tecnico.getNome() + " (Técnico)]: " + respostaTecnica;
        chamado.setRespostaTecnica(historicoAtual + novaInteracao);

        if (novoStatus != null && !novoStatus.isEmpty()) {
            chamado.setStatus(novoStatus);
        }
        return chamadoRepository.save(chamado);
    }


}