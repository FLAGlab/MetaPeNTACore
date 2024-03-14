package metapenta.tools.io.loaders;

public class XMLAttributes {
	
	public static final String ATTRIBUTE_ID = "id";
    public static final String ATTRIBUTE_NAME = "name";
    public static final String ATTRIBUTE_VALUE = "value";
    public static final String ATTRIBUTE_METAID = "metaid";
    public static final String ATTRIBUTE_COMPARTMENT = "compartment";
	
    public static final String ELEMENT_NOTES = "notes";
    public static final String ELEMENT_MODEL = "model";
    
    public static final String ELEMENT_LISTCOMPARTMENTS = "listOfCompartments";
    public static final String ELEMENT_LISTMETABOLITES = "listOfSpecies";
    public static final String ELEMENT_LISTPARAMETERS = "listOfParameters";
    public static final String ELEMENT_LISTREACTIONS = "listOfReactions";
    public static final String ELEMENT_LISTGENEPRODUCTS = "fbc:listOfGeneProducts";
    
    
    public static final String ELEMENT_GENEPRODUCT = "fbc:geneProduct";
    public static final String ATTRIBUTE_FBCID = "fbc:id";
    public static final String ATTRIBUTE_FBCNAME = "fbc:name";
    public static final String ATTRIBUTE_FBCLABEL = "fbc:label";
    public static final String ATTRIBUTE_SBOTERM = "sboTerm";
    
    public static final String ELEMENT_METABOLITE = "species";
    public static final String ATTRIBUTE_HASONLYSUBSTANCEUNITS = "hasOnlySubstanceUnits";
    public static final String ATTRIBUTE_BOUNDARYCOND = "boundaryCondition";
    public static final String ATTRIBUTE_FBC_CHARGE = "fbc:charge";
    public static final String ATTRIBUTE_FBCFORMULA = "fbc:chemicalFormula";
    
    public static final String ELEMENT_PARAMETER = "parameter";
    
    public static final String ELEMENT_REACTION = "reaction";
    public static final String ATTRIBUTE_REVERSIBLE = "reversible";
    public static final String ATTRIBUTE_STOICHIOMETRY = "stoichiometry";
    public static final String ATTRIBUTE_FBC_LOWERBOUND = "fbc:lowerFluxBound";
    public static final String ATTRIBUTE_FBC_UPPERBOUND = "fbc:upperFluxBound";
    public static final String ELEMENT_GENEASSOC = "fbc:geneProductAssociation";
    public static final String ELEMENT_LISTREACTANTS = "listOfReactants";
    public static final String ELEMENT_LISTMETABPRODUCTS = "listOfProducts";
    public static final String ELEMENT_METABREF = "speciesReference";
    
    public static final String ELEMENT_GENEPRODUCTREF = "fbc:geneProductRef";
    

}
