(set-env!
  :resource-paths #{"src"}
  :repositories   #(conj % ["deploy" {:url      "https://clojars.org/repo"
                                      :username (System/getenv "CLOJARS_USER")
                                      :password (System/getenv "CLOJARS_PASS")}])
  :dependencies '[[org.clojure/clojure "1.6.0"      :scope "provided"]
                  [boot/core           "2.0.0-pre5" :scope "provided"]])

(require
  '[clojure.java.io :as io]
  '[boot.git :refer [last-commit]])

(def +version+ "0.1.6-SNAPSHOT")

(task-options!
  push {:repo           "deploy"
        :ensure-branch  "master"
        :ensure-clean   true
        :ensure-tag     (last-commit)
        :ensure-version +version+}
  pom  {:project        'adzerk/bootlaces
        :version        +version+
        :description    "Micha's assorted boot configurations and things."
        :url            "https://github.com/adzerk/bootlaces"
        :scm            {:url "https://github.com/adzerk/bootlaces"}
        :license        {:name "Eclipse Public License"
                         :url  "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build-jar
  "Build jar and install to local repo."
  []
  (comp (pom) (jar) (install)))

(deftask push-snapshot
  "Deploy snapshot version to Clojars."
  [f file PATH str "The jar file to deploy."]
  (push
    :file            file
    :ensure-snapshot true))

(deftask push-release
  "Deploy release version to Clojars."
  [f file PATH str "The jar file to deploy."]
  (push
    :file           file
    :tag            true
    :gpg-sign       true
    :ensure-release true))
