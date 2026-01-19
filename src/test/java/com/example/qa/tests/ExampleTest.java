package com.example.qa.tests;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

public class ExampleTest {

  @Test
  void openPlaywrightSite() {
    try (Playwright playwright = Playwright.create()) {
      Browser browser = playwright.chromium().launch();
      Page page = browser.newPage();

      page.navigate("https://playwright.dev");
      Assertions.assertTrue(page.title().contains("Playwright"));
    }
  }
}
