package hw1.agents;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

/**
 * An Agent that subscribes to service notification.
 * 
 * @author Gianmario Spacagna (gspacagn@cisco.com)
 */
public class Curator extends Agent {

  @Override
  protected void setup() {
    try {
      subscribe();
    } catch (FIPAException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("serial")
  private void subscribe() throws FIPAException {
    DFAgentDescription dfd = new DFAgentDescription();
    ServiceDescription sd = new ServiceDescription();
    sd.setType("awesome-service");
    dfd.addServices(sd);
    SearchConstraints sc = new SearchConstraints();
    sc.setMaxResults(new Long(1));
    ACLMessage message = DFService.createSubscriptionMessage(this,
        getDefaultDF(), dfd, sc);

    System.out.println("Subscription Message is:");
    System.out.println(message);

    addBehaviour(new SubscriptionInitiator(this, message) {
      protected void handleInform(ACLMessage inform) {
        try {
          DFAgentDescription[] dfds = DFService.decodeNotification(inform
              .getContent());
          
          System.out.println("New agents have registered:");
          for (DFAgentDescription dfd : dfds) {
            System.out.println(dfd.getName());
          }
        } catch (FIPAException fe) {
          fe.printStackTrace();
        }
      }
    });
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public Curator() {
    super();
  }
}
