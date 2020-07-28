package com.acsz.systemtest.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserExcelService {

	Object writedata(MultipartFile file);

	Object readdata(String fileName);

}
