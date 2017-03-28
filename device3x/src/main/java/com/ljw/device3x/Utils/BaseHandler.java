package com.ljw.device3x.Utils;

import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Administrator on 2016/6/2 0002.
 */

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public abstract class BaseHandler extends DefaultHandler {

    //缓存！公共数据的存放类，所有数据都从这里取，一般将它写在Const类中，这里我就不建Const.java了
    public static List<String> weatherInfo = new ArrayList<String>();//放在Const.java中！

    public abstract boolean parse(String xmlString);

    public static void parserXml(BaseHandler baseHandler, String xmlString)
            throws Exception {
        if (xmlString == null || xmlString.length() == 0)
            return;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        xmlReader.setContentHandler(baseHandler);

        //解析文件
//		xmlReader.parse(new InputSource(new URL("main.xml").openStream()));//文件
        // 创建一个xml字符串
        StringReader read = new StringReader(xmlString);
        // 创建新的输入源SAX 解析器将使用 InputSource对象来确定如何读取 XML 输入
        InputSource source = new InputSource(read);
        xmlReader.parse(source);
        read.close();
    }

    public abstract void characters(char[] ch, int start, int length)
            throws SAXException;

    public abstract void endDocument() throws SAXException;

    public abstract void endElement(String uri, String localName, String qName)
            throws SAXException;

    public abstract void startDocument() throws SAXException;

    public abstract void startElement(String uri, String localName, String qName,Attributes attributes) throws SAXException;

}

