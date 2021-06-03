package com.codeborne.selenide.impl;

import com.codeborne.selenide.Driver;
import com.codeborne.selenide.ElementsContainer;
import com.codeborne.selenide.ex.ElementNotFound;
import com.codeborne.selenide.ex.PageObjectException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.AbstractList;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.impl.Plugins.inject;

@ParametersAreNonnullByDefault
public class ElementsContainerCollection extends AbstractList<ElementsContainer> {
  private final WebElementSelector elementSelector = inject(WebElementSelector.class);
  private final PageObjectFactory pageFactory;
  private final Driver driver;
  private final WebElementSource parent;
  private final Field field;
  private final Class<?> listType;
  private final Type[] genericTypes;
  private final By selector;

  public ElementsContainerCollection(PageObjectFactory pageFactory, Driver driver, @Nullable WebElementSource parent,
                                     Field field, Class<?> listType, Type[] genericTypes, By selector) {
    this.pageFactory = pageFactory;
    this.driver = driver;
    this.parent = parent;
    this.field = field;
    this.listType = listType;
    this.genericTypes = genericTypes;
    this.selector = selector;
  }

  @CheckReturnValue
  @Nonnull
  @Override
  public ElementsContainer get(int index) {
    WebElementSource self = new ElementFinder(driver, parent, selector, index);
    try {
      return pageFactory.initElementsContainer(driver, field, self, listType, genericTypes);
    }
    catch (ReflectiveOperationException e) {
      throw new PageObjectException("Failed to initialize field " + field, e);
    }
  }

  @CheckReturnValue
  @Override
  public int size() {
    try {
      return elementSelector.findElements(driver, parent, selector).size();
    }
    catch (NoSuchElementException e) {
      throw new ElementNotFound(driver, selector.toString(), exist, e);
    }
  }
}
