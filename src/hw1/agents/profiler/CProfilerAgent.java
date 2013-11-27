package hw1.agents.profiler;

import hw1.agents.ServiceProviderAgent;
import hw1.agents.curator.CArtifact;
import hw1.agents.curator.CCuratorAgent;
import hw1.agents.tourguide.CTourGuideAgent;
import hw1.service.ProvidedService;
import hw2.auction.Auction;
import hw2.auction.AuctionStrategyBehaviour;
import hw2.auction.Auction.State;
import hw2.auction.Auction.Type;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * Profile agent class. Profiler Agents maintains the profile of the user. The
 * profile contains basic user information (age, occupa0on, gender, interest,
 * etc) and visited museum artifacts. Profiler Agent travels around the network
 * and looks for interesting information about art and culture from online
 * museums or art galleries on the Internet.
 * 
 * @author veronika
 * 
 */
public class CProfilerAgent extends ServiceProviderAgent {
  private static final long serialVersionUID = 1L;
  public final static String PROFILER_ONTOLOGY = "Profiler-ontology";
  private CUser user;
  private List<Integer> recommendations = new ArrayList<Integer>();
  private final static int TIME_DELAY_FOR_REQUESTING_TOUR_GUIDE = 5000;
  private static final String AGENT_NAME = "profiler_agent";
  
  @Override
  protected void setup() {
    super.setup();
    System.out
        .println(">>> Profiler Agent: The profiler agent is being launched...");
    user = CUser.generateRandomUser();
    registerService();
    // sequental behaviour
    SequentialBehaviour sequential = new SequentialBehaviour();

    ParallelBehaviour par = new ParallelBehaviour(this,
        ParallelBehaviour.WHEN_ALL);
    par.addSubBehaviour(new CBehaviorForRequestingRecommendationsFromTourGuide(
        this, TIME_DELAY_FOR_REQUESTING_TOUR_GUIDE));
    par.addSubBehaviour(new CBehaviorForReceivingNotifications());
    sequential.addSubBehaviour(new CBehaviourForDefiningUserData());
    sequential.addSubBehaviour(par);

    this.addBehaviour(sequential);
    
    
    
    
  }

  private void registerAgent() {
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType(AGENT_NAME);
    sd.setName(AGENT_NAME);
    dfd.addServices(sd);
    try {
      DFService.register(this, dfd);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }
  }

  @Override
  protected void takeDown() {
    System.out.println(">>> Profiler agent " + getAID()
        + " is being terminated...");
  }

  private void printRecommendedArtifactsIDs() {
    for (Integer r : recommendations) {
      System.out.print(r + ", ");
    }
    System.out.println("\n");
  }

  private void printRecommendedArtifactsData(List<CArtifact> artifacts) {
    for (CArtifact a : artifacts) {
      System.out.println(a.toString());
    }
  }

  private class CBehaviorForRequestingRecommendationsFromTourGuide extends
      TickerBehaviour {
    public CBehaviorForRequestingRecommendationsFromTourGuide(Agent a,
        long period) {
      super(a, period);
    }

    private static final long serialVersionUID = 1L;

    private void sendRequestToTourGuideAgent(AID name) {
      ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
      msg.addReceiver(name);
      msg.setLanguage("Java Serialized");
      msg.setOntology(PROFILER_ONTOLOGY);
      try {
        msg.setContentObject(user);
        send(msg);
      } catch (IOException ex) {
        Logger.getLogger(CProfilerAgent.class.getName()).log(
            Level.SEVERE,
            ">>> Profiler Agent: An error occured on sending "
                + "a request to the tour guide agent. ", ex);
      }
    }
    
    @Override
    public void onTick() {
      DFAgentDescription template = new DFAgentDescription();
      ServiceDescription sd = new ServiceDescription();
      sd.setType(CTourGuideAgent.TOUR_GUIDE_AGENT_NAME);
      template.addServices(sd);
      try {
        DFAgentDescription[] result = DFService.search(myAgent, template);
        for (int i = 0; i < result.length; ++i) {
          sendRequestToTourGuideAgent(result[i].getName());
        }
      } catch (FIPAException fe) {
        fe.printStackTrace();
      }
    }
  }

