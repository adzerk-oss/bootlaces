# boot-useful

[![Clojars Project][2]][3]

Handy tasks and things for the [boot Clojure build tool][1].

* Provides `build-jar`, `push-snapshot`, and `push-release` tasks
* Parses a `gpg.edn` file to configure GPG keyring and key ID for jar signing.

```
# build and install project jar file
$ boot build-jar
```

```
# set environment variables
$ export CLOJARS_USER=foo
$ export CLOJARS_PASS=bar
```

```
# deploy snapshot to clojars
$ boot build-jar push-snapshot
```

```
# deploy release to clojars
$ boot build-jar push-release
```

## Usage

Add `boot-useful` to your `build.boot` dependencies and `require` the namespace:

```clj
(set-env! :dependencies '[[tailrecursion/boot-useful "0.1.3" :scope "test"]])
(require '[tailrecursion.boot-useful :refer :all])
```

Then initialize useful with the project version:

```clj
(def +version+ "0.0-2371-5")
(useful! +version+)
```

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[1]: https://github.com/tailrecursion/boot
[2]: http://clojars.org/tailrecursion/boot-useful/latest-version.svg
[3]: http://clojars.org/tailrecursion/boot-useful
