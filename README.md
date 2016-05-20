All backported fixes for 1.0.x are applied here.

### Releasing
```sh
mvn versions:set -DnewVersion=1.0.x && \
git clean -f && git add -u && git commit -m "1.0.x" && \
mvn -Prelease deploy && mvn scm:tag
```

If that succeeds:
```sh
mvn versions:set -DnewVersion=1.0.x-SNAPSHOT && \
git clean -f && git add -u && git commit -m "1.0.x-SNAPSHOT"
```
