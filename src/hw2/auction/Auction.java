package hw2.auction;

import jade.util.leap.Serializable;

/**
 * An auction action in the dutch protocol.
 * 
 * @author Gianmario Spacagna (gspacagn@cisco.com)
 */
public class Auction implements Serializable {

  @Override
  public String toString() {
    return "Auction [state=" + state + ", type=" + type + ", offer=" + offer
        + "]";
  }

  public void setType(Type type) {
    this.type = type;
  }

  public Auction(State state, Type type, int offer) {
    super();
    this.state = state;
    this.type = type;
    this.offer = offer;
  }

  public Type getType() {
    return type;
  }

  public State getState() {
    return state;
  }

  public int getOffer() {
    return offer;
  }

  public void setState(State state) {
    this.state = state;
  }

  public void setOffer(int offer) {
    this.offer = offer;
  }

  State state;
  Type type;
  int offer;

  public enum State {
    OFF, STARTED, NEGOTIATING, SOLD, NOT_SOLD
  }

  public enum Type {
    PROPOSAL, REJECT, ACCEPT
  }
}
