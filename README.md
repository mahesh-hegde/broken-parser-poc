PoC for a fault-tolerant parser for jnigen.

Requires Java 17.

```bash
mvn dependency:copy -Dartifact="org.apache.pdfbox:pdfbox:2.0.27" -DoutputDirectory=".jar/"
mvn -q exec:java '-Dexec.mainClass=com.github.maheshhegde.brokenparserpoc.Main'
```
