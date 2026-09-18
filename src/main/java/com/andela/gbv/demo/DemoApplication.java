package com.andela.gbv.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.andela.gbv.demo.configs.EncryptionConfig;
import com.andela.gbv.demo.configs.JwtProperties;
import com.andela.gbv.demo.configs.PortalProperties;
import com.andela.gbv.demo.configs.TwilioConfig;

@SpringBootApplication
@EnableConfigurationProperties({ TwilioConfig.class, EncryptionConfig.class, PortalProperties.class, JwtProperties.class })
@EnableScheduling
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
