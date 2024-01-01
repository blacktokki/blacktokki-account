package com.example.account.domain.account.controller;

import com.example.account.core.controller.JpaController;
import com.example.account.domain.account.dto.GroupQueryParam;
import com.example.account.domain.account.dto.GroupMembershipDto;
import com.example.account.domain.account.entity.Group;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/group")
public class GroupController extends JpaController<GroupMembershipDto, Group, GroupQueryParam, Long>{}