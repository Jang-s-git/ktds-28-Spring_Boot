package com.ktdsuniversity.edu.articles.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ktdsuniversity.edu.articles.vo.request.ModifyArticleVO;
import com.ktdsuniversity.edu.articles.vo.request.RegistArticleVO;
import com.ktdsuniversity.edu.articles.vo.request.SearchArticleVO;
import com.ktdsuniversity.edu.articles.vo.response.ArticlesVO;

/**
 * @Mapper: 
 * Spring의 @Repository를 한 번 감싼 어노테이션
 * Mybatis가 익명의 클래스를 만들어서 데이터베이스에 접근하도록 한다
 */
@Mapper
public interface ArticlesDao {

	/**
	 * 게시글의 총 개수를 반환
	 * @return
	 */
	long selectArticlesCount(SearchArticleVO searchArticleVO);
	
	/**
	 * 게시글을 검색해서 반환
	 * @return
	 */
	List<ArticlesVO> selectAllArticles(SearchArticleVO searchArticleVO);
	
	/**
	 * 클라이언트가 보내준 게시글 등록 정보를 데이터베이스에 insert 한다
	 * @param registArticleVO (제목, 내용, 이메일)
	 * @return insert한 row의 개수
	 */
	int insertNewArticle(RegistArticleVO registArticleVO);
	
	/**
	 * 게시글 아이디로 게시글 정보를 조회한다.
	 * @param articleId 게시글의 PK
	 * @return 게시글의 PK로 조회한 게시글 정보
	 */
	ArticlesVO selectArticleByArticleId(String articleId);

	int updateArticle(@Param("articleId") String articleId, // articleId의 파라미터 이름을 value값으로 전달
					  @Param("modifyArticleVO") ModifyArticleVO modifyArticleVO); // modifyArticleVO의 파라미터 이름을 value값으로 전달

	//게시글 삭제
	int deleteArticle(String articleId);

	//게시글 1개 조회수
	int updateIncreaseViewCount(String articleId);
	//게시글 추천수 증가
	int updateIncreaseRecommendCount(String articleId);
}
