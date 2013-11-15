package hw1.agents;

import java.util.logging.Logger;

import hw1.service.ProvidedService;
import hw1.service.ServiceProvidersManager;
import jade.core.Agent;

/**
 * An agent which provides a particular service. During the setup, register the
 * provided service to the @code{ServiceProviderAgent}.
 * 
 * @author Gianmario Spacagna (gspacagn@cisco.com)
 */
public abstract class ServiceProviderAgent extends Agent {

  @Override
  protected void setup() {
    super.setup();
    registerService();
  }

  protected void registerService() {
    ServiceProvidersManager.registerService(getAID(), getService());
  }

  protected abstract ProvidedService getService();
}
