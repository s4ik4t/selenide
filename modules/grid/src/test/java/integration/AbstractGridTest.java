package integration;

import com.codeborne.selenide.WebDriverRunner;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.grid.Main;

import static com.codeborne.selenide.Configuration.timeout;
import static com.codeborne.selenide.Selenide.closeWebDriver;
import static org.openqa.selenium.net.PortProber.findFreePort;

abstract class AbstractGridTest extends IntegrationTest {
  int hubPort;

  @BeforeEach
  final void setUpGrid() {
    if (WebDriverRunner.isChrome()) {
      WebDriverManager.chromedriver().setup();
    }
    if (WebDriverRunner.isFirefox()) {
      WebDriverManager.firefoxdriver().setup();
    }
    if (WebDriverRunner.isEdge()) {
      WebDriverManager.edgedriver().setup();
    }
    closeWebDriver();

    hubPort = findFreePort();
    Main.main(new String[]{"standalone", "--port", String.valueOf(hubPort)});

    timeout = 4000;
  }

  @AfterEach
  final void tearDownGrid() {
    closeWebDriver();
  }
}
