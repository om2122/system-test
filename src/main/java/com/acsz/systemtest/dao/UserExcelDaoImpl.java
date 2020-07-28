package com.acsz.systemtest.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.acsz.systemtest.model.UserExcelModel;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class UserExcelDaoImpl implements UserExcelDao {

	@Autowired
	SessionFactory sessionFactory;

	@Override
	@Transactional
	public boolean addData(List<UserExcelModel> listOfUserExcelData) {
		boolean isAdded = false;
		try (Session session = this.sessionFactory.openSession()) {
			if (!listOfUserExcelData.isEmpty()) {
				listOfUserExcelData.forEach(action -> {
					session.save(action);
				});
			}
			isAdded = true;
		} catch (HibernateException e) {
			throw e;
		}
		return isAdded;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<UserExcelModel> getAllData() {
		List<UserExcelModel> list = new ArrayList<UserExcelModel>();
		try (Session session = this.sessionFactory.openSession()) {
			String sql = "select * from user_excel";
			@SuppressWarnings("deprecation")
			List<Map<?, ?>> lisOfMap = (List<Map<?, ?>>) session.createSQLQuery(sql)
					.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
			list = lisOfMap.stream().map(object -> mapper.convertValue(object, UserExcelModel.class))
					.collect(Collectors.toList());

		} catch (HibernateException e) {
			throw e;
		}
		return list;
	}

}
