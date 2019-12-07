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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NuroHouseCrawler {
	public static void main(String[] args) {
		
		try {
			FileWriter file		= null;
			PrintWriter pw		= null;
			file = new FileWriter("C:\\Users\\makot\\Documents\\nuroHouse\\nuroHouseFindResult.csv", true);
			pw = new PrintWriter(new BufferedWriter(file));
			
			try {
				System.setProperty("webdriver.chrome.driver", "C:\\Program Files (x86)\\Google\\ChromeDriver78\\chromedriver.exe");
				ChromeOptions options = new ChromeOptions();
				options.addArguments("--headless");
				ChromeDriverService driverService = ChromeDriverService.createDefaultService();
				WebDriver driver = new ChromeDriver(driverService, options);
				driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
				
				driver.get("https://nuro.jp/mansion/service/neworder/");
//				WebElement prefSelectElement = driver.findElement(By.id("pref-list"));
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
//						driver.findElement(By.id("buildings-list"));
						String address = "";
						String buildingName = "";
						//TODO .findElements(By.cssSelector("p.item > span")　でNotFoundも含めてデータを取得すればよい。foreachで回せば何物件あるか知らなくてもいけるだろう。
						String judgmentFoExist = driver.findElement(By.cssSelector("#items > p")).getText();
						System.out.println(judgmentFoExist);
						if (judgmentFoExist.contains("導入済みマンション")) {
							address = "NotFound";
							buildingName = "NotFound";
							System.out.println(buildingName + " " + address);
							pw.println(buildingName + "," + address);
						}	else {
							List<WebElement> buildingNumber = driver.findElements(By.id("items"));
							for (WebElement building : buildingNumber) {
								List<WebElement> buildingInfos = building.findElements(By.cssSelector("p.item > span"));
								List<String> dataStock = new ArrayList<String>();
								for (WebElement buildingInfo : buildingInfos) {
									dataStock.add(buildingInfo.getText());
								}
								address = dataStock.get(0);
								buildingName = dataStock.get(1);
								pw.println(buildingName + "," + address);
							}
							
//							for (int k = 1; k <= buildingNumber.size(); k++) {
//								System.out.println(k);
//								String xPathForAddress = "//*[@id=\"items\"]/p[" + k + "]/span[1]";
//								System.out.println(xPathForAddress);
//								String xPathForBuildingName = "//*[@id=\"items\"]/p[" + k + "]/span[2]";
//								System.out.println(xPathForBuildingName);
//								address = driver.findElement(By.xpath(xPathForAddress)).getText();
//								buildingName = driver.findElement(By.xpath(xPathForBuildingName)).getText();
//								pw.println(buildingName + "," + address);
//							}
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
