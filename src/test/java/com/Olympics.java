package com;


import java.util.Map.Entry;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

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
		List<WebElement> rankList = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[1]"));
		// remove the last cell
		rankList.remove(rankList.size() - 1);
		// confirm that rankList is sorted
		Assert.assertTrue(isSorted(rankList));

		// STEP 3-4
		// click on NOC link
		driver.findElement(By.xpath("//caption[.='2016 Summer Olympics medal table']//..//th[.='NOC']")).click();

		// collect all country names
		List<WebElement> countryList = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/th/a"));
		// confirm if countryList is sorted
		Assert.assertTrue(isSorted(countryList));

		// STEP 5
		// collect all first column cells
		rankList = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[1]"));
		// confirm that rankList is not ordered any more
		Assert.assertFalse(isSorted(rankList));
	}

	@Test
	public void testCase2() {
		driver.get("https://en.wikipedia.org/wiki/2016_Summer_Olympics");

		Assert.assertEquals(mostMedalCountryFinder("gold"), "United States");
		Assert.assertEquals(mostMedalCountryFinder("silver"), "United States");
		Assert.assertEquals(mostMedalCountryFinder("bronze"), "United States");
		Assert.assertEquals(mostMedalCountryFinder("all"), "United States");
	}

	@Test
	public void testCase3() {
		driver.get("https://en.wikipedia.org/wiki/2016_Summer_Olympics");
		List<WebElement> countryLst = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/th/a"));
		List<WebElement> silverLst = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[3]"));

		Assert.assertEquals(getCountryByMedalCount(getMap(countryLst, silverLst), 18), Arrays.asList("China", "France"));
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
		List<WebElement> countryLst = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/th/a"));
		List<WebElement> bronzeLst = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[4]"));
		// remove the last row of bronzeList
		bronzeLst.remove(bronzeLst.size() - 1);

		List<String> result = new ArrayList<>();
		Map<String, Integer> map = getMap(countryLst, bronzeLst);
		Set<Entry<String, Integer>> entries = map.entrySet();

		for (Entry<String, Integer> entry1 : entries) {
			for (Entry<String, Integer> entry2 : entries) {
				if (!entry1.getKey().equals(entry2.getKey()) && !result.contains(entry1.getKey())
						&& entry1.getValue() + entry2.getValue() == 18) {
					result.add(entry1.getKey());
					result.add(entry2.getKey());
				}
			}
		}
		Assert.assertEquals(result, Arrays.asList("Italy", "Australia"));
	}

	
	/*
	 *  ALL METHODS BEYOND THIS POINT ARE UTILITY (HELPER) METHODS
	 */
	
	
	// Method to get row and column of given country
	public String getCountryRowColumn(String countryName) {
		int rowCount = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr")).size();
		int columnCount = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../thead/tr/th")).size();

		String[][] arr = new String[rowCount][columnCount];

		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < columnCount; j++) {
				arr[i][j] = driver.findElement(By.xpath("(//caption[.='2016 Summer Olympics medal table']//../tbody/tr[" + (i + 1) + "]/*)[" + (j + 1) + "]")).getText();
				if (arr[i][j].contains(countryName)) {
					return (i + 1) + " " + (j + 1);
				}
			}
		}
		return "Couldn't find the country in table";
	}

	
	// Method to check if given list sorted
	public boolean isSorted(List<WebElement> lst) {

		List<Integer> lstInt = new ArrayList<>();
		List<String> lstStr = new ArrayList<>();

		// Check if content numeric such as Integer
		if(!lst.isEmpty() && Character.isDigit(lst.get(0).getText().charAt(0))) {
			for (WebElement each : lst) {
				lstInt.add(Integer.valueOf(each.getText()));
			}
			List<Integer> lstIntCopy = new ArrayList<>(lstInt);
			Collections.sort(lstInt);
			return lstInt.equals(lstIntCopy);
		} else { // content is not numeric, then check sorting by alphabetic order
			for (WebElement each : lst) {
				lstStr.add(each.getText());
			}
			List<String> lstStrCopy = new ArrayList<>(lstStr);
			return lstStr.equals(lstStrCopy);
		}
	}

	
	// Method returns the country name with the most medals of given type
	public String mostMedalCountryFinder(String medalType) {

		// medalColumn is the column number of given medal type
		int medalColumn = 0;
		switch (medalType) {
		case "gold":	medalColumn = 2; break;
		case "silver":	medalColumn = 3; break;
		case "bronze":	medalColumn = 4; break;
		case "all":		medalColumn = 5; break;
		}

		List<WebElement> countryLst = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/th/a"));
		List<WebElement> medalLst = driver.findElements(By.xpath("//caption[.='2016 Summer Olympics medal table']//../tbody/tr/td[" + medalColumn + "]"));
		// remove the last row
		medalLst.remove(medalLst.size() - 1);

		return findMost(getMap(countryLst, medalLst));

	}

	
	// Method returns country name with the most medal count given Map of country and their medal counts
	public String findMost(Map<String, Integer> map) {
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

	
	// Method returns Map from given two lists (lst1 items as key, lst2 items as value
	public Map<String, Integer> getMap(List<WebElement> lst1, List<WebElement> lst2) {
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < lst1.size(); i++) {
			map.put(lst1.get(i).getText(), Integer.valueOf(lst2.get(i).getText()));
		}
		return map;
	}

	
	// Method returns the country name of given medal count from lists of countries and corresponding medal counts
	public List<String> getCountryByMedalCount(Map<String, Integer> map, int n) {
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