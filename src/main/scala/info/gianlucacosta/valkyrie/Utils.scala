package info.gianlucacosta.valkyrie

import info.gianlucacosta.balmung.lexicon.{Kasus, Noun, Numerus}

private object Utils {
  def getEnding(endingLength: Int, word: String): String = {
    word.substring(
      word.length - endingLength
    )
  }

  def hasLongEnoughSingularNominative(noun: Noun, minimumLength: Int): Boolean = {
    val singularNominativeOption =
      noun.getDeclension
        .getExpression(Numerus.SINGULAR, Kasus.NOMINATIV)

    singularNominativeOption.isPresent &&
      singularNominativeOption.get().length >= minimumLength
  }

  def hasSingleWordSingularNominative(noun: Noun): Boolean = {
    val singularNominative =
      noun.getDeclension.getExpression(Numerus.SINGULAR, Kasus.NOMINATIV)

    singularNominative.isPresent && singularNominative.get().matches("^[A-Za-zßÄäÖöÜü]+$")
  }
}
