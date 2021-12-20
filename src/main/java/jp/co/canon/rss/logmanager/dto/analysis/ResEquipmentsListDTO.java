package jp.co.canon.rss.logmanager.dto.analysis;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Accessors(chain = true)
public class ResEquipmentsListDTO {
    private Map<String, Map<String, List<String>>> equipments;
}
