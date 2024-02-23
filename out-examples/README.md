# Examples outputs

In this folder the outputs examples can be found for all features implemented. These examples were generated using
the [e_coli_core](http://bigg.ucsd.edu/models/e_coli_core) model. 
1. Balance reactions:
   - Input: 
     - Name of metabolic network file in SMBL format
     - Output file pefix
   - Output: 
     - Network with balanced reactions (if possible)
     - Unbalanced reactions report
2. Connected components: Find the connected components of metabolic network
3. Describe metabolic networks: 
    - Input:
      - Name of metabolic network file in SMBL format
      - Output files pefix
    - Output:
      - Compounds: all metabolites of the red in GAMS array format
      - Irreversible reaction: all irreversible reactions of the net
      - Lowerbounds: Lower bounds of reactions
      - Reactions: All reactions
      - Reversible: All reversible reactions
      - S_Matrix: stechometric matrix
      - Upper bound: Upper bound in the reactions
      - [comparment]-metabolites: all the metabolites of a specific compartments
4. Find all paths:
    - Input:
      - Name of metabolic network file in SMBL format
      - Metabolite origin, from this metabolite MetaPeNTA will start to check if exists paths to target metabolite
      - Metabolite target
    - Output:
        - File with all without cycles in JSON format
5. Gene product reactions
   - Input:
      - Name of metabolic network file in SMBL format
      - Gene product ID
      - Output file
   - Output:
      - File with all the reactions of the gene product in JSON format
6. Metabolic network interception
   - Input:
      - First metabolic network 
      - Second metabolic network
      - Output file
   - Output:
      - Output metabolic network in JSON format
7. Network boundary: Find the sinks and sources of the network
    - Input:
        - Network file in SMBL format
        - Output file
    - Output:
        - Sinks and sources en JSON format
8. Metabolite Reactions: Returns the reactions of a metabolite
    - Input:
        - Name of the first metabolic network file in SMBL format
        - Metabolite ID
        - Output file
    - Output:
        - All metabolite reactions in JSON format
9. Shortest path: Find the shortest path between two reactions
    - Input:
        - Name of the first metabolic network file in SMBL format
        - Metabolite ID
        - Output file
    - Output:
        - All metabolite reactions in JSON format
10. Write metabolic network in CSV: Write the network in a CSV
    - Input:
        - Name of the first metabolic network file in SMBL format
    - Output:
        - All the edges of metabolic network