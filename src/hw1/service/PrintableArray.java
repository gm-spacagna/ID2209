package hw1.service;

import hw1.agents.curator.CArtifact;

import java.util.Arrays;
import java.util.List;

/**
 * A printable array.
 * 
 * @author Gianmario Spacagna (gspacagn@cisco.com)
 */
public class PrintableArray {

  @Override
  public String toString() {
    return Arrays.toString(array);
  }

  Object[] array;

  public PrintableArray(Object[] array) {
    super();
    this.array = array;
  }

  public PrintableArray(List list) {
    this(list.toArray());
  }
}
