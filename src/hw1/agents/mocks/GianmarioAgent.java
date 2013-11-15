package hw1.agents.mocks;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;

/**
 * Gianmario's Agent. Simple example class.
 * 
 * @author Gianmario Spacagna (gspacagn@cisco.com)
 */
public class GianmarioAgent extends Agent {
  
  public static void main(String[] args) {
    System.out.println("Hello");
  }

  /**
   * 
   */
  private static final long serialVersionUID = 9101089205025155960L;

  @Override
  protected void setup() {
    super.setup();
    addBehaviour(new GianmarioBehaviour(this));
  }

  class GianmarioBehaviour extends SimpleBehaviour {

    /**
     * 
     */
    private static final long serialVersionUID = -205710971451777349L;

    public GianmarioBehaviour(Agent agent) {
      super(agent);
    }

    private boolean finished = false;

    @Override
    public void action() {
      if (!finished) {
        System.out.println("Hello World! My name is " + myAgent.getLocalName());
        finished = true;
      }
    }

    @Override
    public boolean done() {
      return finished;
    }
  }
}
