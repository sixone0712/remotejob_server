package jp.co.canon.rss.logmanager.dto.rulecrasdata;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Schema
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResOptionDTO {
    private Map<String, List<String>> option;
}
