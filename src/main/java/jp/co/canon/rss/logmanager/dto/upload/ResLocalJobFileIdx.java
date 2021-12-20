package jp.co.canon.rss.logmanager.dto.upload;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class ResLocalJobFileIdx {
    private int fileIndex;
}
