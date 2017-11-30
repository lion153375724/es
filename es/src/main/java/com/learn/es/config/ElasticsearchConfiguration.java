package com.learn.es.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfiguration{
    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchConfiguration.class);
    //由于项目从2.2.4配置的升级到 5.4.1版本 原配置文件不想动还是指定原来配置参数
    @Value("${spring.data.elasticsearch.cluster-nodes}")
    private String clusterNodes ;

    @Value("${spring.data.elasticsearch.cluster-name}")
    private String clusterName;

    private TransportClient client;
    
    @Bean
    public TransportClient client() throws UnknownHostException {
    	try {
            PreBuiltTransportClient  preBuiltTransportClient = new PreBuiltTransportClient(settings());
            if (!"".equals(clusterNodes)){
                for (String nodes:clusterNodes.split(",")) {
                    String InetSocket [] = nodes.split(":");
                    String  Address = InetSocket[0];
                    Integer  port = Integer.valueOf(InetSocket[1]);
                    preBuiltTransportClient.addTransportAddress(new
                                     InetSocketTransportAddress(InetAddress.getByName(Address),port ));
                }
                client = preBuiltTransportClient;
            }
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
        }
    	return client;
    }
    /**
     * 初始化默认的client
     */
    private Settings settings(){
        Settings settings = Settings.builder()
        		.put("cluster.name",clusterName)
        		.put("client.transport.sniff",true) //增加自动嗅探配置
        		.build();
        client = new PreBuiltTransportClient(settings);
        return settings;
    }
}
