package com.ktdsuniversity.edu.members.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ktdsuniversity.edu.commons.crypto.AES;
import com.ktdsuniversity.edu.commons.crypto.encrypt.hash.SHA;
import com.ktdsuniversity.edu.commons.exceptions.ArticleException;
import com.ktdsuniversity.edu.commons.exceptions.enums.ArticleCodes;
import com.ktdsuniversity.edu.commons.exceptions.enums.ExceptionType;
import com.ktdsuniversity.edu.members.dao.MembersDao;
import com.ktdsuniversity.edu.members.vo.request.LoginMemberVO;
import com.ktdsuniversity.edu.members.vo.request.RegistMembersVO;
import com.ktdsuniversity.edu.members.vo.response.MembersVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MembersServiceImpl implements MembersService {

	private static final Logger logger = LoggerFactory.getLogger(MembersServiceImpl.class);
	
	@Value("${app.encrypt.aes.key}")
	private String aesSecretKey;
	
	private final MembersDao membersDao;

	@Transactional
	@Override
	public MembersVO createNewMember(RegistMembersVO registMembersVO) {
		
		String email = registMembersVO.getEmail();
		int emailCount = this.membersDao.selectEmailCount(email);
		if (emailCount > 0) {
//			throw new IllegalArgumentException(email + "은 이미 사용중입니다.");
			throw new ArticleException(ExceptionType.MEMBERS, ArticleCodes.USED);
		}
		
		String nickname = registMembersVO.getNickname();
		int nicknameCount = this.membersDao.selectNicknameCount(nickname);
		if (nicknameCount > 0) {
//			throw new IllegalArgumentException(nickname + "은 이미 사용중입니다.");
			throw new ArticleException(ExceptionType.MEMBERS, ArticleCodes.USED);
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
			throw new ArticleException(ExceptionType.MEMBERS, ArticleCodes.SYSTEM_ERROR);
		}
		
		MembersVO newMember = this.membersDao.selectMemberByEmail(email);
		newMember.setName( AES.decode(this.aesSecretKey, newMember.getName()) );
		newMember.setNickname( AES.decode(this.aesSecretKey, newMember.getNickname()) );
		return newMember;
	}

	@Override
	public MembersVO readMember(LoginMemberVO loginMemberVO) {
		MembersVO membersVO = this.membersDao.selectMemberByEmail(loginMemberVO.getEmail());
		
		if (membersVO == null) {
			throw new ArticleException(ExceptionType.MEMBERS, ArticleCodes.NOT_MATCHED_IDENTIFY);
		}
		
		// 차단된 계정
		if (membersVO.getLoginBlockYn().equals("Y")) {
			// 차단된 후 1시간이 지났는가?
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime loginBlockDate = LocalDateTime.parse(
															membersVO.getLoginBlockDate(),
															formatter);
			loginBlockDate = loginBlockDate.plusHours(1);
			
			if (now.equals(loginBlockDate) || now.isAfter(loginBlockDate)) {
				// 차단 후 1시간 경과
				// 로그인 실패횟수 0으로 초기화 & 차단 여부 N으로 수정
				int updateRows = this.membersDao.updateResetBlock(membersVO.getEmail());
				logger.info("{}건이 차단 해체되었습니다.", updateRows);
			} else {
				throw new ArticleException(ExceptionType.MEMBERS, ArticleCodes.NOT_MATCHED_IDENTIFY);
			}
		}
		
		// 활성화된 계정
		// 사용자 salt 필요
		// 로그인 요청 비밀번호 필요
		// 암호화
		String rawPassword = loginMemberVO.getPassword();
		String storedSalt = membersVO.getSalt();
		String encryptedPassword = SHA.getEncrypt(rawPassword, storedSalt);
		
		if (encryptedPassword.equals(membersVO.getPassword())) {
			// 비밀번호 일치함
			int updateRows = this.membersDao.updateLoginStatus(membersVO.getEmail());
			if (updateRows == 0) {
				throw new ArticleException(ExceptionType.MEMBERS, ArticleCodes.FAILURE_LOGIN);
			}
			MembersVO loggedMember = this.membersDao.selectMemberByEmail(membersVO.getEmail());
			loggedMember.setName(AES.decode(this.aesSecretKey, loggedMember.getName()));
			loggedMember.setNickname(AES.decode(this.aesSecretKey, loggedMember.getNickname()));
			return loggedMember;
		}
		
		// 비밀번호 불일치
		int updateRows = this.membersDao.updateLoginFailed(membersVO.getEmail());
		logger.info("{} 로그인 실패!", updateRows);
		
		int blockUpdateRows = this.membersDao.updateBlock(membersVO.getEmail());
		if (blockUpdateRows > 0) {
			// 계정이 차단됨
			throw new ArticleException(ExceptionType.MEMBERS, ArticleCodes.BLOCKED_LOGIN);
		} else {
			throw new ArticleException(ExceptionType.MEMBERS, ArticleCodes.NOT_MATCHED_IDENTIFY);
		}
	}

	@Transactional
	@Override
	public String updateLogoutStatus(String email) {
		int updateRows = this.membersDao.updateLogoutStatus(email);
		if (updateRows > 0) {
			return email;
		}
		return null;
	}

	@Transactional
	@Override
	public String deleteMember(String email, String password) {
		
		int deleteRows = this.membersDao.deleteMember(email);
		if (deleteRows > 0) {
			return email;
		}
		return null;
	}
	
}