package com.tms.csp.util;

import com.google.common.collect.ImmutableList;
import com.tms.csp.ast.MetaVar;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.List;

public class MetaVarParserDom4j {

    public static MetaVar parseVarMetaDataFromXmlDom4j(String varInfoXmlText) {
        Document document = null;
        try {
            document = DocumentHelper.parseText(varInfoXmlText);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        Element rootElement = document.getRootElement();
        return parseVarMetaDataFromXmlDom4j(rootElement);
    }

    public static MetaVar parseVarMetaDataFromXmlDom4j(Element varInfo) {
        String varCode = varInfo.attributeValue("code");
        String sRadio = varInfo.attributeValue("radio");
        boolean radio = !(sRadio == null || sRadio.trim().equals("") || sRadio.equalsIgnoreCase("false"));
        List<Element> xmlChildNodes = varInfo.elements();
        List<MetaVar> childNodes = convertXmlChildNodes(xmlChildNodes);
        return new MetaVar(varCode, childNodes, radio);
    }

    private static List<MetaVar> convertXmlChildNodes(List<Element> xmlChildNodes) {
        ImmutableList.Builder<MetaVar> b = ImmutableList.builder();
        for (Element xmlChildNode : xmlChildNodes) {
            MetaVar metaVar = parseVarMetaDataFromXmlDom4j(xmlChildNode);
            b.add(metaVar);
        }
        return b.build();
    }

}
