package com.ktdsuniversity.edu.members.vo.request;

import com.ktdsuniversity.edu.commons.vo.PaginationVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SearchMemberVO extends PaginationVO {

	public String name;
	public String nickname;
	public String email;
	public String registDate;
	
}
