package com.acsz.systemtest.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.acsz.systemtest.dao.UserExcelDao;
import com.acsz.systemtest.enums.Gender;
import com.acsz.systemtest.model.UserExcelModel;
import com.acsz.systemtest.utils.EmailUtils;

@Service
public class UserExcelServiceImpl implements UserExcelService {

	private final UserExcelDao userExcelDao;
	private final Environment message;
	
	private static final String DOB_FORMAT = "MM/dd/yyyy";
	private static final String TIMESTAMP_FORMAT = "MM/dd/yyyy hh:mm:ss";

	@Autowired
	public UserExcelServiceImpl(final UserExcelDao userExcelDao, final Environment message) {
		this.userExcelDao = userExcelDao;
		this.message = message;
	}

	@Override
	public Object writedata(final MultipartFile file) {
		String fileName = file.getOriginalFilename();
		Map<String, Object> map = new HashMap<String, Object>();
		List<UserExcelModel> listOfUserExcelData;
		try {
			String workingDirectory = System.getProperty("user.dir");
			String FILE_PATH = workingDirectory + File.separator + fileName;
			File absoluteFile = new File(FILE_PATH);
			if (absoluteFile.createNewFile()) {
				System.out.println("Done" + FILE_PATH);
			} else {
				System.out.println("File already exists! "+FILE_PATH );
			}

			listOfUserExcelData = getListUserExcelModelData(FILE_PATH);
			if (!listOfUserExcelData.isEmpty() && userExcelDao.addData(listOfUserExcelData)) {
				map.put("message", "Successfully added");
			} else {
				map.put("message", "Faided");
			}
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		
		return map;
	}

	@Override
	public Object readdata(final String fileName) {
		Map<String, Object> map = new HashMap<String, Object>();
		String workingDirectory = System.getProperty("user.dir");
		String FILE_PATH = workingDirectory + File.separator;
		if (Objects.isNull(fileName) || fileName.isEmpty()) {
			map.put("message", "Invalid file");
			return map;
		}
		FILE_PATH = FILE_PATH + fileName + ".xlsx";
		List<UserExcelModel> listOfUserExcelData = userExcelDao.getAllData();
		
		List<String> listOfKeys = new ArrayList<String>();
		listOfKeys.add("Name");
		listOfKeys.add("Mobile No");
		listOfKeys.add("Date of birth");
		listOfKeys.add("Gender");
		listOfKeys.add("Appointment Time");
		listOfKeys.add("Date Time");
		
		writeStudentsListToExcel(listOfUserExcelData, FILE_PATH, listOfKeys);

		String email = message.getProperty("user.mail.emailId");
		String[] emailIds = email.split(",");
		boolean isSent = EmailUtils.emailSender(emailIds, "Please find excel file here", "Excel file", FILE_PATH);
		if (isSent) {
			map.put("message", "Mail send successfully");
		} else {
			map.put("message", "Failed");
		}
		return map;
	}

	private List<UserExcelModel> getListUserExcelModelData(final String FILE_PATH) throws ParseException {
		List<UserExcelModel> list = new ArrayList<UserExcelModel>();

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(FILE_PATH);
			Workbook workbook = new XSSFWorkbook(fis);
			int numberOfSheets = workbook.getNumberOfSheets();
			for (int i = 0; i < numberOfSheets; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				@SuppressWarnings("rawtypes")
				Iterator rowIterator = sheet.iterator();
				DataFormatter dataFormatter = new DataFormatter();
				rowIterator.next();

				while (rowIterator.hasNext()) {
					UserExcelModel userExcelModel = new UserExcelModel();
					Row row = (Row) rowIterator.next();
					@SuppressWarnings("rawtypes")
					Iterator cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = (Cell) cellIterator.next();
						int columnIndex = cell.getColumnIndex();
						// Date enrollDate = nextCell.getDateCellValue();
						switch (columnIndex) {
						case 0:
							userExcelModel.setName(cell.getStringCellValue());
							break;
						case 1:
							String str = dataFormatter.formatCellValue(cell);
							userExcelModel.setMob_no(str);
							break;
						case 2:
							String dob = dataFormatter.formatCellValue(cell);
							SimpleDateFormat dobDateformat = new SimpleDateFormat(DOB_FORMAT);
							Date dobDate = dobDateformat.parse(dob);
							userExcelModel.setDob(dobDate);
							break;
						case 3:
							String gender = cell.getStringCellValue();
							if (Gender.MALE.name().equals(gender)) {
								userExcelModel.setGender(Gender.MALE);
							} else {
								userExcelModel.setGender(Gender.FEMALE);
							}
							break;
						case 4:
							String appointmentTime = dataFormatter.formatCellValue(cell);
							SimpleDateFormat formatter = new SimpleDateFormat(TIMESTAMP_FORMAT);
							Date date = formatter.parse(appointmentTime);
							userExcelModel.setAppointment_time(new Timestamp(date.getTime()));
							break;
						case 5:
							String dateTime = dataFormatter.formatCellValue(cell);
							SimpleDateFormat format = new SimpleDateFormat(TIMESTAMP_FORMAT);
							Date dateTimeDate = format.parse(dateTime);
							userExcelModel.setDate_time(new Timestamp(dateTimeDate.getTime()));
							break;
						}
					}

					list.add(userExcelModel);
				}
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static void writeStudentsListToExcel(List<UserExcelModel> list, String FILE_PATH,
			List<String> listOfKeys) {

		Workbook workbook = new XSSFWorkbook();
		Sheet studentsSheet = workbook.createSheet("user");
		
		Row row = null;
		
		int rowIndex = 0;
		if (studentsSheet != null)
			row = studentsSheet.createRow(rowIndex);
		
		int cnt = 0;
		for (String col : listOfKeys) {
			row.createCell(cnt).setCellValue(col);
			cnt++;
		}
		
		for (UserExcelModel userExcelModel : list) {
			row = studentsSheet.createRow(studentsSheet.getLastRowNum()+1);
			int cellIndex = 0;
			row.createCell(cellIndex++).setCellValue(userExcelModel.getName());
			row.createCell(cellIndex++).setCellValue(userExcelModel.getMob_no());
			row.createCell(cellIndex++).setCellValue(new SimpleDateFormat(DOB_FORMAT).format(userExcelModel.getDob().getTime()));
			row.createCell(cellIndex++).setCellValue(userExcelModel.getGender().name());
			row.createCell(cellIndex++).setCellValue(new SimpleDateFormat(TIMESTAMP_FORMAT).format(userExcelModel.getAppointment_time().getTime()));
			row.createCell(cellIndex++).setCellValue(new SimpleDateFormat(TIMESTAMP_FORMAT).format(userExcelModel.getDate_time().getTime()));
		}

		try {
			FileOutputStream fos = new FileOutputStream(FILE_PATH);
			workbook.write(fos);
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
