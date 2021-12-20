package jp.co.canon.rss.logmanager.dto.history;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema
public class ResBuildLog {
	private int id;
	private String name;
	private String status;
}
