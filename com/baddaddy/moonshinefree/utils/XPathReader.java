package com.baddaddy.moonshinefree.utils;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class XPathReader {
    private XPath xPath;
    private Document xmlDocument;

    public XPathReader(InputStream xmlStream) {
        try {
            this.xPath = XPathFactory.newInstance().newXPath();
            this.xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlStream);
            this.xmlDocument.getDocumentElement().normalize();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e2) {
            e2.printStackTrace();
        } catch (ParserConfigurationException e3) {
            e3.printStackTrace();
        }
    }

    public XPathReader(String xmlFile) {
        try {
            this.xPath = XPathFactory.newInstance().newXPath();
            this.xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
            this.xmlDocument.getDocumentElement().normalize();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e2) {
            e2.printStackTrace();
        } catch (ParserConfigurationException e3) {
            e3.printStackTrace();
        }
    }

    public Object read(String expression, QName returnType) {
        try {
            return this.xPath.compile(expression).evaluate(this.xmlDocument, returnType);
        } catch (XPathExpressionException e) {
            e.printStackTrace();
            return null;
        }
    }
}
