package metapenta.commands;

import metapenta.tools.io.utils.kegg.entitiescreator.listcreator.EntityList;

import java.util.*;

public class OrthogroupDTO {
    Map<Integer, List<String>> clusterFunctionalAnnotations = new HashMap<>();
    Map<Integer, List<String>> clusterKEGGGenesIds = new HashMap<>();

    Map<Integer, List<String>> clusterReactions = new HashMap<>();
    Set<String> allGenesEnzymes = new HashSet<>();
    public void addClusterFunctionalAnnotations(int clusterId, List functionalAnnotation) {
        clusterFunctionalAnnotations.put(clusterId, functionalAnnotation);
    }

    public void addClusterKEGGGenesIds(int clusterId, List geneIds) {
        clusterKEGGGenesIds.put(clusterId, geneIds);
    }

    public Map<Integer, List<String>> getClusterFunctionalAnnotations() {
        return clusterFunctionalAnnotations;
    }

    public Map<Integer, List<String>> getClusterKEGGGenesIds() {
        return clusterKEGGGenesIds;
    }

    public Set<String> getAllGenesIDs() {
        calculateAllGenesIDs();
        return allGenesEnzymes;
    }
    private Set<String> calculateAllGenesIDs(){
        for(Integer clusterID: clusterKEGGGenesIds.keySet()) {
            List<String> geneIds = clusterKEGGGenesIds.get(clusterID);
            allGenesEnzymes.addAll(geneIds);
        }

        return allGenesEnzymes;
    }

    public void calculateEnzymeClusters(List<EntityList> geneEnzymes) {
        for (EntityList enzymeReactions: geneEnzymes){
            Integer orthogroupID = othogroupOfGenId(enzymeReactions.ID());
            clusterReactions.put(orthogroupID, enzymeReactions.getList());
        }
    }

    private Integer othogroupOfGenId(String genID) {
        for(Integer orthogroupID: clusterKEGGGenesIds.keySet()){
            if (clusterKEGGGenesIds.get(orthogroupID).contains(genID)){
                return orthogroupID;
            }
        }

        return -1;
    }

    public Map<Integer, List<String>> clusteReactions() {
        return clusterReactions;
    }

    public List<String> reactions(Integer clusterID){
        return clusterReactions.get(clusterID);
    }
}
