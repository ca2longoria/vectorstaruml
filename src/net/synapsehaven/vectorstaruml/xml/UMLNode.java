package net.synapsehaven.vectorstaruml.xml;

public class UMLNode
{
	protected String guid;
	public String getGUID() { return guid; }
	
	public static final int InvalidValue = 0x7fffffff;
	
	// Interfaces
	public static interface Named
	{ public String getName(); }
	
	public static interface Namespaced extends Named
	{
		public UMLNode getNamespace();
		public String getNamespaceGUID();
	}
	
	
	public static abstract class Factory<T extends UMLNode>
	{
		public abstract T newUMLObject(Object... params);
	}
}
