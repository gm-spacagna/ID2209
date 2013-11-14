package hw1.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import property.OfferedServiceManager;
import property.OfferedServiceProperty;
import behaviours.Looper;

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
    } catch (FIPAException e1) {
      e1.printStackTrace();
    }

    addBehaviour(new Looper(this, 1000, 10) {

      @Override
      protected void loopAction() {
        try {
          agents = searchServices();

          if (agents.size() > 0) {
            System.out.println("TourGuide: Choose desired agent: ");

            for (int i = 0; i < agents.size(); i++) {
              DFAgentDescription agent = agents.get(i);
              System.out.println(i + " " + agent.getName());
            }
            Scanner scan = new Scanner(System.in);

            int userInput = scan.nextInt();

            DFAgentDescription agent = agents.get(userInput);
            System.out.println("TourGuide: Services offered by "
                + agent.getName() + " are:");

            OfferedServiceProperty property = OfferedServiceManager
                .getProperty(agent.getName());

            System.out.println(property);

          }
        } catch (FIPAException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void registerService() throws FIPAException {
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType("service-discovery");
    sd.setName(getLocalName());
    OfferedServiceManager.putProperty(getAID(), new OfferedServiceProperty(
        new Tours(getTours())));
    dfd.addServices(sd);
    DFService.register(this, dfd);
  }

  private Tour[] getTours() {
    return new Tour[] { new Tour("London Eye"), new Tour("London Bridges"),
        new Tour("Rock Music Stars"), new Tour("Queen Elizabeth") };
  }

  private List<DFAgentDescription> searchServices() throws FIPAException {
    DFAgentDescription dfd = new DFAgentDescription();
    SearchConstraints ALL = new SearchConstraints();
    ALL.setMaxResults(new Long(-1));

    DFAgentDescription[] results = DFService.search(this, dfd, ALL);

    List<DFAgentDescription> agents = new ArrayList<DFAgentDescription>(
        results.length);
    System.out.println("TourGuide: " + results.length + " results");
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

  class Tour {
    @Override
    public String toString() {
      return "Tour [name=" + name + "]";
    }

    String name;

    public Tour(String name) {
      super();
      this.name = name;
    }
  }

  class Tours {
    @Override
    public String toString() {
      return "Tours [tours=" + Arrays.toString(tours) + "]";
    }

    Tour[] tours;

    public Tours(Tour... tours) {
      super();
      this.tours = tours;
    }
  }
}
