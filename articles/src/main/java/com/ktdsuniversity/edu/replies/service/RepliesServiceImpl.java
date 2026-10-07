package com.ktdsuniversity.edu.replies.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ktdsuniversity.edu.articles.dao.ArticlesDao;
import com.ktdsuniversity.edu.articles.vo.response.ArticlesVO;
import com.ktdsuniversity.edu.commons.exceptions.ArticleException;
import com.ktdsuniversity.edu.commons.exceptions.enums.ArticleCodes;
import com.ktdsuniversity.edu.commons.exceptions.enums.ExceptionType;
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

	private static final Logger logger = LoggerFactory.getLogger(RepliesServiceImpl.class);
	
	private ArticlesDao articlesDao;
	private RepliesDao repliesDao;
	private MultipartHandler multipartHandler;
	
	@Override
	public ReplyListVO readAllRepliesByArticleId(String articleId) {
		
		ArticlesVO articles = this.articlesDao.selectArticleByArticleId(articleId);
		if (articles == null) {
			throw new ArticleException(ExceptionType.ARTICLES, ArticleCodes.NOT_EXISTS);
		}
		
		ReplyListVO list = new ReplyListVO();
		list.setReplyCount(this.repliesDao.selectRepliesCount(articleId));
		list.setReplyList(this.repliesDao.selectAllRepliesByArticleId(articleId));
		return list;
	}

	@Transactional
	@Override
	public RepliesVO createNewReply(String articleId, RegistRepliesVO registRepliesVO) {
		ArticlesVO articles = this.articlesDao.selectArticleByArticleId(articleId);
		if (articles == null) {
			throw new ArticleException(ExceptionType.ARTICLES, ArticleCodes.NOT_EXISTS);
		}
		
		String fileSetId = this.multipartHandler.storeFiles(
				registRepliesVO.getFile(), 
				registRepliesVO.getEmail());
		registRepliesVO.setFileSetId(fileSetId);
		
		int insertedRows = this.repliesDao.insertNewReply(articleId, registRepliesVO);
		logger.info("{}개의 row가 생성되었습니다.", insertedRows);
		
		if (insertedRows == 0) {
//			throw new IllegalArgumentException("입력값이 유효하지 않습니다.");
			throw new ArticleException(ExceptionType.REPLIES, ArticleCodes.BAD_REQUEST);
		}
		
		return this.repliesDao.selectReplyByReplyId(articleId, registRepliesVO.getId());
	}

	@Transactional
	@Override
	public RepliesVO updateReply(String articleId, String replyId, ModifyRepliesVO modifyRepliesVO) {
		ArticlesVO articles = this.articlesDao.selectArticleByArticleId(articleId);
		if (articles == null) {
			throw new ArticleException(ExceptionType.ARTICLES, ArticleCodes.NOT_EXISTS);
		}
		
		RepliesVO reply = this.repliesDao.selectReplyByReplyId(articleId, replyId);
		if (reply == null) {
			throw new ArticleException(ExceptionType.REPLIES, ArticleCodes.NOT_EXISTS);
		}
		
		String fileSetId = this.multipartHandler.storeFiles(
				modifyRepliesVO.getFile(), 
				modifyRepliesVO.getEmail(), 
				reply.getFileSetId());
		modifyRepliesVO.setFileSetId(fileSetId);
		
		int updatedRows = this.repliesDao.updateReply(articleId, replyId, modifyRepliesVO);
		if (updatedRows == 0) {
			throw new ArticleException(ExceptionType.REPLIES, ArticleCodes.NOT_EXISTS);
		}
		
		return this.repliesDao.selectReplyByReplyId(articleId, replyId);
	}

	@Transactional
	@Override
	public String deleteReply(String articleId, String replyId) {
		ArticlesVO articles = this.articlesDao.selectArticleByArticleId(articleId);
		if (articles == null) {
			throw new ArticleException(ExceptionType.ARTICLES, ArticleCodes.NOT_EXISTS);
		}
		
		RepliesVO reply = this.repliesDao.selectReplyByReplyId(articleId, replyId);
		
		int deletedRows = this.repliesDao.deleteReplyByReplyId(articleId, replyId);
		if (deletedRows == 0) {
			throw new ArticleException(ExceptionType.REPLIES, ArticleCodes.NOT_EXISTS);
		}
		
		int deleteCount = this.multipartHandler.deleteFiles(reply.getFileSetId());
		logger.info("{}개의 파일이 삭제되었습니다.", deleteCount);
		return replyId;
	}

	@Transactional
	@Override
	public long recommendOneReply(String articleId, String replyId) {
		
		ArticlesVO articles = this.articlesDao.selectArticleByArticleId(articleId);
		if (articles == null) {
			throw new ArticleException(ExceptionType.ARTICLES, ArticleCodes.NOT_EXISTS);
		}
		
		int updatedRows = this.repliesDao.updateIncreaseRecommendCount(articleId, replyId);
		if (updatedRows == 0) {
			throw new ArticleException(ExceptionType.REPLIES, ArticleCodes.NOT_EXISTS);
		}
		
		RepliesVO reply = this.repliesDao.selectReplyByReplyId(articleId, replyId);
		return reply.getRecommendCnt();
	}

}