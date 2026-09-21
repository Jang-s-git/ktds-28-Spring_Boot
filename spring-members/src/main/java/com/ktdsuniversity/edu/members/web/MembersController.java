package com.ktdsuniversity.edu.members.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ktdsuniversity.edu.members.service.MembersService;
import com.ktdsuniversity.edu.members.vo.response.MemberListVO;
import com.ktdsuniversity.edu.members.vo.response.MembersVO;

@Controller
public class MembersController {

	private MembersService membersService;
	
	public MembersController(MembersService membersService) {
		this.membersService = membersService;
	}
	
	@GetMapping("/members")
	@ResponseBody
	public MemberListVO getMembers() {
		return this.membersService.readAllMembers();
	}
	
}
