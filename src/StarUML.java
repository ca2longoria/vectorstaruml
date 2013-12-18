import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
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
			
			xPath = XPathFactory.newInstance().newXPath();
			
			sized = xPathEval(sizedXPath);
			positioned = xPathEval(positionedXPath);
			classViews = xPathEval(classViewsXPath);
			dependencyViews = xPathEval(dependencyViewsXPath);
			deeseViews = xPathEval(deeseViewsXPath);
			
			// UMLView Factory-based construction
			Map<String,UMLView> guidTable = new HashMap<String,UMLView>();
			Map<String,List<UMLView>> typeTable = new HashMap<String,List<UMLView>>();
			
			UMLView.Factory vf = new XML.UMLViewFactory(guidTable,typeTable);
			
			List<UMLView> deeseViewsReally = new ArrayList<UMLView>();
			for (Node n : deeseViews)
				deeseViewsReally.add(vf.newUMLView(n));
			
			/*
			// NOTE: "final" classes, huh...  Let's try that.
			final class typeFilter extends aFilter<UMLView>
			{
				public typeFilter(Object... params) { super(params); }
				public boolean check(UMLView v)
				{ return v.type.equals(params[0]); }
			}
			//*/
			
			for (String type : typeTable.keySet())
			{
				System.out.println(Global.leftPad(type+": ",60,"="));
				for (UMLView v : typeTable.get(type))
					System.out.println(v);
			}
		}
		
		protected Document doc;
		protected XPath xPath;
		
		protected List<Node> sized;
		protected List<Node> positioned;
		
		protected List<Node> classViews;
		protected List<Node> dependencyViews;
		// TODO: A meaningful name to replace "deeseViews"
		protected List<Node> deeseViews;
		
		private final static String sizedXPath = "PROJECT/BODY//OBJ/ATTR[@name=\"Height\"]/..";
		private final static String positionedXPath = "PROJECT/BODY//OBJ/ATTR[@name=\"Top\"]/..";
		private final static String classViewsXPath = "PROJECT/BODY//OBJ[@type=\"UMLClassView\"]";
		private final static String dependencyViewsXPath = "PROJECT/BODY//OBJ[@type=\"UMLDependencyView\"]";
		private final static String deeseViewsXPath = "PROJECT/BODY//OBJ[starts-with(@type,\"UML\") and contains(@type,\"View\")]";
		
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
			{ this(null,null); }
			public UMLViewFactory(Map<String,UMLView> guidTable, Map<String,List<UMLView>> typeTable)
			{
				this.guidTable = guidTable;
				this.typeTable = typeTable;
			}
			
			// NOTE: This will help for later quick access to other objects via guid reference.
			private Map<String,UMLView> guidTable = null;
			private Map<String,List<UMLView>> typeTable = null;
			
			@Override
			public UMLView newUMLView(Object... params)
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
					v.width = (nlist.size() > 0 ? Integer.parseInt(nlist.get(0).getTextContent()) : UMLView.InvalidPositionValue);
					
					nlist = xPathEval(n,"./ATTR[@name=\"Height\"]");
					v.height = (nlist.size() > 0 ? Integer.parseInt(nlist.get(0).getTextContent()) : UMLView.InvalidPositionValue);
					
					nlist = xPathEval(n,"./ATTR[@name=\"Left\"]");
					v.left = (nlist.size() > 0 ? Integer.parseInt(nlist.get(0).getTextContent()) : UMLView.InvalidPositionValue);
					
					nlist = xPathEval(n,"./ATTR[@name=\"Top\"]");
					v.top = (nlist.size() > 0 ? Integer.parseInt(nlist.get(0).getTextContent()) : UMLView.InvalidPositionValue);
					
					nlist = xPathEval(n,"./ATTR[@name=\"LineColor\"]");
					v.lineColor = (nlist.size() > 0 ? nlist.get(0).getTextContent() : null);
					
					nlist = xPathEval(n,"./ATTR[@name=\"FillColor\"]");
					v.fillColor = (nlist.size() > 0 ? nlist.get(0).getTextContent() : null);
					
					nlist = xPathEval(n,"./OBJ/OBJ[@name=\"NameLabel\"]/ATTR[@name=\"FontStyle\"]");
					v.fontStyle = (nlist.size() > 0 ? Integer.parseInt(nlist.get(0).getTextContent()) : UMLView.InvalidPositionValue);
					
					nlist = xPathEval(n,"./ATTR[@name=\"Points\"]");
					if (nlist.size() > 0)
					{
						v.points = new LinkedList<Point>();
						for (String pair : nlist.get(0).getTextContent().split(";"))
							v.points.add(new Point(
								Integer.parseInt(pair.substring(0,pair.indexOf(','))),
								Integer.parseInt(pair.substring(pair.indexOf(',')+1,pair.length()))));
					}
					else
						v.points = null;
				}
				catch (XPathExpressionException e)
				{ e.printStackTrace(); return null; }
				
				// Add UMLView to tables if provided to the Factory.
				if (guidTable != null)
					guidTable.put(v.guid, v);
				if (typeTable != null)
				{
					if (!typeTable.containsKey(v.type))
						typeTable.put(v.type, new LinkedList<UMLView>());
					typeTable.get(v.type).add(v);
				}
				
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
		
		// In line-based fellows
		protected List<Point> points;
		
		public String getName()   { return name; }
		public String getType()   { return type; }
		public int    getWidth()  { return width; }
		public int    getHeight() { return width; }
		public int    getLeft()   { return width; }
		public int    getTop()    { return width; }
		
		public boolean hasDimensions()
		{ return this.width >= 0 && this.height >= 0; }
		public boolean hasPosition()
		{
			return
				this.left != UMLView.InvalidPositionValue &&
				this.top != UMLView.InvalidPositionValue; 
		}
		public boolean hasColor()
		{ return this.lineColor != null && this.fillColor != null; }
		
		public String toString()
		{
			return String.format(
					"name: %s\ntype: %s\nguid: %s\n" +
					"width: %s\nheight: %s\nleft: %s\nttop: %s\n" +
					"lineColor: %s\nfillColor: %s\n" +
					"fontStyle: %s\npoints: %s\n",
				name, type, guid,
				width, height, left, top,
				lineColor, fillColor,
				fontStyle, points);
		}
		
		public final static int InvalidPositionValue = 0x80000000;
		
		// NOTE: Trying out this Factory thing...
		public static abstract class Factory
		{
			public abstract UMLView newUMLView(Object... params);
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
	
	public static interface iFilter <T>
	{
		public boolean check(T item);
		public List<T> filter(List<T> list);
	}
	public static abstract class aFilter <T> implements iFilter<T>
	{
		public aFilter(Object... params) { this.params = params; }
		
		protected Object[] params;
		
		public abstract boolean check(T item);
		public List<T> filter(List<T> list)
		{
			List<T> ret = new ArrayList<T>();
			for (T a : list)
				if (check(a)) ret.add(a);
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
		public static String leftPad(String s, int width, String pad)
		{
			if (s.length() < width)
			{
				StringBuilder sb = new StringBuilder();
				sb.append(s);
				for (int i=s.length(); i < width; i+=pad.length())
					sb.append(pad);
				return sb.toString().substring(0,width);
			}
			return s.substring(0,width);
		}
	}
}
