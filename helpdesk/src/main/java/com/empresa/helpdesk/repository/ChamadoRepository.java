package com.empresa.helpdesk.repository;

import com.empresa.helpdesk.model.Chamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChamadoRepository extends JpaRepository<Chamado, Long> {

    // Lista chamados de um cliente específico
    List<Chamado> findByClienteId(Long clienteId);

    // Lista chamados por status (ex: úteis para a fila do técnico)
    List<Chamado> findByStatus(String status);
}