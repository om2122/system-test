package com.acsz.systemtest.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.acsz.systemtest.service.UserExcelService;

@RestController
public class UserExcelRest {

	private final UserExcelService userExcelService;

	@Autowired
	public UserExcelRest(final UserExcelService userExcelService) {
		this.userExcelService = userExcelService;
	}
	
	@PostMapping("/write")
	public ResponseEntity<Object> writedata(@RequestParam(value = "file") MultipartFile file) {
		return ResponseEntity.status(HttpStatus.OK).body(userExcelService.writedata(file));
	}
	
	@GetMapping("/read")
	public ResponseEntity<Object> readdata(@RequestParam(value = "fileName") String fileName) {
		return ResponseEntity.status(HttpStatus.OK).body(userExcelService.readdata(fileName));
	}

}
