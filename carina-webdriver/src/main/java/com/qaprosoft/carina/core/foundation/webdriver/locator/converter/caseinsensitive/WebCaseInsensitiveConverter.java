package com.qaprosoft.carina.core.foundation.webdriver.locator.converter.caseinsensitive;

import com.qaprosoft.carina.core.foundation.webdriver.locator.LocatorType;

class WebCaseInsensitiveConverter extends AbstractPlatformDependsConverter implements IPlatformDependsConverter {

    @Override
    public String idToXpath(String by) {
        return locatorToXpath(by, LocatorType.BY_ID,
                value -> createXpathFromAnotherTypeOfLocator(".", "*", "@id", "'", value));
    }

    @Override
    public String nameToXpath(String by) {
        return locatorToXpath(by, LocatorType.BY_NAME,
                value -> createXpathFromAnotherTypeOfLocator(".", "*", "@name", "'", value));
    }

    @Override
    public String linkTextToXpath(String by) {
        return locatorToXpath(by, LocatorType.BY_LINKTEXT,
                value -> createXpathFromAnotherTypeOfLocator(".", "a", "text()", "'", value));
    }

    @Override
    public String xpathIdCaseInsensitive(String by) {
        return caseInsensitiveXpathByAttribute(by, "@id");
    }

    @Override
    public String xpathNameCaseInsensitive(String by) {
        return caseInsensitiveXpathByAttribute(by, "@name");
    }

    @Override
    public String xpathTextCaseInsensitive(String by) {
        return caseInsensitiveXpathByAttribute(by, "text\\(\\)");
    }

    @Override
    public String xpathClassCaseInsensitive(String by) {
        return caseInsensitiveXpathByAttribute(by, "@class");
    }

}
