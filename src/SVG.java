import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SVG
{
	public static String toXML(StarUML.UMLView v)
	{
		if (v.type.equals("UMLClassView"))
		{
			
		}
		
		return null;
	}
	
	public static class Shape
	{
		public static class Circle extends DomNode
		{
			public Circle(int cx, int cy, int r, String stroke, int strokeWidth, String fill)
			{
				super("circle");
				attributes.put("cx", ""+cx);
				attributes.put("cy", ""+cy);
				attributes.put("r", ""+r);
				attributes.put("stroke", stroke);
				attributes.put("stroke-width", ""+strokeWidth);
				attributes.put("fill", fill);
			}
		}
	}
	
	public static class DomNode
	{
		public DomNode() { }
		public DomNode(String tagName)
		{
			this.tagName = tagName;
		}
		
		protected String tagName;
		protected boolean leaf;
		protected Map<String,String> attributes = new HashMap<String,String>();
		protected List<DomNode> children = new LinkedList<DomNode>();
		
		public String toXMLString()
		{
			StringBuilder asb = new StringBuilder();
			for (String key : attributes.keySet())
			{
				asb.append(String.format(" %s=\"%s\"",
					key, attributes.get(key)));
			}
			String attribs = asb.toString();
			
			String header = "<"+tagName+attribs+">";
			String footer = "</"+tagName+">";
			
			StringBuilder sb = new StringBuilder();
			for (DomNode n : children)
				sb.append(n.toXMLString());
			String body = sb.toString();
			
			return header + body + footer;
		}
	}
}
