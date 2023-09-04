package ooga.controller.parsers.math;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import ooga.controller.parsers.ConfigJSONException;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;
import ooga.model.gamestate.statehandler.DrainPopulation;
import ooga.model.gamestate.statehandler.TargetPopulation;
import ooga.model.gamestate.statehandler.factors.FactorCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A class that parses the growth json config file and creates a list of TargetPopulations to be
 * used by the GrowthCalculator and SubPopulationData for use in other set up operations.
 */
public class TargetPopulationsJSON extends DataFileParser {

  private static final Logger LOG = LogManager.getLogger(TargetPopulationsJSON.class);
  private static final String DEFAULT_NORMAL_OPERATION = "NormalOperation";
  private static final String DEFAULT_DEFAULT_POPULATION_NAME = "Default";
  private List<TargetPopulation> targetPopulations;
  private String defaultOperation;
  private List<String> acceptedOperations;
  private final Set<String> existingTargetSubpops;
  private SubPopulationData subPopulationData;
  private String defaultPop;

  public TargetPopulationsJSON(JsonNode topNode) {
    super(topNode);
    targetPopulations = new ArrayList<>();
    existingTargetSubpops = new HashSet<>();
  }


  @Override
  public void parseAllData() throws InvalidFactorException, InvalidOperationException {
    acceptedOperations = getAcceptedOperations(getTopNode());
    defaultOperation = getDefaultOperation(getTopNode());
    defaultPop = ParserUtils.getStringDefault(getTopNode(), "defaultPopulation", s -> true,
        DEFAULT_DEFAULT_POPULATION_NAME, "");
    targetPopulations = setupTargetPopulations();
    Set<String> otherSubPops = getAllPopulations(getTopNode().get("populations"));
    otherSubPops.remove(defaultPop);
    subPopulationData = new SubPopulationData(otherSubPops, defaultPop);
  }

  /**
   * Retrieves all allowed populations
   *
   * @param array the array of population names
   * @return a set of population names
   */
  private Set<String> getAllPopulations(JsonNode array) {
    Set<String> set = new HashSet<>();
    for (JsonNode pop : array) {
      set.add(pop.asText());
    }
    return set;
  }

  /**
   * Creates the list of target populations.
   *
   * @return a list of target populations
   */
  private List<TargetPopulation> setupTargetPopulations()
      throws InvalidFactorException, InvalidOperationException {
    List<TargetPopulation> lst = new ArrayList<>();
    try {
      JsonNode targetPopulationsNode = getTopNode().get("targetPopulations");
      for (JsonNode targetPopulationNode : targetPopulationsNode) {
        try {
          String name = ParserUtils.getNonBlankString(targetPopulationNode, "name");
          if (existingTargetSubpops.contains(name) || defaultPop.equals(name)) {
            String msg = String.format(
                "Duplicated name %s for TargetPopulation found. Populations should have unique names, skipping duplicate",
                name);
            LOG.error(msg);
            throw new ConfigJSONException(msg);
          }
          String source = ParserUtils.getNonBlankString(targetPopulationNode, "source");
          String growthOperation = ParserUtils.getNonBlankString(targetPopulationNode,
              "growthFactorsOperation");
          String sourceOperation = ParserUtils.getNonBlankString(targetPopulationNode,
              "sourceFactorsOperation");
          FactorCollection growthFactors = makeFactorCollection(growthOperation,
              targetPopulationNode.get("growthFactors"));
          FactorCollection sourceFactors = makeFactorCollection(sourceOperation,
              targetPopulationNode.get("sourceFactors"));
          existingTargetSubpops.add(name);
          TargetPopulation targetPopulation = new TargetPopulation(name, source, growthFactors,
              sourceFactors);
          lst.add(targetPopulation);
          addDrainPopulations(targetPopulation, targetPopulationNode.get("drains"));
        } catch (NullPointerException | InvalidFactorException | InvalidOperationException e) {
          LOG.warn("A TargetPopulation was unable to be created, skipping");
          throw e;
        }
      }
      if (lst.isEmpty()) {
        String msg = "No valid TargetPopulations in growth.json";
        LOG.error(msg);
        throw new ConfigJSONException(msg);
      }
      return lst;
    } catch (NullPointerException f) {
      String msg = "No array of target populations provided to node \"targetPopulations\" in growth.json";
      LOG.error(msg);
      throw new ConfigJSONException(msg);
    }
  }

  /**
   * Adds drain populations to target population.
   *
   * @param targetPopulation the target population
   * @param array            the array of drain population json nodes
   */
  private void addDrainPopulations(TargetPopulation targetPopulation, JsonNode array)
      throws InvalidFactorException, InvalidOperationException {
    for (JsonNode drain : array) {
      targetPopulation.addDrainPopulation(makeDrainPopulation(drain));
    }
  }

  /**
   * Creates a DrainPopulation instance from a json node
   *
   * @param drainNode json node
   * @return DrainPopulation instance
   */
  private DrainPopulation makeDrainPopulation(JsonNode drainNode)
      throws InvalidFactorException, InvalidOperationException {
    String name = ParserUtils.getNonBlankString(drainNode, "name");
    String operation = ParserUtils.getNonBlankString(drainNode, "operation");
    FactorCollection factorCollection = makeFactorCollection(operation, drainNode.get("factors"));
    return new DrainPopulation(name, factorCollection);
  }


  public SubPopulationData getSubPopulationData() {
    return this.subPopulationData;
  }

  /**
   * Creates a list of accepted operations
   */
  private List<String> getAcceptedOperations(JsonNode jnode) {
    List<String> lst = new ArrayList<>();
    try {
      for (JsonNode j : jnode.get("acceptedOperations")) {
        String n = j.asText();
        if (!n.isBlank()) {
          lst.add(n);
        }
      }
      return lst;
    } catch (NullPointerException e) {
      return new ArrayList<>();
    }
  }

  /**
   * Retrieves the default operation.
   */
  private String getDefaultOperation(JsonNode jnode) {
    try {
      String def = ParserUtils.getNonBlankString(jnode, "defaultOperation");
      if (!acceptedOperations.contains(def)) {
        acceptedOperations.add(def);
      }
      return def;
    } catch (NullPointerException e) {
      if (acceptedOperations.isEmpty()) {
        acceptedOperations.add(DEFAULT_NORMAL_OPERATION);
        return DEFAULT_NORMAL_OPERATION;
      }
      return acceptedOperations.get(0);
    }
  }

  public Iterator<TargetPopulation> targetPopulationIterator() {
    return targetPopulations.iterator();
  }
}
