package info.gianlucacosta.valkyrie

private case class FrequencyTracker[T](item: T, frequency: Int, universeCount: Int) {
  val percentage: Double =
    frequency * 100.0 / universeCount
}