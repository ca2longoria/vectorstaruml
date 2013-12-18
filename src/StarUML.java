import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class StarUML
{
	public static void main(String[] args) throws XPathExpressionException
	{
		File f = new File(args[0]);
		XML xml = new StarUML.XML(f);
	}
	
	public static class XML
	{
		public XML(File file) throws XPathExpressionException
		{
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(file);
			}
			catch (ParserConfigurationException e) { e.printStackTrace(); }
			catch (SAXException e) { e.printStackTrace(); }
			catch (IOException e) { e.printStackTrace(); }
			
			iMap printem = new aMap()
			{
				public Object map(Object item, Object[] args)
				{
					Node n = (Node)item;
					String tab = Global.tabs((Integer)args[0],"  ");
					
					NamedNodeMap nmap = n.getAttributes();
					if (nmap != null)
						for (int i=0; i < nmap.getLength(); ++i)
						{
							Node a = nmap.item(i);
							System.out.println(tab+a);
						}
					
					System.out.println(tab+n);
					return item;
				}
			};
			
			xPath = XPathFactory.newInstance().newXPath();
			
			sized = xPathEval(sizedXPath);
			positioned = xPathEval(positionedXPath);
			classViews = xPathEval(classViewsXPath);
			dependencyViews = xPathEval(dependencyViewsXPath);
			deeseViews = xPathEval(deeseViewsXPath);
			
			class Lambda
			{
				public void eval(List<Node> nodes)
				{ 
					Set<String> types = new HashSet<String>(nodes.size());
					for (Node n : nodes)
					{
						String type = n.getAttributes().getNamedItem("type").getTextContent();
						String name = n.getAttributes().getNamedItem("name").getTextContent();
						System.out.print(String.format("%-40s", type));
						System.out.println(name);
						types.add(type);
					}
					System.out.println();
					System.out.println(types);
				}
			}
			Lambda lambda = new Lambda();
			
			System.out.println("\nClass Views: =============================================");
			lambda.eval(classViews);
			
			System.out.println("\nDependency Views: ========================================");
			lambda.eval(dependencyViews);
			
			System.out.println("\nDeese Views: =============================================");
			lambda.eval(deeseViews);
			
			Map<String,UMLView> guidTable = new HashMap<String,UMLView>();
			
			UMLViewFactory viewFactory = new UMLViewFactory(guidTable);
			for (Node n : deeseViews)
			{
				UMLView v = viewFactory.initNew(n);
			}
		}
		
		protected Document doc;
		protected XPath xPath;
		
		protected List<Node> sized;
		protected List<Node> positioned;
		
		protected List<Node> classViews;
		protected List<Node> dependencyViews;
		protected List<Node> deeseViews;
		
		private final static String sizedXPath = "PROJECT/BODY//OBJ/ATTR[@name=\"Height\"]/..";
		private final static String positionedXPath = "PROJECT/BODY//OBJ/ATTR[@name=\"Top\"]/..";
		private final static String classViewsXPath = "PROJECT/BODY//OBJ[@type=\"UMLClassView\"]";
		private final static String dependencyViewsXPath = "PROJECT/BODY//OBJ[@type=\"UMLDependencyView\"]";
		private final static String deeseViewsXPath = "PROJECT/BODY//OBJ[starts-with(@type,\"UML\") and contains(@type,\"View\")]";
		
 		protected void traverse(Node n, iMap map)
		{ traverse(n,map,0,0); }
		protected void traverse(Node n, iMap map, int maxDepth)
		{ traverse(n,map,maxDepth,0); }
		protected void traverse(Node n, iMap map, int maxDepth, int currentDepth)
		{
			if (maxDepth > 0 && currentDepth > maxDepth)
				return;
			
			if (map == null)
			{
				
			}
			else
			{
				int tab = 0;
				Object res = map.map(n,new Object[]{new Integer(currentDepth)});
				
				if (n.hasChildNodes())
				{
					NodeList children = n.getChildNodes();
					for (int i=0; i < children.getLength(); ++i)
					{
						traverse(children.item(i),map,maxDepth,currentDepth+1);
					}
				}
			}
		}
		
		protected List<Node> xPathEval(String pattern) throws XPathExpressionException
		{
			return xPathEval(doc,pattern);
		}
		protected List<Node> xPathEval(Node n, String pattern) throws XPathExpressionException
		{
			NodeList nl = (NodeList)xPath.compile(pattern).evaluate(n, XPathConstants.NODESET);
			ArrayList<Node> ret = new ArrayList<Node>(nl.getLength());
			for (int i=0; i < nl.getLength(); ++i)
				ret.add(nl.item(i));
			return ret;
		}
		
		protected class UMLViewFactory extends UMLView.Factory
		{
			public UMLViewFactory()
			{ this(null); }
			public UMLViewFactory(Map<String,UMLView> guidTable)
			{
				this.guidTable = guidTable;
			}
			
			// NOTE: This will help for later quick access to other objects via guid reference.
			private Map<String,UMLView> guidTable = null;
			
			public UMLView initNew(Object... params)
			{
				Node n = (Node)params[0];
				UMLView v = new UMLView();
				
				List<Node> nlist = null;
				try
				{
					nlist = xPathEval(n,"./OBJ/OBJ[@name=\"NameLabel\"]/ATTR[@name=\"Text\"]");
					v.name = (nlist.size() > 0 ? nlist.get(0).getTextContent() : null);
					
					v.type = n.getAttributes().getNamedItem("type").getTextContent();
					
					v.guid = n.getAttributes().getNamedItem("guid").getTextContent();
					
					nlist = xPathEval(n,"./ATTR[@name=\"Width\"]");
					v.width = (nlist.size() > 0 ? Integer.parseInt(nlist.get(0).getTextContent()) : -1);
					
					nlist = xPathEval(n,"./ATTR[@name=\"Height\"]");
					v.height = (nlist.size() > 0 ? Integer.parseInt(nlist.get(0).getTextContent()) : -1);
					
					nlist = xPathEval(n,"./ATTR[@name=\"Left\"]");
					v.left = (nlist.size() > 0 ? Integer.parseInt(nlist.get(0).getTextContent()) : -1);
					
					nlist = xPathEval(n,"./ATTR[@name=\"Top\"]");
					v.top = (nlist.size() > 0 ? Integer.parseInt(nlist.get(0).getTextContent()) : -1);
					
					nlist = xPathEval(n,"./ATTR[@name=\"LineColor\"]");
					v.lineColor = (nlist.size() > 0 ? nlist.get(0).getTextContent() : null);
					
					nlist = xPathEval(n,"./ATTR[@name=\"FillColor\"]");
					v.fillColor = (nlist.size() > 0 ? nlist.get(0).getTextContent() : null);
					
					nlist = xPathEval(n,"./OBJ/OBJ[@name=\"NameLabel\"]/ATTR[@name=\"FontStyle\"]");
					v.fontStyle = (nlist.size() > 0 ? Integer.parseInt(nlist.get(0).getTextContent()) : -1);
					
					System.out.println(v);
				}
				catch (XPathExpressionException e)
				{ e.printStackTrace(); return null; }
				
				if (guidTable != null)
					guidTable.put(v.guid, v);
				
				return v;
			}
		}
	}
	
	public static class UMLView
	{
		// In UML.*View
		protected String guid;
		protected String type;
		protected int width;
		protected int height;
		protected int left;
		protected int top;
		// TODO: Translation of String to Java's Color object, or at least "#fad311" style.
		protected String lineColor;
		protected String fillColor;
		
		// In LabelView
		protected int fontStyle;
		protected String name;
		
		public String getName()   { return name; }
		public String getType()   { return type; }
		public int    getWidth()  { return width; }
		public int    getHeight() { return width; }
		public int    getLeft()   { return width; }
		public int    getTop()    { return width; }
		
		public String toString()
		{
			return String.format(
					"name: %s\ntype: %s\nguid: %s\n" +
					"width: %s\nheight: %s\nleft: %s\nttop: %s\n" +
					"lineColor: %s\nfillColor: %s\n" +
					"fontStyle: %s\n",
				name, type, guid,
				width, height, left, top,
				lineColor, fillColor,
				fontStyle);
		}
		
		// NOTE: Trying out this Factory thing...
		public static abstract class Factory
		{
			public abstract UMLView initNew(Object... params);
		}
	}
	
	public static interface iMap
	{
		public Object map(Object item);
		public Object map(Object item, Object[] args);
		public List<Object> map(Iterable<Object> list);
		public List<Object> map(Iterable<Object> list, Object[] args);
	}
	static abstract class aMap implements iMap
	{
		public Object map(Object item)
		{ return map(item,null); }
		public abstract Object map(Object item, Object[] args);
		
		public List<Object> map(Iterable<Object> items)
		{ return map(items,null); }
		public List<Object> map(Iterable<Object> items, Object[] args)
		{
			List<Object> ret = new ArrayList<Object>();
			for (Object ob : items)
				ret.add(this.map(ob));
			return ret;
		}
	}
	
	public static class Global
	{
		public static String tabs(int depth)
		{
			return tabs(depth,"\t");
		}
		public static String tabs(int depth, String tab)
		{
			StringBuilder sb = new StringBuilder(depth);
			for (int i=0; i < depth; ++i)
				sb.append(tab);
			return sb.toString();
		}
	}
}
