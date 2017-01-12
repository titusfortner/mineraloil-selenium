package com.lithium.mineraloil.selenium.elements;

import com.lithium.mineraloil.selenium.helpers.BaseTest;
import org.junit.Test;
import org.openqa.selenium.By;

import static org.assertj.core.api.Assertions.assertThat;

public class NestedXpathTest extends BaseTest {

    @Test
    public void nestedElementCollapseXpath() {
        BaseElement levelOne = new BaseElement(By.xpath("//div[@id='level_1']"));
        BaseElement levelTwo = levelOne.createBaseElement(By.xpath("//div[@id='level_2']"));
        BaseElement levelThree = levelTwo.createBaseElement(By.xpath("//div[@id='level_3']"));
        BaseElement levelFour = levelThree.createBaseElement(By.xpath("//div[@id='level_4']"));
        assertThat(levelOne.getCollapsedXpathBy()).isNull();
        assertThat(levelOne.getParentElement()).isNull();
        assertThat(levelTwo.getCollapsedXpathBy()).isEqualTo(By.xpath("//div[@id='level_1']//div[@id='level_2']"));
        assertThat(levelTwo.getCollapsedParent()).isNull();
        assertThat(levelThree.getCollapsedXpathBy()).isEqualTo(By.xpath("//div[@id='level_1']//div[@id='level_2']//div[@id='level_3']"));
        assertThat(levelThree.getCollapsedParent()).isNull();
        assertThat(levelFour.getCollapsedXpathBy()).isEqualTo(By.xpath("//div[@id='level_1']//div[@id='level_2']//div[@id='level_3']//div[@id='level_4']"));
        assertThat(levelThree.getCollapsedParent()).isNull();

        assertThat(levelThree.getText()).contains("Level 3", "Welcome to the last level");
        assertThat(levelTwo.getText()).contains("Level 2", "Level 3", "Welcome to the last level");
        assertThat(levelOne.getText()).contains("Level 1", "Level 2", "Level 3", "Welcome to the last level");
    }

    @Test
    public void nestedElementList() {
        ElementList<BaseElement> list = new ElementList<>(By.xpath("//li[@class='nested_item']"), BaseElement.class);
        assertThat(list).hasSize(3);
        assertThat(list.get(0).getText()).isEqualTo("Item 1");
        assertThat(list.get(1).getText()).isEqualTo("Item 2");
        assertThat(list.get(2).getText()).isEqualTo("Item 3");
    }

    @Test
    public void nestedElementListWithNestedElement() {
        ElementList<BaseElement> list = new ElementList<>(By.xpath("//li[@class='nested_item']"), BaseElement.class);
        BaseElement item1 = list.get(0).createBaseElement(By.xpath("//div[@class='nested_text']"));
        BaseElement item2 = list.get(1).createBaseElement(By.xpath("//div[@class='nested_text']"));
        BaseElement item3 = list.get(2).createBaseElement(By.xpath("//div[@class='nested_text']"));
        assertThat(item1.getText()).isEqualTo("Item 1");
        assertThat(item2.getText()).isEqualTo("Item 2");
        assertThat(item3.getText()).isEqualTo("Item 3");
    }

    @Test
    public void nestedElementsWithHover() {
        BaseElement hoverElement = new BaseElement(By.xpath("//div[@id='hoverElement']"));
        BaseElement elementWithHover = new BaseElement(By.xpath("//div[@id='elementWithHover']")).withHover(hoverElement);
        BaseElement nestedElement = elementWithHover.createBaseElement(By.xpath("//div[@id='childElement"));
        assertThat(nestedElement.getCollapsedHoverElement().getBy()).isEqualTo(By.xpath("//div[@id='hoverElement']"));
        assertThat(nestedElement.getCollapsedXpathBy()).isEqualTo(By.xpath("//div[@id='elementWithHover']//div[@id='childElement"));
    }

