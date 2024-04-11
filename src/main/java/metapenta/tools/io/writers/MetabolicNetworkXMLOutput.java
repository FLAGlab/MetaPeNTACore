package metapenta.tools.io.writers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import metapenta.model.networks.MetabolicNetwork;
import metapenta.tools.io.loaders.XMLAttributes;
import metapenta.model.metabolic.network.ChemicalFormula;
import metapenta.model.metabolic.network.Compartment;
import metapenta.model.metabolic.network.GeneProduct;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class MetabolicNetworkXMLOutput {
	
	public void saveNetwork(MetabolicNetwork network, String filename) throws IOException {
	    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder;
	    try {
	        docBuilder = docFactory.newDocumentBuilder();
	    } catch (ParserConfigurationException e) {
	        throw new RuntimeException(e);
	    }

	    
	    Document doc = docBuilder.newDocument();
	    Element rootElement = doc.createElement("sbml");  
	    doc.appendChild(rootElement);
	    rootElement.setAttribute("xmlns", "http://www.sbml.org/sbml/level3/version1/core");
	    rootElement.setAttribute("xmlns:fbc", "http://www.sbml.org/sbml/level3/version1/fbc/version2");
	    rootElement.setAttribute("xmlns:groups","http://www.sbml.org/sbml/level3/version1/groups/version1");
	    rootElement.setAttribute("level","3");
	    rootElement.setAttribute("version","1");
	    rootElement.setAttribute("fbc:required", "false");
	    rootElement.setAttribute("groups:required","false");
	    
	    Element modelElement = doc.createElement(XMLAttributes.ELEMENT_MODEL);
	    
	    rootElement.appendChild(modelElement);

	    Element model= saveModel(network, doc, modelElement);
	    rootElement.appendChild(model);
	    try (FileOutputStream fos = new FileOutputStream(filename)) {
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

	        DOMSource source = new DOMSource(doc);
	        StreamResult result = new StreamResult(fos);

	        transformer.transform(source, result);
	    } catch (TransformerException e) {
	        throw new IOException(e);
	    }
	}
	
	private Element saveModel(MetabolicNetwork network, Document doc, Element modelElement) {
		//TODO: make dynamic
		modelElement.setAttribute("id", "MetaPentaNet");
		modelElement.setAttribute("name", "Metapenta model");
		modelElement.setAttribute("fbc:strict", "true");
		modelElement.appendChild(saveCompartments(network, doc));
	    modelElement.appendChild(saveMetabolites(network, doc));
	    modelElement.appendChild(saveParameters(network, doc));
	    modelElement.appendChild(saveReactions(network, doc));
	    modelElement.appendChild(saveGeneProducts(network, doc));

	    return modelElement;
	}
	
	private Node saveCompartments(MetabolicNetwork network, Document doc) {
		Element listCompartmentsElement = doc.createElement(XMLAttributes.ELEMENT_LISTCOMPARTMENTS);

	    for (Compartment compartment : network.getCompartmentsAsList()) {
	        Element compartmentElement = doc.createElement(XMLAttributes.ATTRIBUTE_COMPARTMENT);
	        compartmentElement.setAttribute(XMLAttributes.ATTRIBUTE_ID, compartment.getId());
	        compartmentElement.setAttribute(XMLAttributes.ATTRIBUTE_NAME, compartment.getName());
	        //TODO: revise meaning of constant
	        compartmentElement.setAttribute(XMLAttributes.ATTRIBUTE_CONSTANT, "false");
	        listCompartmentsElement.appendChild(compartmentElement);
	    }
		return listCompartmentsElement;
	}

	private Node saveParameters(MetabolicNetwork network, Document doc) {
		Element listParametersElement = doc.createElement(XMLAttributes.ELEMENT_LISTPARAMETERS);

	    for (Map.Entry<String, String> entry : network.getParameters().entrySet()) {
	        Element parameterElement = doc.createElement(XMLAttributes.ELEMENT_PARAMETER);
	        parameterElement.setAttribute(XMLAttributes.ATTRIBUTE_ID, entry.getKey());
	        parameterElement.setAttribute(XMLAttributes.ATTRIBUTE_VALUE, entry.getValue());
	        parameterElement.setAttribute(XMLAttributes.ATTRIBUTE_CONSTANT, "true");
	        listParametersElement.appendChild(parameterElement);
	    }
		return listParametersElement;
	}

	private Element saveMetabolites(MetabolicNetwork network, Document doc) {
	    Element listMetabolitesElement = doc.createElement(XMLAttributes.ELEMENT_LISTMETABOLITES);

	    for (Metabolite metabolite : network.getMetabolitesAsList()) {
	        Element metaboliteElement = doc.createElement(XMLAttributes.ELEMENT_METABOLITE);
	        metaboliteElement.setAttribute(XMLAttributes.ATTRIBUTE_ID, metabolite.getId());
	        metaboliteElement.setAttribute(XMLAttributes.ATTRIBUTE_NAME, metabolite.getName());
	        metaboliteElement.setAttribute(XMLAttributes.ATTRIBUTE_COMPARTMENT, metabolite.getCompartmentId());
	        metaboliteElement.setAttribute(XMLAttributes.ATTRIBUTE_CONSTANT, "false");
	        metaboliteElement.setAttribute(XMLAttributes.ATTRIBUTE_HASONLYSUBSTANCEUNITS, ""+metabolite.isHasOnlySubstanceUnits());
	        metaboliteElement.setAttribute(XMLAttributes.ATTRIBUTE_BOUNDARYCOND, ""+metabolite.isBoundaryCondition());
	        metaboliteElement.setAttribute(XMLAttributes.ATTRIBUTE_FBC_CHARGE, ""+metabolite.getCharge());

	        ChemicalFormula formula = metabolite.getChemicalFormula();
	        if(formula!=null) {
	        	metaboliteElement.setAttribute(XMLAttributes.ATTRIBUTE_FBC_FORMULA, formula.getChemicalFormula());
	        }
	        listMetabolitesElement.appendChild(metaboliteElement);
	    }

	    return listMetabolitesElement;
	}
	private Element saveReactions(MetabolicNetwork network, Document doc) {
	    Element listReactionsElement = doc.createElement(XMLAttributes.ELEMENT_LISTREACTIONS);

	    for (Reaction reaction : network.getReactionsAsList()) {
	        Element reactionElement = doc.createElement(XMLAttributes.ELEMENT_REACTION);
	        reactionElement.setAttribute(XMLAttributes.ATTRIBUTE_ID, reaction.getId());
	        reactionElement.setAttribute(XMLAttributes.ATTRIBUTE_NAME, reaction.getName());
	        reactionElement.setAttribute(XMLAttributes.ATTRIBUTE_REVERSIBLE, ""+reaction.isReversible());
	        reactionElement.setAttribute(XMLAttributes.ATTRIBUTE_FAST, "false");
	        reactionElement.setAttribute(XMLAttributes.ATTRIBUTE_FBC_LOWERBOUND, ""+reaction.getLowerBoundFluxParameterId());
	        reactionElement.setAttribute(XMLAttributes.ATTRIBUTE_FBC_UPPERBOUND, ""+reaction.getUpperBoundFluxParameterId());
	        
	        Element listReactanstElement = saveReactionComponents(XMLAttributes.ELEMENT_LISTREACTANTS, reaction.getReactants(), doc);
	        reactionElement.appendChild(listReactanstElement);
	        Element listproductsElement = saveReactionComponents(XMLAttributes.ELEMENT_LISTMETABPRODUCTS, reaction.getProducts(), doc);
	        reactionElement.appendChild(listproductsElement);
	        if(reaction.getEnzymes().size()>0) {
	        	Element listEnzymesElement = saveEnzymeRefs(XMLAttributes.ELEMENT_FBC_GENEASSOC, reaction.getEnzymes(), doc);
		        reactionElement.appendChild(listEnzymesElement);
	        }
	        listReactionsElement.appendChild(reactionElement);
	    }

	    return listReactionsElement;
	}
	
	private Element saveEnzymeRefs(String name, List<GeneProduct> enzymes, Document doc) {
	    Element listEnzymesElement = doc.createElement(name);
	    //TODO: Write propositional logic
	    Element orElement= null;
	    if(enzymes.size()>1) {
	    	orElement = doc.createElement("fbc:or");
	    	listEnzymesElement.appendChild(orElement);
	    }

	    for (GeneProduct enzyme : enzymes) {
	        Element enzymeRefElement = doc.createElement(XMLAttributes.ELEMENT_FBC_GENEPRODUCTREF);
	        enzymeRefElement.setAttribute(XMLAttributes.ELEMENT_FBC_GENEPRODUCT, enzyme.getId());
	        if(orElement!=null) orElement.appendChild(enzymeRefElement);
	        else listEnzymesElement.appendChild(enzymeRefElement);
	    }
	    return listEnzymesElement;
	}
	
	private Element saveReactionComponents(String name, List<ReactionComponent> components, Document doc) {
	    Element listComponentsElement = doc.createElement(name);

	    for (ReactionComponent component : components) {
	        Element componentElement = doc.createElement(XMLAttributes.ELEMENT_METABREF);
	        componentElement.setAttribute(XMLAttributes.ELEMENT_METABOLITE, component.getMetabolite().getId());
	        componentElement.setAttribute(XMLAttributes.ATTRIBUTE_STOICHIOMETRY, Double.toString(component.getStoichiometry()));
	        componentElement.setAttribute(XMLAttributes.ATTRIBUTE_CONSTANT, "true");
	        listComponentsElement.appendChild(componentElement);
	    }

	    return listComponentsElement;
	}
	
	private Node saveGeneProducts(MetabolicNetwork network, Document doc) {
		Element listGeneProductsElement = doc.createElement(XMLAttributes.ELEMENT_FBC_LISTGENEPRODUCTS);

	    for (GeneProduct product : network.getGeneProductsAsList()) {
	        Element geneProductElement = doc.createElement(XMLAttributes.ELEMENT_FBC_GENEPRODUCT);
	        geneProductElement.setAttribute(XMLAttributes.ATTRIBUTE_FBC_ID, product.getId());
	        geneProductElement.setAttribute(XMLAttributes.ATTRIBUTE_FBC_NAME, product.getName());
	        geneProductElement.setAttribute(XMLAttributes.ATTRIBUTE_FBC_LABEL, product.getLabel());
	        geneProductElement.setAttribute(XMLAttributes.ATTRIBUTE_SBOTERM, product.getSboTerm());
	        geneProductElement.setAttribute(XMLAttributes.ATTRIBUTE_METAID, product.getId());
	        
	        listGeneProductsElement.appendChild(geneProductElement);
	    }
		return listGeneProductsElement;
	}
}
