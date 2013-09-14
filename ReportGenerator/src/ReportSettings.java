import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


class ReportSettings {
	private int pageWidth;
	private int pageHeight;
	private Column[] columns;
	static class Column{
		private final String title;
		private final int width;
		public Column(String title, int width){
			this.title = title;
			this.width = width;			
		}
		public String getTitle(){
			return title;
		}
		public int getWidth(){
			return width;
		}
	}

	public ReportSettings(String xmlfile){
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		Document doc;
		try {
		    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		    doc = dBuilder.parse(new File(xmlfile));
		}catch(Exception e){
		    System.out.println(e.getMessage());
		    System.exit(1);
		    return;
		}
		NodeList nList = doc.getElementsByTagName("page");
		Node ObjectItem = nList.item(0);
		Element element = (Element)ObjectItem;
		pageWidth = Integer.parseInt(element.getElementsByTagName("width").item(0).getTextContent());
		pageHeight = Integer.parseInt(element.getElementsByTagName("height").item(0).getTextContent());
		//test 
		columns = new Column[3];
		nList = doc.getElementsByTagName("columns");
		ObjectItem = nList.item(0);
		element = (Element)ObjectItem;
		for(int i=0;i<3;i++){
			String title;
			String width;
			Element colElem = (Element)element.getElementsByTagName("column").item(i);
			title = colElem.getElementsByTagName("title").item(0).getTextContent();
			width = colElem.getElementsByTagName("width").item(0).getTextContent();
			columns[i] = new Column(title, Integer.parseInt(width));
		}	
	}
	public int getPageWidth(){
		return pageWidth;
	}
	public int getPageHeight(){
		return pageHeight;
	}
	public Column [] getColumns(){
		return columns;
	}
}
