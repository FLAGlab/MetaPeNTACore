package metapenta.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import metapenta.model.Compartment;
import metapenta.model.GeneProduct;
import metapenta.model.IncorrectFormulaException;
import metapenta.model.Metabolite;
import metapenta.model.Reaction;
import metapenta.model.ReactionComponent;
import metapenta.model.ReactionGroup;
import metapenta.model.MetabolicNetwork;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class MetabolicNetworkXMLLoader {

	public MetabolicNetwork loadNetwork(String filename) throws IOException {
		System.out.println("Loading network from file: "+filename);
		try (InputStream inputStream = new FileInputStream(filename);) {
			return loadNetwork(inputStream);
		}	
	}

	public MetabolicNetwork loadNetwork (InputStream inputStream) throws IOException {
		MetabolicNetwork metabolicNetwork = null;

		Document doc;
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = documentBuilder.parse(new InputSource(inputStream));
		} catch (ParserConfigurationException | SAXException e) {
			throw new IOException("Error parsing XML file", e);
		}

		Element rootElement = doc.getDocumentElement();
		NodeList offspring = rootElement.getChildNodes(); 

		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);
			if (node instanceof Element){
				Element element = (Element)node;

				if(XMLAttributes.ELEMENT_MODEL.equals(element.getNodeName())) {
					metabolicNetwork = loadModel(element);
				}
			}
		}

		if(metabolicNetwork != null) {
			return metabolicNetwork;
		}

		throw new IOException("Malformed XML file. The element "+XMLAttributes.ELEMENT_MODEL+" could not be found");
	}
	
	private MetabolicNetwork loadModel(Element modelElement) throws IOException {
		MetabolicNetwork metabolicNetwork = new MetabolicNetwork();
		
		Element compartments = getElementByID(modelElement, XMLAttributes.ELEMENT_LISTCOMPARTMENTS);
		loadCompartments(compartments, metabolicNetwork);
		
		Element products = getElementByID(modelElement, XMLAttributes.ELEMENT_FBC_LISTGENEPRODUCTS);
		loadGeneProducts(products, metabolicNetwork);
		
		Element metabolites = getElementByID(modelElement, XMLAttributes.ELEMENT_LISTMETABOLITES);
		loadMetabolites(metabolites, metabolicNetwork);
		
		Element parameters = getElementByID(modelElement, XMLAttributes.ELEMENT_LISTPARAMETERS);
		loadParameters(parameters, metabolicNetwork);
		
		Element reactions = getElementByID(modelElement, XMLAttributes.ELEMENT_LISTREACTIONS);
		loadReactions(reactions, metabolicNetwork);
		
		Element reactionGroups = getElementByID(modelElement, XMLAttributes.ELEMENT_GROUPS_LISTGROUPS);
		if(reactionGroups!=null) loadReactionGroups(reactionGroups, metabolicNetwork);
		
		return metabolicNetwork;
	}


	private Element getElementByID(Element modelElem, String nodeName) {
		NodeList offspring = modelElem.getChildNodes(); 
		for(int i=0; i < offspring.getLength(); i++){
			Node node = offspring.item(i);
			if (node instanceof Element){ 
				Element elem = (Element) node;
				if(nodeName.equals(elem.getNodeName())) {
					return elem;
				}
			}
		}
		
		return null;
	}

	private void loadCompartments(Element listElem, MetabolicNetwork network) throws IOException {
		NodeList offspring = listElem.getChildNodes(); 
		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);
			if (node instanceof Element){ 
				Element elem = (Element)node;
				if(XMLAttributes.ATTRIBUTE_COMPARTMENT.equals(elem.getNodeName())) {
					String id = elem.getAttribute(XMLAttributes.ATTRIBUTE_ID);
					if(id==null || id.length()==0) throw new IOException("Every compartment should have an id");
					String name = elem.getAttribute(XMLAttributes.ATTRIBUTE_NAME);
					if(name==null || name.length()==0) name = id;
					Compartment compartment = new Compartment (id,name);
					network.addCompartment(compartment);
				}
			}
		}	
	}

	private void loadParameters(Element listElem, MetabolicNetwork network) throws IOException {
		NodeList offspring = listElem.getChildNodes(); 
		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);
			if (node instanceof Element){ 
				Element elem = (Element)node;
				if(XMLAttributes.ELEMENT_PARAMETER.equals(elem.getNodeName())) {
					String id = elem.getAttribute(XMLAttributes.ATTRIBUTE_ID);
					if(id==null || id.length()==0) throw new IOException("Every parameter should have an id");
					String value = elem.getAttribute(XMLAttributes.ATTRIBUTE_VALUE);
					if(value==null || value.length()==0) throw new IOException("Value should be given for parameter with id: "+id);
					network.addParameter(id, value);
				}
			}
		}
		
	}

	private void loadGeneProducts(Element listElem, MetabolicNetwork network) throws IOException {
		NodeList offspring = listElem.getChildNodes(); 
		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);
			if (node instanceof Element){ 
				Element elem = (Element)node;
				if(XMLAttributes.ELEMENT_FBC_GENEPRODUCT.equals(elem.getNodeName())) {
					String id = elem.getAttribute(XMLAttributes.ATTRIBUTE_FBC_ID);
					if(id==null || id.length()==0) throw new IOException("Every gene product should have an id");
					String name = elem.getAttribute(XMLAttributes.ATTRIBUTE_FBC_NAME);
					String label = elem.getAttribute(XMLAttributes.ATTRIBUTE_FBC_LABEL);
					String sboTerm = elem.getAttribute(XMLAttributes.ATTRIBUTE_SBOTERM);
					String metaId = elem.getAttribute(XMLAttributes.ATTRIBUTE_METAID);
					if(!id.equals(metaId)) System.err.println("Meta id: "+metaId+" different than id of gene product: "+id);
					
					if(name==null || name.length()==0) {
						if(label!=null && label.length()>0) name = label;
						else name = id;
					}
					GeneProduct product = new GeneProduct(id, name);
					if(label!=null && label.trim().length()>0) product.setLabel(label.trim());
					if(sboTerm!=null && sboTerm.trim().length()>0) product.setSboTerm(sboTerm.trim());
					network.addGeneProduct (product);
				}
			}
		}
	}

	private void loadMetabolites(Element listElem, MetabolicNetwork network) throws IOException {
		NodeList offspring = listElem.getChildNodes(); 
		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);
			if (node instanceof Element){ 
				Element elem = (Element)node;
				if(XMLAttributes.ELEMENT_METABOLITE.equals(elem.getNodeName())) {
					String id = elem.getAttribute(XMLAttributes.ATTRIBUTE_ID);
					if(id==null || id.length()==0) throw new IOException("Every metabolite should have an id");
					String name = elem.getAttribute(XMLAttributes.ATTRIBUTE_NAME);
					if(name==null || name.length()==0) throw new IOException("Invalid name for metabolite with id "+id);
					String compartment = elem.getAttribute(XMLAttributes.ATTRIBUTE_COMPARTMENT);
					if(compartment==null || compartment.length()==0) throw new IOException("Invalid compartment for metabolite with id "+id);
					String formula = elem.getAttribute(XMLAttributes.ATTRIBUTE_FBC_FORMULA);
					String metaid = elem.getAttribute(XMLAttributes.ATTRIBUTE_METAID);
					if(!metaid.equals(id)) System.err.println("WARN. Metaid: "+metaid+" different than id: "+id);
					String unitsS = elem.getAttribute(XMLAttributes.ATTRIBUTE_HASONLYSUBSTANCEUNITS);
					if(!("".equals(unitsS) || "false".equals(unitsS) || "true".equals(unitsS)) ) throw new IOException("Invalid value "+unitsS+" for attribute "+XMLAttributes.ATTRIBUTE_HASONLYSUBSTANCEUNITS+" metabolite id: "+id);
					
					String boundaryS = elem.getAttribute(XMLAttributes.ATTRIBUTE_BOUNDARYCOND);
					if(!("".equals(boundaryS) || "false".equals(boundaryS) || "true".equals(boundaryS)) ) throw new IOException("Invalid value "+boundaryS+" for attribute "+XMLAttributes.ATTRIBUTE_BOUNDARYCOND+" metabolite id: "+id);
					
					String chargeS = elem.getAttribute(XMLAttributes.ATTRIBUTE_FBC_CHARGE);
					
					
					
					Metabolite metabolite = new Metabolite(id, name);
					metabolite.setCompartmentId(compartment);
					if(unitsS!=null && "true".equals(unitsS)) metabolite.setHasOnlySubstanceUnits(true);
					if(boundaryS!=null && "true".equals(boundaryS)) metabolite.setBoundaryCondition(true);
					if(chargeS!=null && chargeS.trim().length()>0) metabolite.setCharge(Integer.parseInt(chargeS.trim()));
					if(formula!=null && formula.trim().length()>0)
						try {
							metabolite.setChemicalFormula(formula.trim());
						} catch (IncorrectFormulaException e) {
							throw new IOException(e);
						}
					List<String> links = loadAnnotations(id, elem, XMLAttributes.ELEMENT_BQBIOL_IS);
					if(links!=null && links.size()>0) metabolite.setLinks(links);
					links = loadAnnotations(id, elem, XMLAttributes.ELEMENT_BQBIOL_HAS_PROPERTY);
					String sboTerm = calculateSBOTerm(links);
					if(sboTerm!=null) metabolite.setSboTerm(sboTerm);
					//else System.out.println("No links for metabolite: "+id);
					//if("CPD__45__19812__91__c__93__".equals(id)) System.out.println("Loaded links: "+links);
					
					network.addMetabolite(metabolite);
				}
			}
		}
		
	}

	private void loadReactions(Element reactions, MetabolicNetwork network) throws IOException {
		NodeList offspring = reactions.getChildNodes();

		for(int i=0;i<offspring.getLength();i++){
			Node node = offspring.item(i);

			if (node instanceof Element){
				Element reactionElement = (Element)node;

				if(XMLAttributes.ELEMENT_REACTION.equals(reactionElement.getNodeName())) {
					String id = reactionElement.getAttribute(XMLAttributes.ATTRIBUTE_ID);
					if(id==null || id.length()==0) throw new IOException("Every reactionElement must have an id");

					String name = reactionElement.getAttribute(XMLAttributes.ATTRIBUTE_NAME);
					if(name==null || name.length()==0) name = id;

					String reversible = reactionElement.getAttribute(XMLAttributes.ATTRIBUTE_REVERSIBLE);
					String lowerBound = reactionElement.getAttribute(XMLAttributes.ATTRIBUTE_FBC_LOWERBOUND);
					String upperBound = reactionElement.getAttribute(XMLAttributes.ATTRIBUTE_FBC_UPPERBOUND);

					Reaction reaction = loadReaction(reactionElement, network, id, name);

					reaction.setReversible(reversible.equals(("true")));
					setBoundsInReaction(reaction, lowerBound, upperBound, network);

					network.addReaction(reaction);
				}
			}
		}
	}

	private Reaction loadReaction (Element reactionElement, MetabolicNetwork network, String id, String name) throws IOException {
		List<GeneProduct> enzymes = new ArrayList<GeneProduct>();
		List<ReactionComponent> reactants = new ArrayList<ReactionComponent>();
		List<ReactionComponent> products = new ArrayList<ReactionComponent>();
		List<String> linksIs = null;
		List<String> linksHasProperty = null;
		NodeList offspring = reactionElement.getChildNodes();

		for(int j=0; j<offspring.getLength(); j++) {
			Node node = offspring.item(j);
			if (node instanceof Element){
				Element element = (Element) node;

				if(XMLAttributes.ELEMENT_FBC_GENEASSOC.equals(element.getNodeName())) {
					enzymes = loadEnzymes(id, element, network);
				}
				if(XMLAttributes.ELEMENT_LISTREACTANTS.equals(element.getNodeName())) {
					reactants = loadReactionComponents(id, element, network);
				}
				if(XMLAttributes.ELEMENT_LISTMETABPRODUCTS.equals(element.getNodeName())) {
					products = loadReactionComponents(id, element, network);
				}
				if(XMLAttributes.ELEMENT_ANNOTATION.equals(element.getNodeName())) {
					linksIs = loadAnnotations(id, element, XMLAttributes.ELEMENT_BQBIOL_IS);
					linksHasProperty = loadAnnotations(id, element, XMLAttributes.ELEMENT_BQBIOL_HAS_PROPERTY);
				}
			}
		}

		if(reactants.isEmpty()) {
			System.err.println("WARN. No reactants found for reactionElement " + id);
		}
		if(products.isEmpty()) {
			System.err.println("WARN. No products found for reactionElement " + id);
		}

		Reaction reaction = new Reaction(id, name, reactants, products);
		reaction.setEnzymes(enzymes);
		if(linksIs!=null && linksIs.size()>0) reaction.setLinks(linksIs);
		String sboTerm = calculateSBOTerm(linksHasProperty);
		if(sboTerm!=null) reaction.setSboTerm(sboTerm);

		return reaction;
	}

	

	private String calculateSBOTerm(List<String> linksHasProperty) {
		if(linksHasProperty==null) return null;
		for(String link:linksHasProperty) {
			//TODO: Include controlled terms
			if(!link.startsWith("http://identifiers.org/sbo/SBO:")) continue;
			return link.substring(27);
		}
		return null;
	}

	private void setBoundsInReaction(Reaction reaction, String lowerBound, String upperBound, MetabolicNetwork network) throws IOException {
		if(lowerBound!=null && !lowerBound.trim().isEmpty()) {
			String lowerBoundValue = network.getValueParameter(lowerBound);
			if(lowerBoundValue ==null) throw new IOException("Lower bound parameter id not found for reactionElement: "+reaction.getId());
			reaction.setLowerBoundFluxParameterId(lowerBound);
			reaction.setLowerBoundFlux(Double.parseDouble(lowerBoundValue));
		}

		if(upperBound!=null && !upperBound.trim().isEmpty()) {
			String valueS = network.getValueParameter(upperBound);
			if(valueS ==null) throw new IOException("Upper bound parameter id not found for reactionElement: "+reaction.getId());
			reaction.setUpperBoundFluxParameterId(upperBound);
			reaction.setUpperBoundFlux(Double.parseDouble(valueS));
		}
	}

	private List<GeneProduct> loadEnzymes(String reactionId, Element listElem, MetabolicNetwork network) throws IOException {
		List<GeneProduct> answer = new ArrayList<GeneProduct>();
		NodeList offspring = listElem.getChildNodes(); 
		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);
			if (node instanceof Element){ 
				Element elem = (Element)node;
				if(XMLAttributes.ELEMENT_FBC_GENEPRODUCTREF.equals(elem.getNodeName())) {
					String enzymeId = elem.getAttribute(XMLAttributes.ELEMENT_FBC_GENEPRODUCT);
					if(enzymeId==null || enzymeId.isEmpty()) throw new IOException("Invalid enzyme for reaction "+reactionId);
					GeneProduct enzyme = network.getGeneProduct(enzymeId);
					answer.add(enzyme);
				} else {
					answer.addAll(loadEnzymes(reactionId, elem, network));
				}
			}
		}
		
		
		return answer;
	}

	private List<ReactionComponent> loadReactionComponents(String reactionId, Element listElem, MetabolicNetwork network) throws IOException {
		List<ReactionComponent> answer = new ArrayList<ReactionComponent>();
		NodeList offspring = listElem.getChildNodes(); 
		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);
			if (node instanceof Element){ 
				Element elem = (Element)node;
				if(XMLAttributes.ELEMENT_METABREF.equals(elem.getNodeName())) {
					String metabId = elem.getAttribute(XMLAttributes.ELEMENT_METABOLITE);
					if(metabId==null || metabId.length()==0) throw new IOException("Invalid metabolite association for reaction "+reactionId);
					Metabolite m = network.getMetabolite(metabId);
					if(m==null) throw new IOException("Metabolite "+metabId+" not found for reaction "+reactionId);
					
					String stchmStr = elem.getAttribute(XMLAttributes.ATTRIBUTE_STOICHIOMETRY);
					if(stchmStr==null || stchmStr.length()==0) throw new IOException("Absent stoichiometry for metabolite "+metabId+" in reaction "+reactionId);
					double stoichiometry;
					try {
						stoichiometry = Double.parseDouble(stchmStr);
					} catch (NumberFormatException e) {
						throw new IOException("Invalid stoichiometry "+stchmStr+" for metabolite "+metabId+" in reaction "+reactionId,e);
					}
					ReactionComponent component = new ReactionComponent(m, stoichiometry);
					answer.add(component);
				}
			}
		}
		return answer;
	}
	private List<String> loadAnnotations(String elementId, Element rootElem, String linkType) {
		List<String> answer = new ArrayList<>();
		NodeList offspring = rootElem.getChildNodes(); 
		for(int i=0;i<offspring.getLength();i++){
			Node node = offspring.item(i);
			if (node instanceof Element){
				Element element = (Element) node;
				//if("CPD__45__19812__91__c__93__".equals(elementId)) System.out.println("Parent node: "+rootElem.getNodeName()+" Next node: "+element.getNodeName());
				//Recursion to avoid more nesting or extra methods
				if(XMLAttributes.ELEMENT_ANNOTATION.equals(element.getNodeName())) {
					answer.addAll(loadAnnotations(elementId, element, linkType));
				}
				if(XMLAttributes.ELEMENT_RDF_ROOT.equals(element.getNodeName())) {
					answer.addAll(loadAnnotations(elementId, element, linkType));
				}
				else if(XMLAttributes.ELEMENT_RDF_DESCRIPTION.equals(element.getNodeName())) {
					answer.addAll(loadAnnotations(elementId, element, linkType));
				}
				else if(linkType.equals(element.getNodeName())) {
					answer.addAll(loadAnnotations(elementId, element, linkType));
				}
				else if(XMLAttributes.ELEMENT_RDF_BAG.equals(element.getNodeName())) {
					answer.addAll(loadAnnotations(elementId, element, linkType));
				}
				else if(XMLAttributes.ELEMENT_RDF_LI.equals(element.getNodeName())) {
					String link = element.getAttribute(XMLAttributes.ATTRIBUTE_RDF_RESOURCE);
					//if("CPD__45__19812__91__c__93__".equals(elementId)) System.out.println("Loaded attribute link: "+link);
					if(link!=null) answer.add(link);
				}
				
			}
		}
		return answer;
	}
	private void loadReactionGroups(Element rootElem, MetabolicNetwork metabolicNetwork) {
		//System.out.println("Loading groups");
		NodeList offspring = rootElem.getChildNodes(); 
		for(int i=0;i<offspring.getLength();i++){
			Node node = offspring.item(i);
			if (node instanceof Element){
				Element element = (Element) node;
				//System.out.println("Next element "+element.getNodeName());
				if(XMLAttributes.ELEMENT_GROUPS_GROUP.equals(element.getNodeName())) {
					String id = element.getAttribute(XMLAttributes.ATTRIBUTE_GROUPS_ID);
					String name = element.getAttribute(XMLAttributes.ATTRIBUTE_GROUPS_NAME);
					String kind = element.getAttribute(XMLAttributes.ATTRIBUTE_GROUPS_KIND);
					String sboTerm = element.getAttribute(XMLAttributes.ATTRIBUTE_SBOTERM);
					ReactionGroup group = new ReactionGroup(id);
					if(name!=null) group.setName(name);
					if(kind!=null) group.setKind(kind);
					if(sboTerm!=null) group.setSboTerm(sboTerm);
					NodeList offspring2 = element.getChildNodes();
					for(int j=0;j<offspring2.getLength();j++){
						Node node2 = offspring2.item(j);
						if (node2 instanceof Element){
							Element element2 = (Element) node2;
							//System.out.println("Next group id "+id+" element "+element2.getNodeName());
							if(XMLAttributes.ELEMENT_GROUPS_LISTMEMBERS.equals(element2.getNodeName())) {
								loadReactionGroupMembers(element2.getChildNodes(),group,metabolicNetwork);
								metabolicNetwork.addReactionGroup(group);
								//System.out.println("Loaded group "+group.getId());
							}
						}
					}
				}
			}
		}
	}

	private void loadReactionGroupMembers(NodeList memberNodes, ReactionGroup group, MetabolicNetwork metabolicNetwork) {
		for(int i=0;i<memberNodes.getLength();i++){
			Node node = memberNodes.item(i);
			if (node instanceof Element){
				Element element = (Element) node;
				if(XMLAttributes.ELEMENT_GROUPS_MEMBER.equals(element.getNodeName())) {
					String id = element.getAttribute(XMLAttributes.ATTRIBUTE_GROUPS_IDREF);
					Reaction r = metabolicNetwork.getReaction(id);
					if(r==null) {
						System.err.println("WARN. Reaction id "+id+" referred in group "+group.getId()+" not found in network");
						continue;
					}
					group.addReaction(r);
				}
			}
		}	
	}
}
