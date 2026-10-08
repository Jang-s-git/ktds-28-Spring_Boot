package com.ktdsuniversity.edu.members.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.ktdsuniversity.edu.members.dao.MembersDao;
import com.ktdsuniversity.edu.members.vo.request.RegistMembersVO;
import com.ktdsuniversity.edu.members.vo.response.MembersVO;

@SpringBootTest // Spring이 생성하고 관리하는 Bean을 자동 주입 받기 위한 어노테이션
//@ExtendWith(SpringExtension.class) //JUnit5 사용 명시
//@Import({MembersDao.class, MembersServiceImpl.class}) // MembersServiceImpl <-- 주입이 필요한 Bean
public class MembersServiceImplTest {

	// SpringBootTest와 Import가 준비한 Bean을 주입 받음
	@Autowired
	private MembersService membersService;
	
	// @Autowired를 붙이지 않음
	// --> JUnit은 파일의 코드가 잘 동작 되는 지만 확인
	// --> 외부의 DB가 잘 동작하는 지는 관심 밖임
	@MockitoBean // = 가짜 Bean
	private MembersDao membersDao;
	
	@Test
	@DisplayName("회원가입 성공 테스트")
	public void testCreateNewMember() {
		
		RegistMembersVO registMembersVO = new RegistMembersVO();
		registMembersVO.setEmail("test@gmail.com");
		registMembersVO.setName("TestUser");
		registMembersVO.setNickname("TestNickname");
		registMembersVO.setPassword("test_password");
		
		// Test pattern: Given(역할 부여) -> When(테스트) -> Then(결과)
		// Given - membersDao에게 부여
		// membersDao.selectEmailCount에게 "test@gmail.com"이 전달되면, 0을 반환하도록 부여
		BDDMockito.given(this.membersDao.selectEmailCount("test@gmail.com"))
				  .willReturn(0);
		
		BDDMockito.given(this.membersDao.selectNicknameCount("TestNickname"))
				  .willReturn(0);
		
		BDDMockito.given(this.membersDao.insertNewMember(registMembersVO))
				  .willReturn(1);
		
		MembersVO returnedMember = new MembersVO();
		
		BDDMockito.given(this.membersDao.selectMemberByEmail("test@gmail.com"))
				  .willReturn(returnedMember);
		
		// When
		MembersVO membersVO = this.membersService.createNewMember(registMembersVO);
		System.out.println("MembersVO => " + membersVO);
		System.out.println("registMembersVO => " + registMembersVO);
		
		//Then
		// 반환값이 올바른지 검증 (Given에서 주었던 값과 일치하는지)
		assertNotNull(membersVO);
		assertEquals(membersVO, returnedMember);
		// 비밀번호가 올바르게 암호화 되었는 지 확인
		assertNotNull(registMembersVO.getSalt());
		assertNotEquals("test_password", registMembersVO.getPassword());
	}
	
}
