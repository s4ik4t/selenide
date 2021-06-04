package com.codeborne.selenide.webdriver;

import com.codeborne.selenide.Browser;
import com.codeborne.selenide.SelenideConfig;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.edge.EdgeOptions;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

class EdgeDriverFactoryTest {
  private final Browser browser = new Browser("edge", false);
  private final EdgeDriverFactory factory = new EdgeDriverFactory();

  @Test
  void headless() {
    SelenideConfig config = new SelenideConfig().headless(true);

    EdgeOptions edgeOptions = factory.createCapabilities(config, browser, null, null);

    assertThat(edgeOptions.asMap().get("ms:edgeOptions")).isEqualTo(
      ImmutableMap.of(
      "args", asList("--headless", "--disable-gpu", "--proxy-bypass-list=<-loopback>", "--disable-dev-shm-usage", "--no-sandbox"),
      "extensions", Collections.emptyList()
    ));
  }

  @Test
  void non_headless() {
    SelenideConfig config = new SelenideConfig().headless(false);

    EdgeOptions edgeOptions = factory.createCapabilities(config, browser, null, null);

    assertThat(edgeOptions.asMap().get("ms:edgeOptions")).isEqualTo(
      ImmutableMap.of(
      "args", asList("--proxy-bypass-list=<-loopback>", "--disable-dev-shm-usage", "--no-sandbox"),
      "extensions", Collections.emptyList()
    ));
  }
}
