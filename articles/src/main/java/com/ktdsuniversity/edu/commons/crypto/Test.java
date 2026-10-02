package com.ktdsuniversity.edu.commons.crypto;

import com.ktdsuniversity.edu.commons.crypto.encrypt.hash.SHA;

public class Test {

	private static final String AES_SECRET_KEY = "abcde12345abcde12345abcde12345zz";
	
	public static void testSHA() {
		String rawPassword = "password1234";
		
		// SHA를 이용한 이중 암호화
		// 1. 이중 암호화를 위한 SALT 발급
		String salt = SHA.generateSalt();
		System.out.println(salt);
		
		// 2. rawPassword와 SALT를 이용한 암호화
		String encryptPassword = SHA.getEncrypt(rawPassword, salt);
		System.out.println(encryptPassword);
	}
	
	public static void testAESEnc() {
		// AES 암호화
		String name = "장규나";
		String encryptedName = AES.encode(AES_SECRET_KEY, name);
		System.out.println(encryptedName);
	}
	
	public static void testAESDec() {
		// AES 복호화
		String encryptedName = "ad4d180ea6c1ca4449c9e0efff99972f"; // --> 암호화된 name 값
		String rawName = AES.decode(AES_SECRET_KEY, encryptedName);
		System.out.println(rawName); // 장규나
	}
	
	public static void main(String[] args) {
		testSHA();
		testAESEnc();
		testAESDec();
	}
	
}
