package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Olympics {
	WebDriver driver;

	@BeforeClass
	public void setup() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
	}
	
	
	@AfterClass
	public void tearDown() {
		driver.quit();
	}

	@Test
	public void testCase1() {
		driver.get("https://en.wikipedia.org/wiki/2016_Summer_Olympics#Medal_table");

		// STEP 1-2
		// collect all first column cells
		List<WebElement> rankList = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[1]"));
		// remove the last cell
		rankList.remove(rankList.size() - 1);
		String comparisonType = "numerical";
		Assert.assertTrue(isSorted(rankList, comparisonType));

		// STEP 3-4
		// click on NOC link
		driver.findElement(By.xpath("//caption[.='2016 Summer Olympics medal table']//..//th[.='NOC']")).click();

		// collect all country names
		List<WebElement> countryList = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/th/a"));
		comparisonType = "text";
		Assert.assertTrue(isSorted(countryList, comparisonType));

		// STEP 5
		// collect all first column cells
		rankList = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[1]"));
		comparisonType = "numerical";
		// confirm that rankList is not ordered any more
		Assert.assertFalse(isSorted(rankList, comparisonType));
	}

	@Test
	public void testCase2() {
		driver.get("https://en.wikipedia.org/wiki/2016_Summer_Olympics");

		Assert.assertEquals(mostGoldMedal(), "United States");
		Assert.assertEquals(mostSilverMedal(), "United States");
		Assert.assertEquals(mostBronzeMedal(), "United States");

	}

	@Test
	public void testCase3() {
		driver.get("https://en.wikipedia.org/wiki/2016_Summer_Olympics");

		List<WebElement> countryLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/th/a"));
		List<WebElement> silverLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[3]"));

		Assert.assertEquals(getCountryByMedalCount(countryLst, silverLst, 18), Arrays.asList("China", "France"));
	}

	@Test
	public void testCase4() {
		driver.get("https://en.wikipedia.org/wiki/2016_Summer_Olympics");
		String countryName = "Japan";
		Assert.assertEquals(getCountryRowColumn(countryName), "6 2");
	}

	@Test
	public void testCase5() {
		driver.get("https://en.wikipedia.org/wiki/2016_Summer_Olympics");
		List<WebElement> countryLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/th/a"));
		List<WebElement> bronzeLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[4]"));
		// remove the last row of bronzeList
		bronzeLst.remove(bronzeLst.size() - 1);
		
		List<String> result = new ArrayList<>();
		Map<String, Integer> map = getMap(countryLst, bronzeLst);

		Set<Entry<String, Integer>> entries = map.entrySet();

		for (Entry<String, Integer> entry1 : entries) {
			for (Entry<String, Integer> entry2 : entries) {
				if(!entry1.getKey().equals(entry2.getKey()) && !result.contains(entry1.getKey()) && entry1.getValue() + entry2.getValue() == 18) {
					result.add(entry1.getKey());
					result.add(entry2.getKey());
				}
			}
		}
		Assert.assertEquals(result, Arrays.asList("Italy","Australia"));
	}

	
	// Method to get row and column of given country
	public String getCountryRowColumn(String countryName) {
		int rowCount = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr"))
				.size();
		int columnCount = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../thead/tr/th")).size();

		String[][] arr = new String[rowCount][columnCount];

		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				arr[i][j] = driver.findElement(By.xpath("(//caption[.='2016 Summer Olympics medal table']//../tbody/tr["
						+ (i + 1) + "]/*)[" + (j + 1) + "]")).getText();
				if (arr[i][j].contains(countryName)) {
					return (i + 1) + " " + (j + 1);
				}
			}
		}
		return "Couldn't find the country in table";
	}

	
	// Method to check if given list sorted by the given content type
	public boolean isSorted(List<WebElement> lst, String type) {

		List<Integer> lstInt = new ArrayList<>();
		List<String> lstStr = new ArrayList<>();

		if (type.equals("numerical")) {
			for (WebElement each : lst) {
				lstInt.add(Integer.valueOf(each.getText()));
			}
			List<Integer> lstIntCopy = new ArrayList<>(lstInt);
			Collections.sort(lstInt);
			return lstInt.equals(lstIntCopy);
		} else {
			for (WebElement each : lst) {
				lstStr.add(each.getText());
			}
			List<String> lstStrCopy = new ArrayList<>(lstStr);
			return lstStr.equals(lstStrCopy);
		}
	}

	
	// Method returns the country name with the most gold medals
	public String mostGoldMedal() {

		List<WebElement> countryLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/th/a"));
		List<WebElement> goldLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[2]"));

		return findMost(countryLst, goldLst);
	}

	
	// Method returns the country name with the most silver medals
	public String mostSilverMedal() {

		List<WebElement> countryLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/th/a"));
		List<WebElement> silverLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[3]"));

		return findMost(countryLst, silverLst);
	}

	
	// Method returns the country name with the most bronze medals
	public String mostBronzeMedal() {

		List<WebElement> countryLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/th/a"));
		List<WebElement> bronzeLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[4]"));

		return findMost(countryLst, bronzeLst);
	}

	
	// Method returns the country name with the most medals
	public String mostMedal() {

		List<WebElement> countryLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/th/a"));
		List<WebElement> medalLst = driver
				.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[5]"));

		return findMost(countryLst, medalLst);
	}

	
	// Method returns country name with the most medal count give country and medal count lists
	public String findMost(List<WebElement> countryLst, List<WebElement> medalCountLst) {
		// remove the last row
		medalCountLst.remove(medalCountLst.size() - 1);

		Map<String, Integer> map = getMap(countryLst, medalCountLst);

		Set<Entry<String, Integer>> entries = map.entrySet();

		String result = "";
		int max = 0;

		for (Entry<String, Integer> each : entries) {

			if (each.getValue() > max) {
				max = each.getValue();
				result = each.getKey();
			}
		}
		return result;
	}

	
	// Method returns Map from given two lists (lst1 items as key, lst2 items as value)
	public Map<String, Integer> getMap(List<WebElement> lst1, List<WebElement> lst2) {
		Map<String, Integer> map = new HashMap<>();

		for (int i = 0; i < lst1.size(); i++) {
			map.put(lst1.get(i).getText(), Integer.valueOf(lst2.get(i).getText()));
		}

		return map;
	}

	
	// Method returns the country name of given medal count from lists of countries and corresponding medal counts
	public List<String> getCountryByMedalCount(List<WebElement> countryLst, List<WebElement> medalCountLst, int n) {
		Map<String, Integer> map = getMap(countryLst, medalCountLst);
		List<String> result = new ArrayList<>();
		Set<Entry<String, Integer>> entries = map.entrySet();
		for (Entry<String, Integer> each : entries) {
			if (each.getValue() == n) {
				result.add(each.getKey());
			}
		}
		return result;
	}
}
