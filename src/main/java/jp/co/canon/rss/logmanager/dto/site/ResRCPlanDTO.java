package jp.co.canon.rss.logmanager.dto.site;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class ResRCPlanDTO {
	public int planId;
	public String planType;
	public int ownerId;
	public String planName;
	public List<String> fabNames;
	public List<String> machineNames;
	public List<String> categoryCodes;
	public List<String> categoryNames;
	public List<String> commands;
	public String type;
	public String interval;
	public String description;
	public String start;
	public String from;
	public String to;
	public String lastCollection;
	public String status;
	public String detailedStatus;
	public String error;
	public String measure;
	public boolean separatedZip;
}
