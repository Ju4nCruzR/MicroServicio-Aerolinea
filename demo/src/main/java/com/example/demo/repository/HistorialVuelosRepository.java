package com.example.demo.repository;

import com.example.demo.entity.HistorialVuelos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialVuelosRepository extends JpaRepository<HistorialVuelos, String> {
}