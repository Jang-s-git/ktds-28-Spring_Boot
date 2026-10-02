package com.ktdsuniversity.edu.replies.vo.request;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ModifyRepliesVO {

	@NotEmpty(message="이메일은 필수 입력값입니다.")
	@Email(message="올바른 이메일을 입력해주세요.")
	private String email;
	
	private String content;
	
	private List<MultipartFile> file;
	
	private String fileSetId;
	
}