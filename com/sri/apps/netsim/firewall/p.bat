java JLex.Main firewall.jlex
del parser\*.java
java java_cup.Main -symbols Symbols -parser Parser firewall.cup
move firewall.jlex.java parser\Scanner.java
move Symbols.java parser
move Parser.java parser
