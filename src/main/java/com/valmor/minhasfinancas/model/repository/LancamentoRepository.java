package com.valmor.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.valmor.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {

}
