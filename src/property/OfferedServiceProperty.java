package property;

import jade.domain.FIPAAgentManagement.Property;

public class OfferedServiceProperty extends Property {

  @Override
  public String toString() {
    return "OfferedServiceProperty [getName()=" + getName() + ", getValue()="
        + getValue() + "]";
  }

  public static final String OFFERED_SERVICE = "offered-service";

  public OfferedServiceProperty(Object value) {
    super(OFFERED_SERVICE, value);
  }
}
