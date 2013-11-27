package hw2.auction;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.io.Serializable;

public abstract class AuctionStrategyBehaviour extends CyclicBehaviour {

  Auction auction;

  private MessageTemplate mt = MessageTemplate
      .MatchPerformative(ACLMessage.REQUEST);

  public AuctionStrategyBehaviour(Auction auction) {
    this.auction = auction;
  }

  @Override
  public void action() {
    block(1000);
    ACLMessage msg = myAgent.receive(mt);

    if (null != msg) {
      try {
        {
          Serializable content = msg.getContentObject();
          if (null != content) {
            Auction auction = (Auction) content;

            System.out.println("Agent " + myAgent.getLocalName()
                + " has received the following auction message: " + auction);

            boolean dealCompleted = false;
            switch (auction.getType()) {
            case ACCEPT:
              dealCompleted = acceptStrategy(auction);
              break;
            case PROPOSAL:
              dealCompleted = proposalStrategy(auction);
              break;
            case REJECT:
              dealCompleted = rejectStrategy(auction);
              break;
            }
            System.out
                .println("Agent " + myAgent.getLocalName()
                    + " replied with the following auction message: "
                    + auction);

            // save the new state
            this.auction = auction;
            // reply
            if (!dealCompleted) {
              ACLMessage reply = msg.createReply();
              reply.setContentObject(auction);
              myAgent.send(reply);
            }
          }
        }
      } catch (UnreadableException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      block();
    }
  }

  protected void sign(Auction auction) {
    System.out.println("Agent " + myAgent.getName()
        + " has signed the deal of auction " + auction);
  }

  protected void reject(Auction auction) {
    System.out.println("Agent " + myAgent.getName()
        + " has rejected the deal of auction " + auction);
  }

  protected abstract boolean proposalStrategy(Auction auction);

  protected abstract boolean acceptStrategy(Auction auction);

  protected abstract boolean rejectStrategy(Auction auction);
}