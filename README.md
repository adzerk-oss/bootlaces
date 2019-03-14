# bootlaces

![](https://clojars.org/adzerk/bootlaces/latest-version.svg)

Handy tasks and things for the [boot Clojure build tool][1].

* Provides `build-jar`, `push-snapshot`, and `push-release` tasks
* Parses a `gpg.edn` file to configure GPG keyring and key ID for jar signing.

> This is an example build and deployment workflow. It showcases the generality
> of boot. We actually use this at Adzerk, but you should [fork] and tailor it
> for your own organization.

## Usage

Add `bootlaces` to your `build.boot` dependencies and `require` the namespace:

```clj
(set-env! :dependencies '[[adzerk/bootlaces "X.Y.Z" :scope "test"]])
(require '[adzerk.bootlaces :refer :all])
```

Then initialize bootlaces with the project version:

```clj
(def +version+ "0.0-2371-5")
(bootlaces! +version+)
```

#### Some things you can do in the terminal:

```bash
# build and install project jar file
boot build-jar
```

```bash
# set environment variables
export CLOJARS_USER=foo
export CLOJARS_PASS=bar
```

```bash
# deploy snapshot to clojars
boot build-jar push-snapshot
```

```bash
# deploy release to clojars
boot build-jar push-release
```

#### Signing

The `gpg.edn` file format:

```clojure
{:keyring "/path/to/secring.gpg"
 :user-id "Micha Niskin <micha.niskin@gmail.com>"}
```

`gpg.edn` can be global, sourced fom your home directory, or local to your project. Local `gpg.edn` takes precedence over global one.

## License

Copyright © 2014 Micha Niskin and Alan Dipert

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

[1]: https://github.com/boot-clj/boot
[2]: http://clojars.org/adzerk/bootlaces/latest-version.svg?cache=2
[3]: http://clojars.org/adzerk/bootlaces
[fork]: https://github.com/adzerk/bootlaces/fork
