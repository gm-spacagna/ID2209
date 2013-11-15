package hw1.service;

import jade.domain.FIPAAgentManagement.Property;

public class ProvidedService extends Property {

  @Override
  public String toString() {
    return "ProvidedService [getName()=" + getName() + ", getValue()="
        + getValue() + "]";
  }

  public static final String PROVIDED_SERVICE = "provided-service";

  public ProvidedService(Object value) {
    super(PROVIDED_SERVICE, value);
  }
}
