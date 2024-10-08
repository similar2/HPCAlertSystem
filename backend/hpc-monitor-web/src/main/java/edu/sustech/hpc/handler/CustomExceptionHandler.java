package edu.sustech.hpc.handler;

import edu.sustech.hpc.result.ApiResponse;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import static edu.sustech.hpc.result.ApiResponse.badRequest;

/**
 * 只能捕获到API阶段的Exception, 无法捕获filter链中的Exception<p>
 * 参考：<a href="https://www.nicechiblog.com/article/14/article_1598135490604.html">Spring中的ExceptionHandler</a>
 * <p><p>
 * 如果不使用{@code @}{@link ReportAsSingleViolation}，那么{@link MethodArgumentNotValidException}和{@link ConstraintViolationException}中可能包含多个Violation，最好都加进报错信息里<p>
 * 参考：<a href="https://reflectoring.io/bean-validation-with-spring-boot/">bean-validation-with-spring-boot</a>
 */
@RestControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(MissingServletRequestPartException.class)
	public ApiResponse handleMissingServletRequestPartException(MissingServletRequestPartException e){
		return badRequest("Missing request part ["+e.getRequestPartName()+"]");
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ApiResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e){
		return badRequest("Missing param ["+e.getParameterName()+"]");
	}

	@SuppressWarnings("ConstantConditions")
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ApiResponse handle(MethodArgumentNotValidException e){
		FieldError fieldError = e.getBindingResult().getFieldError();
		String field = fieldError.getField();
		return badRequest("["+field+"] "+ fieldError.getDefaultMessage());
	}

	@ExceptionHandler({HttpMessageNotReadableException.class, HttpRequestMethodNotSupportedException.class})
	public ApiResponse handleTwoHttpExceptions(Exception e) {
		return badRequest(e.getMessage());
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ApiResponse handleConstraintViolationException(ConstraintViolationException e) {
		String[] split = e.getMessage().split(": ");
		String field = split[0].split("\\.")[1];
		String message = split[1];
		return badRequest("["+field+"] "+message);
	}

	@ExceptionHandler(ApiException.class)
	public ApiResponse handle(ApiException e){
		return new ApiResponse(e.getCode(),e.getMessage());
	}
}
