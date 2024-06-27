package metapenta.io;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class KeggPathwayXMLProcessor {

	//Enzymes by kegg id
	private Map<String,Set<String>> reactionEnzymes;
	private Set<String> orphanReactionIds;
	
	public static void main(String[] args) throws Exception {
		KeggPathwayXMLProcessor instance = new KeggPathwayXMLProcessor();
		String filename = args[0];
		String outPrefix = args[1];
		instance.load(filename);
		instance.printFiles(outPrefix);
	}
	
	

	public Map<String, Set<String>> getReactionEnzymes() {
		return reactionEnzymes;
	}

	public Set<String> getOrphanReactionIds() {
		return orphanReactionIds;
	}



	public void load(String filename) throws IOException {
		
		Document doc = null;
		try (FileInputStream is = new FileInputStream(filename)) {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = documentBuilder.parse(new InputSource(is));
		} catch (SAXException e) {
			throw new IOException(e);
		} catch (ParserConfigurationException e) {
			throw new IOException(e);
		}
		Element rootElement = doc.getDocumentElement();
		NodeList offspring = rootElement.getChildNodes(); 
		reactionEnzymes = new TreeMap<>();
		orphanReactionIds = new TreeSet<>();
		for(int i=0;i<offspring.getLength();i++){  
			Node node = offspring.item(i);
			if (node instanceof Element){
				Element element = (Element)node;
				if("entry".equals(element.getNodeName())) {
					String nameStr = element.getAttribute("name");
					String type = element.getAttribute("type");
					String reactionStr = element.getAttribute("reaction");
					List<String> keggReactions = loadIds(reactionStr);
					if ("gene".equals(type)) {
						List<String> enzymes = loadIds(nameStr);
						for(String keggId:keggReactions) {
							Set<String> geneIds = reactionEnzymes.computeIfAbsent(keggId,v->new TreeSet<>());
							geneIds.addAll(enzymes);
						}
					} else if ("ortholog".equals(type)) {
						System.out.println("Found ortholog. Reactions: "+keggReactions);
						orphanReactionIds.addAll(keggReactions);
					}
				}
			}
		}
		Set<String> toRemove = new HashSet<>();
		for(String orphanId:orphanReactionIds) {
			if(reactionEnzymes.containsKey(orphanId)) {
				System.out.println("Orphan: "+orphanId+" found. Enzymes: "+reactionEnzymes.get(orphanId));
				toRemove.add(orphanId);
			}
		}
		orphanReactionIds.removeAll(toRemove);
	}

	private List<String> loadIds(String text) {
		String [] items = text.split(" ");
		List<String> answer = new ArrayList<>(items.length);
		for(String next:items) {
			int i = next.indexOf(':');
			if(i>=0) answer.add(next.substring(i+1));
		}
		return answer;
	}

	public void printFiles(String outPrefix) throws IOException {
		try(PrintStream out = new PrintStream(outPrefix+"_reactionEnzymes.txt")) {
			for(Map.Entry<String, Set<String>> entry:reactionEnzymes.entrySet()) {
				out.print(entry.getKey()+"\t");
				boolean c = false;
				for(String geneId:entry.getValue()) {
					if(c) out.print(",");
					else c = true;
					out.print(geneId);
				}
				out.println();
			}
		}
		try(PrintStream out = new PrintStream(outPrefix+"_orphanReactions.txt")) {
			for(String reactionId:orphanReactionIds) {
				out.println(reactionId);
			}
		}
	}

}
