package ooga.controller.parsers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import ooga.controller.parsers.end_conditions.InvalidConditionException;
import ooga.controller.parsers.math.InvalidFactorException;
import ooga.controller.parsers.math.InvalidOperationException;
import ooga.model.gamestate.statehandler.factors.Factor;
import ooga.model.gamestate.statehandler.factors.FactorCollection;
import ooga.util.BiOperation;
import ooga.util.ConditionalOperation;
import ooga.util.Operation;

/**
 * Generic JSON data file parser class that can parse by taking in either a file or JsonNode.
 *
 * @author diego
 */
public abstract class DataFileParser {

  private static final String OPERATION_PACKAGE = "ooga.util.";
  private static final String FACTOR_PACKAGE = "ooga.model.gamestate.statehandler.factors.";
  protected JsonNode topNode;

  /**
   * Blank constructor.
   */
  public DataFileParser() {
  }

  /**
   * Initializes parser and topmost JNode with a file.
   *
   * @param file complete filepath of the JSON data file.
   */
  public DataFileParser(File file) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      this.topNode = mapper.readTree(file);
//      this.parseAllData(topNode); // Todo: perhaps create a new abstract method, initParser()?
    } catch (IOException e) {
      topNode = null;
      throw new ConfigJSONException(
          "Could not retrieve topmost node from Json file at " + file.toString());
      // TODO: Handle JSON file acquisition errors. i.e., not a JSON file, blank, broken, etc...
    }
  }

  /**
   * Initializes parser with a pre-generated JsonNode. No file required.
   *
   * @param jnode Pre-generated JsonNode containing data.
   */
  public DataFileParser(JsonNode jnode) {
    topNode = jnode;
  }

  /**
   * @return Topmost JsonNode in the data file.
   */
  public JsonNode getTopNode() {
    return this.topNode;
  }

  /**
   * Allows logic to be performed on a jnode before setting it as topmost node.
   *
   * @param jnode
   */
  protected void setTopNode(JsonNode jnode) {
    this.topNode = jnode;
  }

  /**
   * Main method that executes the complete data parsing of a JSON file.
   */
  public abstract void parseAllData()
      throws InvalidOperationException, InvalidFactorException, InvalidConditionException;

  protected Factor makeFactor(JsonNode factorNode)
      throws InvalidOperationException, InvalidFactorException {
    String name = ParserUtils.getNonBlankString(factorNode, "name");
    String factorClassName = ParserUtils.getNonBlankString(factorNode, "type");
    String operationClassName = ParserUtils.getNonBlankString(factorNode, "operation");
    Operation operation = makeOperation(operationClassName);
    Class<?> clazz;
    Factor factor = null;
    try {
      clazz = Class.forName(String.format("%s%s", FACTOR_PACKAGE, factorClassName));
      factor = (Factor) clazz.getDeclaredConstructor(String.class, Operation.class)
          .newInstance(name, operation);
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
             InvocationTargetException | NoSuchMethodException e) {
      throw new InvalidFactorException(factorClassName);
    }
    return factor;
  }

  protected Operation makeOperation(String name) throws InvalidOperationException {
    Class<?> clazz = null;
    Operation operation = null;
    try {
      clazz = Class.forName(String.format("%s%s", OPERATION_PACKAGE, name));
      operation = (Operation) clazz.getDeclaredConstructor().newInstance();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
             InvocationTargetException |
             NoSuchMethodException e) {
      throw new InvalidOperationException(name);
    }
    return operation;
  }

  protected BiOperation makeBiOperation(String name) throws InvalidOperationException {
    Class<?> clazz = null;
    BiOperation operation = null;
    try {
      clazz = Class.forName(String.format("%s%s", OPERATION_PACKAGE, name));
      operation = (BiOperation) clazz.getDeclaredConstructor().newInstance();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
             InvocationTargetException |
             NoSuchMethodException e) {
      throw new InvalidOperationException(name);
    }
    return operation;
  }

  protected FactorCollection makeFactorCollection(String operationName, JsonNode factorsNode)
      throws InvalidOperationException, InvalidFactorException {
    BiOperation operation = makeBiOperation(operationName);
    FactorCollection factorCollection = new FactorCollection(operation);
    for (JsonNode factorNode : factorsNode) {
      factorCollection.addFactor(makeFactor(factorNode));
    }
    return factorCollection;
  }

  protected ConditionalOperation makeConditionalOperation(String operationName)
      throws InvalidOperationException {
    Class<?> clazz = null;
    ConditionalOperation operation = null;
    try {
      clazz = Class.forName(String.format("%s%s", OPERATION_PACKAGE, operationName));
      operation = (ConditionalOperation) clazz.getDeclaredConstructor().newInstance();
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
             InvocationTargetException |
             NoSuchMethodException e) {
      throw new InvalidOperationException(operationName);
    }
    return operation;
  }
}
