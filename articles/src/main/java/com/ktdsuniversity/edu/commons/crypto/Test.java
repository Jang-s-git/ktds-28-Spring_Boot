package com.ktdsuniversity.edu.commons.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ktdsuniversity.edu.commons.crypto.encrypt.hash.SHA;

public class Test {

	private static final String AES_SECRET_KEY = "abcde12345abcde12345abcde12345zz";
	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	
	public static void testSHA() {
		String rawPassword = "password1234";
		
		// SHA를 이용한 이중 암호화
		// 1. 이중 암호화를 위한 SALT 발급
		String salt = SHA.generateSalt();
		logger.debug(salt);
		
		// 2. rawPassword와 SALT를 이용한 암호화
		String encryptPassword = SHA.getEncrypt(rawPassword, salt);
		logger.debug(encryptPassword);
	}
	
	public static void testAESEnc() {
		// AES 암호화
		String name = "장규나";
		String encryptedName = AES.encode(AES_SECRET_KEY, name);
		logger.debug(encryptedName);
	}
	
	public static void testAESDec() {
		// AES 복호화
		String encryptedName = "ad4d180ea6c1ca4449c9e0efff99972f"; // --> 암호화된 name 값
		String rawName = AES.decode(AES_SECRET_KEY, encryptedName);
		logger.debug(rawName); // 장규나
	}
	
	public static void main(String[] args) {
		testSHA();
		testAESEnc();
		testAESDec();
	}
	
}
