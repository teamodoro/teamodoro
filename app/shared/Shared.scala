package shared

/**
 * Created by nsa, 20/03/15 
 */

import models.Greenhouse

object Shared {

  var greenhouse: Greenhouse = Greenhouse.withName("test")

  def replaceGreenhouse(newGreenhouse: Greenhouse): Greenhouse = greenhouse.synchronized {
    greenhouse = newGreenhouse
    greenhouse
  }
}
