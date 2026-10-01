package info.gianlucacosta.valkyrie

private case class GroupingFrequencyCounter[T](items: Iterable[T]) {
  private val universeCount =
    items.size

  val frequencies: List[FrequencyTracker[T]] =
    items
      .groupBy(item => item)
      .map {
        case (item, occurrences) =>
          FrequencyTracker(
            item,
            occurrences.size,
            universeCount
          )
      }
      .toList
      .sortBy(_.frequency)
}
