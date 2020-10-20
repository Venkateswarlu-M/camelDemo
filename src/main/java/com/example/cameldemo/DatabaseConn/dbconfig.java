package com.example.cameldemo.DatabaseConn;

import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;

@Configuration
public class dbconfig {
	
	@Bean(name ="dataSource")
	@ConfigurationProperties(prefix ="spring.datasource")
	public DataSource dataSource() {
		DataSource ds= DataSourceBuilder.create().build();
		return ds;
		
	}

}