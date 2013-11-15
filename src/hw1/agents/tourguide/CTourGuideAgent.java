package hw1.agents.tourguide;

import hw1.agents.ServiceProviderAgent;
import hw1.agents.curator.CArtifact;
import hw1.agents.curator.CCuratorAgent;
import hw1.agents.profiler.CProfilerAgent;
import hw1.agents.profiler.CUser;
import hw1.service.PrintableArray;
import hw1.service.ProvidedService;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class CTourGuideAgent extends ServiceProviderAgent {
  
  public static final String TOURGUIDE_ONTOLOGY = "Tourguide-ontology";
  public static final String TOUR_GUIDE_AGENT_NAME = "TourGuide";
  private List<CArtifact> artifacts = new ArrayList<CArtifact>();

  
  private final static int TIME_DELAY_FOR_REQUESTING_ARTIFACTS = 7000;

  protected void setup() {
    super.setup();
    System.out.println(">>> The tour guide agent is being started ...");
    registerAgent();
    SequentialBehaviour sequential = new SequentialBehaviour();
    sequential
        .addSubBehaviour(new CBehaviourForRequestingArtifactsListFromCurator(
            this));
    sequential.addSubBehaviour(new CBehaviorForReceivingMessages());
    this.addBehaviour(sequential);
  }

  private void registerAgent() {
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType(TOUR_GUIDE_AGENT_NAME);
    sd.setName(TOUR_GUIDE_AGENT_NAME);
    dfd.addServices(sd);
    try {
      DFService.register(this, dfd);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }
  }

  @Override
  protected void takeDown() {
    super.takeDown();
  }

  private class CBehaviorForReceivingMessages extends CyclicBehaviour {
    private static final long serialVersionUID = 1L;
    private MessageTemplate mt = MessageTemplate
        .MatchPerformative(ACLMessage.REQUEST);
    private MessageTemplate mt_inform = MessageTemplate
        .MatchPerformative(ACLMessage.INFORM);

    @Override
    public void action() {
      ACLMessage msg = receive(mt);
      if (msg == null) {
        msg = receive(mt_inform);
      }
      if (msg != null) {
        if (msg.getOntology()
            .equalsIgnoreCase(CProfilerAgent.PROFILER_ONTOLOGY)) {
          try {
            System.out.println(">>> Tour Guide Agent: Received request "
                + "from the user profiler agent, "
                + "generating recommendations ...");
            ArrayList<Integer> recommendedIDs = calculateRecommendations((CUser) msg
                .getContentObject());
            // send the reply here immediately to the user
            sendListOfSuggestedArtifactsToTheUser(msg.createReply(),
                recommendedIDs);
          } catch (UnreadableException ex) {
            Logger.getLogger(CTourGuideAgent.class.getName()).log(Level.SEVERE,
                null, ex);
          }
        } else if (msg.getOntology().equalsIgnoreCase(
            CCuratorAgent.CURATOR_ONTOLOGY)) {
          try {
            artifacts = ((List<CArtifact>) msg.getContentObject());
            System.out
                .println(">>> Tour Guide Agent: Received artifacts list from the "
                    + "curator agent. Size: " + artifacts.size());
            registerService();
          } catch (UnreadableException e) {
            e.printStackTrace();
          }
        } else {
          System.out.println(">>> Tour Guide Agent:"
              + ": Couldn't match the ontology of the incoming message"
              + msg.getOntology());
        }
      } else {
        block();
      }
    }

    /**
     * Composes a list of recommended objects based on the interests of the user
     * 
     * @param user
     */
    private ArrayList<Integer> calculateRecommendations(CUser user) {
      ArrayList<Integer> recommendationIds = new ArrayList<Integer>();
      for (CArtifact a : artifacts) {
        if (user.getListOfInterests().contains(a.getGenre())) {
          recommendationIds.add(a.getId());
        }
      }
      return recommendationIds;
    }
  }

  private void requestExistingArtifactsFromTheCuratorAgent(AID name) {
    System.out.println(">>> Guide: requesting existing artifacts from the "
        + "curator agent");
    ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
    msg.addReceiver(name);
    msg.setLanguage("Java Serialized");
    msg.setOntology(TOURGUIDE_ONTOLOGY);
    send(msg);
  }

  private void sendMessageToCurator(Agent myAgent) {
    // SENDING MESSAGE TO COURATOR AGENT:
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription sd = new ServiceDescription();
    sd.setType(CCuratorAgent.CURATOR_AGENT_NAME);
    template.addServices(sd);
    try {
      DFAgentDescription[] result = DFService.search(myAgent, template);
      for (int i = 0; i < result.length; ++i) {
        requestExistingArtifactsFromTheCuratorAgent(result[i].getName());
      }
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }
  }

  private class CBehaviourForRequestingArtifactsListFromCurator extends
      WakerBehaviour {
    private Agent m_agent;

    public CBehaviourForRequestingArtifactsListFromCurator(Agent a) {
      super(a, TIME_DELAY_FOR_REQUESTING_ARTIFACTS);
      m_agent = a;
    }

    private static final long serialVersionUID = 1L;

    @Override
    public void onWake() {
      sendMessageToCurator(myAgent);
      m_agent.addBehaviour(new CBehaviourForRequestingArtifactsListFromCurator(
          m_agent));
    }
  }

  private void sendListOfSuggestedArtifactsToTheUser(
      ACLMessage responseMessageToUser, ArrayList<Integer> recommendedIDs) {
    try {
      System.out
          .println(">>> Tour Guide Agent: Sending the list of suggested artifacts ids"
              + " to the profiler...");
      responseMessageToUser.setContentObject((Serializable) recommendedIDs);
      responseMessageToUser.setPerformative(ACLMessage.INFORM);
      responseMessageToUser.setOntology(TOURGUIDE_ONTOLOGY);
      send(responseMessageToUser);
    } catch (IOException e) {
      System.out.println(">>> Tour Guide Agent: An error occured while trying "
          + "to send recommendations to the user");
      e.printStackTrace();
    }
  }

  @Override
  protected ProvidedService getService() {
    return new ProvidedService(new PrintableArray(artifacts));
  }
}