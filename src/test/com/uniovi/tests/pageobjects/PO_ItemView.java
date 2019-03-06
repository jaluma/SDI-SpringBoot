package com.uniovi.tests.pageobjects;

import com.uniovi.tests.util.SeleniumUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class PO_ItemView extends PO_NavView {

	static public void addFillForm(WebDriver driver, String titlep, String descriptionp, String pricep, boolean highlighterp) {
		WebElement title = driver.findElement(By.name("title"));
		title.click();
		title.clear();
		title.sendKeys(titlep);
		WebElement description = driver.findElement(By.name("description"));
		description.click();
		description.clear();
		description.sendKeys(descriptionp);
		WebElement dateFormat = driver.findElement(By.name("dateFormat"));
		WebElement price = driver.findElement(By.name("price"));
		price.click();
		price.clear();
		price.sendKeys(pricep);
		if(!highlighterp) {
			WebElement highlighter = driver.findElement(By.id("highlighter"));
			highlighter.click();
		}

		By boton = By.id("addButton");
		driver.findElement(boton).click();
	}

	static public int checkNumberList(WebDriver driver) {
		return SeleniumUtils.EsperaCargaPagina(driver, "class", "tr-static-height", getTimeout()).size();
	}

	static public void remove(WebDriver driver, int index) {
		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "id", "delete", getTimeout());
		elementos.get(index).click();
	}
}
