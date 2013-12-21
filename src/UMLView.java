
public class UMLView extends StarUML.UMLThing
{
	protected String name;
	protected String type;
	protected int width;
	protected int height;
	protected int left;
	protected int top;
	protected String lineColor;
	protected String fillColor;
	protected String fontFace;
	protected int fontStyle;
	protected UMLClass umlClass;
	
	public String getName() { return name; }
	public String getType() { return type; }
	public int getWidth() { return width; }
	public int getHeight() { return height; }
	public int getLeft() { return left; }
	public int getTop() { return top; }
	public String getLineColor() { return lineColor; }
	public String getFillColor() { return fillColor; }
	public int getFontStyle() { return fontStyle; }
	public UMLClass getUMLClass() { return umlClass; }
}
