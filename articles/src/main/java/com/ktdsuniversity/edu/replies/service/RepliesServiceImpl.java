package com.ktdsuniversity.edu.replies.service;

import org.springframework.stereotype.Service;

import com.ktdsuniversity.edu.articles.dao.ArticlesDao;
import com.ktdsuniversity.edu.articles.vo.response.ArticlesVO;
import com.ktdsuniversity.edu.files.components.MultipartHandler;
import com.ktdsuniversity.edu.replies.dao.RepliesDao;
import com.ktdsuniversity.edu.replies.vo.request.ModifyRepliesVO;
import com.ktdsuniversity.edu.replies.vo.request.RegistRepliesVO;
import com.ktdsuniversity.edu.replies.vo.response.RepliesVO;
import com.ktdsuniversity.edu.replies.vo.response.ReplyListVO;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class RepliesServiceImpl implements RepliesService {

	private ArticlesDao articlesDao;
	private RepliesDao repliesDao;
	private MultipartHandler multipartHandler;
	
	@Override
	public ReplyListVO readAllRepliesByArticleId(String articleId) {
		
		ArticlesVO articles = this.articlesDao.selectArticleByArticleId(articleId);
		if (articles == null) {
			throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
		}
		
		ReplyListVO list = new ReplyListVO();
		list.setReplyCount(this.repliesDao.selectRepliesCount(articleId));
		list.setReplyList(this.repliesDao.selectAllRepliesByArticleId(articleId));
		return list;
	}

	@Override
	public RepliesVO createNewReply(String articleId, RegistRepliesVO registRepliesVO) {
		ArticlesVO articles = this.articlesDao.selectArticleByArticleId(articleId);
		if (articles == null) {
			throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
		}
		
		String fileSetId = this.multipartHandler.storeFiles(
				registRepliesVO.getFile(), 
				registRepliesVO.getEmail());
		registRepliesVO.setFileSetId(fileSetId);
		
		int insertedRows = this.repliesDao.insertNewReply(articleId, registRepliesVO);
		System.out.println(insertedRows + "개의 row가 생성되었습니다.");
		
		if (insertedRows == 0) {
			throw new IllegalArgumentException("입력값이 유효하지 않습니다.");
		}
		
		return this.repliesDao.selectReplyByReplyId(articleId, registRepliesVO.getId());
	}

	@Override
	public RepliesVO updateReply(String articleId, String replyId, ModifyRepliesVO modifyRepliesVO) {
		ArticlesVO articles = this.articlesDao.selectArticleByArticleId(articleId);
		if (articles == null) {
			throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
		}
		
		RepliesVO reply = this.repliesDao.selectReplyByReplyId(articleId, replyId);
		if (reply == null) {
			throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
		}
		
		String fileSetId = this.multipartHandler.storeFiles(
				modifyRepliesVO.getFile(), 
				modifyRepliesVO.getEmail(), 
				reply.getFileSetId());
		modifyRepliesVO.setFileSetId(fileSetId);
		
		int updatedRows = this.repliesDao.updateReply(articleId, replyId, modifyRepliesVO);
		if (updatedRows == 0) {
			throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
		}
		
		return this.repliesDao.selectReplyByReplyId(articleId, replyId);
	}

	@Override
	public String deleteReply(String articleId, String replyId) {
		ArticlesVO articles = this.articlesDao.selectArticleByArticleId(articleId);
		if (articles == null) {
			throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
		}
		
		RepliesVO reply = this.repliesDao.selectReplyByReplyId(articleId, replyId);
		
		int deletedRows = this.repliesDao.deleteReplyByReplyId(articleId, replyId);
		if (deletedRows == 0) {
			throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
		}
		
		int deleteCount = this.multipartHandler.deleteFiles(reply.getFileSetId());
		System.out.println(deleteCount + "개의 파일이 삭제되었습니다.");
		return replyId;
	}

	@Override
	public long recommendOneReply(String articleId, String replyId) {
		
		ArticlesVO articles = this.articlesDao.selectArticleByArticleId(articleId);
		if (articles == null) {
			throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
		}
		
		int updatedRows = this.repliesDao.updateIncreaseRecommendCount(articleId, replyId);
		if (updatedRows == 0) {
			throw new IllegalArgumentException("존재하지 않는 댓글입니다.");
		}
		
		RepliesVO reply = this.repliesDao.selectReplyByReplyId(articleId, replyId);
		return reply.getRecommendCnt();
	}

}