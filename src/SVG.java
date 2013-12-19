import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SVG
{
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
				
				this.leaf = true;
			}
		}
	}
	
	public static final class BaseNode extends DomNode
	{
		public BaseNode(int width, int height)
		{
			super("svg");
			attributes.put("width", ""+width);
			attributes.put("height", ""+height);
			attributes.put("xmlns","http://www.w3.org/2000/svg"); 
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
		
		public DomNode addChild(DomNode n)
		{
			if (children.contains(n))
				children.remove(n);
			children.add(n);
			return n;
		}
		
		public String toXMLString()
		{
			StringBuilder asb = new StringBuilder();
			for (String key : attributes.keySet())
			{
				asb.append(String.format(" %s=\"%s\"",
					key, attributes.get(key)));
			}
			String attribs = asb.toString();
			
			String header = "<"+tagName+attribs+(leaf ? "/" : "")+">";
			
			// Leaf opts out, here.
			if (leaf)
				return header;
			
			String footer = "</"+tagName+">";
			
			StringBuilder sb = new StringBuilder();
			for (DomNode n : children)
				sb.append(n.toXMLString());
			String body = sb.toString();
			
			return header + body + footer;
		}
	}
}
