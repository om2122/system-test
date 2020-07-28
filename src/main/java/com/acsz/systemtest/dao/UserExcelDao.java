package com.acsz.systemtest.dao;

import java.util.List;

import com.acsz.systemtest.model.UserExcelModel;

public interface UserExcelDao {

	boolean addData(List<UserExcelModel> listOfUserExcelData);

	List<UserExcelModel> getAllData();
	
}
