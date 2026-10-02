package com.ktdsuniversity.edu.members.vo.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class MembersVO {

	private String email;
	private String name;
	private String nickname;
	
	// 조회 시 이것만 제외하고 호출
	// 사용자 정보를 조회할 때 민감 정보를 보여주지 않기 위함
	@JsonIgnore
	private String password;
	
	private String registDate;
	private String modifyDate;
	private String latestLoginSuccessDate;
	private String latestLoginFailDate;
	private String latestLogoutDate;
	private int loginFailCount;
	private String loginBlockYn;
	private String loginBlockDate;
	private String loginYn;
	
	@JsonIgnore
	private String salt;
	
	private String delYn;
	
}