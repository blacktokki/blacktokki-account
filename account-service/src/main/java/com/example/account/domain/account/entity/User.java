package com.example.account.domain.account.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.example.account.core.entity.AbstractUser;

import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "`user`")
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends AbstractUser{
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(
        name = "membership",
        joinColumns = @JoinColumn(name = "us_id"),
        inverseJoinColumns = @JoinColumn(name = "gr_id")
    )
    private List<Group> groupList = new ArrayList<>();
}
