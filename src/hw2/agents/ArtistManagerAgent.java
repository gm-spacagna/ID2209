package hw2.agents;

import hw1.agents.curator.CCuratorAgent;
import hw1.service.ProvidedService;
import hw1.service.ServiceProvidersManager;
import hw2.auction.Auction;
import hw2.auction.AuctionStrategyBehaviour;
import hw2.auction.Auction.State;
import hw2.auction.Auction.Type;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import behaviours.Looper;

/**
 * An agent that can discover all registered services.
 * 
 * @author Gianmario Spacagna (gspacagn@cisco.com)
 */
public class ArtistManagerAgent extends Agent {

  private static final int INITIAL_PRICE = 100;
  private static final int MINIMUM_PRICE = 60;
  private static final int REDUCTION = 5;
  private int currentOffer;

  AID curator;
  Auction auction;

  @Override
  protected void setup() {
    addBehaviour(new WakerBehaviour(this, 7000) {

      @Override
      public void onWake() {
        try {
          curator = getCuratorAid();
          informStartOfAuction(curator);
        } catch (IOException e) {
          e.printStackTrace();
        } catch (FIPAException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

      private void informStartOfAuction(AID curator) throws IOException {
        System.out.println("Informing curator of the auction");

        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.setSender(getAID());
        msg.addReplyTo(getAID());
        msg.setProtocol(FIPANames.InteractionProtocol.FIPA_DUTCH_AUCTION);
        msg.addReceiver(curator);
        auction = new Auction(State.STARTED, Type.PROPOSAL, INITIAL_PRICE);
        currentOffer = INITIAL_PRICE;
        msg.setContentObject(auction);

        send(msg);
      }
    });

    addBehaviour(new AuctionStrategyBehaviour(auction) {

      @Override
      protected boolean proposalStrategy(Auction auction) {
        // no proposals are allowed by participants
        auction.setOffer(currentOffer);
        auction.setType(Type.PROPOSAL);
        auction.setState(State.NEGOTIATING);
        return false;
      }

      @Override
      protected boolean acceptStrategy(Auction auction) {
        if (auction.getOffer() >= currentOffer) {
          auction.setState(State.SOLD);
          auction.setType(Type.ACCEPT);
          sign(auction);
          // wait for his confirmation before to complete the deal
        } else {
          // cheating, re-send the proposal
          auction.setOffer(currentOffer);
          auction.setType(Type.PROPOSAL);
          auction.setState(State.NEGOTIATING);
        }
        return false;
      }

      @Override
      protected boolean rejectStrategy(Auction auction) {
        if (auction.getState().equals(State.NEGOTIATING)
            && currentOffer >= MINIMUM_PRICE) {
          currentOffer -= REDUCTION;
          auction.setOffer(currentOffer);
          auction.setType(Type.PROPOSAL);
          return false;
        } else {
          auction.setType(Type.REJECT);
          auction.setState(State.NOT_SOLD);
          reject(auction);
          return true;
        }
      }
    });
  }

  private AID getCuratorAid() throws FIPAException {
    DFAgentDescription template = new DFAgentDescription();
    ServiceDescription sd = new ServiceDescription();
    sd.setType(CCuratorAgent.CURATOR_AGENT_NAME);
    template.addServices(sd);
    DFAgentDescription[] result = DFService.search(this, template);
    return result[0].getName();
  }

  public ArtistManagerAgent() {
    super();
  }
}
