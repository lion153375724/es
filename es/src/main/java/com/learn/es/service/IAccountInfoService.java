package com.learn.es.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.learn.es.model.AccountInfo;
import com.learn.es.model.ResultDTO;

public interface IAccountInfoService {
	Boolean insertAccountInfo(AccountInfo accountIfo);
	
	Boolean deleteAccountInfo(String id);
	
	Boolean updateAccountInfo(AccountInfo accountInfo);
	
	ResultDTO<AccountInfo> queryAccountInfoById(String id);

	List<ResultDTO<AccountInfo>> queryAccountInfo(String accountName,String nikeName);
}
