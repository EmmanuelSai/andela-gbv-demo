package com.andela.gbv.demo.models.menu;
import java.util.Map;


public record DynamicItem(String key, String label, Map<String, Object> extra) {}
