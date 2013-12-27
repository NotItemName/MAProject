package com.epam.preprod.ma.bean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.epam.preprod.ma.dao.entity.Survey;

/**
 * 
 * @author Alexandra Martyntseva
 * 
 */
public class DetailedChartBean implements
		Iterable<Entry<Survey, List<DashboardQuestionBean>>> {

	private static final Logger log = Logger.getLogger(DetailedChartBean.class);

	private SortedMap<Survey, List<DashboardQuestionBean>> map = new TreeMap<>(
			new Comparator<Survey>() {
				@Override
				public int compare(Survey o1, Survey o2) {
					return o1.getEndDate().compareTo(o2.getEndDate());
				}
			});

	public void add(Survey survey, DashboardQuestionBean question) {
		if (log.isTraceEnabled()) {
			log.trace("adding surveyId=" + survey.getId() + ", question="
					+ question);
		}
		if (map.containsKey(survey)) {
			map.get(survey).add(question);
		} else {
			List<DashboardQuestionBean> list = new ArrayList<>();
			list.add(question);
			map.put(survey, list);
		}
	}

	/**
	 * 
	 * @return number of surveys which results this bean stores
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Should be used only if method {@link #size()} returns 1, otherwise result
	 * is unpredictable.
	 * 
	 * @return list of aggregated by question results of 1 survey
	 */
	public List<DashboardQuestionBean> getResults() {
		return map.get(map.firstKey());
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public Iterator<Entry<Survey, List<DashboardQuestionBean>>> iterator() {
		return map.entrySet().iterator();
	}
}
