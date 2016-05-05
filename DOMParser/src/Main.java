import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {

	public static void main(String[] args) throws IOException, SAXException,
			ParserConfigurationException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document document = docBuilder.parse(new File("Claim.xml"));
		int index = 0;
		int r = 0;
		String lastElement = "";
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Sheet1");
		List<Node> nodeList = traverse(document.getDocumentElement());
		for (Node node : nodeList) {
			String path = getXPath(node);
			NamedNodeMap nodeMap = node.getAttributes();
			for (int i = 0; i < nodeMap.getLength(); i++) {
				System.out.print(" : " + nodeMap.item(i));
			}
			System.out.println();
			path = path.replace(':', ',');
			boolean hasChild = checkChildNode(node);

			if (!hasChild) {
				System.out.println(path);
				
				for (;r < nodeList.size();) {
					HSSFRow row = sheet.createRow(r);
					for (int c = 0; c < 2; c++) {

						HSSFCell cell = row.createCell(c);

						if (c != 0) {
							index = path.lastIndexOf("/");
							lastElement = path.substring(index + 1,
									path.length());
							lastElement = lastElement.replace(',', ':');
							cell.setCellValue(lastElement);
							break;
						}
						if (c == 0) {
							index = path.lastIndexOf("/");
							String finalPath = path.substring(0, index);
							finalPath = finalPath.replace(',', ':');
							cell.setCellValue(finalPath);
							System.out.println("Final Path is " +finalPath);
						}
					}
					r++;
					break;
				}
			}
		}
		try {
			FileOutputStream out = new FileOutputStream(new File(
					"C:\\ondemand\\new.xls"));
			workbook.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check child node.
	 *
	 * @param node the node
	 * @return true, if successful
	 */
	private static boolean checkChildNode(Node node) {
		if (node.hasChildNodes()) {
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node currentNode = nodeList.item(i);
				if (currentNode.hasChildNodes())
					return currentNode.hasChildNodes();
			}
		}
		return false;
	}

	/* traverses tree starting with given node */
	private static List<Node> traverse(Node n) {
		return traverse(Arrays.asList(n));
	}

	/* traverses tree starting with given nodes */
	private static List<Node> traverse(List<Node> nodes) {
		List<Node> open = new LinkedList<Node>(nodes);
		List<Node> visited = new LinkedList<Node>();

		ListIterator<Node> it = open.listIterator();
		while (it.hasNext() || it.hasPrevious()) {
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

	/**
	 * Gets the children.
	 *
	 * @param n the n
	 * @return the children
	 */
	private static List<Node> getChildren(Node n) {
		List<Node> children = asList(n.getChildNodes());
		Iterator<Node> it = children.iterator();
		while (it.hasNext())
			if (it.next().getNodeType() != Node.ELEMENT_NODE)
				it.remove();
		return children;
	}

	/**
	 * As list.
	 *
	 * @param nodes the nodes
	 * @return the list
	 */
	private static List<Node> asList(NodeList nodes) {
		List<Node> list = new ArrayList<Node>(nodes.getLength());
		for (int i = 0, l = nodes.getLength(); i < l; i++)
			list.add(nodes.item(i));
		return list;
	}

	/**
	 * Gets the x path.
	 *
	 * @param node the node
	 * @return the x path
	 */
	private static String getXPath(Node node) {
		Node parent = node.getParentNode();
		if (parent == null) {
			return "/";
		}
		return getXPath(parent) + "/" + node.getNodeName();
	}
}