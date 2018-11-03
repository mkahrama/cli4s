# cli4s
CLI utilities for Scala

## Status
Build status on Travis CI:

[![Build Status](https://travis-ci.org/efekahraman/cli4s.svg?branch=master)](https://travis-ci.org/efekahraman/cli4s)


## Dependency

```scala
libraryDependencies += "cli4s" % "cli4s" % "1.0.0"
```

## Components

### GetOpt
POSIX compliant get-opt utility to parse command-line options. For more information see [GNU getopt page](https://www.gnu.org/software/libc/manual/html_node/Argument-Syntax.html).

#### Sample Usage

```scala
import cli4s.getopt._
// ...
def parser(token: Token): Unit = token match {
       case LongOption("long-option") => // Process option... Usable values: token.index, token.value
       case ShortOption('v') => // Process option... Usable values: token.index, token.value
       case ShortOption('s') => // Process option... Usable values: token.index
       case Argument("arg1") => // Process argument... Usable values: token.index
       case _ => println("unknown option")
     }

    val optList = List(TokenItem(ShortOption('s'), false), // Takes no value
                       TokenItem(ShortOption('v'), true, Option(0)), // Takes value, and must be first option if present
                       TokenItem(LongOption("long-option"), true)) // Takes value

    new GetOpt("-v test -s --long-option=test", optList).iterate(parser)
```
