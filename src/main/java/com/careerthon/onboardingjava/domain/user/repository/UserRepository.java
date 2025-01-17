package com.careerthon.onboardingjava.domain.user.repository;

import com.careerthon.onboardingjava.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
