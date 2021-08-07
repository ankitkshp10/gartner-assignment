package com.gartner.clicks.solution;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gartner.clicks.clicksPojo.Clicks;
import com.gartner.clicks.sortClicks.SortClicksComparator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

@Component
public class ClicksSolution {
	private static final Logger LOG = LoggerFactory.getLogger(ClicksSolution.class);

	List<Clicks> resultSet = new ArrayList<>();
	String source = "C:/Users/akashya5/Desktop/Gart/clicks.json";
	String destination = "C:/Users/akashya5/Desktop/Gart/resultSet.json";

	public void solution() {
		this.processJsonFile();
	}

	private void processJsonFile() {
		try {
			byte[] mapData = Files.readAllBytes(Paths.get(source));
			Clicks[] clicks = null;
			clicks = new ObjectMapper().readValue(mapData, Clicks[].class);
			List<Clicks> studentList = Arrays.asList(clicks);
			this.filterList(studentList);
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * in this method, we will remove the IP's having frequency of more than 10
	 * records
	 */
	private void filterList(List<Clicks> clicksList) throws FileNotFoundException {
		// retrieving only ip's from the result set.
		List<String> ipList = clicksList.stream().map(this::getIps).collect(Collectors.toList());

		// create a map of ip's and its count in result set.
		Map<String, Integer> ipCount = new HashMap<>();
		for (String ip : ipList) {
			ipCount.put(ip, Collections.frequency(ipList, ip));
		}

		// create a list of the ip's with frequency with more than 10
		List<String> removeIps = new ArrayList<>();
		ipCount.forEach((k, v) -> {
			if (v > 10)
				removeIps.add(k);
		});

		// remove ip's 'removeIps' from the clickList and create a new list.
		ArrayList<Clicks> filteredList = new ArrayList<>(clicksList);

		for (int i = 0; i < removeIps.size(); i++) {
			String ip = removeIps.get(i);
			filteredList.removeIf(o -> o.getIp().equals(ip));
		}

		this.filterRecordsByClickPeriods(filteredList);
	}

	// method to retrieve ip for every record.
	private String getIps(Clicks clicks) {
		return clicks.getIp();
	}

	// in this method, we will make a map of records w.r.t. every hour, there will
	// be 24 periods.
	private void filterRecordsByClickPeriods(ArrayList<Clicks> filteredList) throws FileNotFoundException {
		// create a map of click period and the list of click records.
		Map<Integer, List<Clicks>> clickPeriods = new HashMap<>();
		int lower = 0; // lower limit of hour, corresponds to lower limit of click period.
		int upper = 1; // upper limit of hour, corresponds to upper limit of click period.
		List<Clicks> list = null; // initialize the list.

		for (int i = 1; i < 25; i++) {
			list = new ArrayList<>(); // create a list for every click period.
			for (Clicks c : filteredList) {
				if ((Integer.parseInt(c.getTimestamp().substring(10, 12)) >= lower)
						&& (Integer.parseInt(c.getTimestamp().substring(10, 12)) < upper)) {
					list.add(c); // add record to list.
				}
			}
			clickPeriods.put(i, list);
			lower++;
			upper++;
		}

		this.processClickPeriods(clickPeriods);
	}

	private void processClickPeriods(Map<Integer, List<Clicks>> clickPeriods) throws FileNotFoundException {
		Map<String, Clicks> clicksMap = null; //
		for (Map.Entry<Integer, List<Clicks>> record : clickPeriods.entrySet()) {
			if (record.getValue() == null || record.getValue().isEmpty()) {
				LOG.debug("no record exists for this click period.");
			}

			else if (record.getValue().size() == 1) {
				for (Clicks rec : record.getValue()) {
					this.insertRecord(rec);
				}
			} else {
				Collections.sort(record.getValue(), new SortClicksComparator());
				clicksMap = new HashMap<>();
				for (Clicks click : record.getValue()) {
					clicksMap.put(click.getIp(), click);
				}

				for (Map.Entry<String, Clicks> finalMap : clicksMap.entrySet()) {
					this.insertRecord(finalMap.getValue());
				}
			}
		}

		this.convertListToJsonFormat(resultSet);
	}

	private void convertListToJsonFormat(List<Clicks> result) throws FileNotFoundException {
		Gson gson = new GsonBuilder().create();
		JsonArray resultJson = gson.toJsonTree(result).getAsJsonArray();
		this.writeFile(resultJson);

	}

	private void writeFile(JsonArray resultJson) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(destination);
		pw.write(resultJson.toString());
		pw.flush();
		pw.close();

	}

	// method to add records to resultSet.
	private void insertRecord(Clicks click) {
		resultSet.add(click);
	}

}
