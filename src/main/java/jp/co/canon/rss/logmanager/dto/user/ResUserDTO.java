package jp.co.canon.rss.logmanager.dto.user;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ResUserDTO {
	private Integer id;

	private String username;

	private List<String> roles;

	private String updateAt;

	private String accessAt;
}
