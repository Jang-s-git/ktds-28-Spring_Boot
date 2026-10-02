package com.ktdsuniversity.edu.replies.service;

import com.ktdsuniversity.edu.replies.vo.request.ModifyRepliesVO;
import com.ktdsuniversity.edu.replies.vo.request.RegistRepliesVO;
import com.ktdsuniversity.edu.replies.vo.response.RepliesVO;
import com.ktdsuniversity.edu.replies.vo.response.ReplyListVO;

public interface RepliesService {

	// 게시글에 등록된 댓글을 반환
	ReplyListVO readAllRepliesByArticleId(String articleId);

	// 댓글 추가
	RepliesVO createNewReply(String articleId, RegistRepliesVO registRepliesVO);

	// 댓글 수정
	RepliesVO updateReply(String articleId, String replyId, ModifyRepliesVO modifyRepliesVO);

	// 댓글 삭제
	String deleteReply(String articleId, String replyId);

	// 댓글 추천
	long recommendOneReply(String articleId, String replyId);

}