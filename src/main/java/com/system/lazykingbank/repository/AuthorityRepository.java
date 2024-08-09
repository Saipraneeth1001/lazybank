package com.system.lazykingbank.repository;

import com.system.lazykingbank.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    Authority save(Authority authority);
}
