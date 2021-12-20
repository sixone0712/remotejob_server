package jp.co.canon.rss.logmanager.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;


@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ReqSingUpDTO {
	@NotNull
	private String username;

	@NotNull
	private String password;

	@NotNull
	private List<String> roles;
}
