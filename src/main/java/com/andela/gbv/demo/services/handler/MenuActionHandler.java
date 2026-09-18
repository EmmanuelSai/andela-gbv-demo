package com.andela.gbv.demo.services.handler;

import java.util.Map;

public interface MenuActionHandler {
    String getName();

    void handle(String userNumber, String input, Map<String, Object> context);
}
