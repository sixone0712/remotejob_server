package jp.co.canon.rss.logmanager.controller.model;

import io.swagger.v3.oas.annotations.media.Schema;

public class MultiRequest {
    @Schema(type = "string", format = "binary", description = "payload")
    public String file;
}
