package com.example.account.domain.account.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.example.account.core.entity.AbstractUser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "`user`")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends AbstractUser{
    @Column(name = "us_oauth")
    String oauth;
}
