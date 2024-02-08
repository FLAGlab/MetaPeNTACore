package metapenta.model;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import metapenta.model.metabolic.network.GeneProduct;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MetabolicNetworkXMLOutput {
	private static final String ELEMENT_NOTES = "notes";
	private static final String ELEMENT_MODEL = "model";
	private static final String ELEMENT_LISTPRODUCTS = "listOfGeneProducts";
	private static final String ELEMENT_LISTMETABOLITES = "listOfSpecies";
	private static final String ELEMENT_LISTREACTIONS = "listOfReactions";
	private static final String ELEMENT_GENEPRODUCT = "geneProduct";
	private static final String ATTRIBUTE_FBCID = "fbc\\:id";
	private static final String ATTRIBUTE_FBCNAME = "name";
	private static final String ATTRIBUTE_FBCLABEL = "label";
	private static final String ELEMENT_METABOLITE = "species";
	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_NAME = "name";
	private static final String ATTRIBUTE_COMPARTMENT = "compartment";
	private static final String ATTRIBUTE_FBCFORMULA = "chemicalFormula";
	private static final String ELEMENT_REACTION = "reaction";
	private static final String ATTRIBUTE_REVERSIBLE = "reversible";
	private static final String ELEMENT_GENEASSOC = "geneProductAssociation";
	private static final String ELEMENT_LISTREACTANTS = "listOfReactants";
	private static final String ELEMENT_LISTMETABPRODUCTS = "listOfProducts";
	private static final String ELEMENT_METABREF = "speciesReference";
	private static final String ATTRIBUTE_STOICHIOMETRY = "stoichiometry";
	private static final String ELEMENT_GENEPRODUCTREF = "geneProductRef";
	private static final String ATTRIBUTE_FBC_LOWERBOUND = "lowerFluxBound";
	private static final String ATTRIBUTE_FBC_UPPERBOUND = "upperFluxBound";
	
	public void saveNetwork(MetabolicNetwork network, String filename) throws IOException {
	    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder docBuilder;
	    try {
	        docBuilder = docFactory.newDocumentBuilder();
	    } catch (ParserConfigurationException e) {
	        throw new RuntimeException(e);
	    }

	    
	    Document doc = docBuilder.newDocument();
	    Element rootElement = doc.createElement("root");  
	    doc.appendChild(rootElement);

	    Element modelElement = doc.createElement(ELEMENT_MODEL);
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

	    modelElement.appendChild(saveMetabolites(network, doc));

	    modelElement.appendChild(saveReactions(network, doc));

	    return modelElement;
	}
	
	private Element saveMetabolites(MetabolicNetwork network, Document doc) {
	    Element listMetabolitesElement = doc.createElement(ELEMENT_LISTMETABOLITES);

	    for (Metabolite metabolite : network.getMetabolitesAsList()) {
	        Element metaboliteElement = doc.createElement(ELEMENT_METABOLITE);
	        metaboliteElement.setAttribute(ATTRIBUTE_ID, metabolite.getId());
	        metaboliteElement.setAttribute(ATTRIBUTE_NAME, metabolite.getName());
	        metaboliteElement.setAttribute(ATTRIBUTE_COMPARTMENT, metabolite.getCompartment());

	        String formula = metabolite.getChemicalFormula().getChemicalFormula();
	        metaboliteElement.setAttribute(ATTRIBUTE_FBCFORMULA, formula);

	        listMetabolitesElement.appendChild(metaboliteElement);
	    }

	    return listMetabolitesElement;
	}
	private Element saveReactions(MetabolicNetwork network, Document doc) {
	    Element listReactionsElement = doc.createElement(ELEMENT_LISTREACTIONS);

	    for (Reaction reaction : network.getReactionsAsList()) {
	        Element reactionElement = doc.createElement(ELEMENT_REACTION);
	        reactionElement.setAttribute(ATTRIBUTE_ID, reaction.getId());
	        reactionElement.setAttribute(ATTRIBUTE_NAME, reaction.getName());

	        if (reaction.isReversible()) {
	            reactionElement.setAttribute(ATTRIBUTE_REVERSIBLE, "true");
	        }
	        Element listEnzymesElement = saveEnzymes(ELEMENT_GENEASSOC, reaction.getEnzymes(), doc);
	        reactionElement.appendChild(listEnzymesElement);
	        Element listReactanstElement = saveReactionComponents(ELEMENT_LISTREACTANTS, reaction.getReactants(), doc);
	        reactionElement.appendChild(listReactanstElement);
	        Element listproductsElement = saveReactionComponents(ELEMENT_LISTMETABPRODUCTS, reaction.getProducts(), doc);
	        reactionElement.appendChild(listproductsElement);

	        listReactionsElement.appendChild(reactionElement);
	    }

	    return listReactionsElement;
	}
	
	private Element saveEnzymes(String name, List<GeneProduct> enzymes, Document doc) {
	    Element listEnzymesElement = doc.createElement(name);

	    for (GeneProduct enzyme : enzymes) {
	        Element enzymeRefElement = doc.createElement(ELEMENT_GENEPRODUCTREF);
	        enzymeRefElement.setAttribute(ELEMENT_GENEPRODUCT, enzyme.getId());

	        listEnzymesElement.appendChild(enzymeRefElement);
	    }

	    return listEnzymesElement;
	}
	
	private Element saveReactionComponents(String name, List<ReactionComponent> components, Document doc) {
	    Element listComponentsElement = doc.createElement(name);

	    for (ReactionComponent component : components) {
	        Element componentElement = doc.createElement(ELEMENT_METABREF);
	        componentElement.setAttribute(ELEMENT_METABOLITE, component.getMetabolite().getId());
	        componentElement.setAttribute(ATTRIBUTE_STOICHIOMETRY, Double.toString(component.getStoichiometry()));

	        listComponentsElement.appendChild(componentElement);
	    }

	    return listComponentsElement;
	}


	
	

}
