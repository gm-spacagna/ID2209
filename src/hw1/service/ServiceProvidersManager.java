package hw1.service;

import hw1.agents.ServiceProviderAgent;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import jade.core.AID;

/**
 * A manager of all offered service by each agent.
 * 
 * @author Gianmario Spacagna (gspacagn@cisco.com)
 */
public class ServiceProvidersManager {
  private final static Logger LOGGER = Logger.getLogger(ServiceProvidersManager.class.getName());

  private static final Map<AID, ProvidedService> map = new HashMap<AID, ProvidedService>();

  public static ProvidedService getProperty(AID aid) {
    return map.get(aid);
  }

  public static ProvidedService registerService(AID aid, ProvidedService property) {
    //LOGGER.info("Registering service: " + property + " to aid: " + aid);
    return map.put(aid, property);
  }
}
