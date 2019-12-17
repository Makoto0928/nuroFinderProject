package crawlerForNuroHouse;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class NuroHouseCrawler {
	
	private static String goeCodingApiKey() {
		ResourceBundle rb = ResourceBundle.getBundle("googleGeocodingApi");
		return rb.getString("apiKey");
	}
	private static GeoApiContext context = new GeoApiContext.Builder()
			.apiKey(goeCodingApiKey())
			.build();
	
	
	public static void main(String[] args) {
		
		try {
			Scanner scanner = new Scanner(System.in);
			FileWriter file		= null;
			PrintWriter pw		= null;
			System.out.print("OutPutCsvFilePath: ");
			String csvFilePathForAddress = scanner.nextLine();
			file = new FileWriter(csvFilePathForAddress, true);
			pw = new PrintWriter(new BufferedWriter(file));
//			FileWriter file1	= null;
//			PrintWriter pw1		= null;
//			System.out.print("csvFilePathForBuildingName: ");
//			String csvFilePathForBuildingName = scanner.nextLine();
//			file1 = new FileWriter(csvFilePathForBuildingName, true);
//			pw1 = new PrintWriter(new BufferedWriter(file1));
			
			try {
				ResourceBundle rbChromeDriverFilePath = ResourceBundle.getBundle("webDriverFilePath");
				String chromeDriverFilePath = rbChromeDriverFilePath.getString("webDriverPath.chromeDriverPath");
				System.setProperty("webdriver.chrome.driver", chromeDriverFilePath);
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--headless");
				ChromeDriverService driverService = ChromeDriverService.createDefaultService();
				WebDriver driver = new ChromeDriver(driverService, options);
				driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
				driver.get("https://nuro.jp/mansion/service/neworder/");
				
				// Only for Tokyo23
				Select dropDownPref = new Select(driver.findElement(By.id("pref-list")));
				dropDownPref.selectByValue("25");
				Select dropDownCity = new Select(driver.findElement(By.id("city-list")));
				String cityNumber = "";
				int townValue[] = {115, 98, 117, 157, 68, 108, 104, 156, 130, 88, 216, 277, 80, 85, 139, 83, 113, 52, 134, 202, 269, 155, 201};
				pw.println("Address,Lat,Lng");
				System.out.println("Address,Lat,Lng");
				for (int i = 0; i < 23; i++) {
					cityNumber = Integer.toString(i);
					dropDownCity.selectByValue(cityNumber);
					Select dropDownTown = new Select(driver.findElement(By.id("town-list")));
					int selectedTownMax = townValue[i];
					String townNumber = "";
					for (int j = 0; j < selectedTownMax; j++) {
						townNumber = Integer.toString(j);
						dropDownTown.selectByValue(townNumber);
						String address = "";
//						String buildingName = "";
						List<WebElement> buildingList = driver.findElements(By.cssSelector("#items > p > span.label"));
						for (WebElement building : buildingList) {
							address = building.getText();
							GeocodingResult[] results = getResults(address);
							if (results != null && results.length > 0) {
								LatLng latLng = results[0].geometry.location;
								System.out.println("Address: " + address + ", " + "Lat: " + latLng.lat + ", " + "Lng: " + latLng.lng);
								pw.println(address + "," + latLng.lat + "," + latLng.lng);
							}
						}
//						List<WebElement> buildingNameList = driver.findElements(By.cssSelector("#items > p > span.selectable"));
//						for (WebElement buildingNameElement : buildingNameList) {
//							buildingName = buildingNameElement.getText();
//							System.out.println(buildingName);
//							pw1.println(buildingName);
//						}
						
						try {
							Thread.sleep(5);
						}	catch (InterruptedException ie) {
							System.out.println("Thread Sleep ERROR");
							ie.printStackTrace();
						}
					}
					try {
						Thread.sleep(5);
					}	catch (InterruptedException ie) {
						System.out.println("Thread Sleep ERROR");
						ie.printStackTrace();
					}
				}
				pw.close();
				System.out.println("FINISH!");
			}	catch (Exception e) {
				System.out.println("==========Crawling Error ==========");
				e.printStackTrace();
			}	finally {
				pw.close();
			}
		}	catch (Exception ex) {
			System.out.println("+++++ERROR+++++");
			ex.printStackTrace();
		}
	}
	
	
	/** Google Geocoding API handling */
	public static GeocodingResult[] getResults(String address) throws ApiException, InterruptedException, IOException {
		GeocodingApiRequest req = GeocodingApi.newRequest(context)
				.address(address)
				.language("ja");
		try {
			GeocodingResult[] results = req.await();
			if (results == null || results.length == 0) {
				System.out.println("zero results.");
			}
			return results;
		}	catch (ApiException e) {
			System.out.println("geocode failed.");
			e.printStackTrace();
			throw e;
		}	catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
			throw e;
		}	catch (InterruptedException e) {
			System.out.println("InterruptedException");
			e.printStackTrace();
			throw e;
		}
	}
}
