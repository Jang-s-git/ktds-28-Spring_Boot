package com.ktdsuniversity.edu.commons.exceptions;

import com.ktdsuniversity.edu.commons.exceptions.enums.ArticleCodes;
import com.ktdsuniversity.edu.commons.exceptions.enums.ExceptionType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
public class ArticleException extends RuntimeException {

	private static final long serialVersionUID = -8793056757109164458L;
	
	private ExceptionType type;
	private ArticleCodes codes;
}
