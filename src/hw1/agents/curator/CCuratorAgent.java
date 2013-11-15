package hw1.agents.curator;

import hw1.agents.ServiceProviderAgent;
import hw1.agents.profiler.CProfilerAgent;
import hw1.agents.tourguide.CTourGuideAgent;
import hw1.service.PrintableArray;
import hw1.service.ProvidedService;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.states.MsgReceiver;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Current agent represents a gallery or a museum and provides the data about
 * artifacts currently available. Processes incoming requests regarding details
 * of the artifacts.
 * 
 * @author veronika
 * 
 */
public class CCuratorAgent extends ServiceProviderAgent {
  private static final long serialVersionUID = 1L;
  public static final String CURATOR_AGENT_NAME = "curatorAgent";
  public static final String CURATOR_ONTOLOGY = "curator-ontology";

  // template for filtering incoming messages
  private final MessageTemplate requestTemplate = new MessageTemplate(
      new MessageTemplate.MatchExpression() {
        @Override
        public boolean match(ACLMessage aclm) {
          if (aclm == null || aclm.getOntology() == null) {
            return false;
          }

          if (aclm.getOntology().equals(CTourGuideAgent.TOURGUIDE_ONTOLOGY)) {
            return true;
          }
          return false;
        }
      });

  @Override
  protected void setup() {
    super.setup();
    System.out.println(">>> The curator agent is being started ...");
    SequentialBehaviour sequential = new SequentialBehaviour();
    sequential.addSubBehaviour(new CMesageReceiver(this, requestTemplate,
        MsgReceiver.INFINITE, new DataStore(), new Object()));
    sequential.addSubBehaviour(new CBehaviourForListeningIncomingMessages());
    this.addBehaviour(sequential);
    registerAgentToDF();
  }

  @Override
  protected void takeDown() {
    unregisterAgentFromDF();
  }

  private void registerAgentToDF() {
    DFAgentDescription dfd = new DFAgentDescription();
    dfd.setName(getAID());
    ServiceDescription sd = new ServiceDescription();
    sd.setType(CURATOR_AGENT_NAME);
    sd.setName(CURATOR_AGENT_NAME);
    dfd.addServices(sd);

    try {
      DFService.register(this, dfd);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }
  }

  private void unregisterAgentFromDF() {
    try {
      DFService.deregister(this);
    } catch (FIPAException fe) {
      fe.printStackTrace();
    }
    System.out.println("The curator agent " + getAID().getName()
        + " is being terminated ...");
  }

  public class CBehaviourForListeningIncomingMessages extends CyclicBehaviour {
    private MessageTemplate mt = MessageTemplate
        .MatchPerformative(ACLMessage.REQUEST);
    private MessageTemplate mt_inform = MessageTemplate
        .MatchPerformative(ACLMessage.INFORM);
    private ACLMessage responseMessage;

    @Override
    public void action() {
      ACLMessage msg = receive(mt_inform);
      if (msg == null) {
        msg = receive(mt);
      }

      if (msg != null) {
        if (msg.getOntology().equals(CProfilerAgent.PROFILER_ONTOLOGY)) {
          Random r = new Random();
          int n = r.nextInt(2);
          if (n > 0)// sometimes just skip answering, pretending that
          // nothing changed
          {
            try {
              @SuppressWarnings("unchecked")
              List<Integer> ids = (List<Integer>) msg.getContentObject();
              System.out.println(">>> Curator Agent: Received a request for"
                  + " additional information about the artifacts");

              responseMessage = msg.createReply();
              responseMessage.setOntology(CURATOR_ONTOLOGY);
              responseMessage.setContentObject((Serializable) CArtifact
                  .getArtifactsByIDs(ids));
              send(responseMessage);
            } catch (UnreadableException e) {
              e.printStackTrace();
            } catch (IOException e) {
              responseMessage.setPerformative(ACLMessage.FAILURE);
              responseMessage
                  .setContent(">>> Curator Agent: An error occured with "
                      + "the list serialization");
            }
          }
        }// end id Profiler
        else if (msg.getOntology().equals(CTourGuideAgent.TOURGUIDE_ONTOLOGY)) {
          System.out.println(">>> Curator Agent: Received a request for"
              + " the list of existing artifacts.");

          responseMessage = msg.createReply();
          responseMessage.setOntology(CURATOR_ONTOLOGY);
          try {
            responseMessage.setContentObject((Serializable) CArtifact
                .getRandomListOfArtifacts());
            send(responseMessage);
          } catch (IOException e) {
            System.out.println(">>> Curator Agent: An error occured with "
                + "the list serialization");
          }
        }
      } else {
        block();
      }
    }
  }

  public class CMesageReceiver extends MsgReceiver {
    CCuratorAgent m_curatorAgent;

    public CMesageReceiver(CCuratorAgent curatorAgent,
        MessageTemplate curatorRequest, int infinite, DataStore dataStore,
        Object object) {
      super(curatorAgent, curatorRequest, infinite, dataStore, object);
      m_curatorAgent = curatorAgent;
    }

    private static final long serialVersionUID = 1L;
    private ACLMessage responseMessage;

    @Override
    protected void handleMessage(ACLMessage msg) {
      System.out.println(">>> Curator Agent: Received a request for"
          + " the list of existing artifacts.");

      responseMessage = msg.createReply();
      responseMessage.setOntology(CURATOR_ONTOLOGY);
      try {
        responseMessage.setContentObject((Serializable) CArtifact
            .getRandomListOfArtifacts());
        send(responseMessage);
      } catch (IOException e) {
        System.out.println(">>> Curator Agent: An error occured with "
            + "the list serialization");
      }
    }
  }

  @Override
  protected ProvidedService getService() {
    return new ProvidedService(new PrintableArray(CArtifacts.LIST));
  }
}