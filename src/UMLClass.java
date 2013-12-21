import java.util.ArrayList;
import java.util.List;

public class UMLClass extends StarUML.UMLThing
{
	protected String name;
	protected Object namespace;
	protected List<Object> views;
	
	//protected List<Object> clientDependencies;
	//protected List<Object> supplierDependencies;
	//protected List<Object> associations;
	
	protected List<Object> operations;
	protected List<Object> attributes;
	
	public String getName() { return new String(name); }
	public Object getNamespace() { return namespace; }
	public List<Object> getViews() { return new ArrayList<Object>(views); }
	public List<Object> getOperations() { return new ArrayList<Object>(operations); }
	public List<Object> getAttributes() { return new ArrayList<Object>(attributes); }
}
