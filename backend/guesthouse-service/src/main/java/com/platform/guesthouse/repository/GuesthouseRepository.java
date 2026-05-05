package com.platform.guesthouse.repository;

import com.platform.guesthouse.model.Guesthouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuesthouseRepository extends JpaRepository<Guesthouse, String> {
    List<Guesthouse> findByActiveTrue();
}
