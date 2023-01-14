package com.example.account.core.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
public abstract class ActiveRecord extends DateTimeRecord {
    @Column(name = "ar_active_start", nullable = true)
    private Date activeStart;

    @Column(name = "ar_active_end", nullable = true)
    private Date activeEnd;
}
