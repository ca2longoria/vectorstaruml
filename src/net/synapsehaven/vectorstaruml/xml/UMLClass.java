package net.synapsehaven.vectorstaruml.xml;

import java.util.ArrayList;
import java.util.List;

public class UMLClass extends UMLNode implements UMLNode.Namespaced
{
	protected String name;
	protected UMLNode namespace;
	
	protected List<UMLView> views;
	protected List<? extends UMLNode> clientDependencies;
	protected List<? extends UMLNode> supplierDependencies;
	protected List<? extends UMLNode> associations;
	
	protected List<UMLProperty> operations;
	protected List<UMLProperty> attributes;
	
	public String  getName()          { return new String(name); }
	public UMLNode getNamespace()     { return namespace; }
	public String  getNamespaceGUID() { return namespace.guid; }
	
	public List<Object>      getViews()      { return new ArrayList<Object>(views); }
	public List<UMLProperty> getOperations() { return new ArrayList<UMLProperty>(operations); }
	public List<UMLProperty> getAttributes() { return new ArrayList<UMLProperty>(attributes); }
}
