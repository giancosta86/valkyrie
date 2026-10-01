package info.gianlucacosta.valkyrie

import java.nio.file.{Files, Path, Paths}
import java.util.Optional
import java.util.stream.Collectors

import info.gianlucacosta.balmung.lexicon.Noun
import info.gianlucacosta.odin.parsing.wiktionary.WiktionaryParser
import info.gianlucacosta.odin.storage.Lexicon
import info.gianlucacosta.odin.storage.hibernate.{HibernateLexicon, LocalDatabase}


object App {
  def main(args: Array[String]): Unit = {
    val dbPath =
      Paths.get("output/db")

    val localDb =
      new LocalDatabase(Optional.of(dbPath))

    val shouldLoadExternalLexicon =
      args.length > 0

    println("== Opening the internal DB (might take some time) ==")
    println()

    val sessionFactory =
      localDb.createSessionFactory()

    try {
      val lexicon =
        new HibernateLexicon(sessionFactory)

      if (shouldLoadExternalLexicon) {
        val lexiconSourcePathString =
          args(0)

        loadLexiconFromSource(
          Paths.get(lexiconSourcePathString),
          lexicon
        )
      }

      analyzeNouns(lexicon)
    } finally {
      sessionFactory.close()
    }
  }

  private def loadLexiconFromSource(lexiconSourcePath: Path, lexicon: Lexicon): Unit = {
    println("== NOW PARSING WIKTIONARY ==")
    println()


    val sourceStream =
      Files.newInputStream(lexiconSourcePath)

    try {
      val wiktionaryParser =
        new WiktionaryParser(
          sourceStream,
          lexicon
        )

      val parserResult =
        wiktionaryParser.parse()


      println(s"Skipped lemmas: ${parserResult.getSkippedLemmasCount}")
      println(s"Unsaved lemmas: ${parserResult.getUnsavedLemmasCount}")
      println(s"Saved lemmas: ${parserResult.getSavedLemmasCount}")
      println()
      println(s"Total lemmas: ${parserResult.getTotalLemmasCount}")
    } finally {
      sourceStream.close()
    }
  }


  private def analyzeNouns(lexicon: Lexicon): Unit = {
    println()
    println("** LOADING NOUNS **")
    println()


    val nounsStream =
      lexicon.findNouns()

    try {
      val javaNouns: java.util.List[Noun] =
        nounsStream
          .collect(Collectors.toList())

      val nouns: Iterable[Noun] =
        collection.JavaConverters.collectionAsScalaIterable(
          javaNouns
        )


      if (nouns.nonEmpty) {
        println(s"Loaded nouns: ${nouns.size}")

        println()
        println()


        val outputDirPath = Paths.get("output/stats")
        Files.createDirectories(outputDirPath)


        GenusAnalysis.perform(
          outputDirPath,
          nouns
        )


        PluralAnalysis.perform(
          outputDirPath,
          nouns
        )
      } else {
        println(" --> No nouns found! Please run the program again, passing the path of a lexicon source file")
      }

    } finally {
      nounsStream.close()
    }
  }
}
