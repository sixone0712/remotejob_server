package jp.co.canon.rss.logmanager.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebPageControllerTest {

    WebPageController webPageController = new WebPageController();

    @Test
    void index() {
        webPageController.index();
    }

    @Test
    void notSupport() {
        webPageController.notSupport();
    }
}