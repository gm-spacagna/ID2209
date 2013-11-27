package hw2.agents;

import hw1.agents.curator.CCuratorAgent;
import hw2.auction.Auction;
import hw2.auction.AuctionStrategyBehaviour;
import hw2.auction.Auction.State;
import hw2.auction.Auction.Type;

/**
 * A curator agent which implements the negotiation protocol.
 * 
 * @author Gianmario Spacagna (gspacagn@cisco.com)
 */
public class NegotiatingCuratorAgent extends CCuratorAgent {

  private static final int MAX_PRICE = 70;

  @Override
  protected void setup() {
    System.out.println(">>> The curator agent is being started ...");
    registerAgentToDF();
    addBehaviour(new AuctionStrategyBehaviour(auction) {

      @Override
      protected boolean proposalStrategy(Auction auction) {
        if (auction.getOffer() <= MAX_PRICE) {
          auction.setType(Type.ACCEPT);
        } else {
          auction.setType(Type.REJECT);
          auction.setState(State.NEGOTIATING);
        }
        return false;
      }

      @Override
      protected boolean acceptStrategy(Auction auction) {
        if (auction.getOffer() <= MAX_PRICE) {
          sign(auction);
          return true;
        } else {
          // cheating
          auction.setType(Type.REJECT);
          return false;
        }
      }

      @Override
      protected boolean rejectStrategy(Auction auction) {
        reject(auction);
        return true;
      }
    });
    super.setup();
  }

  /**
   * 
   */
  private static final long serialVersionUID = -5285952338205411274L;

}
