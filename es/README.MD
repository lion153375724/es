由于spring boot 目前SpringBoot 1.5.1.RELEASE和Spring Data Elasticsearch 2.10.RELEASE仅支持ElasticSearch 2.4.0。 
他们不支持最新版本的ElasticSearch 5.x版本。

Spring Boot Version (x)	Spring Data Elasticsearch Version (y)	Elasticsearch Version (z)
x <= 1.3.5						y <= 1.3.4						z <= 1.7.2*
x >= 1.4.x						2.0.0 <=y < 5.0.0**				2.0.0 <= z < 5.0.0**


版本对应说明:https://github.com/spring-projects/spring-data-elasticsearch/wiki/Spring-Data-Elasticsearch---Spring-Boot---version-matrix

由于我现在安装的为es 5.x版本，所以目前springboot的EsTemplate.及Resp..都不支持。关注后续版本
只能使用原始的客户端架包测试
