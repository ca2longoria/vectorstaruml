package net.synapsehaven.vectorstaruml.xml;

public class UMLProperty
{
	public UMLProperty(UMLProperty p)
	{
		this(p.text, p.detail);
	}
	public UMLProperty(String text, Detail detail)
	{
		this.text = text;
		this.detail = detail;
	}
	
	public final String text;
	public final Detail detail;
	
	public static enum Detail
	{
		Public,
		Protected,
		Private,
		Package
	}
}
