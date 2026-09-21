package com.ktdsuniversity.edu.members.vo.response;

import java.util.List;

public class MemberListVO {

	private long memberCount;
	
	private List<MembersVO> memberList;

	public long getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(long memberCount) {
		this.memberCount = memberCount;
	}

	public List<MembersVO> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<MembersVO> memberList) {
		this.memberList = memberList;
	}

	@Override
	public String toString() {
		return "MemberListVO [memberCount=" + memberCount + ", memberList=" + memberList + ", getMemberCount()="
				+ getMemberCount() + ", getMemberList()=" + getMemberList() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}
	
	
}
