package jp.co.canon.rss.logmanager.dto.auth;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ResLoginDTO {
	private Integer id;
	private String username;
	private List<String> roles;
}
