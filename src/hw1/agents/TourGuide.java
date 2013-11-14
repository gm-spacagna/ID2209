package hw1.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.leap.Iterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * An agent that can discover all registered services.
 * 
 * @author Gianmario Spacagna (gspacagn@cisco.com)
 */
public class TourGuide extends Agent {

  List<DFAgentDescription> agents;

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
    try {
      registerService();
      agents = searchServices();

      System.out.println("Choose desired agent: ");

      for (int i = 0; i < agents.size(); i++) {
        DFAgentDescription agent = agents.get(i);
        System.out.println(i + " " + agent.getName());
      }
      Scanner scan = new Scanner(System.in);

      int userInput = scan.nextInt();

      DFAgentDescription agent = agents.get(userInput);
      System.out.println("Services of " + agent.getName() + " are:");

      Iterator services = agent.getAllServices();

      while (services.hasNext()) {
        ServiceDescription service = (ServiceDescription) services.next();
        System.out.println(service);
      }

    } catch (FIPAException e) {
      e.printStackTrace();
    }
  }

  private void registerService() throws FIPAException {
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType("service-discovery");
    sd.setName(getLocalName());
    dfd.addServices(sd);
    DFService.register(this, dfd);
  }

  private List<DFAgentDescription> searchServices() throws FIPAException {
    DFAgentDescription dfd = new DFAgentDescription();
    SearchConstraints ALL = new SearchConstraints();
    ALL.setMaxResults(new Long(-1));

    DFAgentDescription[] results = DFService.search(this, dfd, ALL);

    List<DFAgentDescription> agents = new ArrayList<DFAgentDescription>(
        results.length);
    System.out.println(results.length + " results");
    for (DFAgentDescription result : results) {
      agents.add(result);
    }
    return agents;
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public TourGuide() {
    super();
  }
}
