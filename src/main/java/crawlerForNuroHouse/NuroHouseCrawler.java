package crawlerForNuroHouse;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NuroHouseCrawler {
	public static void main(String[] args) {
		
		try {
			FileWriter file		= null;
			PrintWriter pw		= null;
			file = new FileWriter("C:\\Users\\makot\\Documents\\nuroHouse\\nuroHouseFindResultAddress.csv", true);
			pw = new PrintWriter(new BufferedWriter(file));
			FileWriter file1	= null;
			PrintWriter pw1		= null;
			file1 = new FileWriter("C:\\Users\\makot\\Documents\\nuroHouse\\nuroHouseFindResultBuildingName.csv", true);
			pw1 = new PrintWriter(new BufferedWriter(file1));
			
			try {
				System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\ChromeDriver78\\chromedriver.exe");
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--headless");
				ChromeDriverService driverService = ChromeDriverService.createDefaultService();
				WebDriver driver = new ChromeDriver(driverService, options);
				driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
				
				driver.get("https://nuro.jp/mansion/service/neworder/");
				Select dropDownPref = new Select(driver.findElement(By.id("pref-list")));
				dropDownPref.selectByValue("25");
				Select dropDownCity = new Select(driver.findElement(By.id("city-list")));
				String cityNumber = "";
				int townValue[] = {115, 98, 117, 157, 68, 108, 104, 156, 130, 88, 216, 277, 80, 85, 139, 83, 113, 52, 134, 202, 269, 155, 201};
				
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
						String buildingName = "";
						List<WebElement> buildingList = driver.findElements(By.cssSelector("#items > p > span.label"));
						for (WebElement building : buildingList) {
							address = building.getText();
							System.out.println(address);
							pw.println(address);
						}
						List<WebElement> buildingNameList = driver.findElements(By.cssSelector("#items > p > span.selectable"));
						for (WebElement buildingNameElement : buildingNameList) {
							buildingName = buildingNameElement.getText();
							System.out.println(buildingName);
							pw1.println(buildingName);
						}
						
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
}
