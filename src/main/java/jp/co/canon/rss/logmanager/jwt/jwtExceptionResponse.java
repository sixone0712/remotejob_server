package jp.co.canon.rss.logmanager.jwt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class jwtExceptionResponse {
	String timestamp;
	int status;
	String error;
	String message;
	String path;
}
