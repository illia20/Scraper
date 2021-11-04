import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.ConfProperties;
import pages.HomePage;
import pages.HotelPage;
import pages.HotelsPage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Scraper {
    static List<String> cities;
    static WebDriver driver;
    static String url;
    static HomePage homePage;
    static HotelsPage hotelsPage;
    static HotelPage hotelPage;
    static WebDriverWait wait;
    static Actions action;
    static ArrayList<Hotel> h = new ArrayList<Hotel>();
    static List<String> pages;
    public static void main(String[] args) {
        setup();
        //start();
        getData();
        // exit();
    }
    static void setup(){
        System.setProperty("webdriver.chrome.driver", ConfProperties.getProperty("chromedriver"));
        cities = List.of(ConfProperties.getProperty("cities").split(","));
        pages = List.of(ConfProperties.getProperty("pages").split(","));
        url = "jdbc:sqlite:D:\\University4th\\DataMining\\hotels1.db";
        createNewDatabase();
        createTableCities();
        createTableHotels();
        driver = new ChromeDriver();
        action = new Actions(driver);

        homePage = new HomePage(driver);
        hotelsPage = new HotelsPage(driver);
        hotelPage = new HotelPage(driver);

        wait = new WebDriverWait(driver, 3);
        driver.manage().window().maximize();
    }
    static void exit(){
        driver.close();
    }
    static void getData(){
        for(int i = 0; i < cities.size(); ++i){
            var city = cities.get(i);
            insertCity(city);
            var page = pages.get(i);

            driver.get(page);
            getHotels(city);
        }
    }
    static void getHotels(String city){
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("/html/body/div[4]/div[2]/div/div[2]/div/div/div[2]/div/div[1]")));
        action.sendKeys(Keys.ESCAPE).perform();

        for (int i = 2; i < 45; ++i){
            System.out.println(i);
            try{
                Hotel hotel = new Hotel();

                WebElement title;
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[2]/div[3]/div[3]/div[4]/div[" + i + "]/div/div[1]/div[2]/div[1]/div/a")));
                    title = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[2]/div[3]/div[3]/div[4]/div[" + i + "]/div/div[1]/div[2]/div[1]/div/a"));
                    action.moveToElement(title).perform();
                }
                catch (Exception e){
                    try {
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[2]/div[3]/div[3]/div[4]/div[" + i + "]/div/div[1]/div[2]/div[1]/div/div[2]/a")));
                        title = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[2]/div[3]/div[3]/div[4]/div[" + i + "]/div/div[1]/div[2]/div[1]/div/div[2]/a"));
                        action.moveToElement(title).perform();
                    }
                    catch (Exception e1){
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[2]/div[3]/div[3]/div[4]/div/div[" + i + "]/div/div[1]/div[2]/div[1]/div/a")));
                        title = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[2]/div[3]/div[3]/div[4]/div/div[" + i + "]/div/div[1]/div[2]/div[1]/div/a"));
                        action.moveToElement(title).perform();
                    }
                }
                WebElement reviews = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[2]/div[3]/div[3]/div[4]/div["+ i +"]/div/div[1]/div[2]/div[2]/div[2]/div[1]/a[2]"));
                WebElement price0 = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[2]/div[3]/div[3]/div[4]/div["+ i +"]/div/div[1]/div[2]/div[2]/div[1]/div/div/div[1]/div/div[1]/div[1]/div"));
                WebElement price1 = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[2]/div[3]/div[3]/div[4]/div["+ i +"]/div/div[1]/div[2]/div[2]/div[1]/div/div/div[2]/div[1]/div[2]"));
                WebElement price2 = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[2]/div[3]/div[3]/div[4]/div["+ i +"]/div/div[1]/div[2]/div[2]/div[1]/div/div/div[2]/div[2]/div[2]"));
                WebElement extra = driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[1]/div/div[2]/div[3]/div[3]/div[4]/div["+ i +"]/div/div[1]/div[2]/div[2]/div[2]/div[2]/div"));

                hotel.city_num = cities.indexOf(city) + 1;
                hotel.name = title.getText();
                hotel.extra = extra.getText();
                try {
                    hotel.price0 = Integer.valueOf(price0.getText().substring(price0.getText().indexOf(' ')).replace(",", "").replace("/night", "").trim());
                    hotel.price1 = Integer.valueOf(price1.getText().substring(price1.getText().indexOf(' ')).replace(",", "").trim());
                    hotel.price2 = Integer.valueOf(price2.getText().substring(price2.getText().indexOf(' ')).replace(",", "").trim());
                    hotel.reviews = Integer.valueOf(reviews.getText().substring(0, reviews.getText().indexOf(' ')).replace(",", "").trim());
                }
                catch (Exception e){e.printStackTrace();}

                System.out.println(hotel);

                h.add(hotel);
                insertHotel(hotel);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static void createNewDatabase() {

        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTableCities() {

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS cities (\n"
                + " id integer PRIMARY KEY,\n"
                + " name text NOT NULL\n"
                + ");";

        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createTableHotels() {

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS hotels (\n"
                + " id integer PRIMARY KEY,\n"
                + " name text NOT NULL,\n price0 integer, \n price1 integer, \n price2 integer, \n rev integer, \n"
                + " extra text,\n"
                + " city_num integer, FOREIGN KEY(city_num) REFERENCES cities(id)\n"
                + ");";

        try{
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void insertCity(String city){
        String sql = "INSERT INTO cities(name) VALUES(?)";

        try{
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, city);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void insertHotel(Hotel hotel){
        String sql = "INSERT INTO hotels(name, price0, price1, price2, rev, extra, city_num) VALUES(?,?,?,?,?,?,?)";

        try{
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hotel.name);
            pstmt.setInt(2, hotel.price0);
            pstmt.setInt(3, hotel.price1);
            pstmt.setInt(4, hotel.price2);
            pstmt.setInt(5, hotel.reviews);
            pstmt.setString(6, hotel.extra);
            pstmt.setInt(7, hotel.city_num);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
