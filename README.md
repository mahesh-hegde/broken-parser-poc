PoC for a fault-tolerant parser for jnigen.

Requires Java 17.

```bash
mvn dependency:copy -Dartifact="org.apache.pdfbox:pdfbox:2.0.27" -DoutputDirectory=".jar/"
mvn -q exec:java '-Dexec.mainClass=com.github.maheshhegde.brokenparserpoc.Main'
```

Results:

| CHECK                                                    | QDox       | Ecj        | JavaParser | 
|----------------------------------------------------------|------------|------------|------------|
| Well formed source class import                          | true       | true       | true       | 
| Well formed source class import using wildcard           | true       | true       | true       | 
| Type from bad source used in impl                        | true       | true       | true       | 
| Type from bad source used in signature                   | true       | true       | true       | 
| Type from JAR library in classpath                       | true       | true       | true       | 
| Signature of method using unknown types in implementation | true       | true       | true       | 
| Unknown library type imported qualified                  | true       | false      | false      | 
| Unknown library type, wildcard import                    | false      | false      | false      | 
