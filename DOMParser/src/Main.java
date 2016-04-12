/*
 * Copyright (c) 2016.
 * @Author Pragyan Sen
 *
 */

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {
        System.out.println("Hello World!");

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new File("document.xml"));

        List<Node> nodeList = traverse(document.getDocumentElement());
        System.out.println("nodeList");
        for (Node node: nodeList) {
            String path = getXPath(node);
            boolean hasChild = checkChildNode(node);

            if(!hasChild){
                System.out.print(path + " : " + node.getTextContent());
                //System.out.println(path);
            } else {
                //System.out.print(path);
            }
            NamedNodeMap nodeMap = node.getAttributes();
            for(int i=0;i<nodeMap.getLength();i++){
                System.out.print(" : " + nodeMap.item(i));
            }
            System.out.println();
        }

    }

    private static boolean checkChildNode(Node node) {

        if(node.hasChildNodes()){
            NodeList nodeList = node.getChildNodes();
            for ( int i = 0 ; i<nodeList.getLength() ; i++) {
                Node currentNode = nodeList.item(i);
                if(currentNode.hasChildNodes())
                    return currentNode.hasChildNodes();
            }
        }
        return false;
    }

    /* traverses tree starting with given node */
    private static List<Node> traverse(Node n)
    {
        return traverse(Arrays.asList(n));
    }

    /* traverses tree starting with given nodes */
    private static List<Node> traverse(List<Node> nodes)
    {
        List<Node> open = new LinkedList<Node>(nodes);
        List<Node> visited = new LinkedList<Node>();

        ListIterator<Node> it = open.listIterator();
        while (it.hasNext() || it.hasPrevious())
        {
            Node unvisited;
            if (it.hasNext())
                unvisited = it.next();
            else
                unvisited = it.previous();

            it.remove();

            List<Node> children = getChildren(unvisited);
            for (Node child : children)
                it.add(child);

            visited.add(unvisited);
        }

        return visited;
    }

    private static List<Node> getChildren(Node n)
    {
        List<Node> children = asList(n.getChildNodes());
        Iterator<Node> it = children.iterator();
        while (it.hasNext())
            if (it.next().getNodeType() != Node.ELEMENT_NODE)
                it.remove();
        return children;
    }

    private static List<Node> asList(NodeList nodes)
    {
        List<Node> list = new ArrayList<Node>(nodes.getLength());
        for (int i = 0, l = nodes.getLength(); i < l; i++)
            list.add(nodes.item(i));
        return list;
    }

    private static String getXPath(Node node)
    {
        Node parent = node.getParentNode();
        if (parent == null)
        {
            return "/";
        }
        return getXPath(parent) + "/" + node.getNodeName();
    }
}