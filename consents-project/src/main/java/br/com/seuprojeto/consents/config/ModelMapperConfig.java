package br.com.seuprojeto.consents.config;


import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Date;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Add a custom converter for java.sql.Date
        Converter<Date, java.util.Date> sqlDateToUtilDate = new AbstractConverter<>() {
            @Override
            protected java.util.Date convert(Date source) {
                return source != null ? new java.util.Date(source.getTime()):null;
            }
        };

        modelMapper.addConverter(sqlDateToUtilDate);

        return modelMapper;
    }
}