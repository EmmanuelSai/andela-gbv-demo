package com.andela.gbv.demo.utils;

import com.andela.gbv.demo.models.FemicideIndicators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.stereotype.Component;

@Component
@Converter
public class RiskIndicatorsConverter implements AttributeConverter<FemicideIndicators, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public String convertToDatabaseColumn(FemicideIndicators attribute) {
        if (attribute == null)
            return null;
        try {
            return MAPPER.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to serialize FemicideIndicators: " + e.getMessage(), e);
        }
    }

    @Override
    public FemicideIndicators convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank())
            return new FemicideIndicators();
        try {
            return MAPPER.readValue(dbData, FemicideIndicators.class);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to deserialize FemicideIndicators: " + e.getMessage(), e);
        }
    }
}