    @Test
    public void nestElementWithMultipleHover() {
        BaseElement hoverElement = new BaseElement(By.xpath("//div[@id='hoverElement']"));
        BaseElement hoverElement2 = new BaseElement(By.xpath("//div[@id='hoverElementTwo']"));
        BaseElement elementWithHover = new BaseElement(By.xpath("//div[@id='elementWithHover']")).withHover(hoverElement);
        BaseElement elementWithHover2 = elementWithHover.createBaseElement(By.xpath("//div[@id='elementWithHoverTwo']")).withHover(hoverElement2);
        BaseElement nestedElement = elementWithHover2.createBaseElement(By.xpath("//div[@id='childElement']"));

        assertThat(elementWithHover2.getCollapsedXpathBy()).isNull();
        assertThat(nestedElement.getCollapsedHoverElement().getBy()).isEqualTo(By.xpath("//div[@id='hoverElementTwo']"));
        assertThat(nestedElement.getCollapsedXpathBy()).isEqualTo(By.xpath("//div[@id='elementWithHoverTwo']//div[@id='childElement']"));
    }

    @Test
    public void nestedElementsWithDifferentLocators() {
        BaseElement xpathElement1 = new BaseElement(By.xpath("//div[@id='test1']"));
        BaseElement xpathElement2 = xpathElement1.createBaseElement(By.xpath("//div[@id='test2']"));
        BaseElement idLocator1 = xpathElement2.createBaseElement(By.id("testId"));
        BaseElement xpathElement3 = idLocator1.createBaseElement(By.xpath("//div[@id='test3']"));
        BaseElement xpathElement4 = xpathElement3.createBaseElement(By.xpath("//div[@id='test4']"));
        assertThat(xpathElement1.getCollapsedXpathBy()).isNull();
        assertThat(xpathElement2.getCollapsedXpathBy()).isEqualTo(By.xpath("//div[@id='test1']//div[@id='test2']"));
        assertThat(idLocator1.getCollapsedXpathBy()).isNull();
        assertThat(xpathElement3.getCollapsedXpathBy()).isNull();
        assertThat(xpathElement4.getCollapsedXpathBy()).isEqualTo(By.xpath("//div[@id='test3']//div[@id='test4']"));

    }

    @Test
    public void nestedElementListWithNestedDivs() {
        BaseElement listContainer = new BaseElement(By.xpath("//div[@id='nested_list_container']"));
        BaseElement ul = listContainer.createBaseElement(By.xpath("//ul[@id='nested_list']"));
        ElementList<BaseElement> list = ul.createBaseElements(By.xpath("//li[@class='nested_item']"));
        assertThat(list).hasSize(3);
        assertThat(list.get(0).getText()).isEqualTo("Item 1");
        assertThat(list.get(1).getText()).isEqualTo("Item 2");
        assertThat(list.get(2).getText()).isEqualTo("Item 3");
    }

    @Test
    public void parentHasAutoscroll() {
        BaseElement parent = new BaseElement(By.xpath("//div[@id='parent']")).withAutoScrollIntoView();
        BaseElement child = parent.createBaseElement(By.xpath("//div[@id='child']"));
        assertThat(parent.getCollapsedXpathBy()).isNull();
        assertThat(child.getCollapsedXpathBy()).isNull();
    }

    @Test
    public void childAndParentHaveAutoScroll() {
        BaseElement parent = new BaseElement(By.xpath("//div[@id='parent']")).withAutoScrollIntoView();
        BaseElement child = parent.createBaseElement(By.xpath("//div[@id='child']")).withAutoScrollIntoView();
        assertThat(parent.getCollapsedXpathBy()).isNull();
        assertThat(child.getCollapsedXpathBy()).isNull();
    }

    @Test
    public void childHasAutoScrollandParentDoesNot() {
        BaseElement parent = new BaseElement(By.xpath("//div[@id='parent']"));
        BaseElement child = parent.createBaseElement(By.xpath("//div[@id='child']")).withAutoScrollIntoView();
        assertThat(parent.getCollapsedXpathBy()).isNull();
        assertThat(child.getCollapsedXpathBy()).isEqualTo(By.xpath("//div[@id='parent']//div[@id='child']"));
    }
}
