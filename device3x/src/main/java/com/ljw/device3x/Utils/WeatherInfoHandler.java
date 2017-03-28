package com.ljw.device3x.Utils;

/**
 * Created by Administrator on 2016/6/2 0002.
 */

import org.xml.sax.Attributes;


import java.util.HashMap;
import java.util.Stack;
import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


//实现类
public class WeatherInfoHandler extends BaseHandler {

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // TODO Auto-generated method stub
        String chars = new String(ch, start, length).trim();
        if (chars != null) {
            String tagName = (String) tagStack.peek();//查看栈顶对象而不移除它
//			System.out.println("chars="+chars);
            if("string".equals(tagName)) {
                weatherInfo.add(chars);
            }
        }
    }

    @Override
    public void endDocument() throws SAXException {
        // TODO Auto-generated method stub
//		Consts.hash.put("students", students.lastElement());//一个对象
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // TODO Auto-generated method stub
        tagStack.pop();//移除栈顶对象并作为此函数的值返回该对象
    }

    @Override
    public boolean parse(String xmlString) {
        // TODO Auto-generated method stub
        try {
            super.parserXml(this, xmlString);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void startDocument() throws SAXException {
        // TODO Auto-generated method stub

    }

    private Stack<String> tagStack = new Stack<String>();

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub
//		System.out.println("qName="+qName);
        if (qName.equals("student")) {

        }
        tagStack.push(qName);
    }

}

