package com.ktdsuniversity.edu.members.service;

import com.ktdsuniversity.edu.members.vo.request.RegistMembersVO;
import com.ktdsuniversity.edu.members.vo.response.MembersVO;

import jakarta.validation.Valid;

public interface MembersService {

	MembersVO createNewMember(@Valid RegistMembersVO registMembersVO);

}