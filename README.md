All backported fixes for 1.0.x are applied here.

### Releasing
```
mvn versions:set -DnewVersion=1.0.x
mvn -Prelease deploy
mvn scm:tag
mvn versions:set -DnewVersion=1.0.x-SNAPSHOT
```
