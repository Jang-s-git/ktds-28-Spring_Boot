package com.ktdsuniversity.edu.members.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ktdsuniversity.edu.commons.crypto.AES;
import com.ktdsuniversity.edu.commons.crypto.encrypt.hash.SHA;
import com.ktdsuniversity.edu.members.dao.MembersDao;
import com.ktdsuniversity.edu.members.vo.request.RegistMembersVO;
import com.ktdsuniversity.edu.members.vo.response.MembersVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembersServiceImpl implements MembersService {

	@Value("${app.encrypt.aes.key}")
	private String aesSecretKey;
	
	private final MembersDao membersDao;

	@Override
	public MembersVO createNewMember(RegistMembersVO registMembersVO) {
		
		String email = registMembersVO.getEmail();
		int emailCount = this.membersDao.selectEmailCount(email);
		if (emailCount > 0) {
			throw new IllegalArgumentException(email + "은 이미 사용중입니다.");
		}
		
		String nickname = registMembersVO.getNickname();
		int nicknameCount = this.membersDao.selectNicknameCount(nickname);
		if (nicknameCount > 0) {
			throw new IllegalArgumentException(nickname + "은 이미 사용중입니다.");
		}
		
		String rawName = registMembersVO.getName();
		String encryptedName = AES.encode(this.aesSecretKey, rawName);
		registMembersVO.setName(encryptedName);
		
		String rawNickname = registMembersVO.getNickname();
		String encryptedNickname = AES.encode(this.aesSecretKey, rawNickname);
		registMembersVO.setNickname(encryptedNickname);
		
		String rawPassword = registMembersVO.getPassword();
		String salt = SHA.generateSalt();
		String encryptedPassword = SHA.getEncrypt(rawPassword, salt);
		
		registMembersVO.setSalt(salt);
		registMembersVO.setPassword(encryptedPassword);
		
		int insertCount = this.membersDao.insertNewMember(registMembersVO);
		if (insertCount == 0) {
			throw new IllegalArgumentException("회원가입을 할 수 없습니다. 다시 시도해주세요");
		}
		
		MembersVO newMember = this.membersDao.selectMemberByEmail(email);
		newMember.setName( AES.decode(this.aesSecretKey, newMember.getName()) );
		newMember.setNickname( AES.decode(this.aesSecretKey, newMember.getNickname()) );
		return newMember;
	}
	
}