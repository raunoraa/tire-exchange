package com.example.tire_exchange.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;


@Component
public class XmlTireDataHandler implements TireDataHandler {

    private final ObjectMapper objectMapper;

    public XmlTireDataHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String parseTireData(String data) {
        // Parse XML data
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(data)));

            // Convert the DOM Document into a Map
            Map<String, Object> jsonMap = new HashMap<>();
            Element root = doc.getDocumentElement();
            NodeList tireNodes = root.getChildNodes();

            // Iterate through the child nodes of the root element
            for (int i = 0; i < tireNodes.getLength(); i++) {
                Node node = tireNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    Map<String, String> elementMap = getStringStringMap(element);
                    jsonMap.put(element.getTagName() + i, elementMap);
                }
            }

            // Convert the Map into a JSON string using ObjectMapper
            return objectMapper.writeValueAsString(jsonMap);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML", e);
        }
    }

    private static Map<String, String> getStringStringMap(Element element) {
        Map<String, String> elementMap = new HashMap<>();

        // Iterate through the child nodes of each tire element
        NodeList childNodes = element.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                elementMap.put(childElement.getTagName(), childElement.getTextContent());
            }
        }
        return elementMap;
    }
}
