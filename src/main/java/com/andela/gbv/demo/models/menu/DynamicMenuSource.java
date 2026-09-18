package com.andela.gbv.demo.models.menu;

import java.util.List;
import java.util.Map;

public interface DynamicMenuSource {
    String getName();

    List<DynamicItem> resolve(String userNumber, Map<String, Object> context);
}
