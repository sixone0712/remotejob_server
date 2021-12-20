package jp.co.canon.rss.logmanager.dto.site;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ResPlanDTO {
	private int planId;
	private String planName;
	private String planType;
	private List<String> machineNames;
	private List<String> targetNames;
	private String description;
	private String status;
	private String error;
	private String measure;
	private String detail;
}