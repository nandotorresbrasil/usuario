package com.javanauta.usuario.infrastructure.repository;

import com.javanauta.aprendento_spring.infraestructure.entity.Telefone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelefoneRepository extends JpaRepository<Telefone, Long> {


}
