package core;

import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.concurrent.TimeUnit;
import java.util.logging.*;
// import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import java.math.*;
import java.text.DecimalFormat;
import java.util.regex.*;

public class IE {
	
	static WebDriver driver;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws InterruptedException {

		Logger logger = Logger.getLogger("");
		logger.setLevel(Level.OFF);

		String driverPath = "./resources/webdrivers/pc/IEDriverServer.exe";
		
		String url = "http://alex.academy/exe/payment/index.html";
		
		if (!System.getProperty("os.name").contains("Windows")) {
			throw new IllegalArgumentException("Internet Explorer is available only on Windows");
			}
			
		DesiredCapabilities IEDesiredCapabilities = DesiredCapabilities.internetExplorer();
		IEDesiredCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		IEDesiredCapabilities.setCapability(InternetExplorerDriver.INITIAL_BROWSER_URL, "");
		IEDesiredCapabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		IEDesiredCapabilities.setJavascriptEnabled(true);
		IEDesiredCapabilities.setCapability("enablePersistentHover", false);

		System.setProperty("webdriver.ie.driver", driverPath);

		driver = new InternetExplorerDriver(IEDesiredCapabilities);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.manage().window().maximize();
		//		WebDriverWait wait = new WebDriverWait(driver, 15);
		
		driver.get(url);
		
		//$1,654.55
		String string_monthly_payment = driver.findElement(By.id("id_monthly_payment")).getText();
		String regex = "^"
		               + "(?:\\$)?"
		               + "(?:\\s*)?"
		               + "((?:\\d{1,3})(?:\\,)?(?:\\d{3})?(?:\\.)?(\\d{0,2})?)"
		               + "$";
		//String regex = "(1,654.55)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(string_monthly_payment);
		m.find();
		// 1,654.55
		double monthly_payment = Double.parseDouble(m.group(1).replaceAll(",", ""));
		// 1654.55 * 12 = 19854.60
		double annual_payment = new BigDecimal(monthly_payment * 12).setScale(2, RoundingMode.HALF_UP).doubleValue();
		DecimalFormat df = new DecimalFormat("0.00");
		// 19854.6 =>19854.60
		String f_annual_payment = df.format(annual_payment);
		driver.findElement(By.id("id_annual_payment")).sendKeys(String.valueOf(f_annual_payment));
		driver.findElement(By.id("id_validate_button")).submit();
		String actual_result = driver.findElement(By.id("id_result")).getText();
		System.out.println("String: \t" + string_monthly_payment);
		System.out.println("Annual Payment: " + f_annual_payment);
		System.out.println("Result: \t" + actual_result);
		driver.quit();
	}

}
