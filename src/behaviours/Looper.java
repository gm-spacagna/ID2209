package behaviours;

import jade.core.Agent;
import jade.core.behaviours.*;

public abstract class Looper extends SimpleBehaviour {
  static long t0 = System.currentTimeMillis();

  int n = 1;
  long dt;
  int max;

  public Looper(Agent a, long dt, int max) {
    super(a);
    this.dt = dt;
    this.max = max;
  }

  public void action() {
    block(dt);
    loopAction();
    n++;
  }

  protected abstract void loopAction();

  public boolean done() {
    return n > max;
  }
}
