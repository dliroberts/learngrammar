README file for simplenlg v4.1

CONTENTS

This zip file contains the following directories
dist - jar file.  Please note you will also need to include the jars
       in the lib directory
doc  - API documentation
lib  - library jars needed by simplenlg
src  - source

It also contains the following top-level files
README.txt  - this file
lexicon-setup.txt  - instructions for setting up a lexicon
lgpl-license.txt, gpl-license.txt - License terms (lesser GPL)

USAGE

simplenlg requires Java 6.

To use simplenlg without changing it, simply add the
jar file as a library in your Java project.  You will also
need to add some of the jars in the lib directory, depending
on what kind of lexicon  you use (probably easiest to just
add all of them)

To modify simplenlg, just unzip the sources and set these up as a
project in your Java IDE.

GENERAL NOTE

This is work-in-progress.  Please report any bugs to
e.reiter@abdn.ac.uk.  The tutorial is not yet updated, but
the API information should be accurate.  To see examples,
please see src/simplenlg/test. Most of these are JUnit tests,
but  StandAloneExample.java shows a stand-alone example.
Note that in order to run simplenlg V4, you must create
a lexicon, nlgfactory, and realiser, as in StandAloneExample.java
   Note that some of the simplenlg V3 functionality is not yet
present in simplenlg V4, including the KB stuff and the
morphg-based morphology system.


VERSION NOTES - Simplenlg V4.1 *************************

* Many bugs fixed
* API made more similar to simplenlg V3 (eg, SPhraseSpec is back)
* aggregation added back in

You will need to make changes to V4.0 code to use V4.1.  In particular
PhraseFactory and DocumentFactory have been replaced by a single
NLGFactory .  Also features which API users do not normally set have been
moved from Feature to InternalFeature or LexicalFeature

Documentation is still poor, this is our next priority
