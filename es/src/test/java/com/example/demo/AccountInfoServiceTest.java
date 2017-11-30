package com.example.demo;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.learn.es.ESApplication;
import com.learn.es.model.AccountInfo;
import com.learn.es.model.ResultDTO;
import com.learn.es.service.impl.AccountInfoService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ESApplication.class)
public class AccountInfoServiceTest {

	@Autowired
	private AccountInfoService accountInfoService;

	@Test
	public void insert(){
		for(int i=5; i<10; i++){
			AccountInfo accountInfo = new AccountInfo();
			accountInfo.setId(String.valueOf(i));
			accountInfo.setAccountName("jason"+i);
			accountInfo.setNickName("jason_nn"+i);
			System.out.println(accountInfoService.insertAccountInfo(accountInfo));
		}
	}
	
	@Test
	public void update(){
		AccountInfo accountInfo = new AccountInfo();
		accountInfo.setId(String.valueOf(4));
		accountInfo.setAccountName("jason");
		accountInfo.setNickName("jason_nn");
		System.out.println(accountInfoService.updateAccountInfo(accountInfo));
	}
	
	@Test
	public void delete(){
		System.out.println(accountInfoService.deleteAccountInfo("1"));
	}
	
	@Test
	public void queryById(){
		ResultDTO<AccountInfo> result = accountInfoService.queryAccountInfoById("2");
		System.out.println(result.getMsg() + ":" + result.getCode() + ":" + result.isSuccess());
		AccountInfo account = result.getResult();
		if(null != account){
			System.out.println(account.getId() + ":" + account.getAccountName() + ":" + account.getNickName());
		}
	}
	
	@Test
	public void queryAccountInfo(){
		List<ResultDTO<AccountInfo>> list = accountInfoService.queryAccountInfo("jason", "");
		for(ResultDTO<AccountInfo> result : list){
			System.out.println(result.getMsg() + ":" + result.getCode() + ":" + result.isSuccess());
			AccountInfo account = result.getResult();
			if(null != account){
				System.out.println(account.getId() + ":" + account.getAccountName() + ":" + account.getNickName());
			}
		}
	}
}
