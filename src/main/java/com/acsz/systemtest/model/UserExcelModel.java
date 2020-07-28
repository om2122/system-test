package com.acsz.systemtest.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.acsz.systemtest.enums.Gender;

@Entity
@Table(name = "user_excel")
public class UserExcelModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sno", nullable = false)
	private Integer sno;

	@Column(name = "name")
	private String name;

	@Column(name = "mob_no")
	private String mob_no;

	@Column(name = "dob")
	private Date dob;

	@Column(name = "gender")
	private Gender gender;

	@Column(name = "appointment_time")
	private Date appointment_time;

	@Column(name = "date_time")
	private Date date_time;

	public Integer getSno() {
		return sno;
	}

	public void setSno(Integer sno) {
		this.sno = sno;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMob_no() {
		return mob_no;
	}

	public void setMob_no(String mob_no) {
		this.mob_no = mob_no;
	}

	public Date getDob() {
		return dob;
	}

	public void setDob(Date dob) {
		this.dob = dob;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Date getAppointment_time() {
		return appointment_time;
	}

	public void setAppointment_time(Date appointment_time) {
		this.appointment_time = appointment_time;
	}

	public Date getDate_time() {
		return date_time;
	}

	public void setDate_time(Date date_time) {
		this.date_time = date_time;
	}

}
