package com.ktdsuniversity.edu.replies.web;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ktdsuniversity.edu.commons.util.ApiResponse;
import com.ktdsuniversity.edu.replies.service.RepliesService;
import com.ktdsuniversity.edu.replies.vo.request.ModifyRepliesVO;
import com.ktdsuniversity.edu.replies.vo.request.RegistRepliesVO;
import com.ktdsuniversity.edu.replies.vo.response.RepliesVO;
import com.ktdsuniversity.edu.replies.vo.response.ReplyListVO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController // (@Controller + @ResponseBody) 메소드에 @ResponseBody 생략 가능
public class RepliesController {

	private RepliesService repliesService;
	
	// GET /replies/{게시글아이디}
	// 게시글에 등록된 댓글을 반환
	@GetMapping("/articles/{articleId}/replies")
	public ApiResponse<ReplyListVO> getReplies(@PathVariable String articleId) {
		try {
			return ApiResponse.OK(this.repliesService.readAllRepliesByArticleId(articleId));
		} catch (IllegalArgumentException iae) {
			return ApiResponse.FORBIDDEN(iae.getMessage());
		}
	}
	
	// POST /replies/{게시글아이디}
	// 게시글에 댓글 작성 (파일 첨부 가능)
	@PostMapping("/articles/{articleId}/replies")
	public ApiResponse<RepliesVO> makeNewReply(@PathVariable String articleId,
			@Valid @ModelAttribute RegistRepliesVO registRepliesVO,
			BindingResult validationResult
	) {
		if (validationResult.hasErrors()) {
			return ApiResponse.BAD_REQUEST(validationResult.getFieldErrors());
		}
		
		try {
			return ApiResponse.OK(this.repliesService.createNewReply(articleId, registRepliesVO));
		} catch (IllegalArgumentException iae) {
			return ApiResponse.FORBIDDEN(iae.getMessage());
		}
	}
	
	// PUT /replies/{게시글아이디}/{댓글아이디}
	// 게시글에 등록된 댓글을 수정 (파일 첨부 가능)
	@PutMapping("/articles/{articleId}/replies/{replyId}")
	public ApiResponse<RepliesVO> updateReply(@PathVariable String articleId, @PathVariable String replyId,
			@Valid @ModelAttribute ModifyRepliesVO modifyRepliesVO,
			BindingResult validationResult
	) {
		if (validationResult.hasErrors()) {
			return ApiResponse.BAD_REQUEST(validationResult.getFieldErrors());
		}
		
		try {
			return ApiResponse.OK(this.repliesService.updateReply(articleId, replyId, modifyRepliesVO));
		} catch (IllegalArgumentException iae) {
			return ApiResponse.FORBIDDEN(iae.getMessage());
		}
	}
	
	// DELETE /replies/{게시글아이디}/{댓글아이디}
	// 게시글에 등록된 댓글 하나를 삭제
	// 첨부된 파일 제거
	@DeleteMapping("/articles/{articleId}/replies/{replyId}")
	public ApiResponse<String> deleteReply(
			@Size(min=18, max=20, message="잘못된 값입니다.")
			@PathVariable String articleId, 
			@Size(min=18, max=20, message="잘못된 값입니다.")
			@PathVariable String replyId)
	{
		try {
			return ApiResponse.OK(this.repliesService.deleteReply(articleId, replyId));
		} catch (IllegalArgumentException iae) {
			return ApiResponse.FORBIDDEN(iae.getMessage());
		}
	}
	
	// PUT /replies/{게시글아이디}/recommend/{댓글아이디}
	// 게시글에 등록된 댓글 하나를 추천
	@PutMapping("/articles/{articleId}/replies/recommend/{replyId}")
	public ApiResponse<Long> recommendOneReply(@PathVariable String articleId, @PathVariable String replyId) {
		try {
			return ApiResponse.OK(this.repliesService.recommendOneReply(articleId, replyId));
		} catch (IllegalArgumentException iae) {
			return ApiResponse.FORBIDDEN(iae.getMessage());
		}
	}
	
}
