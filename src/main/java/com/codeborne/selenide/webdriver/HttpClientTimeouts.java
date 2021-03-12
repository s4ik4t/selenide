package com.codeborne.selenide.webdriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.HttpCommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.internal.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.time.Duration;

/**
 * A temporary workaround to override default timeouts of OkClient used in Selenium.
 *
 * Its default timeouts are incredibly long:
 * 1) connectTimeout = 120000 ms
 * 2) readTimeout = 10800000 ms
 *
 * If it's fixed in Selenium 4, this workaround can be removed.
 */
@ParametersAreNonnullByDefault
class HttpClientTimeouts {
  private static final Logger logger = LoggerFactory.getLogger(HttpClientTimeouts.class);

  public void setup(WebDriver webDriver, Duration readTimeout, Duration connectTimeout) {
    if (webDriver instanceof RemoteWebDriver) {
      try {
        setupTimeouts((RemoteWebDriver) webDriver, readTimeout, connectTimeout);
      }
      catch (Exception e) {
        throw new IllegalStateException("Failed to setup Selenium HttpClient timeouts", e);
      }
    }
  }

  private void setupTimeouts(RemoteWebDriver webDriver, Duration readTimeout, Duration connectTimeout) throws Exception {
    CommandExecutor executor = webDriver.getCommandExecutor();
    if (executor instanceof HttpCommandExecutor) {
      setupTimeouts((HttpCommandExecutor) executor, readTimeout, connectTimeout);
    }
  }

  private void setupTimeouts(HttpCommandExecutor executor, Duration readTimeout, Duration connectTimeout) throws Exception {
    Field clientField = HttpCommandExecutor.class.getDeclaredField("client");
    clientField.setAccessible(true);
    HttpClient client = (HttpClient) clientField.get(executor);
    if (client instanceof OkHttpClient) {
      setupTimeouts((OkHttpClient) client, readTimeout, connectTimeout);
    }
  }

  private void setupTimeouts(OkHttpClient client, Duration readTimeout, Duration connectTimeout) throws Exception {
    Field okClientField = OkHttpClient.class.getDeclaredField("client");
    okClientField.setAccessible(true);
    Object okClient = okClientField.get(client);
    if (okClient instanceof okhttp3.OkHttpClient) {
      setupTimeouts(readTimeout, connectTimeout, (okhttp3.OkHttpClient) okClient);
    }
  }

  private void setupTimeouts(Duration readTimeout, Duration connectTimeout, okhttp3.OkHttpClient okClient) throws Exception {
    int previousConnectTimeout = okClient.connectTimeoutMillis();
    int previousReadTimeout = okClient.readTimeoutMillis();
    setFieldValue(okClient, "connectTimeout", (int) connectTimeout.toMillis());
    setFieldValue(okClient, "readTimeout", (int) readTimeout.toMillis());
    logger.info("Changed connectTimeout from {} to {}", previousConnectTimeout, okClient.connectTimeoutMillis());
    logger.info("Changed readTimeout from {} to {}", previousReadTimeout, okClient.readTimeoutMillis());
  }

  private <T> void setFieldValue(T object, String fieldName, Object fieldValue) throws Exception {
    Field field = object.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(object, fieldValue);
  }
}
