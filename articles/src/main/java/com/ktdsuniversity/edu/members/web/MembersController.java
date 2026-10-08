package com.ktdsuniversity.edu.members.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ktdsuniversity.edu.commons.util.ApiResponse;
import com.ktdsuniversity.edu.members.service.MembersService;
import com.ktdsuniversity.edu.members.vo.request.LoginMemberVO;
import com.ktdsuniversity.edu.members.vo.request.RegistMembersVO;
import com.ktdsuniversity.edu.members.vo.request.SearchMemberVO;
import com.ktdsuniversity.edu.members.vo.response.MemberListVO;
import com.ktdsuniversity.edu.members.vo.response.MembersVO;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class MembersController {

	private static final Logger logger = LoggerFactory.getLogger(MembersController.class);
	
	private MembersService membersService;
	
	@PostMapping("/members/signup")
	public ApiResponse<MembersVO> createNewMember(
					@Valid @RequestBody RegistMembersVO registMembersVO,
					BindingResult validationResults
	) {
		
		if (validationResults.hasErrors()) {
			return ApiResponse.BAD_REQUEST(validationResults.getFieldErrors());
		}
		
		MembersVO membersVO = this.membersService.createNewMember(registMembersVO);
		// 가입된 회원의 정보를 반환
		return ApiResponse.OK(membersVO);
	}
	
	@GetMapping("/members/login")
	public ApiResponse<MembersVO> loginMember(
					@Valid @ModelAttribute LoginMemberVO loginMemberVO
					, BindingResult validationResult
					, HttpSession session
	) {
		logger.debug("{} <- SessionID", session.getId());
		
		if (validationResult.hasErrors()) {
			return ApiResponse.BAD_REQUEST(validationResult.getFieldErrors());
		}
		
		MembersVO loggedMember = this.membersService.readMember(loginMemberVO);
		
		// HttpSession에 로그인 한 사용자 정보를 기억시킨다
		session.setAttribute("__LOGIN_USER__", loggedMember);
		
		return ApiResponse.OK(loggedMember);
		
	}
	
	@GetMapping("/members/logout")
	public ApiResponse<String> logout(
			HttpSession session,
			@SessionAttribute("__LOGIN_USER__") MembersVO membersVO
	){
		// session 만료 처리
		// 만료된 session의 ID는 더이상 사용할 수 없음
		session.invalidate();
		
		String email = this.membersService.updateLogoutStatus(membersVO.getEmail());
		
		return ApiResponse.OK(email);
	}
	
	@DeleteMapping("/members/exit")
	public ApiResponse<String> exitMember(
			HttpSession session,
			@RequestParam String password,
			@SessionAttribute("__LOGIN_USER__") MembersVO membersVO
	){
		String email = this.membersService.deleteMember(membersVO.getEmail(), password);
		
		return ApiResponse.OK(email);
	}
	
	@GetMapping("/members/list")
	public ApiResponse<MemberListVO> getMembers(SearchMemberVO searchMemberVO) {
		MemberListVO result = this.membersService.readAllMembers(searchMemberVO);
		
		ApiResponse<MemberListVO> response = ApiResponse.OK(result);
		response.setPaginate(searchMemberVO);
		
		return response;
	}
	
}