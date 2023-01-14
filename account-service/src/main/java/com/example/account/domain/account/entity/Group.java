package com.example.account.domain.account.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.example.account.core.entity.DateTimeRecord;

import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "`group`")
@Data
@EqualsAndHashCode(callSuper = false)
public class Group extends DateTimeRecord{
    @Id
    @Column(name = "gr_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "gr_root_id")
    private Long rootId;

    @Column(name = "gr_parent_id")
    private Long parentId;

    @Column(name = "gr_name")
    private String name;

    @Column(name = "gr_image_url")
    private String imageUrl;

    @OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "gr_id")
    private List<Membership> membershipList = new ArrayList<>();
}
