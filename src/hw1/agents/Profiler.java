package hw1.agents;

import property.OfferedServiceManager;
import property.OfferedServiceProperty;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * An agent that provides and register a service.
 * 
 * @author Gianmario Spacagna (gspacagn@cisco.com)
 */
public class Profiler extends Agent {

  @Override
  protected void takeDown() {
    super.takeDown();
    try {
      DFService.deregister(this);
    } catch (FIPAException e) {
      // Printout a dismissal message
      System.out.println("Agent " + getAID().getName() + " terminating.");
    }
  }

  @Override
  protected void setup() {
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType("awesome-service");
    sd.setName(getLocalName());
    OfferedServiceManager.putProperty(getAID(), new OfferedServiceProperty(
        new Profile("My profile is awesome!!!")));
    dfd.addServices(sd);
    try {
      DFService.register(this, dfd);
      System.out.println("Profiler: I have registered my service");
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public Profiler() {
    super();
  }

  class Profile {
    @Override
    public String toString() {
      return "Profile [description=" + description + "]";
    }

    String description;

    public Profile(String description) {
      super();
      this.description = description;
    }
  }
}
