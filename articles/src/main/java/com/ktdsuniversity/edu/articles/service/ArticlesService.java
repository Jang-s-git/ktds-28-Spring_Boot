package com.ktdsuniversity.edu.articles.service;

import com.ktdsuniversity.edu.articles.vo.response.ArticleListVO;

public interface ArticlesService {

	/**
	 * 게시글의 목록 조회
	 * @return (개시글 수, 게시글 목록)
	 */
	ArticleListVO readAllArticles();
	
}
