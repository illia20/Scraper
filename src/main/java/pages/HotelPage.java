package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;

public class HotelPage{
    protected WebDriver driver;
    public HotelPage(WebDriver driver){
        PageFactory.initElements(driver, this);
        this.driver = driver;
    }
}
