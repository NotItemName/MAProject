package com.epam.preprod.ma.bean;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.epam.preprod.ma.dao.entity.Dimension;
import com.epam.preprod.ma.dao.entity.Result;
import com.epam.preprod.ma.dao.entity.Survey;

/**
 * 
 * @author Alexandra Martyntseva
 * 
 */
public class MotivationMapBean implements
		Iterable<Entry<Survey, SortedMap<Dimension, Result>>> {

	private static final Logger log = Logger.getLogger(MotivationMapBean.class);

	/**
	 * Keys are surveys, values are maps of results for each dimension. So this
	 * map can be used to store data for detailed charts for one survey and for
	 * several surveys. Each value is a SortedMap so aggregated results for each
	 * dimension are sorted in natural order of the {@link Dimension} enum. It
	 * is useful for retrieving results to draw charts, where order is important
	 * and must be predictable.
	 */
	private SortedMap<Survey, SortedMap<Dimension, Result>> map = new TreeMap<>(
			new Comparator<Survey>() {
				@Override
				public int compare(Survey o1, Survey o2) {
					return o1.getEndDate().compareTo(o2.getEndDate());
				}
			});

	public void add(Survey survey, Dimension dimension, Result values) {
		if (log.isTraceEnabled()) {
			log.trace("adding surveyId=" + survey.getId() + ", dimension="
					+ dimension + ", values=[ satisf="
					+ values.getSatisfaction() + ", prior="
					+ values.getPriority() + " ]");
		}
		if (map.containsKey(survey)) {
			map.get(survey).put(dimension, values);
		} else {
			SortedMap<Dimension, Result> results = new TreeMap<>();
			for (Dimension dim : Dimension.values()) {
				results.put(dim, new Result());
			}
			results.put(dimension, values);
			map.put(survey, results);
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
	 * @return aggregated by dimension results for 1 survey
	 */
	public Collection<Result> getResults() {
		return map.get(map.firstKey()).values();
	}

	@Override
	public String toString() {
		return map.toString();
	}

	@Override
	public Iterator<Entry<Survey, SortedMap<Dimension, Result>>> iterator() {
		return map.entrySet().iterator();
	}
}
