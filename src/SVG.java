import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SVG
{
	public static class RawText extends DomNode
	{
		public RawText(String text)
		{ this.text = text; }
		
		final private String text;
		
		public String toXMLString()
		{ return new String(text); }
	}
	
	public static class Text extends DomNode
	{
		public Text(String text, int x, int y, String fontFamily, int fontSize, String fontWeight, String textAnchor)
		{
			super("text");
			attributes.put("x", ""+x);
			attributes.put("y", ""+y);
			attributes.put("font-family", fontFamily);
			attributes.put("font-size", ""+fontSize);
			attributes.put("font-weight", ""+fontWeight);
			attributes.put("text-anchor", textAnchor);
			
			this.addChild(new RawText(text));
		}
	}
	
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
	
	public static class Rect extends DomNode
	{
		public Rect(int x, int y, int width, int height, String stroke, int strokeWidth, String fill)
		{
			super("rect");
			attributes.put("x", ""+x);
			attributes.put("y", ""+y);
			attributes.put("width", ""+width);
			attributes.put("height", ""+height);
			attributes.put("stroke", stroke);
			attributes.put("stroke-width", ""+strokeWidth);
			attributes.put("fill", ""+fill);
			
			this.leaf = true;
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
