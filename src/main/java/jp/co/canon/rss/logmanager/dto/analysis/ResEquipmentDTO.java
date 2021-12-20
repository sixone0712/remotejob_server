package jp.co.canon.rss.logmanager.dto.analysis;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ResEquipmentDTO {
    public String equipment_name;
    public String fab_name;
    public String inner_tool_id;
    public String machineName;
    public String toolType;
    public String tool_id;
    public String tool_serial;
    public String user_name;
}