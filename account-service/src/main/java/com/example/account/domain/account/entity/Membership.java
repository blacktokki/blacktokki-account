package com.example.account.domain.account.entity;


import javax.persistence.Entity;
import javax.persistence.Table;

import com.example.account.core.entity.DateTimeRecord;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.AccessLevel;

@Entity
@Table(name = "membership")
@Data
@EqualsAndHashCode(callSuper = false)
public class Membership extends DateTimeRecord{
    @Id
    @Column(name = "mb_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "us_id", updatable = false, insertable = false)
    private User user;

    @Column(name = "us_id", nullable = true)
    private Long userId;

    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "gr_id", updatable = false, insertable = false)
    private Group group;

    @Column(name = "gr_id", nullable = true)
    private Long groupId;
}
