package metapenta.tools.io.loaders;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import metapenta.model.metabolic.network.GeneProduct;
import metapenta.model.metabolic.network.Metabolite;
import metapenta.model.metabolic.network.Reaction;
import metapenta.model.metabolic.network.ReactionComponent;
import metapenta.model.networks.MetabolicNetwork;
import metapenta.model.networks.MetabolicNetworkElements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Loads a metabolic network froman XML file
 * @author Jorge Duitama
 */
public class MetabolicNetworkXMLLoader {
	private int metaboliteNumber = 0;
	private int reactionNumber = 0;

	public MetabolicNetwork loadNetwork(String filename) throws Exception {
		InputStream is = new FileInputStream(filename);

		return loadNetwork(is);
	}

	public MetabolicNetwork loadNetwork (InputStream is) throws Exception {
		MetabolicNetwork mn = null;

		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = documentBuilder.parse(new InputSource(is));

		Element rootElement = doc.getDocumentElement();
		NodeList offspring = rootElement.getChildNodes(); 

		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);
			if (node instanceof Element){ 
				Element elem = (Element)node;
				if(XMLAttributes.ELEMENT_MODEL.equals(elem.getNodeName())) {
					mn = loadModel(elem);
				}
			}
		}
		if(mn != null) {
			return mn;
		}

		is.close();

		throw new IOException("Malformed XML file. The element "+XMLAttributes.ELEMENT_MODEL+" could not be found");
	}
	
	private MetabolicNetwork loadModel(Element modelElem) throws Exception {
		MetabolicNetwork answer = new MetabolicNetwork();
		
		Element products = getElementByID(modelElem, XMLAttributes.ELEMENT_LISTPRODUCTS);
		loadGeneProducts (products, answer);
		
		Element metabolites = getElementByID(modelElem, XMLAttributes.ELEMENT_LISTMETABOLITES);
		loadMetabolites (metabolites, answer);
		
		Element reactions = getElementByID(modelElem, XMLAttributes.ELEMENT_LISTREACTIONS);
		loadReactions (reactions, answer);
		
		return answer;
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

	private void loadGeneProducts(Element listElem, MetabolicNetwork network) throws IOException {
		NodeList offspring = listElem.getChildNodes(); 
		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);
			if (node instanceof Element){ 
				Element elem = (Element)node;
				if(XMLAttributes.ELEMENT_GENEPRODUCT.equals(elem.getNodeName())) {
					String id = elem.getAttribute(XMLAttributes.ATTRIBUTE_FBCID);
					if(id==null || id.length()==0) throw new IOException("Every gene product should have an id");
					String name = elem.getAttribute(XMLAttributes.ATTRIBUTE_FBCNAME);
					if(name==null || name.length()==0) name = id;
					String label = elem.getAttribute(XMLAttributes.ATTRIBUTE_FBCLABEL);
					
					
					GeneProduct product = new GeneProduct(id, name);
					if(label!=null) product.setLabel(label);
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
					String formula = elem.getAttribute(XMLAttributes.ATTRIBUTE_FBCFORMULA);
					Metabolite metabolite = new Metabolite(id, name, compartment, metaboliteNumber);
					metaboliteNumber ++;
					if(formula!=null) metabolite.setChemicalFormula(formula);
					network.addMetabolite(metabolite);
				}
			}
		}
		
	}

	private void loadReactions(Element listElem, MetabolicNetwork network) throws Exception {
		NodeList offspring = listElem.getChildNodes(); 
		int k=0;
		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);			
			if (node instanceof Element){				
				Element elem = (Element)node;
				if(XMLAttributes.ELEMENT_REACTION.equals(elem.getNodeName())) {
					String id = elem.getAttribute(XMLAttributes.ATTRIBUTE_ID);
					if(id==null || id.length()==0) throw new IOException("Every reaction should have an id");
					String name = elem.getAttribute(XMLAttributes.ATTRIBUTE_NAME);

					if(name==null || name.length()==0) throw new IOException("Invalid name for reaction with id "+id);
					String reversibleStr = elem.getAttribute(XMLAttributes.ATTRIBUTE_REVERSIBLE);
					List<GeneProduct> enzymes = new ArrayList<GeneProduct>();
					List<ReactionComponent> reactants = new ArrayList<ReactionComponent>();
					List<ReactionComponent> products = new ArrayList<ReactionComponent>();
					String lbCode = elem.getAttribute(XMLAttributes.ATTRIBUTE_FBC_LOWERBOUND);
					String ubCode = elem.getAttribute(XMLAttributes.ATTRIBUTE_FBC_UPPERBOUND);
					
					NodeList offspring2 = elem.getChildNodes(); 
					for(int j=0;j<offspring2.getLength();j++) {
						Node node2 = offspring2.item(j);
						if (node2 instanceof Element){ 
							Element elem2 = (Element) node2;
							
							if(XMLAttributes.ELEMENT_GENEASSOC.equals(elem2.getNodeName())) {
								enzymes = loadEnzymes(id, elem2, network);
							}
							if(XMLAttributes.ELEMENT_LISTREACTANTS.equals(elem2.getNodeName())) {
								reactants = loadReactionComponents(id, elem2, network);
							}
							if(XMLAttributes.ELEMENT_LISTMETABPRODUCTS.equals(elem2.getNodeName())) {
								products = loadReactionComponents(id, elem2, network);
							}
						}
					}
					if(reactants.isEmpty()) {
						System.err.println("No reactants found for reaction "+id);
						continue;
					}
					if(products.size()==0) {						
						//System.err.println("No products found for reaction "+id);
						continue;
					}
					
					Reaction r = new Reaction(id, name, reactants, products, reactionNumber);
					this.reactionNumber++;
					if("true".equals(reversibleStr)) r.setReversible(true);
					r.setEnzymes(enzymes);
					//TODO: Load bounds
					if(lbCode!=null && lbCode.contains("cobra_0")) r.setLowerBoundFlux(0);
					if(ubCode!=null && ubCode.contains("cobra_0")) r.setUpperBoundFlux(0);					
					network.addReaction(r);
				}
			}
		}
		
	}

	private List<GeneProduct> loadEnzymes(String reactionId, Element listElem, MetabolicNetwork network) throws Exception {
		List<GeneProduct> answer = new ArrayList<GeneProduct>();
		NodeList offspring = listElem.getChildNodes(); 
		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);
			if (node instanceof Element){ 
				Element elem = (Element)node;
				if(XMLAttributes.ELEMENT_GENEPRODUCTREF.equals(elem.getNodeName())) {
					String enzymeId = elem.getAttribute(XMLAttributes.ELEMENT_GENEPRODUCT);
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

	private List<ReactionComponent> loadReactionComponents(String reactionId, Element listElem, MetabolicNetwork network) throws Exception {
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
					component.setFormulaReactionComponent(m);
					answer.add(component);
				}
			}
		}
		return answer;
	}
}
