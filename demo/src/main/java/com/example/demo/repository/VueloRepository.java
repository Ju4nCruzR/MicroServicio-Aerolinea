package com.example.demo.repository;

import com.example.demo.entity.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface VueloRepository extends JpaRepository<Vuelo, UUID> {
}