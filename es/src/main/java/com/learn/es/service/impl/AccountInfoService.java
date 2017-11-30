package com.learn.es.service.impl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.beanutils.BeanUtils;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.learn.es.model.AccountInfo;
import com.learn.es.model.ResultDTO;
import com.learn.es.service.IAccountInfoService;

@Service
public class AccountInfoService implements IAccountInfoService{
	private Logger log = LoggerFactory.getLogger(AccountInfoService.class);
	private static final String ES_INDEX = "springbootes";
	private static final String ES_TYPE = "accountInfo";
	
	@Autowired
    private TransportClient client;
	
	/*@Autowired
	private ElasticsearchTemplate elasticsearchTemplate;*/
	
	@Override
	public Boolean insertAccountInfo(AccountInfo accountInfo) {
		ResponseEntity entity = null;
		 try {
            XContentBuilder builder = XContentFactory.jsonBuilder().startObject()
                    .field("accountName", accountInfo.getAccountName())
                    .field("nickName", accountInfo.getNickName())
                    .endObject();
            IndexResponse response = this.client.prepareIndex(ES_INDEX, ES_TYPE,accountInfo.getId())
                    .setSource(builder).get();
            entity =  new ResponseEntity(response.getId(), HttpStatus.OK);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            entity = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);

        }
		return entity.getStatusCode() == HttpStatus.OK ? true : false;
	}
	

	@Override
	public Boolean deleteAccountInfo(String id) {
		ResponseEntity entity = null;
		if (StringUtils.isEmpty(id)) {
			entity = new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        DeleteResponse response = this.client.prepareDelete(ES_INDEX, ES_TYPE, id).get();
        entity = new ResponseEntity(response.getResult().toString(), HttpStatus.OK);
        return entity.getStatusCode() == HttpStatus.OK ? true : false;
	}

	@Override
	public Boolean updateAccountInfo(AccountInfo accountInfo) {
		UpdateRequest request = new UpdateRequest(ES_INDEX, ES_TYPE, accountInfo.getId());
		ResponseEntity entity = null;
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
            if (accountInfo.getAccountName() != null) {
                builder.field("accountName", accountInfo.getAccountName());
            }
            if (accountInfo.getNickName() != null) {
                builder.field("nickName", accountInfo.getNickName());
            }
            builder.endObject();
            request.doc(builder);
            UpdateResponse response = this.client.update(request).get();
            entity =  new ResponseEntity(response.getResult().toString(), HttpStatus.OK);
        } catch (IOException | InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            entity = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        return entity.getStatusCode() == HttpStatus.OK ? true : false;
	}

	@Override
	public ResultDTO<AccountInfo> queryAccountInfoById(String id) {
		ResultDTO<AccountInfo> result = new ResultDTO<AccountInfo>();
		ResponseEntity  entity =  null;
		if (StringUtils.isEmpty(id)) {
			result.setCode(HttpStatus.NOT_FOUND.toString());
			result.setSuccess(false);
			result.setMsg("查询失败:id为空");
        }

        GetResponse response = this.client.prepareGet(ES_INDEX, ES_TYPE, id).get();
        if (!response.isExists()) {
        	result.setCode(HttpStatus.NOT_FOUND.toString());
			result.setSuccess(false);
			result.setMsg("查询失败:找不到结果！");
        }
        AccountInfo account = new AccountInfo();
        Map<String,Object> source = response.getSource();
        try {
			BeanUtils.populate(account, source);
		} catch (Exception e) {
			e.printStackTrace();
		}
        result.setResult(account);
        result.setSuccess(true);
        result.setCode(HttpStatus.OK.toString());
        result.setMsg("查询成功");
        return result;
	}

	@Override
	public List<ResultDTO<AccountInfo>> queryAccountInfo(String accountName,String nikeName) {
		BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(accountName)) {
            boolBuilder.must(QueryBuilders.matchQuery("accountName", accountName));
        }
        if (!StringUtils.isEmpty(nikeName)) {
            boolBuilder.must(QueryBuilders.matchQuery("nikeName", nikeName));
        }

        /*RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("word_count").from(gtWordCount);
        if (ltWordCount != null) {
            rangeQuery.to(ltWordCount);
        }
        boolBuilder.filter(rangeQuery);*/
        SearchRequestBuilder builder = this.client.prepareSearch(ES_INDEX)
                    .setTypes(ES_TYPE)
                    //Type 什么意思不懂
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(boolBuilder)
                    .setFrom(0)
                    .setSize(10);
        log.info(String.valueOf(builder));
        SearchResponse response = builder.get();
        
        List<ResultDTO<AccountInfo>> list = new ArrayList<ResultDTO<AccountInfo>>();
        AccountInfo accountInfo;
        ResultDTO<AccountInfo> result;
        for(SearchHit s : response.getHits()){
        	System.out.println("########:" + s);
        	result = new ResultDTO<AccountInfo>();
        	accountInfo = new AccountInfo();
        	try {
				BeanUtils.populate(accountInfo, s.getSource());
				accountInfo.setId(s.getId());
			} catch (Exception e) {
				e.printStackTrace();
				result.setSuccess(true);
	            result.setCode("-1");
	            result.setMsg("查询成功,BeanUtils:map转AccountInfo失败");
			}
        	
        	
        	result.setResult(accountInfo);
            result.setSuccess(true);
            result.setCode(HttpStatus.OK.toString());
            result.setMsg("查询成功");
            list.add(result);
        }
        return list;
	}
	
}
