package com.baddaddy.moonshinefree.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class XPathEvaluator {
    public void evaluateDocument(File xmlDocument) {
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();
            System.out.println("Title: " + xPath.compile("/catalog/journal/article[@date='January-2004']/title").evaluate(new InputSource(new FileInputStream(xmlDocument))));
            InputSource inputSource = new InputSource(new FileInputStream(xmlDocument));
            System.out.println("Publisher:" + xPath.evaluate("/catalog/journal/@publisher", inputSource));
            NodeList nodeList = (NodeList) xPath.evaluate("/catalog/journal/article", inputSource, XPathConstants.NODESET);
        } catch (IOException e) {
        } catch (XPathExpressionException e2) {
        }
    }
}
