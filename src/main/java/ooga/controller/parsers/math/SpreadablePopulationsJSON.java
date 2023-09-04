package ooga.controller.parsers.math;

import com.fasterxml.jackson.databind.JsonNode;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import ooga.controller.parsers.DataFileParser;
import ooga.controller.parsers.ParserUtils;
import ooga.model.gamestate.statehandler.SpreadablePopulation;
import ooga.model.region.Census;
import ooga.util.Operation;

/**
 * A parsing class that reads in the spread json file and creates a list of SpreadablePopulations
 * for use in the SpreadCalculator and a starting census that will be added to a region upon
 * selection at the start of the game.
 */
public class SpreadablePopulationsJSON extends DataFileParser {

  private static final String OPERATION_PACKAGE = "ooga.util.";
  private final List<SpreadablePopulation> spreadablePopulationList;
  private final Census startCensus;

  public SpreadablePopulationsJSON(JsonNode topNode) {
    super(topNode);
    spreadablePopulationList = new ArrayList<>();
    startCensus = new Census();
  }

  @Override
  public void parseAllData() throws InvalidOperationException {
    JsonNode spreadablePopulationsNode = getTopNode().get("spreadablePopulations");
    for (JsonNode populationNode : spreadablePopulationsNode) {
      String name = ParserUtils.getNonBlankString(populationNode, "name");
      String alternatePopulation = ParserUtils.getNonBlankString(populationNode,
          "alternatePopulation");
      String operationClassName = ParserUtils.getNonBlankString(populationNode, "operation");
      int startAmt = ParserUtils.getIntValue(populationNode, "startAmt");
      Operation operation;
      Class<?> clazz;
      try {
        clazz = Class.forName(String.format("%s%s", OPERATION_PACKAGE, operationClassName));
        operation = (Operation) clazz.getDeclaredConstructor().newInstance();
      } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
               InvocationTargetException | NoSuchMethodException e) {
        throw new InvalidOperationException(operationClassName);
      }
      SpreadablePopulation population = new SpreadablePopulation(name, alternatePopulation,
          operation);
      spreadablePopulationList.add(population);
      startCensus.addPopulation(name, startAmt);
    }
  }

  public Iterator<SpreadablePopulation> spreadablePopulationIterator() {
    return spreadablePopulationList.iterator();
  }

  public Census getStartCensus() {
    return startCensus;
  }
}