  private class CBehaviourForDefiningUserData extends OneShotBehaviour {
    @Override
    public final void action() {
      System.out
          .println(">>> Profiler Agent: Generated a user with the following data:"
              + user.toString());
      System.out
          .println(">>> Profiler Agent: Would you like anything to change? \n"
              + "1 - YES, 0 - NO\n");
      char nAnswer = 0;
      try {
        nAnswer = (char) System.in.read();
        if (nAnswer == '1') {
          for (Field field : CUser.class.getDeclaredFields()) {
            if (field.getName().equals("interests")
                || field.getName().equals("visitedIds")) {
              break;
            }
            System.out.println(">>> Profile Agent: Would you like to change "
                + field.getName() + "? \n1 - YES, 0 - NO\n");
            nAnswer = (char) System.in.read();
            while (nAnswer != '1' && nAnswer != '0') {
              nAnswer = (char) System.in.read();
            }
            if (nAnswer == '1') {
              Scanner in = new Scanner(System.in);
              System.out.println(">>> Profile Agent: Provide new value:\n ");
              String s = in.nextLine();
              while (s.equals("") || s.equals("\n")) {
                s = in.nextLine();
              }
              try {
                field.set(user, s);
              } catch (Exception ex) {
                System.out
                    .println(">>> Profile Agent: Error with provided value.\n "
                        + ex);
              }
            }
          }
        } else {
          System.out.println(">>> Profiler Agent: Nothing will be changed.");
        }
      } catch (IOException e) {
        System.out.println(">>> Profiler Agent: An error occured while "
            + "reading your answer. " + "Nothing will be changed.");
      }
    }
  }

  /**
   * Cyclic behavior for receiving notifications
   * 
   * @author veronika
   */
  private class CBehaviorForReceivingNotifications extends CyclicBehaviour {
    private static final long serialVersionUID = 1L;
    private MessageTemplate mt = MessageTemplate
        .MatchPerformative(ACLMessage.INFORM);

    private void requestInformatinAboutArtifactsFromCurator() {
      DFAgentDescription template = new DFAgentDescription();
      ServiceDescription sd = new ServiceDescription();
      sd.setType(CCuratorAgent.CURATOR_AGENT_NAME);
      template.addServices(sd);
      try {
        DFAgentDescription[] result = DFService.search(myAgent, template);
        for (int i = 0; i < result.length; ++i) {
          sendMessageToCurator(result[i].getName());
        }
      } catch (FIPAException fe) {
        fe.printStackTrace();
      }
    }

    private void sendMessageToCurator(AID name) {
      System.out
          .println(">>> Profiler agent: requesting existing artifacts from the "
              + "curator agent");
      ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
      msg.addReceiver(name);
      msg.setLanguage("Java Serialized");
      msg.setOntology(PROFILER_ONTOLOGY);
      try {
        msg.setContentObject((Serializable) recommendations);
        send(msg);
      } catch (IOException ex) {
        Logger.getLogger(CTourGuideAgent.class.getName()).log(Level.SEVERE,
            null, ex);
      }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void action() {
      ACLMessage msg = receive(mt);
      if (msg != null) {
        if (msg.getOntology().equalsIgnoreCase(CCuratorAgent.CURATOR_ONTOLOGY)) {
          try {
            System.out.println(">>> Profiler Agent: Received detailed "
                + "info about recommended artifacts :");
            printRecommendedArtifactsData((List<CArtifact>) msg
                .getContentObject());
          } catch (UnreadableException ex) {
            Logger.getLogger(CProfilerAgent.class.getName()).log(Level.SEVERE,
                null, ex);
          }
        } else if (msg.getOntology().equalsIgnoreCase(
            CTourGuideAgent.TOURGUIDE_ONTOLOGY)) {
          try {
            System.out.println(">>> Profiler Agent: Received recommendations "
                + "from the tour guide agent: ");
            recommendations = (List<Integer>) msg.getContentObject();

            if (recommendations.size() > 0) {
              printRecommendedArtifactsIDs();
              requestInformatinAboutArtifactsFromCurator();
            } else {
              System.out.print("nothing was recommended");
            }
          } catch (UnreadableException ex) {
            Logger.getLogger(CProfilerAgent.class.getName()).log(Level.SEVERE,
                null, ex);
          }
        } else {
          System.out.println(">>> Profiler Agent: Couldn't match the "
              + "received message ontology: " + msg.getOntology());
        }
      } else {
        block();
      }
    }
  }

  @Override
  protected ProvidedService getService() {
    return new ProvidedService(user);
  }
}