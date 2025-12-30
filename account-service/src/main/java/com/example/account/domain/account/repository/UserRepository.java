package com.example.account.domain.account.repository;

import com.example.account.domain.account.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User>{
    User findByUsername(String username);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User u SET u.otpSecret = :otpSecret, u.otpDeletionRequested = :deletionRequested WHERE u.id = :id")
    int updateOtpStatus(
        @Param("id") Long id, 
        @Param("otpSecret") String otpSecret, 
        @Param("deletionRequested") Boolean deletionRequested
    );
}
