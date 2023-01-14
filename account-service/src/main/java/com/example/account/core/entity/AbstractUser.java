package com.example.account.core.entity;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@MappedSuperclass
public abstract class AbstractUser extends DateTimeRecord{
    @Id
    @Column(name = "us_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "us_name")
    private String name;
    
    @Column(name = "us_username")
    private String username;
    
    @Column(name = "us_password")
    private String password;

    @Column(name = "us_is_admin")
    private Boolean isAdmin;

    @Column(name = "us_is_guest")
    private Boolean isGuest;

    @Column(name = "us_image_url")
    private String imageUrl;
}
