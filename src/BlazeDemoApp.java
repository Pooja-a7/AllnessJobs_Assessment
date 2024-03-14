import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class BlazeDemoApp {

    private WebDriver driver;
    private WebDriverWait wait;

    
	@BeforeClass
    public void setUp() {
        // Set Chrome driver path
		System.setProperty("webdriver.chrome.driver","/Users/Admin/Documents/chromedriver.exe");

        // Set Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        // Initialize ChromeDriver
        driver = new ChromeDriver(options);
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        
    }

    @Test(priority = 1)
    public void verifyHomePageTitle() {
    	
    	//Navigate to the url
        driver.navigate().to("https://blazedemo.com/index.php");
        String expectedHeader = "Welcome to the Simple Travel Agency!";
        String actualHeader = driver.findElement(By.xpath("//div[@class='container']/h1")).getText();
        System.out.println(actualHeader);
        
        Assert.assertEquals(actualHeader, expectedHeader, "Home page title is not as expected.");
    }

    @Test(priority = 2)
    public void navigateToDestinationOfTheWeek() {
        WebElement destinationLink = driver.findElement(By.linkText("destination of the week! The Beach!"));
        destinationLink.click();
        
        String currentUrl = driver.getCurrentUrl();
        System.out.println(driver.getTitle());
        Assert.assertTrue(currentUrl.contains("vacation"), "New tab URL doesn't contain 'vacation' string.");
        //Navigate back to Home Page
        driver.navigate().back();
        
    }

    @Test(priority = 3, dataProvider = "flightData")
    public void purchaseTicket(String departureCity, String destinationCity) {
    	
        // Fill departure and destination cities
        Select departureDropdown = new Select(driver.findElement(By.name("fromPort")));
        departureDropdown.selectByVisibleText(departureCity);
        Select destinationDropdown = new Select(driver.findElement(By.name("toPort")));
        destinationDropdown.selectByVisibleText(destinationCity);
        
        // Click 'Find Flights'
        driver.findElement(By.cssSelector("input[type='submit']")).click();
        
        // Wait for flight results to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input[type='submit'][value='Choose This Flight']")));
        
        int Col = driver.findElements(By.xpath("//table[@class='table']//tr/th")).size();
        for(int i=1;i<=Col;i++)
        {
 			String ColName= driver.findElement(By.xpath("//table[@class='table']//tr/th["+i+"]")).getText();
  			
 				if(ColName.equals("Price"))
    			{
    				 System.out.println("Price header is Present at "+ " " + i +"th " +"column");
    				 List<WebElement> prices=driver.findElements(By.xpath("//table[@class='table']//tr/td[" + i + "]"));
    				 
    				 double lowestPrice = Double.MAX_VALUE;
    				 for (WebElement e : prices) {
    					 
    		                // Get the text content of the WebElement
    		                String numericString = e.getText();
    		                String amountWithoutDollarSign = numericString.replace("$", "");
    		                
    		                // Parse the text content to a double
    		                double numericValue = Double.parseDouble(amountWithoutDollarSign);
    		                if (numericValue < lowestPrice) {
    		                    lowestPrice = numericValue;
    		                    
    		                }
    		            }System.out.println("Lowest price in the table is:" + lowestPrice);
    		            
    		            int rows= driver.findElements(By.xpath("//table[@class='table']//tr")).size();
    		            System.out.println(rows);
    	 				for(int r=1;r<rows;r++)
    	 				{
    	 					WebElement rowElement=driver.findElement(By.xpath("//table[@class='table']//tr["+r+"]/td["+i+"]"));
    	 					
    	 					if (rowElement.getAttribute("textContent").replace("$", "").contains(Double.toString(lowestPrice)))
    	 					{
    	 					
    	 						wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//input[@value='Choose This Flight'])["+r+"]")));
    	 						driver.findElement(By.xpath("(//input[@value='Choose This Flight'])["+r+"]")).click();
    	 						break;
    	 					}
    	 				}
    		              			
    			}
 				

    	}
        //Wait for the purchase page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(),'Total Cost')]")));
        
        // Check if 'Total Cost' field is available
        Assert.assertTrue(driver.findElement(By.xpath("//p[contains(text(),'Total Cost')]")).isDisplayed(), "Total Cost field not available.");
        
        // Click on 'Purchase Flight' button
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@value='Purchase Flight']")));
        driver.findElement(By.xpath("//input[@value='Purchase Flight']")).click();
        
       // Wait for purchase confirmation page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(), 'Thank you for your purchase today!')]")));
        
       // Get the purchase ID and log it
        String purchaseId = driver.findElement(By.xpath("//td[text()='Id']/following-sibling::td")).getText();
        System.out.println("Purchase ID to be stored: " + purchaseId);
        
        
    }	
    	

    @AfterClass
    public void tearDown() {
        driver.quit();
    }

    // Data provider for test data
    
    @DataProvider(name = "flightData")
    public Object[][] getFlightData() {
        return new Object[][]{
                {"Mexico City", "London"},
                
                
        };
    }
}
