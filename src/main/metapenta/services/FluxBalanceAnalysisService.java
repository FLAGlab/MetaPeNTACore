package metapenta.services;


import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.optim.*;
import org.apache.commons.math3.optim.linear.*;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;

import metapenta.model.MetabolicNetwork;
import metapenta.model.Metabolite;
import metapenta.model.Reaction;
import metapenta.model.ReactionComponent;
import metapenta.petrinet.Edge;
import metapenta.petrinet.PetriNetElements;
import metapenta.petrinet.Place;
import metapenta.petrinet.Transition;

import java.util.*;

public class FluxBalanceAnalysisService {
    private RealMatrix stequiometryMatrix;
    private Map<String, Integer> rowsMetabolites = new HashMap<>();
    private Map<String, Integer> columnReactions = new HashMap<>();
    private ArrayList<double[]> reactionsBounds = new ArrayList();
    private PetriNetElements petriNet;

    private int growthReactionIndex;
    private static final double LOWER_LIMIT_NO_REVERSIBLE_RXN = 0;
    private static final double UPPER_LIMIT_RXN = 1000;
    private static final double LOWER_LIMIT_REVERSIBLE_RXN = -1000;

    FluxBalanceAnalysisService(PetriNetElements petriNet, String growthReactionID) {
        int actualColum = 0;
        Set<String> reactionsKeySet = petriNet.getTransitions().keySet();
        for (String reaction : reactionsKeySet) {
            Reaction r = (Reaction) petriNet.getTransition(reaction).getObject();
            columnReactions.put(r.getId(), actualColum);

            if (r.getId().equals(growthReactionID)) {
                growthReactionIndex = actualColum;
            }

            actualColum++;
        }

        int actualRow = 0;
        Set<String> metabolitesKeySet = petriNet.getPlaces().keySet();
        for (String metabolite : metabolitesKeySet) {
            rowsMetabolites.put(petriNet.getPlace(metabolite).getID(), actualRow);
            actualRow++;
        }

        this.petriNet = petriNet;
        loadStequiometryMatrix();
    }

    private void loadStequiometryMatrix() {
        double[][] data = new double[petriNet.getPlaces().size()][petriNet.getTransitions().size()];

        Set<String> reactionsKeys = columnReactions.keySet();

        for (String reaction : reactionsKeys) {
            Integer columnReaction = columnReactions.get(reaction);
            Reaction r = (Reaction) petriNet.getTransition(reaction).getObject();

            double[] bounds = getReactionsBounds(r);
            reactionsBounds.add(bounds);

            List<ReactionComponent> reactants = r.getReactants();

            for (ReactionComponent reactant : reactants) {
                String metaboliteId = reactant.getMetabolite().getId();
                Integer rowMetabolite = rowsMetabolites.get(metaboliteId);
                data[rowMetabolite][columnReaction] = -reactant.getStoichiometry();
            }

            List<ReactionComponent> products = r.getProducts();

            for (ReactionComponent product : products) {
                String metaboliteId = product.getMetaboliteId();
                int rowMetabolite = rowsMetabolites.get(metaboliteId);
                data[rowMetabolite][columnReaction] = product.getStoichiometry();
            }
        }

        stequiometryMatrix = MatrixUtils.createRealMatrix(data);
    }

    private LinearObjectiveFunction getObjectiveFunction() {
        double[] reactionVector = new double[columnReactions.size()];
        reactionVector[growthReactionIndex] = 1;

        return new LinearObjectiveFunction(reactionVector, 0);
    }

    private LinearConstraintSet getConstraints() {
        Collection constraints = new ArrayList();
        for (int i = 0; i < rowsMetabolites.size(); i++) {
            double[] dmdt = stequiometryMatrix.getRow(i);

            LinearConstraint steadyStateConstraint = new LinearConstraint(dmdt, Relationship.EQ, 0);
            Collection boundsConstraints = getLinearConstraintForBounds(reactionsBounds.get(i), i);

            constraints.add(steadyStateConstraint);
            constraints.addAll(boundsConstraints);

        }

        return new LinearConstraintSet(constraints);
    }

    private Collection getLinearConstraintForBounds(double[] bounds, int reactionNumber){
        double[] reactionVector = new double[columnReactions.size()];
        reactionVector[reactionNumber] = 1;

        LinearConstraint lowerBoundConstraint = new LinearConstraint(reactionVector, Relationship.GEQ, bounds[0]);
        LinearConstraint upperBoundConstraint = new LinearConstraint(reactionVector, Relationship.LEQ, bounds[1]);

        Collection boundsConstraints = new ArrayList();
        boundsConstraints.add(lowerBoundConstraint);
        boundsConstraints.add(upperBoundConstraint);


        // Rea x Metabolito , [flujos], optimizar flujo [i - Indice donde esta la funcion de crecimiento] y las condiciones S*v = 0 <--- SS

        return boundsConstraints;
    }

    private double[] getReactionsBounds(Reaction r) {
        if (r.isReversible()) {
            return new double[]{LOWER_LIMIT_REVERSIBLE_RXN, UPPER_LIMIT_RXN};
        }

        return new double[]{LOWER_LIMIT_NO_REVERSIBLE_RXN, UPPER_LIMIT_RXN};
    }

    public void Optimize() {

        LinearObjectiveFunction lof = getObjectiveFunction();
        LinearConstraintSet linearConstraints = getConstraints();
        MultivariateOptimizer optimizer = new SimplexSolver();

        PointValuePair solution = optimizer.optimize(
                new MaxIter(2000),
                lof,
                linearConstraints,
                GoalType.MAXIMIZE);


        double[] optimalB = solution.getPoint();
        System.out.println(Arrays.toString(optimalB));
    }
}