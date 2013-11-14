package property;

import java.util.HashMap;
import java.util.Map;

import jade.core.AID;

/**
 * A manager of all offered service by each agent.
 * 
 * @author Gianmario Spacagna (gspacagn@cisco.com)
 */
public class OfferedServiceManager {

  private static final Map<AID, OfferedServiceProperty> map = new HashMap<AID, OfferedServiceProperty>();

  public static OfferedServiceProperty getProperty(AID aid) {
    return map.get(aid);
  }

  public static OfferedServiceProperty putProperty(AID aid,
      OfferedServiceProperty property) {
    return map.put(aid, property);
  }
}
