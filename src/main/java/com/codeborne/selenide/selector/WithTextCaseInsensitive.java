package com.codeborne.selenide.selector;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Quotes;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.selector.Xpath.NORMALIZE_SPACE_XPATH;

@ParametersAreNonnullByDefault
public class WithTextCaseInsensitive extends By.ByXPath {
  protected final String elementText;

  public WithTextCaseInsensitive(String elementText) {
    super(".//*/text()[contains(lower-case(" + NORMALIZE_SPACE_XPATH + "), lower-case(" + Quotes.escape(elementText) + "))]/parent::*");
    this.elementText = elementText;
  }

  @Override
  @CheckReturnValue
  @Nonnull
  public String toString() {
    return "with text: " + elementText;
  }

  @CheckReturnValue
  @Nonnull
  String getXPath() {
    return super.toString().replace("By.xpath: ", "");
  }
}
