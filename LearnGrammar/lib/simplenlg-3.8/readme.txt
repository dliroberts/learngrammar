README file for simplenlg 3.8

CONTENTS

The simplenlg distribution contains

* readme.txt  :  this file
* simplenlg-source-3.8.zip : source files
* simplenlg-api-3.8.zip : API documentation
* simplenlg-3.8.jar  : compiled library
* simplenlg-3.8.pdf  : main documentation file
* simplenlg-tutorial-3.8.pdf : tutorial for simplenlg

You can also download simplenlg-lexicon.zip (3.5MB); this
contains the simplenlg version of the NIH Specialist lexicon,
as a Mysql backup/dump file.

USAGE

simplenlg requires Java 5.

To use simplenlg without changing it, simply add the
jar file as a library in your Java project.  If you are using
the Protege interface, you will also need to download protege.jar
(from the Protege website) and add it as a library as well.
If you want to use the lexicon DB, you will need to download
and unzip specialist-lexicon, and load it into MySQL

To modify simplenlg, just unzip the sources and set these up as a
project in your Java IDE.  If you are not using the Protege
interface, you can delete simplenlg.kb.ProtegeKB and
simplenlg.kb.ProtegeClass; this will eliminate error/warning
messages about unknown Protege-related classes.  You should
add a JUnit jar as a library, this is used for testing


VERSION NOTES - V3.8 *********************************

ClauseAggregator added, this is an experimental aggregation
module.  Also added AdvPhraseSpec and supports more types
of doc structures (sections, indented lists)

VERSION NOTES - V3.7 *********************************

DBLexicon added, so simplenlg can access the NIH Specialist
lexicon

VERSION NOTES - V3.6 *********************************

Support for questions added, also many bug fixes


VERSION NOTES - V3.5 *********************************

CHANGES TO EXISTING CODE

SyntaxPhraseSpec has been replaced by HeadedPhraseSpec

Modifiers - all types of HeaderPhraseSpec can have pre and
post modifiers (these replace the head and end modifiers
of v3.4).  SPhraseSpec (only) can also have a front modifier
(unchanged from v3.4).

ENHANCEMENTS
1) CoordinateNPPhraseSpec has been generalised, there is not
   a COordinatePhraseSpec interface with specific coordinate
   classes for each phrase type
2) AdjPhraseSpec has been added
3) PhraseFactory provides factory methods for creating phrases
4) Many enhancements to individual classes; for example
   SPhraseSpec can have indirect objects, NPPhraseSpec can
   be possessive.  See API for details.
5) Javadoc for API is much more comprehensive
6) Testing is better structured, using JUnit

VERSION NOTES - V3.4 **************************************

CHANGES TO EXISTING CODE

We have made a few changes that require modifications to code
that uses earlier simplenlg versions.  Apologies for this, but
some things needed to be rationalised...

The main change is that we have reorganised the package structure
of simplenlg.  It now contains 4 subpackages:
simplenlg.realiser   - Realiser, SPhraseSpec, etc
simplenlg.lexicon    - Lexicon, etc
simplenlg.kb         - NLGKB, Lexicaliser, etc
simplenlg.features   - Tense, Form, etc

This means that you will need to change the import statements
in existing simplenlg code; many Java IDEs contain tools to
automate this.

Also note that features are now in separate classes. For example,
(assuming you have imported simplenlg.features.*) you should refer to
Tense.PAST instead of SPhraseSpec.Tense.PAST

[Below para updated for V3.5]
We have also rationalised modifiers.  Simplenlg now recognises
three kinds of modifiers
* front modifiers - these go at the beginning of a phrase
* pre modifiers - these go just before the head word of a phrase
* post modifiers - these go at the end of a phrase

You can directly specify where a modifier goes using addHeadModifier (etc).
You can also just call a generic  setModifier  method, and let
simplenlg decide where to put your modifier.

ENHANCEMENTS/CHANGES

1) We have added a tutorial for beginners.  This is very much
   work in progress, feedback is appreciated
2) SPhraseSpec's now support subjunctive mood, perfect aspect, and
   modals (such as "must").
3) CoordinateNPPhraseSpec allows NPs to be conjoined with arbitrary
   conjuncts; this in particular allows "or" lists.
4) An empty SPhraseSpec is now realised as an empty string.
5) Modifiers (see above) give better support for adverbs