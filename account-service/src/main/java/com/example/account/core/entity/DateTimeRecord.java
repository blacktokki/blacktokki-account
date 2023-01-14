package com.example.account.core.entity;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@MappedSuperclass
public abstract class DateTimeRecord {
    @CreationTimestamp
    @Column(name = "dt_created")
    private ZonedDateTime created;
    
    @UpdateTimestamp
    @Column(name = "dt_updated")
    private ZonedDateTime updated;
}
