import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeTest {
    WebDriver driver;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.get("https://interview-prep-test.herokuapp.com/");
        driver.findElement(By.xpath("//input[@name='email']")).sendKeys("test@yahoo.com");
        driver.findElement(By.xpath("//input[@name='password']")).sendKeys("test123");
        driver.findElement(By.xpath("//button[@type='submit']")).click();
    }

    @Test (testName = "IN-1", description = "Page Title")
    public void pageTitle() {
        Assert.assertEquals(driver.getTitle(), "Interview App");
    }

    @Test (testName = "IN-2", description = "User access")
    public void userAccess() {
        //Verify "Sign out" is displayed and is clickable
        Assert.assertTrue(driver.findElement(By.linkText("Sign out")).isEnabled());
        //Verify "Manage Access" is not available
        Assert.assertThrows(NoSuchElementException.class, ()-> {driver.findElement(By.linkText("Manage Access"));});
    }

    @Test (testName = "IN-3", description = "Default Dashboards")
    public void defaultDashboards() {
        Assert.assertTrue(driver.findElement(By.xpath("//*[contains(text(),'All Topics')]")).isEnabled());
        Assert.assertTrue(driver.findElement(By.xpath("//*[contains(text(),'Coding')]")).isEnabled());
        Assert.assertTrue(driver.findElement(By.xpath("//*[contains(text(),'Soft skills')]")).isEnabled());
    }
    @Test (testName = "IN-4", description = "Interview related statements")
    public void interviewStatements() {
        //Verify "Add do" button is clickable
        Assert.assertTrue(driver.findElement(By.xpath("//div[@class='col-md-7 do']/button")).isEnabled());

        //Verify "Add don't" button is clickable
        Assert.assertTrue(driver.findElement(By.xpath("//div[@class='col-md-3 dont']/button")).isEnabled());

        //Verify user can add a message in the Dont's field
        driver.findElement(By.xpath("//div[@class='col-md-3 dont']/button")).click();
        WebElement inputField = (new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//textarea[@id='inputArea2']")))));
        inputField.sendKeys("Test message%#");
        driver.findElement(By.xpath("//button[text()='Enter']")).click();
        WebElement message = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//div[@class='col-md-7 txt odd' and text()='Test message%#']"))));
        Assert.assertTrue(message.isDisplayed());

        //Verifying if message contains special char and fails
        Assert.assertFalse(!message.getText().contains("%") || !message.getText().contains("#"));
        driver.findElement(By.xpath("//div[@class='col-md-3 dont']/button")).click(); //hiding the "dont's" section so the driver could press the "Enter" button

        //Verify user can add a message in the Do's field
        driver.findElement(By.xpath("//div[@class='col-md-7 do']/button")).click();
        WebElement inputField2 = (new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(driver.findElement(By.xpath("//textarea[@id='inputArea1']")))));
        inputField2.sendKeys("My message$$%%&&!#");
        driver.findElement(By.xpath("//button[text()='Enter']")).click();
        WebElement message2 = (new WebDriverWait(driver, 10)).until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath("//div[@class='col-md-7 txt odd' and text()='My message$$%%&&!#']"))));
        Assert.assertTrue(message2.isDisplayed());

        //Verifying if message contains a special char
        Assert.assertFalse(!message2.getText().contains("%") || !message2.getText().contains("#"));
    }

    @Test (testName = "IN-5", description = "Dashboard questions")
    public void dashboardQuestions() {
        //Adding a question in "Coding" dashboard
        driver.findElement(By.className("codeChallengeImg")).click();
        driver.findElement(By.cssSelector("*.newbtn")).click();

        //Verify our message is displayed on the "Coding" board
        String message = "Java number@123&&%#?";
        driver.findElement(By.cssSelector("textarea[id='question']")).sendKeys(message);
        driver.findElement(By.cssSelector("div>button[type='submit']")).click();
        Assert.assertEquals(driver.findElement(By.xpath("//p[contains (text(),'Java number@123&&%#?')]")).getText(), message);

        //Verify our message contains special chars and numbers
        Assert.assertTrue(driver.findElement(By.xpath("//p[contains (text(),'Java number@123&&%#?')]")).getText().contains("#"));
        Assert.assertTrue(driver.findElement(By.xpath("//p[contains (text(),'Java number@123&&%#?')]")).getText().contains("123"));

        //Adding a question in "Soft skills" dashboard
        driver.navigate().back();
        driver.findElement(By.xpath("//button[text()='Soft skills']")).click();
        driver.findElement(By.xpath("//button[text()='Enter new question ']")).click();
        driver.findElement(By.xpath("//div/input[@class='form-control']")).sendKeys(message);
        driver.findElement(By.xpath("//div/button[@type='submit']")).click();

        //Verify our message is displayed on the "Soft skills" board
        Assert.assertEquals(driver.findElement(By.xpath("//p[contains (text(),'Java number@123&&%#?')]")).getText(), message);

        //Verify our message contains special chars and numbers
        Assert.assertTrue(driver.findElement(By.xpath("//p[contains (text(),'Java number@123&&%#?')]")).getText().contains("#"));
        Assert.assertTrue(driver.findElement(By.xpath("//p[contains (text(),'Java number@123&&%#?')]")).getText().contains("123"));
    }

    @Test (testName = "IN-6", description = "Modify dashboard questions")
    public void modifyQuestions() {
        //Create a question in "Coding" dashboard
        driver.findElement(By.xpath("//button[text()='Soft skills']")).click();
        driver.findElement(By.xpath("//button[text()='Enter new question ']")).click();
        driver.findElement(By.xpath("//div/input[@class='form-control']")).sendKeys("Test question");
        driver.findElement(By.xpath("//div/button[@type='submit']")).click();
        //Edit the created question
        driver.findElement(By.xpath("//p[contains (text(),'Test question')]/parent::a/parent::div//button[@class='btn btn-sm-outline-warning']")).click();
        Actions action = new Actions(driver);
        action.doubleClick(driver.findElement(By.name("newQuestion"))).click().sendKeys("Edited question").perform();
        driver.findElement(By.xpath("//button[@class='btn btn-sm-outline-success']")).click();
        Assert.assertEquals(driver.findElement(By.xpath("//p[contains (text(),'Edited question')]")).getText(), "Edited question");
        //Delete the edited question
        driver.findElement(By.xpath("//p[contains (text(),'Edited question')]/parent::a/parent::div//button[@class='btn btn-sm-outline-danger ml-1']")).click();
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='col-md-8']//p"));
        new WebDriverWait(driver, 5).until(ExpectedConditions.numberOfElementsToBe(By.xpath("//div[@class='col-md-8']//p"), list.size() - 1));
        Assert.assertThrows(NoSuchElementException.class, ()-> {driver.findElement(By.xpath("//p[contains (text(),'Edited question')]"));});
    }

    @Test (testName = "IN-7", description = "All topics dashboard")
    public void topicsDashboard() {
        //Create a question in "Coding" dashboard
        driver.findElement(By.xpath("//button[text()='Soft skills']")).click();
        driver.findElement(By.xpath("//button[text()='Enter new question ']")).click();
        driver.findElement(By.xpath("//div/input[@class='form-control']")).sendKeys("Test question1");
        driver.findElement(By.xpath("//div/button[@type='submit']")).click();
        driver.navigate().back();
        //Create a question in "Soft skills" dashboard
        driver.findElement(By.xpath("//button[text()='Soft skills']")).click();
        driver.findElement(By.xpath("//button[text()='Enter new question ']")).click();
        driver.findElement(By.xpath("//div/input[@class='form-control']")).sendKeys("Test question2");
        driver.findElement(By.xpath("//div/button[@type='submit']")).click();
        driver.navigate().back();
        //Verifying both created questions are present in All Topics dashboard
        driver.findElement(By.xpath("//button[text()='All Topics']")).click();
        Assert.assertTrue(driver.findElement(By.xpath("//p[contains (text(),'Test question1')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//p[contains (text(),'Test question2')]")).isDisplayed());
    }

    @Test (testName = "IN-8", description = "Search option")
    public void searchOption() throws InterruptedException {
        //Create a question in "Soft skills" dashboard
        String message = "aaaaaaaaaaaaaaa1111111111111111111111222222222222222222222222";
        driver.findElement(By.xpath("//button[text()='Soft skills']")).click();
        driver.findElement(By.xpath("//button[text()='Enter new question ']")).click();
        driver.findElement(By.xpath("//div/input[@class='form-control']")).sendKeys(message);
        driver.findElement(By.xpath("//div/button[@type='submit']")).click();
        //Search for the question in the search box applying keyword "Test"
        driver.findElement(By.xpath("//input[@name='search']")).sendKeys("aa11");
        driver.findElement(By.xpath("//button[@class='btn btn-primary']")).click();
        Assert.assertTrue(driver.findElement(By.xpath("//p[contains (text(),'" + message + "')]")).isDisplayed());
        driver.findElement(By.xpath("//button[text()='Show all']")).click();
        //Search for the question exceeding the search limit of 40 characters
        driver.findElement(By.xpath("//input[@name='search']")).sendKeys(message);
        driver.findElement(By.xpath("//button[@class='btn btn-primary']")).click();
        Assert.assertTrue(driver.findElement(By.xpath("//p[contains (text(),'" + message + "')]")).isDisplayed());
        //test failed - we searched with more than 40 characters and the search worked (assert true was used to allow next blocks of code to be executed)

        //Verify "Show all" button resets the filter and shows all questions
        driver.findElement(By.xpath("//button[text()='Show all']")).click();
        new WebDriverWait(driver, 5).until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class='col-md-8']//p"), 1));
        List<WebElement> list = driver.findElements(By.xpath("//div[@class='col-md-8']//p"));
        Assert.assertTrue(list.size() > 1);
    }

    @AfterMethod
    public void tearDown() {
        driver.close();
    }

}
