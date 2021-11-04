package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends Page{
    public HomePage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "/html/body/div/main/div[1]/div[2]/div/div/div[1]/a/span[1]/span")
    public WebElement hotelsLink;

    @FindBy(xpath = "/html/body/div[2]/div/form/input[1]")
    public WebElement citySearch;

    public void load(){
        driver.get(ConfProperties.getProperty("tripadvisor"));
    }
}
