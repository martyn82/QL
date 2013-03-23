Software Construction Assignment
================================

Write a parser for the QL language, using a Java parser generator. The provided skeleton code (see below) includes
three starting points based on Jacc, Rats! and ANTLR. You're expected to choose one, and be able to motivate your
choice.

It's your job to complete one of the provided grammars and AST hierarchies.
The parser should produce an abstract syntax tree (AST); this is a tree of objects representing the source program. The
AST is described by a class hierarchy. (NB: this class hierarchy is required! You could use this requirement in your
selection of parser generator).

A well-formedness/consistency checker implemented on top of your AST classes. You may restrict the supported data types
by QL to integers, boolean, and strings. See Variant 1 for details.

An interpreter for QL: this component should render a questionnaire as an interactive form (e.g., using Swing, JavaFX,
or HTML). Note: an interpreter runs a questionnaire directly, there's no code generation involved.

Skeleton code
-------------
We've provided two projects which you can use to get started. This code is incomplete. You should make it complete by
adding the following features:
- Syntax for booleans, string literals. Don't forget to take care of keyword reservation: true and false should be
  parsed as boolean literals, not as identifiers.
- Add single-line comments (a la Java: //).
- Add syntax productions for forms, questions, computed quetsions, types (int, bool, and string) and if-then and
  if-then-else statements. Use string literals for question labels. See the LWC'13 link above for an example
  questionnaire.
- Add tests to check your syntax extensions.
- Add AST classes for the provided expression categories, and for you syntactic extensions. Make sure the parser
  creates objects of the appropriate type.
- Change the start symbol of the parser to parse forms, instead of Expressions.

Note: don't be seduced by the provided example code and start copy-pasting grammar rules around. It is important to
      really understand the parser technology involved. ANTLR, Rats! and Jacc are well-documented on the web. Please
      use this information to fulfill the above requirements.
