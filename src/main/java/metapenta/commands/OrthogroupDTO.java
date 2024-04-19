package metapenta.commands;

import java.util.*;

public class OrthogroupDTO {
    Map<Integer, List<String>> clusterFunctionalAnnotations = new HashMap<>();
    Map<Integer, List<String>> clusterKEGGGenesIds = new HashMap<>();
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

    public Set<String> getAllGenesEnzymes() {
        calculateAllGenesEnzymes();
        return allGenesEnzymes;
    }
    private Set<String> calculateAllGenesEnzymes(){
        for(Integer clusterID: clusterKEGGGenesIds.keySet()) {
            List<String> geneIds = clusterKEGGGenesIds.get(clusterID);
            allGenesEnzymes.addAll(geneIds);
        }

        return allGenesEnzymes;
    }

}
